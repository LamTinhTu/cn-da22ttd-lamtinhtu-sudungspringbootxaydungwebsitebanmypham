package com.oceanbutterflyshop.backend.services;

import java.util.List;

import com.oceanbutterflyshop.backend.dtos.request.ProductRequest;
import com.oceanbutterflyshop.backend.dtos.response.ProductResponse;

public interface ProductService {
    List<ProductResponse> getAllProducts();
    ProductResponse getProductById(Integer productId);
    ProductResponse createProduct(ProductRequest productRequest);
    ProductResponse updateProduct(Integer productId, ProductRequest productRequest);
    void deleteProduct(Integer productId);
}
