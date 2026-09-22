package com.caesar.add2num.web.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("POST /api/v1/sum")
class SumApiControllerTest {

    private final MockMvc mockMvc;

    @Autowired
    SumApiControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    @DisplayName("returns the sum together with the walkthrough")
    void returnsTheSum() throws Exception {
        mockMvc.perform(post("/api/v1/sum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"first\":\"1234\",\"second\":\"897\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value("2131"))
                .andExpect(jsonPath("$.digitCount").value(4))
                .andExpect(jsonPath("$.totalSteps").value(4))
                .andExpect(jsonPath("$.returnedSteps").value(4))
                .andExpect(jsonPath("$.truncated").value(false))
                .andExpect(jsonPath("$.carryCount").value(3))
                .andExpect(jsonPath("$.steps", org.hamcrest.Matchers.hasSize(4)))
                .andExpect(jsonPath("$.steps[0].place").value("units"))
                .andExpect(jsonPath("$.steps[0].total").value(11))
                .andExpect(jsonPath("$.steps[0].description").value("4 + 7 + 0 = 11 -> write 1, carry 1"));
    }

    @Test
    @DisplayName("honours a smaller step budget and says the walkthrough is partial")
    void honoursTheStepBudget() throws Exception {
        mockMvc.perform(post("/api/v1/sum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"first\":\"123456789\",\"second\":\"987654321\",\"maxSteps\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value("1111111110"))
                .andExpect(jsonPath("$.totalSteps").value(10))
                .andExpect(jsonPath("$.returnedSteps").value(2))
                .andExpect(jsonPath("$.truncated").value(true));
    }

    @Test
    @DisplayName("clamps a step budget larger than the server limit")
    void clampsAnExcessiveStepBudget() throws Exception {
        mockMvc.perform(post("/api/v1/sum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"first\":\"1234\",\"second\":\"897\",\"maxSteps\":100000}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.returnedSteps").value(4));
    }

    @Test
    @DisplayName("adds operands far beyond the range of long")
    void addsVeryLargeOperands() throws Exception {
        String left = "9".repeat(200);

        mockMvc.perform(post("/api/v1/sum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"first\":\"" + left + "\",\"second\":\"1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value("1" + "0".repeat(200)))
                .andExpect(jsonPath("$.digitCount").value(201));
    }

    @Test
    @DisplayName("answers 400 with a per field message when an operand is not a number")
    void rejectsNonNumericOperands() throws Exception {
        mockMvc.perform(post("/api/v1/sum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"first\":\"12a4\",\"second\":\"897\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fields[0].field").value("first"))
                .andExpect(jsonPath("$.fields[0].message").value("first must contain decimal digits only"));
    }

    @Test
    @DisplayName("answers 400 when an operand is missing")
    void rejectsAMissingOperand() throws Exception {
        mockMvc.perform(post("/api/v1/sum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"first\":\"1234\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fields[0].field").value("second"));
    }

    @Test
    @DisplayName("answers 400 when the body is not JSON at all")
    void rejectsAMalformedBody() throws Exception {
        mockMvc.perform(post("/api/v1/sum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("not json"))
                .andExpect(status().isBadRequest());
    }
}
