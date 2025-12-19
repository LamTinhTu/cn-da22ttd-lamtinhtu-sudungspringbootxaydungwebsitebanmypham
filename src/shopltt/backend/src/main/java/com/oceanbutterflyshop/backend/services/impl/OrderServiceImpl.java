package com.oceanbutterflyshop.backend.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oceanbutterflyshop.backend.dtos.request.OrderItemRequest;
import com.oceanbutterflyshop.backend.dtos.request.OrderRequest;
import com.oceanbutterflyshop.backend.dtos.response.OrderResponse;
import com.oceanbutterflyshop.backend.entities.Order;
import com.oceanbutterflyshop.backend.entities.OrderItem;
import com.oceanbutterflyshop.backend.entities.Product;
import com.oceanbutterflyshop.backend.entities.User;
import com.oceanbutterflyshop.backend.enums.OrderStatus;
import com.oceanbutterflyshop.backend.enums.PaymentMethod;
import com.oceanbutterflyshop.backend.exceptions.BadRequestException;
import com.oceanbutterflyshop.backend.exceptions.ResourceNotFoundException;
import com.oceanbutterflyshop.backend.mappers.OrderMapper;
import com.oceanbutterflyshop.backend.repositories.OrderItemRepository;
import com.oceanbutterflyshop.backend.repositories.OrderRepository;
import com.oceanbutterflyshop.backend.repositories.ProductRepository;
import com.oceanbutterflyshop.backend.repositories.UserRepository;
import com.oceanbutterflyshop.backend.services.OrderService;
import com.oceanbutterflyshop.backend.utils.CodeGeneratorUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {
    
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;
    private final CodeGeneratorUtils codeGeneratorUtils;

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserId(Integer userId) {
        return orderRepository.findByUserUserId(userId).stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByStatus(String status) {
        return orderRepository.findByOrderStatusOrderByOrderDateDesc(status).stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderByCode(String orderCode) {
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "code", orderCode));
        return orderMapper.toResponse(order);
    }

    @Override
    public OrderResponse createOrder(OrderRequest orderRequest, String username) {
        // Get the logged-in user from SecurityContext (passed from controller)
        User user = userRepository.findByUserAccount(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        // Create order entity from request
        Order order = orderMapper.toEntity(orderRequest, user);
        
        // Generate unique order code
        String orderCode;
        do {
            orderCode = codeGeneratorUtils.generateOrderCode();
        } while (orderRepository.existsByOrderCode(orderCode));
        
        order.setOrderCode(orderCode);
        
        // Snapshot: Capture user's current address and phone as shipping info
        // These are snapshots at the time of order creation
        if (orderRequest.getShippingAddress() == null || orderRequest.getShippingAddress().isEmpty()) {
            order.setShippingAddress(user.getUserAddress());
        }
        if (orderRequest.getShippingPhone() == null || orderRequest.getShippingPhone().isEmpty()) {
            order.setShippingPhone(user.getUserPhone());
        }
        
        // Calculate total amount using BigDecimal operations
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        // Validate order items and calculate total
        for (OrderItemRequest itemRequest : orderRequest.getOrderItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", itemRequest.getProductId()));
            
            // Check if enough stock
            if (product.getQuantityStock() < itemRequest.getItemQuantity()) {
                throw new BadRequestException("Insufficient stock for product: " + product.getProductName() + 
                                            ". Available: " + product.getQuantityStock() + 
                                            ", Requested: " + itemRequest.getItemQuantity());
            }
            
            // Calculate item total using BigDecimal operations
            BigDecimal itemPrice = product.getProductPrice(); // Already BigDecimal
            BigDecimal quantity = BigDecimal.valueOf(itemRequest.getItemQuantity());
            BigDecimal itemTotal = itemPrice.multiply(quantity);
            
            totalAmount = totalAmount.add(itemTotal);
        }
        
        order.setOrderAmount(totalAmount);
        
        // Save the order first
        Order savedOrder = orderRepository.save(order);
        
        // Create order items
        for (OrderItemRequest itemRequest : orderRequest.getOrderItems()) {
            Product product = productRepository.findById(itemRequest.getProductId()).get();
            
            // Create order item using mapper
            OrderItem orderItem = orderMapper.toOrderItemEntity(itemRequest, savedOrder, product);
            
            // Set the calculated price (snapshot at time of purchase)
            BigDecimal itemPrice = product.getProductPrice();
            orderItem.setItemPrice(itemPrice); // BigDecimal
            orderItem.setUnitPrice(itemPrice.doubleValue()); // Convert to Double per spec
            
            orderItemRepository.save(orderItem);
            
            // Update product stock
            product.setQuantityStock(product.getQuantityStock() - itemRequest.getItemQuantity());
            productRepository.save(product);
        }
        
        // Return the complete order with items
        Order finalOrder = orderRepository.findById(savedOrder.getOrderId()).get();
        return orderMapper.toResponse(finalOrder);
    }

    @Override
    public OrderResponse updateOrderStatus(Integer orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        
        String currentStatus = order.getOrderStatus().getDisplayName();
        
        // Validate status transition
        if ("Delivered".equals(currentStatus) || "Cancelled".equals(currentStatus)) {
            throw new BadRequestException("Cannot update status of a delivered or cancelled order");
        }
        
        order.setOrderStatus(OrderStatus.fromDisplayName(newStatus));
        
        // If marking as delivered, set payment date
        if ("Delivered".equals(newStatus) && order.getPaymentDate() == null) {
            order.setPaymentDate(LocalDate.now());
        }
        
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toResponse(updatedOrder);
    }

    @Override
    public OrderResponse updatePayment(Integer orderId, String paymentMethod) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        
        order.setPaymentMethod(PaymentMethod.fromDisplayName(paymentMethod));
        order.setPaymentDate(LocalDate.now());
        
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toResponse(updatedOrder);
    }

    @Override
    public void cancelOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        
        // Can only cancel if status is "Mới" or "Đang xử lý"
        if (!("Mới".equals(order.getOrderStatus()) || "Đang xử lý".equals(order.getOrderStatus()))) {
            throw new BadRequestException("Cannot cancel order with status: " + order.getOrderStatus());
        }
        
        // Restore product stock
        List<OrderItem> orderItems = orderItemRepository.findByOrderOrderId(orderId);
        for (OrderItem item : orderItems) {
            Product product = item.getProduct();
            product.setQuantityStock(product.getQuantityStock() + item.getItemQuantity());
            productRepository.save(product);
        }
        
        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateOrderAmount(List<Integer> productIds, List<Integer> quantities) {
        if (productIds.size() != quantities.size()) {
            throw new BadRequestException("Product IDs and quantities must have the same size");
        }
        
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < productIds.size(); i++) {
            final Integer productId = productIds.get(i);
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
            BigDecimal itemTotal = product.getProductPrice().multiply(BigDecimal.valueOf(quantities.get(i)));
            total = total.add(itemTotal);
        }
        return total;
    }
}