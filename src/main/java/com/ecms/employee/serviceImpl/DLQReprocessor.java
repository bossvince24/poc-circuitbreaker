package com.ecms.employee.serviceImpl;

import java.nio.charset.StandardCharsets;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ecms.employee.config.RabbitMQConfig;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DLQReprocessor {

	@Autowired
	private CircuitBreakerRegistry registry;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private RabbitMQConfig rabbitMQConfig;

	@Scheduled(fixedRate = 10000)
	public void reprocessDLQMessages() {

		CircuitBreaker.State state = registry.circuitBreaker("employeeServiceImpl").getState();
		log.info("DLQ Reprocessor - CircuitBreaker state: {}", state);

		if (state == CircuitBreaker.State.CLOSED) {

			Message message = rabbitTemplate.receive(rabbitMQConfig.dlqQueueName);

			if (message == null) {
				return;
			}

			log.info("Circuit is CLOSED. DLQ has messages. Starting reprocessing...");

			while (message != null) {
				try {

					String body = new String(message.getBody(), StandardCharsets.UTF_8);

					log.info("Reprocessing message from DLQ: {}", body);

					Message newMessage = MessageBuilder.withBody(body.getBytes()).setPriority(5)
							.setContentType("application/json").build();

					log.info("Sending DLQ message to main queue with priority: {}",
							newMessage.getMessageProperties().getPriority());

					rabbitTemplate.send(rabbitMQConfig.exchangeName, "", newMessage);

					message = rabbitTemplate.receive(rabbitMQConfig.dlqQueueName);
				} catch (Exception e) {
					log.error("Error while reprocessing DLQ message: {}", e.getMessage(), e);
					break;
				}
			}
		} else {
			log.info("Circuit is OPEN. Will not reprocess DLQ messages.");
		}
	}
}
