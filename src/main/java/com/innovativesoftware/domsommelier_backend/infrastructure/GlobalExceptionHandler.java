package com.innovativesoftware.domsommelier_backend.infrastructure;

import com.innovativesoftware.domsommelier_backend.exceptions.ApiErrorResponse;
import com.innovativesoftware.domsommelier_backend.exceptions.InvalidIdException;
import com.innovativesoftware.domsommelier_backend.exceptions.InvalidValueException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiErrorResponse> handleNoSuchElementException(RuntimeException ex) {
        ApiErrorResponse response = new ApiErrorResponse(
                "Ошибка: Элемент не найден",
                String.valueOf(HttpStatus.NOT_FOUND.value()),
                "Элемент не найден",
                null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(" "));

        ApiErrorResponse response = new ApiErrorResponse(
                "Ошибка валидации данных",
                "VALIDATION_ERROR",
                "Некорректные параметры запроса",
                details
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    /*
    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        String details = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        ApiErrorResponse response = new ApiErrorResponse(
                "Ошибка значения",
                "INVALID_EVENT_TYPE",
                "Ошибка валидации типа мероприятия",
                details
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }*/

}
