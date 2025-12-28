package com.oceanbutterflyshop.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer imageId;
    
    @Column(name = "image_name", nullable = false)
    private String imageName;
    
    @Column(name = "image_url", columnDefinition = "TEXT", nullable = false)
    private String imageURL;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
