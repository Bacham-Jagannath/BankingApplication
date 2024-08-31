package com.cg.admin.service;

import com.cg.admin.dto.AdminDto;
import com.cg.admin.dto.AuthResponse;
import com.cg.admin.dto.PasswordChangeRequestDto;
import com.cg.admin.dto.StatusUpdateDto;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AdminService  {

    public ResponseEntity<?> updateCustomerByPanNumber(StatusUpdateDto statusUpdateDto);

    public ResponseEntity<?> findAllCustomersByStatus(String registrationReqStatus);

    public AuthResponse findByUserNameAndPassword(String username, String password);

    public String createAdmin(AdminDto adminDto);

    public String updatePasswordChangeRequest(StatusUpdateDto statusUpdateDto);

    public List<PasswordChangeRequestDto> findAllPasswordChangeRequests(String status);
}
