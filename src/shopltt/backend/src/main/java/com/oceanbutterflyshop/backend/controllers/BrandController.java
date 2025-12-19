package com.oceanbutterflyshop.backend.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.oceanbutterflyshop.backend.dtos.ApiResponse;
import com.oceanbutterflyshop.backend.dtos.request.BrandRequest;
import com.oceanbutterflyshop.backend.dtos.response.BrandResponse;
import com.oceanbutterflyshop.backend.services.BrandService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
@Tag(name = "Brand Management", description = "APIs for managing brands")
public class BrandController {
    private final BrandService brandService;

    @Operation(summary = "Get all brands")
    @GetMapping
    public ResponseEntity<ApiResponse<List<BrandResponse>>> getAllBrands() {
        List<BrandResponse> brands = brandService.getAllBrands();
        return ResponseEntity.ok(ApiResponse.success("Brands retrieved successfully", brands));
    }
    
    @Operation(summary = "Get brand by ID")
    @GetMapping("/{brandId}")
    public ResponseEntity<ApiResponse<BrandResponse>> getBrandById(@PathVariable Integer brandId) {
        BrandResponse brand = brandService.getBrandById(brandId);
        return ResponseEntity.ok(ApiResponse.success("Brand retrieved successfully", brand));
    }

    @Operation(summary = "Create a new brand")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<BrandResponse>> createBrand(@Valid @RequestBody BrandRequest brandRequest) {
        BrandResponse createdBrand = brandService.createBrand(brandRequest);
        return new ResponseEntity<>(
            ApiResponse.success("Brand created successfully", createdBrand),
            HttpStatus.CREATED
        );
    }

    @Operation(summary = "Update brand")
    @PutMapping("/{brandId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<BrandResponse>> updateBrand(
            @PathVariable Integer brandId,
            @Valid @RequestBody BrandRequest brandRequest){
        BrandResponse updatedBrand = brandService.updateBrand(brandId, brandRequest);
        return ResponseEntity.ok(ApiResponse.success("Brand updated successfully", updatedBrand));
    }

    @Operation(summary = "Delete brand")
    @DeleteMapping("/{brandId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<Object>> deleteBrand(@PathVariable Integer brandId){
        brandService.deleteBrand(brandId);
        return ResponseEntity.ok(ApiResponse.success("Brand deleted successfully", null));
    }
}
