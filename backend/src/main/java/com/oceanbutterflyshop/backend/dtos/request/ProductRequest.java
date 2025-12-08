package com.oceanbutterflyshop.backend.dtos.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Data
@Schema(description = "Request DTO for creating or updating a product")
public class ProductRequest {
    
    @NotBlank(message = "Product name is required")
    @Pattern(regexp = "^[a-zA-Z0-9\\s\\-_]+$", message = "Product name can only contain letters, numbers, spaces, hyphens, and underscores")
    @Size(max = 100, message = "Product name must not exceed 100 characters")
    @Schema(description = "Product name", example = "Rolex Submariner Classic", maxLength = 100)
    private String productName;
    
    @Size(max = 1000, message = "Product description must not exceed 1000 characters")
    @Schema(description = "Product description", example = "Luxury diving watch with automatic movement and ceramic bezel", maxLength = 1000)
    private String productDescription;
    
    @NotNull(message = "Product price is required")
    @Positive(message = "Product price must be a positive number")
    @Schema(description = "Product price in decimal format", example = "8500.50", type = "number", format = "decimal")
    private BigDecimal productPrice;
    
    @NotNull(message = "Quantity stock is required")
    @Min(value = 0, message = "Quantity stock cannot be negative")
    @Schema(description = "Available stock quantity", example = "15", minimum = "0")
    private Integer quantityStock;

    @NotNull(message = "Brand ID is required")
    @Schema(description = "Brand identifier", example = "1")
    private Integer brandId;
}