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
     * Chuyển UserRequest thành entity User
     * Note: userCode sẽ được thiết lập bởi service sử dụng CodeGeneratorUtils
     */
    public User toEntity(UserRequest request, Role role) {
        if (request == null) {
            return null;
        }
        
        User user = new User();
        user.setUserName(request.getUserName());
        user.setUserGender(Gender.valueOf(request.getUserGender())); // Convert string to enum by name
        user.setUserBirthDate(request.getUserBirthDate());
        user.setUserAddress(request.getUserAddress());
        user.setUserPhone(request.getUserPhone());
        user.setUserAccount(request.getUserAccount());
        user.setUserPassword(request.getUserPassword()); // Nên được mã hóa trong lớp service
        user.setRole(role);
        
        return user;
    }
    
    /**
     * Cập nhật entity User hiện có với dữ liệu từ UserRequest
     */
    public void updateEntity(User user, UserRequest request, Role role) {
        if (user == null || request == null) {
            return;
        }
        
        user.setUserName(request.getUserName());
        user.setUserGender(Gender.valueOf(request.getUserGender())); // Convert string to enum by name
        user.setUserBirthDate(request.getUserBirthDate());
        user.setUserAddress(request.getUserAddress());
        user.setUserPhone(request.getUserPhone());
        user.setUserAccount(request.getUserAccount());
        // Lưu ý: việc cập nhật mật khẩu nên được xử lý riêng với xác thực phù hợp
        user.setRole(role);
    }
    
    /**
     * Chuyển đổi entity User thành UserResponse
     */
    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }
        
        UserResponse response = new UserResponse();
        response.setUserId(user.getUserId());
        response.setUserCode(user.getUserCode());
        response.setUserName(user.getUserName());
        response.setUserGender(user.getUserGender() != null ? user.getUserGender().name() : null); // Convert enum to string name (MALE, FEMALE, OTHER), handle null
        response.setUserBirthDate(user.getUserBirthDate());
        response.setUserAddress(user.getUserAddress());
        response.setUserPhone(user.getUserPhone());
        response.setUserAccount(user.getUserAccount());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        
        // Cập nhật thông tin vai trò
        if (user.getRole() != null) {
            Role role = user.getRole();
            response.setRoleId(role.getRoleId());
            response.setRoleCode(role.getRoleCode());
            response.setRoleName(role.getRoleName());
        }
        
        return response;
    }
}