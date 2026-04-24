package com.eci.arcn.producer.service;

import com.eci.arcn.producer.dto.MessageRequestDto;
import com.eci.arcn.producer.dto.MessageResponseDto;
import com.eci.arcn.producer.service.impl.MessagePublisherServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link MessagePublisherServiceImpl}.
 *
 * @author Jesús Pinzón
 * @version 1.0
 * @since 2026-04-23
 */
@ExtendWith(MockitoExtension.class)
class MessagePublisherServiceImplTest {

    private static final String EXCHANGE = "messages.exchange";
    private static final String ROUTING_KEY = "messages.routing-key";
    private static final String SAMPLE_CONTENT = "Hello from test";

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private MessagePublisherServiceImpl service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "exchangeName", EXCHANGE);
        ReflectionTestUtils.setField(service, "routingKey", ROUTING_KEY);
    }

    @Test
    void shouldReturnSentStatusWhenMessageIsPublished() {
        // Arrange
        MessageRequestDto request = MessageRequestDto.builder().content(SAMPLE_CONTENT).build();

        // Act
        MessageResponseDto response = service.publish(request);

        // Assert
        assertEquals("SENT", response.getStatus());
    }

    @Test
    void shouldReturnOriginalContentInResponse() {
        // Arrange
        MessageRequestDto request = MessageRequestDto.builder().content(SAMPLE_CONTENT).build();

        // Act
        MessageResponseDto response = service.publish(request);

        // Assert
        assertEquals(SAMPLE_CONTENT, response.getMessage());
    }

    @Test
    void shouldReturnNonNullTimestampAfterPublishing() {
        // Arrange
        MessageRequestDto request = MessageRequestDto.builder().content(SAMPLE_CONTENT).build();

        // Act
        MessageResponseDto response = service.publish(request);

        // Assert
        assertNotNull(response.getTimestamp());
    }

    @Test
    void shouldDelegateToRabbitTemplateWithCorrectArguments() {
        // Arrange
        MessageRequestDto request = MessageRequestDto.builder().content(SAMPLE_CONTENT).build();

        // Act
        service.publish(request);

        // Assert
        verify(rabbitTemplate, times(1))
                .convertAndSend(EXCHANGE, ROUTING_KEY, SAMPLE_CONTENT);
    }

    @Test
    void shouldPublishOnlyOncePerCall() {
        // Arrange
        MessageRequestDto request = MessageRequestDto.builder().content(SAMPLE_CONTENT).build();

        // Act
        service.publish(request);

        // Assert — exactly one interaction with the broker per publish call
        verify(rabbitTemplate, times(1))
                .convertAndSend(EXCHANGE, ROUTING_KEY, SAMPLE_CONTENT);
    }
}
