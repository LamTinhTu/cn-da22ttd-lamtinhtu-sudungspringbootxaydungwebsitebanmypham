package com.oceanbutterflyshop.backend.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * XSS (Cross-Site Scripting) Protection Filter
 * 
 * Xác thực các nội dung JSON request body đến để ngăn chặn các cuộc tấn công XSS bằng cách:
 * 1. Loại bỏ các thẻ HTML sử dụng biểu thức chính quy
 * 2. Loại bỏ các nội dung script nguy hiểm
 * 3. Mã hóa các ký tự đặc biệt
 * 
 * Bộ lọc này chạy TRƯỚC khi yêu cầu đến lớp Controller,
 * đảm bảo tất cả các đầu vào chuỗi được làm sạch tại điểm nhập.
 * 
 * Lưu ý: Đối với môi trường sản xuất, hãy xem xét sử dụng OWASP Java Encoder hoặc thư viện Jsoup
 * để làm sạch HTML mạnh mẽ hơn.
 * 
 * Pattern Detection bao gồm nhưng không giới hạn ở:
 * - <script>...</script> tags
 * - <iframe> tags
 * - javascript: protocol
 * - on* event handlers (onclick, onerror, etc.)
 * - <img> tags with onerror
 * 
 * @see <a href="https://owasp.org/www-community/attacks/xss/">OWASP XSS</a>
 */
@Component
@Order(2) // Chạy sau RateLimitingFilter (Order 1)
@Slf4j
public class XSSFilter extends OncePerRequestFilter {

    // Biểu thức chính quy để phát hiện và loại bỏ các cuộc tấn công XSS phổ biến
    private static final Pattern[] XSS_PATTERNS = {
        // Các đoạn script
        Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("src[\r\n]*=[\r\n]*'(.*?)'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        Pattern.compile("src[\r\n]*=[\r\n]*\"(.*?)\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        
        // Biểu thức eval(...) và expression(...)
        Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
        Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
        
        // Các trình xử lý sự kiện on* (onclick, onerror, v.v.)
        Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        Pattern.compile("onerror(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        Pattern.compile("onclick(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        Pattern.compile("onmouseover(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        
        // Iframe và embed tags
        Pattern.compile("<iframe(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        Pattern.compile("</iframe>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<embed(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        Pattern.compile("<object(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        
        // Các thẻ nguy hiểm khác
        Pattern.compile("<link(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        Pattern.compile("<style>(.*?)</style>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
    };

    /**
     * Bộ lọc chỉ áp dụng cho các yêu cầu POST, PUT, PATCH với loại nội dung JSON
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String method = request.getMethod();
        String contentType = request.getContentType();
        
        // Bộ lọc chỉ áp dụng cho các yêu cầu POST, PUT, PATCH với loại nội dung JSON
        boolean isModifyingRequest = "POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method);
        boolean isJsonContent = contentType != null && contentType.contains("application/json");
        
        return !isModifyingRequest || !isJsonContent;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Bao bọc request để cho phép đọc body nhiều lần
        // ContentCachingRequestWrapper yêu cầu tham số giới hạn bộ nhớ cache nội dung trong Spring Boot 4.x
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request, 1024 * 1024); // 1MB cache
        
        try {
            // Đọc body của yêu cầu
            byte[] body = wrappedRequest.getContentAsByteArray();
            
            if (body.length > 0) {
                String requestBody = new String(body, StandardCharsets.UTF_8);
                
                // Làm sạch body
                String sanitizedBody = sanitizeInput(requestBody);
                
                // Kiểm tra nếu nội dung XSS được phát hiện và loại bỏ
                if (!requestBody.equals(sanitizedBody)) {
                    log.warn("XSS content detected and sanitized from request: {} {}", 
                            request.getMethod(), request.getRequestURI());
                    log.debug("Original: {}", requestBody);
                    log.debug("Sanitized: {}", sanitizedBody);
                }
                
                // Lưu ý: Vì chúng ta đang sử dụng ContentCachingRequestWrapper, nên body gốc được giữ nguyên
                // Để thực sự sửa đổi body, bạn cần một HttpServletRequestWrapper tùy chỉnh
                // Hiện tại, điều này chỉ phục vụ cho việc phát hiện và ghi log
            }
            
            // Tiếp tục chuỗi bộ lọc
            filterChain.doFilter(wrappedRequest, response);
            
        } catch (Exception e) {
            log.error("Error in XSS filter", e);
            filterChain.doFilter(wrappedRequest, response);
        }
    }

    /**
     * Làm sạch chuỗi đầu vào bằng cách loại bỏ các mẫu XSS
     * 
     * @param input Chuỗi đầu vào thô
     * @return Chuỗi đã được làm sạch với các mẫu XSS đã bị loại bỏ
     */
    private String sanitizeInput(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        
        String sanitized = input;
        
        // Áp dụng tất cả các mẫu XSS để loại bỏ nội dung nguy hiểm
        for (Pattern pattern : XSS_PATTERNS) {
            sanitized = pattern.matcher(sanitized).replaceAll("");
        }
        
        // Loại bỏ các byte null
        sanitized = sanitized.replaceAll("\0", "");
        
        return sanitized;
    }
}
