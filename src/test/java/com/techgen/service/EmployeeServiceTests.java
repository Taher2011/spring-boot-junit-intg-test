package com.techgen.service;

import com.techgen.exception.EmployeeNotFoundException;
import com.techgen.model.Employee;
import com.techgen.repository.EmployeeRepository;
import com.techgen.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTests {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employeeM;

    private com.techgen.entity.Employee employeeE;

    @BeforeEach
    public void setUp() {
        employeeM = Employee.builder().id(1L).firstName("Taher").lastName("Ali").email("taher@gmail.com").build();
        employeeE = com.techgen.entity.Employee.builder().id(1L).firstName("Taher").lastName("Ali").email("taher@gmail.com").build();
    }

    @DisplayName("Junit for save employee")
    @Test
    public void givenEmpObj_whenSaveEmp_thenReturnEmp() {
        given(employeeRepository.findByEmail(employeeM.getEmail())).willReturn(Optional.empty());
        given(modelMapper.map(employeeM, com.techgen.entity.Employee.class)).willReturn(employeeE);
        given(employeeRepository.save(employeeE)).willReturn(employeeE);
        given(modelMapper.map(employeeE, Employee.class)).willReturn(employeeM);
        Employee savedEmployee = employeeService.saveEmployee(employeeM);
        assertThat(savedEmployee).isNotNull();
        assertThat(savedEmployee.getId()).isEqualTo(1L);
    }

    @DisplayName("Junit for save employee which throws exception")
    @Test
    public void givenExistingEmail_whenSaveEmp_thenThrowsException() {
        given(employeeRepository.findByEmail(employeeM.getEmail())).willReturn(Optional.of(employeeE));
        org.junit.jupiter.api.Assertions.assertThrows(EmployeeNotFoundException.class, () -> employeeService.saveEmployee(employeeM));
    }

    @DisplayName("Junit for getAll employees")
    @Test
    public void givenEmpList_whenGetAllEmp_thenReturnEmpList() {
        List<com.techgen.entity.Employee> employeeE1 = List.of(employeeE, com.techgen.entity.Employee.builder().id(2L).firstName("Amit").lastName("Sharma").email("amit@gmail.com").build());
        List<Employee> employeeM1 = List.of(employeeM, Employee.builder().id(2L).firstName("Amit").lastName("Sharma").email("amit@gmail.com").build());
        given(employeeRepository.findAll()).willReturn(employeeE1);
        final int[] i = {0};
        employeeE1.forEach(e -> {
            given(modelMapper.map(e, Employee.class)).willReturn(employeeM1.get(i[0]));
            i[0]++;
        });
        List<Employee> employees1 = employeeService.getEmployees();
        assertThat(employees1).isNotNull();
        assertThat(employees1.size()).isEqualTo(2);
    }

    @DisplayName("Junit for getAll employees(negative scenario)")
    @Test
    public void givenEmptyEmpList_whenGetAllEmp_thenReturnEmptyEmpList() {
        given(employeeRepository.findAll()).willReturn(Collections.emptyList());
        List<Employee> employees = employeeService.getEmployees();
        assertThat(employees).isEmpty();
        assertThat(0).isEqualTo(0);
    }

    @DisplayName("Junit for get employee by id")
    @Test
    public void givenEmpId_whenGetEmpById_thenReturnEmp() {
        given(employeeRepository.findById(1L)).willReturn(Optional.of(employeeE));
        given(modelMapper.map(employeeE, Employee.class)).willReturn(employeeM);
        Optional<Employee> employeeFromDB = employeeService.getEmployeeById(employeeM.getId());
        assertThat(employeeFromDB).isNotNull();
        assertThat(employeeFromDB.get().getId()).isEqualTo(1);
    }

    @DisplayName("Junit for update employee")
    @Test
    public void givenEmpObj_whenUpdateEmp_thenReturnUpdatedEmp() {
        given(modelMapper.map(employeeM, com.techgen.entity.Employee.class)).willReturn(employeeE);
        given(employeeRepository.save(employeeE)).willReturn(employeeE);
        employeeM.setFirstName("taheri");
        employeeM.setEmail("taher@yahoo.com");
        given(modelMapper.map(employeeE, Employee.class)).willReturn(employeeM);
        Employee updatedEmployee = employeeService.updateEmployee(employeeM);
        assertThat(updatedEmployee).isNotNull();
        assertThat(updatedEmployee.getFirstName()).isEqualTo("taheri");
        assertThat(updatedEmployee.getEmail()).isEqualTo("taher@yahoo.com");
    }

    @DisplayName("Junit for delete employee")
    @Test
    public void givenEmpId_whenDeleteEmp_thenNothing() {
        long empId = 1L;
        willDoNothing().given(employeeRepository).deleteById(empId);
        employeeService.deleteEmployee(empId);
        verify(employeeRepository, times(1)).deleteById(empId);
    }
}
