package com.oceanbutterflyshop.backend.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.oceanbutterflyshop.backend.dtos.request.ProductRequestDTO;
import com.oceanbutterflyshop.backend.dtos.response.ProductResponse;
import com.oceanbutterflyshop.backend.enums.ProductStatus;
import com.oceanbutterflyshop.backend.enums.ProductCategory;

public interface ProductService {
    Page<ProductResponse> getAllProductsPaginated(
        Pageable pageable,
        String keyword,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Integer brandId,
        ProductStatus status,
        ProductCategory category
    );
    ProductResponse getProductById(Integer productId);
    ProductResponse createProduct(ProductRequestDTO productRequest);
    ProductResponse updateProduct(Integer productId, ProductRequestDTO productRequest);
    void deleteProduct(Integer productId);
    List<ProductResponse> getBestSellingProducts(int limit);
}
