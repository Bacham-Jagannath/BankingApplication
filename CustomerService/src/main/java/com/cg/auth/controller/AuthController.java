package com.cg.auth.controller;

import com.cg.auth.dto.AuthResponse;
import com.cg.auth.dto.CustomerDto;
import com.cg.auth.exception.AuthServiceException;
import com.cg.auth.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/welcome")
    public String welcome(){
        return  "welcome to AuthService";
    }

    @PostMapping("/create")
    public ResponseEntity<String> registerCustomer(@RequestBody CustomerDto customerDto) throws AuthServiceException {
        String s = customerService.addCustomer(customerDto);
        return ResponseEntity.ok(s);
    }


    @PostMapping("/login")
    public AuthResponse loginCustomer(@RequestBody CustomerDto customerDto)  {
        return customerService.findByUserNameAndPassword(customerDto.getPanNumber(),customerDto.getPassword());
    }
}
