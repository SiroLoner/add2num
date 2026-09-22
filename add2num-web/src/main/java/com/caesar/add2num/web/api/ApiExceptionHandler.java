package com.caesar.add2num.web.api;

import com.caesar.add2num.web.service.InvalidNumberException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Turns failures of the JSON API into the single {@link ApiError} shape.
 *
 * <p>Scoped to {@link SumApiController} with {@code assignableTypes}. Without that scope this advice
 * would also intercept the Thymeleaf controller and answer a browser with JSON instead of the error
 * page.
 */
@RestControllerAdvice(assignableTypes = SumApiController.class)
public class ApiExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

    /** Bean validation on the request body: a field is missing or is not a plain number. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationFailure(MethodArgumentNotValidException e) {
        Map<String, String> fields = new LinkedHashMap<>();
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        FieldError fieldError;
        int i = 0;
        for (; i < fieldErrors.size(); i++) {
            fieldError = fieldErrors.get(i);
            fields.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ApiError body = ApiError.of(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "The request contains invalid operands.", fields);
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * The body was not readable as JSON. Without this handler the catch-all below would report a
     * malformed request as a server fault, which is both wrong and unhelpful to the caller.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadableBody(HttpMessageNotReadableException e) {
        logger.debug("rejected an unreadable request body: {}", e.getMessage());

        ApiError body = ApiError.of(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "The request body could not be read as JSON.");
        return ResponseEntity.badRequest().body(body);
    }

    /** The caller sent something other than {@code application/json}. */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiError> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException e) {
        ApiError body = ApiError.of(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                HttpStatus.UNSUPPORTED_MEDIA_TYPE.getReasonPhrase(),
                "This endpoint accepts application/json only.");
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(body);
    }

    /** A limit the annotations cannot express, such as the configured maximum operand length. */
    @ExceptionHandler(InvalidNumberException.class)
    public ResponseEntity<ApiError> handleInvalidNumber(InvalidNumberException e) {
        ApiError body = ApiError.of(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
                e.getMessage(), Map.of(e.getField(), e.getMessage()));
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Anything unforeseen. The detail goes to the log, where it belongs, and the caller gets a
     * stable message rather than a stack trace.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleUnexpectedFailure(IllegalStateException e) {
        logger.error("unexpected state while serving the sum API", e);

        ApiError body = ApiError.of(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "The calculation could not be completed.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
