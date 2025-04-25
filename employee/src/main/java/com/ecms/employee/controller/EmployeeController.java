package com.ecms.employee.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ecms.employee.entity.Employee;
import com.ecms.employee.serviceImpl.EmployeeServiceImpl;

@Controller
public class EmployeeController {

	@Autowired
	private EmployeeServiceImpl serviceImpl;

	@GetMapping("/api")
	private Employee getEmployeeById(@RequestParam Long id) {
		return serviceImpl.getEmployeeById(id);
	}
}
