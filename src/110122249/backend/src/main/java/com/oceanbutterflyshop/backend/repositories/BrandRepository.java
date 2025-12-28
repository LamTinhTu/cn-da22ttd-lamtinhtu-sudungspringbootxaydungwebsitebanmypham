package com.oceanbutterflyshop.backend.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oceanbutterflyshop.backend.entities.Brand;
    
@Repository
public interface BrandRepository extends JpaRepository<Brand, Integer> {
    List<Brand> findByBrandNameContainingIgnoreCase(String brandName);
    Optional<Brand> findByBrandCode(String brandCode);
    boolean existsByBrandCode(String brandCode);
}
