package com.oceanbutterflyshop.backend.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.oceanbutterflyshop.backend.exceptions.BadRequestException;
import com.oceanbutterflyshop.backend.services.FileStorageService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Service implementation cho việc lưu trữ file
 * Xử lý upload, lưu trữ, và xóa file ảnh
 */
@Service
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {
    
    // Thư mục lưu trữ file upload
    private final Path fileStorageLocation;
    
    // Các định dạng file được phép
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");
    
    // Kích thước file tối đa (5MB)
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    
    /**
     * Constructor - Khởi tạo thư mục lưu trữ
     */
    public FileStorageServiceImpl() {
        // Tạo thư mục uploads trong thư mục gốc project
        this.fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();
        
        try {
            // Tạo thư mục nếu chưa tồn tại
            Files.createDirectories(this.fileStorageLocation);
            log.info("Khởi tạo thư mục lưu trữ file tại: {}", this.fileStorageLocation);
        } catch (IOException ex) {
            log.error("Không thể tạo thư mục lưu trữ file", ex);
            throw new RuntimeException("Không thể tạo thư mục lưu trữ file", ex);
        }
    }
    
    /**
     * Lưu file upload vào hệ thống
     * 
     * @param file File upload từ client
     * @return Tên file đã được tạo
     */
    @Override
    public String storeFile(MultipartFile file) {
        // 1. Kiểm tra file có rỗng không
        if (file.isEmpty()) {
            throw new BadRequestException("File không được để trống");
        }
        
        // 2. Kiểm tra kích thước file
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("Kích thước file vượt quá giới hạn 5MB");
        }
        
        // 3. Lấy tên file gốc và chuẩn hóa
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        
        // 4. Kiểm tra tên file hợp lệ
        if (originalFilename.contains("..")) {
            throw new BadRequestException("Tên file không hợp lệ: " + originalFilename);
        }
        
        // 5. Lấy phần mở rộng file
        String fileExtension = getFileExtension(originalFilename);
        
        // 6. Xác thực định dạng file
        if (!ALLOWED_EXTENSIONS.contains(fileExtension.toLowerCase())) {
            throw new BadRequestException(
                "Định dạng file không được hỗ trợ. Chỉ chấp nhận: " + String.join(", ", ALLOWED_EXTENSIONS)
            );
        }
        
        // 7. Tạo tên file mới với UUID để tránh trùng lặp
        String newFilename = UUID.randomUUID().toString() + "_" + originalFilename;
        
        try {
            // 8. Xác định đường dẫn lưu file
            Path targetLocation = this.fileStorageLocation.resolve(newFilename);
            
            // 9. Sao chép file vào thư mục đích
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            log.info("File đã được lưu thành công: {}", newFilename);
            
            return newFilename;
            
        } catch (IOException ex) {
            log.error("Lỗi khi lưu file: {}", originalFilename, ex);
            throw new RuntimeException("Không thể lưu file: " + originalFilename, ex);
        }
    }
    
    /**
     * Xóa file khỏi hệ thống
     * 
     * @param filename Tên file cần xóa
     */
    @Override
    public void deleteFile(String filename) {
        try {
            Path filePath = this.fileStorageLocation.resolve(filename).normalize();
            Files.deleteIfExists(filePath);
            log.info("Đã xóa file: {}", filename);
        } catch (IOException ex) {
            log.error("Lỗi khi xóa file: {}", filename, ex);
            throw new RuntimeException("Không thể xóa file: " + filename, ex);
        }
    }
    
    /**
     * Lấy phần mở rộng của file
     * 
     * @param filename Tên file
     * @return Phần mở rộng (không bao gồm dấu chấm)
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        
        return filename.substring(lastDotIndex + 1);
    }
}
