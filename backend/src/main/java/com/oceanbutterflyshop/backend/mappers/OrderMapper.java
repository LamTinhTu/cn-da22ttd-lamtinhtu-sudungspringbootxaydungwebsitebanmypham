package com.oceanbutterflyshop.backend.mappers;

import org.springframework.stereotype.Component;

import com.oceanbutterflyshop.backend.dtos.request.OrderItemRequest;
import com.oceanbutterflyshop.backend.dtos.request.OrderRequest;
import com.oceanbutterflyshop.backend.dtos.response.OrderItemResponse;
import com.oceanbutterflyshop.backend.dtos.response.OrderResponse;
import com.oceanbutterflyshop.backend.entities.Order;
import com.oceanbutterflyshop.backend.entities.OrderItem;
import com.oceanbutterflyshop.backend.entities.Product;
import com.oceanbutterflyshop.backend.entities.User;
import com.oceanbutterflyshop.backend.enums.OrderStatus;
import com.oceanbutterflyshop.backend.enums.PaymentMethod;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {
    
    /**
     * Convert OrderRequest to Order entity
     * Note: orderCode will be set by the service using CodeGeneratorUtils
     */
    public Order toEntity(OrderRequest request, User user) {
        if (request == null) {
            return null;
        }
        
        Order order = new Order();
        order.setOrderDate(LocalDate.now()); // Set current date
        order.setOrderStatus(OrderStatus.fromDisplayName(request.getOrderStatus())); // Convert string to enum
        order.setOrderAmount(request.getOrderAmount()); // Already BigDecimal
        order.setShippingAddress(request.getShippingAddress());
        order.setShippingPhone(request.getShippingPhone());
        order.setPaymentDate(request.getPaymentDate());
        
        // Convert payment method string to enum if provided
        if (request.getPaymentMethod() != null && !request.getPaymentMethod().isEmpty()) {
            order.setPaymentMethod(PaymentMethod.fromDisplayName(request.getPaymentMethod()));
        }
        
        order.setUser(user);
        
        return order;
    }
    
    /**
     * Update existing Order entity with OrderRequest data
     */
    public void updateEntity(Order order, OrderRequest request, User user) {
        if (order == null || request == null) {
            return;
        }
        
        order.setOrderStatus(OrderStatus.fromDisplayName(request.getOrderStatus())); // Convert string to enum
        order.setOrderAmount(request.getOrderAmount()); // Already BigDecimal
        order.setShippingAddress(request.getShippingAddress());
        order.setShippingPhone(request.getShippingPhone());
        order.setPaymentDate(request.getPaymentDate());
        
        // Convert payment method string to enum if provided
        if (request.getPaymentMethod() != null && !request.getPaymentMethod().isEmpty()) {
            order.setPaymentMethod(PaymentMethod.fromDisplayName(request.getPaymentMethod()));
        }
        
        order.setUser(user);
    }
    
    /**
     * Convert Order entity to OrderResponse
     */
    public OrderResponse toResponse(Order order) {
        if (order == null) {
            return null;
        }
        
        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getOrderId());
        response.setOrderCode(order.getOrderCode());
        response.setUserId(order.getUser().getUserId());
        response.setOrderDate(order.getOrderDate());
        response.setOrderStatus(order.getOrderStatus().getDisplayName()); // Convert enum to string
        response.setOrderAmount(order.getOrderAmount()); // Already BigDecimal
        response.setShippingAddress(order.getShippingAddress());
        response.setShippingPhone(order.getShippingPhone());
        response.setPaymentDate(order.getPaymentDate());
        
        // Convert payment method enum to string if present
        if (order.getPaymentMethod() != null) {
            response.setPaymentMethod(order.getPaymentMethod().getDisplayName());
        }
        
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        
        // Map user information
        if (order.getUser() != null) {
            User user = order.getUser();
            response.setUserName(user.getUserName());
            response.setUserPhone(user.getUserPhone());
        }
        
        // Map order items
        if (order.getOrderItems() != null) {
            List<OrderItemResponse> orderItemResponses = order.getOrderItems().stream()
                    .map(this::toOrderItemResponse)
                    .collect(Collectors.toList());
            response.setOrderItems(orderItemResponses);
        }
        
        return response;
    }
    
    /**
     * Convert OrderItemRequest to OrderItem entity
     */
    public OrderItem toOrderItemEntity(OrderItemRequest request, Order order, Product product) {
        if (request == null) {
            return null;
        }
        
        OrderItem orderItem = new OrderItem();
        orderItem.setItemQuantity(request.getItemQuantity());
        orderItem.setItemPrice(request.getItemPrice()); // Already BigDecimal
        orderItem.setUnitPrice(request.getUnitPrice()); // Remains Double per specification
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        
        return orderItem;
    }
    
    /**
     * Convert OrderItem entity to OrderItemResponse
     */
    public OrderItemResponse toOrderItemResponse(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }
        
        OrderItemResponse response = new OrderItemResponse();
        response.setOrderItemId(orderItem.getOrderItemId());
        response.setOrderId(orderItem.getOrder().getOrderId());
        response.setProductId(orderItem.getProduct().getProductId());
        response.setItemQuantity(orderItem.getItemQuantity());
        response.setItemPrice(orderItem.getItemPrice()); // Already BigDecimal
        response.setUnitPrice(orderItem.getUnitPrice().floatValue()); // Convert Double to Float
        response.setCreatedAt(orderItem.getCreatedAt());
        response.setUpdatedAt(orderItem.getUpdatedAt());
        
        // Map product information
        if (orderItem.getProduct() != null) {
            Product product = orderItem.getProduct();
            response.setProductName(product.getProductName());
            response.setProductCode(product.getProductCode());
            
            if (product.getBrand() != null) {
                response.setBrandName(product.getBrand().getBrandName());
            }
        }
        
        return response;
    }
}