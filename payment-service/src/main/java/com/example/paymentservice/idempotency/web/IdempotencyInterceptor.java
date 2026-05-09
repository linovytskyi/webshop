package com.example.paymentservice.idempotency.web;

import static com.example.paymentservice.idempotency.IdempotencyConstants.IDEMPOTENCY_KEY_HEADER;
import static com.example.paymentservice.idempotency.IdempotencyConstants.IDEMPOTENCY_REQUEST_ATTRIBUTE;

import com.example.paymentservice.idempotency.model.IdempotencyRecord;
import com.example.paymentservice.idempotency.service.IdempotencyReservationResult;
import com.example.paymentservice.idempotency.service.MissingIdempotencyKeyException;
import com.example.paymentservice.idempotency.service.IdempotencyProcessingContext;
import com.example.paymentservice.idempotency.service.IdempotencyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class IdempotencyInterceptor implements HandlerInterceptor {

    private final IdempotencyService idempotencyService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if (!HttpMethod.POST.matches(request.getMethod()) || !"/api/payments".equals(request.getRequestURI())) {
            return true;
        }

        String idempotencyKey = request.getHeader(IDEMPOTENCY_KEY_HEADER);
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new MissingIdempotencyKeyException("Idempotency-Key header is required for POST /api/payments");
        }

        String requestBody = extractRequestBody(request);
        String requestHash = idempotencyService.calculateRequestHash(
                request.getMethod(),
                request.getRequestURI(),
                requestBody
        );

        IdempotencyReservationResult reservation = idempotencyService.reserve(
                idempotencyKey,
                request.getMethod(),
                request.getRequestURI(),
                requestHash
        );
        IdempotencyRecord record = reservation.getRecord();
        idempotencyService.validateSameEndpoint(record, request.getMethod(), request.getRequestURI());
        idempotencyService.validateSameRequest(record, requestHash);

        if (record.getResponseBody() != null) {
            replayCachedResponse(record, response);
            return false;
        }

        if (!reservation.isReservedByCurrentRequest()) {
            idempotencyService.ensureProcessingFinished(record);
            replayCachedResponse(record, response);
            return false;
        }

        request.setAttribute(
                IDEMPOTENCY_REQUEST_ATTRIBUTE,
                new IdempotencyProcessingContext(
                        idempotencyKey,
                        requestHash,
                        request.getMethod(),
                        request.getRequestURI()
                )
        );
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (ex == null) {
            return;
        }
        Object attribute = request.getAttribute(IDEMPOTENCY_REQUEST_ATTRIBUTE);
        if (attribute instanceof IdempotencyProcessingContext context) {
            idempotencyService.releaseProcessingRecord(context.getKey());
            request.removeAttribute(IDEMPOTENCY_REQUEST_ATTRIBUTE);
        }
    }

    private String extractRequestBody(HttpServletRequest request) {
        if (request instanceof CachedBodyHttpServletRequest wrapper) {
            return new String(wrapper.getCachedBody(), StandardCharsets.UTF_8);
        }
        return "";
    }

    private void replayCachedResponse(IdempotencyRecord record, HttpServletResponse response) throws IOException {
        response.setStatus(record.getResponseStatus());
        response.setContentType(record.getResponseContentType());
        response.getWriter().write(record.getResponseBody());
    }
}
