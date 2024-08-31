package com.cg.auth.service;

import com.cg.auth.dto.AdminDto;
import com.cg.auth.dto.AuthResponse;
import com.cg.auth.dto.LoginRequest;

public interface AdminService {
    public AuthResponse findByUserNameAndPassword(LoginRequest loginRequest);

    public String createAdmin(AdminDto adminDto);

}
