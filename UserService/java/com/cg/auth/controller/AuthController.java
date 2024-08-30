package com.cg.auth.controller;

import com.cg.auth.dto.*;
import com.cg.auth.entity.PassWordChangeRequest;
import com.cg.auth.exception.AuthServiceException;
import com.cg.auth.service.AdminService;
import com.cg.auth.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AdminService adminService;

    @GetMapping("/welcome")
    public String welcome(){
        return  "welcome to AuthService";
    }

    @PostMapping("/create")
    public ResponseEntity<String> registerCustomer(@RequestBody CustomerDto customerDto) throws AuthServiceException {
        String s = customerService.addCustomer(customerDto);
        return ResponseEntity.ok(s);
    }

    @GetMapping("/getAllRegisterCustomers/{registrationStatus}")
    public  ResponseEntity<?> getAllCustomers(@PathVariable("registrationStatus") String registrationStatus)
    {
        return customerService.getAll(registrationStatus);
    }

    @GetMapping("/{panNumber}")
    public CustomerDto getByPanNumber(@PathVariable("panNumber") String panNumber){
        return customerService.getByPanNumber(panNumber);
    }

    @PutMapping("/updateRegistrationStatus/{panNumber}")
    public ResponseEntity<?> updateCustomerAccount(@PathVariable("panNumber") String panNumber,
                                     @RequestBody CustomerDto customerDto)
    {
        return customerService.updateCustomer(panNumber, customerDto);
    }

    @PostMapping("/login")
    public AuthResponse loginCustomer(@RequestBody CustomerDto customerDto)  {
        return customerService.findByUserNameAndPassword(customerDto.getPanNumber(),customerDto.getPassword());
    }


    @PutMapping("/updatePasswordReq/{panNumber}")
    public String updatePasswordReq(@PathVariable("panNumber") String panNumber, @RequestBody PassWordChangeRequest passWordRequest)//, UserPrincipal userPrincipal)
    {
        return customerService.updatePasswordReq(panNumber,passWordRequest.getPassword());//,userPrincipal);
    }

    @GetMapping("/getAllPasswordReq/{passwordReqStatus}")
    public ResponseEntity<?> getAllPassWordReqStatus(@PathVariable("passwordReqStatus") String passwordReqStatus)
    {
        return customerService.getAllPassWordReqStatus(passwordReqStatus);
    }

    @PostMapping("/createLoan/{panNumber}")
    public String createLoan(@PathVariable("panNumber") String panNumber,@RequestBody LoanDto loanDto){
        return customerService.createLoan(panNumber,loanDto);
    }

//    @PostMapping("/admin/login")
//    public AuthResponse adminLogin(@RequestBody LoginRequest loginRequest)  {
//        return adminService.findByUserNameAndPassword(loginRequest);
//    }
//
//    @PostMapping("/admin/create")
//    public String adminCreate(@RequestBody AdminDto adminDto)  {
//        return adminService.createAdmin(adminDto);
//    }
}
