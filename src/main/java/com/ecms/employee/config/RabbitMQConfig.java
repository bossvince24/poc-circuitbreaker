package com.ecms.employee.config;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

	@Value("${spring.rabbitmq.exchange.name}")
	public String exchangeName;

	@Value("${spring.rabbitmq.dlq.exchange}")
	public String dlqExchange;
	
	@Value("${spring.rabbitmq.dlq.name}")
	public String dlqQueueName;

	@Value("${spring.rabbitmq.dlq.routing-key}")
	public String deadLetterRoutingKey;

	@Bean
	public FanoutExchange fanoutExchange() {
		return new FanoutExchange(exchangeName);
	}

	@Bean
	public DirectExchange directExchange() {
		return new DirectExchange(dlqExchange);
	}
	
	@Bean
	public Queue deadLetterQueue() {
		return QueueBuilder.durable(dlqQueueName).build();
	}
}
