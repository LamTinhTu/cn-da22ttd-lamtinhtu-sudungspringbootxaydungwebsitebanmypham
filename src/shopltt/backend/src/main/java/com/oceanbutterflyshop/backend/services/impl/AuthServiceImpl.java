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
import com.oceanbutterflyshop.backend.repositories.RoleRepository;
import com.oceanbutterflyshop.backend.repositories.UserRepository;
import com.oceanbutterflyshop.backend.services.AuthService;
import com.oceanbutterflyshop.backend.utils.CodeGeneratorUtils;
import com.oceanbutterflyshop.backend.utils.JwtUtils;

/**
 * Implementation of AuthService for user authentication and registration
 * As per PROJECT_SPEC.md Section 4.7
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
    
    private static final String CUSTOMER_ROLE_CODE = "CUS";
    private static final String CUSTOMER_USER_PREFIX = "KH";
    
    @Override
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        log.info("Starting registration for username: {}", request.getUserAccount());
        
        // 1. Check if username already exists
        if (userRepository.existsByUserAccount(request.getUserAccount())) {
            throw new BadRequestException("Username already exists: " + request.getUserAccount());
        }
        
        // 2. Check if phone already exists
        if (userRepository.existsByUserPhone(request.getUserPhone())) {
            throw new BadRequestException("Phone number already exists: " + request.getUserPhone());
        }
        
        // 3. Get default customer role
        Role customerRole = roleRepository.findByRoleCode(CUSTOMER_ROLE_CODE)
                .orElseThrow(() -> new ResourceNotFoundException("Customer role not found. Please initialize roles first."));
        
        // 4. Generate unique user code with prefix "KH"
        String userCode = generateUniqueUserCode(CUSTOMER_USER_PREFIX);
        
        // 5. Create new user entity
        User user = new User();
        user.setUserCode(userCode);
        user.setUserName(request.getUserName());
        user.setUserPhone(request.getUserPhone());
        user.setUserAccount(request.getUserAccount());
        
        // 6. Hash password using BCrypt
        String hashedPassword = passwordEncoder.encode(request.getUserPassword());
        user.setUserPassword(hashedPassword);
        
        // 7. Assign default customer role
        user.setRole(customerRole);
        
        // 8. Save user to database
        User savedUser = userRepository.save(user);
        
        log.info("Successfully registered user: {} with code: {}", savedUser.getUserAccount(), savedUser.getUserCode());
        
        // 9. Return response
        return AuthResponseDTO.builder()
                .userCode(savedUser.getUserCode())
                .userName(savedUser.getUserName())
                .roleName(customerRole.getRoleName())
                .accessToken(null) // JWT token placeholder - to be implemented later
                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public AuthResponseDTO login(LoginRequestDTO request) {
        log.info("Login attempt for username: {}", request.getUserAccount());
        
        // 1. Find user by username
        User user = userRepository.findByUserAccount(request.getUserAccount())
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));
        
        // 2. Verify password hash
        if (!passwordEncoder.matches(request.getUserPassword(), user.getUserPassword())) {
            throw new BadRequestException("Invalid credentials");
        }
        
        log.info("Successful login for user: {} ({})", user.getUserName(), user.getUserCode());
        
        // 3. Generate JWT token
        String accessToken = jwtUtils.generateToken(user.getUserAccount());
        
        // 4. Return user information with JWT token
        return AuthResponseDTO.builder()
                .userCode(user.getUserCode())
                .userName(user.getUserName())
                .roleName(user.getRole().getRoleName())
                .accessToken(accessToken)
                .build();
    }
    
    /**
     * Helper method to generate unique user code
     * Retries if code already exists in database
     */
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
