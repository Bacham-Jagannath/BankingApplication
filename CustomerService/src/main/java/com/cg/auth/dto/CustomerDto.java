package com.cg.auth.dto;

import com.cg.auth.entity.Customer;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    private PasswordChangeRequestDto passWordRequest;

    private LocalDateTime createdOn;

    private LocalDateTime updatedOn;

    private String status;
    private String createdBy;

    private String updatedBy;

    public CustomerDto(String panNumber){
        this.panNumber=panNumber;
    }

    public CustomerDto(Customer info){
        this.panNumber = info.getPanNumber();
        this.firstname = info.getFirstname();
        this.lastname = info.getLastname();
        this.password = null;
        this.email= info.getEmail();
        this.role = info.getRole();
        this.mobileNum = info.getMobileNum();
        this.createdOn=info.getCreatedOn();
        this.updatedOn = info.getUpdatedOn();
        this.status=info.getStatus();
        this.createdBy = info.getCreatedBy();
        this.updatedBy=info.getUpdatedBy();
        this.account=new
                AccountDto(info.getAccount().getId(),info.getAccount().getAccountNo(),
                info.getAccount().getAccountType(),info.getAccount().getCurrentBalance(),
                info.getAccount().getAccountSource());
    }

    public String getPanNumber() {
        return panNumber!=null && !panNumber.isBlank()?panNumber.toUpperCase():panNumber;
    }

}
