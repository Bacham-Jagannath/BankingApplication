package com.cg.repository;

import com.cg.entity.Loan;
import org.apache.catalina.LifecycleState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan,Long> {

    @Query("select l from Loan l where l.loanType=:loanType AND l.panNumber=:panNumber AND l.status in('CREATED', 'IN_PROGRESS')")
    public Loan getLoan(@Param("loanType") String loanType, @Param("panNumber") String panNumber);

    @Query("select l from Loan l where l.status =:status")
    public List<Loan> getByLoanStatus(@Param("status") String status);
}
