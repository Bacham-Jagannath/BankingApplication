package com.cg.admin.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "Customer")
@Table(name = "customer", schema = "auth_db")
public class Customer  {
    @Id
    @Column(name = "pan_number")
    private String panNumber;

    @Column(name = "firstname")
    private String firstname;
    @Column(name = "lastname")
    private  String lastname;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;
    @Column(name = "email")
    private String  email;

    @Column(name = "role")
    private  String role;

    @Column(name = "mobile_number")
    private Long mobileNum;

    @Column(name = "registration_ReqStatus")
    private String registrationReqStatus;

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Account account;

    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "updated_by")
    private  String updatedBy;
    @Column(name = "created_on", updatable = false)
    private LocalDateTime createdOn;
    @Column(name = "updated_on")
    private LocalDateTime updatedOn;
}
