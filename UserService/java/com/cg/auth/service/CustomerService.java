package com.cg.auth.service;

import com.cg.auth.dto.AuthResponse;
import com.cg.auth.dto.CustomerDto;
import com.cg.auth.dto.LoanDto;
import com.cg.auth.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface CustomerService  extends UserDetailsService {
    public String addCustomer(CustomerDto customer) ;
    public AuthResponse findByUserNameAndPassword(String panNumber, String password);

    //public CustomerDto updateCustomer(String panNumber, CustomerDto customerDto, UserPrincipal userPrincipal);
  public ResponseEntity<?> updateCustomer(String panNumber,CustomerDto customerDto);
    public  CustomerDto getByPanNumber(String panNumber);

    public String updatePasswordReq(String panNumber, String password);//, UserPrincipal userPrincipal);

    public String createLoan(String panNumber, LoanDto loanDto);

    public ResponseEntity<?> getAll(String registrationStatus);

    public   ResponseEntity<?> getAllPassWordReqStatus(String passwordReqStatus);
}
