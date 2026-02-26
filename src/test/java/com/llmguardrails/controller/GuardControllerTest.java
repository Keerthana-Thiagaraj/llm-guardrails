package com.llmguardrails.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.llmguardrails.model.Decision;
import com.llmguardrails.model.dto.MetricsResponse;
import com.llmguardrails.model.dto.ValidationRequest;
import com.llmguardrails.model.dto.ValidationResponse;
import com.llmguardrails.service.GuardService;
import com.llmguardrails.service.MetricsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class GuardControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GuardService guardService;

    @Mock
    private MetricsService metricsService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        GuardController controller = new GuardController(guardService, metricsService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testValidate_WithValidRequest_ShouldReturnOk() throws Exception {
        ValidationRequest request = new ValidationRequest("Hello, world!");
        ValidationResponse response = new ValidationResponse(
                Decision.ALLOW, 0.0, List.of("No risk"), null
        );

        when(guardService.validate(any(ValidationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/guard/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.decision").value("ALLOW"))
                .andExpect(jsonPath("$.riskScore").value(0.0))
                .andExpect(jsonPath("$.reasons").isArray());
    }

    @Test
    void testValidate_WithBlockDecision_ShouldReturnBlock() throws Exception {
        ValidationRequest request = new ValidationRequest("SELECT * FROM users");
        ValidationResponse response = new ValidationResponse(
                Decision.BLOCK, 0.9, List.of("SQL injection detected"), null
        );

        when(guardService.validate(any(ValidationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/guard/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.decision").value("BLOCK"))
                .andExpect(jsonPath("$.riskScore").value(0.9));
    }

    @Test
    void testValidate_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/guard/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"prompt\": \"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testValidate_WithMissingPrompt_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/guard/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetMetrics_ShouldReturnMetrics() throws Exception {
        MetricsResponse metrics = new MetricsResponse(100L, 70L, 20L, 10L);
        when(metricsService.getMetrics()).thenReturn(metrics);

        mockMvc.perform(get("/api/guard/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRequests").value(100))
                .andExpect(jsonPath("$.allowed").value(70))
                .andExpect(jsonPath("$.blocked").value(20))
                .andExpect(jsonPath("$.redacted").value(10));
    }

    @Test
    void testHealth_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/guard/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }
}

