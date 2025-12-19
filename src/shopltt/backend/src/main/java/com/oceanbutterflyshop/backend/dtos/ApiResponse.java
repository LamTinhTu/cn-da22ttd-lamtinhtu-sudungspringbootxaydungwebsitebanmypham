package com.oceanbutterflyshop.backend.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Standard API Response wrapper
 * As per PROJECT_SPEC.md Section 5.1
 * Format: {"status": 200, "message": "Success", "data": {...}}
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
    
    // Success response with data
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "Success", data, null);
    }
    
    // Success response with custom message
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data, null);
    }
    
    // Error response
    public static <T> ApiResponse<T> error(int status, String message) {
        return new ApiResponse<>(status, message, null, message);
    }
    
    // Error response with details
    public static <T> ApiResponse<T> error(int status, String message, String error) {
        return new ApiResponse<>(status, message, null, error);
    }
}