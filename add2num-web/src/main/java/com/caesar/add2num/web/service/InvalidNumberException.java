package com.caesar.add2num.web.service;

import java.io.Serial;

/**
 * Raised when a request carries an operand the calculator will not accept: empty, too long, or
 * containing something other than a decimal digit.
 *
 * <p>This is a client mistake rather than a server fault, so it is translated into HTTP 400 rather
 * than being allowed to surface as a stack trace.
 */
public class InvalidNumberException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String field;

    public InvalidNumberException(String field, String message) {
        super(message);
        this.field = field;
    }

    public InvalidNumberException(String field, String message, Throwable cause) {
        super(message, cause);
        this.field = field;
    }

    /** Name of the operand that was rejected, so the user interface can point at the right box. */
    public String getField() {
        return field;
    }
}
