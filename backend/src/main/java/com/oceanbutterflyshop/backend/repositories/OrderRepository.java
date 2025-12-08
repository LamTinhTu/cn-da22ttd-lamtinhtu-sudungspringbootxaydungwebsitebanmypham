package com.oceanbutterflyshop.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oceanbutterflyshop.backend.entities.Order;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUserUserId(Integer userId);
    Optional<Order> findByOrderCode(String orderCode);
    List<Order> findByOrderStatusOrderByOrderDateDesc(String orderStatus);
    boolean existsByOrderCode(String orderCode);
}