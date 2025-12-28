package com.oceanbutterflyshop.backend.services.impl;

import com.oceanbutterflyshop.backend.dtos.response.DashboardStatsResponse;
import com.oceanbutterflyshop.backend.dtos.response.RecentOrderResponse;
import com.oceanbutterflyshop.backend.dtos.response.TopProductResponse;
import com.oceanbutterflyshop.backend.entities.Order;
import com.oceanbutterflyshop.backend.enums.OrderStatus;
import com.oceanbutterflyshop.backend.repositories.OrderItemRepository;
import com.oceanbutterflyshop.backend.repositories.OrderRepository;
import com.oceanbutterflyshop.backend.repositories.ProductRepository;
import com.oceanbutterflyshop.backend.repositories.UserRepository;
import com.oceanbutterflyshop.backend.services.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {
    
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public DashboardStatsResponse getDashboardStats() {
        // Tổng doanh thu từ các đơn hàng đã giao
        BigDecimal totalRevenue = orderRepository.findAll().stream()
                .filter(order -> order.getOrderStatus() == OrderStatus.DELIVERED)
                .map(Order::getOrderAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Tổng số đơn hàng
        long totalOrders = orderRepository.count();
        
        // Số đơn hàng mới (trạng thái NEW)
        long newOrders = orderRepository.findAll().stream()
                .filter(order -> order.getOrderStatus() == OrderStatus.NEW)
                .count();
        
        // Tổng số khách hàng (role = CUS)
        long totalCustomers = userRepository.findByRole_RoleCode("CUS").size();
        
        // Tổng số sản phẩm
        long totalProducts = productRepository.count();
        
        // Sản phẩm sắp hết hàng (< 10)
        long lowStockProducts = productRepository.findAll().stream()
                .filter(p -> p.getQuantityStock() < 10)
                .count();
        
        // Calculate trends (simplified - comparing with last month)
        LocalDate lastMonthStart = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        LocalDate lastMonthEnd = LocalDate.now().minusMonths(1).withDayOfMonth(
                LocalDate.now().minusMonths(1).lengthOfMonth());
        
        BigDecimal lastMonthRevenue = orderRepository.findAll().stream()
                .filter(order -> order.getOrderStatus() == OrderStatus.DELIVERED)
                .filter(order -> order.getOrderDate() != null &&
                        !order.getOrderDate().isBefore(lastMonthStart) &&
                        !order.getOrderDate().isAfter(lastMonthEnd))
                .map(Order::getOrderAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        double revenueTrend = calculateTrend(totalRevenue, lastMonthRevenue);
        
        return DashboardStatsResponse.builder()
                .totalRevenue(totalRevenue)
                .totalOrders((int) totalOrders)
                .newOrders((int) newOrders)
                .totalCustomers((int) totalCustomers)
                .totalProducts((int) totalProducts)
                .lowStockProducts((int) lowStockProducts)
                .revenueTrend(revenueTrend)
                .ordersTrend(8.2)  // Mock data
                .customersTrend(5.1) // Mock data
                .productsTrend(-2.4) // Mock data
                .build();
    }

    @Override
    public List<RecentOrderResponse> getRecentOrders(int limit) {
        List<Order> orders = orderRepository.findAll(
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "orderDate"))
        ).getContent();
        
        return orders.stream()
                .map(order -> RecentOrderResponse.builder()
                        .orderId(order.getOrderId())
                        .orderCode(order.getOrderCode())
                        .customerName(order.getUser().getUserName())
                        .orderDate(order.getOrderDate())
                        .totalAmount(order.getOrderAmount())
                        .orderStatus(order.getOrderStatus().name())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<TopProductResponse> getTopSellingProducts(int limit) {
        List<Integer> productIds = orderItemRepository.findBestSellingProductIds();
        
        return productIds.stream()
                .limit(limit)
                .map(id -> productRepository.findById(id).orElse(null))
                .filter(product -> product != null)
                .map(product -> {
                    // Tính tổng số lượng đã bán
                    int totalSold = orderItemRepository.findByProductProductId(product.getProductId())
                            .stream()
                            .mapToInt(item -> item.getItemQuantity())
                            .sum();
                    
                    // Lấy URL hình ảnh đầu tiên
                    String imageUrl = product.getImages().isEmpty() ? null : 
                            product.getImages().get(0).getImageURL();
                    
                    return TopProductResponse.builder()
                            .productId(product.getProductId())
                            .productCode(product.getProductCode())
                            .productName(product.getProductName())
                            .productPrice(product.getProductPrice())
                            .totalSold(totalSold)
                            .imageUrl(imageUrl)
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    private double calculateTrend(BigDecimal current, BigDecimal previous) {
        if (previous.compareTo(BigDecimal.ZERO) == 0) {
            return current.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0.0;
        }
        BigDecimal diff = current.subtract(previous);
        return diff.divide(previous, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }
}
