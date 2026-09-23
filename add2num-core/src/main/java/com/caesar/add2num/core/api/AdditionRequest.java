package com.caesar.add2num.core.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/** Request body for the core addition REST API. */
@Schema(description = "Two non-negative decimal integers to add.")
public record AdditionRequest(
        @Schema(description = "First operand. Digits only; surrounding whitespace is accepted.",
                example = "999999999999999999999")
        @NotBlank(message = "firstNumber must not be blank")
        @Pattern(regexp = "^\\s*[0-9]+\\s*$", message = "firstNumber must contain decimal digits only")
        String firstNumber,

        @Schema(description = "Second operand. Digits only; surrounding whitespace is accepted.",
                example = "1")
        @NotBlank(message = "secondNumber must not be blank")
        @Pattern(regexp = "^\\s*[0-9]+\\s*$", message = "secondNumber must contain decimal digits only")
        String secondNumber) {
}