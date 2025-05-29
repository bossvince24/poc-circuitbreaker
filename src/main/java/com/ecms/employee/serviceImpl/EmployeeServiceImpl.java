package com.ecms.employee.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecms.employee.entity.Employee;
import com.ecms.employee.exception.EmployeeNotFoundException;
import com.ecms.employee.repository.EmployeeRepository;
import com.ecms.employee.service.EmployeeService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	private EmployeeRepository repo;

	@Autowired
	private RabbitMQService rabbitMQService;

	private static final String EMPLOYEE_SERVICEIMPL = "employeeServiceImpl";

	@Override
	public List<Employee> getAllEmployees() {
		// TODO Auto-generated method stub
		return repo.findAll();
	}

	@Override
	public Employee saveEmployee(Employee employee) {
		// TODO Auto-generated method stub
		return repo.save(employee);
	}

	@Override
	@CircuitBreaker(name = EMPLOYEE_SERVICEIMPL, fallbackMethod = "getEmployeeFallback")
	public Employee getEmployeeById(Long id) {
		// TODO Auto-generated method stub
		log.info("Trying to fetch employee...");

		Employee employee = repo.findById(id).orElseThrow(() -> new EmployeeNotFoundException("Employee Not Found"));

		String message = "Successfully fetched employee with ID: " + id;
		rabbitMQService.routeMessageBasedOnCircuitState(message);

		return employee;
	}

	public Employee getEmployeeFallback(Long id, EmployeeNotFoundException exception) {
		log.info("Fallback trigger due to: " + exception.getMessage());
		rabbitMQService.routeMessageBasedOnCircuitState("Failed to fetch employee with ID: " + id);
		return new Employee();
	}
}
