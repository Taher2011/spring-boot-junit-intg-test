package com.techgen.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techgen.model.Employee;
import com.techgen.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    public void givenEmpObj_whenCreateEmp_thenReturnSavedEmp() throws Exception {
        Employee employee = Employee.builder().firstName("Taher").lastName("Ali").email("taher@gmail.com").build();
        String employeeJsonReq = objectMapper.writeValueAsString(employee);
        given(employeeService.saveEmployee(any(Employee.class))).willAnswer((invocation -> invocation.getArgument(0)));
        ResultActions response = mockMvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON).content(employeeJsonReq));
        response.andDo(print()).andExpect(status().isCreated()).andExpect(jsonPath("$.firstName", is(employee.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(employee.getLastName()))).
                andExpect(jsonPath("$.email", is(employee.getEmail())));
    }

    @Test
    public void givenListOfEmpObj_whenGetEmps_thenReturnEmps() throws Exception {
        List<Employee> employees = List.of(Employee.builder().firstName("Taher").lastName("Ali").email("taher@gmail.com").build(),
                Employee.builder().firstName("Jai").lastName("Yadaav").email("jai@gmail.com").build());
        given(employeeService.getEmployees()).willReturn(employees);
        ResultActions response = mockMvc.perform(get("/api/employees"));
        response.andDo(print()).andExpect(status().isOk()).
                andExpect(jsonPath("$.size()", is(employees.size())));
    }

    @Test
    public void givenEmpId_whenGetEmpById_thenReturnEmp() throws Exception {
        long empId = 1L;
        Employee employee = Employee.builder().id(empId).firstName("Taher").lastName("Ali").email("taher@gmail.com").build();
        given(employeeService.getEmployeeById(1L)).willReturn(Optional.of(employee));
        ResultActions response = mockMvc.perform(get("/api/employees/{id}", empId));
        response.andDo(print()).andExpect(status().isOk()).
                andExpect(jsonPath("$.firstName", is(employee.getFirstName()))).
                andExpect(jsonPath("$.lastName", is(employee.getLastName()))).
                andExpect(jsonPath("$.email", is(employee.getEmail())));
    }

    @Test
    public void givenInvalidEmpId_whenGetEmpById_thenReturnEmptyEmp() throws Exception {
        long empId = 1L;
        given(employeeService.getEmployeeById(1L)).willReturn(Optional.empty());
        ResultActions response = mockMvc.perform(get("/api/employees/{id}", empId));
        response.andDo(print()).andExpect(status().isNotFound());
    }

    @Test
    public void givenUpdatedEmp_whenUpdateEmp_thenReturnUpdatedEmp() throws Exception {
        long empId = 1L;
        Employee savedEmployee = Employee.builder().id(empId).firstName("Taher").lastName("Ali").email("taher@gmail.com").build();
        Employee updatedEmployee = Employee.builder().id(empId).firstName("Taheri").lastName("Aly").email("taheri@gmail.com").build();
        given(employeeService.getEmployeeById(empId)).willReturn(Optional.of(savedEmployee));
        given(employeeService.updateEmployee(savedEmployee)).willReturn(updatedEmployee);
        //   given(employeeService.updateEmployee(any(Employee.class))).willAnswer((invocation -> invocation.getArgument(0)));
        String employeeJsonReq = objectMapper.writeValueAsString(updatedEmployee);
        ResultActions response = mockMvc.perform(put("/api/employees/{id}", empId).contentType(MediaType.APPLICATION_JSON).content(employeeJsonReq));
        response.andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.firstName", is(updatedEmployee.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(updatedEmployee.getLastName()))).
                andExpect(jsonPath("$.email", is(updatedEmployee.getEmail())));
    }

    @Test
    public void givenUpdatedEmp_whenUpdateEmp_thenReturn404() throws Exception {
        long empId = 1L;
        Employee savedEmployee = Employee.builder().id(empId).firstName("Taher").lastName("Ali").email("taher@gmail.com").build();
        Employee updatedEmployee = Employee.builder().id(empId).firstName("Taheri").lastName("Aly").email("taheri@gmail.com").build();
        given(employeeService.getEmployeeById(empId)).willReturn(Optional.empty());
        String employeeJsonReq = objectMapper.writeValueAsString(updatedEmployee);
        ResultActions response = mockMvc.perform(put("/api/employees/{id}", empId).contentType(MediaType.APPLICATION_JSON).content(employeeJsonReq));
        response.andDo(print()).andExpect(status().isNotFound());
    }

    @Test
    public void givenEmpId_whenDeleteEmp_thenReturn200() throws Exception {
        long empId = 1L;
        willDoNothing().given(employeeService).deleteEmployee(empId);
        ResultActions response = mockMvc.perform(delete("/api/employees/{id}", empId));
        response.andDo(print()).andExpect(status().isOk());
    }
}
