package com.cg.loans.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class CustomerDto {
    private Long id;
    private String firstname;
    private  String lastname;
    private  String username;
    private String password;
    private String  email;
    private Long mobileNum;
    private  String role;

    public CustomerDto(Long id, String firstname, String lastname, String username) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
    }


}

