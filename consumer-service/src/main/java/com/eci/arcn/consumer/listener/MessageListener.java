package com.eci.arcn.consumer.listener;

import com.eci.arcn.consumer.service.MessageProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * AMQP listener that receives messages from the RabbitMQ queue
 * and delegates processing to {@link MessageProcessorService}.
 *
 * @author Jesús Pinzón
 * @version 1.0
 * @since 2026-04-23
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageListener {

    private final MessageProcessorService messageProcessorService;

    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void onMessage(String message) {
        log.info("Received message from queue: '{}'", message);
        messageProcessorService.process(message);
    }
}
