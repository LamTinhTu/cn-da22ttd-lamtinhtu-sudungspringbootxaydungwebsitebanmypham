package com.oceanbutterflyshop.backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.oceanbutterflyshop.backend.dtos.ApiResponse;
import com.oceanbutterflyshop.backend.dtos.request.UserRequest;
import com.oceanbutterflyshop.backend.dtos.response.UserResponse;
import com.oceanbutterflyshop.backend.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Integer userId) {
        UserResponse user = userService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", user));
    }

    @GetMapping("/account/{userAccount}")
    @Operation(summary = "Get user by account")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByAccount(@PathVariable String userAccount) {
        UserResponse user = userService.getUserByAccount(userAccount);
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", user));
    }

    @PostMapping
    @Operation(summary = "Create a new user")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserRequest userRequest) {
        UserResponse createdUser = userService.createUser(userRequest);
        return new ResponseEntity<>(
            ApiResponse.success("User created successfully", createdUser),
            HttpStatus.CREATED
        );
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Update user")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Integer userId,
            @Valid @RequestBody UserRequest userRequest) {
        UserResponse updatedUser = userService.updateUser(userId, userRequest);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", updatedUser));
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user")
    public ResponseEntity<ApiResponse<Object>> deleteUser(@PathVariable Integer userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }

    @GetMapping("/check-account/{userAccount}")
    @Operation(summary = "Check if user account exists")
    public ResponseEntity<ApiResponse<Boolean>> checkAccountExists(@PathVariable String userAccount) {
        boolean exists = userService.existsByAccount(userAccount);
        return ResponseEntity.ok(ApiResponse.success("Account existence checked", exists));
    }

    @GetMapping("/check-phone/{userPhone}")
    @Operation(summary = "Check if phone number exists")
    public ResponseEntity<ApiResponse<Boolean>> checkPhoneExists(@PathVariable String userPhone) {
        boolean exists = userService.existsByPhone(userPhone);
        return ResponseEntity.ok(ApiResponse.success("Phone existence checked", exists));
    }
}