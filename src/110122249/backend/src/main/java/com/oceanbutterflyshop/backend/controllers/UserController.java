package com.oceanbutterflyshop.backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.oceanbutterflyshop.backend.dtos.ApiResponse;
import com.oceanbutterflyshop.backend.dtos.request.UserRequest;
import com.oceanbutterflyshop.backend.dtos.response.PageResponseWrapper;
import com.oceanbutterflyshop.backend.dtos.response.UserResponse;
import com.oceanbutterflyshop.backend.services.UserService;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(
        summary = "Get users with pagination",
        description = """
            Retrieve users with pagination and sorting support. Requires ADMIN role.
            
            **Default Behavior:** Returns page 0 with 10 items, sorted by userId descending.
            
            **Pagination Parameters:**
            - page: Page number (0-indexed, default: 0)
            - size: Number of items per page (default: 10)
            - sort: Sort field and direction (default: userId,desc)
            
            **Sortable Fields:** userId, userAccount, userFullName, userEmail
            
            **Examples:**
            - GET /api/v1/users → Returns page 0, size 10 (default)
            - GET /api/v1/users?page=1&size=5 → Returns page 1, size 5
            - GET /api/v1/users?sort=userFullName,asc → Sorted by name ascending
            """)
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<PageResponseWrapper<UserResponse>>> getAllUsers(
            @Parameter(hidden = true) @PageableDefault(size = 10, sort = "userId", direction = Sort.Direction.DESC) Pageable pageable,
            @Parameter(description = "Page number (0-indexed)", example = "0") @RequestParam(required = false, defaultValue = "0") Integer page,
            @Parameter(description = "Number of items per page", example = "10") @RequestParam(required = false, defaultValue = "10") Integer size,
            @Parameter(description = "Sort field and direction (format: field,direction)", example = "userId,desc") @RequestParam(required = false, defaultValue = "userId,desc") String sort,
            @Parameter(description = "Search keyword", example = "Nguyen") @RequestParam(required = false) String keyword,
            @Parameter(description = "Filter by role name", example = "Customer") @RequestParam(required = false) String roleName
    ) {
        // Phân tích tham số sắp xếp thủ công
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc") 
            ? Sort.Direction.DESC : Sort.Direction.ASC;
        
        Pageable pageableRequest = PageRequest.of(page, size, Sort.by(direction, sortField));
        Page<UserResponse> userPage = userService.getAllUsersPaginated(keyword, roleName, pageableRequest);
        PageResponseWrapper<UserResponse> response = PageResponseWrapper.of(userPage);
        
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", response));
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserRequest userRequest) {
        UserResponse createdUser = userService.createUser(userRequest);
        return new ResponseEntity<>(
            ApiResponse.success("User created successfully", createdUser),
            HttpStatus.CREATED
        );
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Update user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Integer userId,
            @Valid @RequestBody UserRequest userRequest) {
        UserResponse updatedUser = userService.updateUser(userId, userRequest);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", updatedUser));
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user")
    @PreAuthorize("hasRole('ADMIN')")
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