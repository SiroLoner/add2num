package com.caesar.add2num.web.view;

import com.caesar.add2num.core.SumResult;

import java.util.List;

/**
 * Everything the result page needs, assembled once in the controller so that the template contains
 * presentation only and no logic worth testing.
 */
public class CalculationView {

    private final String first;
    private final String second;
    private final String value;
    private final List<StepView> steps;
    private final int totalSteps;
    private final boolean truncated;
    private final long carryCount;
    private final double elapsedMillis;
    private final ColumnLayout columnLayout;

    public CalculationView(String first, String second, SumResult result, int maxColumnLayoutWidth) {
        this.first = first;
        this.second = second;
        this.value = result.value();
        this.steps = result.steps().stream().map(StepView::new).toList();
        this.totalSteps = result.totalSteps();
        this.truncated = result.truncated();
        this.carryCount = result.carryCount();
        this.elapsedMillis = result.elapsedMillis();
        this.columnLayout = ColumnLayout.of(first, second, result, maxColumnLayoutWidth);
    }

    public String getFirst() {
        return first;
    }

    public String getSecond() {
        return second;
    }

    public String getValue() {
        return value;
    }

    public List<StepView> getSteps() {
        return steps;
    }

    /** Number of columns the calculation went through, which may exceed the number shown. */
    public int getTotalSteps() {
        return totalSteps;
    }

    /** Number of columns actually sent to the browser. */
    public int getShownSteps() {
        return steps.size();
    }

    /** {@code true} when the walkthrough shows only the first part of the calculation. */
    public boolean isTruncated() {
        return truncated;
    }

    public long getCarryCount() {
        return carryCount;
    }

    public double getElapsedMillis() {
        return elapsedMillis;
    }

    public int getDigitCount() {
        return value.length();
    }

    public int getFirstDigitCount() {
        return first.length();
    }

    public int getSecondDigitCount() {
        return second.length();
    }

    public ColumnLayout getColumnLayout() {
        return columnLayout;
    }

    /** {@code true} when the calculation is small enough to be drawn as a paper layout. */
    public boolean isColumnLayoutAvailable() {
        return columnLayout != null;
    }

    /** {@code true} when the result is long enough that the page should let it wrap and scroll. */
    public boolean isLongResult() {
        return value.length() > 120;
    }
}
