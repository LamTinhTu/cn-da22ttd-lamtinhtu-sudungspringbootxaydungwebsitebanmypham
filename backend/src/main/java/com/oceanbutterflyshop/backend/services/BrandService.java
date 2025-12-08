package com.oceanbutterflyshop.backend.services;

import java.util.List;

import com.oceanbutterflyshop.backend.dtos.request.BrandRequest;
import com.oceanbutterflyshop.backend.dtos.response.BrandResponse;

public interface BrandService {
    List<BrandResponse> getAllBrands();
    BrandResponse getBrandById(Integer brandId);
    BrandResponse createBrand(BrandRequest brandRequest);
    BrandResponse updateBrand(Integer brandId, BrandRequest brandRequest);
    void deleteBrand(Integer brandId);
}
