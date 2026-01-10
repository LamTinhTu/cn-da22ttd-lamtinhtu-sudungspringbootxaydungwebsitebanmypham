package com.oceanbutterflyshop.backend.services;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.oceanbutterflyshop.backend.dtos.request.OrderRequest;
import com.oceanbutterflyshop.backend.dtos.response.OrderResponse;

public interface OrderService {
    Page<OrderResponse> getAllOrdersPaginated(Pageable pageable);
    List<OrderResponse> getOrdersByUserId(Integer userId);
    List<OrderResponse> getOrdersByStatus(String status);
    OrderResponse getOrderById(Integer orderId, String currentUsername);
    OrderResponse getOrderByCode(String orderCode, String currentUsername);
    OrderResponse createOrder(OrderRequest orderRequest, String username);
    OrderResponse updateOrderStatus(Integer orderId, String newStatus);
    OrderResponse updatePayment(Integer orderId, String paymentMethod);
    OrderResponse updatePaymentStatus(Integer orderId, Boolean isPaid);
    void cancelOrder(Integer orderId, String currentUsername);
    void deleteOrder(Integer orderId, String currentUsername);
    BigDecimal calculateOrderAmount(List<Integer> productIds, List<Integer> quantities);
}