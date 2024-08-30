package com.cg.exception;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoanServiceException extends RuntimeException {
    private int statusCode;
    public LoanServiceException(String message) {
        super(message);
    }
    public LoanServiceException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
