package com.oceanbutterflyshop.backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.oceanbutterflyshop.backend.dtos.ApiResponse;
import com.oceanbutterflyshop.backend.dtos.RoleDTO;
import com.oceanbutterflyshop.backend.services.RoleService;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "Role Management", description = "APIs for managing roles")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @Operation(summary = "Get all roles")
    public ResponseEntity<ApiResponse<List<RoleDTO>>> getAllRoles() {
        List<RoleDTO> roles = roleService.getAllRoles();
        return ResponseEntity.ok(ApiResponse.success("Roles retrieved successfully", roles));
    }

    @GetMapping("/{roleId}")
    @Operation(summary = "Get role by ID")
    public ResponseEntity<ApiResponse<RoleDTO>> getRoleById(@PathVariable Integer roleId) {
        RoleDTO role = roleService.getRoleById(roleId);
        return ResponseEntity.ok(ApiResponse.success("Role retrieved successfully", role));
    }

    @GetMapping("/code/{roleCode}")
    @Operation(summary = "Get role by code")
    public ResponseEntity<ApiResponse<RoleDTO>> getRoleByCode(@PathVariable String roleCode) {
        RoleDTO role = roleService.getRoleByCode(roleCode);
        return ResponseEntity.ok(ApiResponse.success("Role retrieved successfully", role));
    }

    @PostMapping
    @Operation(summary = "Create a new role")
    public ResponseEntity<ApiResponse<RoleDTO>> createRole(@Valid @RequestBody RoleDTO roleDTO) {
        RoleDTO createdRole = roleService.createRole(roleDTO);
        return new ResponseEntity<>(
            ApiResponse.success("Role created successfully", createdRole),
            HttpStatus.CREATED
        );
    }

    @PutMapping("/{roleId}")
    @Operation(summary = "Update role")
    public ResponseEntity<ApiResponse<RoleDTO>> updateRole(
            @PathVariable Integer roleId,
            @Valid @RequestBody RoleDTO roleDTO) {
        RoleDTO updatedRole = roleService.updateRole(roleId, roleDTO);
        return ResponseEntity.ok(ApiResponse.success("Role updated successfully", updatedRole));
    }

    @DeleteMapping("/{roleId}")
    @Operation(summary = "Delete role")
    public ResponseEntity<ApiResponse<Object>> deleteRole(@PathVariable Integer roleId) {
        roleService.deleteRole(roleId);
        return ResponseEntity.ok(ApiResponse.success("Role deleted successfully", null));
    }
}