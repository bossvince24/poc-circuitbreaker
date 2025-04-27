//package com.ecms.employee.consumer;
//
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.stereotype.Component;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Component
//@Slf4j
//public class RabbitMQConsumer {
//	
//	@RabbitListener(queues = "employeeQueue")
//	public void listen(String message) {
//		log.info("Consuming message from employeeQueue: " + message);
//		
////		if (message.contains("Failed")) {
////			throw new RuntimeException("Simulated processing failure");
////		}
//	}
//
//}
