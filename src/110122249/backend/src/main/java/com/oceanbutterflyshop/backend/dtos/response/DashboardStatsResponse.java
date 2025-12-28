package com.oceanbutterflyshop.backend.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {
    private BigDecimal totalRevenue;
    private Integer totalOrders;
    private Integer newOrders;
    private Integer totalCustomers;
    private Integer totalProducts;
    private Integer lowStockProducts;
    
    // Trend percentages (so với tháng trước)
    private Double revenueTrend;
    private Double ordersTrend;
    private Double customersTrend;
    private Double productsTrend;
}
