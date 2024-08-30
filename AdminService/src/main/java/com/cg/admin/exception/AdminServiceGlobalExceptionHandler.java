package com.cg.admin.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class AdminServiceGlobalExceptionHandler {

    @ExceptionHandler(AdminServiceException.class)
    public ResponseEntity<ErrorResponse> handleGenericError(AdminServiceException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
        return  ResponseEntity.status(ex.getStatusCode() == 0 ? 400 : ex.getStatusCode()).body(errorResponse);
    }
}
