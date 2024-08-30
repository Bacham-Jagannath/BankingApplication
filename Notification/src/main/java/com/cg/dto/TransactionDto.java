package com.cg.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TransactionDto {
    private Long Id;
    private  Long customerId;
    private Long accountId;
    private String transactionStatus;
    private String transactionId;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String updatedBy;

}
