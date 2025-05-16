package com.innovativesoftware.domsommelier_backend.exceptions;

import lombok.Getter;

@Getter
public class InvalidValueException extends RuntimeException {
    private final String errorCode;
    private final String details;

    public InvalidValueException(String message, String errorCode, String details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details;
    }
}
