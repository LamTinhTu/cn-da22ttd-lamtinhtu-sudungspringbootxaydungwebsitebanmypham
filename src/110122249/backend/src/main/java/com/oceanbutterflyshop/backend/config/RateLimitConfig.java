package com.oceanbutterflyshop.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Cấu hình Rate Limiting
 * 
 * Cấu hình giới hạn tốc độ cho các endpoint API khác nhau.
 * Có thể được ghi đè trong application.properties hoặc application-dev.properties
 */
@Configuration
@ConfigurationProperties(prefix = "rate-limit")
@Getter
@Setter
public class RateLimitConfig {
    
    /**
     * Giới hạn tốc độ mặc định toàn cục: 20 yêu cầu mỗi phút
     */
    private int defaultLimit = 20;
    
    /**
     * Khoảng thời gian tính bằng phút cho giới hạn tốc độ
     */
    private int timeWindowMinutes = 1;
    
    /**
     * Bật hoặc tắt giới hạn tốc độ toàn cục
     */
    private boolean enabled = true;
    
    /**
     * Giới hạn tốc độ riêng cho từng endpoint (yêu cầu mỗi phút)
     * Key: mẫu endpoint (ví dụ: "/api/v1/auth/login")
     * Value: giới hạn yêu cầu mỗi phút
     */
    private Map<String, Integer> endpoints = new HashMap<>();
    
    /**
     * Lấy giới hạn tốc độ cho endpoint cụ thể
     * Trả về giới hạn riêng cho endpoint nếu được cấu hình, nếu không trả về giới hạn mặc định
     */
    public int getLimitForEndpoint(String endpoint) {
        return endpoints.getOrDefault(endpoint, defaultLimit);
    }
}
