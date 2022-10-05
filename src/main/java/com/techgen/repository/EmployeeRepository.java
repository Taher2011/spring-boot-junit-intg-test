package com.techgen.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techgen.entity.Employee;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmail(String email);

    //JPQL with index params
    @Query("select e from Employee e where e.firstName = ?1 and e.lastName= ?2")
    Employee findByFirstNameAndLastName(String firstName, String lastName);

    //JPQL with named params
    @Query("select e from Employee e where e.lastName = :lName and e.firstName= :fName")
    Employee findByLastNameAndFirstName(@Param("lName") String lastName, @Param("fName") String firstName);

    //Native query with index params
    @Query(value = "select * from employees e where e.email = ?1 and e.last_name = ?2", nativeQuery = true)
    Employee findByEmailAndLastName(String email, String lastName);

    //Native query with named params
    @Query(value = "select * from employees e where e.last_Name = :lName and e.email = :email", nativeQuery = true)
    Employee findByLastNameAndEmail(@Param("lName") String lastName, @Param("email") String email);
}
