package com.oceanbutterflyshop.backend.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Cấu hình XSS Sanitization
 * 
 * Tính năng này chặn tất cả các request body JSON đến TRƯỚC khi chúng được
 * giải tuần tự thành các DTO, cho phép chúng ta làm sạch các giá trị chuỗi
 * ngay tại điểm vào của ứng dụng.
 * 
 * Chiến lược:
 * 1. Phân tích JSON đầu vào thành cây JsonNode
 * 2. Duyệt đệ quy tất cả các nút
 * 3. Làm sạch tất cả các giá trị chuỗi bằng cách loại bỏ thẻ HTML và nội dung nguy hiểm
 * 4. Trả về JSON đã được chỉnh sửa để giải tuần tự
 * 
 * Ưu điểm so với Filter:
 * - Thực sự sửa đổi request body (Filter chỉ có thể phát hiện)
 * - Hoạt động với bộ chuyển đổi thông điệp của Spring
 * - Tích hợp tự động với Jackson
 * 
 * Lưu ý: Đây là phương pháp dựa trên regex đơn giản.
 * Đối với môi trường sản xuất, hãy xem xét sử dụng OWASP Java Encoder hoặc thư viện Jsoup.
 */
@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class XSSSanitizationAdvice extends RequestBodyAdviceAdapter {

    private final ObjectMapper objectMapper;

    // Thẻ HTML đơn giản loại bỏ (pattern)
    private static final Pattern HTML_TAGS = Pattern.compile("<[^>]*>");
    
    // Patterns nguy hiểm để loại bỏ hoàn toàn
    private static final Pattern[] DANGEROUS_PATTERNS = {
        Pattern.compile("<script[^>]*>.*?</script>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
        Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
        Pattern.compile("onerror\\s*=", Pattern.CASE_INSENSITIVE),
        Pattern.compile("onclick\\s*=", Pattern.CASE_INSENSITIVE),
        Pattern.compile("onload\\s*=", Pattern.CASE_INSENSITIVE),
        Pattern.compile("eval\\(", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<iframe[^>]*>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<embed[^>]*>", Pattern.CASE_INSENSITIVE)
    };

    /**
     * Áp dụng advice này cho tất cả các tham số request body
     */
    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType,
                          Class<? extends HttpMessageConverter<?>> converterType) {
        return true; // Áp dụng cho tất cả các request body
    }

    /**
     * Làm sạch request body trước khi nó được đọc và giải tuần tự
     */
    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter,
                                          Type targetType, Class<? extends HttpMessageConverter<?>> converterType)
            throws IOException {
        
        try {
            // Đọc luồng đầu vào gốc
            InputStream originalStream = inputMessage.getBody();
            
            // Phân tích cú pháp JSON
            JsonNode rootNode = objectMapper.readTree(originalStream);
            
            // Làm sạch đệ quy tất cả các nút JSON
            JsonNode sanitizedNode = sanitizeJsonNode(rootNode);
            
            // Chuyển đổi lại thành chuỗi JSON
            String sanitizedJson = objectMapper.writeValueAsString(sanitizedNode);
            
            // Kiểm tra xem có xảy ra làm sạch không
            String originalJson = objectMapper.writeValueAsString(rootNode);
            if (!originalJson.equals(sanitizedJson)) {
                log.warn("XSS content detected and sanitized in request body for: {}", 
                        parameter.getMethod().getName());
            }
            
            // Trả về wrapped input message với nội dung đã được làm sạch
            return new SanitizedHttpInputMessage(inputMessage, sanitizedJson);
            
        } catch (Exception e) {
            log.error("Error sanitizing request body", e);
            return inputMessage; // Trả về original nếu có lỗi
        }
    }

    /**
     * Đệ quy làm sạch tất cả các giá trị chuỗi trong cây JSON
     */
    private JsonNode sanitizeJsonNode(JsonNode node) {
        if (node.isTextual()) {
            // Làm sạch nút văn bản
            String sanitized = sanitizeString(node.asText());
            return new TextNode(sanitized);
            
        } else if (node.isObject()) {
            // Đệ quy làm sạch các trường đối tượng
            ObjectNode objectNode = (ObjectNode) node;
            ObjectNode sanitizedObject = objectMapper.createObjectNode();
            
            // Sử dụng properties() thay vì fields() đã bị loại bỏ - trả về Set, nên lặp trực tiếp
            for (Map.Entry<String, JsonNode> field : objectNode.properties()) {
                sanitizedObject.set(field.getKey(), sanitizeJsonNode(field.getValue()));
            }
            return sanitizedObject;
            
        } else if (node.isArray()) {
            // Đệ quy làm sạch các phần tử mảng
            ArrayNode arrayNode = (ArrayNode) node;
            ArrayNode sanitizedArray = objectMapper.createArrayNode();
            
            for (JsonNode element : arrayNode) {
                sanitizedArray.add(sanitizeJsonNode(element));
            }
            return sanitizedArray;
        }
        
        // Trả về các loại khác không thay đổi (số, boolean, null)
        return node;
    }

    /**
     * Làm sạch một giá trị chuỗi đơn
     * 
     * Chiến lược:
     * 1. Loại bỏ các mẫu nguy hiểm (thẻ script, trình xử lý sự kiện)
     * 2. Loại bỏ tất cả các thẻ HTML còn lại
     * 3. Cắt bớt khoảng trắng thừa
     */
    private String sanitizeString(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        
        String sanitized = input;
        
        // Loại bỏ các mẫu nguy hiểm trước tiên
        for (Pattern pattern : DANGEROUS_PATTERNS) {
            sanitized = pattern.matcher(sanitized).replaceAll("");
        }
        
        // Loại bỏ tất cả các thẻ HTML còn lại
        sanitized = HTML_TAGS.matcher(sanitized).replaceAll("");
        
        // Loại bỏ các byte null
        sanitized = sanitized.replace("\0", "");
        
        // Cắt bớt khoảng trắng thừa (nhưng giữ lại khoảng trắng đơn)
        sanitized = sanitized.trim();
        
        return sanitized;
    }

    /**
     * Wrapper HttpInputMessage tùy chỉnh cung cấp nội dung đã được làm sạch
     */
    private static class SanitizedHttpInputMessage implements HttpInputMessage {
        private final HttpInputMessage delegate;
        private final String sanitizedBody;

        public SanitizedHttpInputMessage(HttpInputMessage delegate, String sanitizedBody) {
            this.delegate = delegate;
            this.sanitizedBody = sanitizedBody;
        }

        @Override
        public InputStream getBody() throws IOException {
            return new ByteArrayInputStream(sanitizedBody.getBytes(StandardCharsets.UTF_8));
        }

        @Override
        public org.springframework.http.HttpHeaders getHeaders() {
            return delegate.getHeaders();
        }
    }
}
