package com.oceanbutterflyshop.backend.dtos;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Data
public class OrderDTO {
    private Integer orderId;
    
    @NotBlank(message = "Order code is required")
    @Size(max = 20, message = "Order code must not exceed 20 characters")
    private String orderCode;
    
    @NotNull(message = "User ID is required")
    private Integer userId;
    
    private LocalDate orderDate;
    
    @NotBlank(message = "Order status is required")
    @Pattern(regexp = "^(Mới|Đang xử lý|Đã giao|Đã hủy)$", 
             message = "Order status must be: Mới, Đang xử lý, Đã giao, or Đã hủy")
    private String orderStatus;
    
    @NotNull(message = "Order amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Order amount must be greater than 0")
    private Double orderAmount;
    
    private LocalDate paymentDate;
    
    @Pattern(regexp = "^(Tiền mặt|Thẻ|Chuyển khoản)$", 
             message = "Payment method must be: Tiền mặt, Thẻ, or Chuyển khoản")
    private String paymentMethod;
    
    // For response - user information
    private String userName;
    private String userPhone;
    
    // Order items
    @NotNull(message = "Order items are required")
    @Size(min = 1, message = "Order must have at least one item")
    private List<OrderItemDTO> orderItems;
}