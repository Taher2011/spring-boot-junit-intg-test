package com.techgen.exception;

public class EmployeeNotFoundException extends RuntimeException {

    private String message;

    public EmployeeNotFoundException(String message) {
        super(message);
    }

    public EmployeeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
