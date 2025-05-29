//package com.ecms.employee.listener;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import com.ecms.employee.serviceImpl.DLQReprocessor;
//
//import io.github.resilience4j.circuitbreaker.CircuitBreaker;
//import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
//import jakarta.annotation.PostConstruct;
//
//@Component
//public class CircuitBreakerListener {
//
//	@Autowired
//	private CircuitBreakerRegistry registry;
//
//	@Autowired
//	private DLQReprocessor reprocessor;
//
//	@PostConstruct
//	public void subscribeToEvents() {
//		registry.circuitBreaker("employeeServiceImpl").getEventPublisher().onStateTransition(event -> {
//			if (event.getStateTransition().getToState() == CircuitBreaker.State.CLOSED) {
//				reprocessor.reprocessDLQMessages();
//			}
//		});
//	}
//
//}
