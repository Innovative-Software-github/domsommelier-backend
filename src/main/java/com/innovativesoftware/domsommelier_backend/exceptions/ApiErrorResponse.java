package com.innovativesoftware.domsommelier_backend.exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ApiErrorResponse {
    private String error;
    private String errorCode;
    private String message;
    private String details;
    private LocalDateTime timestamp;

    public ApiErrorResponse(String error, String errorCode, String message, String details) {
        this.error = error;
        this.errorCode = errorCode;
        this.message = message;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }
}
