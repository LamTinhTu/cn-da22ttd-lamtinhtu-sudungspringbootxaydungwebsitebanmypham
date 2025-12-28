package com.oceanbutterflyshop.backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.oceanbutterflyshop.backend.dtos.ApiResponse;
import com.oceanbutterflyshop.backend.dtos.response.DashboardStatsResponse;
import com.oceanbutterflyshop.backend.dtos.response.RecentOrderResponse;
import com.oceanbutterflyshop.backend.dtos.response.TopProductResponse;
import com.oceanbutterflyshop.backend.services.DashboardService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "3. Dashboard", description = "Dashboard statistics and overview. Requires ADMIN or STAFF role.")
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(
        summary = "Get dashboard statistics",
        description = "Retrieve overall statistics including total revenue, orders, customers, and products. Requires ADMIN or STAFF role."
    )
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboardStats() {
        DashboardStatsResponse stats = dashboardService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success("Dashboard statistics retrieved successfully", stats));
    }
    
    @Operation(
        summary = "Get recent orders",
        description = "Retrieve most recent orders with customer information. Requires ADMIN or STAFF role."
    )
    @GetMapping("/recent-orders")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<RecentOrderResponse>>> getRecentOrders(
            @Parameter(description = "Number of orders to return", example = "5")
            @RequestParam(required = false, defaultValue = "5") Integer limit
    ) {
        List<RecentOrderResponse> orders = dashboardService.getRecentOrders(limit);
        return ResponseEntity.ok(ApiResponse.success("Recent orders retrieved successfully", orders));
    }
    
    @Operation(
        summary = "Get top selling products",
        description = "Retrieve best-selling products for dashboard. Requires ADMIN or STAFF role."
    )
    @GetMapping("/top-products")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TopProductResponse>>> getTopProducts(
            @Parameter(description = "Number of products to return", example = "5")
            @RequestParam(required = false, defaultValue = "5") Integer limit
    ) {
        List<TopProductResponse> products = dashboardService.getTopSellingProducts(limit);
        return ResponseEntity.ok(ApiResponse.success("Top selling products retrieved successfully", products));
    }
}