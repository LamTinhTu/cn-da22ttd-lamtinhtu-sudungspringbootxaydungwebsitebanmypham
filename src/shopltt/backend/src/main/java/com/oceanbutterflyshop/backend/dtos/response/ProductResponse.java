package com.oceanbutterflyshop.backend.dtos.response;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "Response DTO for product information")
public class ProductResponse {
    
    @Schema(description = "Product ID", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer productId;
    
    @Schema(description = "Auto-generated product code", example = "SP09123842", accessMode = Schema.AccessMode.READ_ONLY)
    private String productCode;
    
    @Schema(description = "Product name", example = "Rolex Submariner Classic")
    private String productName;
    
    @Schema(description = "Product description", example = "Luxury diving watch with automatic movement")
    private String productDescription;
    
    @Schema(description = "Product price", example = "8500.50", type = "number", format = "decimal")
    private BigDecimal productPrice;
    
    @Schema(description = "Available stock quantity", example = "15")
    private Integer quantityStock;
    
    @Schema(description = "Creation timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;
    
    @Schema(description = "Last update timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
    
    // Brand information
    @Schema(description = "Brand ID", example = "1")
    private Integer brandId;
    
    @Schema(description = "Brand code", example = "TH12345678", accessMode = Schema.AccessMode.READ_ONLY)
    private String brandCode;
    
    @Schema(description = "Brand name", example = "Omega")
    private String brandName;
    
    @Schema(description = "Brand description", example = "Swiss luxury watchmaker")
    private String brandDescription;
    
    // Product images
    @Schema(description = "Product images list")
    private List<ImageResponse> images;
}