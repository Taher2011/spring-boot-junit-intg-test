package com.techgen.service.impl;

import com.techgen.exception.EmployeeNotFoundException;
import com.techgen.model.Employee;
import com.techgen.repository.EmployeeRepository;
import com.techgen.service.EmployeeService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    private final ModelMapper modelMapper;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, ModelMapper modelMapper) {
        this.employeeRepository = employeeRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Employee saveEmployee(Employee employee) {
        Optional<com.techgen.entity.Employee> employeeFromDB = employeeRepository.findByEmail(employee.getEmail());
        if (employeeFromDB.isPresent()) {
            throw new EmployeeNotFoundException("employee already exist wih given email : " + employee.getEmail());
        }
        com.techgen.entity.Employee employeeE = modelMapper.map(employee, com.techgen.entity.Employee.class);
        com.techgen.entity.Employee savedEmployee = employeeRepository.save(employeeE);
        employee = modelMapper.map(savedEmployee, Employee.class);
        return employee;
    }

    @Override
    public List<Employee> getEmployees() {
        List<com.techgen.entity.Employee> employeesE = employeeRepository.findAll();
        List<Employee> employees = new ArrayList<>();
        employeesE.forEach(e -> {
            Employee employee = modelMapper.map(e, Employee.class);
            employees.add(employee);
        });
        return employees;
    }

    @Override
    public Optional<Employee>  getEmployeeById(long id) {
        Optional<Employee> employee = Optional.empty();
        Optional<com.techgen.entity.Employee> employeeOptional = employeeRepository.findById(id);
        if (employeeOptional.isPresent()) {
            com.techgen.entity.Employee employeeE = employeeOptional.get();
            employee = Optional.of(modelMapper.map(employeeE, Employee.class));
        }
        return employee;
    }

    @Override
    public Employee updateEmployee(Employee employee) {
        com.techgen.entity.Employee employeeE = modelMapper.map(employee, com.techgen.entity.Employee.class);
        com.techgen.entity.Employee savedEmployee = employeeRepository.save(employeeE);
        employee = modelMapper.map(savedEmployee, Employee.class);
        return employee;
    }

    @Override
    public void deleteEmployee(long id) {
        employeeRepository.deleteById(id);
    }
}
