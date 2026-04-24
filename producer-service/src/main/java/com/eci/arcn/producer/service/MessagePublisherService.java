package com.eci.arcn.producer.service;

import com.eci.arcn.producer.dto.MessageRequestDto;
import com.eci.arcn.producer.dto.MessageResponseDto;

/**
 * Contract for publishing messages to the RabbitMQ exchange.
 *
 * @author Jesús Pinzón
 * @version 1.0
 * @since 2026-04-23
 */
public interface MessagePublisherService {

    /**
     * Publishes the given message to the configured RabbitMQ exchange.
     *
     * @param request the message payload to publish
     * @return a response containing the sent content, status, and timestamp
     */
    MessageResponseDto publish(MessageRequestDto request);
}
