package com.oceanbutterflyshop.backend.dtos.response;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "Response DTO for brand information")
public class BrandResponse {
    
    @Schema(description = "Brand ID", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer brandId;
    
    @Schema(description = "Auto-generated brand code", example = "TH12345678", accessMode = Schema.AccessMode.READ_ONLY)
    private String brandCode;
    
    @Schema(description = "Brand name", example = "Omega")
    private String brandName;
    
    @Schema(description = "Brand description", example = "Swiss luxury watchmaker known for precision and innovation")
    private String brandDescription;
    
    @Schema(description = "Creation timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;
    
    @Schema(description = "Last update timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
    
    // Products in this brand
    @Schema(description = "Products in this brand", accessMode = Schema.AccessMode.READ_ONLY)
    private List<ProductResponse> products;
}