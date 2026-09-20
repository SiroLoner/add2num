package com.caesar.add2num.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Behaviour of the traced variant, which is what feeds the "progress of the calculation" panel
 * required by TASK 2.
 */
@DisplayName("MyBigNumber.sumWithTrace")
class MyBigNumberTraceTest {

    private final MyBigNumber calculator = new MyBigNumber();

    @Nested
    @DisplayName("recorded steps")
    class RecordedSteps {

        @Test
        @DisplayName("records one step per column, from the units column upwards")
        void recordsEveryColumn() {
            SumResult result = calculator.sumWithTrace("1234", "897");

            assertThat(result.value()).isEqualTo("2131");
            assertThat(result.totalSteps()).isEqualTo(4);
            assertThat(result.truncated()).isFalse();
            assertThat(result.steps()).extracting(SumStep::describe).containsExactly(
                    "4 + 7 + 0 = 11 -> write 1, carry 1",
                    "3 + 9 + 1 = 13 -> write 3, carry 1",
                    "2 + 8 + 1 = 11 -> write 1, carry 1",
                    "1 + 0 + 1 = 2 -> write 2, carry 0");
        }

        @Test
        @DisplayName("positions are consecutive and start at the units column")
        void numbersTheColumns() {
            SumResult result = calculator.sumWithTrace("555", "444");

            assertThat(result.steps()).extracting(SumStep::position).containsExactly(0, 1, 2);
            assertThat(result.steps().get(0).placeValueLabel()).isEqualTo("units");
            assertThat(result.steps().get(1).placeValueLabel()).isEqualTo("tens");
            assertThat(result.steps().get(2).placeValueLabel()).isEqualTo("hundreds");
        }

        @Test
        @DisplayName("the final carry is recorded as its own column")
        void recordsTheFinalCarry() {
            SumResult result = calculator.sumWithTrace("999", "1");

            assertThat(result.value()).isEqualTo("1000");
            assertThat(result.totalSteps()).isEqualTo(4);
            assertThat(result.steps()).hasSize(4);
            assertThat(result.steps().get(3).describe()).isEqualTo("0 + 0 + 1 = 1 -> write 1, carry 0");
            assertThat(result.steps().get(3).placeValueLabel()).isEqualTo("10^3");
        }

        @Test
        @DisplayName("the digits written across the steps, read backwards, rebuild the result")
        void stepsRebuildTheResult() {
            SumResult result = calculator.sumWithTrace("86420", "13579");

            StringBuilder rebuilt = new StringBuilder();
            for (SumStep step : result.steps()) {
                rebuilt.insert(0, step.digit());
            }

            assertThat(rebuilt.toString()).isEqualTo(result.value());
        }

        @Test
        @DisplayName("the carry of one column is the incoming carry of the next")
        void carriesChainCorrectly() {
            List<SumStep> steps = calculator.sumWithTrace("987654321", "123456789").steps();

            for (int i = 1; i < steps.size(); i++) {
                assertThat(steps.get(i).carryIn())
                        .withFailMessage("carry into column %d should come from column %d", i, i - 1)
                        .isEqualTo(steps.get(i - 1).carryOut());
            }
            assertThat(steps.get(0).carryIn()).isZero();
        }

        @Test
        @DisplayName("counts the columns that produced a carry")
        void countsCarries() {
            assertThat(calculator.sumWithTrace("1234", "897").carryCount()).isEqualTo(3);
            assertThat(calculator.sumWithTrace("11", "11").carryCount()).isZero();
        }
    }

    @Nested
    @DisplayName("bounded traces")
    class BoundedTraces {

        @Test
        @DisplayName("keeps at most the requested number of steps and says the trace is partial")
        void capsTheNumberOfSteps() {
            SumResult result = calculator.sumWithTrace("123456789", "987654321", 3);

            assertThat(result.value()).isEqualTo("1111111110");
            assertThat(result.steps()).hasSize(3);
            assertThat(result.totalSteps()).isEqualTo(10);
            assertThat(result.truncated()).isTrue();
        }

        @Test
        @DisplayName("a cap of zero still computes the sum but records nothing")
        void supportsAZeroCap() {
            SumResult result = calculator.sumWithTrace("12", "34", 0);

            assertThat(result.value()).isEqualTo("46");
            assertThat(result.steps()).isEmpty();
            assertThat(result.truncated()).isTrue();
        }

        @Test
        @DisplayName("a cap larger than the column count leaves the trace complete")
        void doesNotMarkShortTracesAsTruncated() {
            SumResult result = calculator.sumWithTrace("12", "34", 1_000);

            assertThat(result.steps()).hasSize(2);
            assertThat(result.truncated()).isFalse();
        }

        @Test
        @DisplayName("the default cap protects against operands with millions of digits")
        void appliesADefaultCap() {
            String huge = "1".repeat(MyBigNumber.DEFAULT_MAX_TRACE_STEPS * 3);

            SumResult result = calculator.sumWithTrace(huge, huge);

            assertThat(result.steps()).hasSize(MyBigNumber.DEFAULT_MAX_TRACE_STEPS);
            assertThat(result.totalSteps()).isEqualTo(huge.length());
            assertThat(result.truncated()).isTrue();
        }

        @Test
        @DisplayName("a negative cap is rejected")
        void rejectsANegativeCap() {
            assertThatThrownBy(() -> calculator.sumWithTrace("1", "2", -1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("maxTraceSteps");
        }
    }

    @Nested
    @DisplayName("edge cases")
    class EdgeCases {

        @Test
        @DisplayName("two empty operands produce zero and no steps at all")
        void handlesEmptyOperands() {
            SumResult result = calculator.sumWithTrace("", "");

            assertThat(result.value()).isEqualTo("0");
            assertThat(result.steps()).isEmpty();
            assertThat(result.totalSteps()).isZero();
            assertThat(result.truncated()).isFalse();
        }

        @Test
        @DisplayName("leading zeros are visible as columns but not in the result")
        void keepsColumnsForLeadingZeros() {
            SumResult result = calculator.sumWithTrace("007", "3");

            assertThat(result.value()).isEqualTo("10");
            assertThat(result.totalSteps()).isEqualTo(3);
            assertThat(result.steps()).hasSize(3);
        }

        @Test
        @DisplayName("invalid operands are rejected before any step is recorded")
        void validatesBeforeTracing() {
            assertThatThrownBy(() -> calculator.sumWithTrace("1x", "2"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("stn1");
        }
    }

    @Nested
    @DisplayName("result metadata")
    class ResultMetadata {

        @Test
        @DisplayName("reports the width of the result and a non negative duration")
        void reportsMetadata() {
            SumResult result = calculator.sumWithTrace("999", "1");

            assertThat(result.digitCount()).isEqualTo(4);
            assertThat(result.elapsedNanos()).isNotNegative();
            assertThat(result.elapsed().toNanos()).isEqualTo(result.elapsedNanos());
            assertThat(result.elapsedMillis()).isNotNegative();
        }

        @Test
        @DisplayName("the step list handed to callers cannot be modified")
        void exposesAnImmutableStepList() {
            SumResult result = calculator.sumWithTrace("1", "1");

            assertThatThrownBy(() -> result.steps().add(new SumStep(9, 1, 1, 0, 2, 2, 0)))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }
}
