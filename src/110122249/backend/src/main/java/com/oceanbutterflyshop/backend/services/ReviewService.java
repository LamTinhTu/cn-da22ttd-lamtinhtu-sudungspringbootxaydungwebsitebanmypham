package com.oceanbutterflyshop.backend.services;

import com.oceanbutterflyshop.backend.dtos.request.ReviewRequestDTO;
import com.oceanbutterflyshop.backend.dtos.response.ReviewResponse;
import java.util.List;

public interface ReviewService {
    ReviewResponse createReview(ReviewRequestDTO reviewRequest, String userAccount);
    ReviewResponse updateReview(Integer reviewId, ReviewRequestDTO reviewRequest, String userAccount);
    void deleteReview(Integer reviewId, String userAccount);
    ReviewResponse getReviewById(Integer reviewId);
    List<ReviewResponse> getAllReviews(String keyword);
    List<ReviewResponse> getReviewsByProductId(Integer productId);
    List<ReviewResponse> getReviewsByUser(String userAccount);
    Double getAverageRating(Integer productId);
    Long getReviewCount(Integer productId);
}
