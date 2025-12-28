package com.oceanbutterflyshop.backend.dtos.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "Request DTO for creating or updating an order")
public class OrderRequest {
    
    // LƯU Ý: userId không còn bắt buộc - sẽ được lấy từ SecurityContext
    
    @NotBlank(message = "Order status is required")
    @Pattern(regexp = "^(NEW|PROCESSING|DELIVERED|CANCELLED)$", 
             message = "Order status must be: NEW, PROCESSING, DELIVERED, or CANCELLED")
    @Schema(description = "Order status", example = "NEW", allowableValues = {"NEW", "PROCESSING", "DELIVERED", "CANCELLED"})
    private String orderStatus;
    
    @NotNull(message = "Order amount is required")
    @Positive(message = "Order amount must be a positive number")
    @Schema(description = "Total order amount", example = "17001.00", type = "number", format = "decimal")
    private BigDecimal orderAmount;
    
    @NotBlank(message = "Shipping address is required")
    @Size(max = 200, message = "Shipping address must not exceed 200 characters")
    @Schema(description = "Delivery address (snapshot)", example = "123 đường Lê Lợi, Quận 1, Thành phố Hồ Chí Minh", maxLength = 200)
    private String shippingAddress;
    
    @NotBlank(message = "Shipping phone is required")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Shipping phone must be exactly 10 or 11 digits")
    @Schema(description = "Receiver phone number (snapshot)", example = "0912345678", pattern = "^[0-9]{10,11}$")
    private String shippingPhone;
    
    @Schema(description = "Payment date", example = "2025-12-03", type = "string", format = "date")
    private LocalDate paymentDate;
    
    @Pattern(regexp = "^(CASH|BANK_TRANSFER|CARD)$", 
             message = "Payment method must be: CASH, BANK_TRANSFER, or CARD")
    @Schema(description = "Payment method", example = "BANK_TRANSFER", allowableValues = {"CASH", "BANK_TRANSFER", "CARD"})
    private String paymentMethod;
    
    // Các sản phẩm trong đơn hàng
    @NotNull(message = "Order items are required")
    @Size(min = 1, message = "Order must have at least one item")
    @Schema(description = "List of order items")
    private List<OrderItemRequest> orderItems;
}