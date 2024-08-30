package com.cg.auth.service;

import com.cg.auth.dto.AdminDto;
import com.cg.auth.dto.AuthResponse;
import com.cg.auth.dto.LoginRequest;
import com.cg.auth.entity.Admin;
import com.cg.auth.exception.AuthServiceException;
import com.cg.auth.repository.AdminRepository;
import com.cg.auth.security.TokenServiceProvider;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class AdminServiceImpl implements AdminService{

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private TokenServiceProvider tokenServiceProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public AuthResponse findByUserNameAndPassword(LoginRequest loginRequest) {
        Admin admin =  adminRepository.findByUsernameAndPassword(loginRequest.getUsername(), loginRequest.getPassword()).orElseThrow(() -> new AuthServiceException("Invalid username or password"));
        log.info("Admin Login Successfully");
        String token = tokenGenerator(admin);
        return new AuthResponse(token);
    }

    @Override
    public String createAdmin(AdminDto adminDto) {
        validateIsAdminExist(adminDto);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Admin admin = objectMapper.convertValue(adminDto, Admin.class);
        adminRepository.save(admin);
        return "Admin Successfully Registered";
    }

    private void validateIsAdminExist(AdminDto adminDto) {
        Optional<Admin> byUsername = adminRepository.findByUsername(adminDto.getUsername());
        if (byUsername.isPresent()) {
            log.error("Customer already exist   " + byUsername.get().getUsername());
            throw new AuthServiceException("Customer Username %s is already exist".formatted(adminDto.getUsername()));
        }
    }

    private String tokenGenerator(Admin admin) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", admin.getId());
        map.put("username", admin.getUsername());
        map.put("name", "%s %s".formatted(admin.getFirstname(), admin.getLastname()).trim());
        map.put("role", admin.getRole());
        return tokenServiceProvider.generateToken(map).getToken();
    }
}
