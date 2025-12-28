package com.oceanbutterflyshop.backend.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Request DTO for creating or updating a brand")
public class BrandRequest {
    
    @NotBlank(message = "Brand name is required")
    @Size(max = 100, message = "Brand name must not exceed 100 characters")
    @Schema(description = "Brand name", example = "Shiseido", maxLength = 100)
    private String brandName;
    
    @Size(max = 1000, message = "Brand description must not exceed 1000 characters")
    @Schema(description = "Brand description", example = "Sự kết hợp giữa khoa học phương Tây và bí quyết làm đẹp phương Đông", maxLength = 1000)
    private String brandDescription;
}