package com.oceanbutterflyshop.backend.repositories;

import com.oceanbutterflyshop.backend.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findAllByOrderByCreatedAtDesc();
    
    @Query("SELECT r FROM Review r WHERE " +
           "LOWER(r.user.userName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.user.userAccount) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.product.productName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.comment) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "ORDER BY r.createdAt DESC")
    List<Review> searchReviews(String keyword);
    
    List<Review> findByProduct_ProductIdOrderByCreatedAtDesc(Integer productId);
    
    List<Review> findByUser_UserIdOrderByCreatedAtDesc(Integer userId);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.productId = :productId")
    Double getAverageRatingByProductId(Integer productId);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.productId = :productId")
    Long getReviewCountByProductId(Integer productId);
}
