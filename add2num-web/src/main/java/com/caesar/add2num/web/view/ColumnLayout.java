package com.caesar.add2num.web.view;

import com.caesar.add2num.core.SumResult;
import com.caesar.add2num.core.SumStep;

import java.util.Arrays;

/**
 * The calculation drawn the way it would be written on paper: the carries on top, the two operands
 * right aligned underneath, and the result below the rule.
 *
 * <pre>
 *   111
 *   1234
 * +  897
 * ------
 *   2131
 * </pre>
 *
 * <p>Only built for calculations narrow enough to stay readable; see
 * {@link #of(String, String, SumResult, int)}.
 */
public class ColumnLayout {

    private final String carryRow;
    private final String firstRow;
    private final String secondRow;
    private final String sumRow;
    private final int width;

    private ColumnLayout(String carryRow, String firstRow, String secondRow, String sumRow, int width) {
        this.carryRow = carryRow;
        this.firstRow = firstRow;
        this.secondRow = secondRow;
        this.sumRow = sumRow;
        this.width = width;
    }

    /**
     * Builds the paper layout, or returns {@code null} when it would not be useful.
     *
     * <p>It is skipped when the trace is partial, because the carry row would then be wrong for the
     * columns that were never recorded, and when the calculation is wider than {@code maxWidth},
     * because a row of several hundred digits is a wall of text rather than an explanation. Showing
     * nothing is the honest outcome in both cases.
     *
     * @return the layout, or {@code null} when it should not be drawn
     */
    public static ColumnLayout of(String first, String second, SumResult result, int maxWidth) {
        if (result.truncated()) {
            return null;
        }
        int width = Math.max(result.value().length(), Math.max(first.length(), second.length()));
        if (width > maxWidth) {
            return null;
        }

        char[] carries = new char[width];
        Arrays.fill(carries, ' ');
        SumStep step;
        int i = 0;
        for (; i < result.steps().size(); i++) {
            step = result.steps().get(i);
            if (step.carryOut() == 1) {
                // A carry out of column k is written above column k + 1, one place further left.
                int index = width - 2 - step.position();
                if (index >= 0) {
                    carries[index] = '1';
                }
            }
        }

        return new ColumnLayout(new String(carries), padLeft(first, width), padLeft(second, width),
                padLeft(result.value(), width), width);
    }

    private static String padLeft(String value, int width) {
        int padding = width - value.length();
        return (padding <= 0) ? value : " ".repeat(padding) + value;
    }

    public String getCarryRow() {
        return carryRow;
    }

    public String getFirstRow() {
        return firstRow;
    }

    public String getSecondRow() {
        return secondRow;
    }

    public String getSumRow() {
        return sumRow;
    }

    public int getWidth() {
        return width;
    }

    /** The horizontal rule drawn between the operands and the result. */
    public String getRule() {
        return "-".repeat(width);
    }
}
