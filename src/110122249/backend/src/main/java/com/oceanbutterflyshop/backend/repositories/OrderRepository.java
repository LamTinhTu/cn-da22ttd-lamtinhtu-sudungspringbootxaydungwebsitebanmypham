package com.oceanbutterflyshop.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.oceanbutterflyshop.backend.entities.Order;
import com.oceanbutterflyshop.backend.enums.OrderStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUserUserIdOrderByOrderDateDesc(Integer userId);
    Optional<Order> findByOrderCode(String orderCode);
    List<Order> findByOrderStatusOrderByOrderDateDesc(OrderStatus orderStatus);
    boolean existsByOrderCode(String orderCode);
    
    /**
     * Kiểm tra xem user đã mua sản phẩm và đơn hàng đã được giao hay chưa
     * @param userId ID của user
     * @param productId ID của sản phẩm
     * @return true nếu user đã mua sản phẩm và đơn hàng đã giao
     */
    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END FROM Order o " +
           "JOIN o.orderItems oi " +
           "WHERE o.user.userId = :userId " +
           "AND oi.product.productId = :productId " +
           "AND o.orderStatus = 'DELIVERED'")
    boolean hasUserPurchasedAndReceivedProduct(@Param("userId") Integer userId, @Param("productId") Integer productId);
}