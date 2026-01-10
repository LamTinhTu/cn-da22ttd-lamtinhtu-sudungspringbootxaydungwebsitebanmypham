package com.oceanbutterflyshop.backend.dtos.request;

import com.oceanbutterflyshop.backend.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request DTO cho đăng ký người dùng
 * Theo PROJECT_SPEC.md Phần 4.7.A và 5.1
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
    @Pattern(regexp = "^(0[0-9]{9,10}|\\+1[0-9]{10})$", message = "RBSDT: Phone must be VN (0XXX) or US (+1XXX) format")
    @Schema(
        description = "Phone number (VN: 0XXXXXXXXX or US: +1XXXXXXXXXX)",
        example = "0912345678",
        requiredMode = Schema.RequiredMode.REQUIRED,
        pattern = "^(0[0-9]{9,10}|\\+1[0-9]{10})$"
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

    @Schema(
        description = "Gender (MALE, FEMALE, OTHER)",
        example = "MALE"
    )
    private Gender userGender;

    @Schema(
        description = "User birth date", 
        example = "1990-05-15", 
        type = "string", 
        format = "date"
    )
    @com.fasterxml.jackson.annotation.JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private java.time.LocalDate userBirthDate;

    @Schema(
        description = "Address",
        example = "123 Main St, City, Country"
    )
    private String userAddress;
}
