package com.oceanbutterflyshop.backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.oceanbutterflyshop.backend.dtos.ApiResponse;
import com.oceanbutterflyshop.backend.dtos.request.OrderRequest;
import com.oceanbutterflyshop.backend.dtos.response.OrderResponse;
import com.oceanbutterflyshop.backend.services.OrderService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order Management", description = "APIs for managing orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "Get all orders")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders() {
        List<OrderResponse> orders = orderService.getAllOrders();
        return ResponseEntity.ok(ApiResponse.success("Orders retrieved successfully", orders));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable Integer orderId) {
        OrderResponse order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(ApiResponse.success("Order retrieved successfully", order));
    }

    @GetMapping("/code/{orderCode}")
    @Operation(summary = "Get order by code")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderByCode(@PathVariable String orderCode) {
        OrderResponse order = orderService.getOrderByCode(orderCode);
        return ResponseEntity.ok(ApiResponse.success("Order retrieved successfully", order));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get orders by user ID")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrdersByUserId(@PathVariable Integer userId) {
        List<OrderResponse> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("User orders retrieved successfully", orders));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get orders by status")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrdersByStatus(@PathVariable String status) {
        List<OrderResponse> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Orders by status retrieved successfully", orders));
    }

    @PostMapping
    @Operation(summary = "Create a new order")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        OrderResponse createdOrder = orderService.createOrder(orderRequest);
        return new ResponseEntity<>(
            ApiResponse.success("Order created successfully", createdOrder),
            HttpStatus.CREATED
        );
    }

    @PutMapping("/{orderId}/status")
    @Operation(summary = "Update order status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Integer orderId,
            @RequestParam String status) {
        OrderResponse updatedOrder = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(ApiResponse.success("Order status updated successfully", updatedOrder));
    }

    @PutMapping("/{orderId}/payment")
    @Operation(summary = "Update order payment")
    public ResponseEntity<ApiResponse<OrderResponse>> updatePayment(
            @PathVariable Integer orderId,
            @RequestParam String paymentMethod) {
        OrderResponse updatedOrder = orderService.updatePayment(orderId, paymentMethod);
        return ResponseEntity.ok(ApiResponse.success("Order payment updated successfully", updatedOrder));
    }

    @PutMapping("/{orderId}/cancel")
    @Operation(summary = "Cancel order")
    public ResponseEntity<ApiResponse<Object>> cancelOrder(@PathVariable Integer orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok(ApiResponse.success("Order cancelled successfully", null));
    }

    @GetMapping("/calculate-amount")
    @Operation(summary = "Calculate order amount")
    public ResponseEntity<ApiResponse<BigDecimal>> calculateOrderAmount(
            @RequestParam List<Integer> productIds,
            @RequestParam List<Integer> quantities) {
        BigDecimal amount = orderService.calculateOrderAmount(productIds, quantities);
        return ResponseEntity.ok(ApiResponse.success("Order amount calculated", amount));
    }
}