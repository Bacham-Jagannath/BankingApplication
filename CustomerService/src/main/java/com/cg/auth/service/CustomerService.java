package com.cg.auth.service;

import com.cg.auth.dto.*;
import com.cg.auth.entity.PasswordChangeRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface CustomerService {
    public String addCustomer(CustomerDto customer) ;
    public AuthResponse findByUserNameAndPassword(String panNumber, String password);

    //public CustomerDto updateCustomer(String panNumber, CustomerDto customerDto, UserPrincipal userPrincipal);
  public ResponseEntity<?> updateCustomer(StatusUpdateDto statusUpdateDto);
    public  CustomerDto getByPanNumber(String panNumber);

    public String createUpdatePasswordRequest(PasswordChangeRequestDto passwordChangeRequestDto);//, UserPrincipal userPrincipal);

    public String updatePasswordReq(StatusUpdateDto statusUpdateDto);//, UserPrincipal userPrincipal);

    public ResponseEntity<?> createLoan(String panNumber, LoanDto loanDto);

    public ResponseEntity<?> getAll(String registrationStatus);

    public List<PasswordChangeRequestDto> getAllPassWordReqStatus(String passwordReqStatus);
}
