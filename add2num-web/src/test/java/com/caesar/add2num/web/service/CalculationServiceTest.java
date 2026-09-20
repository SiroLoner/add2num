package com.caesar.add2num.web.service;

import com.caesar.add2num.core.MyBigNumber;
import com.caesar.add2num.core.SumResult;
import com.caesar.add2num.web.config.Add2NumProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * The service in isolation, with no Spring context: it is a plain object with two collaborators,
 * so a plain unit test is both faster and more precise than starting an application for it.
 */
@DisplayName("CalculationService")
class CalculationServiceTest {

    private final Add2NumProperties properties = new Add2NumProperties(20, 5, 40);
    private final CalculationService service = new CalculationService(new MyBigNumber(), properties);

    @Test
    @DisplayName("delegates the arithmetic to the core library")
    void addsTwoNumbers() {
        SumResult result = service.add("1234", "897");

        assertThat(result.value()).isEqualTo("2131");
    }

    @Test
    @DisplayName("trims the whitespace that comes with a pasted number")
    void trimsInput() {
        assertThat(service.add("  1234\n", "\t897  ").value()).isEqualTo("2131");
    }

    @Test
    @DisplayName("clamps a step budget larger than the configured maximum")
    void clampsTheStepBudget() {
        SumResult result = service.add("123456789", "987654321", 1_000);

        assertThat(result.steps()).hasSize(5);
        assertThat(result.truncated()).isTrue();
    }

    @Test
    @DisplayName("honours a step budget smaller than the configured maximum")
    void honoursASmallerBudget() {
        assertThat(service.add("123456789", "987654321", 2).steps()).hasSize(2);
    }

    @Test
    @DisplayName("treats a negative step budget as zero rather than failing")
    void toleratesANegativeBudget() {
        SumResult result = service.add("12", "34", -5);

        assertThat(result.value()).isEqualTo("46");
        assertThat(result.steps()).isEmpty();
    }

    @ParameterizedTest(name = "[{index}] rejects \"{0}\"")
    @ValueSource(strings = {"", "   ", "12a4", "-1", "1.5", "1 2"})
    @DisplayName("rejects an operand that is not a plain number")
    void rejectsInvalidOperands(String invalid) {
        assertThatThrownBy(() -> service.add(invalid, "1"))
                .isInstanceOf(InvalidNumberException.class);
    }

    @Test
    @DisplayName("rejects a null operand")
    void rejectsNull() {
        assertThatThrownBy(() -> service.add(null, "1"))
                .isInstanceOf(InvalidNumberException.class)
                .hasMessageContaining("first");
    }

    @Test
    @DisplayName("names the operand that was rejected so the page can point at it")
    void namesTheRejectedOperand() {
        assertThatThrownBy(() -> service.add("1", "2b"))
                .isInstanceOf(InvalidNumberException.class)
                .hasMessageContaining("second");
    }

    @Test
    @DisplayName("refuses an operand longer than the configured limit")
    void enforcesTheLengthLimit() {
        String tooLong = "1".repeat(properties.maxInputDigits() + 1);

        assertThatThrownBy(() -> service.add(tooLong, "1"))
                .isInstanceOf(InvalidNumberException.class)
                .hasMessageContaining("exceeds the configured limit");
    }

    @Test
    @DisplayName("accepts an operand exactly at the limit")
    void acceptsAnOperandAtTheLimit() {
        String atLimit = "1".repeat(properties.maxInputDigits());

        assertThat(service.add(atLimit, "0").value()).isEqualTo(atLimit);
    }

    @Test
    @DisplayName("exposes the configured limits for the user interface")
    void exposesTheLimits() {
        assertThat(service.maxInputDigits()).isEqualTo(20);
        assertThat(service.maxTraceSteps()).isEqualTo(5);
        assertThat(service.maxColumnLayoutWidth()).isEqualTo(40);
    }

    @Test
    @DisplayName("rejects a nonsensical configuration at construction time")
    void rejectsABrokenConfiguration() {
        assertThatThrownBy(() -> new Add2NumProperties(0, 5, 40))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("max-input-digits");

        assertThatThrownBy(() -> new Add2NumProperties(10, -1, 40))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("max-trace-steps");

        assertThatThrownBy(() -> new Add2NumProperties(10, 5, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("max-column-layout-width");
    }
}
