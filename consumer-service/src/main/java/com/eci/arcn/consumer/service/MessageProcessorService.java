package com.eci.arcn.consumer.service;

import java.util.List;

/**
 * Contract for processing messages received from the RabbitMQ queue.
 *
 * @author Jesús Pinzón
 * @version 1.0
 * @since 2026-04-23
 */
public interface MessageProcessorService {

    /**
     * Processes a single message received from the queue.
     *
     * @param message the raw message content
     */
    void process(String message);

    /**
     * Returns an immutable view of all messages processed since startup.
     *
     * @return list of processed message contents
     */
    List<String> getProcessedMessages();
}
