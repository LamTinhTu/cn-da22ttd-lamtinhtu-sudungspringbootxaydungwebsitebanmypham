package com.oceanbutterflyshop.backend.dtos;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class RoleDTO {
    private Integer roleId;
    
    @NotBlank(message = "Role code is required")
    @Size(max = 20, message = "Role code must not exceed 20 characters")
    private String roleCode;
    
    @NotBlank(message = "Role name is required")
    @Size(max = 100, message = "Role name must not exceed 100 characters")
    private String roleName;
}