package com.oceanbutterflyshop.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "http://127.0.0.1:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .exposedHeaders("Authorization", "Content-Type")
                .allowCredentials(true)
                .maxAge(3600);
    }
    
    /**
     * Cấu hình phục vụ file tĩnh từ thư mục uploads
     * Cho phép truy cập hình ảnh qua URL: /uploads/{filename}
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/");
    }
    
    /**
     * Cấu hình ObjectMapper bean cho việc tuần tự hóa/giải tuần tự hóa JSON
     * Được sử dụng bởi RateLimitingFilter và XSSSanitizationAdvice
     * 
     * Sử dụng khởi tạo trực tiếp ObjectMapper vì Jackson2ObjectMapperBuilder
     * đã bị loại bỏ trong Spring Boot 4.x
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}