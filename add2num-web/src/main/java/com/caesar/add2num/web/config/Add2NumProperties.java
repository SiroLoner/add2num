package com.caesar.add2num.web.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Tunables for the web layer, bound from the {@code add2num.*} keys in {@code application.yml}.
 *
 * <p>These are limits, not preferences. Without them a single request carrying a hundred million
 * digits could exhaust the heap of the whole server, which is exactly the sort of detail a
 * calculator that accepts unbounded input has to answer for.
 *
 * @param maxInputDigits        largest operand the web layer will accept, in digits
 * @param maxTraceSteps         largest number of columns sent to the browser for the walkthrough
 * @param maxColumnLayoutWidth  widest calculation still drawn as a pen-and-paper column layout
 */
@ConfigurationProperties(prefix = "add2num")
public record Add2NumProperties(
        @DefaultValue("100000") int maxInputDigits,
        @DefaultValue("500") int maxTraceSteps,
        @DefaultValue("40") int maxColumnLayoutWidth) {

    public Add2NumProperties {
        if (maxInputDigits < 1) {
            throw new IllegalArgumentException("add2num.max-input-digits must be at least 1, but was " + maxInputDigits);
        }
        if (maxTraceSteps < 0) {
            throw new IllegalArgumentException("add2num.max-trace-steps must not be negative, but was " + maxTraceSteps);
        }
        if (maxColumnLayoutWidth < 0) {
            throw new IllegalArgumentException(
                    "add2num.max-column-layout-width must not be negative, but was " + maxColumnLayoutWidth);
        }
    }
}
