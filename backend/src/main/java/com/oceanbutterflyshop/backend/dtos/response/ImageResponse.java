package com.oceanbutterflyshop.backend.dtos.response;

import lombok.Data;

@Data
public class ImageResponse {
    private Integer imageId;
    private Integer productId;
    private String imageName;
    private String imageURL;
    private String productName;
}