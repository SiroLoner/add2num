package com.caesar.add2num.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The requirement document asks for logging so that the working of the algorithm can be observed.
 * These tests pin that behaviour down instead of leaving it to be discovered by reading the console.
 *
 * <p>The library logs through {@link System.Logger}, whose default JDK implementation delegates to
 * {@code java.util.logging}. The tests therefore attach a capturing handler to the JUL logger that
 * carries the class name, and restore the previous configuration afterwards so that no other test
 * is affected.
 */
@DisplayName("MyBigNumber logging")
class MyBigNumberLoggingTest {

    private final MyBigNumber calculator = new MyBigNumber();

    private Logger julLogger;
    private CapturingHandler handler;
    private Level previousLevel;
    private boolean previousUseParentHandlers;

    @BeforeEach
    void enableVerboseLogging() {
        julLogger = Logger.getLogger(MyBigNumber.class.getName());
        previousLevel = julLogger.getLevel();
        previousUseParentHandlers = julLogger.getUseParentHandlers();

        handler = new CapturingHandler();
        handler.setLevel(Level.ALL);
        julLogger.setLevel(Level.ALL);
        julLogger.setUseParentHandlers(false);   // keep the build output clean
        julLogger.addHandler(handler);
    }

    @AfterEach
    void restoreLogging() {
        julLogger.removeHandler(handler);
        julLogger.setLevel(previousLevel);
        julLogger.setUseParentHandlers(previousUseParentHandlers);
    }

    @Test
    @DisplayName("logs a summary of every completed addition")
    void logsASummary() {
        calculator.sum("1234", "897");

        assertThat(handler.messages()).anyMatch(message -> message.contains("2131"));
    }

    @Test
    @DisplayName("logs one line per column when tracing is enabled")
    void logsEveryColumn() {
        calculator.sum("1234", "897");

        assertThat(handler.messages()).contains("column 0: 4 + 7 + 0 = 11 -> write 1, carry 1");
        assertThat(handler.messages()).contains("column 1: 3 + 9 + 1 = 13 -> write 3, carry 1");
    }

    @Test
    @DisplayName("logs the column that writes the final carry")
    void logsTheFinalCarry() {
        calculator.sum("999", "1");

        assertThat(handler.messages()).anyMatch(message -> message.contains("final carry"));
    }

    @Test
    @DisplayName("abbreviates very long operands so that a log line stays readable")
    void abbreviatesLongOperands() {
        String left = "1".repeat(120);
        String right = "2".repeat(120);

        calculator.sum(left, right);

        assertThat(handler.messages())
                .anyMatch(message -> message.contains("...") && message.contains("120 digits"));
    }

    @Test
    @DisplayName("logs how much of the trace was kept")
    void logsTheTraceSize() {
        calculator.sumWithTrace("123456", "654321", 2);

        assertThat(handler.messages()).anyMatch(message -> message.contains("2 of 6 step(s)"));
    }

    /** Collects log records in memory so that assertions can be made about them. */
    private static final class CapturingHandler extends Handler {

        private final List<String> messages = new ArrayList<>();

        @Override
        public synchronized void publish(LogRecord record) {
            messages.add(record.getMessage());
        }

        @Override
        public void flush() {
            // nothing buffered
        }

        @Override
        public void close() {
            // nothing to release
        }

        synchronized List<String> messages() {
            return List.copyOf(messages);
        }
    }
}
