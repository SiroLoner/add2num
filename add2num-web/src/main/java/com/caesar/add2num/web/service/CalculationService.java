package com.caesar.add2num.web.service;

import com.caesar.add2num.core.MyBigNumber;
import com.caesar.add2num.core.SumResult;
import com.caesar.add2num.web.config.Add2NumProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Application service sitting between the HTTP layer and the TASK 1 library.
 *
 * <p>Its job is everything the library deliberately does not do: apply the limits this deployment
 * is willing to accept, tidy up what a human pasted into a text box, and turn a library level
 * {@link IllegalArgumentException} into a domain exception the web layer knows how to present.
 * The arithmetic itself is never duplicated here.
 */
@Service
public class CalculationService {

    private static final Logger logger = LoggerFactory.getLogger(CalculationService.class);

    private final MyBigNumber calculator;
    private final Add2NumProperties properties;

    public CalculationService(MyBigNumber calculator, Add2NumProperties properties) {
        this.calculator = calculator;
        this.properties = properties;
    }

    /**
     * Adds two operands using the configured trace budget.
     *
     * @throws InvalidNumberException if either operand is missing, too long, or not a plain number
     */
    public SumResult add(String first, String second) {
        return add(first, second, properties.maxTraceSteps());
    }

    /**
     * Adds two operands, keeping at most {@code requestedSteps} columns of the walkthrough.
     *
     * <p>The requested budget is clamped to {@link Add2NumProperties#maxTraceSteps()}: a caller may
     * ask for less detail, never for more than this deployment is prepared to build and send.
     *
     * @throws InvalidNumberException if either operand is missing, too long, or not a plain number
     */
    public SumResult add(String first, String second, int requestedSteps) {
        String left = normalise(first, "first");
        String right = normalise(second, "second");
        int steps = Math.max(0, Math.min(requestedSteps, properties.maxTraceSteps()));

        try {
            SumResult result = calculator.sumWithTrace(left, right, steps);
            logger.debug("added {} and {} digit operands into a {} digit result in {} ms",
                    left.length(), right.length(), result.digitCount(), result.elapsedMillis());
            return result;
        } catch (IllegalArgumentException e) {
            // The core library validates too. Reaching this point means the checks below and the
            // library's checks disagree, which is a bug worth surfacing rather than hiding.
            logger.warn("core library rejected an operand that passed the web layer checks", e);
            throw new InvalidNumberException("first", e.getMessage(), e);
        }
    }

    /**
     * Trims the surrounding whitespace that comes with pasted numbers, then enforces the two limits
     * the library does not impose: the operand must be present, and it must fit the configured size.
     */
    private String normalise(String value, String field) {
        String trimmed = (value == null) ? "" : value.trim();

        if (trimmed.isEmpty()) {
            throw new InvalidNumberException(field, "Operand '" + field + "' must not be empty.");
        }
        if (trimmed.length() > properties.maxInputDigits()) {
            throw new InvalidNumberException(field, "Operand '" + field + "' has " + trimmed.length()
                    + " digits, which exceeds the configured limit of " + properties.maxInputDigits() + ".");
        }
        int i = 0;
        char c;
        for (; i < trimmed.length(); i++) {
            c = trimmed.charAt(i);
            if (c < '0' || c > '9') {
                throw new InvalidNumberException(field, "Operand '" + field + "' must contain digits only, "
                        + "but found '" + c + "' at position " + (i + 1) + ".");
            }
        }
        return trimmed;
    }

    /** The largest operand this deployment accepts, in digits. Used to label the user interface. */
    public int maxInputDigits() {
        return properties.maxInputDigits();
    }

    /** The largest number of columns this deployment will send to the browser. */
    public int maxTraceSteps() {
        return properties.maxTraceSteps();
    }

    /** The widest calculation still drawn as a pen-and-paper column layout. */
    public int maxColumnLayoutWidth() {
        return properties.maxColumnLayoutWidth();
    }
}
