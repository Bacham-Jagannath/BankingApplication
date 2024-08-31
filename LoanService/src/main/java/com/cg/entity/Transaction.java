package com.cg.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "transaction",schema = "loan_db")
public class Transaction {
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long Id;
    @Column(name = "pan_number")
    private  String panNumber;
    @Column(name = "account_id")
    private Long accountId;
    @Column(name = "transaction_status")
    private String transactionStatus;
    @Column(name = "transaction_id")
    private String transactionId;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "created_on", updatable = false)
    private LocalDateTime createdOn;
    @Column(name = "updated_on")
    private LocalDateTime updatedOn;
    @Column(name = "updated_by")
    private Long updatedBy;

    @OneToOne
    @JoinColumn(name = "loanId")
    @JsonBackReference
    private Loan loan;

}
