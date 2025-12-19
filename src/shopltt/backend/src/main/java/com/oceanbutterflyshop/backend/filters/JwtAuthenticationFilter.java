package com.oceanbutterflyshop.backend.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.oceanbutterflyshop.backend.utils.JwtUtils;

import java.io.IOException;

/**
 * JWT Authentication Filter that intercepts HTTP requests and validates JWT tokens.
 * Extends OncePerRequestFilter to ensure single execution per request.
 * 
 * Filter Logic:
 * 1. Extract 'Authorization' header from request
 * 2. Check if header contains 'Bearer ' prefix
 * 3. Extract JWT token from header
 * 4. Validate token and extract username
 * 5. Load user details from database
 * 6. Create authentication object and set in SecurityContext
 * 
 * Token Format: "Authorization: Bearer <JWT_TOKEN>"
 * 
 * This filter runs before UsernamePasswordAuthenticationFilter in the security chain.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    /**
     * Filter incoming requests to validate JWT tokens
     * 
     * @param request HTTP request
     * @param response HTTP response
     * @param filterChain Filter chain to continue processing
     * @throws ServletException if servlet error occurs
     * @throws IOException if I/O error occurs
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        
        // Extract Authorization header
        final String authorizationHeader = request.getHeader("Authorization");
        
        // Check if Authorization header is present and has Bearer prefix
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.debug("No JWT token found in request headers");
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            // Extract JWT token (remove "Bearer " prefix)
            final String jwt = authorizationHeader.substring(7);
            final String username = jwtUtils.extractUsername(jwt);
            
            log.debug("JWT token found for username: {}", username);
            
            // If username is present and user is not already authenticated
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // Load user details from database
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                // Validate token against user details
                if (jwtUtils.validateToken(jwt, userDetails)) {
                    log.debug("JWT token is valid for user: {}", username);
                    
                    // Create authentication token
                    UsernamePasswordAuthenticationToken authenticationToken = 
                        new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                        );
                    
                    // Set authentication details from request
                    authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    
                    // Set authentication in SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    
                    log.debug("User {} authenticated successfully with roles: {}", 
                        username, userDetails.getAuthorities());
                } else {
                    log.warn("Invalid JWT token for user: {}", username);
                }
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
        }
        
        // Continue filter chain
        filterChain.doFilter(request, response);
    }
}
