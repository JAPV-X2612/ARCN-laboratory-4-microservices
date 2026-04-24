package com.eci.arcn.consumer;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

/**
 * Spring Boot context load smoke test for the Consumer microservice.
 * The {@link ConnectionFactory} is mocked to avoid requiring a live RabbitMQ instance.
 *
 * @author Jesús Pinzón
 * @version 1.0
 * @since 2026-04-23
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ConsumerApplicationTests {

    @MockBean
    private ConnectionFactory connectionFactory;

    @Test
    void contextLoads() {
    }
}
