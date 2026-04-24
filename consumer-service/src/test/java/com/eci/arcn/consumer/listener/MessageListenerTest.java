package com.eci.arcn.consumer.listener;

import com.eci.arcn.consumer.service.MessageProcessorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link MessageListener}.
 *
 * @author Jesús Pinzón
 * @version 1.0
 * @since 2026-04-23
 */
@ExtendWith(MockitoExtension.class)
class MessageListenerTest {

    private static final String SAMPLE_MESSAGE = "Hello from RabbitMQ";

    @Mock
    private MessageProcessorService messageProcessorService;

    @InjectMocks
    private MessageListener messageListener;

    @Test
    void shouldDelegateReceivedMessageToProcessorService() {
        // Arrange and Act
        messageListener.onMessage(SAMPLE_MESSAGE);

        // Assert
        verify(messageProcessorService, times(1)).process(SAMPLE_MESSAGE);
    }

    @Test
    void shouldProcessEachMessageExactlyOnce() {
        // Arrange
        String firstMessage = "First event";
        String secondMessage = "Second event";

        // Act
        messageListener.onMessage(firstMessage);
        messageListener.onMessage(secondMessage);

        // Assert
        verify(messageProcessorService, times(1)).process(firstMessage);
        verify(messageProcessorService, times(1)).process(secondMessage);
    }

    @Test
    void shouldHandleMessageWithSpecialCharacters() {
        // Arrange
        String specialMessage = "Test: áéíóú @#$% 123";

        // Act
        messageListener.onMessage(specialMessage);

        // Assert
        verify(messageProcessorService, times(1)).process(specialMessage);
    }
}
