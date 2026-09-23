package com.example.ai_native_core;

import java.math.BigDecimal;

public class ScratchHandler {

    public BigDecimal sum(String left, String right) {
        if (left == null || right == null || left.isBlank() || right.isBlank()) {
            throw new IllegalArgumentException("Input must not be blank");
        }

        try {
            return new BigDecimal(left.trim()).add(new BigDecimal(right.trim()));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Input values must be valid decimal numbers", ex);
        }
    }
}
