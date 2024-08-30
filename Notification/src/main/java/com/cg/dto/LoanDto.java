package com.cg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanDto {
    private Long loanId;

    private Double loanAmount;

    private String loanType;
    private String status;
    private CustomerDto customerDto;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    private TransactionDto transaction;


    private Long customerId;

}
