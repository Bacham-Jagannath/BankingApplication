package com.cg.auth.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TransactionDto {
    private Long Id;
    private  String panNumber;
    private Long accountId;
    private String transactionStatus;
    private String transactionId;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String updatedBy;

}
