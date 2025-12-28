package com.oceanbutterflyshop.backend.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentOrderResponse {
    private Integer orderId;
    private String orderCode;
    private String customerName;
    private LocalDate orderDate;
    private BigDecimal totalAmount;
    private String orderStatus;
}
