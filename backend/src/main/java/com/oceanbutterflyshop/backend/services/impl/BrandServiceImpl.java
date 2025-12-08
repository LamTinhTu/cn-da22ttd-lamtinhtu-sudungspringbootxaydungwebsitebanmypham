package com.oceanbutterflyshop.backend.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oceanbutterflyshop.backend.dtos.request.BrandRequest;
import com.oceanbutterflyshop.backend.dtos.response.BrandResponse;
import com.oceanbutterflyshop.backend.entities.Brand;
import com.oceanbutterflyshop.backend.exceptions.BadRequestException;
import com.oceanbutterflyshop.backend.mappers.BrandMapper;
import com.oceanbutterflyshop.backend.repositories.BrandRepository;
import com.oceanbutterflyshop.backend.services.BrandService;
import com.oceanbutterflyshop.backend.utils.CodeGeneratorUtils;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private BrandMapper brandMapper;
    
    @Autowired
    private CodeGeneratorUtils codeGeneratorUtils;

    @Override
    public List<BrandResponse> getAllBrands() {
        return brandRepository.findAll()
                .stream()
                .map(brandMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BrandResponse getBrandById(Integer brandId) {
        Optional<Brand> brandOpt = brandRepository.findById(brandId);
        if (brandOpt.isEmpty()) {
            throw new BadRequestException("Brand not found with id: " + brandId);
        }
        return brandMapper.toResponse(brandOpt.get());
    }

    @Override
    public BrandResponse createBrand(BrandRequest brandRequest) {
        Brand brand = brandMapper.toEntity(brandRequest);
        
        // Generate unique brand code
        String brandCode;
        do {
            brandCode = codeGeneratorUtils.generateCode("TH");
        } while (brandRepository.existsByBrandCode(brandCode));
        
        brand.setBrandCode(brandCode);
        Brand savedBrand = brandRepository.save(brand);
        return brandMapper.toResponse(savedBrand);
    }

    @Override
    public BrandResponse updateBrand(Integer brandId, BrandRequest brandRequest) {
        Optional<Brand> brandOpt = brandRepository.findById(brandId);
        if (brandOpt.isEmpty()) {
            throw new BadRequestException("Brand not found with id: " + brandId);
        }
        
        Brand existingBrand = brandOpt.get();
        brandMapper.updateEntity(existingBrand, brandRequest);
        
        Brand savedBrand = brandRepository.save(existingBrand);
        return brandMapper.toResponse(savedBrand);
    }

    @Override
    public void deleteBrand(Integer brandId) {
        Optional<Brand> brandOpt = brandRepository.findById(brandId);
        if (brandOpt.isEmpty()) {
            throw new BadRequestException("Brand not found with id: " + brandId);
        }
        brandRepository.deleteById(brandId);
    }
}