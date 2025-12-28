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
import com.oceanbutterflyshop.backend.exceptions.BadRequestException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {
    
    /**
     * Chuyển OrderRequest thành Order entity
     * Note: orderCode sẽ được thiết lập bởi service sử dụng CodeGeneratorUtils
     */
    public Order toEntity(OrderRequest request, User user) {
        if (request == null) {
            return null;
        }
        
        Order order = new Order();
        order.setOrderDate(LocalDate.now()); // Thiết lập ngày hiện tại
        
        // Parse orderStatus từ enum name (NEW, PROCESSING, DELIVERED, CANCELLED)
        try {
            order.setOrderStatus(OrderStatus.valueOf(request.getOrderStatus().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid order status: " + request.getOrderStatus());
        }
        
        order.setOrderAmount(request.getOrderAmount()); // Đã là BigDecimal
        order.setShippingAddress(request.getShippingAddress());
        order.setShippingPhone(request.getShippingPhone());
        order.setPaymentDate(request.getPaymentDate());
        
        // Parse paymentMethod từ enum name (CASH, BANK_TRANSFER, CARD)
        if (request.getPaymentMethod() != null && !request.getPaymentMethod().isEmpty()) {
            try {
                order.setPaymentMethod(PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid payment method: " + request.getPaymentMethod());
            }
        }
        
        order.setUser(user);
        
        return order;
    }
    
    /**
     * Cập nhật entity Order hiện có với dữ liệu từ OrderRequest
     */
    public void updateEntity(Order order, OrderRequest request, User user) {
        if (order == null || request == null) {
            return;
        }
        
        // Parse orderStatus từ enum name (NEW, PROCESSING, DELIVERED, CANCELLED)
        try {
            order.setOrderStatus(OrderStatus.valueOf(request.getOrderStatus().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid order status: " + request.getOrderStatus());
        }
        
        order.setOrderAmount(request.getOrderAmount()); // Đã là BigDecimal
        order.setShippingAddress(request.getShippingAddress());
        order.setShippingPhone(request.getShippingPhone());
        order.setPaymentDate(request.getPaymentDate());
        
        // Parse paymentMethod từ enum name (CASH, BANK_TRANSFER, CARD)
        if (request.getPaymentMethod() != null && !request.getPaymentMethod().isEmpty()) {
            try {
                order.setPaymentMethod(PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid payment method: " + request.getPaymentMethod());
            }
        }
        
        order.setUser(user);
    }
    
    /**
     * Chuyển đổi entity Order thành OrderResponse
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
        response.setOrderStatus(order.getOrderStatus().name()); // Trả về enum name (NEW, PROCESSING, etc.)
        response.setOrderAmount(order.getOrderAmount()); // Đã là BigDecimal
        response.setShippingAddress(order.getShippingAddress());
        response.setShippingPhone(order.getShippingPhone());
        response.setPaymentDate(order.getPaymentDate());
        
        // Chuyển đổi enum phương thức thanh toán thành enum name
        if (order.getPaymentMethod() != null) {
            response.setPaymentMethod(order.getPaymentMethod().name()); // Trả về enum name (CASH, BANK_TRANSFER, CARD)
        }
        
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        
        // Cập nhật thông tin người dùng
        if (order.getUser() != null) {
            User user = order.getUser();
            response.setUserName(user.getUserName());
            response.setUserPhone(user.getUserPhone());
        }
        
        // Cập nhật các mục đơn hàng
        if (order.getOrderItems() != null) {
            List<OrderItemResponse> orderItemResponses = order.getOrderItems().stream()
                    .map(this::toOrderItemResponse)
                    .collect(Collectors.toList());
            response.setOrderItems(orderItemResponses);
        }
        
        return response;
    }
    
    /**
     * Chuyển đổi OrderItemRequest thành entity OrderItem
     */
    public OrderItem toOrderItemEntity(OrderItemRequest request, Order order, Product product) {
        if (request == null) {
            return null;
        }
        
        OrderItem orderItem = new OrderItem();
        orderItem.setItemQuantity(request.getItemQuantity());
        orderItem.setItemPrice(request.getItemPrice()); // Đã là BigDecimal
        orderItem.setUnitPrice(request.getUnitPrice()); // Vẫn là Double theo đặc tả
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        
        return orderItem;
    }
    
    /**
     * Chuyển đổi entity OrderItem thành OrderItemResponse
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
        response.setItemPrice(orderItem.getItemPrice()); // Đã là BigDecimal
        response.setUnitPrice(orderItem.getUnitPrice().floatValue()); // Chuyển đổi Double thành Float
        response.setCreatedAt(orderItem.getCreatedAt());
        response.setUpdatedAt(orderItem.getUpdatedAt());
        
        // Cập nhật thông tin sản phẩm
        if (orderItem.getProduct() != null) {
            Product product = orderItem.getProduct();
            response.setProductName(product.getProductName());
            response.setProductCode(product.getProductCode());
            
            if (product.getBrand() != null) {
                response.setBrandName(product.getBrand().getBrandName());
            }
            
            // Lấy ảnh đầu tiên của sản phẩm nếu có
            if (product.getImages() != null && !product.getImages().isEmpty()) {
                response.setProductImage(product.getImages().get(0).getImageURL());
            }
        }
        
        return response;
    }
}