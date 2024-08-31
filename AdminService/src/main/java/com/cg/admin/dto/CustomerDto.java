package com.cg.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {

    private String panNumber;
    private String firstname;
    private  String lastname;
    private String password;
    private String  email;
    private Long mobileNum;
    private  String role;
    private AccountDto account;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    private String status;
    private String createdBy;
    private String updatedBy;

    public String getPanNumber() {
        return panNumber!=null && !panNumber.isBlank()?panNumber.toUpperCase():panNumber;
    }

}
