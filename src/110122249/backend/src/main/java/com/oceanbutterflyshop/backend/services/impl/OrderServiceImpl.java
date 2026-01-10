package com.oceanbutterflyshop.backend.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<OrderResponse> getAllOrdersPaginated(Pageable pageable) {
        Page<Order> orderPage = orderRepository.findAll(pageable);
        return orderPage.map(orderMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserId(Integer userId) {
        return orderRepository.findByUserUserIdOrderByOrderDateDesc(userId).stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByStatus(String status) {
        // Parse status from enum name (NEW, PROCESSING, DELIVERED, CANCELLED)
        OrderStatus orderStatus;
        try {
            orderStatus = OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid order status: " + status);
        }
        
        return orderRepository.findByOrderStatusOrderByOrderDateDesc(orderStatus).stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Integer orderId, String currentUsername) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        
        // Bảo vệ IDOR: Kiểm tra người dùng hiện tại có sở hữu đơn hàng này không (trừ khi là admin/staff)
        User currentUser = userRepository.findByUserAccount(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUsername));
        
        boolean isAdmin = "ADMIN".equals(currentUser.getRole().getRoleCode());
        boolean isStaff = "STAFF".equals(currentUser.getRole().getRoleCode());
        boolean isOwner = order.getUser().getUserId().equals(currentUser.getUserId());
        
        if (!isAdmin && !isStaff && !isOwner) {
            throw new com.oceanbutterflyshop.backend.exceptions.AccessDeniedException(
                "You do not have permission to access this order");
        }
        
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderByCode(String orderCode, String currentUsername) {
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "code", orderCode));
        
        // Bảo vệ IDOR: Kiểm tra người dùng hiện tại có sở hữu đơn hàng này không (trừ khi là admin/staff)
        User currentUser = userRepository.findByUserAccount(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUsername));
        
        boolean isAdmin = "ADMIN".equals(currentUser.getRole().getRoleCode());
        boolean isStaff = "STAFF".equals(currentUser.getRole().getRoleCode());
        boolean isOwner = order.getUser().getUserId().equals(currentUser.getUserId());
        
        if (!isAdmin && !isStaff && !isOwner) {
            throw new com.oceanbutterflyshop.backend.exceptions.AccessDeniedException(
                "You do not have permission to access this order");
        }
        
        return orderMapper.toResponse(order);
    }

    @Override
    public OrderResponse createOrder(OrderRequest orderRequest, String username) {
        // Lấy người dùng đã đăng nhập từ SecurityContext (truyền từ controller)
        User user = userRepository.findByUserAccount(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        // Tạo entity order từ request
        Order order = orderMapper.toEntity(orderRequest, user);
        
        // Tạo mã đơn hàng duy nhất
        String orderCode;
        do {
            orderCode = codeGeneratorUtils.generateOrderCode();
        } while (orderRepository.existsByOrderCode(orderCode));
        
        order.setOrderCode(orderCode);
        
        // Snapshot: Ghi lại địa chỉ và số điện thoại hiện tại của người dùng làm thông tin giao hàng
        // Đây là snapshot tại thời điểm tạo đơn hàng
        if (orderRequest.getShippingAddress() == null || orderRequest.getShippingAddress().isEmpty()) {
            order.setShippingAddress(user.getUserAddress());
        }
        if (orderRequest.getShippingPhone() == null || orderRequest.getShippingPhone().isEmpty()) {
            order.setShippingPhone(user.getUserPhone());
        }
        
        // Tính tổng tiền sử dụng các phép toán BigDecimal
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        // Xác thực order items và tính tổng
        for (OrderItemRequest itemRequest : orderRequest.getOrderItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", itemRequest.getProductId()));
            
            // Kiểm tra đủ số lượng tồn kho
            if (product.getQuantityStock() < itemRequest.getItemQuantity()) {
                throw new BadRequestException("Insufficient stock for product: " + product.getProductName() + 
                                            ". Available: " + product.getQuantityStock() + 
                                            ", Requested: " + itemRequest.getItemQuantity());
            }
            
            // Tính tổng tiền từng mục sử dụng các phép toán BigDecimal
            BigDecimal itemPrice = product.getProductPrice(); // BigDecimal
            BigDecimal quantity = BigDecimal.valueOf(itemRequest.getItemQuantity());
            BigDecimal itemTotal = itemPrice.multiply(quantity);
            
            totalAmount = totalAmount.add(itemTotal);
        }
        
        order.setOrderAmount(totalAmount);
        
        // Lưu đơn hàng trước
        Order savedOrder = orderRepository.save(order);
        
        // Tạo các mục đơn hàng và cập nhật tồn kho sản phẩm
        for (OrderItemRequest itemRequest : orderRequest.getOrderItems()) {
            Product product = productRepository.findById(itemRequest.getProductId()).get();
            
            // Tạo mục đơn hàng sử dụng mapper
            OrderItem orderItem = orderMapper.toOrderItemEntity(itemRequest, savedOrder, product);
            
            // Đặt giá đã tính (snapshot tại thời điểm mua hàng)
            BigDecimal itemPrice = product.getProductPrice();
            orderItem.setItemPrice(itemPrice); // BigDecimal
            orderItem.setUnitPrice(itemPrice.doubleValue()); // Chuyển đổi sang Double theo yêu cầu
            
            orderItemRepository.save(orderItem);
            
            // Cập nhật tồn kho sản phẩm
            product.setQuantityStock(product.getQuantityStock() - itemRequest.getItemQuantity());
            productRepository.save(product);
        }
        
        // Trả về đơn hàng hoàn chỉnh với các mục đã lưu
        Order finalOrder = orderRepository.findById(savedOrder.getOrderId()).get();
        return orderMapper.toResponse(finalOrder);
    }

    @Override
    public OrderResponse updateOrderStatus(Integer orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        
        OrderStatus currentStatus = order.getOrderStatus();
        
        // Xác thực trạng thái chuyển đổi
        if (currentStatus == OrderStatus.DELIVERED || currentStatus == OrderStatus.CANCELLED) {
            throw new BadRequestException("Cannot update status of a delivered or cancelled order");
        }
        
        // Parse newStatus from enum name (NEW, PROCESSING, DELIVERED, CANCELLED)
        OrderStatus newOrderStatus;
        try {
            newOrderStatus = OrderStatus.valueOf(newStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid order status: " + newStatus);
        }
        
        order.setOrderStatus(newOrderStatus);
        
        // Nếu đánh dấu là đã giao, đặt ngày thanh toán
        if (newOrderStatus == OrderStatus.DELIVERED && order.getPaymentDate() == null) {
            order.setPaymentDate(LocalDate.now());
        }
        
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toResponse(updatedOrder);
    }

    @Override
    public OrderResponse updatePayment(Integer orderId, String paymentMethod) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        
        // Parse paymentMethod from enum name (CASH, BANK_TRANSFER, CARD)
        PaymentMethod method;
        try {
            method = PaymentMethod.valueOf(paymentMethod.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid payment method: " + paymentMethod);
        }
        
        order.setPaymentMethod(method);
        // Only set payment date if it's not already set, or maybe we shouldn't set it here automatically?
        // Keeping existing behavior for now, but maybe we should remove it if we have explicit status update
        if (order.getPaymentDate() == null) {
             order.setPaymentDate(LocalDate.now());
        }
        
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toResponse(updatedOrder);
    }

    @Override
    public OrderResponse updatePaymentStatus(Integer orderId, Boolean isPaid) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        
        if (Boolean.TRUE.equals(isPaid)) {
            order.setPaymentDate(LocalDate.now());
        } else {
            order.setPaymentDate(null);
        }
        
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toResponse(updatedOrder);
    }

    @Override
    public void cancelOrder(Integer orderId, String currentUsername) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        
        // Bảo vệ IDOR: Kiểm tra xem người dùng hiện tại có sở hữu đơn hàng này không (trừ khi họ là admin/nhân viên)
        User currentUser = userRepository.findByUserAccount(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUsername));
        
        boolean isAdmin = "ADM".equals(currentUser.getRole().getRoleCode());
        boolean isStaff = "STF".equals(currentUser.getRole().getRoleCode());
        boolean isOwner = order.getUser().getUserId().equals(currentUser.getUserId());
        
        if (!isAdmin && !isStaff && !isOwner) {
            throw new com.oceanbutterflyshop.backend.exceptions.AccessDeniedException(
                "You do not have permission to cancel this order");
        }
        
        // Chỉ có thể hủy nếu trạng thái là NEW hoặc PROCESSING
        OrderStatus currentStatus = order.getOrderStatus();
        if (currentStatus != OrderStatus.NEW && currentStatus != OrderStatus.PROCESSING) {
            throw new BadRequestException("Cannot cancel order with status: " + currentStatus.getDisplayName());
        }
        
        // Khôi phục tồn kho sản phẩm
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
    public void deleteOrder(Integer orderId, String currentUsername) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        
        // Check permissions
        User currentUser = userRepository.findByUserAccount(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUsername));
        
        boolean isAdmin = "ADM".equals(currentUser.getRole().getRoleCode());
        
        if (!isAdmin) {
            throw new com.oceanbutterflyshop.backend.exceptions.AccessDeniedException(
                "Only administrators can delete orders");
        }

        // Allow deleting if status is CANCELLED or PROCESSING
        if (order.getOrderStatus() != OrderStatus.CANCELLED && order.getOrderStatus() != OrderStatus.PROCESSING) {
            throw new BadRequestException("Only cancelled or processing orders can be deleted");
        }
        
        // If deleting a PROCESSING order, restore stock
        if (order.getOrderStatus() == OrderStatus.PROCESSING) {
            List<OrderItem> orderItems = orderItemRepository.findByOrderOrderId(orderId);
            for (OrderItem item : orderItems) {
                Product product = item.getProduct();
                product.setQuantityStock(product.getQuantityStock() + item.getItemQuantity());
                productRepository.save(product);
            }
        }
        
        orderRepository.delete(order);
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