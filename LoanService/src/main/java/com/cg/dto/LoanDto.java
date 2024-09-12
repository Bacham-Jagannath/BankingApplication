package com.cg.dto;

import com.cg.entity.Loan;
import jakarta.validation.constraints.DecimalMin;
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

    private Double loanAmount;

    private String loanType;
    private String status;
    private CustomerDto customerDto;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;

   private TransactionDto transaction;

    private String panNumber;

    public LoanDto(Loan loan){
        this.loanId = loan.getLoanId();
        this.loanType = loan.getLoanType();
        this.loanAmount = loan.getLoanAmount();
        this.createdOn = loan.getCreatedOn();
        this.updatedOn = loan.getUpdatedOn();
        this.status = loan.getStatus();
        this.panNumber = loan.getPanNumber();
    }
}
