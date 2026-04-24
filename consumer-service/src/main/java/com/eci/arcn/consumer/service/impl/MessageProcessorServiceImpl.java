package com.eci.arcn.consumer.service.impl;

import com.eci.arcn.consumer.service.MessageProcessorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Processes messages received from the RabbitMQ queue by logging them
 * and storing them in memory for observability.
 *
 * @author Jesús Pinzón
 * @version 1.0
 * @since 2026-04-23
 */
@Slf4j
@Service
public class MessageProcessorServiceImpl implements MessageProcessorService {

    private final List<String> processedMessages = new CopyOnWriteArrayList<>();

    @Override
    public void process(String message) {
        log.info("Processing message: '{}'", message);
        processedMessages.add(message);
    }

    @Override
    public List<String> getProcessedMessages() {
        return Collections.unmodifiableList(processedMessages);
    }
}
