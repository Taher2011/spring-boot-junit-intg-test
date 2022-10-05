package com.techgen.repo;

import com.techgen.entity.Employee;
import com.techgen.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class EmployeeRepositoryTests {

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee employee;

    @BeforeEach
    public void setUp() {
        employee = Employee.builder().firstName("Taher").lastName("Ali").email("test@gmail.com").build();
    }

    @DisplayName("Junit for save employee")
    @Test
    public void givenEmployeeObject_whenSave_thenReturnEmployee() {
        Employee saveEmployee = employeeRepository.save(employee);
        assertThat(saveEmployee).isNotNull();
        assertThat(saveEmployee.getId()).isGreaterThan(0);
    }

    @DisplayName("Junit for get all employees")
    @Test
    public void givenEmployeeList_whenFindAll_thenReturnEmployeesList() {
        List<Employee> employees = Arrays.asList(employee,
                Employee.builder().firstName("Vikas").lastName("Patil").email("pest@gmail.com").build());
        employees.stream().forEach(employee ->
                employeeRepository.save(employee)
        );
        List<Employee> employeesFromDB = employeeRepository.findAll();
        assertThat(employeesFromDB).isNotNull();
        assertThat(employeesFromDB.size()).isEqualTo(2);
    }

    @DisplayName("Junit for get employee by id")
    @Test
    public void givenEmployeeObject_whenFindById_thenReturnEmployee() {
        Employee saveEmployee = employeeRepository.save(employee);
        Optional<Employee> optionalEmployeeFromDB = employeeRepository.findById(employee.getId());
        if (optionalEmployeeFromDB.isPresent()) {
            assertThat(optionalEmployeeFromDB).isNotNull();
        }
    }

    @DisplayName("Junit for get employee by email")
    @Test
    public void givenEmployeeEmail_whenFindByEmail_thenReturnEmployee() {
        employeeRepository.save(employee);
        Optional<Employee> optionalEmployeeFromDB = employeeRepository.findByEmail(employee.getEmail());
        if (optionalEmployeeFromDB.isPresent()) {
            assertThat(optionalEmployeeFromDB).isNotNull();
            assertThat(optionalEmployeeFromDB.get().getEmail()).isEqualTo("test@gmail.com");
        }
    }

    @DisplayName("Junit for update employee")
    @Test
    public void givenEmployeeObject_whenUpdateEmployee_thenReturnUpdatedEmployee() {
        employeeRepository.save(employee);
        Optional<Employee> employeeFromBD = employeeRepository.findById(employee.getId());
        if (employeeFromBD.isPresent()) {
            employeeFromBD.get().setEmail("test@yahoo.com");
            employeeFromBD.get().setFirstName("Taheri");
            Employee updatedEmployee = employeeRepository.save(employeeFromBD.get());
            assertThat(updatedEmployee.getEmail()).isEqualTo("test@yahoo.com");
            assertThat(updatedEmployee.getFirstName()).isEqualTo("Taheri");
        }
    }

    @DisplayName("Junit for delete employee")
    @Test
    public void givenEmployeeObject_whenDeleteEmployee_thenRemoveEmployee() {
        employeeRepository.save(employee);
        employeeRepository.delete(employee);
        Optional<Employee> employeeFromDB = employeeRepository.findById(employee.getId());
        assertThat(employeeFromDB).isEmpty();
    }

    @DisplayName("Junit for custom query using JPQL with index param")
    @Test
    public void givenFirstAndLastName_whenFindByJPQLIndexParam_thenReturnEmployee() {
        employeeRepository.save(employee);
        String firstName = "Taher";
        String lastName = "Ali";
        Employee employeeFromDB = employeeRepository.findByFirstNameAndLastName(firstName, lastName);
        assertThat(employeeFromDB).isNotNull();
        assertThat(employeeFromDB.getFirstName()).isEqualTo(firstName);
    }

    @DisplayName("Junit for custom query using JPQL with named param")
    @Test
    public void givenFirstAndLastName_whenFindByJPQLNamedParam_thenReturnEmployee() {
        employeeRepository.save(employee);
        String lastName = "Ali";
        String firstName = "Taher";
        Employee employeeFromDB = employeeRepository.findByLastNameAndFirstName(lastName, firstName);
        assertThat(employeeFromDB).isNotNull();
        assertThat(employeeFromDB.getLastName()).isEqualTo(lastName);
    }

    @DisplayName("Junit for custom query using NativeSQL with index param")
    @Test
    public void givenEmailAndLastName_whenFindByNativeSQLWithIndexParam_thenReturnEmployee() {
        employeeRepository.save(employee);
        String email = "test@gmail.com";
        String lastName = "Ali";
        Employee employeeFromDB = employeeRepository.findByEmailAndLastName(email, lastName);
        assertThat(employeeFromDB).isNotNull();
        assertThat(employeeFromDB.getEmail()).isEqualTo(email);
    }

    @DisplayName("Junit for custom query using NativeSQL with named param")
    @Test
    public void givenLastNameAndEmail_whenFindByNativeSQLWithNamedParam_thenReturnEmployee() {
        employeeRepository.save(employee);
        String lastName = "Ali";
        String email = "test@gmail.com";
        Employee employeeFromDB = employeeRepository.findByLastNameAndEmail(lastName, email);
        assertThat(employeeFromDB).isNotNull();
        assertThat(employeeFromDB.getLastName()).isEqualTo(lastName);
    }

}
