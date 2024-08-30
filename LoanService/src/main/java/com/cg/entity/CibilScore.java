package com.cg.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "cibil_score",schema = "loan_db")
public class CibilScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "pan_number",unique = true)
    private String panNumber;
    @Column(name = "cibil_score")
    private Integer cibilScore;
}
