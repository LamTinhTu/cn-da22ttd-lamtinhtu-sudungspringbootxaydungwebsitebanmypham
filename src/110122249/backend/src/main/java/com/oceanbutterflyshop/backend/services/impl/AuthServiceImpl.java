package com.oceanbutterflyshop.backend.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oceanbutterflyshop.backend.dtos.request.LoginRequestDTO;
import com.oceanbutterflyshop.backend.dtos.request.RegisterRequestDTO;
import com.oceanbutterflyshop.backend.dtos.response.AuthResponseDTO;
import com.oceanbutterflyshop.backend.entities.Role;
import com.oceanbutterflyshop.backend.entities.User;
import com.oceanbutterflyshop.backend.exceptions.BadRequestException;
import com.oceanbutterflyshop.backend.exceptions.ResourceNotFoundException;
import com.oceanbutterflyshop.backend.repositories.OTPVerificationRepository;
import com.oceanbutterflyshop.backend.repositories.RoleRepository;
import com.oceanbutterflyshop.backend.repositories.UserRepository;
import com.oceanbutterflyshop.backend.services.AuthService;
import com.oceanbutterflyshop.backend.services.OTPService;
import com.oceanbutterflyshop.backend.utils.CodeGeneratorUtils;
import com.oceanbutterflyshop.backend.utils.JwtUtils;

/**
 * Triển khai AuthService cho xác thực và đăng ký người dùng
 * Theo PROJECT_SPEC.md Mục 4.7
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CodeGeneratorUtils codeGeneratorUtils;
    private final JwtUtils jwtUtils;
    private final OTPService otpService;
    private final OTPVerificationRepository otpVerificationRepository;
    
    private static final String CUSTOMER_ROLE_CODE = "CUS";
    private static final String CUSTOMER_USER_PREFIX = "KH";
    
    @Override
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        log.info("Starting registration for username: {}", request.getUserAccount());
        
        // 1. Kiểm tra tên đăng nhập đã tồn tại chưa
        if (userRepository.existsByUserAccount(request.getUserAccount())) {
            throw new BadRequestException("Username already exists: " + request.getUserAccount());
        }
        
        // 2. Kiểm tra số điện thoại đã tồn tại chưa
        if (userRepository.existsByUserPhone(request.getUserPhone())) {
            throw new BadRequestException("Phone number already exists: " + request.getUserPhone());
        }
        
        // 3. Lấy vai trò customer mặc định
        Role customerRole = roleRepository.findByRoleCode(CUSTOMER_ROLE_CODE)
                .orElseThrow(() -> new ResourceNotFoundException("Customer role not found. Please initialize roles first."));
        
        // 4. Tạo mã người dùng duy nhất với tiền tố "KH"
        String userCode = generateUniqueUserCode(CUSTOMER_USER_PREFIX);
        
        // 5. Tạo entity người dùng mới
        User user = new User();
        user.setUserCode(userCode);
        user.setUserName(request.getUserName());
        user.setUserPhone(request.getUserPhone());
        user.setUserAccount(request.getUserAccount());
        user.setUserGender(request.getUserGender());
        user.setUserBirthDate(request.getUserBirthDate());
        user.setUserAddress(request.getUserAddress());
        
        // 6. Mã hóa mật khẩu bằng BCrypt
        String hashedPassword = passwordEncoder.encode(request.getUserPassword());
        user.setUserPassword(hashedPassword);
        
        // 7. Gán vai trò customer mặc định
        user.setRole(customerRole);
        
        // 8. Lưu người dùng vào database
        User savedUser = userRepository.save(user);
        
        log.info("Successfully registered user: {} with code: {}", savedUser.getUserAccount(), savedUser.getUserCode());
        
        // 9. Trả về response
        return AuthResponseDTO.builder()
                .userId(savedUser.getUserId())
                .userCode(savedUser.getUserCode())
                .userName(savedUser.getUserName())
                .roleName(customerRole.getRoleName())
                .accessToken(null) // JWT token placeholder - sẽ triển khai sau
                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public AuthResponseDTO login(LoginRequestDTO request) {
        log.info("Login attempt for username: {}", request.getUserAccount());
        
        // 1. Tìm người dùng theo tên đăng nhập
        User user = userRepository.findByUserAccount(request.getUserAccount())
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));
        
        // 2. Xác minh hash mật khẩu
        if (!passwordEncoder.matches(request.getUserPassword(), user.getUserPassword())) {
            throw new BadRequestException("Invalid credentials");
        }
        
        log.info("Successful login for user: {} ({})", user.getUserName(), user.getUserCode());
        
        // 3. Tạo JWT token
        String accessToken = jwtUtils.generateToken(user.getUserAccount());
        
        // 4. Trả về thông tin người dùng với JWT token
        return AuthResponseDTO.builder()
                .userId(user.getUserId())
                .userCode(user.getUserCode())
                .userName(user.getUserName())
                .roleName(user.getRole().getRoleName())
                .accessToken(accessToken)
                .build();
    }
    
    /**
     * Phương thức hỗ trợ để tạo mã người dùng duy nhất
     * Thử lại nếu mã đã tồn tại trong database
     */
    @Override
    public void forgotPassword(com.oceanbutterflyshop.backend.dtos.request.ForgotPasswordRequestDTO request) {
        log.info("Forgot password request for phone: {}", request.getPhoneNumber());
        
        // Kiểm tra xem số điện thoại có tồn tại trong hệ thống không
        if (!userRepository.existsByUserPhone(request.getPhoneNumber())) {
            // Vì lý do bảo mật, chúng ta không nên thông báo rõ ràng là số điện thoại không tồn tại
            // Nhưng trong môi trường dev/test có thể log ra
            log.warn("Phone number not found: {}", request.getPhoneNumber());
            return;
        }
        
        // TODO: Tích hợp SMS Gateway để gửi mã OTP
        log.info("SMS sent to {} with OTP: 123456 (Simulation)", request.getPhoneNumber());
    }

    @Override
    @Transactional
    public void resetPassword(com.oceanbutterflyshop.backend.dtos.request.ResetPasswordRequestDTO request) {
        log.info("Reset password request for phone: {}", request.getPhoneNumber());
        
        // Tìm người dùng theo số điện thoại
        User user = userRepository.findByUserPhone(request.getPhoneNumber())
                .orElseThrow(() -> new BadRequestException("Số điện thoại không tồn tại trong hệ thống"));
        
        // Mã hóa mật khẩu mới
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setUserPassword(encodedPassword);
        
        // Lưu người dùng với mật khẩu mới
        userRepository.save(user);
        
        log.info("Password reset successfully for user: {}", user.getUserAccount());
    }

    @Override
    public boolean checkPhoneExists(String phoneNumber) {
        log.info("Check phone exists: {}", phoneNumber);
        return userRepository.existsByUserPhone(phoneNumber);
    }

    private String generateUniqueUserCode(String prefix) {
        String userCode;
        int attempts = 0;
        do {
            userCode = codeGeneratorUtils.generateCode(prefix);
            attempts++;
            if (attempts > 100) {
                throw new RuntimeException("Failed to generate unique user code after 100 attempts");
            }
        } while (userRepository.existsByUserCode(userCode));
        return userCode;
    }
}
