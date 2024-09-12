package com.cg.controller;

import com.cg.dto.LoanDto;
import com.cg.service.LoanService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

@RestController
@RequestMapping("/loans")
public class LoanController {

    @Autowired
    LoanService loanService;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to Loan Service";
    }

    @PostMapping
    public LoanDto createLoan(@Valid @RequestBody LoanDto loanDto, HttpServletRequest httpServletRequest) {
        return loanService.createLoan(loanDto, httpServletRequest);
    }

    @GetMapping
    public ResponseEntity<?> getAllLoans() {

        return loanService.getAllLoans();
    }

    @GetMapping("/{loanId}")
    public LoanDto getLoanById(@PathVariable Long loanId) {
        return loanService.getLoanById(loanId);
    }

    @PutMapping("/{loanId}")
    public LoanDto updateLoan(@PathVariable Long loanId, @Valid @RequestBody LoanDto loanDetails) {
        LoanDto loanObj = loanService.updateLoan(loanId, loanDetails);
        return loanObj;
    }


}
