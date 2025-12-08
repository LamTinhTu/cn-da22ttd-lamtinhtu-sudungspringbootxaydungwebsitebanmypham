package com.oceanbutterflyshop.backend.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oceanbutterflyshop.backend.dtos.ImageDTO;
import com.oceanbutterflyshop.backend.entities.Image;
import com.oceanbutterflyshop.backend.entities.Product;
import com.oceanbutterflyshop.backend.exceptions.ResourceNotFoundException;
import com.oceanbutterflyshop.backend.repositories.ImageRepository;
import com.oceanbutterflyshop.backend.repositories.ProductRepository;
import com.oceanbutterflyshop.backend.services.ImageService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageServiceImpl implements ImageService {
    
    private final ImageRepository imageRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ImageDTO> getAllImages() {
        return imageRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ImageDTO> getImagesByProductId(Integer productId) {
        return imageRepository.findByProductProductId(productId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ImageDTO getImageById(Integer imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image", "id", imageId));
        return convertToDTO(image);
    }

    @Override
    public ImageDTO createImage(ImageDTO imageDTO) {
        // Validate product exists
        Product product = productRepository.findById(imageDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", imageDTO.getProductId()));
        
        Image image = convertToEntity(imageDTO);
        image.setProduct(product);
        image = imageRepository.save(image);
        
        return convertToDTO(image);
    }

    @Override
    public ImageDTO updateImage(Integer imageId, ImageDTO imageDTO) {
        Image existingImage = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image", "id", imageId));
        
        // Validate product exists if product is being changed
        if (!existingImage.getProduct().getProductId().equals(imageDTO.getProductId())) {
            Product product = productRepository.findById(imageDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", imageDTO.getProductId()));
            existingImage.setProduct(product);
        }
        
        existingImage.setImageName(imageDTO.getImageName());
        existingImage.setImageURL(imageDTO.getImageURL());
        
        existingImage = imageRepository.save(existingImage);
        return convertToDTO(existingImage);
    }

    @Override
    public void deleteImage(Integer imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image", "id", imageId));
        imageRepository.delete(image);
    }

    @Override
    public void deleteImagesByProductId(Integer productId) {
        // Validate product exists
        productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        imageRepository.deleteByProductProductId(productId);
    }

    private ImageDTO convertToDTO(Image image) {
        ImageDTO dto = new ImageDTO();
        dto.setImageId(image.getImageId());
        dto.setProductId(image.getProduct().getProductId());
        dto.setProductName(image.getProduct().getProductName());
        dto.setImageName(image.getImageName());
        dto.setImageURL(image.getImageURL());
        return dto;
    }

    private Image convertToEntity(ImageDTO dto) {
        Image image = new Image();
        image.setImageName(dto.getImageName());
        image.setImageURL(dto.getImageURL());
        return image;
    }
}