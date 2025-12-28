package com.oceanbutterflyshop.backend.mappers;

import org.springframework.stereotype.Component;

import com.oceanbutterflyshop.backend.dtos.request.BrandRequest;
import com.oceanbutterflyshop.backend.dtos.response.BrandResponse;
import com.oceanbutterflyshop.backend.entities.Brand;

@Component
public class BrandMapper {
    
    /**
     * Chuyển BrandRequest thành Brand entity
     * Note: brandCode sẽ được thiết lập bởi service sử dụng CodeGeneratorUtils
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
     * Cập nhật entity Brand hiện có với dữ liệu từ BrandRequest
     */
    public void updateEntity(Brand brand, BrandRequest request) {
        if (brand == null || request == null) {
            return;
        }
        
        brand.setBrandName(request.getBrandName());
        brand.setBrandDescription(request.getBrandDescription());
    }
    
    /**
     * Chuyển đổi entity Brand thành BrandResponse
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
        
        // Lưu ý: việc ánh xạ products có thể được thực hiện nếu cần, nhưng thường tránh để ngăn ngừa tham chiếu vòng
        // response.setProducts(...);
        
        return response;
    }
}