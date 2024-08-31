package com.cg.auth.controller;

import com.cg.auth.dto.CustomerDto;
import com.cg.auth.dto.LoanDto;
import com.cg.auth.dto.PasswordChangeRequestDto;
import com.cg.auth.dto.StatusUpdateDto;
import com.cg.auth.entity.PasswordChangeRequest;
import com.cg.auth.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    CustomerService customerService;

    @PutMapping("/{panNumber}")
    public ResponseEntity<CustomerDto> updateCustomer(@PathVariable("panNumber") String panNumber, @RequestBody CustomerDto customerDto) {
        // CustomerDto dto = customerService.updateCustomer(panNumber,customerDto,userPrincipal);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/getAllCustomersByStatus/{status}")
    public ResponseEntity<?> getAllCustomersByStatus(@PathVariable("status") String status) {
        return customerService.getAll(status);
    }

    @GetMapping("/{panNumber}")
    public CustomerDto getByPanNumber(@PathVariable("panNumber") String panNumber) {
        return customerService.getByPanNumber(panNumber);
    }

    @PutMapping("/updateCustomer")
    public ResponseEntity<?> updateCustomerAccount(@RequestBody StatusUpdateDto statusUpdateDto) {
        return customerService.updateCustomer(statusUpdateDto);
    }

    @PostMapping("/createPasswordChangeRequest")
    public String createPasswordChangeRequest(@RequestBody PasswordChangeRequestDto passwordChangeRequestDto)//, UserPrincipal userPrincipal)
    {
        return customerService.createUpdatePasswordRequest(passwordChangeRequestDto);
    }

    @PutMapping("/updatePasswordChangeRequest")
    public String updatePasswordChangeRequest(@RequestBody StatusUpdateDto statusUpdateDto) {
        return customerService.updatePasswordReq(statusUpdateDto);
    }

    @GetMapping("/getAllPasswordReq/{status}")
    public ResponseEntity<List<PasswordChangeRequestDto>> getAllPassWordReqStatus(@PathVariable("status") String status) {
        return ResponseEntity.ok(customerService.getAllPassWordReqStatus(status));
    }

    @PostMapping("/createLoan/{panNumber}")
    public ResponseEntity<?> createLoan(@PathVariable("panNumber") String panNumber, @RequestBody LoanDto loanDto) {
        return customerService.createLoan(panNumber, loanDto);
    }

}
