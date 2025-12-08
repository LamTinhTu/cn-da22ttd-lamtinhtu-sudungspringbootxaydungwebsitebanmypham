package com.oceanbutterflyshop.backend.dtos.request;

import lombok.Data;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Data
@Schema(description = "Request DTO for order item")
public class OrderItemRequest {
    
    @NotNull(message = "Product ID is required")
    @Schema(description = "Product identifier", example = "1")
    private Integer productId;
    
    @NotNull(message = "Item quantity is required")
    @Min(value = 1, message = "Item quantity must be at least 1")
    @Schema(description = "Quantity of the product", example = "2", minimum = "1")
    private Integer itemQuantity;
    
    @NotNull(message = "Item price is required")
    @Positive(message = "Item price must be a positive number")
    @Schema(description = "Item price at time of purchase", example = "8500.50", type = "number", format = "decimal")
    private BigDecimal itemPrice;
    
    @NotNull(message = "Unit price is required")
    @Positive(message = "Unit price must be a positive number")
    private Double unitPrice;
}