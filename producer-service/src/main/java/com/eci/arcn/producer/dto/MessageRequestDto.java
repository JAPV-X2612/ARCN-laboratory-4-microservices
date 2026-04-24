package com.eci.arcn.producer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Inbound payload for publishing a message to the RabbitMQ exchange.
 *
 * @author Jesús Pinzón
 * @version 1.0
 * @since 2026-04-23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequestDto {

    @NotBlank(message = "Message content must not be blank")
    @Size(max = 500, message = "Message content must not exceed 500 characters")
    private String content;
}
