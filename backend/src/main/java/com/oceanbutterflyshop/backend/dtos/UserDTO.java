package com.oceanbutterflyshop.backend.dtos;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Data
public class UserDTO {
    private Integer userId;
    
    @NotBlank(message = "User code is required")
    @Size(max = 20, message = "User code must not exceed 20 characters")
    private String userCode;
    
    @NotBlank(message = "User name is required")
    @Size(max = 100, message = "User name must not exceed 100 characters")
    private String userName;
    
    @Size(max = 10, message = "Gender must not exceed 10 characters")
    private String userGender;
    
    @Past(message = "Birth date must be in the past")
    private LocalDate userBirthDate;
    
    @Size(max = 200, message = "Address must not exceed 200 characters")
    private String userAddress;
    
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone number must be 10-15 digits")
    private String userPhone;
    
    @NotBlank(message = "User account is required")
    @Size(min = 3, max = 50, message = "User account must be between 3-50 characters")
    private String userAccount;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String userPassword;
    
    @NotNull(message = "Role ID is required")
    private Integer roleId;
    
    // For response - role information
    private String roleCode;
    private String roleName;
}