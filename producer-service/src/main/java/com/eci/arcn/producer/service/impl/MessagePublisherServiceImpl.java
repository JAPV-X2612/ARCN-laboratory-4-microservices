package com.eci.arcn.producer.service.impl;

import com.eci.arcn.producer.dto.MessageRequestDto;
import com.eci.arcn.producer.dto.MessageResponseDto;
import com.eci.arcn.producer.service.MessagePublisherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Publishes messages to RabbitMQ using {@link RabbitTemplate}.
 *
 * @author Jesús Pinzón
 * @version 1.0
 * @since 2026-04-23
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessagePublisherServiceImpl implements MessagePublisherService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${app.rabbitmq.routing-key}")
    private String routingKey;

    @Override
    public MessageResponseDto publish(MessageRequestDto request) {
        log.info("Publishing message to exchange='{}' routingKey='{}': '{}'",
                exchangeName, routingKey, request.getContent());

        rabbitTemplate.convertAndSend(exchangeName, routingKey, request.getContent());

        return MessageResponseDto.builder()
                .message(request.getContent())
                .status("SENT")
                .timestamp(LocalDateTime.now())
                .build();
    }
}
