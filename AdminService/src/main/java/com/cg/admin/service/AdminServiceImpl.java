package com.cg.admin.service;

import com.cg.admin.dto.AdminDto;
import com.cg.admin.dto.AuthResponse;
import com.cg.admin.dto.CustomerDto;
import com.cg.admin.entity.Admin;
import com.cg.admin.exception.AdminServiceException;
import com.cg.admin.repository.AdminRepository;
import com.cg.admin.repository.CustomerRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownContentTypeException;

import java.util.*;

@Service
@Slf4j
@Transactional
public class AdminServiceImpl implements AdminService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AdminRepository adminRepository;



    //@Autowired
   // private TokenServiceProvider tokenServiceProvider;
//
//    @Autowired
//    private TokenAuthenticationFilter tokenAuthenticationFilter;

    @Autowired
    private HttpServletRequest request;

    @Value("${app.user-service}")
    private String userServiceUrl;

    @Override
    public AuthResponse findByUserNameAndPassword(String username, String password) {
        Admin admin = adminRepository.findByUsernameAndPassword(username, password).orElseThrow(() -> new AdminServiceException("Invalid username or password"));
        AdminDto adminDto= objectMapper.convertValue(admin,AdminDto.class);
        log.info("Admin Login Successfully");
       String token = tokenGenerator(admin);
        return new AuthResponse(token);
                //ResponseEntity.ok(adminDto); //
    }

    @Override
    public String createAdmin(AdminDto adminDto) {
        validateIsAdminExist(adminDto);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Admin admin = objectMapper.convertValue(adminDto, Admin.class);
        adminRepository.save(admin);
        return "Admin Successfully Registered";
    }

    @Override
    public ResponseEntity<?> findAllAccountRegCustomerByRegStatus(String registrationReqStatus) {
//        Claims tokenDetails = getTokenDetails(request);
//        if (!tokenDetails.get("role").equals("ADMIN")) {
//            throw new AdminServiceException("UnAuthorized", 403);
//        }
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8081/auth/getAllCustomers/{registrationReqStatus}";
        ResponseEntity<List<CustomerDto>> response;
        if (registrationReqStatus.equalsIgnoreCase("ALL")) {

            response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<CustomerDto>>() {
                    },
                    registrationReqStatus
            );

        } else {
            response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<CustomerDto>>() {
                    },
                    registrationReqStatus
            );
            ;
        }

        return ResponseEntity.ok(registrationReqStatus.equalsIgnoreCase("ALL") ? response.getBody() : response.getBody());
    }


    @Override
    public ResponseEntity<?> updateCustomerByPanNumber(String panNumber) {
        CustomerDto customerDto = checkCustomerAndGetId(panNumber);
        //Claims tokenDetails = getTokenDetails(request);
        //if (tokenDetails.get("role").equals("ADMIN")) {
        customerDto.setRegistrationReqStatus("APPROVED");
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8081/auth/updateRegistrationStatus/{panNumber}";
        // Create the request entity with the body
        HttpEntity<CustomerDto> requestEntity = new HttpEntity<>(customerDto);

        // Make the PUT request to update the customer
        ResponseEntity<CustomerDto> response = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                requestEntity,
                CustomerDto.class,
                panNumber
        );

        // Return the updated CustomerDto from the response
        return ResponseEntity.ok(response.getBody());
    }
           // return ResponseEntity.ok("Account Created SuccessFully");
//        } else {
//            throw new AdminServiceException("You don't have Permission to update the Details   ", 403);
//        }



//    private Claims getTokenDetails(HttpServletRequest request) {
//        String token = tokenAuthenticationFilter.getJwtFromRequest(request);
//        Claims claims = tokenServiceProvider.extractAllClaims(token);
//        return claims;
//    }

    private void validateIsAdminExist(AdminDto adminDto) {
        Optional<Admin> byUsername = adminRepository.findByUsername(adminDto.getUsername());
        if (byUsername.isPresent()) {
            log.error("Customer already exist   " + byUsername.get().getUsername());
            throw new AdminServiceException("Customer Username %s is already exist".formatted(adminDto.getUsername()));
        }
    }

    private String tokenGenerator(Admin admin) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", admin.getId());
        map.put("username", admin.getUsername());
        map.put("name", "%s %s".formatted(admin.getFirstname(), admin.getLastname()).trim());
        map.put("role", admin.getRole());
        return null;//tokenServiceProvider.generateToken(map).getToken();
    }

    private CustomerDto checkCustomerAndGetId(String panNumber) {

        try {
            RestTemplate restTemplate = new RestTemplate();
            CustomerDto customerDto = restTemplate
                    .getForObject(userServiceUrl + "/%s".formatted(panNumber), CustomerDto.class);
            return customerDto;
        } catch (HttpClientErrorException | UnknownContentTypeException | HttpServerErrorException e) {
            throw new AdminServiceException("Customer not found");
        }
    }

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        return null;
//    }
}
