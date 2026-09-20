package com.caesar.add2num.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("SumStep")
class SumStepTest {

    @Test
    @DisplayName("describes a column the way it would be read aloud")
    void describesTheColumn() {
        SumStep step = new SumStep(0, 4, 7, 0, 11, 1, 1);

        assertThat(step.describe()).isEqualTo("4 + 7 + 0 = 11 -> write 1, carry 1");
    }

    @ParameterizedTest(name = "[{index}] column {0} is labelled {1}")
    @CsvSource({"0, units", "1, tens", "2, hundreds", "3, 10^3", "12, 10^12"})
    void labelsThePlaceValue(int position, String expected) {
        SumStep step = new SumStep(position, 0, 0, 0, 0, 0, 0);

        assertThat(step.placeValueLabel()).isEqualTo(expected);
    }

    @Test
    @DisplayName("two steps describing the same column are equal")
    void hasValueSemantics() {
        assertThat(new SumStep(1, 2, 3, 0, 5, 5, 0))
                .isEqualTo(new SumStep(1, 2, 3, 0, 5, 5, 0))
                .hasSameHashCodeAs(new SumStep(1, 2, 3, 0, 5, 5, 0));
    }

    @Test
    @DisplayName("exposes its components")
    void exposesComponents() {
        SumStep step = new SumStep(2, 9, 8, 1, 18, 8, 1);

        assertThat(step.position()).isEqualTo(2);
        assertThat(step.leftDigit()).isEqualTo(9);
        assertThat(step.rightDigit()).isEqualTo(8);
        assertThat(step.carryIn()).isEqualTo(1);
        assertThat(step.total()).isEqualTo(18);
        assertThat(step.digit()).isEqualTo(8);
        assertThat(step.carryOut()).isEqualTo(1);
    }

    @Test
    @DisplayName("rejects a negative column index")
    void rejectsNegativePosition() {
        assertThatThrownBy(() -> new SumStep(-1, 0, 0, 0, 0, 0, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("position");
    }

    @ParameterizedTest(name = "[{index}] rejects digit {0}")
    @CsvSource({"10", "-1", "99"})
    void rejectsDigitsOutsideZeroToNine(int digit) {
        assertThatThrownBy(() -> new SumStep(0, digit, 0, 0, digit, digit, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("rejects a carry that is neither zero nor one")
    void rejectsImpossibleCarry() {
        assertThatThrownBy(() -> new SumStep(0, 1, 1, 2, 4, 4, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("carryIn");

        assertThatThrownBy(() -> new SumStep(0, 1, 1, 0, 2, 2, 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("carryOut");
    }

    @Test
    @DisplayName("rejects a total that does not match its operands")
    void rejectsInconsistentTotal() {
        assertThatThrownBy(() -> new SumStep(0, 1, 1, 0, 9, 9, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("total must equal leftDigit + rightDigit + carryIn");
    }

    @Test
    @DisplayName("rejects a digit and carry that do not add back up to the total")
    void rejectsInconsistentSplit() {
        assertThatThrownBy(() -> new SumStep(0, 9, 9, 1, 19, 8, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("carryOut * 10 + digit");
    }
}
