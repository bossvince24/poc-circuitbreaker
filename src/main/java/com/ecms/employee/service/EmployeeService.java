package com.ecms.employee.service;

import java.util.List;

import com.ecms.employee.entity.Employee;

public interface EmployeeService {
	
	List<Employee> getAllEmployees();
	
	Employee saveEmployee(Employee employee);
	
	Employee getEmployeeById(Long id);
}
