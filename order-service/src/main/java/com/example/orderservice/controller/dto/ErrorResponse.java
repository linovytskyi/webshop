package com.example.orderservice.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Error response body")
public class ErrorResponse {

    @Schema(description = "Timestamp when the error occurred", example = "2026-04-19T19:20:00")
    private String timestamp;

    @Schema(description = "HTTP status code", example = "404")
    private int status;

    @Schema(description = "Human-readable error message", example = "Order with id 1 not found")
    private String error;
}
