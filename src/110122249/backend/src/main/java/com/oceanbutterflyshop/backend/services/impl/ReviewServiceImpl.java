package com.oceanbutterflyshop.backend.services.impl;

import com.oceanbutterflyshop.backend.dtos.request.ReviewRequestDTO;
import com.oceanbutterflyshop.backend.dtos.response.ReviewResponse;
import com.oceanbutterflyshop.backend.entities.Product;
import com.oceanbutterflyshop.backend.entities.Review;
import com.oceanbutterflyshop.backend.entities.User;
import com.oceanbutterflyshop.backend.exceptions.BadRequestException;
import com.oceanbutterflyshop.backend.exceptions.ResourceNotFoundException;
import com.oceanbutterflyshop.backend.mappers.ReviewMapper;
import com.oceanbutterflyshop.backend.repositories.OrderRepository;
import com.oceanbutterflyshop.backend.repositories.ProductRepository;
import com.oceanbutterflyshop.backend.repositories.ReviewRepository;
import com.oceanbutterflyshop.backend.repositories.UserRepository;
import com.oceanbutterflyshop.backend.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ReviewMapper reviewMapper;
    
    @Override
    public ReviewResponse createReview(ReviewRequestDTO reviewRequest, String userAccount) {
        Product product = productRepository.findById(reviewRequest.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", reviewRequest.getProductId()));
        
        User user = userRepository.findByUserAccount(userAccount)
                .orElseThrow(() -> new ResourceNotFoundException("User", "account", userAccount));
        
        // Kiểm tra xem user đã mua sản phẩm và đơn hàng đã được giao hay chưa
        boolean hasPurchasedAndReceived = orderRepository.hasUserPurchasedAndReceivedProduct(
                user.getUserId(), 
                reviewRequest.getProductId()
        );
        
        if (!hasPurchasedAndReceived) {
            throw new BadRequestException("Bạn chỉ có thể đánh giá sản phẩm sau khi đã đặt hàng và nhận hàng thành công");
        }
        
        Review review = reviewMapper.toEntity(reviewRequest, product, user);
        Review savedReview = reviewRepository.save(review);
        
        return reviewMapper.toResponse(savedReview);
    }
    
    @Override
    public ReviewResponse updateReview(Integer reviewId, ReviewRequestDTO reviewRequest, String userAccount) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
        
        // Check if the user is the owner of the review
        if (!review.getUser().getUserAccount().equals(userAccount)) {
            throw new BadRequestException("You can only update your own reviews");
        }
        
        reviewMapper.updateEntity(review, reviewRequest);
        Review updatedReview = reviewRepository.save(review);
        
        return reviewMapper.toResponse(updatedReview);
    }
    
    @Override
    public void deleteReview(Integer reviewId, String userAccount) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
        
        User currentUser = userRepository.findByUserAccount(userAccount)
                .orElseThrow(() -> new ResourceNotFoundException("User", "account", userAccount));
        
        // Admin có quyền xóa bất kỳ đánh giá nào
        // User thường chỉ có thể xóa đánh giá của chính mình
        boolean isAdmin = currentUser.getRole() != null && 
                         ("ADM".equalsIgnoreCase(currentUser.getRole().getRoleCode()) || 
                          "ADMIN".equalsIgnoreCase(currentUser.getRole().getRoleCode()));
        boolean isOwner = review.getUser().getUserAccount().equals(userAccount);
        
        if (!isAdmin && !isOwner) {
            throw new BadRequestException("Bạn chỉ có thể xóa đánh giá của chính mình");
        }
        
        reviewRepository.delete(review);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ReviewResponse getReviewById(Integer reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
        
        return reviewMapper.toResponse(review);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getAllReviews(String keyword) {
        List<Review> reviews;
        if (keyword != null && !keyword.trim().isEmpty()) {
            reviews = reviewRepository.searchReviews(keyword.trim());
        } else {
            reviews = reviewRepository.findAllByOrderByCreatedAtDesc();
        }
        return reviews.stream()
                .map(reviewMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByProductId(Integer productId) {
        List<Review> reviews = reviewRepository.findByProduct_ProductIdOrderByCreatedAtDesc(productId);
        return reviews.stream()
                .map(reviewMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByUser(String userAccount) {
        User user = userRepository.findByUserAccount(userAccount)
                .orElseThrow(() -> new ResourceNotFoundException("User", "account", userAccount));
        
        List<Review> reviews = reviewRepository.findByUser_UserIdOrderByCreatedAtDesc(user.getUserId());
        return reviews.stream()
                .map(reviewMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Double getAverageRating(Integer productId) {
        Double avgRating = reviewRepository.getAverageRatingByProductId(productId);
        return avgRating != null ? avgRating : 0.0;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long getReviewCount(Integer productId) {
        return reviewRepository.getReviewCountByProductId(productId);
    }
}
