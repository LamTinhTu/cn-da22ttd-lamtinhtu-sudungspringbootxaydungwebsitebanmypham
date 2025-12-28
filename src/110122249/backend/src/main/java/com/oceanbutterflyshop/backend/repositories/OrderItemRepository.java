package com.oceanbutterflyshop.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.oceanbutterflyshop.backend.entities.OrderItem;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    List<OrderItem> findByOrderOrderId(Integer orderId);
    List<OrderItem> findByProductProductId(Integer productId);
    
    /**
     * Find best selling products by total quantity sold
     * @return List of product IDs ordered by total quantity sold descending
     */
    @Query("SELECT oi.product.productId FROM OrderItem oi " +
           "GROUP BY oi.product.productId " +
           "ORDER BY SUM(oi.itemQuantity) DESC")
    List<Integer> findBestSellingProductIds();
}