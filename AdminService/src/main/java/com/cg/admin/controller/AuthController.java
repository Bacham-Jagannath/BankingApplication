package com.cg.admin.controller;

import com.cg.admin.dto.AdminDto;
import com.cg.admin.dto.AuthResponse;
import com.cg.admin.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    AdminService adminService;

    @GetMapping("/welcome")
    public String welcome(){
        return  "welcome to AdminService";
    }
    @Operation(summary = "Login Admin",description = "return admin token")
    @PostMapping("/login")
    public AuthResponse adminLogin(@RequestBody AdminDto adminDto)  {
        return adminService.findByUserNameAndPassword(adminDto.getUsername(),adminDto.getPassword());
    }

    @PostMapping("/create")
    public String adminCreate(@RequestBody AdminDto adminDto)  {
        return adminService.createAdmin(adminDto);
    }

}
