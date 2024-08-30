package com.cg.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "loans",schema = "loan_db")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loan_id")
    private Long loanId;

    @Column(name = "loan_amount")
    private Double loanAmount;

    @Column(name = "loan_type")
    private String loanType;

    @Column(name = "status")
    private String status;

    //@NotNull
   // @PastOrPresent(message = "Created date must be in the past or present")
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    //@NotNull
   // @PastOrPresent(message = "Updated date must be in the past or present")
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "pan_number")
    private String panNumber;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "updated_by")
    private  String updatedBy;

    @OneToOne(mappedBy = "loan")
    @JsonManagedReference
    private Transaction transaction;
}
