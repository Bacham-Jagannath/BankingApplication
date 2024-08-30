package com.cg.admin.service;

import com.cg.admin.dto.AdminDto;
import com.cg.admin.dto.AuthResponse;
import com.cg.admin.dto.CustomerDto;
import org.springframework.http.ResponseEntity;

public interface AdminService  {

    public ResponseEntity<?> updateCustomerByPanNumber(String panNumber);

    public ResponseEntity<?> findAllAccountRegCustomerByRegStatus(String registrationReqStatus);

    public AuthResponse findByUserNameAndPassword(String username, String password);

    public String createAdmin(AdminDto adminDto);
}
