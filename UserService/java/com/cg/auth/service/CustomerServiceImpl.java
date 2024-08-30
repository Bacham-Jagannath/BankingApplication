package com.cg.auth.service;

import com.cg.auth.dto.AuthResponse;
import com.cg.auth.dto.CustomerDto;
import com.cg.auth.dto.LoanDto;
import com.cg.auth.dto.PassWordRequestDto;
import com.cg.auth.entity.Account;
import com.cg.auth.entity.Customer;
import com.cg.auth.entity.PassWordChangeRequest;
import com.cg.auth.exception.AuthServiceException;
import com.cg.auth.repository.AccountRepo;
import com.cg.auth.repository.CustomerRepo;
import com.cg.auth.repository.PassWordRepository;
import com.cg.auth.security.TokenAuthenticationFilter;
import com.cg.auth.security.TokenServiceProvider;
import com.cg.auth.security.UserPrincipal;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
@Transactional
public class CustomerServiceImpl implements CustomerService,UserDetailsService {

    @Autowired
    CustomerRepo customerRepo;

    @Autowired
    AccountRepo accountRepo;

    @Autowired
    PassWordRepository passWordRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TokenServiceProvider tokenServiceProvider;

    @Autowired
    private TokenAuthenticationFilter tokenAuthenticationFilter;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private HttpServletRequest request;



    @Override
    public String addCustomer(CustomerDto customerDto) throws AuthServiceException {
        validateIsCustomerExist(customerDto);

        if (customerDto.getAccount() == null) {
            throw new AuthServiceException("Account details are should not be empty", 400);
        }
        Account account = getAccountInfo(customerDto);
        Customer customer = customerRepo.save(getCustomerInfo(customerDto));
        account.setCustomer(customer);
        PassWordChangeRequest passWordRequest = new PassWordChangeRequest();
        passWordRequest.setCustomer(customer);
        passWordRequest.setPassword(passwordEncoder.encode(customerDto.getPassword()));
        passWordRepository.save(passWordRequest);
        accountRepo.save(account);
        log.info("Customer Registration Created Successfully");
        return "Customer Registration Created Successfully";

    }

    @Override
    public AuthResponse findByUserNameAndPassword(String panNumber, String password) {
        //Optional<Customer> customer = customerRepo.findByPanNumber(panNumber);
        Optional<Customer> customer = Optional.ofNullable(customerRepo.findByPanNumber(panNumber).orElseThrow(() -> new AuthServiceException("User not found")));

        Optional<PassWordChangeRequest> passWordRequest = Optional.ofNullable(passWordRepository.findByPanNumber(panNumber)
                .orElseThrow(() -> new AuthServiceException("User not found")));
        if (!customer.get().getRegistrationReqStatus().equalsIgnoreCase("APPROVED")
                &&  !passwordEncoder.matches(password, customer.get().getPassword())
                    && !passwordEncoder.matches(password, passWordRequest.get().getPassword())) {
            log.error("Registration is not Approved for this Customer/Bad Credentials", panNumber);
            throw new AuthServiceException("Registration is not Approved for this Customer".formatted(panNumber));
        }
        log.info("Customer Login Successfully");
        String token = tokenGenerator(customer.get());
        return new AuthResponse(token);
    }

    @Override
    public ResponseEntity<?> getAll(String registrationStatus) {
        List<CustomerDto> customerDtoList = null;
        CustomerDto customerDto;
        if(registrationStatus.equalsIgnoreCase("ALL")){
            List<Customer> customerList = customerRepo.findAll();
            customerDtoList = customerList.stream().map(CustomerServiceImpl::convertToCustomerDto)
                    .collect(Collectors.toList());
        }
        else
        {
            List<Customer> customer = customerRepo.getCustomerByRegistrationStatus(registrationStatus);
            customerDtoList = customer.stream().map(CustomerServiceImpl::convertToCustomerDto)
                    .collect(Collectors.toList());;
        }

        return ResponseEntity.ok(registrationStatus.equalsIgnoreCase("ALL")?customerDtoList:customerDtoList);
    }

    @Override
    public ResponseEntity<?> updateCustomer(String panNumber, CustomerDto customerDto) {
     //   Claims tokenDetails = getTokenDetails(request);
//        if (!tokenDetails.get("role").equals("ADMIN")) {
//            throw new AuthServiceException("You don't have Permission to update Customer Details", 403);
//        }
        Customer customer = customerRepo.findByPanNumber(panNumber).orElseThrow(() -> new AuthServiceException("User not found"));
        customer.setRegistrationReqStatus(customerDto.getRegistrationReqStatus());
        customer.setUpdatedBy("ADMIN");
        Customer saveCustomer = customerRepo.save(customer);
        CustomerDto updatedCustomerDto = objectMapper.convertValue(saveCustomer,CustomerDto.class);
        updatedCustomerDto.setPassword(null);
        return ResponseEntity.ok(updatedCustomerDto);
    }

    @Override
    public CustomerDto getByPanNumber(String panNumber) {
        Customer customer = customerRepo.findByPanNumber(panNumber).orElseThrow(() -> new AuthServiceException("User not found"));
        CustomerDto customerDto = objectMapper.convertValue(customer, CustomerDto.class);
        customerDto.setPassword(null);
        return customerDto;
    }
    @Override
    public String updatePasswordReq(String panNumber, String password){ //},UserPrincipal userPrincipal) {
//        if (userPrincipal.getPanNumber() != panNumber) {
//            throw new AuthServiceException("Unauthorized", 401);
//        }
        PassWordChangeRequest passWordRequest = passWordRepository.findByPanNumber(panNumber).orElseThrow(() -> new AuthServiceException("User not found"));
        passWordRequest.setPassword(passwordEncoder.encode(password));
        passWordRequest.setPasswordReqStatus("CREATED");
        passWordRepository.save(passWordRequest);
        return "Password Update request Created SuccessFully";
    }

    @Override
    public ResponseEntity<?> getAllPassWordReqStatus(String passwordReqStatus) {
        List<PassWordRequestDto> passWordRequestDtos = null;
        PassWordRequestDto passWordRequestDto = null;
        if (passwordReqStatus.equalsIgnoreCase("ALL")){
            List<PassWordChangeRequest> passWordRequestList = passWordRepository.findAll();
            passWordRequestDtos = passWordRequestList.stream().
                    map(CustomerServiceImpl::convertToPassWordReqDto).
                    collect(Collectors.toList());
        }
        else {
            Optional<PassWordChangeRequest> passWordRequest =
                                passWordRepository.getByPassWordReqByStatus(passwordReqStatus);
            passWordRequestDto = objectMapper.convertValue(passWordRequest,PassWordRequestDto.class);
        }
        return ResponseEntity.ok(passwordReqStatus.equalsIgnoreCase("ALL")?passWordRequestDtos:passWordRequestDto);
    }

    private static CustomerDto convertToCustomerDto(Customer customer) {

        return new CustomerDto(customer);
    }
    private static PassWordRequestDto convertToPassWordReqDto(PassWordChangeRequest passWordRequest) {
        return new PassWordRequestDto(passWordRequest);
    }

    @Override
    public String createLoan(String panNumber, LoanDto loanDto) {
        Customer customer = customerRepo.findByPanNumber(panNumber).orElseThrow(() -> new AuthServiceException("User not found"));
        if (!customer.getRegistrationReqStatus().equalsIgnoreCase("APPROVED")) {
            log.error("Registration is not Approved for this Customer", panNumber);
            throw new AuthServiceException("Registration is not Approved for this Customer".formatted(panNumber));
        }
        return "Loan Created Successfully";
    }

    private Customer getCustomerInfo(CustomerDto customerDto) {
        Customer customer = new Customer();
        customer.setFirstname(customerDto.getFirstname());
        customer.setLastname(customerDto.getLastname());
        customer.setRegistrationReqStatus("CREATED");
        customer.setEmail(customerDto.getEmail());
        customer.setUsername(customerDto.getUsername());
        customer.setPassword(passwordEncoder.encode(customerDto.getPassword()));
        customer.setRole(customerDto.getRole());
        customer.setMobileNum(customerDto.getMobileNum());
        customer.setPanNumber(customerDto.getPanNumber());
        customer.setCreatedBy(customerDto.getPanNumber());
        return customer;
    }

    private Account getAccountInfo(CustomerDto customerDto) {
        Account account = new Account();
        account.setAccountNo(customerDto.getAccount().getAccountNo());
        account.setAccountType(customerDto.getAccount().getAccountType());
        account.setAccountSource(customerDto.getAccount().getAccountSource());
        account.setCurrentBalance(customerDto.getAccount().getCurrentBalance());
        return account;
    }

    private void validateIsCustomerExist(CustomerDto customerDto) {
        Optional<Customer> customer = customerRepo.findByPanNumber(customerDto.getPanNumber());
        if (customer.isPresent()) {
            log.error("Customer already exist   " + customerDto.getUsername());
            throw new AuthServiceException("Customer PAN %s is already exist".formatted(customerDto.getPanNumber()));
        }
    }


    private String tokenGenerator(Customer customer) {
        Map<String, Object> map = new HashMap<>();
        map.put("panNumber", customer.getPanNumber());
        map.put("id", customer.getPanNumber());
        map.put("username", customer.getUsername());
        map.put("name", "%s %s".formatted(customer.getFirstname(), customer.getLastname()).trim());
        map.put("role", customer.getRole());
        return tokenServiceProvider.generateToken(map).getToken();
    }

    private Claims getTokenDetails(HttpServletRequest request){
            String token = tokenAuthenticationFilter.getJwtFromRequest(request);
            Claims claims = tokenServiceProvider.extractAllClaims(token);
            return claims;
        }

//    @Override
//    public CustomerDto updateCustomer(String panNumber, CustomerDto customerDto, UserPrincipal userPrincipal) {
//        if (userPrincipal.getPanNumber() != panNumber) {
//            throw new AuthServiceException("Unauthorized", 401);
//        }
//        Customer customer = customerRepo.findByPanNumber(panNumber).
//                orElseThrow(() -> new AuthServiceException("Customer Id is Not Present:  " + panNumber));
//        customer.setFirstname(customerDto.getFirstname());
//        customer.setLastname(customerDto.getLastname());
//        customer.setEmail(customerDto.getEmail());
//        customer.setMobileNum(customerDto.getMobileNum());
//        if (!ObjectUtils.isEmpty(customerDto.getAccount())) {
//            Account account = new Account();
//            account.setCustomer(customer);
//            account.setId(customerDto.getAccount().getId());
//            account.setAccountSource(customerDto.getAccount().getAccountSource());
//            account.setAccountType(customerDto.getAccount().getAccountType());
//            account.setAccountNo(customerDto.getAccount().getAccountNo());
//            account.setCurrentBalance(customerDto.getAccount().getCurrentBalance());
//            customer.setAccount(account);
//        }
//        if (!ObjectUtils.isEmpty(customerDto.getPassword())) {
//            customer.setPassword(passwordEncoder.encode(customerDto.getPassword()));
//        }
//        Customer Customer1 = customerRepo.save(customer);
//        CustomerDto customerDto1 = objectMapper.convertValue(Customer1, CustomerDto.class);
//        customerDto1.setPassword(null);
//        return customerDto1;
//    }

//        if (!customer.isPresent() && !passwordEncoder.matches(password,customer.get().getPassword())) {
//            log.error("Customer does not exist/Password doesn't matched {}", panNumber);
//            throw new AuthServiceException("Customer does not exist/Password doesn't matched %s".formatted(panNumber));
//        }
//        if (!passwordEncoder.matches(password,customer.get().getPassword()))
//        {
//            log.error("Password doesn't matched {}",panNumber);
//            throw new AuthServiceException("Password doesn't matched".formatted(panNumber));
//        }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String userName)
            throws UsernameNotFoundException {
        Customer customer = customerRepo.findByUsername(userName)
                .orElseThrow(() ->
                        new AuthServiceException("User not found: %s".formatted(userName)));
        return UserPrincipal.create(customer);
    }
}
