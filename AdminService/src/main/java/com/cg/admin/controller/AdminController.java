package com.cg.admin.controller;

import com.cg.admin.dto.LoanDto;
import com.cg.admin.dto.PasswordChangeRequestDto;
import com.cg.admin.dto.StatusUpdateDto;
import com.cg.admin.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    AdminService adminService;

    @GetMapping("/findAllCustomer/{status}")
    public ResponseEntity<?> findAllAccountRegCustomer(@PathVariable("status") String  status){
        return adminService.findAllCustomersByStatus(status);
    }
    @PutMapping("/updateCustomer")
    public ResponseEntity<?> updateCustomerByPanNumber(@RequestBody StatusUpdateDto statusUpdateDto){
        return adminService.updateCustomerByPanNumber(statusUpdateDto);
    }

    @GetMapping("/findAllPasswordChangeRequests/{status}")
    public ResponseEntity<List<PasswordChangeRequestDto>> findAllPasswordChangeRequests(@PathVariable("status") String  status){
        return ResponseEntity.ok(adminService.findAllPasswordChangeRequests(status));
    }

    @PutMapping("/updatePasswordChangeRequest")
    public ResponseEntity<String> updatePasswordChangeRequest(@RequestBody StatusUpdateDto statusUpdateDto){
        return ResponseEntity.ok(adminService.updatePasswordChangeRequest(statusUpdateDto));
    }

    @GetMapping("/getAllCreatedLoans")
    public  ResponseEntity<?> getAllCreatedLoans(){
        return adminService.getAllCreatedLoans();
    }

    @PutMapping("/updateLoanById/{loanId}")
    public LoanDto updateLoanById(@PathVariable Long loanId,
                                  @Valid @RequestBody LoanDto loanDetails){
            return adminService.updateLoanById(loanId,loanDetails);
    }
}
