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
    
    // NOTE: userId is no longer required - it will be retrieved from SecurityContext
    
    @NotBlank(message = "Order status is required")
    @Pattern(regexp = "^(New|Processing|Delivered|Cancelled)$", 
             message = "Order status must be: New, Processing, Delivered, or Cancelled")
    @Schema(description = "Order status", example = "New", allowableValues = {"New", "Processing", "Delivered", "Cancelled"})
    private String orderStatus;
    
    @NotNull(message = "Order amount is required")
    @Positive(message = "Order amount must be a positive number")
    @Schema(description = "Total order amount", example = "17001.00", type = "number", format = "decimal")
    private BigDecimal orderAmount;
    
    @NotBlank(message = "Shipping address is required")
    @Size(max = 200, message = "Shipping address must not exceed 200 characters")
    @Schema(description = "Delivery address (snapshot)", example = "123 Le Loi Street, District 1, Ho Chi Minh City", maxLength = 200)
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
    
    // Order items
    @NotNull(message = "Order items are required")
    @Size(min = 1, message = "Order must have at least one item")
    @Schema(description = "List of order items")
    private List<OrderItemRequest> orderItems;
}