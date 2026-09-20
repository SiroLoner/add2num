package com.caesar.add2num.web.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Backing object for the form on the home page.
 *
 * <p>A mutable class with getters and setters rather than a record, because Spring's form binding
 * and Thymeleaf's {@code th:field} both expect that shape.
 *
 * <p>The size limit repeated here has to be a compile time constant, so it cannot read
 * {@code add2num.max-input-digits}. It is deliberately set to the same default; the service applies
 * the configured value as well, and that second check is the one that governs.
 */
public class CalculationForm {

    /** Mirrors the default of {@code add2num.max-input-digits}. */
    static final int MAX_DIGITS = 100_000;

    @NotBlank(message = "Vui lòng nhập số thứ nhất.")
    @Pattern(regexp = "^\\s*[0-9]+\\s*$", message = "Chỉ được nhập chữ số 0-9, không dấu cách ở giữa, không dấu âm.")
    @Size(max = MAX_DIGITS, message = "Số thứ nhất vượt quá " + MAX_DIGITS + " chữ số.")
    private String firstNumber;

    @NotBlank(message = "Vui lòng nhập số thứ hai.")
    @Pattern(regexp = "^\\s*[0-9]+\\s*$", message = "Chỉ được nhập chữ số 0-9, không dấu cách ở giữa, không dấu âm.")
    @Size(max = MAX_DIGITS, message = "Số thứ hai vượt quá " + MAX_DIGITS + " chữ số.")
    private String secondNumber;

    public CalculationForm() {
        // required by the form binder
    }

    public CalculationForm(String firstNumber, String secondNumber) {
        this.firstNumber = firstNumber;
        this.secondNumber = secondNumber;
    }

    public String getFirstNumber() {
        return firstNumber;
    }

    public void setFirstNumber(String firstNumber) {
        this.firstNumber = firstNumber;
    }

    public String getSecondNumber() {
        return secondNumber;
    }

    public void setSecondNumber(String secondNumber) {
        this.secondNumber = secondNumber;
    }
}
