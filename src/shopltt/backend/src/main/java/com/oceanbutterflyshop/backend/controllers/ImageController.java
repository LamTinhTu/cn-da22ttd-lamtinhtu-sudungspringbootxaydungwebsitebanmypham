package com.oceanbutterflyshop.backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.RestController;

import com.oceanbutterflyshop.backend.dtos.ApiResponse;
import com.oceanbutterflyshop.backend.dtos.ImageDTO;
import com.oceanbutterflyshop.backend.services.ImageService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
@Tag(name = "Image Management", description = "APIs for managing images")
public class ImageController {

    private final ImageService imageService;

    @GetMapping
    @Operation(summary = "Get all images")
    public ResponseEntity<ApiResponse<List<ImageDTO>>> getAllImages() {
        List<ImageDTO> images = imageService.getAllImages();
        return ResponseEntity.ok(ApiResponse.success("Images retrieved successfully", images));
    }

    @GetMapping("/{imageId}")
    @Operation(summary = "Get image by ID")
    public ResponseEntity<ApiResponse<ImageDTO>> getImageById(@PathVariable Integer imageId) {
        ImageDTO image = imageService.getImageById(imageId);
        return ResponseEntity.ok(ApiResponse.success("Image retrieved successfully", image));
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get images by product ID")
    public ResponseEntity<ApiResponse<List<ImageDTO>>> getImagesByProductId(@PathVariable Integer productId) {
        List<ImageDTO> images = imageService.getImagesByProductId(productId);
        return ResponseEntity.ok(ApiResponse.success("Product images retrieved successfully", images));
    }

    @PostMapping
    @Operation(summary = "Create a new image")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<ImageDTO>> createImage(@Valid @RequestBody ImageDTO imageDTO) {
        ImageDTO createdImage = imageService.createImage(imageDTO);
        return new ResponseEntity<>(
            ApiResponse.success("Image created successfully", createdImage),
            HttpStatus.CREATED
        );
    }

    @PutMapping("/{imageId}")
    @Operation(summary = "Update image")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<ImageDTO>> updateImage(
            @PathVariable Integer imageId,
            @Valid @RequestBody ImageDTO imageDTO) {
        ImageDTO updatedImage = imageService.updateImage(imageId, imageDTO);
        return ResponseEntity.ok(ApiResponse.success("Image updated successfully", updatedImage));
    }

    @DeleteMapping("/{imageId}")
    @Operation(summary = "Delete image")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<Object>> deleteImage(@PathVariable Integer imageId) {
        imageService.deleteImage(imageId);
        return ResponseEntity.ok(ApiResponse.success("Image deleted successfully", null));
    }

    @DeleteMapping("/product/{productId}")
    @Operation(summary = "Delete all images by product ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<Object>> deleteImagesByProductId(@PathVariable Integer productId) {
        imageService.deleteImagesByProductId(productId);
        return ResponseEntity.ok(ApiResponse.success("Product images deleted successfully", null));
    }
}