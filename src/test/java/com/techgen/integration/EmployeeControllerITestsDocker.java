package com.techgen.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techgen.model.Employee;
import com.techgen.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
public class EmployeeControllerITestsDocker {

    @Container
    private static MySQLContainer mySQLContainer = new MySQLContainer("mysql:latest");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ModelMapper modelMapper;

    private com.techgen.entity.Employee employeeE;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
        employeeE = com.techgen.entity.Employee.builder().firstName("Taher").lastName("Ali").email("taher@gmail.com").build();
    }

    @Test
    public void printDockerContainerDetails() {
        System.out.println(mySQLContainer.getUsername());
        System.out.println(mySQLContainer.getPassword());
        System.out.println(mySQLContainer.getDatabaseName());
        System.out.println(mySQLContainer.getJdbcUrl());
        assert(true);
    }

    @Test
    public void givenEmpObj_whenCreateEmp_thenReturnSavedEmp() throws Exception {
        Employee employee = Employee.builder().firstName("Taher").lastName("Ali").email("taher@gmail.com").build();
        String employeeJsonReq = objectMapper.writeValueAsString(employee);
        ResultActions response = mockMvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON).content(employeeJsonReq));
        response.andDo(print()).andExpect(status().isCreated()).andExpect(jsonPath("$.firstName", is(employee.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(employee.getLastName()))).
                andExpect(jsonPath("$.email", is(employee.getEmail())));
    }

    @Test
    public void givenListOfEmpObj_whenGetEmps_thenReturnEmps() throws Exception {
        List<Employee> employees = List.of(Employee.builder().firstName("Taher").lastName("Ali").email("taher@gmail.com").build(),
                Employee.builder().firstName("Jai").lastName("Yadaav").email("jai@gmail.com").build());
        List<com.techgen.entity.Employee> employeesE = new ArrayList<>();
        employees.forEach(e -> {
            com.techgen.entity.Employee employee = modelMapper.map(e, com.techgen.entity.Employee.class);
            employeesE.add(employee);
        });
        employeeRepository.saveAll(employeesE);
        ResultActions response = mockMvc.perform(get("/api/employees"));
        response.andDo(print()).andExpect(status().isOk()).
                andExpect(jsonPath("$.size()", is(employees.size())));
    }

    @Test
    public void givenEmpId_whenGetEmpById_thenReturnEmp() throws Exception {
        employeeE = employeeRepository.save(employeeE);
        long empId = employeeE.getId();
        Employee employee = Employee.builder().id(empId).firstName("Taher").lastName("Ali").email("taher@gmail.com").build();
        ResultActions response = mockMvc.perform(get("/api/employees/{id}", empId));
        response.andDo(print()).andExpect(status().isOk()).
                andExpect(jsonPath("$.firstName", is(employee.getFirstName()))).
                andExpect(jsonPath("$.lastName", is(employee.getLastName()))).
                andExpect(jsonPath("$.email", is(employee.getEmail())));
    }

    @Test
    public void givenInvalidEmpId_whenGetEmpById_thenReturnEmptyEmp() throws Exception {
        long empId = 1L;
        employeeRepository.save(employeeE);
        ResultActions response = mockMvc.perform(get("/api/employees/{id}", empId));
        response.andDo(print()).andExpect(status().isNotFound());
    }

    @Test
    public void givenUpdatedEmp_whenUpdateEmp_thenReturnUpdatedEmp() throws Exception {
        employeeE = employeeRepository.save(employeeE);
        com.techgen.entity.Employee employeeUE = com.techgen.entity.Employee.builder().firstName("Taheri").lastName("Aly").email("taheri@gmail.com").build();
        Employee employeeM = modelMapper.map(employeeUE, Employee.class);
        String employeeJsonReq = objectMapper.writeValueAsString(employeeM);
        ResultActions response = mockMvc.perform(put("/api/employees/{id}", employeeE.getId()).contentType(MediaType.APPLICATION_JSON).content(employeeJsonReq));
        response.andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.firstName", is(employeeM.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(employeeM.getLastName()))).
                andExpect(jsonPath("$.email", is(employeeM.getEmail())));
    }

    @Test
    public void givenUpdatedEmp_whenUpdateEmp_thenReturn404() throws Exception {
        long empId = 1L;
        employeeE = employeeRepository.save(employeeE);
        com.techgen.entity.Employee employeeUE = com.techgen.entity.Employee.builder().firstName("Taheri").lastName("Aly").email("taheri@gmail.com").build();
        Employee employeeM = modelMapper.map(employeeUE, Employee.class);
        String employeeJsonReq = objectMapper.writeValueAsString(employeeM);
        ResultActions response = mockMvc.perform(put("/api/employees/{id}", empId).contentType(MediaType.APPLICATION_JSON).content(employeeJsonReq));
        response.andDo(print()).andExpect(status().isNotFound());
    }

    @Test
    public void givenEmpId_whenDeleteEmp_thenReturn200() throws Exception {
        employeeE = employeeRepository.save(employeeE);
        ResultActions response = mockMvc.perform(delete("/api/employees/{id}", employeeE.getId()));
        response.andDo(print()).andExpect(status().isOk());
    }
}
