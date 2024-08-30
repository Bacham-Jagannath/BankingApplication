package com.cg.auth.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminDto {

    private Long id;
    private String firstname;
    private  String lastname;
    private  String username;
    private String password;
    private String  email;
    private Long mobileNum;
    private  String role;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

}
