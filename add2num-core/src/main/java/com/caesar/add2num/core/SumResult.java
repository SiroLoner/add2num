package com.caesar.add2num.core;

import java.time.Duration;
import java.util.List;

/**
 * Outcome of {@link MyBigNumber#sumWithTrace(String, String, int)}: the sum itself plus the record
 * of how it was reached.
 *
 * <p>{@link #steps()} may hold fewer entries than {@link #totalSteps()} when the caller capped the
 * trace; {@link #truncated()} says whether that happened, so a user interface can show an honest
 * "showing the first N of M columns" note instead of pretending the list is complete.
 *
 * @param steps        the recorded columns, ordered from the units column upwards; never {@code null}
 * @param value        the sum in canonical decimal form
 * @param totalSteps   the number of columns the calculation actually went through
 * @param truncated    {@code true} when {@code steps} holds fewer entries than {@code totalSteps}
 * @param elapsedNanos wall clock time spent inside the addition, in nanoseconds
 */
public record SumResult(
        String value,
        List<SumStep> steps,
        int totalSteps,
        boolean truncated,
        long elapsedNanos) {

    /** Defensively copies the step list so that an instance handed to another layer cannot be mutated. */
    public SumResult {
        if (value == null) {
            throw new IllegalArgumentException("value must not be null");
        }
        if (steps == null) {
            throw new IllegalArgumentException("steps must not be null");
        }
        if (totalSteps < 0) {
            throw new IllegalArgumentException("totalSteps must not be negative, but was " + totalSteps);
        }
        if (elapsedNanos < 0) {
            throw new IllegalArgumentException("elapsedNanos must not be negative, but was " + elapsedNanos);
        }
        steps = List.copyOf(steps);
    }

    /** Number of digits in the result. */
    public int digitCount() {
        return value.length();
    }

    /** Time spent in the addition, as a {@link Duration}. */
    public Duration elapsed() {
        return Duration.ofNanos(elapsedNanos);
    }

    /** Elapsed time in milliseconds, rounded to three decimals, convenient for display. */
    public double elapsedMillis() {
        return Math.round(elapsedNanos / 1_000.0) / 1_000.0;
    }

    /** Number of columns that produced a carry, a small but telling statistic for the UI. */
    public long carryCount() {
        return steps.stream().filter(step -> step.carryOut() == 1).count();
    }
}
