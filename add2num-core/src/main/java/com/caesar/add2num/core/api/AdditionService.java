package com.caesar.add2num.core.api;

import com.caesar.add2num.core.MyBigNumber;
import org.springframework.stereotype.Service;

/** Application service that exposes the core calculator to the REST adapter. */
@Service
public class AdditionService {

    private final MyBigNumber calculator;

    public AdditionService(MyBigNumber calculator) {
        this.calculator = calculator;
    }

    public AdditionResponse add(String firstNumber, String secondNumber) {
        String first = normalise(firstNumber, "firstNumber");
        String second = normalise(secondNumber, "secondNumber");
        return new AdditionResponse(calculator.sum(first, second), first, second);
    }

    private String normalise(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        String trimmed = value.trim();
        for (int index = 0; index < trimmed.length(); index++) {
            char character = trimmed.charAt(index);
            if (character < '0' || character > '9') {
                throw new IllegalArgumentException(field + " must contain decimal digits only");
            }
        }
        return trimmed;
    }
}