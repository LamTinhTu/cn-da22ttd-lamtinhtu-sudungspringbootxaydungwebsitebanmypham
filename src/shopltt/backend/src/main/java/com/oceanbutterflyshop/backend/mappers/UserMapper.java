package com.oceanbutterflyshop.backend.mappers;

import org.springframework.stereotype.Component;

import com.oceanbutterflyshop.backend.dtos.request.UserRequest;
import com.oceanbutterflyshop.backend.dtos.response.UserResponse;
import com.oceanbutterflyshop.backend.entities.Role;
import com.oceanbutterflyshop.backend.entities.User;
import com.oceanbutterflyshop.backend.enums.Gender;

@Component
public class UserMapper {
    
    /**
     * Convert UserRequest to User entity
     * Note: userCode will be set by the service using CodeGeneratorUtils
     */
    public User toEntity(UserRequest request, Role role) {
        if (request == null) {
            return null;
        }
        
        User user = new User();
        user.setUserName(request.getUserName());
        user.setUserGender(Gender.fromDisplayName(request.getUserGender())); // Convert string to enum
        user.setUserBirthDate(request.getUserBirthDate());
        user.setUserAddress(request.getUserAddress());
        user.setUserPhone(request.getUserPhone());
        user.setUserAccount(request.getUserAccount());
        user.setUserPassword(request.getUserPassword()); // Should be hashed in service layer
        user.setRole(role);
        
        return user;
    }
    
    /**
     * Update existing User entity with UserRequest data
     */
    public void updateEntity(User user, UserRequest request, Role role) {
        if (user == null || request == null) {
            return;
        }
        
        user.setUserName(request.getUserName());
        user.setUserGender(Gender.fromDisplayName(request.getUserGender())); // Convert string to enum
        user.setUserBirthDate(request.getUserBirthDate());
        user.setUserAddress(request.getUserAddress());
        user.setUserPhone(request.getUserPhone());
        user.setUserAccount(request.getUserAccount());
        // Note: password update should be handled separately with proper validation
        user.setRole(role);
    }
    
    /**
     * Convert User entity to UserResponse
     */
    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }
        
        UserResponse response = new UserResponse();
        response.setUserId(user.getUserId());
        response.setUserCode(user.getUserCode());
        response.setUserName(user.getUserName());
        response.setUserGender(user.getUserGender().getDisplayName()); // Convert enum to string
        response.setUserBirthDate(user.getUserBirthDate());
        response.setUserAddress(user.getUserAddress());
        response.setUserPhone(user.getUserPhone());
        response.setUserAccount(user.getUserAccount());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        
        // Map role information
        if (user.getRole() != null) {
            Role role = user.getRole();
            response.setRoleId(role.getRoleId());
            response.setRoleCode(role.getRoleCode());
            response.setRoleName(role.getRoleName());
        }
        
        return response;
    }
}