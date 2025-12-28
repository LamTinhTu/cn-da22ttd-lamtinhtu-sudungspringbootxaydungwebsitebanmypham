package com.oceanbutterflyshop.backend.exceptions;

/**
 * Trả về ngoại lệ khi người dùng cố gắng truy cập tài nguyên mà họ không có quyền.
 * Được sử dụng để bảo vệ IDOR (Insecure Direct Object Reference).
 * 
 * Ví dụ: Khách hàng cố gắng truy cập đơn hàng của khách hàng khác.
 */
public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }
}
