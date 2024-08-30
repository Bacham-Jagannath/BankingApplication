package com.cg.admin.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "Admin")
@Table(name = "admin", schema = "auth_db")
public class Admin {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "firstname")
    private String firstname;
    @Column(name = "lastname")
    private  String lastname;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;
    @Column(name = "email")
    private String  email;

    @Column(name = "role")
    private  String role;

    @Column(name = "mobile_number")
    private Long mobileNum;


}
