package com.oceanbutterflyshop.backend.mappers;

import org.springframework.stereotype.Component;

import com.oceanbutterflyshop.backend.dtos.request.BrandRequest;
import com.oceanbutterflyshop.backend.dtos.response.BrandResponse;
import com.oceanbutterflyshop.backend.entities.Brand;

@Component
public class BrandMapper {
    
    /**
     * Convert BrandRequest to Brand entity
     * Note: brandCode will be set by the service using CodeGeneratorUtils
     */
    public Brand toEntity(BrandRequest request) {
        if (request == null) {
            return null;
        }
        
        Brand brand = new Brand();
        brand.setBrandName(request.getBrandName());
        brand.setBrandDescription(request.getBrandDescription());
        
        return brand;
    }
    
    /**
     * Update existing Brand entity with BrandRequest data
     */
    public void updateEntity(Brand brand, BrandRequest request) {
        if (brand == null || request == null) {
            return;
        }
        
        brand.setBrandName(request.getBrandName());
        brand.setBrandDescription(request.getBrandDescription());
    }
    
    /**
     * Convert Brand entity to BrandResponse
     */
    public BrandResponse toResponse(Brand brand) {
        if (brand == null) {
            return null;
        }
        
        BrandResponse response = new BrandResponse();
        response.setBrandId(brand.getBrandId());
        response.setBrandCode(brand.getBrandCode());
        response.setBrandName(brand.getBrandName());
        response.setBrandDescription(brand.getBrandDescription());
        response.setCreatedAt(brand.getCreatedAt());
        response.setUpdatedAt(brand.getUpdatedAt());
        
        // Note: products mapping can be done if needed, but usually avoided to prevent circular references
        // response.setProducts(...);
        
        return response;
    }
}