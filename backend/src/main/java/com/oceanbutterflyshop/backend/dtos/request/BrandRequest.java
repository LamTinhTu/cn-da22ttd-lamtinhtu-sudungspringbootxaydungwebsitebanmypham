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
    @Schema(description = "Brand name", example = "Omega", maxLength = 100)
    private String brandName;
    
    @Size(max = 1000, message = "Brand description must not exceed 1000 characters")
    @Schema(description = "Brand description", example = "Swiss luxury watchmaker known for precision and innovation", maxLength = 1000)
    private String brandDescription;
}