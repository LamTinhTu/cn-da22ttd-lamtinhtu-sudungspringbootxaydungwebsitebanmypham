package com.oceanbutterflyshop.backend.dtos;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class OrderItemDTO {
    private Integer orderItemId;
    
    @NotNull(message = "Order ID is required")
    private Integer orderId;
    
    @NotNull(message = "Product ID is required")
    private Integer productId;
    
    @NotNull(message = "Item quantity is required")
    @Min(value = 1, message = "Item quantity must be at least 1")
    private Integer itemQuantity;
    
    @NotNull(message = "Item price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Item price must be greater than 0")
    private Double itemPrice;
    
    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Unit price must be greater than 0")
    private Double unitPrice;
    
    // For response - product information
    private String productName;
    private String productCode;
    private String brandName;
}