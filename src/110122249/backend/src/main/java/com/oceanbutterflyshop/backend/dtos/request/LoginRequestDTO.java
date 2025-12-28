package com.oceanbutterflyshop.backend.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request DTO cho đăng nhập người dùng
 * Theo PROJECT_SPEC.md Phần 4.7.B
 */
@Data
@Schema(description = "Login credentials for authentication")
public class LoginRequestDTO {
    
    @NotBlank(message = "Username is required")
    @Schema(
        description = "Username for login",
        example = "admin",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String userAccount;
    
    @NotBlank(message = "Password is required")
    @Schema(
        description = "Password for authentication",
        example = "password",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String userPassword;
}
