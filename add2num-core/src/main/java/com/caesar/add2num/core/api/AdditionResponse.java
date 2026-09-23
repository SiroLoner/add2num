package com.caesar.add2num.core.api;

import io.swagger.v3.oas.annotations.media.Schema;

/** Response body for the core addition REST API. */
@Schema(description = "The operands and their canonical decimal sum.")
public record AdditionResponse(
        @Schema(description = "Canonical decimal result.", example = "1000000000000000000000")
        String result,
        @Schema(description = "First operand after trimming surrounding whitespace.", example = "999999999999999999999")
        String firstNumber,
        @Schema(description = "Second operand after trimming surrounding whitespace.", example = "1")
        String secondNumber) {
}