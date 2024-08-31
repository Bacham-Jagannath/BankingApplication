package com.cg.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "password_change_request", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"pan_number"})
},schema = "auth_db")
public class PasswordChangeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "pan_number", unique = true)
    private String panNumber;

    @Column(name = "password")
    private String password;

    @Column(name = "status")
    private String status;

    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    private String createdBy;
    private String updatedBy;
}
