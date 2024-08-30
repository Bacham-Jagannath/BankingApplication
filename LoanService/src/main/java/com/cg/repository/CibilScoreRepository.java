package com.cg.repository;

import com.cg.entity.CibilScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CibilScoreRepository extends JpaRepository<CibilScore,Long> {
    public CibilScore findByPanNumber(String panNumber) ;

}
