package com.oceanbutterflyshop.backend.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oceanbutterflyshop.backend.dtos.request.ProductRequest;
import com.oceanbutterflyshop.backend.dtos.response.ProductResponse;
import com.oceanbutterflyshop.backend.entities.Brand;
import com.oceanbutterflyshop.backend.entities.Product;
import com.oceanbutterflyshop.backend.exceptions.BadRequestException;
import com.oceanbutterflyshop.backend.exceptions.ResourceNotFoundException;
import com.oceanbutterflyshop.backend.mappers.ProductMapper;
import com.oceanbutterflyshop.backend.repositories.BrandRepository;
import com.oceanbutterflyshop.backend.repositories.ProductRepository;
import com.oceanbutterflyshop.backend.services.ProductService;
import com.oceanbutterflyshop.backend.utils.CodeGeneratorUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final ProductMapper productMapper;
    private final CodeGeneratorUtils codeGeneratorUtils;

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        return productMapper.toResponse(product);
    }

    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {
        // Validate brand exists
        Brand brand = brandRepository.findById(productRequest.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand", "id", productRequest.getBrandId()));
        
        // Convert request to entity
        Product product = productMapper.toEntity(productRequest, brand);
        
        // Generate unique product code
        String productCode;
        do {
            productCode = codeGeneratorUtils.generateProductCode();
        } while (productRepository.existsByProductCode(productCode));
        
        product.setProductCode(productCode);
        
        Product savedProduct = productRepository.save(product);
        return productMapper.toResponse(savedProduct);
    }

    @Override
    public ProductResponse updateProduct(Integer productId, ProductRequest productRequest) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        // Validate brand exists
        Brand brand = brandRepository.findById(productRequest.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand", "id", productRequest.getBrandId()));
        
        // Update entity with request data (code remains unchanged)
        productMapper.updateEntity(product, productRequest, brand);
        
        Product updatedProduct = productRepository.save(product);
        return productMapper.toResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        // Check if product has any order items (business logic)
        if (!product.getOrderItems().isEmpty()) {
            throw new BadRequestException("Cannot delete product that has been ordered");
        }
        
        productRepository.delete(product);
    }
}
