package com.oceanbutterflyshop.backend.dtos.request;

import lombok.Data;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Data
@Schema(description = "Request DTO for creating or updating a user")
public class UserRequest {
    
    @NotBlank(message = "User name is required")
    @Pattern(regexp = "^[a-zA-ZÀ-ỹ\\s]+$", message = "Full name can only contain letters and spaces")
    @Size(max = 100, message = "User name must not exceed 100 characters")
    @Schema(description = "Full name of the user", example = "Nguyen Van An", maxLength = 100)
    private String userName;
    
    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "^(Nam|Nu|Khac)$", message = "Gender must be: Nam, Nu, or Khac")
    @Schema(description = "User gender", example = "Nam", allowableValues = {"Nam", "Nu", "Khac"})
    private String userGender;
    
    @Past(message = "Birth date must be in the past")
    @Schema(description = "User birth date", example = "1990-05-15", type = "string", format = "date")
    private LocalDate userBirthDate;
    
    @Size(max = 200, message = "Address must not exceed 200 characters")
    @Schema(description = "User address", example = "123 Le Loi Street, District 1, Ho Chi Minh City", maxLength = 200)
    private String userAddress;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Phone number must be exactly 10 or 11 digits")
    @Schema(description = "Phone number (10-11 digits)", example = "0912345678", pattern = "^[0-9]{10,11}$")
    private String userPhone;
    
    @NotBlank(message = "User account is required")
    @Size(min = 3, max = 50, message = "User account must be between 3-50 characters")
    @Schema(description = "User account/username", example = "nguyenvanan", minLength = 3, maxLength = 50)
    private String userAccount;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Schema(description = "User password", example = "password123", minLength = 6, format = "password")
    private String userPassword;
    
    @NotNull(message = "Role ID is required")
    @Schema(description = "Role identifier (1=Admin, 2=Staff, 3=Customer)", example = "3")
    private Integer roleId;
}