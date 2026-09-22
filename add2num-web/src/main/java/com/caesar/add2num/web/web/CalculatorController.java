package com.caesar.add2num.web.web;

import com.caesar.add2num.core.SumResult;
import com.caesar.add2num.web.service.CalculationService;
import com.caesar.add2num.web.service.InvalidNumberException;
import com.caesar.add2num.web.view.CalculationView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * The single page of the application: a form for the two operands and, once submitted, the result
 * together with the step by step walkthrough of the calculation.
 *
 * <p>The page re-renders in place rather than redirecting, so that the numbers the user typed stay
 * in the boxes next to the answer and can be edited and submitted again.
 */
@Controller
public class CalculatorController {

    private static final Logger logger = LoggerFactory.getLogger(CalculatorController.class);

    private final CalculationService calculationService;
    private final ObjectMapper objectMapper;

    public CalculatorController(CalculationService calculationService, ObjectMapper objectMapper) {
        this.calculationService = calculationService;
        this.objectMapper = objectMapper;
    }

    /** Exposed to every render so the page can state the limit rather than let the user find it. */
    @ModelAttribute("maxInputDigits")
    public int maxInputDigits() {
        return calculationService.maxInputDigits();
    }

    @ModelAttribute("maxTraceSteps")
    public int maxTraceSteps() {
        return calculationService.maxTraceSteps();
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("form", new CalculationForm());
        return "index";
    }

    @PostMapping("/")
    public String calculate(@Valid @ModelAttribute("form") CalculationForm form,
                            BindingResult binding,
                            Model model) {
        if (binding.hasErrors()) {
            return "index";
        }

        try {
            SumResult result = calculationService.add(form.getFirstNumber(), form.getSecondNumber());
            CalculationView view = new CalculationView(
                    form.getFirstNumber().trim(),
                    form.getSecondNumber().trim(),
                    result,
                    calculationService.maxColumnLayoutWidth());

            model.addAttribute("view", view);
            model.addAttribute("stepsJson", toJson(view));
        } catch (InvalidNumberException e) {
            // Reached when the service applies a limit the form annotations cannot express, such as
            // the configured maximum length. Reported against the offending box when it is known.
            logger.debug("rejected a submission: {}", e.getMessage());
            String field = "second".equals(e.getField()) ? "secondNumber" : "firstNumber";
            binding.rejectValue(field, "invalid.number", e.getMessage());
        }
        return "index";
    }

    /**
     * Serialises the columns for the browser side walkthrough. The steps are plain view objects, so
     * a failure here would mean a programming error rather than bad input.
     */
    private String toJson(CalculationView view) {
        try {
            return objectMapper.writeValueAsString(view.getSteps());
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("could not serialise the calculation steps", e);
        }
    }
}
