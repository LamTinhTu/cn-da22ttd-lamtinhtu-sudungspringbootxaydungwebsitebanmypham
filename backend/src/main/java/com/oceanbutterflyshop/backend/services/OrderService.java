package com.oceanbutterflyshop.backend.services;

import java.math.BigDecimal;
import java.util.List;

import com.oceanbutterflyshop.backend.dtos.request.OrderRequest;
import com.oceanbutterflyshop.backend.dtos.response.OrderResponse;

public interface OrderService {
    List<OrderResponse> getAllOrders();
    List<OrderResponse> getOrdersByUserId(Integer userId);
    List<OrderResponse> getOrdersByStatus(String status);
    OrderResponse getOrderById(Integer orderId);
    OrderResponse getOrderByCode(String orderCode);
    OrderResponse createOrder(OrderRequest orderRequest);
    OrderResponse updateOrderStatus(Integer orderId, String newStatus);
    OrderResponse updatePayment(Integer orderId, String paymentMethod);
    void cancelOrder(Integer orderId);
    BigDecimal calculateOrderAmount(List<Integer> productIds, List<Integer> quantities);
}