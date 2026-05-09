package com.example.paymentservice.idempotency.web;

import static com.example.paymentservice.idempotency.IdempotencyConstants.IDEMPOTENCY_REQUEST_ATTRIBUTE;

import com.example.paymentservice.idempotency.service.IdempotencyProcessingContext;
import com.example.paymentservice.idempotency.service.IdempotencyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class IdempotencyResponseAdvice implements ResponseBodyAdvice<Object> {

    private final IdempotencyService idempotencyService;
    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response
    ) {
        if (!(request instanceof ServletServerHttpRequest servletRequest)
                || !(response instanceof ServletServerHttpResponse servletResponse)) {
            return body;
        }

        Object attribute = servletRequest.getServletRequest().getAttribute(IDEMPOTENCY_REQUEST_ATTRIBUTE);
        if (!(attribute instanceof IdempotencyProcessingContext context)) {
            return body;
        }

        try {
            String responseBody = objectMapper.writeValueAsString(body);
            idempotencyService.saveResponse(
                    context,
                    servletResponse.getServletResponse().getStatus(),
                    selectedContentType != null ? selectedContentType.toString() : MediaType.APPLICATION_JSON_VALUE,
                    responseBody
            );
            servletRequest.getServletRequest().removeAttribute(IDEMPOTENCY_REQUEST_ATTRIBUTE);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to serialize idempotent response", exception);
        }

        return body;
    }
}
