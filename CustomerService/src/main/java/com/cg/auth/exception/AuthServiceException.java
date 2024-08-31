package com.cg.auth.exception;

import lombok.Data;

@Data
public class AuthServiceException extends RuntimeException {

    private int statusCode;
    public AuthServiceException(String message) {
        super(message);
    }

    public AuthServiceException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
