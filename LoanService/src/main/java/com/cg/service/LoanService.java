package com.cg.service;

import com.cg.dto.LoanDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface LoanService {

    public ResponseEntity<?> getAllLoans();

    public LoanDto createLoan(LoanDto loanDto, HttpServletRequest httpServletRequest);

    public LoanDto getLoanById(Long loanId);

    public LoanDto updateLoan(Long loanId, LoanDto loanDetails);
}
