package com.example.paymentservice.idempotency.service;

import com.example.paymentservice.idempotency.model.IdempotencyRecord;
import com.example.paymentservice.idempotency.model.IdempotencyStatus;
import com.example.paymentservice.idempotency.repository.IdempotencyRecordRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final IdempotencyRecordRepository idempotencyRecordRepository;

    public Optional<IdempotencyRecord> findByKey(String key) {
        return idempotencyRecordRepository.findById(key);
    }

    @Transactional
    public IdempotencyReservationResult reserve(String key, String httpMethod, String requestPath, String requestHash) {
        IdempotencyRecord record = IdempotencyRecord.builder()
                .idempotencyKey(key)
                .httpMethod(httpMethod)
                .requestPath(requestPath)
                .requestHash(requestHash)
                .status(IdempotencyStatus.PROCESSING)
                .createdAt(LocalDateTime.now())
                .build();
        try {
            return new IdempotencyReservationResult(idempotencyRecordRepository.saveAndFlush(record), true);
        } catch (DataIntegrityViolationException exception) {
            IdempotencyRecord existingRecord = idempotencyRecordRepository.findById(key)
                    .orElseThrow(() -> exception);
            return new IdempotencyReservationResult(existingRecord, false);
        }
    }

    public String calculateRequestHash(String method, String path, String body) {
        String normalizedBody = body == null ? "" : body.trim();
        String payload = method + "|" + path + "|" + normalizedBody;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 algorithm is not available", exception);
        }
    }

    public void validateSameRequest(IdempotencyRecord record, String requestHash) {
        if (!record.getRequestHash().equals(requestHash)) {
            throw new IdempotencyConflictException(
                    "Idempotency-Key was already used with a different request body"
            );
        }
    }

    public void validateSameEndpoint(IdempotencyRecord record, String method, String path) {
        if (!record.getHttpMethod().equals(method) || !record.getRequestPath().equals(path)) {
            throw new IdempotencyConflictException(
                    "Idempotency-Key was already used for a different endpoint"
            );
        }
    }

    public void ensureProcessingFinished(IdempotencyRecord record) {
        if (record.getStatus() == IdempotencyStatus.PROCESSING) {
            throw new IdempotencyProcessingException(
                    "Request with this Idempotency-Key is already being processed"
            );
        }
    }

    @Transactional
    public void saveResponse(
            IdempotencyProcessingContext context,
            int responseStatus,
            String responseContentType,
            String responseBody
    ) {
        IdempotencyRecord record = idempotencyRecordRepository.findById(context.getKey())
                .orElseThrow(() -> new IllegalStateException("Idempotency record not found for key " + context.getKey()));
        record.setStatus(IdempotencyStatus.COMPLETED);
        record.setResponseStatus(responseStatus);
        record.setResponseContentType(responseContentType == null ? MediaType.APPLICATION_JSON_VALUE : responseContentType);
        record.setResponseBody(responseBody);
        idempotencyRecordRepository.save(record);
    }

    @Transactional
    public void releaseProcessingRecord(String key) {
        idempotencyRecordRepository.findById(key).ifPresent(record -> {
            if (record.getStatus() == IdempotencyStatus.PROCESSING) {
                idempotencyRecordRepository.delete(record);
            }
        });
    }
}
