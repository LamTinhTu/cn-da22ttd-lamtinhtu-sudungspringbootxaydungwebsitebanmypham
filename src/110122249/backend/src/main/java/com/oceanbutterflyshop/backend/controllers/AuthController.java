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
 * REST Controller cho các endpoint xác thực
 * Theo PROJECT_SPEC.md Mục 4.7 và 5.1
 * 
 * Base URL: /api/v1/auth
 * 
 * Endpoints:
 * - POST /register - Đăng ký tài khoản khách hàng mới
 * - POST /login - Xác thực người dùng và trả về thông tin người dùng
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "1. Authentication", description = "Public authentication endpoints for user registration and login. No JWT token required.")
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * Đăng ký tài khoản khách hàng mới
     * 
     * Quy trình:
     * 1. Xác thực dữ liệu đầu vào (tên, số điện thoại, tên đăng nhập, mật khẩu)
     * 2. Kiểm tra trùng lặp tên đăng nhập/số điện thoại
     * 3. Mã hóa mật khẩu bằng BCrypt
     * 4. Gán vai trò Customer mặc định
     * 5. Tạo mã người dùng với tiền tố "KH"
     * 6. Tạo tài khoản người dùng
     * 
     * @param request Yêu cầu đăng ký chứa thông tin người dùng
     * @return ApiResponse với thông tin người dùng (status 201)
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
    @SecurityRequirement(name = "")  // Ghi đè global security - không yêu cầu xác thực
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
     * Xác thực người dùng và trả về thông tin người dùng
     * 
     * Quy trình:
     * 1. Xác thực dữ liệu đầu vào (tên đăng nhập, mật khẩu)
     * 2. Tìm người dùng theo tên đăng nhập
     * 3. Xác minh hash mật khẩu
     * 4. Trả về thông tin người dùng nếu thành công
     * 
     * @param request Yêu cầu đăng nhập chứa tên đăng nhập và mật khẩu
     * @return ApiResponse với thông tin người dùng và token placeholder (status 200)
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
    @SecurityRequirement(name = "")  // Ghi đè global security - không yêu cầu xác thực
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

    @PostMapping("/forgot-password")
    @Operation(summary = "Forgot password", description = "Request password reset via SMS")
    @SecurityRequirement(name = "")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody com.oceanbutterflyshop.backend.dtos.request.ForgotPasswordRequestDTO request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("If the phone number exists, an SMS has been sent.")
                .build());
    }
}
