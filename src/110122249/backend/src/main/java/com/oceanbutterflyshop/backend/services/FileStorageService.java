package com.oceanbutterflyshop.backend.services;

import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface cho việc lưu trữ và quản lý file
 */
public interface FileStorageService {
    
    /**
     * Lưu file upload vào hệ thống file local
     * 
     * @param file File upload từ client
     * @return Tên file đã được tạo (UUID + tên gốc)
     */
    String storeFile(MultipartFile file);
    
    /**
     * Xóa file khỏi hệ thống
     * 
     * @param filename Tên file cần xóa
     */
    void deleteFile(String filename);
}
