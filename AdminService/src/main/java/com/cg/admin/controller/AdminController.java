package com.cg.admin.controller;

import com.cg.admin.dto.CustomerDto;
import com.cg.admin.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    AdminService adminService;

    @PutMapping("/updateCustomerAccountReq/{panNumber}")
    public ResponseEntity<?> updateCustomerByPanNumber(@PathVariable("panNumber") String panNumber){
        return adminService.updateCustomerByPanNumber(panNumber);

    }

    @GetMapping("/findAllAccountRegCustomer/{registrationReqStatus}")
    public ResponseEntity<?> findAll(@PathVariable("registrationReqStatus") String  registrationReqStatus){
        //List<CustomerDto> customerDtos = adminService.findAll();
        return adminService.findAllAccountRegCustomerByRegStatus(registrationReqStatus);
    }

}
