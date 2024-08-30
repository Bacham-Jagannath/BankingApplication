package com.cg.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CustomerDto {
    private String panNumber;
    private String firstname;
    private  String lastname;
    private  String username;
    @JsonIgnore
    private String password;
    private String  email;
    private Long mobileNum;
    @JsonIgnore
    private  String role;

    private AccountDto account;

    public CustomerDto(String panNumber, String firstname, String lastname, String username) {
        this.panNumber = panNumber;
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
    }


}
