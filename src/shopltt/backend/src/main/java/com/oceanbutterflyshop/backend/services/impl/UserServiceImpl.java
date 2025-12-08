package com.oceanbutterflyshop.backend.services.impl;

import lombok.RequiredArgsConstructor;
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

import java.util.List;
import java.util.stream.Collectors;

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
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
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
        // Check if account already exists
        if (userRepository.existsByUserAccount(userRequest.getUserAccount())) {
            throw new BadRequestException("User account already exists: " + userRequest.getUserAccount());
        }
        
        // Check if phone already exists
        if (userRequest.getUserPhone() != null && userRepository.existsByUserPhone(userRequest.getUserPhone())) {
            throw new BadRequestException("Phone number already exists: " + userRequest.getUserPhone());
        }
        
        // Validate role exists
        Role role = roleRepository.findById(userRequest.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", userRequest.getRoleId()));
        
        // Convert request to entity
        User user = userMapper.toEntity(userRequest, role);
        
        // Generate unique user code based on role
        String userCode;
        do {
            userCode = codeGeneratorUtils.generateUserCode(role.getRoleName());
        } while (userRepository.existsByUserCode(userCode));
        
        user.setUserCode(userCode);
        
        // TODO: Hash password in production
        // user.setUserPassword(passwordEncoder.encode(userRequest.getUserPassword()));
        
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse updateUser(Integer userId, UserRequest userRequest) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        // Check if account is being changed and if new account already exists
        if (!existingUser.getUserAccount().equals(userRequest.getUserAccount()) 
            && userRepository.existsByUserAccount(userRequest.getUserAccount())) {
            throw new BadRequestException("User account already exists: " + userRequest.getUserAccount());
        }
        
        // Check if phone is being changed and if new phone already exists
        if (!existingUser.getUserPhone().equals(userRequest.getUserPhone()) 
            && userRequest.getUserPhone() != null 
            && userRepository.existsByUserPhone(userRequest.getUserPhone())) {
            throw new BadRequestException("Phone number already exists: " + userRequest.getUserPhone());
        }
        
        // Validate role exists
        Role role = roleRepository.findById(userRequest.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", userRequest.getRoleId()));
        
        // Update entity with request data (userCode remains unchanged)
        userMapper.updateEntity(existingUser, userRequest, role);
        
        // TODO: Hash password in production if password is being updated
        // if (userRequest.getUserPassword() != null && !userRequest.getUserPassword().isEmpty()) {
        //     existingUser.setUserPassword(passwordEncoder.encode(userRequest.getUserPassword()));
        // }
        
        User updatedUser = userRepository.save(existingUser);
        return userMapper.toResponse(updatedUser);
    }

    @Override
    public void deleteUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        // Check if user has any orders (business logic)
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