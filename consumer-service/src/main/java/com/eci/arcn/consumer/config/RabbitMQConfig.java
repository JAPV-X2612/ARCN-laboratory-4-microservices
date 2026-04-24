package com.eci.arcn.consumer.config;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration for the consumer: declares the durable queue
 * so it exists even when this service starts before the producer.
 *
 * @author Jesús Pinzón
 * @version 1.0
 * @since 2026-04-23
 */
@Configuration
public class RabbitMQConfig {

    @Value("${app.rabbitmq.queue}")
    private String queueName;

    @Bean
    public Queue queue() {
        // durable=true must match the producer's declaration to avoid topology conflicts
        return new Queue(queueName, true);
    }
}
