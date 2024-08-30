package com.cg.apigateway.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @RequestMapping("/fallback")
    public String authFallback() {
        return "Auth Service is currently unavailable. Please try again later.";
    }

    @RequestMapping("/loan")
    public String loanFallback() {
        return "Loan Service is currently unavailable. Please try again later.";
    }
}
