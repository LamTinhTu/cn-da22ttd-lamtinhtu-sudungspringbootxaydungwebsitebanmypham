package com.oceanbutterflyshop.backend.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.oceanbutterflyshop.backend.dtos.ApiResponse;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        return new ResponseEntity<>(
            ApiResponse.error(HttpStatus.NOT_FOUND.value(), ex.getMessage()),
            HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequestException(
            BadRequestException ex, WebRequest request) {
        return new ResponseEntity<>(
            ApiResponse.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage()),
            HttpStatus.BAD_REQUEST
        );
    }

    /**
     * Xử lý lỗi validation từ các annotation @Valid
     * Trả về định dạng phản hồi API chuẩn với chi tiết lỗi validation
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        
        // Trích xuất tất cả lỗi validation của các trường
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        // Xây dựng thông điệp lỗi đầu tiên cho trường message chính
        String mainMessage = errors.isEmpty() ? "Validation failed" : errors.values().iterator().next();
        
        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Validation failed: " + mainMessage)
                .data(errors)
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {
        return new ResponseEntity<>(
            ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Authentication failed", ex.getMessage()),
            HttpStatus.UNAUTHORIZED
        );
    }

    /**
     * Xử lý ngoại lệ AccessDeniedException của Spring Security (Lỗi phân quyền dựa trên vai trò)
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleSpringAccessDeniedException(
            org.springframework.security.access.AccessDeniedException ex, WebRequest request) {
        return new ResponseEntity<>(
            ApiResponse.error(HttpStatus.FORBIDDEN.value(), "Access denied", ex.getMessage()),
            HttpStatus.FORBIDDEN
        );
    }

    /**
     * Xử lý ngoại lệ AccessDeniedException tùy chỉnh (Bảo vệ IDOR)
     * Ngoại lệ này được ném ra khi người dùng cố gắng truy cập tài nguyên mà họ không sở hữu
     */
    @ExceptionHandler(com.oceanbutterflyshop.backend.exceptions.AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomAccessDeniedException(
            com.oceanbutterflyshop.backend.exceptions.AccessDeniedException ex, WebRequest request) {
        return new ResponseEntity<>(
            ApiResponse.error(HttpStatus.FORBIDDEN.value(), "Access denied", ex.getMessage()),
            HttpStatus.FORBIDDEN
        );
    }

    /**
     * Xử lý ngoại lệ DataIntegrityViolationException (Foreign Key Constraint)
     * Xảy ra khi cố gắng xóa hoặc cập nhật dữ liệu đang được tham chiếu bởi dữ liệu khác
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex, WebRequest request) {
        String message = "Không thể xóa dữ liệu này vì đang được sử dụng bởi dữ liệu khác";
        
        // Kiểm tra nếu là lỗi foreign key constraint
        if (ex.getMessage() != null && ex.getMessage().contains("foreign key constraint")) {
            if (ex.getMessage().contains("brands")) {
                message = "Không thể xóa nhà sản xuất này vì đang có sản phẩm sử dụng";
            } else if (ex.getMessage().contains("products")) {
                message = "Không thể xóa sản phẩm này vì đang được sử dụng trong đơn hàng";
            }
        }
        
        return new ResponseEntity<>(
            ApiResponse.error(HttpStatus.CONFLICT.value(), message),
            HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGlobalException(
            Exception ex, WebRequest request) {
        ex.printStackTrace(); // Print stack trace to console for debugging
        return new ResponseEntity<>(
            ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                            "An unexpected error occurred: " + ex.getMessage(), ex.getMessage()),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}