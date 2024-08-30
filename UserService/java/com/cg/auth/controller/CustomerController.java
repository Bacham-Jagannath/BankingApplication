package com.cg.auth.controller;

import com.cg.auth.dto.CustomerDto;
import com.cg.auth.security.CurrentUser;
import com.cg.auth.security.UserPrincipal;
import com.cg.auth.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    CustomerService customerService;

    @PutMapping("/{panNumber}")
    public ResponseEntity<CustomerDto> updateCustomer(@CurrentUser UserPrincipal userPrincipal, @PathVariable("panNumber") String panNumber, @RequestBody CustomerDto customerDto)
    {
        log.info(userPrincipal.getUsername());
       // CustomerDto dto = customerService.updateCustomer(panNumber,customerDto,userPrincipal);
        return ResponseEntity.ok(null);
    }


}
