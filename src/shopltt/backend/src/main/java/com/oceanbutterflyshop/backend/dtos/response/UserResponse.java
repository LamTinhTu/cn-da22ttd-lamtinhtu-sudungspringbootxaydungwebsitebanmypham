package com.oceanbutterflyshop.backend.dtos.response;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "Response DTO for user information")
public class UserResponse {
    
    @Schema(description = "User ID", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer userId;
    
    @Schema(description = "Auto-generated user code", example = "KH12345678", accessMode = Schema.AccessMode.READ_ONLY)
    private String userCode;
    
    @Schema(description = "Full name", example = "Nguyen Van An")
    private String userName;
    
    @Schema(description = "User gender", example = "Nam", allowableValues = {"Nam", "Nu", "Khac"})
    private String userGender;
    
    @Schema(description = "Birth date", example = "1990-05-15")
    private LocalDate userBirthDate;
    
    @Schema(description = "Address", example = "123 Le Loi Street, District 1, Ho Chi Minh City")
    private String userAddress;
    
    @Schema(description = "Phone number", example = "0912345678")
    private String userPhone;
    
    @Schema(description = "Username/account", example = "nguyenvanan")
    private String userAccount;
    
    @Schema(description = "Creation timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;
    
    @Schema(description = "Last update timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
    
    // Role information
    @Schema(description = "Role ID", example = "3")
    private Integer roleId;
    
    @Schema(description = "Role code", example = "KH", accessMode = Schema.AccessMode.READ_ONLY)
    private String roleCode;
    
    @Schema(description = "Role name", example = "Customer", accessMode = Schema.AccessMode.READ_ONLY)
    private String roleName;
}