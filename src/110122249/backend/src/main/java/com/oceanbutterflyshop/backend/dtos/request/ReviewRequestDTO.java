package com.oceanbutterflyshop.backend.dtos.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Request DTO for creating or updating a review")
public class ReviewRequestDTO {
    
    @NotNull(message = "Product ID is required")
    @Schema(description = "Product ID", example = "1")
    private Integer productId;
    
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    @Schema(description = "Rating from 1 to 5 stars", example = "5", minimum = "1", maximum = "5")
    private Integer rating;
    
    @Schema(description = "Review comment", example = "Great product!")
    private String comment;
}
