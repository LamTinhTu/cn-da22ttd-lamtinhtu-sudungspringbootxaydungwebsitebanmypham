package com.oceanbutterflyshop.backend.controllers;

import com.oceanbutterflyshop.backend.dtos.ApiResponse;
import com.oceanbutterflyshop.backend.dtos.request.ReviewRequestDTO;
import com.oceanbutterflyshop.backend.dtos.response.ReviewResponse;
import com.oceanbutterflyshop.backend.services.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Tag(name = "6. Review Management", description = "CRUD operations for product reviews")
public class ReviewController {
    
    private final ReviewService reviewService;
    
    @Operation(
        summary = "Get all reviews",
        description = "Retrieve all reviews in the system. Requires ADMIN role."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reviews retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getAllReviews(
            @RequestParam(required = false) String keyword) {
        List<ReviewResponse> reviews = reviewService.getAllReviews(keyword);
        return ResponseEntity.ok(ApiResponse.success("All reviews retrieved successfully", reviews));
    }
    
    @Operation(
        summary = "Get reviews by product ID",
        description = "Retrieve all reviews for a specific product. Public endpoint."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reviews retrieved successfully")
    })
    @SecurityRequirement(name = "")
    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getReviewsByProduct(
            @Parameter(description = "Product ID") @PathVariable Integer productId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByProductId(productId);
        return ResponseEntity.ok(ApiResponse.success("Reviews retrieved successfully", reviews));
    }
    
    @Operation(
        summary = "Get average rating for a product",
        description = "Get the average rating and review count for a product. Public endpoint."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Rating retrieved successfully")
    })
    @SecurityRequirement(name = "")
    @GetMapping("/product/{productId}/rating")
    public ResponseEntity<ApiResponse<Object>> getProductRating(
            @Parameter(description = "Product ID") @PathVariable Integer productId) {
        Double avgRating = reviewService.getAverageRating(productId);
        Long reviewCount = reviewService.getReviewCount(productId);
        
        java.util.Map<String, Object> ratingData = java.util.Map.of(
            "averageRating", avgRating != null ? avgRating : 0.0,
            "reviewCount", reviewCount != null ? reviewCount : 0L
        );
        
        return ResponseEntity.ok(ApiResponse.success("Product rating retrieved successfully", ratingData));
    }
    
    @Operation(
        summary = "Get user's reviews",
        description = "Get all reviews created by the authenticated user. Requires authentication."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User reviews retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/my-reviews")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getMyReviews(Authentication authentication) {
        String userAccount = authentication.getName();
        List<ReviewResponse> reviews = reviewService.getReviewsByUser(userAccount);
        return ResponseEntity.ok(ApiResponse.success("User reviews retrieved successfully", reviews));
    }
    
    @Operation(
        summary = "Create a review",
        description = "Create a new review for a product. Requires CUSTOMER role."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Review created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @Valid @RequestBody ReviewRequestDTO reviewRequest,
            Authentication authentication) {
        String userAccount = authentication.getName();
        ReviewResponse review = reviewService.createReview(reviewRequest, userAccount);
        return new ResponseEntity<>(
            ApiResponse.success("Review created successfully", review),
            HttpStatus.CREATED
        );
    }
    
    @Operation(
        summary = "Update a review",
        description = "Update an existing review. Users can only update their own reviews."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Review updated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error or not owner"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Review not found")
    })
    @PutMapping("/{reviewId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @Parameter(description = "Review ID") @PathVariable Integer reviewId,
            @Valid @RequestBody ReviewRequestDTO reviewRequest,
            Authentication authentication) {
        String userAccount = authentication.getName();
        ReviewResponse review = reviewService.updateReview(reviewId, reviewRequest, userAccount);
        return ResponseEntity.ok(ApiResponse.success("Review updated successfully", review));
    }
    
    @Operation(
        summary = "Delete a review",
        description = "Delete a review. Customers can delete their own reviews. Admins can delete any review."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Review deleted successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Not owner of review"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Review not found")
    })
    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deleteReview(
            @Parameter(description = "Review ID") @PathVariable Integer reviewId,
            Authentication authentication) {
        String userAccount = authentication.getName();
        reviewService.deleteReview(reviewId, userAccount);
        return ResponseEntity.ok(ApiResponse.success("Review deleted successfully", null));
    }
}
