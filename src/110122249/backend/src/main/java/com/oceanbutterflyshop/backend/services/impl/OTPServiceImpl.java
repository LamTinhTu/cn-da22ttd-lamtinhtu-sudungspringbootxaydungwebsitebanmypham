package com.oceanbutterflyshop.backend.services.impl;

import com.oceanbutterflyshop.backend.entities.OTPVerification;
import com.oceanbutterflyshop.backend.exceptions.BadRequestException;
import com.oceanbutterflyshop.backend.repositories.OTPVerificationRepository;
import com.oceanbutterflyshop.backend.repositories.UserRepository;
import com.oceanbutterflyshop.backend.services.OTPService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class OTPServiceImpl implements OTPService {
    
    @Value("${twilio.account-sid}")
    private String accountSid;
    
    @Value("${twilio.auth-token}")
    private String authToken;
    
    @Value("${twilio.phone-number}")
    private String twilioPhoneNumber;
    
    private final OTPVerificationRepository otpRepository;
    private final UserRepository userRepository;
    private final Random random = new Random();
    
    @Override
    @Transactional
    public String sendOTP(String phoneNumber) {
        // Kiểm tra số điện thoại đã được đăng ký chưa
        if (userRepository.existsByUserPhone(phoneNumber)) {
            throw new BadRequestException("Số điện thoại đã được đăng ký");
        }
        
        // Xóa chỉ OTP chưa verify cũ của số điện thoại này
        otpRepository.deleteByPhoneNumberAndVerifiedFalse(phoneNumber);
        
        // Tạo mã OTP 6 chữ số
        String otp = String.format("%06d", random.nextInt(1000000));
        
        // Lưu OTP vào database
        OTPVerification otpVerification = new OTPVerification();
        otpVerification.setPhoneNumber(phoneNumber);
        otpVerification.setOtp(otp);
        otpVerification.setCreatedAt(LocalDateTime.now());
        otpVerification.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        otpVerification.setVerified(false);
        otpVerification.setAttempts(0);
        
        otpRepository.save(otpVerification);
        
        // Send SMS via Twilio
        try {
            Twilio.init(accountSid, authToken);
            
            // Convert phone number to international format
            String internationalPhone;
            if (phoneNumber.startsWith("0")) {
                // Vietnamese number: 0XXX -> +84XXX
                internationalPhone = "+84" + phoneNumber.substring(1);
            } else if (phoneNumber.startsWith("+1")) {
                // US number: already in international format
                internationalPhone = phoneNumber;
            } else {
                // Default: assume already international
                internationalPhone = phoneNumber;
            }
            
            Message message = Message.creator(
                new PhoneNumber(internationalPhone),
                new PhoneNumber(twilioPhoneNumber),
                "Ma OTP cua ban la: " + otp + ". Co hieu luc trong 5 phut."
            ).create();
            
            log.info("SMS sent successfully to {}. Message SID: {}", internationalPhone, message.getSid());
        } catch (Exception e) {
            log.error("Failed to send SMS via Twilio: {}", e.getMessage(), e);
            // Fallback: Return OTP for development mode
            log.info("Fallback - OTP for {}: {} (expires in 5 minutes)", phoneNumber, otp);
        }
        
        return otp; // Return OTP for development mode
    }
    
    @Override
    @Transactional
    public boolean verifyOTP(String phoneNumber, String otp) {
        Optional<OTPVerification> otpOptional = otpRepository
                .findTopByPhoneNumberAndVerifiedFalseOrderByCreatedAtDesc(phoneNumber);
        
        if (otpOptional.isEmpty()) {
            throw new BadRequestException("Không tìm thấy mã OTP. Vui lòng yêu cầu gửi lại.");
        }
        
        OTPVerification otpVerification = otpOptional.get();
        
        // Kiểm tra đã hết hạn chưa
        if (otpVerification.isExpired()) {
            throw new BadRequestException("Mã OTP đã hết hạn. Vui lòng yêu cầu gửi lại.");
        }
        
        // Kiểm tra số lần thử
        if (otpVerification.isMaxAttemptsReached()) {
            throw new BadRequestException("Đã nhập sai quá nhiều lần. Vui lòng yêu cầu gửi lại OTP mới.");
        }
        
        // Tăng số lần thử
        otpVerification.setAttempts(otpVerification.getAttempts() + 1);
        
        // Kiểm tra OTP có đúng không
        if (!otpVerification.getOtp().equals(otp)) {
            otpRepository.save(otpVerification);
            throw new BadRequestException("Mã OTP không chính xác. Còn " + 
                    (5 - otpVerification.getAttempts()) + " lần thử.");
        }
        
        // OTP đúng - đánh dấu đã xác thực
        otpVerification.setVerified(true);
        otpRepository.save(otpVerification);
        
        return true;
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isPhoneVerified(String phoneNumber) {
        Optional<OTPVerification> otpOptional = otpRepository
                .findTopByPhoneNumberAndVerifiedTrueOrderByCreatedAtDesc(phoneNumber);
        
        if (otpOptional.isEmpty()) {
            return false;
        }
        
        OTPVerification otpVerification = otpOptional.get();
        
        // Kiểm tra OTP đã verify có còn trong thời hạn không (trong vòng 10 phút)
        LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(10);
        return otpVerification.getCreatedAt().isAfter(tenMinutesAgo);
    }
    
    @Override
    @Transactional
    @Scheduled(fixedRate = 3600000) // Chạy mỗi giờ
    public void cleanupExpiredOTPs() {
        LocalDateTime now = LocalDateTime.now();
        otpRepository.deleteByExpiresAtBefore(now);
        log.info("Cleaned up expired OTPs before {}", now);
    }
}
