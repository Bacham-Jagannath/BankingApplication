package com.cg.auth.security;


import com.cg.auth.dto.CustomerDto;
import com.cg.auth.entity.Customer;
import com.cg.auth.repository.CustomerRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
public class TokenServiceProvider {



    private static final Logger logger = LoggerFactory.getLogger(TokenServiceProvider.class);

    @Autowired
    private AppProperties appProperties;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private CustomerRepo customerRepo;

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getId);
    }


    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    public TokenDetails generateToken(Map<String, Object> extraClaims) {
        Date issuedDate = new Date(System.currentTimeMillis());
        Date expirationDate = new Date(System.currentTimeMillis() +appProperties.getAuth().getTokenExpirationMsec());
        String token = Jwts.builder().setClaims(extraClaims).setSubject("")
                .setIssuedAt(issuedDate)
                .setId((String) extraClaims.get("username"))
                .setExpiration(expirationDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
        TokenDetails tokenDetails = new TokenDetails();
        tokenDetails.setToken(token);
        tokenDetails.setAlgo(SignatureAlgorithm.HS256.getDescription());
        tokenDetails.setSub(appProperties.getAuth().getSub());
        tokenDetails.setIat(issuedDate);
        tokenDetails.setExp(expirationDate);
        return tokenDetails;
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token)
                .getBody();
    }

    public TokenDetails getTokenDetails(String token){
        Claims claims = extractAllClaims(token);
        TokenDetails tokenDetails = new TokenDetails();
        tokenDetails.setToken(token);
        tokenDetails.setSub(claims.getSubject());
        tokenDetails.setIat(claims.getIssuedAt());
        tokenDetails.setExp(claims.getExpiration());
        tokenDetails.setIssuer(claims.getIssuer());
        tokenDetails.setId(claims.getId());

        CustomerDto customerDto = new CustomerDto();
        customerDto.setUsername(String.valueOf(claims.get("username")));
        customerDto.setPanNumber((String) claims.get("id"));

        tokenDetails.setCustomerDto(customerDto);
        return tokenDetails;
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(appProperties.getAuth().getTokenSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Optional<Customer> byUsername = customerRepo.findByUsername(userPrincipal.getUsername());
        logger.info("userPrincipal.getAuthorities() {}", userPrincipal.getAttributes());
        Customer user = byUsername.orElse(null);
        Map<String, Object> map = new HashMap<>();
        assert user != null;
        map.put("customerId", user.getPanNumber());
        map.put("username", user.getUsername());
        return generateToken(map).getToken();
    }
}
