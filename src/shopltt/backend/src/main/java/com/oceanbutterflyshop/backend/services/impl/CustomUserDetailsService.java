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
 * Custom implementation of Spring Security's UserDetailsService.
 * Loads user-specific data for authentication and authorization.
 * 
 * This service:
 * 1. Fetches user from database by username (UserAccount)
 * 2. Maps user role to Spring Security GrantedAuthority
 * 3. Returns UserDetails object for Spring Security authentication
 * 
 * Role Mapping:
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
     * Load user by username for Spring Security authentication
     * 
     * @param username The username (UserAccount) to search for
     * @return UserDetails object containing user information and authorities
     * @throws UsernameNotFoundException if user is not found
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);
        
        // Fetch user from database
        User user = userRepository.findByUserAccount(username)
                .orElseThrow(() -> {
                    log.error("User not found with username: {}", username);
                    return new UsernameNotFoundException("User not found with username: " + username);
                });
        
        log.debug("User found: {} with role: {}", user.getUserName(), user.getRole().getRoleName());
        
        // Map user to Spring Security UserDetails
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUserAccount())
                .password(user.getUserPassword()) // Already BCrypt hashed
                .authorities(getAuthorities(user))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    /**
     * Convert user role to Spring Security authorities
     * 
     * Role Code Mapping:
     * - ADM → ROLE_ADMIN
     * - STF → ROLE_STAFF
     * - CUS → ROLE_CUSTOMER
     * 
     * @param user User entity
     * @return Collection of granted authorities
     */
    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        String roleCode = user.getRole().getRoleCode();
        String roleName;
        
        // Map role code to Spring Security role name
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
