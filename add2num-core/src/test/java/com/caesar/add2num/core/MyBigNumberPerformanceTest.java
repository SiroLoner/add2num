package com.caesar.add2num.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Guards the scale the library claims to handle.
 *
 * <p>Tagged {@code slow} and therefore skipped by a plain {@code mvn test}; the {@code full} profile
 * used by CI runs it. The time budget is deliberately generous, because a unit test asserting a
 * wall clock deadline is only useful for catching an accidental change of complexity class, such as
 * building the result with string concatenation inside the loop, and must not turn into a flaky
 * test on a loaded build agent.
 */
@Tag("slow")
@DisplayName("MyBigNumber at scale")
class MyBigNumberPerformanceTest {

    private static final int ONE_MILLION = 1_000_000;

    private final MyBigNumber calculator = new MyBigNumber();

    @Test
    @DisplayName("adds two one million digit numbers correctly and well inside the budget")
    void addsAMillionDigitsQuickly() {
        Random random = new Random(4_242L);
        String left = TestNumbers.digits(ONE_MILLION, random);
        String right = TestNumbers.digits(ONE_MILLION, random);

        long startedAt = System.nanoTime();
        String actual = calculator.sum(left, right);
        long elapsedMillis = (System.nanoTime() - startedAt) / 1_000_000;

        assertThat(actual).isEqualTo(new BigInteger(left).add(new BigInteger(right)).toString());
        assertThat(elapsedMillis)
                .withFailMessage("adding two %d digit numbers took %d ms, which suggests the "
                        + "single pass linear algorithm has been broken", ONE_MILLION, elapsedMillis)
                .isLessThan(5_000L);
    }

    @Test
    @DisplayName("runtime grows linearly, not quadratically, with the number of digits")
    void scalesLinearly() {
        Random random = new Random(7_777L);

        long smallNanos = timeAddition(TestNumbers.digits(200_000, random), TestNumbers.digits(200_000, random));
        long largeNanos = timeAddition(TestNumbers.digits(800_000, random), TestNumbers.digits(800_000, random));

        // Four times the input. Linear behaviour costs roughly four times as much; a quadratic
        // implementation would cost sixteen times as much. A ceiling of ten leaves ample room for
        // JIT warm up and garbage collection while still failing loudly on a quadratic regression.
        double ratio = (double) largeNanos / Math.max(smallNanos, 1L);

        assertThat(ratio)
                .withFailMessage("quadrupling the input multiplied the runtime by %.1f, "
                        + "which is not consistent with a linear algorithm", ratio)
                .isLessThan(10.0);
    }

    @Test
    @DisplayName("tracing a huge addition stays bounded in memory")
    void boundsTheTraceForHugeInputs() {
        String left = "9".repeat(500_000);
        String right = "9".repeat(500_000);

        SumResult result = calculator.sumWithTrace(left, right, 100);

        assertThat(result.steps()).hasSize(100);
        assertThat(result.truncated()).isTrue();
        assertThat(result.totalSteps()).isEqualTo(500_001);
        assertThat(result.value()).hasSize(500_001);
    }

    private long timeAddition(String left, String right) {
        // One warm up pass so the measurement is not dominated by interpretation of cold bytecode.
        calculator.sum(left, right);

        long startedAt = System.nanoTime();
        calculator.sum(left, right);
        return System.nanoTime() - startedAt;
    }
}
