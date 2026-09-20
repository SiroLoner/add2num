package com.caesar.add2num.web.api;

import com.caesar.add2num.core.SumResult;
import com.caesar.add2num.core.SumStep;

import java.util.List;

/**
 * Response body of {@code POST /api/v1/sum}.
 *
 * @param value        the sum, in canonical decimal form
 * @param digitCount   number of digits in {@code value}
 * @param totalSteps   number of columns the calculation went through
 * @param returnedSteps number of columns included in {@code steps}
 * @param truncated    {@code true} when {@code steps} is only the beginning of the calculation
 * @param carryCount   number of returned columns that produced a carry
 * @param elapsedMillis time spent inside the addition itself, excluding HTTP and serialisation
 * @param steps        the recorded columns, from the units column upwards
 */
public record SumResponse(
        String value,
        int digitCount,
        int totalSteps,
        int returnedSteps,
        boolean truncated,
        long carryCount,
        double elapsedMillis,
        List<Step> steps) {

    /** One column, flattened for the wire. */
    public record Step(
            int position,
            String place,
            int leftDigit,
            int rightDigit,
            int carryIn,
            int total,
            int digit,
            int carryOut,
            String description) {

        static Step from(SumStep step) {
            return new Step(step.position(), step.placeValueLabel(), step.leftDigit(), step.rightDigit(),
                    step.carryIn(), step.total(), step.digit(), step.carryOut(), step.describe());
        }
    }

    static SumResponse from(SumResult result) {
        return new SumResponse(
                result.value(),
                result.digitCount(),
                result.totalSteps(),
                result.steps().size(),
                result.truncated(),
                result.carryCount(),
                result.elapsedMillis(),
                result.steps().stream().map(Step::from).toList());
    }
}
