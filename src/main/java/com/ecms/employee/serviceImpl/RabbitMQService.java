package com.ecms.employee.serviceImpl;

import java.time.LocalDateTime;
import java.util.HashMap;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecms.employee.config.RabbitMQConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RabbitMQService {

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private CircuitBreakerRegistry registry;

	@Autowired
	private RabbitMQConfig rabbitMQConfig;

	public void routeMessageBasedOnCircuitState(String message) {

		CircuitBreaker circuitBreaker = registry.circuitBreaker("employeeServiceImpl");
		CircuitBreaker.State state = circuitBreaker.getState();

		log.info("CircuitBreaker state: {}", state);

		if (state == CircuitBreaker.State.OPEN) {
			log.info("Routing to DLQ... ");
			sendMessageToDLQExchange(message, rabbitMQConfig.dlqExchange, rabbitMQConfig.deadLetterRoutingKey);
		} else if (message.contains("Successfully")) {
			log.info("Success Response Routing to Main Exchange... ");
			sendSuccessMessageToMainExchange(message, rabbitMQConfig.exchangeName);
		} else {
			log.info("Routing to Main Exchange... ");
			sendMessageToMainExchange(message, rabbitMQConfig.exchangeName);
		}
	}

	public void sendSuccessMessageToMainExchange(String message, String exchangeName) {

		log.info("Sending Success Message to the RabbitMQ Exchange [{}]: {}", exchangeName, message);

		HashMap<String, String> mapAuditLog = new HashMap<>();
		mapAuditLog.put("Message", message);
		mapAuditLog.put("TransactionDate", LocalDateTime.now().toString());
		mapAuditLog.put("Type", "Employee Fetch Success");

		String requestMessage = "";
		try {
			requestMessage = new ObjectMapper().writeValueAsString(mapAuditLog);
			log.info("Sending message: {}", requestMessage);
			rabbitTemplate.convertAndSend(exchangeName, "", requestMessage);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void sendMessageToMainExchange(String message, String exchangeName) {

		log.info("Sending Message to the RabbitMQ Exchange [{}]: {}", exchangeName, message);

		HashMap<String, String> mapAuditLog = new HashMap<>();
		mapAuditLog.put("Username", message);
		mapAuditLog.put("TransactionDate", LocalDateTime.now().toString());
		mapAuditLog.put("Type", "Validate Api Key");

		String requestMessage = "";
		try {
			requestMessage = new ObjectMapper().writeValueAsString(mapAuditLog);
			log.info("Sending message: {}", requestMessage);
			rabbitTemplate.convertAndSend(exchangeName, "", requestMessage);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void sendMessageToDLQExchange(String message, String exchangeName, String deadLetterRoutingKey) {

		log.info("Preparing to send message to the RabbitMQ Exchange [{}] with Routing Key [{}]: {}", exchangeName,
				deadLetterRoutingKey, message);

		HashMap<String, String> mapAuditLog = new HashMap<>();
		mapAuditLog.put("Username", message);
		mapAuditLog.put("TransactionDate", LocalDateTime.now().toString());
		mapAuditLog.put("Type", "Circuit Breaker in OPEN State");

		String requestMessage = "";
		try {
			requestMessage = new ObjectMapper().writeValueAsString(mapAuditLog);
			log.info("Sending message: {}", requestMessage);
			rabbitTemplate.convertAndSend(exchangeName, deadLetterRoutingKey, requestMessage);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}
}
