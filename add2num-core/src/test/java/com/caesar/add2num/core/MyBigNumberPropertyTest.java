package com.caesar.add2num.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigInteger;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Property based checks: instead of asserting individual results, these tests assert laws that must
 * hold for every input, and then exercise those laws over a large seeded sample.
 *
 * <p>Hand written examples prove that the obvious cases work. These tests are what catch the cases
 * nobody thought to write down, and they are the reason the suite can be trusted when the
 * implementation is changed later.
 */
@DisplayName("MyBigNumber algebraic properties")
class MyBigNumberPropertyTest {

    /** Fixed so that a failure is reproducible; change it only to widen the search. */
    private static final long SEED = 20_260_920L;

    private static final int SAMPLES = 2_000;

    private final MyBigNumber calculator = new MyBigNumber();

    @Test
    @DisplayName("agrees with BigInteger on every sampled pair")
    void agreesWithBigInteger() {
        Random random = new Random(SEED);

        for (int i = 0; i < SAMPLES; i++) {
            String left = TestNumbers.digits(random.nextInt(80), random);
            String right = TestNumbers.digits(random.nextInt(80), random);

            String expected = toBigInteger(left).add(toBigInteger(right)).toString();

            assertThat(calculator.sum(left, right))
                    .withFailMessage("sum(%s, %s) should be %s", left, right, expected)
                    .isEqualTo(expected);
        }
    }

    @Test
    @DisplayName("a + b equals b + a")
    void isCommutative() {
        Random random = new Random(SEED + 1);

        for (int i = 0; i < SAMPLES; i++) {
            String left = TestNumbers.digits(1 + random.nextInt(60), random);
            String right = TestNumbers.digits(1 + random.nextInt(60), random);

            assertThat(calculator.sum(left, right)).isEqualTo(calculator.sum(right, left));
        }
    }

    @Test
    @DisplayName("(a + b) + c equals a + (b + c)")
    void isAssociative() {
        Random random = new Random(SEED + 2);

        for (int i = 0; i < SAMPLES; i++) {
            String a = TestNumbers.digits(1 + random.nextInt(40), random);
            String b = TestNumbers.digits(1 + random.nextInt(40), random);
            String c = TestNumbers.digits(1 + random.nextInt(40), random);

            assertThat(calculator.sum(calculator.sum(a, b), c))
                    .isEqualTo(calculator.sum(a, calculator.sum(b, c)));
        }
    }

    @Test
    @DisplayName("adding zero returns the same number in canonical form")
    void hasZeroAsIdentity() {
        Random random = new Random(SEED + 3);

        for (int i = 0; i < SAMPLES; i++) {
            String value = TestNumbers.digits(1 + random.nextInt(60), random);
            String canonical = toBigInteger(value).toString();

            assertThat(calculator.sum(value, "0")).isEqualTo(canonical);
            assertThat(calculator.sum("0", value)).isEqualTo(canonical);
            assertThat(calculator.sum(value, "")).isEqualTo(canonical);
        }
    }

    @Test
    @DisplayName("the result is never shorter than the longer operand and never more than one digit longer")
    void hasAPredictableWidth() {
        Random random = new Random(SEED + 4);

        for (int i = 0; i < SAMPLES; i++) {
            String left = TestNumbers.digits(1 + random.nextInt(60), random).replaceFirst("^0+(?=.)", "");
            String right = TestNumbers.digits(1 + random.nextInt(60), random).replaceFirst("^0+(?=.)", "");

            int widest = Math.max(left.length(), right.length());
            int resultWidth = calculator.sum(left, right).length();

            assertThat(resultWidth).isBetween(widest, widest + 1);
        }
    }

    @ParameterizedTest(name = "[{index}] doubling a number of {0} nines")
    @ValueSource(ints = {1, 2, 3, 10, 100, 1_000})
    void doublingAllNinesMatchesBigInteger(int length) {
        String nines = "9".repeat(length);

        String expected = new BigInteger(nines).multiply(BigInteger.TWO).toString();

        assertThat(calculator.sum(nines, nines)).isEqualTo(expected);
    }

    private static BigInteger toBigInteger(String value) {
        return value.isEmpty() ? BigInteger.ZERO : new BigInteger(value);
    }
}
