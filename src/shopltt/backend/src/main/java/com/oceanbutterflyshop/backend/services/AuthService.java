package com.oceanbutterflyshop.backend.services;

import com.oceanbutterflyshop.backend.dtos.request.LoginRequestDTO;
import com.oceanbutterflyshop.backend.dtos.request.RegisterRequestDTO;
import com.oceanbutterflyshop.backend.dtos.response.AuthResponseDTO;

/**
 * Service interface for authentication operations
 * As per PROJECT_SPEC.md Section 4.7
 */
public interface AuthService {
    
    /**
     * Register a new user
     * Process:
     * 1. Check if username already exists
     * 2. Check if phone already exists
     * 3. Hash password using BCrypt
     * 4. Assign default role (Customer - CUS)
     * 5. Generate user code with prefix "KH"
     * 6. Save user to database
     * 
     * @param request Registration request containing user details
     * @return AuthResponseDTO with user information
     * @throws BadRequestException if username or phone already exists
     */
    AuthResponseDTO register(RegisterRequestDTO request);
    
    /**
     * Authenticate user and return user information
     * Process:
     * 1. Find user by username
     * 2. Verify password hash matches
     * 3. Return user information if successful
     * 
     * @param request Login request containing username and password
     * @return AuthResponseDTO with user information and token placeholder
     * @throws BadRequestException if credentials are invalid
     */
    AuthResponseDTO login(LoginRequestDTO request);
}
