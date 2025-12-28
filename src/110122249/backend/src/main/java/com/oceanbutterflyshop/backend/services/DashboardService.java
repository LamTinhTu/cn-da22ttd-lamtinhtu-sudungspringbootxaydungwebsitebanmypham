package com.oceanbutterflyshop.backend.services;

import com.oceanbutterflyshop.backend.dtos.response.DashboardStatsResponse;
import com.oceanbutterflyshop.backend.dtos.response.RecentOrderResponse;
import com.oceanbutterflyshop.backend.dtos.response.TopProductResponse;

import java.util.List;

public interface DashboardService {
    DashboardStatsResponse getDashboardStats();
    List<RecentOrderResponse> getRecentOrders(int limit);
    List<TopProductResponse> getTopSellingProducts(int limit);
}
