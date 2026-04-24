package com.eci.arcn.producer.controller;

import com.eci.arcn.producer.dto.MessageRequestDto;
import com.eci.arcn.producer.dto.MessageResponseDto;
import com.eci.arcn.producer.service.MessagePublisherService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Slice tests for {@link MessageController} using {@link MockMvc}.
 * Only the web layer is loaded; the service is mocked.
 *
 * @author Jesús Pinzón
 * @version 1.0
 * @since 2026-04-23
 */
@WebMvcTest(MessageController.class)
class MessageControllerTest {

    private static final String ENDPOINT = "/api/messages/send";
    private static final String SAMPLE_CONTENT = "Hello World";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MessagePublisherService messagePublisherService;

    private MessageResponseDto sampleResponse;

    @BeforeEach
    void setUp() {
        sampleResponse = MessageResponseDto.builder()
                .message(SAMPLE_CONTENT)
                .status("SENT")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldReturnOkWhenValidMessageIsSent() throws Exception {
        // Arrange
        MessageRequestDto request = MessageRequestDto.builder().content(SAMPLE_CONTENT).build();
        when(messagePublisherService.publish(any())).thenReturn(sampleResponse);

        // Act & Assert
        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SENT"))
                .andExpect(jsonPath("$.message").value(SAMPLE_CONTENT));
    }

    @Test
    void shouldReturnBadRequestWhenContentIsBlank() throws Exception {
        // Arrange
        MessageRequestDto request = MessageRequestDto.builder().content("   ").build();

        // Act & Assert
        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenContentIsNull() throws Exception {
        // Arrange
        MessageRequestDto request = new MessageRequestDto();

        // Act & Assert
        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenContentExceedsMaxLength() throws Exception {
        // Arrange
        String oversizedContent = "A".repeat(501);
        MessageRequestDto request = MessageRequestDto.builder().content(oversizedContent).build();

        // Act & Assert
        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNonNullTimestampInResponse() throws Exception {
        // Arrange
        MessageRequestDto request = MessageRequestDto.builder().content(SAMPLE_CONTENT).build();
        when(messagePublisherService.publish(any())).thenReturn(sampleResponse);

        // Act & Assert
        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }
}
