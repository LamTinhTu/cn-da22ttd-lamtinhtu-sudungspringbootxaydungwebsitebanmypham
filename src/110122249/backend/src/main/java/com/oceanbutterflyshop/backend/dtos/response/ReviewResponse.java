package com.oceanbutterflyshop.backend.dtos.response;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Data
@Schema(description = "Response DTO for review information")
public class ReviewResponse {
    
    @Schema(description = "Review ID", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer reviewId;
    
    @Schema(description = "Product ID", example = "1")
    private Integer productId;
    
    @Schema(description = "Product name", example = "Kem dưỡng da")
    private String productName;
    
    @Schema(description = "User ID", example = "1")
    private Integer userId;
    
    @Schema(description = "User name", example = "Nguyễn Văn A")
    private String userName;
    
    @Schema(description = "Rating from 1 to 5", example = "5")
    private Integer rating;
    
    @Schema(description = "Review comment", example = "Great product!")
    private String comment;
    
    @Schema(description = "Creation timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;
    
    @Schema(description = "Last update timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
}
