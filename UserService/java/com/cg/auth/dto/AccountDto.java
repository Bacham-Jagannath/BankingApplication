package com.cg.auth.dto;

import com.cg.auth.entity.Account;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {

    private Long id;
    private String accountNo;
    private String accountType;
    private Double currentBalance;

    private String accountSource;
    @JsonIgnore
    private CustomerDto customerInfo;

    public AccountDto(Long id, String accountNo, String accountType, Double currentBalance, String accountSource) {
        this.id = id;
        this.accountNo = accountNo;
        this.accountType = accountType;
        this.currentBalance = currentBalance;
        this.accountSource = accountSource;
    }
}
