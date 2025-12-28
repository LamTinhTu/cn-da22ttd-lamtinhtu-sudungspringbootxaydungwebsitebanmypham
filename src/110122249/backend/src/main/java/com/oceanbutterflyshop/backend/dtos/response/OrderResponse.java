package com.oceanbutterflyshop.backend.dtos.response;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "Response DTO for order information")
public class OrderResponse {
    
    @Schema(description = "Order ID", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer orderId;
    
    @Schema(description = "Auto-generated order code", example = "DH12345678", accessMode = Schema.AccessMode.READ_ONLY)
    private String orderCode;
    
    @Schema(description = "Customer user ID", example = "1")
    private Integer userId;
    
    @Schema(description = "Order date", example = "2025-12-03", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDate orderDate;
    
    @Schema(description = "Order status", example = "New", allowableValues = {"New", "Processing", "Delivered", "Cancelled"})
    private String orderStatus;
    
    @Schema(description = "Total order amount", example = "17001.00", type = "number", format = "decimal")
    private BigDecimal orderAmount;
    
    @Schema(description = "Shipping address snapshot", example = "123 Le Loi Street, District 1, Ho Chi Minh City")
    private String shippingAddress;
    
    @Schema(description = "Shipping phone snapshot", example = "0912345678")
    private String shippingPhone;
    
    @Schema(description = "Payment date", example = "2025-12-03")
    private LocalDate paymentDate;
    
    @Schema(description = "Payment method", example = "BANK_TRANSFER", allowableValues = {"CASH", "BANK_TRANSFER", "CARD"})
    private String paymentMethod;
    
    @Schema(description = "Creation timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;
    
    @Schema(description = "Last update timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
    
    // Thông tin người dùng
    @Schema(description = "Customer name", example = "Nguyen Van An", accessMode = Schema.AccessMode.READ_ONLY)
    private String userName;
    
    @Schema(description = "Customer phone", example = "0912345678", accessMode = Schema.AccessMode.READ_ONLY)
    private String userPhone;
    
    // Các sản phẩm trong đơn hàng
    @Schema(description = "List of order items")
    private List<OrderItemResponse> orderItems;
}