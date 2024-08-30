package com.cg.auth.security;

import com.cg.auth.entity.Customer;
import com.cg.auth.exception.OAuth2AuthenticationProcessingException;
import com.cg.auth.repository.CustomerRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        log.info("loadUser");
        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        log.info("processOAuth2User");

        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(oAuth2UserRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());
        if(StringUtils.isEmpty(oAuth2UserInfo.getUserName())) {
            throw new OAuth2AuthenticationProcessingException("Username not found from OAuth2 provider");
        }

        Optional<Customer> userOptional = customerRepo.findByUsername(oAuth2UserInfo.getUserName());

        Customer customer = null;
        if(userOptional.isPresent()) {
            customer = userOptional.get();
            setAuthentication(oAuth2UserInfo);
            customer = updateExistingUser(customer, oAuth2UserInfo);
        } else {
            setAuthentication(oAuth2UserInfo);
            customer = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }
        return UserPrincipal.create(customer, oAuth2User.getAttributes());
    }

    private Customer registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        Customer customer = new Customer();

        customer.setFirstname(oAuth2UserInfo.getFirstName());
        customer.setLastname(oAuth2UserInfo.getLastName());
        customer.setUsername(oAuth2UserInfo.getUserName());
        customer.setEmail(oAuth2UserInfo.getEmail());
        customer.setRole("USER");
        customer = customerRepo.save(customer);

        return customer;
    }

    private Customer updateExistingUser(Customer existingCustomer, OAuth2UserInfo oAuth2UserInfo) {
        existingCustomer.setFirstname(oAuth2UserInfo.getFirstName());
        existingCustomer.setLastname(oAuth2UserInfo.getLastName());
        return customerRepo.save(existingCustomer);
    }

    private void setAuthentication(OAuth2UserInfo oAuth2UserInfo){

        Customer customer = new Customer();
        customer.setUsername(oAuth2UserInfo.getUserName());
        customer.setFirstname(oAuth2UserInfo.getFirstName());
        customer.setLastname(oAuth2UserInfo.getLastName());

        SecurityContext context = SecurityContextHolder.createEmptyContext();

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                customer, null, customer.getAuthorities());
        context.setAuthentication(usernamePasswordAuthenticationToken);
        SecurityContextHolder.setContext(context);
    }

}
