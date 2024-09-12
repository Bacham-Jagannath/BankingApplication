package com.cg.admin.config;

import com.cg.admin.exception.AdminServiceException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    private final List<String> AUTH_WHITELIST = List.of("/actuator/**","/auth/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html");


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // Skip JWT validation for public endpoints
        if (isPublicPath(requestURI, AUTH_WHITELIST)) {
            chain.doFilter(request, response);
            return;
        }

        // Your existing JWT validation logic goes here...

        String authorizationHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);

            // Validate token before extracting any claims
            if (!jwtUtil.isTokenExpired(token)) {
                username = String.valueOf(jwtUtil.extractAllClaims(token).get("id"));

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, token, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                }else {
                    throw new AdminServiceException("Invalid JWT token", 401);
                }
            } else {
                throw new AdminServiceException("JWT token is expired", 401);
            }
        } else {
            // Commented out exception handling for missing token
            throw new AdminServiceException("Authorization JWT Token is missing in Header", 401);
        }
        chain.doFilter(request, response);
    }


    private boolean isPublicPath(String requestURI, List<String> publicPaths) {
        // Use AntPathMatcher to match the requestURI with public paths
        return publicPaths.stream().anyMatch(pattern -> antPathMatcher.match(pattern, requestURI));
    }
}
