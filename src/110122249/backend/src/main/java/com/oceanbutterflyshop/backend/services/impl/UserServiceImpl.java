package com.oceanbutterflyshop.backend.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oceanbutterflyshop.backend.dtos.request.UserRequest;
import com.oceanbutterflyshop.backend.dtos.response.UserResponse;
import com.oceanbutterflyshop.backend.entities.Role;
import com.oceanbutterflyshop.backend.entities.User;
import com.oceanbutterflyshop.backend.exceptions.BadRequestException;
import com.oceanbutterflyshop.backend.exceptions.ResourceNotFoundException;
import com.oceanbutterflyshop.backend.mappers.UserMapper;
import com.oceanbutterflyshop.backend.repositories.RoleRepository;
import com.oceanbutterflyshop.backend.repositories.UserRepository;
import com.oceanbutterflyshop.backend.services.UserService;
import com.oceanbutterflyshop.backend.utils.CodeGeneratorUtils;


@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final CodeGeneratorUtils codeGeneratorUtils;

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsersPaginated(String keyword, String roleName, Pageable pageable) {
        Page<User> userPage;
        if ((keyword != null && !keyword.trim().isEmpty()) || (roleName != null && !roleName.trim().isEmpty())) {
            userPage = userRepository.searchUsers(keyword != null ? keyword.trim() : null, roleName, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }
        return userPage.map(userMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByAccount(String userAccount) {
        User user = userRepository.findByUserAccount(userAccount)
                .orElseThrow(() -> new ResourceNotFoundException("User", "account", userAccount));
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse createUser(UserRequest userRequest) {
        // Kiểm tra tài khoản đã tồn tại chưa
        if (userRepository.existsByUserAccount(userRequest.getUserAccount())) {
            throw new BadRequestException("User account already exists: " + userRequest.getUserAccount());
        }
        
        // Kiểm tra số điện thoại đã tồn tại chưa
        if (userRequest.getUserPhone() != null && userRepository.existsByUserPhone(userRequest.getUserPhone())) {
            throw new BadRequestException("Phone number already exists: " + userRequest.getUserPhone());
        }
        
        // Xác thực vai trò tồn tại
        Role role = roleRepository.findById(userRequest.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", userRequest.getRoleId()));
        
        // Chuyển đổi request sang entity
        User user = userMapper.toEntity(userRequest, role);
        
        // Tạo mã người dùng duy nhất dựa trên vai trò
        String userCode;
        do {
            userCode = codeGeneratorUtils.generateUserCode(role.getRoleName());
        } while (userRepository.existsByUserCode(userCode));
        
        user.setUserCode(userCode);
        
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse updateUser(Integer userId, UserRequest userRequest) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        // Kiểm tra tài khoản có đang được thay đổi và tài khoản mới đã tồn tại chưa
        if (!existingUser.getUserAccount().equals(userRequest.getUserAccount()) 
            && userRepository.existsByUserAccount(userRequest.getUserAccount())) {
            throw new BadRequestException("User account already exists: " + userRequest.getUserAccount());
        }
        
        // Kiểm tra số điện thoại có đang được thay đổi và số mới đã tồn tại chưa
        if (!existingUser.getUserPhone().equals(userRequest.getUserPhone()) 
            && userRequest.getUserPhone() != null 
            && userRepository.existsByUserPhone(userRequest.getUserPhone())) {
            throw new BadRequestException("Phone number already exists: " + userRequest.getUserPhone());
        }
        
        // Xác thực vai trò tồn tại
        Role role = roleRepository.findById(userRequest.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + userRequest.getRoleId()));
        
        // Cập nhật entity với dữ liệu request (userCode không thay đổi)
        userMapper.updateEntity(existingUser, userRequest, role);
        
        User updatedUser = userRepository.save(existingUser);
        return userMapper.toResponse(updatedUser);
    }

    @Override
    public void deleteUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        // Kiểm tra người dùng có đơn hàng nào không (logic nghiệp vụ)
        if (!user.getOrders().isEmpty()) {
            throw new BadRequestException("Cannot delete user with existing orders");
        }
        
        userRepository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByAccount(String userAccount) {
        return userRepository.existsByUserAccount(userAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByPhone(String userPhone) {
        return userRepository.existsByUserPhone(userPhone);
    }
}