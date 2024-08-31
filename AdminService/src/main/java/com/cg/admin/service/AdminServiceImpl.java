package com.cg.admin.service;

import com.cg.admin.config.JwtUtil;
import com.cg.admin.dto.*;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.*;
import org.springframework.web.util.UriComponentsBuilder;

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

    @Autowired
    private TokenService tokenService;

    //@Autowired
    // private TokenServiceProvider tokenServiceProvider;
//
//    @Autowired
//    private TokenAuthenticationFilter tokenAuthenticationFilter;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${app.auth.customerServiceUrl}")
    private String customerServiceUrl;

    @Override
    public AuthResponse findByUserNameAndPassword(String username, String password) {
        Admin admin = adminRepository.findByUsernameAndPassword(username, password).orElseThrow(() -> new AdminServiceException("Invalid username or password"));
        log.info("Admin Login Successfully");
        String token = tokenGenerator(admin);
        return new AuthResponse(token);
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
    public String updatePasswordChangeRequest(StatusUpdateDto statusUpdateDto) {

        String token = tokenService.getCurrentToken();

        RestTemplate restTemplate = new RestTemplate();
        String url = "%s/customer/updatePasswordChangeRequest".formatted(customerServiceUrl);

        // Create headers and add Authorization
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer %s".formatted(token));

        // Create the request entity with the body
        HttpEntity<StatusUpdateDto> requestEntity = new HttpEntity<>(statusUpdateDto, headers);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        ResponseEntity<String> response;
        try {
            // Perform the HTTP PUT request
            response = restTemplate.exchange(
                    builder.buildAndExpand().toUri(), // Expand path variable
                    HttpMethod.PUT,
                    requestEntity,
                    String.class
            );
            // Handle response if needed
        } catch (HttpClientErrorException | HttpServerErrorException | ResourceAccessException e) {
            // Rethrow the same exception type
            throw new AdminServiceException(e.getMessage(), 400);
        } catch (RestClientException e) {
            // Rethrow a more general RestClientException if needed
            throw new AdminServiceException(e.getMessage(), 400);
        }
        return response.getBody();
    }

    @Override
    public List<PasswordChangeRequestDto> findAllPasswordChangeRequests(String status) {

        if (!tokenService.isAdmin()) {
            throw new AdminServiceException("Unauthorized", 401);
        }

        String token = tokenService.getCurrentToken();
        Map<String, Object> tokenDetails = tokenService.getAdminDetailsByToken();

        RestTemplate restTemplate = new RestTemplate();
        String url = "%s/customer/getAllPasswordReq/{status}".formatted(customerServiceUrl);
        ResponseEntity<List<PasswordChangeRequestDto>> response;

        // Create headers and add Authorization
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer %s".formatted(token));

        // Create an HttpEntity with the headers
        HttpEntity<String> entity = new HttpEntity<>(headers);
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        try {
            response = restTemplate.exchange(
                    builder.buildAndExpand(status).toUri(), // Expand path variable
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<PasswordChangeRequestDto>>() {
                    }
            );
            // Handle response if needed
        } catch (HttpClientErrorException | HttpServerErrorException | ResourceAccessException e) {
            // Rethrow the same exception type
            throw new AdminServiceException(e.getMessage(), 400);
        } catch (RestClientException e) {
            // Rethrow a more general RestClientException if needed
            throw new AdminServiceException(e.getMessage(), 400);
        }

        return response.getBody();
    }

    private String tokenGenerator(Admin admin) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", admin.getId());
        map.put("username", admin.getUsername());
        map.put("role", admin.getRole());
        return jwtUtil.createToken(map, "JWT Validator Token");
    }

    @Override
    public ResponseEntity<?> findAllCustomersByStatus(String status) {

        if (!tokenService.isAdmin()) {
            throw new AdminServiceException("Unauthorized", 401);
        }

        String token = tokenService.getCurrentToken();
        Map<String, Object> tokenDetails = tokenService.getAdminDetailsByToken();

        RestTemplate restTemplate = new RestTemplate();
        String url = "%s/customer/getAllCustomersByStatus/{registrationReqStatus}".formatted(customerServiceUrl);
        ResponseEntity<List<CustomerDto>> response;

        // Create headers and add Authorization
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer %s".formatted(token));

        // Create an HttpEntity with the headers
        HttpEntity<String> entity = new HttpEntity<>(headers);
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        try {
            response = restTemplate.exchange(
                    builder.buildAndExpand(status).toUri(), // Expand path variable
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<CustomerDto>>() {
                    }
            );
            // Handle response if needed
        } catch (HttpClientErrorException | HttpServerErrorException | ResourceAccessException e) {
            // Rethrow the same exception type
            throw new AdminServiceException(e.getMessage(), 400);
        } catch (RestClientException e) {
            // Rethrow a more general RestClientException if needed
            throw new AdminServiceException(e.getMessage(), 400);
        }
        return ResponseEntity.ok(response.getBody());
    }


    @Override
    public ResponseEntity<?> updateCustomerByPanNumber(StatusUpdateDto statusUpdateDto) {

        checkCustomerAndGetId(statusUpdateDto.getPanNumber());

        String token = tokenService.getCurrentToken();

        RestTemplate restTemplate = new RestTemplate();
        String url = "%s/customer/updateCustomer".formatted(customerServiceUrl);

        // Create headers and add Authorization
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer %s".formatted(token));

        // Create the request entity with the body
        HttpEntity<StatusUpdateDto> requestEntity = new HttpEntity<>(statusUpdateDto, headers);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        ResponseEntity<CustomerDto> postResponse;
        try {
            // Perform the HTTP PUT request
            postResponse = restTemplate.exchange(
                    builder.buildAndExpand().toUri(), // Expand path variable
                    HttpMethod.PUT,
                    requestEntity,
                    CustomerDto.class
            );
            // Handle response if needed
        } catch (HttpClientErrorException | HttpServerErrorException | ResourceAccessException e) {
            // Rethrow the same exception type
            throw new AdminServiceException(e.getMessage(), 400);
        } catch (RestClientException e) {
            // Rethrow a more general RestClientException if needed
            throw new AdminServiceException(e.getMessage(), 400);
        }

        // Return the updated CustomerDto from the response
        return ResponseEntity.ok(postResponse.getBody());
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
            log.error("Admin already exist " + byUsername.get().getUsername());
            throw new AdminServiceException("Admin is %s already exist".formatted(adminDto.getUsername()));
        }
    }

    private CustomerDto checkCustomerAndGetId(String panNumber) {

        try {

            RestTemplate restTemplate = new RestTemplate();
            String url = "%s/customer/{panNumber}".formatted(customerServiceUrl);
            ResponseEntity<CustomerDto> response;

            // Create headers and add Authorization
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer %s".formatted(tokenService.getCurrentToken()));


            // Create an HttpEntity with the headers
            HttpEntity<String> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
            try {
                response = restTemplate.exchange(
                        builder.buildAndExpand(panNumber).toUri(), // Expand path variable
                        HttpMethod.GET,
                        entity,
                        CustomerDto.class
                );
                // Handle response if needed
            } catch (HttpClientErrorException | HttpServerErrorException | ResourceAccessException e) {
                // Rethrow the same exception type
                throw new AdminServiceException(e.getMessage(), 400);
            } catch (RestClientException e) {
                // Rethrow a more general RestClientException if needed
                throw new AdminServiceException(e.getMessage(), 400);
            }

            return response.getBody();
        } catch (HttpClientErrorException | UnknownContentTypeException | HttpServerErrorException e) {
            throw new AdminServiceException("Customer not found");
        }
    }

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        return null;
//    }
}
