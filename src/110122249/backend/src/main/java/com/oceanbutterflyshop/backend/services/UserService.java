package com.oceanbutterflyshop.backend.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.oceanbutterflyshop.backend.dtos.request.UserRequest;
import com.oceanbutterflyshop.backend.dtos.response.UserResponse;

public interface UserService {
    Page<UserResponse> getAllUsersPaginated(String keyword, String roleName, Pageable pageable);
    UserResponse getUserById(Integer userId);
    UserResponse getUserByAccount(String userAccount);
    UserResponse createUser(UserRequest userRequest);
    UserResponse updateUser(Integer userId, UserRequest userRequest);
    void deleteUser(Integer userId);
    boolean existsByAccount(String userAccount);
    boolean existsByPhone(String userPhone);
}