package com.oceanbutterflyshop.backend.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
public class ProductDTO {
    private Integer productId;
    
    @NotBlank(message = "Product code is required")
    @Size(max = 20, message = "Product code must not exceed 20 characters")
    private String productCode;
    
    @NotBlank(message = "Product name is required")
    @Size(max = 100, message = "Product name must not exceed 100 characters")
    private String productName;
    
    @Size(max = 1000, message = "Product description must not exceed 1000 characters")
    private String productDescription;
    
    @NotNull(message = "Product price is required")
    @Min(value = 0, message = "Product price must be non-negative")
    private Double productPrice;
    
    @NotNull(message = "Quantity stock is required")
    @Min(value = 0, message = "Quantity stock must be non-negative")
    private Integer quantityStock;

    @NotNull(message = "Brand ID is required")
    private Integer brandId;
    
    // For response - brand information
    private String brandCode;
    private String brandName;
    private String brandDescription;
    
    // For response - product images
    private List<ImageDTO> images;
}
