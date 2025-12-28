package com.oceanbutterflyshop.backend.services;

import java.util.List;

import com.oceanbutterflyshop.backend.dtos.ImageDTO;

public interface ImageService {
    List<ImageDTO> getAllImages();
    List<ImageDTO> getImagesByProductId(Integer productId);
    ImageDTO getImageById(Integer imageId);
    ImageDTO createImage(ImageDTO imageDTO);
    ImageDTO updateImage(Integer imageId, ImageDTO imageDTO);
    void deleteImage(Integer imageId);
    void deleteImagesByProductId(Integer productId);
}