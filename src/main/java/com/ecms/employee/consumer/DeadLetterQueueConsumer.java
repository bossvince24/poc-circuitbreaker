//package com.ecms.employee.consumer;
//
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.stereotype.Component;
//
//import lombok.extern.slf4j.Slf4j;
//
//
//@Component
//@Slf4j
//public class DeadLetterQueueConsumer {
//	
//	@RabbitListener(queues = "employee.dlq")
//	public void listenDlq(String message) {
//		log.info("Message in DLQ: " + message);
//	}
//
//}
