package com.caesar.add2num.core.api;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Converts request validation failures into a stable API response. */
@RestControllerAdvice(assignableTypes = AdditionApiController.class)
public class CoreApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CoreApiError> handleValidation(MethodArgumentNotValidException exception) {
        Map<String, String> messages = new java.util.LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error -> messages.putIfAbsent(error.getField(), error.getDefaultMessage()));
        List<CoreApiError.FieldMessage> fields = messages.entrySet().stream()
                .map(entry -> new CoreApiError.FieldMessage(entry.getKey(), entry.getValue()))
                .toList();
        return ResponseEntity.badRequest().body(new CoreApiError(Instant.now(),
                HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "The request contains invalid operands.", fields));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CoreApiError> handleInvalidOperand(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(new CoreApiError(Instant.now(),
                HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
                exception.getMessage(), List.of()));
    }
}