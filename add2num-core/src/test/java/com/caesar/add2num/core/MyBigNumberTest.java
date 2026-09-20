package com.caesar.add2num.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Behaviour of {@link MyBigNumber#sum(String, String)}.
 *
 * <p>The suite is organised by the rule under test rather than by method, so that a reviewer can
 * see at a glance which part of the specification each group of cases defends.
 */
@DisplayName("MyBigNumber.sum")
class MyBigNumberTest {

    private final MyBigNumber calculator = new MyBigNumber();

    @Nested
    @DisplayName("worked example from the requirement document")
    class SpecificationExample {

        @Test
        @DisplayName("1234 + 897 = 2131, the example spelled out in Add2Num_High-level-requirement_v1.8")
        void addsTheDocumentedExample() {
            assertThat(calculator.sum("1234", "897")).isEqualTo("2131");
        }

        @Test
        @DisplayName("the first column is 4 + 7 = 11, which writes 1 and carries 1")
        void firstColumnMatchesTheDocument() {
            SumStep first = calculator.sumWithTrace("1234", "897").steps().get(0);

            assertThat(first.total()).isEqualTo(11);
            assertThat(first.digit()).isEqualTo(1);
            assertThat(first.carryOut()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("ordinary addition")
    class OrdinaryAddition {

        @ParameterizedTest(name = "[{index}] {0} + {1} = {2}")
        @CsvSource({
                "0,          0,          0",
                "1,          1,          2",
                "5,          5,          10",
                "9,          9,          18",
                "10,         1,          11",
                "42,         58,         100",
                "123456789,  987654321,  1111111110",
                "1000000000, 1,          1000000001"
        })
        void addsPairsOfNumbers(String left, String right, String expected) {
            assertThat(calculator.sum(left, right)).isEqualTo(expected);
        }

        @Test
        @DisplayName("handles operands far beyond the range of long")
        void addsNumbersLargerThanLong() {
            String left = "179769313486231570000000000000000000000000000000000000000000";
            String right = "820230686513768430000000000000000000000000000000000000000001";

            assertThat(calculator.sum(left, right))
                    .isEqualTo("1000000000000000000000000000000000000000000000000000000000001");
        }
    }

    @Nested
    @DisplayName("operands of different lengths")
    class DifferentLengths {

        @ParameterizedTest(name = "[{index}] {0} + {1} = {2}")
        @CsvSource({
                "1,      99999,   100000",
                "99999,  1,       100000",
                "7,      1234567, 1234574",
                "1234567, 7,      1234574",
                "5,      12345678901234567890, 12345678901234567895"
        })
        void alignsOperandsToTheRight(String left, String right, String expected) {
            assertThat(calculator.sum(left, right)).isEqualTo(expected);
        }

        @Test
        @DisplayName("a missing digit on the shorter operand behaves as zero")
        void treatsMissingDigitsAsZero() {
            assertThat(calculator.sum("1000000", "1")).isEqualTo("1000001");
        }
    }

    @Nested
    @DisplayName("carry propagation")
    class CarryPropagation {

        @ParameterizedTest(name = "[{index}] {0} nines + 1")
        @ValueSource(ints = {1, 2, 3, 5, 17, 64, 255, 1_000})
        void carriesAcrossTheWholeNumber(int length) {
            String nines = "9".repeat(length);

            String expected = "1" + "0".repeat(length);
            assertThat(calculator.sum(nines, "1")).isEqualTo(expected);
        }

        @Test
        @DisplayName("999 + 1 grows the result by one digit")
        void producesAnExtraDigitOnOverflow() {
            assertThat(calculator.sum("999", "1")).isEqualTo("1000");
        }

        @Test
        @DisplayName("the largest possible column, 9 + 9 + 1, is handled")
        void handlesTheLargestColumn() {
            assertThat(calculator.sum("99", "99")).isEqualTo("198");
        }
    }

    @Nested
    @DisplayName("canonical form of the result")
    class CanonicalForm {

        @ParameterizedTest(name = "[{index}] {0} + {1} = {2}")
        @CsvSource({
                "007,    3,      10",
                "0001,   0002,   3",
                "0000,   0000,   0",
                "000,    1,      1",
                "00000000000000000001, 00000000000000000001, 2"
        })
        void acceptsLeadingZerosAndRemovesThemFromTheResult(String left, String right, String expected) {
            assertThat(calculator.sum(left, right)).isEqualTo(expected);
        }

        @Test
        @DisplayName("a zero result is a single zero, never an empty string")
        void keepsOneZeroForAZeroResult() {
            assertThat(calculator.sum("0", "0")).isEqualTo("0");
            assertThat(calculator.sum("0000", "00")).isEqualTo("0");
        }

        @Test
        @DisplayName("the result never carries a leading zero")
        void neverEmitsALeadingZero() {
            assertThat(calculator.sum("0099", "0001")).isEqualTo("100").doesNotStartWith("0");
        }
    }

    @Nested
    @DisplayName("empty operands")
    class EmptyOperands {

        @ParameterizedTest(name = "[{index}] ''{0}'' + ''{1}'' = {2}")
        @CsvSource({
                "'',  '',  0",
                "'',  5,   5",
                "5,   '',  5",
                "'',  000, 0"
        })
        void treatsAnEmptyOperandAsZero(String left, String right, String expected) {
            assertThat(calculator.sum(left, right)).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("contract enforcement")
    class ContractEnforcement {

        @Test
        @DisplayName("a null first operand is rejected by name")
        void rejectsNullFirstOperand() {
            assertThatThrownBy(() -> calculator.sum(null, "1"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("stn1")
                    .hasMessageContaining("null");
        }

        @Test
        @DisplayName("a null second operand is rejected by name")
        void rejectsNullSecondOperand() {
            assertThatThrownBy(() -> calculator.sum("1", null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("stn2")
                    .hasMessageContaining("null");
        }

        @ParameterizedTest(name = "[{index}] rejects \"{0}\"")
        @ValueSource(strings = {"12a4", "-5", "+5", "1 2", "1.5", "1,5", "1_000", "١٢", "１"})
        void rejectsAnythingThatIsNotAnAsciiDigit(String invalid) {
            assertThatThrownBy(() -> calculator.sum(invalid, "1"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("stn1");
        }

        @Test
        @DisplayName("the error message names the offending index, which makes the failure diagnosable")
        void reportsTheOffendingIndex() {
            assertThatThrownBy(() -> calculator.sum("12a4", "1"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("index 2")
                    .hasMessageContaining("'a'");
        }
    }

    @Nested
    @DisplayName("agreement with java.math.BigInteger")
    class AgreementWithBigInteger {

        @ParameterizedTest(name = "[{index}] {0} digits per operand")
        @ValueSource(ints = {1, 2, 9, 10, 19, 20, 64, 500, 5_000})
        void matchesBigIntegerForOperandsOfEveryWidth(int digits) {
            String left = TestNumbers.digits(digits, 11L + digits);
            String right = TestNumbers.digits(digits, 97L + digits);

            String expected = new BigInteger(left).add(new BigInteger(right)).toString();
            assertThat(calculator.sum(left, right)).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("statelessness")
    class Statelessness {

        @Test
        @DisplayName("repeated calls on one instance do not influence each other")
        void isReusable() {
            assertThat(calculator.sum("999", "1")).isEqualTo("1000");
            assertThat(calculator.sum("1", "1")).isEqualTo("2");
            assertThat(calculator.sum("999", "1")).isEqualTo("1000");
        }

        @Test
        @DisplayName("the same instance can be used from several threads")
        void isThreadSafe() throws InterruptedException {
            int threads = 8;
            String left = "9".repeat(5_000);
            String expected = "1" + "0".repeat(4_999) + "8";
            boolean[] ok = new boolean[threads];
            Thread[] workers = new Thread[threads];

            for (int t = 0; t < threads; t++) {
                final int index = t;
                workers[t] = new Thread(() -> {
                    boolean allMatched = true;
                    for (int i = 0; i < 200; i++) {
                        allMatched &= calculator.sum(left, "9").equals(expected);
                    }
                    ok[index] = allMatched;
                });
                workers[t].start();
            }
            for (Thread worker : workers) {
                worker.join();
            }

            assertThat(ok).containsOnly(true);
        }
    }
}
