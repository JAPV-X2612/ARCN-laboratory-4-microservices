package com.eci.arcn.consumer.service;

import com.eci.arcn.consumer.service.impl.MessageProcessorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link MessageProcessorServiceImpl}.
 *
 * @author Jesús Pinzón
 * @version 1.0
 * @since 2026-04-23
 */
class MessageProcessorServiceImplTest {

    private MessageProcessorServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new MessageProcessorServiceImpl();
    }

    @Test
    void shouldProcessMessageWithoutThrowingException() {
        // Arrange
        String message = "Hello from test";

        // Act & Assert
        assertDoesNotThrow(() -> service.process(message));
    }

    @Test
    void shouldStoreMessageAfterProcessing() {
        // Arrange
        String message = "Hello from test";

        // Act
        service.process(message);

        // Assert
        assertTrue(service.getProcessedMessages().contains(message));
    }

    @Test
    void shouldAccumulateMultipleMessages() {
        // Arrange
        String first = "First message";
        String second = "Second message";

        // Act
        service.process(first);
        service.process(second);

        // Assert
        assertEquals(2, service.getProcessedMessages().size());
        assertTrue(service.getProcessedMessages().contains(first));
        assertTrue(service.getProcessedMessages().contains(second));
    }

    @Test
    void shouldProcessEmptyStringWithoutException() {
        // Arrange
        String emptyMessage = "";

        // Act & Assert
        assertDoesNotThrow(() -> service.process(emptyMessage));
        assertTrue(service.getProcessedMessages().contains(emptyMessage));
    }

    @Test
    void shouldReturnEmptyListBeforeAnyMessageIsProcessed() {
        // Arrange

        // Act & Assert
        assertTrue(service.getProcessedMessages().isEmpty());
    }

    @Test
    void shouldReturnImmutableListView() {
        // Arrange
        service.process("test");

        // Act & Assert
        assertDoesNotThrow(() -> {
            var list = service.getProcessedMessages();
            assertFalse(list.isEmpty());
            // Verify it is an unmodifiable view by attempting mutation
            try {
                list.add("should not be added");
            } catch (UnsupportedOperationException e) {
                // expected
            }
            assertEquals(1, service.getProcessedMessages().size());
        });
    }
}
