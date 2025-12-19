package com.oceanbutterflyshop.backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oceanbutterflyshop.backend.dtos.ApiResponse;
import com.oceanbutterflyshop.backend.dtos.request.LoginRequestDTO;
import com.oceanbutterflyshop.backend.dtos.request.RegisterRequestDTO;
import com.oceanbutterflyshop.backend.dtos.response.AuthResponseDTO;
import com.oceanbutterflyshop.backend.services.AuthService;

/**
 * REST Controller for authentication endpoints
 * As per PROJECT_SPEC.md Section 4.7 and 5.1
 * 
 * Base URL: /api/v1/auth
 * 
 * Endpoints:
 * - POST /register - Register a new customer account
 * - POST /login - Authenticate user and return user information
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "1. Authentication", description = "Public authentication endpoints for user registration and login. No JWT token required.")
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * Register a new customer account
     * 
     * Process:
     * 1. Validate input data (name, phone, username, password)
     * 2. Check for duplicate username/phone
     * 3. Hash password using BCrypt
     * 4. Assign default Customer role
     * 5. Generate user code with prefix "KH"
     * 6. Create user account
     * 
     * @param request Registration request containing user details
     * @return ApiResponse with user information (status 201)
     */
    @PostMapping("/register")
    @Operation(
        summary = "Register new customer", 
        description = "Create a new customer account with default role (CUSTOMER). Auto-generates user code with 'KH' prefix."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Registration successful - User account created",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Bad Request - Validation error or duplicate username/phone",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @SecurityRequirement(name = "")  // Override global security - no auth required
    public ResponseEntity<ApiResponse<AuthResponseDTO>> register(@Valid @RequestBody RegisterRequestDTO request) {
        log.info("POST /api/v1/auth/register - Register request for username: {}", request.getUserAccount());
        
        AuthResponseDTO response = authService.register(request);
        
        ApiResponse<AuthResponseDTO> apiResponse = ApiResponse.<AuthResponseDTO>builder()
                .status(HttpStatus.CREATED.value())
                .message("Registration successful")
                .data(response)
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }
    
    /**
     * Authenticate user and return user information
     * 
     * Process:
     * 1. Validate input (username, password)
     * 2. Find user by username
     * 3. Verify password hash
     * 4. Return user information if successful
     * 
     * @param request Login request containing username and password
     * @return ApiResponse with user information and token placeholder (status 200)
     */
    @PostMapping("/login")
    @Operation(
        summary = "User login", 
        description = "Authenticate user with username and password. Returns JWT token and user information on success."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Login successful - Returns JWT token and user details",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Bad Request - Validation error (missing username/password)",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid credentials",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @SecurityRequirement(name = "")  // Override global security - no auth required
    public ResponseEntity<ApiResponse<AuthResponseDTO>> login(@Valid @RequestBody LoginRequestDTO request) {
        log.info("POST /api/v1/auth/login - Login request for username: {}", request.getUserAccount());
        
        AuthResponseDTO response = authService.login(request);
        
        ApiResponse<AuthResponseDTO> apiResponse = ApiResponse.<AuthResponseDTO>builder()
                .status(HttpStatus.OK.value())
                .message("Login successful")
                .data(response)
                .build();
        
        return ResponseEntity.ok(apiResponse);
    }
}
