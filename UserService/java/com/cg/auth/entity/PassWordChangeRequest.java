package com.cg.auth.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "PassWordRequest")
@Table(name = "password_table",schema = "auth_db")
public class PassWordChangeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "password")
    private String password;

    @Column(name = "passwordReqStatus")
    private String passwordReqStatus;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pan_number", referencedColumnName = "pan_number")
    @JsonBackReference
    private Customer customer;
}
