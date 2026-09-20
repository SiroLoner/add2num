package com.caesar.add2num.web.view;

import com.caesar.add2num.core.MyBigNumber;
import com.caesar.add2num.core.SumResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ColumnLayout")
class ColumnLayoutTest {

    private final MyBigNumber calculator = new MyBigNumber();

    @Test
    @DisplayName("lays the calculation out the way it is written on paper")
    void laysOutTheCalculation() {
        SumResult result = calculator.sumWithTrace("1234", "897");

        ColumnLayout layout = ColumnLayout.of("1234", "897", result, 40);

        assertThat(layout).isNotNull();
        assertThat(layout.getWidth()).isEqualTo(4);
        assertThat(layout.getCarryRow()).isEqualTo("111 ");
        assertThat(layout.getFirstRow()).isEqualTo("1234");
        assertThat(layout.getSecondRow()).isEqualTo(" 897");
        assertThat(layout.getRule()).isEqualTo("----");
        assertThat(layout.getSumRow()).isEqualTo("2131");
    }

    @Test
    @DisplayName("widens every row when the result gains a digit")
    void widensForAnExtraDigit() {
        SumResult result = calculator.sumWithTrace("999", "1");

        ColumnLayout layout = ColumnLayout.of("999", "1", result, 40);

        assertThat(layout.getWidth()).isEqualTo(4);
        assertThat(layout.getFirstRow()).isEqualTo(" 999");
        assertThat(layout.getSecondRow()).isEqualTo("   1");
        assertThat(layout.getSumRow()).isEqualTo("1000");
        assertThat(layout.getCarryRow()).isEqualTo("111 ");
    }

    @Test
    @DisplayName("stays wide enough for operands that carry leading zeros")
    void accommodatesLeadingZeros() {
        SumResult result = calculator.sumWithTrace("0007", "3");

        ColumnLayout layout = ColumnLayout.of("0007", "3", result, 40);

        assertThat(layout.getWidth()).isEqualTo(4);
        assertThat(layout.getFirstRow()).isEqualTo("0007");
        assertThat(layout.getSecondRow()).isEqualTo("   3");
        assertThat(layout.getSumRow()).isEqualTo("  10");
    }

    @Test
    @DisplayName("leaves the carry row blank when nothing is carried")
    void leavesTheCarryRowBlank() {
        SumResult result = calculator.sumWithTrace("11", "11");

        ColumnLayout layout = ColumnLayout.of("11", "11", result, 40);

        assertThat(layout.getCarryRow()).isEqualTo("  ");
        assertThat(layout.getSumRow()).isEqualTo("22");
    }

    @Test
    @DisplayName("is not drawn when the calculation is wider than the configured limit")
    void skipsWideCalculations() {
        String left = "1".repeat(50);
        SumResult result = calculator.sumWithTrace(left, "1");

        assertThat(ColumnLayout.of(left, "1", result, 40)).isNull();
    }

    @Test
    @DisplayName("is not drawn when the walkthrough is only partial, because the carries would be wrong")
    void skipsTruncatedTraces() {
        SumResult result = calculator.sumWithTrace("1234", "897", 2);

        assertThat(ColumnLayout.of("1234", "897", result, 40)).isNull();
    }

    @Test
    @DisplayName("is drawn for a calculation exactly at the width limit")
    void drawsAtTheLimit() {
        String left = "1".repeat(40);
        SumResult result = calculator.sumWithTrace(left, "1");

        assertThat(ColumnLayout.of(left, "1", result, 40)).isNotNull();
    }
}
