package com.caesar.add2num.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("SumResult")
class SumResultTest {

    private static final SumStep CARRYING = new SumStep(0, 4, 7, 0, 11, 1, 1);
    private static final SumStep PLAIN = new SumStep(1, 1, 1, 1, 3, 3, 0);

    @Test
    @DisplayName("copies the step list so later changes by the caller cannot leak in")
    void defensivelyCopiesSteps() {
        List<SumStep> mutable = new ArrayList<>(List.of(CARRYING));

        SumResult result = new SumResult("11", mutable, 1, false, 0L);
        mutable.add(PLAIN);

        assertThat(result.steps()).containsExactly(CARRYING);
    }

    @Test
    @DisplayName("the exposed step list is immutable")
    void exposesAnImmutableList() {
        SumResult result = new SumResult("11", List.of(CARRYING), 1, false, 0L);

        assertThatThrownBy(() -> result.steps().add(PLAIN))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("reports the width of the result")
    void reportsDigitCount() {
        assertThat(new SumResult("1000", List.of(), 4, true, 0L).digitCount()).isEqualTo(4);
    }

    @Test
    @DisplayName("counts only the columns that carried")
    void countsCarries() {
        SumResult result = new SumResult("31", List.of(CARRYING, PLAIN), 2, false, 0L);

        assertThat(result.carryCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("converts the elapsed time to a Duration and to milliseconds")
    void convertsElapsedTime() {
        SumResult result = new SumResult("2", List.of(), 1, false, 2_500_000L);

        assertThat(result.elapsed()).isEqualTo(Duration.ofNanos(2_500_000L));
        assertThat(result.elapsedMillis()).isEqualTo(2.5);
    }

    @Test
    @DisplayName("rejects a null value")
    void rejectsNullValue() {
        assertThatThrownBy(() -> new SumResult(null, List.of(), 0, false, 0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("value");
    }

    @Test
    @DisplayName("rejects a null step list")
    void rejectsNullSteps() {
        assertThatThrownBy(() -> new SumResult("0", null, 0, false, 0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("steps");
    }

    @Test
    @DisplayName("rejects negative counters")
    void rejectsNegativeCounters() {
        assertThatThrownBy(() -> new SumResult("0", List.of(), -1, false, 0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("totalSteps");

        assertThatThrownBy(() -> new SumResult("0", List.of(), 0, false, -1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("elapsedNanos");
    }

    @Test
    @DisplayName("two results describing the same calculation are equal")
    void hasValueSemantics() {
        SumResult first = new SumResult("31", List.of(CARRYING), 1, false, 5L);
        SumResult second = new SumResult("31", List.of(CARRYING), 1, false, 5L);

        assertThat(first).isEqualTo(second).hasSameHashCodeAs(second);
    }
}
