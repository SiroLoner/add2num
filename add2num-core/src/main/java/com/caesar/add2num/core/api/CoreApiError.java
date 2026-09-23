package com.caesar.add2num.core.api;

import java.time.Instant;
import java.util.List;

/** Stable validation error shape returned by the core REST API. */
public record CoreApiError(Instant timestamp, int status, String error, String message,
                           List<FieldMessage> fields) {

    public record FieldMessage(String field, String message) {
    }
}