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

import com.oceanbutterflyshop.backend.enums.ProductStatus;
import com.oceanbutterflyshop.backend.enums.ProductCategory;

/**
 * Request DTO cho việc tạo mới hoặc cập nhật sản phẩm
 * Theo PROJECT_SPEC.md Phần 4.1 và 4.6
 * Lưu ý: ProductCode KHÔNG được bao gồm - nó được tự động tạo bởi hệ thống
 */
@Data
@Schema(description = "Request DTO for creating or updating a product")
public class ProductRequestDTO {
    
    @NotBlank(message = "Product name is required")
    @Size(max = 100, message = "Product name must not exceed 100 characters")
    @Schema(description = "Product name", example = "Kem dưỡng da", maxLength = 100)
    private String productName;
    
    @Size(max = 1000, message = "Product description must not exceed 1000 characters")
    @Schema(description = "Product description", example = "Kem dưỡng da cao cấp với thành phần tự nhiên và công thức độc quyền", maxLength = 1000)
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
    
    @NotNull(message = "Product status is required")
    @Schema(description = "Product status: NOT_SOLD, SELLING, OUT_OF_STOCK, DISCONTINUED", 
            example = "SELLING", 
            allowableValues = {"NOT_SOLD", "SELLING", "OUT_OF_STOCK", "DISCONTINUED"})
    private ProductStatus productStatus;
    
    @Schema(description = "Product category: MAKEUP, SKINCARE, HAIRCARE", 
            example = "SKINCARE", 
            allowableValues = {"MAKEUP", "SKINCARE", "HAIRCARE"})
    private ProductCategory productCategory;
}