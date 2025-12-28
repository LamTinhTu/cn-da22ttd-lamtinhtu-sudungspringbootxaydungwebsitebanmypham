package com.oceanbutterflyshop.backend.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Lớp bọc phản hồi API chuẩn
 * Theo PROJECT_SPEC.md Mục 5.1
 * Định dạng: {"status": 200, "message": "Success", "data": {...}}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private int status;
    private String message;
    private T data;
    private String error;
    
    // Phản hồi thành công với dữ liệu
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "Success", data, null);
    }
    
    // Phản hồi thành công với thông báo tùy chỉnh
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data, null);
    }
    
    // Phản hồi lỗi
    public static <T> ApiResponse<T> error(int status, String message) {
        return new ApiResponse<>(status, message, null, message);
    }
    
    // Phản hồi lỗi với chi tiết
    public static <T> ApiResponse<T> error(int status, String message, String error) {
        return new ApiResponse<>(status, message, null, error);
    }
}