 package com.ecms.employee.resolver;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.ecms.employee.entity.Employee;
import com.ecms.employee.serviceImpl.EmployeeServiceImpl;

@Controller
public class EmployeeResolver {

	@Autowired
	private EmployeeServiceImpl service;
	
	@QueryMapping
	public List<Employee> getAllEmployees() {
		return service.getAllEmployees();
	}
	
	@MutationMapping
	public Employee saveEmployee(@Argument String name, @Argument int age, 
			@Argument float salary, @Argument String company) {
		
		Employee employees = new Employee();
		employees.setName(name);
		employees.setAge(age);
		employees.setSalary(salary);
		employees.setCompany(company);
		
				return service.saveEmployee(employees);
	}
	
	@QueryMapping
	public Employee getEmployeeById(@Argument Long employeeId) {
		return service.getEmployeeById(employeeId);
	}
}
