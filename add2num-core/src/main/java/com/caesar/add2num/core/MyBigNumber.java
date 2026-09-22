package com.caesar.add2num.core;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adds two arbitrarily large non-negative integers that are represented as decimal strings,
 * using the pen-and-paper column addition taught in primary school: walk both numbers from the
 * least significant digit to the most significant one, add the digit pair plus the incoming carry,
 * write down the last digit of that partial sum and carry the rest over to the next column.
 *
 * <p>The size of the operands is limited only by the JVM heap and by the maximum length of a
 * {@link String}; no {@code long}, {@code double}, {@link java.math.BigInteger} or
 * {@link java.math.BigDecimal} is used anywhere in the computation.
 *
 * <h2>Complexity</h2>
 * Let {@code n} be the length of the longer operand. The algorithm performs a single pass and runs
 * in {@code O(n)} time and {@code O(n)} additional space (one {@code char[]} of {@code n + 1} cells,
 * which is the minimum needed to hold the result).
 *
 * <h2>Contract</h2>
 * <ul>
 *   <li>Operands must contain ASCII digits {@code '0'..'9'} only. No sign, no separator,
 *       no whitespace, no Unicode digits from other scripts.</li>
 *   <li>An empty string is accepted and behaves as {@code "0"}, so that {@code sum("", "")}
 *       is {@code "0"} rather than an empty result.</li>
 *   <li>Leading zeros are accepted on input and are removed from the output, so the result is
 *       always the canonical decimal representation: {@code sum("007", "3")} is {@code "10"}.</li>
 *   <li>{@code null} operands and non-digit characters raise {@link IllegalArgumentException}.</li>
 * </ul>
 *
 * <p>The specification for this exercise states that the caller guarantees valid input, so
 * validation would have been optional. It is performed anyway because it costs one extra linear
 * scan, turns a silent wrong answer into an immediate and precise error, and a library that is
 * reused by other modules cannot rely on every future caller honouring an unchecked contract.
 *
 * <h2>Thread safety</h2>
 * This class holds no mutable state; a single instance may be shared by any number of threads.
 *
 * <h2>Logging</h2>
 * Diagnostics go through SLF4J. Log messages contain only sizes and arithmetic metadata, never the
 * operand values.
 *
 * @see SumResult
 * @see SumStep
 */
public final class MyBigNumber {

    /** Default upper bound on the number of steps kept by {@link #sumWithTrace(String, String)}. */
    public static final int DEFAULT_MAX_TRACE_STEPS = 1_000;

    private static final Logger logger = LoggerFactory.getLogger(MyBigNumber.class);

    private static final char ZERO = '0';
    private static final char NINE = '9';

    /** Creates a stateless calculator. */
    public MyBigNumber() {
        // Intentionally empty: the class carries no state.
    }

    /**
     * Adds two non-negative integers given as decimal strings.
     *
     * <p>This is the method required by the specification. It does not record the intermediate
     * steps; use {@link #sumWithTrace(String, String)} when the caller needs to display them.
     *
     * @param stn1 the first operand, digits only, may be empty
     * @param stn2 the second operand, digits only, may be empty
     * @return the sum in canonical decimal form, without leading zeros
     * @throws IllegalArgumentException if either operand is {@code null} or contains a non-digit
     */
    public String sum(String stn1, String stn2) {
        requireDigitsOnly(stn1, "stn1");
        requireDigitsOnly(stn2, "stn2");

        String result = add(stn1, stn2, null, 0);

        logger.debug("added {} and {} digit operands into a {} digit result",
                stn1.length(), stn2.length(), result.length());
        return result;
    }

    /**
     * Adds two non-negative integers and records how each column was computed, so that a user
     * interface can replay the calculation the way it would be written out by hand.
     *
     * @param stn1 the first operand, digits only, may be empty
     * @param stn2 the second operand, digits only, may be empty
     * @return the sum together with the recorded steps
     * @throws IllegalArgumentException if either operand is {@code null} or contains a non-digit
     */
    public SumResult sumWithTrace(String stn1, String stn2) {
        return sumWithTrace(stn1, stn2, DEFAULT_MAX_TRACE_STEPS);
    }

    /**
     * Adds two non-negative integers and records at most {@code maxTraceSteps} steps.
     *
     * <p>The cap exists because operands may hold millions of digits, and materialising one step
     * object per digit would cost far more memory than the result itself. When the cap is reached
     * the remaining columns are still computed, they are simply not recorded, and
     * {@link SumResult#truncated()} reports that the trace is partial.
     *
     * @param stn1          the first operand, digits only, may be empty
     * @param stn2          the second operand, digits only, may be empty
     * @param maxTraceSteps the maximum number of steps to keep, zero meaning "compute but record nothing"
     * @return the sum together with the recorded steps
     * @throws IllegalArgumentException if either operand is invalid or {@code maxTraceSteps} is negative
     */
    public SumResult sumWithTrace(String stn1, String stn2, int maxTraceSteps) {
        requireDigitsOnly(stn1, "stn1");
        requireDigitsOnly(stn2, "stn2");
        if (maxTraceSteps < 0) {
            throw new IllegalArgumentException("maxTraceSteps must not be negative, but was " + maxTraceSteps);
        }

        // Pre-size the list: the column count is known in advance, so the list never has to grow.
        int columns = Math.max(stn1.length(), stn2.length()) + 1;
        List<SumStep> steps = new ArrayList<>(Math.min(maxTraceSteps, columns));

        long startedAt = System.nanoTime();
        String value = add(stn1, stn2, steps, maxTraceSteps);
        long elapsedNanos = System.nanoTime() - startedAt;

        int totalColumns = countColumns(stn1, stn2, value);
        SumResult result = new SumResult(value, steps, totalColumns, steps.size() < totalColumns, elapsedNanos);

        logger.debug("sumWithTrace produced {} of {} step(s) in {} us",
                result.steps().size(), result.totalSteps(), elapsedNanos / 1_000);
        return result;
    }

    // ---------------------------------------------------------------------------------------
    // Internals
    // ---------------------------------------------------------------------------------------

    /**
     * The single-pass column addition. Both operands have already been validated when this runs.
     *
     * @param trace         where to record steps, or {@code null} to record nothing
     * @param maxTraceSteps ignored when {@code trace} is {@code null}
     */
    private String add(String stn1, String stn2, List<SumStep> trace, int maxTraceSteps) {
        final int len1 = stn1.length();
        final int len2 = stn2.length();

        // One extra cell for the carry that may fall out of the most significant column
        // (for example 999 + 1 needs four cells for a three digit operand).
        final int capacity = Math.max(len1, len2) + 1;
        final char[] buffer = new char[capacity];

        int left = len1 - 1;           // read cursor into stn1, right to left
        int right = len2 - 1;          // read cursor into stn2, right to left
        int write = capacity - 1;      // write cursor into buffer, right to left
        int carry = 0;
        int column = 0;                // 0 = units, 1 = tens, ...
        int leftDigit;
        int rightDigit;
        int total;
        int carryOut;
        int digit;

        while (left >= 0 || right >= 0) {
            // A missing digit on the shorter operand behaves as 0, which is exactly what happens
            // on paper when the two numbers are right aligned.
            leftDigit = (left >= 0) ? stn1.charAt(left) - ZERO : 0;
            rightDigit = (right >= 0) ? stn2.charAt(right) - ZERO : 0;

            total = leftDigit + rightDigit + carry;

            // total is at most 9 + 9 + 1 = 19, so the carry is 0 or 1 and the digit is total or
            // total - 10. Using a comparison instead of / and % keeps the hot loop free of
            // integer division, which is the most expensive operation that would appear here.
            carryOut = (total > 9) ? 1 : 0;
            digit = total - (carryOut * 10);

            buffer[write--] = (char) (ZERO + digit);

            if (trace != null && trace.size() < maxTraceSteps) {
                trace.add(new SumStep(column, leftDigit, rightDigit, carry, total, digit, carryOut));
            }
            logger.trace("column {}: {} + {} + {} = {} -> write {}, carry {}",
                    column, leftDigit, rightDigit, carry, total, digit, carryOut);

            carry = carryOut;
            left--;
            right--;
            column++;
        }

        if (carry > 0) {
            buffer[write--] = (char) (ZERO + carry);
            if (trace != null && trace.size() < maxTraceSteps) {
                trace.add(new SumStep(column, 0, 0, carry, carry, carry, 0));
            }
            logger.trace("column {}: final carry {} written out", column, carry);
        }

        return canonicalise(buffer, write + 1);
    }

    /**
     * Builds the result string from the filled tail of the buffer, dropping leading zeros so that
     * the output is canonical, while keeping a single {@code '0'} for a zero result.
     *
     * @param from index of the most significant digit written into {@code buffer}
     */
    private static String canonicalise(char[] buffer, int from) {
        int end = buffer.length;
        if (from >= end) {
            // Reached only when both operands were empty, so nothing was ever written.
            return "0";
        }
        int start = from;
        while (start < end - 1 && buffer[start] == ZERO) {
            start++;
        }
        return new String(buffer, start, end - start);
    }

    /**
     * Rejects {@code null} and any character outside {@code '0'..'9'} in one linear scan.
     *
     * <p>Kept separate from {@link #add} on purpose: the addition loop stays small enough for the
     * JIT to inline and unroll, and the error message can name the exact offending index.
     */
    private static void requireDigitsOnly(String value, String parameterName) {
        if (value == null) {
            throw new IllegalArgumentException(parameterName + " must not be null");
        }
        int n = value.length();
        int i = 0;
        char c;
        for (; i < n; i++) {
            c = value.charAt(i);
            if (c < ZERO || c > NINE) {
                throw new IllegalArgumentException(parameterName + " must contain decimal digits only, but found '"
                        + c + "' (U+" + String.format("%04X", (int) c) + ") at index " + i);
            }
        }
    }

    /**
     * Number of columns the calculation actually went through, which is the length of the longer
     * operand plus one when a final carry produced an extra digit.
     */
    private static int countColumns(String stn1, String stn2, String value) {
        int widest = Math.max(stn1.length(), stn2.length());
        if (widest == 0) {
            return 0;
        }
        return (value.length() > widest) ? widest + 1 : widest;
    }

}
