package com.oceanbutterflyshop.backend.dtos;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
public class ImageDTO {
    private Integer imageId;
    
    @NotNull(message = "Product ID is required")
    private Integer productId;
    
    @NotBlank(message = "Image name is required")
    @Size(max = 100, message = "Image name must not exceed 100 characters")
    private String imageName;
    
    @NotBlank(message = "Image URL is required")
    @Size(max = 255, message = "Image URL must not exceed 255 characters")
    @Pattern(regexp = "^https?://.+\\.(jpg|jpeg|png|gif|webp)$", 
             message = "Image URL must be a valid HTTP/HTTPS URL ending with jpg, jpeg, png, gif, or webp")
    private String imageURL;
    
    // Dành cho response - thông tin sản phẩm
    private String productName;
}