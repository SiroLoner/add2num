package com.caesar.add2num.web.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Request body of {@code POST /api/v1/sum}.
 *
 * @param first    first operand, decimal digits only
 * @param second   second operand, decimal digits only
 * @param maxSteps how many columns of the walkthrough to return; omitted means the server default,
 *                 and a value larger than the server limit is clamped down to it
 */
public record SumRequest(

        @NotBlank(message = "first must not be blank")
        @Pattern(regexp = "^\\s*[0-9]+\\s*$", message = "first must contain decimal digits only")
        String first,

        @NotBlank(message = "second must not be blank")
        @Pattern(regexp = "^\\s*[0-9]+\\s*$", message = "second must contain decimal digits only")
        String second,

        Integer maxSteps) {
}
