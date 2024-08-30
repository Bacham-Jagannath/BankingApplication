package com.cg.admin.exception;

import lombok.Data;

@Data
public class AdminServiceException extends RuntimeException {

    private int statusCode;
    public AdminServiceException(String message) {
        super(message);
    }

    public AdminServiceException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
