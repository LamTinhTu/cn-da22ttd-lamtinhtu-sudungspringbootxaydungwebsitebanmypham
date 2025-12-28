package com.oceanbutterflyshop.backend.services.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oceanbutterflyshop.backend.dtos.request.ProductRequestDTO;
import com.oceanbutterflyshop.backend.dtos.response.ProductResponse;
import com.oceanbutterflyshop.backend.entities.Brand;
import com.oceanbutterflyshop.backend.entities.Product;
import com.oceanbutterflyshop.backend.enums.ProductStatus;
import com.oceanbutterflyshop.backend.enums.ProductCategory;
import com.oceanbutterflyshop.backend.exceptions.BadRequestException;
import com.oceanbutterflyshop.backend.exceptions.ResourceNotFoundException;
import com.oceanbutterflyshop.backend.mappers.ProductMapper;
import com.oceanbutterflyshop.backend.repositories.BrandRepository;
import com.oceanbutterflyshop.backend.repositories.OrderItemRepository;
import com.oceanbutterflyshop.backend.repositories.ProductRepository;
import com.oceanbutterflyshop.backend.services.ProductService;
import com.oceanbutterflyshop.backend.specifications.ProductSpecification;
import com.oceanbutterflyshop.backend.utils.CodeGeneratorUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductMapper productMapper;
    private final CodeGeneratorUtils codeGeneratorUtils;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProductsPaginated(
            Pageable pageable,
            String keyword,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Integer brandId,
            ProductStatus status,
            ProductCategory category
    ) {
        Specification<Product> spec = ProductSpecification.filterProducts(
            keyword, minPrice, maxPrice, brandId, status, category
        );
        Page<Product> productPage = productRepository.findAll(spec, pageable);
        return productPage.map(productMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        return productMapper.toResponse(product);
    }

    @Override
    public ProductResponse createProduct(ProductRequestDTO productRequest) {
        // Xác thực thương hiệu tồn tại
        Brand brand = brandRepository.findById(productRequest.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand", "id", productRequest.getBrandId()));
        
        // Chuyển đổi request sang entity
        Product product = productMapper.toEntity(productRequest, brand);
        
        // Tạo mã sản phẩm duy nhất
        String productCode;
        do {
            productCode = codeGeneratorUtils.generateProductCode();
        } while (productRepository.existsByProductCode(productCode));
        
        product.setProductCode(productCode);
        
        Product savedProduct = productRepository.save(product);
        return productMapper.toResponse(savedProduct);
    }

    @Override
    public ProductResponse updateProduct(Integer productId, ProductRequestDTO productRequest) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        // Xác thực thương hiệu tồn tại
        Brand brand = brandRepository.findById(productRequest.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand", "id", productRequest.getBrandId()));
        
        // Cập nhật entity với dữ liệu từ request (mã vẫn giữ nguyên)
        productMapper.updateEntity(product, productRequest, brand);
        
        Product updatedProduct = productRepository.save(product);
        return productMapper.toResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        // Kiểm tra nếu sản phẩm có bất kỳ mục đơn hàng nào (logic nghiệp vụ)
        if (!product.getOrderItems().isEmpty()) {
            throw new BadRequestException("Không thể xóa sản phẩm này vì đã có trong đơn hàng");
        }
        
        productRepository.delete(product);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getBestSellingProducts(int limit) {
        List<Integer> productIds = orderItemRepository.findBestSellingProductIds();
        
        // Giới hạn số lượng sản phẩm
        List<Integer> limitedIds = productIds.stream()
                .limit(limit)
                .collect(Collectors.toList());
        
        // Lấy sản phẩm theo thứ tự bán chạy
        return limitedIds.stream()
                .map(id -> productRepository.findById(id)
                        .map(productMapper::toResponse)
                        .orElse(null))
                .filter(product -> product != null)
                .collect(Collectors.toList());
    }
}
