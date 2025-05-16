package com.innovativesoftware.domsommelier_backend.infrastructure;

import com.innovativesoftware.domsommelier_backend.exceptions.ApiErrorResponse;
import com.innovativesoftware.domsommelier_backend.exceptions.InvalidIdException;
import com.innovativesoftware.domsommelier_backend.exceptions.InvalidValueException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex) {
        return new ResponseEntity<>("Ошибка: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidIdException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidId(InvalidIdException ex) {
        ApiErrorResponse response = new ApiErrorResponse(
                "Ошибка идентификатора",
                ex.getErrorCode(),
                ex.getMessage(),
                ex.getDetails()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidValueException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidValue(InvalidValueException ex) {
        ApiErrorResponse response = new ApiErrorResponse(
                "Ошибка значения",
                ex.getErrorCode(),
                ex.getMessage(),
                ex.getDetails()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
