package com.caesar.add2num.web;

import com.caesar.add2num.core.MyBigNumber;
import com.caesar.add2num.web.config.Add2NumProperties;
import com.caesar.add2num.web.service.CalculationService;
import com.caesar.add2num.web.web.CalculatorController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The application starts and wires itself together.
 *
 * <p>A context load test is cheap and catches the whole class of mistakes that only appear at
 * startup: a missing bean, a property that fails to bind, a component outside the scanned packages.
 */
@SpringBootTest
@DisplayName("Add2Num web application")
class Add2NumWebApplicationTests {

    private final ApplicationContext context;
    private final Add2NumProperties properties;

    @Autowired
    Add2NumWebApplicationTests(ApplicationContext context, Add2NumProperties properties) {
        this.context = context;
        this.properties = properties;
    }

    @Test
    @DisplayName("starts with every collaborator in place")
    void contextLoads() {
        assertThat(context.getBean(MyBigNumber.class)).isNotNull();
        assertThat(context.getBean(CalculationService.class)).isNotNull();
        assertThat(context.getBean(CalculatorController.class)).isNotNull();
    }

    @Test
    @DisplayName("binds the configured limits from application.yml")
    void bindsProperties() {
        assertThat(properties.maxInputDigits()).isEqualTo(100_000);
        assertThat(properties.maxTraceSteps()).isEqualTo(500);
        assertThat(properties.maxColumnLayoutWidth()).isEqualTo(40);
    }

    @Test
    @DisplayName("reuses the core library rather than re-implementing the addition")
    void reusesTheCoreLibrary() {
        MyBigNumber calculator = context.getBean(MyBigNumber.class);

        assertThat(calculator.sum("1234", "897")).isEqualTo("2131");
    }
}
