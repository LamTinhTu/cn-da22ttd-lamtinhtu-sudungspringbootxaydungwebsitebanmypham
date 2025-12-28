package com.oceanbutterflyshop.backend.dtos.response;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "Response DTO for order item information")
public class OrderItemResponse {
    
    @Schema(description = "Order item ID", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer orderItemId;
    
    @Schema(description = "Order ID", example = "1")
    private Integer orderId;
    
    @Schema(description = "Product ID", example = "1")
    private Integer productId;
    
    @Schema(description = "Quantity purchased", example = "2")
    private Integer itemQuantity;
    
    @Schema(description = "Item price at time of purchase", example = "8500.50", type = "number", format = "decimal")
    private BigDecimal itemPrice;
    
    @Schema(description = "Original unit price", example = "8500.50")
    private Float unitPrice;
    
    @Schema(description = "Creation timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;
    
    @Schema(description = "Last update timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
    
    // Thông tin sản phẩm
    @Schema(description = "Product name", example = "Rolex Submariner Classic", accessMode = Schema.AccessMode.READ_ONLY)
    private String productName;
    
    @Schema(description = "Product code", example = "SP12345678", accessMode = Schema.AccessMode.READ_ONLY)
    private String productCode;
    private String brandName;
    
    @Schema(description = "Product image URL", example = "http://localhost:8080/uploads/image.jpg", accessMode = Schema.AccessMode.READ_ONLY)
    private String productImage;
}