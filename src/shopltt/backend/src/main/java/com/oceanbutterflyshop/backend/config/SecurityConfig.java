package com.oceanbutterflyshop.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.oceanbutterflyshop.backend.filters.JwtAuthenticationFilter;

/**
 * Security configuration for JWT-based authentication and authorization.
 * Implements stateless session management with JWT tokens.
 * 
 * Security Features:
 * - JWT token-based authentication
 * - Stateless session management (no cookies)
 * - Role-based access control (ADMIN, STAFF, CUSTOMER)
 * - Public endpoints for authentication and product browsing
 * - Protected endpoints for authenticated users
 * 
 * Authentication Flow:
 * 1. User logs in with credentials → receives JWT token
 * 2. Client includes token in Authorization header: "Bearer <token>"
 * 3. JwtAuthenticationFilter validates token on each request
 * 4. SecurityContext is populated with authenticated user
 * 5. Spring Security enforces role-based access control
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    /**
     * Configure Security Filter Chain with JWT authentication
     * 
     * Public Endpoints (No Authentication Required):
     * - /api/v1/auth/** - Login and registration
     * - /api/v1/products/** (GET only) - Browse products
     * - /swagger-ui/** - Swagger documentation
     * - /v3/api-docs/** - OpenAPI documentation
     * 
     * Protected Endpoints (Authentication Required):
     * - All other endpoints require valid JWT token
     * 
     * @param http HttpSecurity configuration
     * @return SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF (not needed for stateless JWT authentication)
            .csrf(AbstractHttpConfigurer::disable)
            
            // Configure authorization rules
            .authorizeHttpRequests(auth -> auth
                // Public endpoints - no authentication required
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()  // Only GET allowed for guests
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/v3/api-docs/**", "/api-docs/**").permitAll()
                
                // All other endpoints require authentication
                .anyRequest().authenticated()
            )
            
            // Configure stateless session management (JWT tokens only)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Set authentication provider
            .authenticationProvider(authenticationProvider())
            
            // Add JWT filter before UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            
            // Disable default form login and HTTP basic auth
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable);
        
        return http.build();
    }

    /**
     * Configure authentication provider with UserDetailsService and PasswordEncoder
     * 
     * @return AuthenticationProvider configured with DAO authentication
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Expose AuthenticationManager bean for manual authentication
     * Used in AuthController for login functionality
     * 
     * @param config AuthenticationConfiguration
     * @return AuthenticationManager
     * @throws Exception if authentication manager cannot be created
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configure password encoder using BCrypt
     * BCrypt is a one-way hashing algorithm with salt
     * 
     * @return PasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

