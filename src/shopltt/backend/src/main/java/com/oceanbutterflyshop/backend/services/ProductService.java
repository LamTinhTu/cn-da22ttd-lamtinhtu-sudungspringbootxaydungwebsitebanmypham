package com.oceanbutterflyshop.backend.services;

import java.util.List;

import com.oceanbutterflyshop.backend.dtos.request.ProductRequestDTO;
import com.oceanbutterflyshop.backend.dtos.response.ProductResponse;

public interface ProductService {
    List<ProductResponse> getAllProducts();
    ProductResponse getProductById(Integer productId);
    ProductResponse createProduct(ProductRequestDTO productRequest);
    ProductResponse updateProduct(Integer productId, ProductRequestDTO productRequest);
    void deleteProduct(Integer productId);
}
