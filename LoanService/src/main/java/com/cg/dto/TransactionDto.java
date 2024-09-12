package com.cg.dto;

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

    public TransactionDto(Long id, String panNumber, Long accountId, String transactionStatus,String transactionId) {
        Id = id;
        this.panNumber = panNumber;
        this.accountId = accountId;
        this.transactionStatus = transactionStatus;
        this.transactionId = transactionId;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.updatedBy = updatedBy;
    }
}
