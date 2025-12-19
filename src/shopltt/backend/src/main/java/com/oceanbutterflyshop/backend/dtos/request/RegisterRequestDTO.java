package com.oceanbutterflyshop.backend.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request DTO for user registration
 * As per PROJECT_SPEC.md Section 4.7.A and 5.1
 */
@Data
@Schema(description = "Registration request for creating a new customer account")
public class RegisterRequestDTO {
    
    @NotBlank(message = "Full name is required")
    @Pattern(regexp = "^[a-zA-ZÀ-ỹ\\s]+$", message = "RBHT: Full name must contain only letters and spaces")
    @Schema(
        description = "Full name of the user (letters and spaces only)",
        example = "Nguyen Van An",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String userName;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "RBSDT: Phone must be exactly 10 or 11 digits")
    @Schema(
        description = "Phone number (10-11 digits)",
        example = "0912345678",
        requiredMode = Schema.RequiredMode.REQUIRED,
        pattern = "^[0-9]{10,11}$"
    )
    private String userPhone;
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Schema(
        description = "Unique username for login (3-50 characters)",
        example = "customer123",
        requiredMode = Schema.RequiredMode.REQUIRED,
        minLength = 3,
        maxLength = 50
    )
    private String userAccount;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Schema(
        description = "Password (minimum 6 characters)",
        example = "password123",
        requiredMode = Schema.RequiredMode.REQUIRED,
        minLength = 6
    )
    private String userPassword;
}
