package com.oceanbutterflyshop.backend.utils;

import java.security.SecureRandom;
import org.springframework.stereotype.Component;

/**
 * Lớp tiện ích để tạo mã tự động theo định dạng:
 * TIỀN_TỐ + 8 SỐ NGẪU NHIÊN
 * 
 * Ví dụ:
 * - Thương hiệu: "TH" + 8 chữ số (ví dụ: TH12345678)
 * - Sản phẩm: "SP" + 8 chữ số (ví dụ: SP09123842)
 * - Đơn hàng: "DH" + 8 chữ số (ví dụ: DH87654321)
 * - Người dùng: Dựa theo vai trò ("AD", "NV", "KH") + 8 chữ số (ví dụ: AD00001234)
 */
@Component
public class CodeGeneratorUtils {
    
    private static final SecureRandom random = new SecureRandom();
    private static final int MAX_RANDOM_VALUE = 100000000; // 10^8 để có tối đa 8 chữ số
    
    /**
     * Tạo mã với tiền tố được chỉ định theo sau bởi 8 chữ số ngẫu nhiên.
     * Các chữ số được đệm thêm số 0 ở đầu nếu cần thiết để đảm bảo đúng 8 chữ số.
     * 
     * @param prefix Tiền tố để thêm vào trước các chữ số tạo ra (ví dụ: "SP", "TH", "DH", "AD", "NV", "KH")
     * @return Chuỗi chứa tiền tố theo sau bởi đúng 8 chữ số
     * 
     * @example
     * generateCode("SP") có thể trả về "SP09123842"
     * generateCode("TH") có thể trả về "TH00001234"
     * generateCode("DH") có thể trả về "DH87654321"
     */
    public String generateCode(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("Prefix cannot be null");
        }
        
        // Tạo một số ngẫu nhiên từ 0 đến 99999999 (bao gồm cả hai đầu)
        int randomNumber = random.nextInt(MAX_RANDOM_VALUE);
        
        // Định dạng số để đảm bảo đúng 8 chữ số với các số 0 ở đầu nếu cần
        String paddedNumber = String.format("%08d", randomNumber);
        
        return prefix + paddedNumber;
    }
    
    /**
     * Tạo mã người dùng dựa trên vai trò.
     * 
     * @param role Tên vai trò (Admin, Staff, Customer)
     * @return Mã được tạo với tiền tố phù hợp
     */
    public String generateUserCode(String role) {
        String prefix;
        
        switch (role.toUpperCase()) {
            case "ADMIN":
            case "ADMINISTRATOR":
                prefix = "AD";
                break;
            case "STAFF":
            case "NV":
                prefix = "NV";
                break;
            case "CUSTOMER":
            case "KH":
                prefix = "KH";
                break;
            default:
                throw new IllegalArgumentException("Invalid role: " + role + ". Expected: Admin, Staff, or Customer");
        }
        
        return generateCode(prefix);
    }
    
    /**
     * Tạo mã thương hiệu với tiền tố "TH".
     * 
     * @return Mã thương hiệu được tạo (ví dụ: "TH12345678")
     */
    public String generateBrandCode() {
        return generateCode("TH");
    }
    
    /**
     * Tạo mã sản phẩm với tiền tố "SP".
     * 
     * @return Mã sản phẩm được tạo (ví dụ: "SP09123842")
     */
    public String generateProductCode() {
        return generateCode("SP");
    }
    
    /**
     * Tạo mã đơn hàng với tiền tố "DH".
     * 
     * @return Mã đơn hàng được tạo (ví dụ: "DH87654321")
     */
    public String generateOrderCode() {
        return generateCode("DH");
    }
}