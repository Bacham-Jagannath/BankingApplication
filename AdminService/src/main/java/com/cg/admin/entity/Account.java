package com.cg.admin.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "account", schema = "auth_db")
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "account_number")
    private String accountNo;

    @Column(name = "account_type")
    private String accountType;

    @Column(name = "current_balance")
    private Double currentBalance;

    @Column(name = "account_source")
    private String accountSource;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pan_number", referencedColumnName = "pan_number")
    @JsonBackReference
    private Customer customer;


}
