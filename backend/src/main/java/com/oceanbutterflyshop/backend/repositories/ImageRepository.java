package com.oceanbutterflyshop.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oceanbutterflyshop.backend.entities.Image;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {
    List<Image> findByProductProductId(Integer productId);
    void deleteByProductProductId(Integer productId);
}