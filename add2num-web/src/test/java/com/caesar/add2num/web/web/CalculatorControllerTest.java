package com.caesar.add2num.web.web;

import com.caesar.add2num.web.view.CalculationView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * End to end behaviour of the page, exercised through the real Spring MVC stack and the real
 * Thymeleaf templates, so that a broken expression in a template fails the build rather than
 * waiting to be found in a browser.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Calculator page")
class CalculatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("renders the empty form")
    void rendersTheForm() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attributeDoesNotExist("view"))
                .andExpect(content().string(containsString("Nhập hai số cần cộng")));
    }

    @Test
    @DisplayName("adds two numbers and shows the result")
    void addsTwoNumbers() throws Exception {
        MvcResult result = mockMvc.perform(post("/")
                        .param("firstNumber", "1234")
                        .param("secondNumber", "897"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("view", "stepsJson"))
                .andReturn();

        CalculationView calculationView = (CalculationView) result.getModelAndView()
                .getModel().get("view");

        assertThat(calculationView.getValue()).isEqualTo("2131");
        assertThat(calculationView.getTotalSteps()).isEqualTo(4);
        assertThat(calculationView.getCarryCount()).isEqualTo(3);
        assertThat(calculationView.isTruncated()).isFalse();
        assertThat(calculationView.isColumnLayoutAvailable()).isTrue();
    }

    @Test
    @DisplayName("shows the paper layout with the carries above the operands")
    void showsThePaperLayout() throws Exception {
        MvcResult result = mockMvc.perform(post("/")
                        .param("firstNumber", "1234")
                        .param("secondNumber", "897"))
                .andExpect(status().isOk())
                .andReturn();

        CalculationView calculationView = (CalculationView) result.getModelAndView().getModel().get("view");

        assertThat(calculationView.getColumnLayout().getFirstRow()).isEqualTo("1234");
        assertThat(calculationView.getColumnLayout().getSecondRow()).isEqualTo(" 897");
        assertThat(calculationView.getColumnLayout().getSumRow()).isEqualTo("2131");
        assertThat(calculationView.getColumnLayout().getCarryRow()).isEqualTo("111 ");
        assertThat(calculationView.getColumnLayout().getRule()).isEqualTo("----");
    }

    @Test
    @DisplayName("hands the browser the steps as JSON so the walkthrough can be replayed")
    void exposesTheStepsAsJson() throws Exception {
        MvcResult result = mockMvc.perform(post("/")
                        .param("firstNumber", "1234")
                        .param("secondNumber", "897"))
                .andReturn();

        String stepsJson = (String) result.getModelAndView().getModel().get("stepsJson");

        assertThat(stepsJson)
                .contains("\"column\":1")
                .contains("\"total\":11")
                .contains("\"carryOut\":1");
    }

    @Test
    @DisplayName("trims whitespace around a pasted number")
    void trimsPastedInput() throws Exception {
        MvcResult result = mockMvc.perform(post("/")
                        .param("firstNumber", "  1234\n")
                        .param("secondNumber", "\t897 "))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andReturn();

        CalculationView calculationView = (CalculationView) result.getModelAndView().getModel().get("view");

        assertThat(calculationView.getValue()).isEqualTo("2131");
    }

    @Test
    @DisplayName("accepts operands far beyond the range of long")
    void addsVeryLargeOperands() throws Exception {
        String left = "9".repeat(400);
        String right = "1";

        MvcResult result = mockMvc.perform(post("/")
                        .param("firstNumber", left)
                        .param("secondNumber", right))
                .andExpect(status().isOk())
                .andReturn();

        CalculationView calculationView = (CalculationView) result.getModelAndView().getModel().get("view");

        assertThat(calculationView.getValue()).isEqualTo("1" + "0".repeat(400));
        assertThat(calculationView.isColumnLayoutAvailable())
                .withFailMessage("a 401 digit calculation is too wide to be drawn on paper")
                .isFalse();
    }

    @Test
    @DisplayName("reports an empty operand against the right box")
    void rejectsAnEmptyOperand() throws Exception {
        mockMvc.perform(post("/")
                        .param("firstNumber", "")
                        .param("secondNumber", "897"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeHasFieldErrors("form", "firstNumber"))
                .andExpect(model().attributeDoesNotExist("view"));
    }

    @Test
    @DisplayName("rejects anything that is not a plain number")
    void rejectsNonNumericInput() throws Exception {
        mockMvc.perform(post("/")
                        .param("firstNumber", "12a4")
                        .param("secondNumber", "-897"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("form", "firstNumber", "secondNumber"))
                .andExpect(model().attributeDoesNotExist("view"));
    }

    @Test
    @DisplayName("keeps what the user typed when the submission is rejected")
    void keepsTheSubmittedValuesOnError() throws Exception {
        mockMvc.perform(post("/")
                        .param("firstNumber", "12a4")
                        .param("secondNumber", "897"))
                .andExpect(content().string(containsString("12a4")));
    }

    @Test
    @DisplayName("serves the Bootstrap 5.2.0 stylesheet from the application itself")
    void servesBootstrapLocally() throws Exception {
        mockMvc.perform(get("/webjars/bootstrap/5.2.0/css/bootstrap.min.css"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("serves its own stylesheet and script")
    void servesItsOwnAssets() throws Exception {
        mockMvc.perform(get("/css/app.css")).andExpect(status().isOk());
        mockMvc.perform(get("/js/app.js")).andExpect(status().isOk());
    }
}
