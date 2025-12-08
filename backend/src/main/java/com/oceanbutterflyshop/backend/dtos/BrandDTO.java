package com.oceanbutterflyshop.backend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BrandDTO {
    private Integer brandId;
    
    @NotBlank(message = "Brand code is required")
    @Size(max = 20, message = "Brand code must not exceed 20 characters")
    private String brandCode;
    
    @NotBlank(message = "Brand name is required")
    @Size(max = 100, message = "Brand name must not exceed 100 characters")
    private String brandName;
    
    @Size(max = 1000, message = "Brand description must not exceed 1000 characters")
    private String brandDescription;
}
