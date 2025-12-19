package com.oceanbutterflyshop.backend.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for authentication (login/register)
 * As per PROJECT_SPEC.md Section 4.7.B and 5.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Authentication response containing user information and JWT token")
public class AuthResponseDTO {
    
    @Schema(
        description = "Auto-generated user code (AD for Admin, NV for Staff, KH for Customer)",
        example = "KH12345678"
    )
    private String userCode;
    
    @Schema(
        description = "Full name of the user",
        example = "Nguyen Van An"
    )
    private String userName;
    
    @Schema(
        description = "Role name of the user",
        example = "Customer",
        allowableValues = {"Administrator", "Staff", "Customer"}
    )
    private String roleName;
    
    @Schema(
        description = "JWT access token for authentication (valid for 24 hours)",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    private String accessToken;
}
