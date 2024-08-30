package com.cg.auth.controller;

import com.cg.auth.entity.Customer;
import com.cg.auth.repository.CustomerRepo;
import com.cg.auth.security.CurrentUser;
import com.cg.auth.security.TokenServiceProvider;
import com.cg.auth.security.UserPrincipal;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class SocialLoginController {

    private static String authorizationRequestBaseUri
            = "oauth2/authorization";
    Map<String, String> oauth2AuthenticationUrls
            = new HashMap<>();

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;
    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    private CustomerRepo customerRepo;
    @Autowired
    private TokenServiceProvider tokenServiceProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/oauth_login")
    public String getLoginPage(Model model) {
        Iterable<ClientRegistration> clientRegistrations = null;
        ResolvableType type = ResolvableType.forInstance(clientRegistrationRepository)
                .as(Iterable.class);
        if (type != ResolvableType.NONE && ClientRegistration.class.isAssignableFrom(type.resolveGenerics()[0])) {
            clientRegistrations = (Iterable<ClientRegistration>) clientRegistrationRepository;
        }

        clientRegistrations.forEach(registration -> oauth2AuthenticationUrls.put(registration.getClientName(), authorizationRequestBaseUri + "/" + registration.getRegistrationId()));
        model.addAttribute("urls", oauth2AuthenticationUrls);

        return "oauth_login";
    }


    @GetMapping("/")
    public String getLoginInfo(Model model, OAuth2AuthenticationToken authentication) {

        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(authentication.getAuthorizedClientRegistrationId(), authentication.getName());

        String userInfoEndpointUri = client.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUri();

        if (!StringUtils.isEmpty(userInfoEndpointUri)) {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + client.getAccessToken()
                    .getTokenValue());

            HttpEntity<String> entity = new HttpEntity<String>("", headers);

            ResponseEntity<Map> response = restTemplate.exchange(userInfoEndpointUri, HttpMethod.GET, entity, Map.class);
            Map userAttributes = response.getBody();
            Optional<Customer> customer = customerRepo.findByUsername(userAttributes.get("email").toString());
            String token = tokenGenerator(customer.get());
            model.addAttribute("name", userAttributes.get("name"));
            model.addAttribute("token", token);
            try {
                model.addAttribute("dto", objectMapper.writeValueAsString(customer.get()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return "loginSuccess";
    }

    private String tokenGenerator(Customer customer) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", customer.getPanNumber());
        map.put("username", customer.getUsername());
        map.put("name", "%s %s".formatted(customer.getFirstname(), customer.getLastname()).trim());
        map.put("role", customer.getRole());
        return tokenServiceProvider.generateToken(map).getToken();
    }

//    @GetMapping
//    public Map<String, Object> getCurrentUser(Model model, @CurrentUser UserPrincipal userPrincipal) {
//        System.out.println(userPrincipal.getAttributes());
//        return userPrincipal.getAttributes();
//    }
}