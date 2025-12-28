package com.oceanbutterflyshop.backend.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oceanbutterflyshop.backend.config.RateLimitConfig;
import com.oceanbutterflyshop.backend.dtos.ApiResponse;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate Limiting Filter sử dụng Bucket4j để giới hạn số lượng yêu cầu từ mỗi địa chỉ IP.
 * 
 * Triển khai thuật toán token bucket để giới hạn yêu cầu theo địa chỉ IP.
 * 
 * Tính năng:
 * - Giới hạn tốc độ dựa trên IP
 * - Giới hạn có thể cấu hình cho từng endpoint
 * - Trả về 429 Too Many Requests khi vượt quá giới hạn
 * - Tự động dọn dẹp các bucket đã hết hạn để tránh rò rỉ bộ nhớ
 * 
 * Mặc định: 20 yêu cầu mỗi phút cho mỗi IP
 * Có thể cấu hình qua application.properties:
 *   rate-limit.default-limit=20
 *   rate-limit.endpoints./api/v1/auth/login=5
 *   rate-limit.endpoints./api/v1/products=50
 */
@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class RateLimitingFilter implements Filter {

    private final RateLimitConfig rateLimitConfig;
    private final ObjectMapper objectMapper;
    
    // Bộ nhớ đệm để lưu trữ các bucket theo địa chỉ IP và endpoint
    // Key: "IP:endpoint", Value: Bucket
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
    
    // Theo dõi thời gian truy cập cuối cùng để dọn dẹp các mục không hoạt động
    private final Map<String, Long> lastAccessTime = new ConcurrentHashMap<>();
    
    // Ngưỡng dọn dẹp: 10 phút không hoạt động
    private static final long CLEANUP_THRESHOLD_MS = 10 * 60 * 1000;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Bỏ qua giới hạn tốc độ nếu bị vô hiệu hóa
        if (!rateLimitConfig.isEnabled()) {
            chain.doFilter(request, response);
            return;
        }
        
        // Lấy địa chỉ IP của client
        String clientIp = getClientIp(httpRequest);
        String endpoint = httpRequest.getRequestURI();
        String cacheKey = clientIp + ":" + endpoint;
        
        // Lấy hoặc tạo bucket cho IP và endpoint này
        Bucket bucket = resolveBucket(cacheKey, endpoint);
        
        // Thử tiêu thụ 1 token
        if (bucket.tryConsume(1)) {
            // Yêu cầu được phép
            log.debug("Rate limit OK for IP: {} on endpoint: {}", clientIp, endpoint);
            chain.doFilter(request, response);
        } else {
            // Vượt quá giới hạn tốc độ
            log.warn("Rate limit exceeded for IP: {} on endpoint: {}", clientIp, endpoint);
            handleRateLimitExceeded(httpResponse, endpoint);
        }
        
        // Dọn dẹp định kỳ các mục cũ
        cleanupOldEntries();
    }
    
    /**
     * Lấy hoặc tạo bucket cho khóa bộ nhớ đệm đã cho
     */
    private Bucket resolveBucket(String cacheKey, String endpoint) {
        lastAccessTime.put(cacheKey, System.currentTimeMillis());
        
        return cache.computeIfAbsent(cacheKey, key -> {
            int limit = rateLimitConfig.getLimitForEndpoint(endpoint);
            int timeWindowMinutes = rateLimitConfig.getTimeWindowMinutes();
            
            log.info("Creating new rate limit bucket for key: {} with limit: {}/{} minutes", 
                    key, limit, timeWindowMinutes);
            
            // Tạo token bucket với giới hạn đã chỉ định sử dụng builder pattern
            Bandwidth bandwidth = Bandwidth.builder()
                    .capacity(limit)
                    .refillGreedy(limit, Duration.ofMinutes(timeWindowMinutes))
                    .build();
            
            return Bucket.builder()
                    .addLimit(bandwidth)
                    .build();
        });
    }
    
    /**
     * Lấy địa chỉ IP của client từ yêu cầu
     * Xử lý các header proxy (X-Forwarded-For, X-Real-IP)
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // Xử lý nhiều địa chỉ IP trong X-Forwarded-For (lấy địa chỉ đầu tiên)
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }
    
    /**
     * Gửi phản hồi 429 Quá nhiều yêu cầu khi vượt quá giới hạn tốc độ
     */
    private void handleRateLimitExceeded(HttpServletResponse response, String endpoint) throws IOException {
        int limit = rateLimitConfig.getLimitForEndpoint(endpoint);
        int timeWindow = rateLimitConfig.getTimeWindowMinutes();
        
        ApiResponse<Void> apiResponse = ApiResponse.error(
            HttpStatus.TOO_MANY_REQUESTS.value(),
            String.format("Rate limit exceeded. Maximum %d requests per %d minute(s) allowed for this endpoint.", 
                    limit, timeWindow),
            "RATE_LIMIT_EXCEEDED"
        );
        
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        
        // Thêm header Retry-After (tính bằng giây)
        response.setHeader("Retry-After", String.valueOf(timeWindow * 60));
        
        // Thêm các header giới hạn tốc độ tùy chọn
        response.setHeader("X-RateLimit-Limit", String.valueOf(limit));
        response.setHeader("X-RateLimit-Remaining", "0");
        response.setHeader("X-RateLimit-Reset", String.valueOf(System.currentTimeMillis() + (timeWindow * 60 * 1000)));
        
        objectMapper.writeValue(response.getWriter(), apiResponse);
    }
    
    /**
     * Dọn dẹp các mục bucket cũ không được truy cập gần đây
     * Ngăn ngừa rò rỉ bộ nhớ từ các IP không hoạt động
     */
    private void cleanupOldEntries() {
        long now = System.currentTimeMillis();
        
        lastAccessTime.entrySet().removeIf(entry -> {
            boolean shouldRemove = (now - entry.getValue()) > CLEANUP_THRESHOLD_MS;
            if (shouldRemove) {
                cache.remove(entry.getKey());
                log.debug("Cleaned up expired rate limit entry: {}", entry.getKey());
            }
            return shouldRemove;
        });
    }
}
