package com.oceanbutterflyshop.backend.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oceanbutterflyshop.backend.entities.User;
import com.oceanbutterflyshop.backend.repositories.UserRepository;

import java.util.Collection;
import java.util.Collections;

/**
 * Triển khai tùy chỉnh của UserDetailsService của Spring Security.
 * Tải dữ liệu người dùng cụ thể cho xác thực và phân quyền.
 * 
 * Dịch vụ này:
 * 1. Lấy người dùng từ database theo tên đăng nhập (UserAccount)
 * 2. Ánh xạ vai trò người dùng sang Spring Security GrantedAuthority
 * 3. Trả về đối tượng UserDetails cho xác thực Spring Security
 * 
 * Ánh xạ Vai trò:
 * - Administrator (ADM) → ROLE_ADMIN
 * - Staff (STF) → ROLE_STAFF
 * - Customer (CUS) → ROLE_CUSTOMER
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Tải người dùng theo tên đăng nhập cho xác thực Spring Security
     * 
     * @param username Tên đăng nhập (UserAccount) cần tìm kiếm
     * @return Đối tượng UserDetails chứa thông tin người dùng và quyền
     * @throws UsernameNotFoundException nếu không tìm thấy người dùng
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);
        
        // Lấy người dùng từ database
        User user = userRepository.findByUserAccount(username)
                .orElseThrow(() -> {
                    log.error("User not found with username: {}", username);
                    return new UsernameNotFoundException("User not found with username: " + username);
                });
        
        log.debug("User found: {} with role: {}", user.getUserName(), user.getRole().getRoleName());
        
        // Ánh xạ người dùng sang Spring Security UserDetails
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUserAccount())
                .password(user.getUserPassword()) // Đã được mã hóa BCrypt
                .authorities(getAuthorities(user))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    /**
     * Chuyển đổi vai trò người dùng sang quyền Spring Security
     * 
     * Ánh xạ Mã Vai trò:
     * - ADM → ROLE_ADMIN
     * - STF → ROLE_STAFF
     * - CUS → ROLE_CUSTOMER
     * 
     * @param user Entity người dùng
     * @return Tập hợp các quyền được cấp
     */
    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        String roleCode = user.getRole().getRoleCode();
        String roleName;
        
        // Ánh xạ mã vai trò sang tên vai trò Spring Security
        switch (roleCode) {
            case "ADM":
                roleName = "ROLE_ADMIN";
                break;
            case "STF":
                roleName = "ROLE_STAFF";
                break;
            case "CUS":
                roleName = "ROLE_CUSTOMER";
                break;
            default:
                log.warn("Unknown role code: {}. Defaulting to ROLE_CUSTOMER", roleCode);
                roleName = "ROLE_CUSTOMER";
        }
        
        log.debug("Mapped role {} to authority {}", roleCode, roleName);
        return Collections.singletonList(new SimpleGrantedAuthority(roleName));
    }
}
