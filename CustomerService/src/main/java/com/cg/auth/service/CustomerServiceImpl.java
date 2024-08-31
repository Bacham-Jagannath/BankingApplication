package com.cg.auth.service;

import com.cg.auth.config.JwtUtil;
import com.cg.auth.dto.*;
import com.cg.auth.entity.Account;
import com.cg.auth.entity.Customer;
import com.cg.auth.entity.PasswordChangeRequest;
import com.cg.auth.exception.AuthServiceException;
import com.cg.auth.repository.AccountRepo;
import com.cg.auth.repository.CustomerRepo;
import com.cg.auth.repository.PasswordChangeRequestRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
@Transactional
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    CustomerRepo customerRepo;

    @Autowired
    AccountRepo accountRepo;

    @Autowired
    PasswordChangeRequestRepository passwordChangeRequestRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenService tokenService;

    @Value("${app.loan-service:http://localhost:8085}")
    private String loanServiceUrl;

    @Override
    public String addCustomer(CustomerDto customerDto) throws AuthServiceException {
        validateIsCustomerExist(customerDto);

        if (customerDto.getAccount() == null) {
            throw new AuthServiceException("Account details are should not be empty", 400);
        }
        Account account = getAccountInfo(customerDto);
        Customer customer = customerRepo.save(getCustomerInfo(customerDto));
        account.setCustomer(customer);
        accountRepo.save(account);
        log.info("Customer Registration Created Successfully");
        return "Customer Registration Created Successfully";
    }

    @Override
    public AuthResponse findByUserNameAndPassword(String panNumber, String password) {
        //Optional<Customer> customer = customerRepo.findByPanNumber(panNumber);
        Customer customer = customerRepo.findByPanNumber(panNumber).orElseThrow(() -> new AuthServiceException("User not found"));

        if (!customer.getStatus().equalsIgnoreCase("APPROVED")) {
            throw new AuthServiceException("Customer %s is not Approved".formatted(panNumber));
        } else {
            if (passwordEncoder.matches(password, customer.getPassword())) {
                log.info("Customer Login Successfully");
                String token = tokenGenerator(customer);
                return new AuthResponse(token);
            } else {
                throw new AuthServiceException("Password might changed for this customer %s. Please try again".formatted(panNumber), 400);
            }
        }
    }

    @Override
    public ResponseEntity<?> getAll(String registrationStatus) {
        List<CustomerDto> customerDtoList = null;
        if (registrationStatus.equalsIgnoreCase("ALL")) {
            List<Customer> customerList = customerRepo.findAll();
            customerDtoList = customerList.stream().map(CustomerServiceImpl::convertToCustomerDto)
                    .collect(Collectors.toList());
        } else {
            List<Customer> customer = customerRepo.getCustomerByRegistrationStatus(registrationStatus);
            customerDtoList = customer.stream().map(CustomerServiceImpl::convertToCustomerDto)
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(customerDtoList);
    }

    @Override
    public ResponseEntity<?> updateCustomer(StatusUpdateDto statusUpdateDto) {

        // TODO Check
        try {
            log.info("Current User {}", objectMapper.writeValueAsString(SecurityContextHolder.getContext().getAuthentication().getPrincipal()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Customer customer = customerRepo.findByPanNumber(statusUpdateDto.getPanNumber()).orElseThrow(() -> new AuthServiceException("User not found"));
        customer.setStatus(statusUpdateDto.getStatus());
        customer.setUpdatedBy(tokenService.getCustomClaimFromToken("id"));
        Customer saveCustomer = customerRepo.save(customer);
        CustomerDto updatedCustomerDto = objectMapper.convertValue(saveCustomer, CustomerDto.class);
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
    @Transactional
    public String createUpdatePasswordRequest(PasswordChangeRequestDto passwordChangeRequestDto) {

        Customer customer = customerRepo.findByPanNumber(passwordChangeRequestDto.getPanNumber()).orElseThrow(() -> new AuthServiceException("Customer not found"));

        try {
            PasswordChangeRequest passwordChangeRequest = new PasswordChangeRequest();
            passwordChangeRequest.setPassword(passwordEncoder.encode(passwordChangeRequestDto.getPassword()));
            passwordChangeRequest.setStatus(passwordChangeRequestDto.getStatus());
            passwordChangeRequest.setPanNumber(customer.getPanNumber());
            passwordChangeRequest.setCreatedBy(tokenService.getCustomClaimFromToken("id"));
            passwordChangeRequestRepository.save(passwordChangeRequest);
        }catch (Exception ex){
            throw new AuthServiceException(ex.getMessage(), 400);
        }
        return "Request for password change received";
    }

    @Override
    public String updatePasswordReq(StatusUpdateDto statusUpdateDto) { //},UserPrincipal userPrincipal) {

        if (tokenService.isAdmin()) {
            PasswordChangeRequest passwordChangeRequest = passwordChangeRequestRepository.findByPanNumber(statusUpdateDto.getPanNumber()).orElseThrow(() -> new AuthServiceException("User not found"));

            if (!passwordChangeRequest.getStatus().equalsIgnoreCase(StatusEnum.APPROVED.name()) || !passwordChangeRequest.getStatus().equalsIgnoreCase(StatusEnum.REJECTED.name())) {

                Customer customer = customerRepo.findByPanNumber(statusUpdateDto.getPanNumber()).orElseThrow(() -> new AuthServiceException("Customer not found"));
                customer.setPassword(passwordChangeRequest.getPassword());
                customer.setUpdatedBy(tokenService.getCustomClaimFromToken("id"));
                customerRepo.save(customer);

                passwordChangeRequest.setPassword(null);
                passwordChangeRequest.setStatus(StatusEnum.APPROVED.name());
                passwordChangeRequest.setUpdatedBy(tokenService.getCustomClaimFromToken("id"));
                passwordChangeRequestRepository.save(passwordChangeRequest);
            } else {
                throw new AuthServiceException("The request id: %d has been %s already".formatted(passwordChangeRequest.getId(), passwordChangeRequest.getStatus()), 400);
            }
        } else {
            throw new AuthServiceException("Unauthorized", 401);
        }
        return "Password Change request Updated SuccessFully";
    }

    @Override
    public List<PasswordChangeRequestDto> getAllPassWordReqStatus(String passwordReqStatus) {

        List<PasswordChangeRequest> passWordRequestList;

        if (passwordReqStatus.equalsIgnoreCase("ALL")) {
            passWordRequestList = passwordChangeRequestRepository.findAll();
        } else {
            passWordRequestList = passwordChangeRequestRepository.findAllByStatus(passwordReqStatus);
        }
        return passWordRequestList.stream().
                map(CustomerServiceImpl::convertToPassWordReqDto).
                collect(Collectors.toList());
    }

    private static CustomerDto convertToCustomerDto(Customer customer) {

        return new CustomerDto(customer);
    }

    private static PasswordChangeRequestDto convertToPassWordReqDto(PasswordChangeRequest passWordRequest) {
        return new PasswordChangeRequestDto(passWordRequest);
    }

    @Override
    public ResponseEntity<?> createLoan(String panNumber, LoanDto loanDto) {

        Customer customer = customerRepo.findByPanNumber(panNumber).orElseThrow(() -> new AuthServiceException("Customer not found"));

        if (!customer.getStatus().equalsIgnoreCase("APPROVED")) {
            log.error("Customer {} is not allowed to create loan due to not approved", panNumber);
            throw new AuthServiceException("Customer %s is not allowed to create loan due to not approved".formatted(panNumber));
        }

        String token = tokenService.getCurrentToken();

        RestTemplate restTemplate = new RestTemplate();
        String url = "%s/loans".formatted(loanServiceUrl);

        // Create headers and add Authorization
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer %s".formatted(token));

        // Create the request entity with the body
        HttpEntity<LoanDto> requestEntity = new HttpEntity<>(loanDto, headers);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        ResponseEntity<LoanDto> response;

        try {
            response = restTemplate.exchange(
                    builder.buildAndExpand().toUri(), // Expand path variable
                    HttpMethod.POST,
                    requestEntity,
                    LoanDto.class
            );
            // Handle response if needed
        } catch (HttpClientErrorException | HttpServerErrorException | ResourceAccessException e) {
            // Rethrow the same exception type
            throw new AuthServiceException(e.getMessage(), 400);
        } catch (RestClientException e) {
            // Rethrow a more general RestClientException if needed
            throw new AuthServiceException(e.getMessage(), 400);
        }

        return ResponseEntity.ok(response.getBody());
    }

    private Customer getCustomerInfo(CustomerDto customerDto) {
        Customer customer = new Customer();
        customer.setFirstname(customerDto.getFirstname());
        customer.setLastname(customerDto.getLastname());
        customer.setStatus(StatusEnum.CREATED.name());
        customer.setEmail(customerDto.getEmail());
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
            log.error("Customer already exist   " + customerDto.getPanNumber());
            throw new AuthServiceException("Customer PAN %s is already exist".formatted(customerDto.getPanNumber()));
        }
    }


    private String tokenGenerator(Customer customer) {
        Map<String, Object> map = new HashMap<>();
        map.put("panNumber", customer.getPanNumber());
        map.put("id", customer.getPanNumber());
        map.put("username", customer.getPanNumber());
        map.put("name", "%s %s".formatted(customer.getFirstname(), customer.getLastname()).trim());
        map.put("role", customer.getRole());
        return jwtUtil.createToken(map, "JWT Validator Token");
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
}
