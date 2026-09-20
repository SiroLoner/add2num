package com.caesar.add2num.core;

/**
 * One column of the pen-and-paper addition: the two digits that were added, the carry that came in,
 * the digit that was written down and the carry that went out.
 *
 * <p>For {@code 1234 + 897} the first step is {@code 4 + 7 + 0 = 11}, which writes {@code 1} and
 * carries {@code 1}.
 *
 * @param position   zero based column index counted from the right, so 0 is the units column
 * @param leftDigit  digit taken from the first operand, 0 when that operand has no digit here
 * @param rightDigit digit taken from the second operand, 0 when that operand has no digit here
 * @param carryIn    carry produced by the previous column, 0 or 1
 * @param total      {@code leftDigit + rightDigit + carryIn}, between 0 and 19
 * @param digit      digit written into the result for this column, {@code total % 10}
 * @param carryOut   carry handed to the next column, {@code total / 10}
 */
public record SumStep(
        int position,
        int leftDigit,
        int rightDigit,
        int carryIn,
        int total,
        int digit,
        int carryOut) {

    /**
     * Validates the arithmetic invariants of a step. These cannot be violated by the algorithm in
     * {@link MyBigNumber}; the checks exist so that a hand-built step used in a test or in another
     * consumer of this API cannot silently describe an impossible column.
     */
    public SumStep {
        if (position < 0) {
            throw new IllegalArgumentException("position must not be negative, but was " + position);
        }
        requireDigit(leftDigit, "leftDigit");
        requireDigit(rightDigit, "rightDigit");
        requireDigit(digit, "digit");
        requireCarry(carryIn, "carryIn");
        requireCarry(carryOut, "carryOut");
        if (total != leftDigit + rightDigit + carryIn) {
            throw new IllegalArgumentException("total must equal leftDigit + rightDigit + carryIn, but "
                    + total + " != " + leftDigit + " + " + rightDigit + " + " + carryIn);
        }
        if (total != carryOut * 10 + digit) {
            throw new IllegalArgumentException("total must equal carryOut * 10 + digit, but "
                    + total + " != " + carryOut + " * 10 + " + digit);
        }
    }

    /**
     * Human readable form of this column, for logs and for the user interface.
     *
     * @return for example {@code "4 + 7 + 0 = 11 -> write 1, carry 1"}
     */
    public String describe() {
        return leftDigit + " + " + rightDigit + " + " + carryIn + " = " + total
                + " -> write " + digit + ", carry " + carryOut;
    }

    /** The place value of this column, useful for labelling a UI: 1, 10, 100, ... as a power of ten. */
    public String placeValueLabel() {
        return switch (position) {
            case 0 -> "units";
            case 1 -> "tens";
            case 2 -> "hundreds";
            default -> "10^" + position;
        };
    }

    private static void requireDigit(int value, String name) {
        if (value < 0 || value > 9) {
            throw new IllegalArgumentException(name + " must be between 0 and 9, but was " + value);
        }
    }

    private static void requireCarry(int value, String name) {
        if (value < 0 || value > 1) {
            throw new IllegalArgumentException(name + " must be 0 or 1, but was " + value);
        }
    }
}
