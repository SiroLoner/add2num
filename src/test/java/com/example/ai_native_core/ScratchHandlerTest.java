package com.example.ai_native_core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class ScratchHandlerTest {

    private final ScratchHandler handler = new ScratchHandler();

    @Test
    void sum_adds_two_decimal_values() {
        assertEquals(new BigDecimal("42.75"), handler.sum("12.25", "30.50"));
    }

    @Test
    void sum_rejects_blank_or_invalid_input() {
        assertThrows(IllegalArgumentException.class, () -> handler.sum(null, "1"));
        assertThrows(IllegalArgumentException.class, () -> handler.sum("1", "abc"));
        assertThrows(IllegalArgumentException.class, () -> handler.sum("", "2"));
    }
}
