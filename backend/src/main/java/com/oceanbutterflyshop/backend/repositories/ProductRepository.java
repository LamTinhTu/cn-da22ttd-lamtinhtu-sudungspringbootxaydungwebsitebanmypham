package com.oceanbutterflyshop.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oceanbutterflyshop.backend.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByProductNameContainingIgnoreCase(String productName);
    Optional<Product> findByProductCode(String productCode);
    List<Product> findByBrandBrandId(Integer brandId);
    boolean existsByProductCode(String productCode);
}
