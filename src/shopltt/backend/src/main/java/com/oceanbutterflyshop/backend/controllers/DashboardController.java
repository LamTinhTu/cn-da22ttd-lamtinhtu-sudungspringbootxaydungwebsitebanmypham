package com.oceanbutterflyshop.backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oceanbutterflyshop.backend.dtos.ApiResponse;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "APIs for dashboard statistics")
public class DashboardController {

    private final JdbcTemplate jdbcTemplate;

    @Operation(summary = "Get dashboard statistics")
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // Count total products
            Integer totalProducts = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM products", Integer.class);
            stats.put("totalProducts", totalProducts != null ? totalProducts : 0);
            
            // Count total brands
            Integer totalBrands = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM brands", Integer.class);
            stats.put("totalBrands", totalBrands != null ? totalBrands : 0);
            
            // Count total users
            Integer totalUsers = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users", Integer.class);
            stats.put("totalUsers", totalUsers != null ? totalUsers : 0);
            
            // Count total orders
            Integer totalOrders = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM orders", Integer.class);
            stats.put("totalOrders", totalOrders != null ? totalOrders : 0);
            
            // Count pending orders
            Integer pendingOrders = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM orders WHERE order_status = ?", Integer.class, "Mới");
            stats.put("pendingOrders", pendingOrders != null ? pendingOrders : 0);
            
            // Calculate total revenue
            Double totalRevenue = jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(order_amount), 0) FROM orders WHERE order_status = ?", 
                Double.class, "Đã giao");
            stats.put("totalRevenue", totalRevenue != null ? totalRevenue : 0.0);
            
            // Low stock products (< 10)
            Integer lowStockProducts = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM products WHERE quantity_stock < ?", Integer.class, 10);
            stats.put("lowStockProducts", lowStockProducts != null ? lowStockProducts : 0);
            
        } catch (Exception e) {
            // If tables don't exist yet or any error occurs, return zeros
            stats.put("totalProducts", 0);
            stats.put("totalBrands", 0);
            stats.put("totalUsers", 0);
            stats.put("totalOrders", 0);
            stats.put("pendingOrders", 0);
            stats.put("totalRevenue", 0.0);
            stats.put("lowStockProducts", 0);
            stats.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(ApiResponse.success("Dashboard statistics retrieved", stats));
    }
}