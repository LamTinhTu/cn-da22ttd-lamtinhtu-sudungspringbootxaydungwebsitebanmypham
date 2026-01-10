package com.oceanbutterflyshop.backend.services;

public interface OTPService {
    
    /**
     * Gửi mã OTP đến số điện thoại
     * @param phoneNumber Số điện thoại nhận OTP
     * @return OTP code (for development only)
     */
    String sendOTP(String phoneNumber);
    
    /**
     * Xác thực mã OTP
     * @param phoneNumber Số điện thoại
     * @param otp Mã OTP cần xác thực
     * @return true nếu OTP hợp lệ
     */
    boolean verifyOTP(String phoneNumber, String otp);
    
    /**
     * Kiểm tra số điện thoại đã verify OTP chưa
     * @param phoneNumber Số điện thoại
     * @return true nếu đã verify
     */
    boolean isPhoneVerified(String phoneNumber);
    
    /**
     * Xóa các OTP đã hết hạn
     */
    void cleanupExpiredOTPs();
}
