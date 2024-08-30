package com.cg.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {

    private Long id;
    private Long accountNo;
    private String accountType;
    private Double currentBalance;

    private String accountSource;
    @JsonIgnore
    private CustomerDto customerInfo;
}
