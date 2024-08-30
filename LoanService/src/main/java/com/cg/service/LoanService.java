package com.cg.service;

import com.cg.dto.LoanDto;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface LoanService {

    public List<LoanDto> getAllLoans();

    public LoanDto createLoan(LoanDto loanDto, HttpServletRequest httpServletRequest);

    public LoanDto getLoanById(Long loanId);

    public LoanDto updateLoan(Long loanId, LoanDto loanDetails);
}
