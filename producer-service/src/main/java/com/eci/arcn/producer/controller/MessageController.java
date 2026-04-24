package com.eci.arcn.producer.controller;

import com.eci.arcn.producer.dto.MessageRequestDto;
import com.eci.arcn.producer.dto.MessageResponseDto;
import com.eci.arcn.producer.service.MessagePublisherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller that exposes the message publishing endpoint.
 *
 * @author Jesús Pinzón
 * @version 1.0
 * @since 2026-04-23
 */
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessagePublisherService messagePublisherService;

    /**
     * Publishes a message to the RabbitMQ exchange.
     *
     * @param request the message payload; validated before processing
     * @return HTTP 200 with the send confirmation and timestamp
     */
    @PostMapping("/send")
    public ResponseEntity<MessageResponseDto> send(@Valid @RequestBody MessageRequestDto request) {
        return ResponseEntity.ok(messagePublisherService.publish(request));
    }
}
