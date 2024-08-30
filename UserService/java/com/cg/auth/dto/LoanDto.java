package com.cg.auth.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanDto {
    private Long loanId;

    @NotNull(message = "loan amount should not be null")
    private Double loanAmount;

    private String loanType;
    private String status;
    private CustomerDto customerDto;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

   private TransactionDto transaction;

    private String panNumber;

//    public LoanDto(Loan loan){
//        this.loanId = loan.getLoanId();
//        this.loanType = loan.getLoanType();
//        this.loanAmount = loan.getLoanAmount();
//        this.createdDate = loan.getCreatedDate();
//        this.updatedDate = loan.getUpdatedDate();
//        this.status = loan.getStatus();
//        this.panNumber = loan.getPanNumber();
//    }
}
