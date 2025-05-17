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

    @ExceptionHandler({InvalidIdException.class, InvalidValueException.class})
    public ResponseEntity<ApiErrorResponse> handleInvalidException(RuntimeException ex) {
        ApiErrorResponse response = new ApiErrorResponse();
        if (ex instanceof InvalidIdException invalidIdException) {
            response = new ApiErrorResponse(
                    "Ошибка идентификатора",
                    invalidIdException.getErrorCode(),
                    invalidIdException.getMessage(),
                    invalidIdException.getDetails());
        } else if (ex instanceof InvalidValueException invalidValueException) {
            response = new ApiErrorResponse(
                    "Ошибка значения",
                    invalidValueException.getErrorCode(),
                    invalidValueException.getMessage(),
                    invalidValueException.getDetails());
        }
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
