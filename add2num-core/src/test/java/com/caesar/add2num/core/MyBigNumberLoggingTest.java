package com.caesar.add2num.core;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The requirement document asks for logging so that the working of the algorithm can be observed.
 * These tests pin that behaviour down instead of leaving it to be discovered by reading the console.
 *
 * <p>The test attaches a Logback appender to the SLF4J logger and restores its state afterwards.
 */
@DisplayName("MyBigNumber logging")
class MyBigNumberLoggingTest {

    private final MyBigNumber calculator = new MyBigNumber();

    private Logger logger;
    private ListAppender<ILoggingEvent> appender;
    private Level previousLevel;

    @BeforeEach
    void enableVerboseLogging() {
        logger = (Logger) LoggerFactory.getLogger(MyBigNumber.class);
        previousLevel = logger.getLevel();
        appender = new ListAppender<>();
        appender.start();
        logger.setLevel(Level.TRACE);
        logger.addAppender(appender);
    }

    @AfterEach
    void restoreLogging() {
        logger.detachAppender(appender);
        logger.setLevel(previousLevel);
    }

    @Test
    @DisplayName("logs a summary of every completed addition")
    void logsASummary() {
        calculator.sum("1234", "897");

        assertThat(messages()).anyMatch(message -> message.contains("4 and 3 digit operands"));
    }

    @Test
    @DisplayName("logs one line per column when tracing is enabled")
    void logsEveryColumn() {
        calculator.sum("1234", "897");

        assertThat(messages()).anyMatch(message -> message.contains("column 0: 4 + 7 + 0 = 11"));
        assertThat(messages()).anyMatch(message -> message.contains("column 1: 3 + 9 + 1 = 13"));
    }

    @Test
    @DisplayName("logs the column that writes the final carry")
    void logsTheFinalCarry() {
        calculator.sum("999", "1");

        assertThat(messages()).anyMatch(message -> message.contains("final carry"));
    }

    @Test
    @DisplayName("does not write operand values to logs")
    void doesNotWriteOperandValuesToLogs() {
        String left = "1".repeat(120);
        String right = "2".repeat(120);

        calculator.sum(left, right);

        assertThat(messages()).noneMatch(message -> message.contains(left) || message.contains(right));
    }

    @Test
    @DisplayName("logs how much of the trace was kept")
    void logsTheTraceSize() {
        calculator.sumWithTrace("123456", "654321", 2);

        assertThat(messages()).anyMatch(message -> message.contains("2 of 6 step(s)"));
    }

    private List<String> messages() {
        return appender.list.stream().map(ILoggingEvent::getFormattedMessage).toList();
    }
}
