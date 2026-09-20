package com.caesar.add2num.web.api;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Error body returned by the JSON API.
 *
 * <p>One shape for every failure, so a client can parse errors without branching on the status code
 * first.
 *
 * @param timestamp when the failure was produced
 * @param status    the HTTP status code
 * @param error     the HTTP reason phrase
 * @param message   a single sentence a human can act on
 * @param fields    per field messages for a validation failure, empty otherwise
 */
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        List<FieldMessage> fields) {

    /**
     * @param field   name of the rejected field
     * @param message why it was rejected
     */
    public record FieldMessage(String field, String message) {
    }

    static ApiError of(int status, String error, String message) {
        return new ApiError(Instant.now(), status, error, message, List.of());
    }

    static ApiError of(int status, String error, String message, Map<String, String> fieldMessages) {
        List<FieldMessage> fields = fieldMessages.entrySet().stream()
                .map(entry -> new FieldMessage(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> a.field().compareTo(b.field()))
                .toList();
        return new ApiError(Instant.now(), status, error, message, fields);
    }
}
