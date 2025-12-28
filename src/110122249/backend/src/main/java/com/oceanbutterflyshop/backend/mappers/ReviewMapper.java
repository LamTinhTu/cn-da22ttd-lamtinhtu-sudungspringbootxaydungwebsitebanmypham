package com.oceanbutterflyshop.backend.mappers;

import org.springframework.stereotype.Component;
import com.oceanbutterflyshop.backend.dtos.request.ReviewRequestDTO;
import com.oceanbutterflyshop.backend.dtos.response.ReviewResponse;
import com.oceanbutterflyshop.backend.entities.Product;
import com.oceanbutterflyshop.backend.entities.Review;
import com.oceanbutterflyshop.backend.entities.User;

@Component
public class ReviewMapper {
    
    public Review toEntity(ReviewRequestDTO request, Product product, User user) {
        if (request == null) {
            return null;
        }
        
        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        
        return review;
    }
    
    public void updateEntity(Review review, ReviewRequestDTO request) {
        if (review == null || request == null) {
            return;
        }
        
        review.setRating(request.getRating());
        review.setComment(request.getComment());
    }
    
    public ReviewResponse toResponse(Review review) {
        if (review == null) {
            return null;
        }
        
        ReviewResponse response = new ReviewResponse();
        response.setReviewId(review.getReviewId());
        response.setProductId(review.getProduct().getProductId());
        response.setProductName(review.getProduct().getProductName());
        response.setUserId(review.getUser().getUserId());
        response.setUserName(review.getUser().getUserName());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setCreatedAt(review.getCreatedAt());
        response.setUpdatedAt(review.getUpdatedAt());
        
        return response;
    }
}
