package com.cg.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanDto {
    private Long loanId;
    private Double loanAmount;
    private String loanType;
    private String loanStatus;

    private String panNumber;

}
