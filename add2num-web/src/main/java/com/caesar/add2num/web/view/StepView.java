package com.caesar.add2num.web.view;

import com.caesar.add2num.core.SumStep;

/**
 * One column of the calculation, shaped for display.
 *
 * <p>Written as a class with conventional getters rather than as a record on purpose: instances of
 * this type are read both by Thymeleaf expressions and by Jackson, and plain getters are understood
 * by every version of both. The records in the core library stay records; they never reach a
 * template directly.
 */
public class StepView {

    private final int position;
    private final String place;
    private final int leftDigit;
    private final int rightDigit;
    private final int carryIn;
    private final int total;
    private final int digit;
    private final int carryOut;
    private final String description;

    public StepView(SumStep step) {
        this.position = step.position();
        this.place = step.placeValueLabel();
        this.leftDigit = step.leftDigit();
        this.rightDigit = step.rightDigit();
        this.carryIn = step.carryIn();
        this.total = step.total();
        this.digit = step.digit();
        this.carryOut = step.carryOut();
        this.description = step.describe();
    }

    /** One based column number, which reads better than a zero based index in a table. */
    public int getColumn() {
        return position + 1;
    }

    public int getPosition() {
        return position;
    }

    public String getPlace() {
        return place;
    }

    public int getLeftDigit() {
        return leftDigit;
    }

    public int getRightDigit() {
        return rightDigit;
    }

    public int getCarryIn() {
        return carryIn;
    }

    public int getTotal() {
        return total;
    }

    public int getDigit() {
        return digit;
    }

    public int getCarryOut() {
        return carryOut;
    }

    public String getDescription() {
        return description;
    }

    /** {@code true} when this column produced a carry, used to highlight the row. */
    public boolean isCarrying() {
        return carryOut == 1;
    }
}
