package com.techgen.model;

import lombok.*;

import javax.persistence.*;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Employee {

    private long id;

    private String firstName;

    private String lastName;

    private String email;
}
