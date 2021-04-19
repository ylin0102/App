package com.harrisburg.app.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "UserInfo")
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "username")
    @NotEmpty(message = "Username is required")
    private String username;

    @Column(name = "password")
    @NotEmpty(message = "Password is required")
    private String password;

    @Column(name = "firstname")
    private String firstname;

    @Column(name = "lastname")
    private String lastname;

    @Column(name = "phone")
    private String phone;
}
