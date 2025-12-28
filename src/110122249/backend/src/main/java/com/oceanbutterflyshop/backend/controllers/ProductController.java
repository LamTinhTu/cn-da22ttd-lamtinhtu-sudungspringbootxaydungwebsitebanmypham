package com.oceanbutterflyshop.backend.controllers;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.oceanbutterflyshop.backend.dtos.ApiResponse;
import com.oceanbutterflyshop.backend.dtos.request.ProductRequestDTO;
import com.oceanbutterflyshop.backend.dtos.response.PageResponseWrapper;
import com.oceanbutterflyshop.backend.dtos.response.ProductResponse;
import com.oceanbutterflyshop.backend.enums.ProductStatus;
import com.oceanbutterflyshop.backend.enums.ProductCategory;
import com.oceanbutterflyshop.backend.services.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "2. Product Management", description = "CRUD operations for watch products. GET endpoints are public, Create/Update/Delete require ADMIN or STAFF role.")
public class ProductController {
    private final ProductService productService;

    @Operation(
        summary = "Get products with pagination and filtering", 
        description = """
            Retrieve products with pagination, sorting, and advanced filtering. Public endpoint.
            
            **Default Behavior:** Returns page 0 with 10 items, sorted by productId descending.
            
            **Pagination Parameters:**
            - page: Page number (0-indexed, default: 0)
            - size: Number of items per page (default: 10)
            - sort: Sort field and direction (format: field,direction e.g., productPrice,desc)
            
            **Sortable Fields:** productId, productName, productPrice, productStatus
            
            **Filter Parameters (Optional):**
            - keyword: Search in product name and description (case-insensitive)
            - minPrice: Minimum product price
            - maxPrice: Maximum product price
            - brandId: Filter by brand ID
            - status: Filter by product status (SELLING, OUT_OF_STOCK, DISCONTINUED)
            
            **Examples:**
            - GET /api/v1/products → Returns page 0, size 10 (default)
            - GET /api/v1/products?page=1&size=5 → Returns page 1, size 5
            - GET /api/v1/products?sort=productPrice,desc&keyword=rolex&minPrice=1000&maxPrice=5000
            """
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Products retrieved successfully with pagination")
    })
    @SecurityRequirement(name = "")  // Endpoint công khai
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseWrapper<ProductResponse>>> getAllProducts(
            @Parameter(hidden = true) @PageableDefault(size = 10, sort = "productId", direction = Sort.Direction.DESC) Pageable pageable,
            @Parameter(description = "Page number (0-indexed)", example = "0") @RequestParam(required = false, defaultValue = "0") Integer page,
            @Parameter(description = "Number of items per page", example = "10") @RequestParam(required = false, defaultValue = "10") Integer size,
            @Parameter(description = "Sort field and direction (format: field,direction)", example = "productId,desc") @RequestParam(required = false, defaultValue = "productId,desc") String sort,
            @Parameter(description = "Search keyword in product name and description") @RequestParam(required = false) String keyword,
            @Parameter(description = "Minimum product price") @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "Maximum product price") @RequestParam(required = false) BigDecimal maxPrice,
            @Parameter(description = "Filter by brand ID") @RequestParam(required = false) Integer brandId,
            @Parameter(description = "Filter by product status") @RequestParam(required = false) ProductStatus status,
            @Parameter(description = "Filter by product category (MAKEUP, SKINCARE, HAIRCARE)") @RequestParam(required = false) ProductCategory category
    ) {
        // Phân tích tham số sắp xếp thủ công
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc") 
            ? Sort.Direction.DESC : Sort.Direction.ASC;
        
        Pageable pageableRequest = org.springframework.data.domain.PageRequest.of(page, size, Sort.by(direction, sortField));
        Page<ProductResponse> productPage = productService.getAllProductsPaginated(
            pageableRequest, keyword, minPrice, maxPrice, brandId, status, category
        );
        
        PageResponseWrapper<ProductResponse> response = PageResponseWrapper.of(productPage);
        return ResponseEntity.ok(ApiResponse.success("Products retrieved successfully", response));
    }

    @Operation(summary = "Get product by ID", description = "Retrieve a single product by its ID. Public endpoint.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found")
    })
    @SecurityRequirement(name = "")  // Endpoint công khai
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Integer productId){
        ProductResponse product = productService.getProductById(productId);
        return ResponseEntity.ok(ApiResponse.success("Product retrieved successfully", product));
    }

    @Operation(summary = "Create a new product", description = "Create a new watch product with auto-generated code (SP prefix). Requires ADMIN or STAFF role.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Product created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN or STAFF role")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductRequestDTO productRequest){
        ProductResponse createdProduct = productService.createProduct(productRequest);
        return new ResponseEntity<>(
            ApiResponse.success("Product created successfully", createdProduct),
            HttpStatus.CREATED
        );
    }

    @Operation(summary = "Update product", description = "Update an existing product. Requires ADMIN or STAFF role.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product updated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN or STAFF role"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PutMapping("/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Integer productId,
            @Valid @RequestBody ProductRequestDTO productRequest){
        ProductResponse updatedProduct = productService.updateProduct(productId, productRequest);
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully", updatedProduct));
    }

    @Operation(summary = "Delete product", description = "Delete a product (soft delete). Requires ADMIN role.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product deleted successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found")
    })
    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deleteProduct(@PathVariable Integer productId){
        productService.deleteProduct(productId);
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully", null));
    }
    
    @Operation(
        summary = "Get best selling products",
        description = "Retrieve top best-selling products based on order quantity. Public endpoint."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Best selling products retrieved successfully")
    })
    @SecurityRequirement(name = "")  // Public endpoint
    @GetMapping("/best-sellers")
    public ResponseEntity<ApiResponse<java.util.List<ProductResponse>>> getBestSellers(
            @Parameter(description = "Number of products to return", example = "10")
            @RequestParam(required = false, defaultValue = "10") Integer limit
    ) {
        java.util.List<ProductResponse> bestSellers = productService.getBestSellingProducts(limit);
        return ResponseEntity.ok(ApiResponse.success("Best selling products retrieved successfully", bestSellers));
    }
}
