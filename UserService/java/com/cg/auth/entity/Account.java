package com.cg.auth.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "account_table", schema = "auth_db")
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "account_no")
    private String accountNo;

    @Column(name = "account_type")
    private String accountType;

    @Column(name = "current_balance")
    private  Double currentBalance;

    @Column(name = "account_source")
    private String accountSource;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pan_number", referencedColumnName = "pan_number")
    @JsonBackReference
    private Customer customer;

}
