package com.oceanbutterflyshop.backend.services;

import java.util.List;

import com.oceanbutterflyshop.backend.dtos.request.UserRequest;
import com.oceanbutterflyshop.backend.dtos.response.UserResponse;

public interface UserService {
    List<UserResponse> getAllUsers();
    UserResponse getUserById(Integer userId);
    UserResponse getUserByAccount(String userAccount);
    UserResponse createUser(UserRequest userRequest);
    UserResponse updateUser(Integer userId, UserRequest userRequest);
    void deleteUser(Integer userId);
    boolean existsByAccount(String userAccount);
    boolean existsByPhone(String userPhone);
}