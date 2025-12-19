package com.oceanbutterflyshop.backend.mappers;

import org.springframework.stereotype.Component;

import com.oceanbutterflyshop.backend.dtos.request.ProductRequestDTO;
import com.oceanbutterflyshop.backend.dtos.response.ImageResponse;
import com.oceanbutterflyshop.backend.dtos.response.ProductResponse;
import com.oceanbutterflyshop.backend.entities.Brand;
import com.oceanbutterflyshop.backend.entities.Product;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {
    
    /**
     * Convert ProductRequest to Product entity
     * Note: productCode will be set by the service using CodeGeneratorUtils
     */
    public Product toEntity(ProductRequestDTO request, Brand brand) {
        if (request == null) {
            return null;
        }
        
        Product product = new Product();
        product.setProductName(request.getProductName());
        product.setProductDescription(request.getProductDescription());
        product.setProductPrice(request.getProductPrice()); // Already BigDecimal
        product.setQuantityStock(request.getQuantityStock());
        product.setProductStatus(request.getProductStatus());
        product.setBrand(brand);
        
        return product;
    }
    
    /**
     * Update existing Product entity with ProductRequest data
     */
    public void updateEntity(Product product, ProductRequestDTO request, Brand brand) {
        if (product == null || request == null) {
            return;
        }
        
        product.setProductName(request.getProductName());
        product.setProductDescription(request.getProductDescription());
        product.setProductPrice(request.getProductPrice()); // Already BigDecimal
        product.setQuantityStock(request.getQuantityStock());
        product.setProductStatus(request.getProductStatus());
        product.setBrand(brand);
    }
    
    /**
     * Convert Product entity to ProductResponse
     */
    public ProductResponse toResponse(Product product) {
        if (product == null) {
            return null;
        }
        
        ProductResponse response = new ProductResponse();
        response.setProductId(product.getProductId());
        response.setProductCode(product.getProductCode());
        response.setProductName(product.getProductName());
        response.setProductDescription(product.getProductDescription());
        response.setProductPrice(product.getProductPrice()); // Already BigDecimal
        response.setQuantityStock(product.getQuantityStock());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        
        // Map brand information
        if (product.getBrand() != null) {
            Brand brand = product.getBrand();
            response.setBrandId(brand.getBrandId());
            response.setBrandCode(brand.getBrandCode());
            response.setBrandName(brand.getBrandName());
            response.setBrandDescription(brand.getBrandDescription());
        }
        
        // Map images
        if (product.getImages() != null) {
            List<ImageResponse> imageResponses = product.getImages().stream()
                    .map(image -> {
                        ImageResponse imageResponse = new ImageResponse();
                        imageResponse.setImageId(image.getImageId());
                        imageResponse.setProductId(image.getProduct().getProductId());
                        imageResponse.setImageName(image.getImageName());
                        imageResponse.setImageURL(image.getImageURL());
                        imageResponse.setProductName(image.getProduct().getProductName());
                        return imageResponse;
                    })
                    .collect(Collectors.toList());
            response.setImages(imageResponses);
        }
        
        return response;
    }
}