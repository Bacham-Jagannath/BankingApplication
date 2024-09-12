package com.cg.config;

import com.cg.dto.RolesEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TokenService {

    @Autowired
    private JwtUtil jwtUtil;

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername(); // Extract username if UserDetails is the principal
            } else {
                return principal.toString(); // Principal can be a string if it's just the username
            }
        }
        return null; // Return null if no authentication or user not authenticated
    }

    public String getCurrentToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return (String) authentication.getCredentials(); // Extract the JWT token from the credentials
        }
        return null;
    }

    // You can also extract custom claims from the JWT token here
    public String getCustomClaimFromToken(String claimName) {
        String token = getCurrentToken();
        if (token != null) {
            // Use JwtUtil or similar utility class to extract claims
            // Assuming JwtUtil is your JWT utility class
            return jwtUtil.extractClaim(token, claimName);
        }
        return null;
    }

    public Map<String, Object> getAdminDetailsByToken(){
        return jwtUtil.extractAllClaims(getCurrentToken());
    }

    public Boolean isAdmin(){
        return getCustomClaimFromToken("role").equalsIgnoreCase(RolesEnum.ADMIN.name());
    }
}

