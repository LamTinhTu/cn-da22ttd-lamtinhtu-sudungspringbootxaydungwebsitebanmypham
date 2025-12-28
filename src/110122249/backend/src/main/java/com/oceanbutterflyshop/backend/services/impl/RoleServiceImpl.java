package com.oceanbutterflyshop.backend.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oceanbutterflyshop.backend.dtos.RoleDTO;
import com.oceanbutterflyshop.backend.entities.Role;
import com.oceanbutterflyshop.backend.exceptions.BadRequestException;
import com.oceanbutterflyshop.backend.exceptions.ResourceNotFoundException;
import com.oceanbutterflyshop.backend.repositories.RoleRepository;
import com.oceanbutterflyshop.backend.services.RoleService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {
    
    private final RoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RoleDTO getRoleById(Integer roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId));
        return convertToDTO(role);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleDTO getRoleByCode(String roleCode) {
        Role role = roleRepository.findByRoleCode(roleCode)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "code", roleCode));
        return convertToDTO(role);
    }

    @Override
    public RoleDTO createRole(RoleDTO roleDTO) {
        // Kiểm tra nếu mã vai trò đã tồn tại
        if (roleRepository.findByRoleCode(roleDTO.getRoleCode()).isPresent()) {
            throw new BadRequestException("Role code already exists: " + roleDTO.getRoleCode());
        }
        
        Role role = convertToEntity(roleDTO);
        role = roleRepository.save(role);
        return convertToDTO(role);
    }

    @Override
    public RoleDTO updateRole(Integer roleId, RoleDTO roleDTO) {
        Role existingRole = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId));
        
        // Kiểm tra mã vai trò có đang được thay đổi và mã mới đã tồn tại chưa
        if (!existingRole.getRoleCode().equals(roleDTO.getRoleCode()) 
            && roleRepository.findByRoleCode(roleDTO.getRoleCode()).isPresent()) {
            throw new BadRequestException("Role code already exists: " + roleDTO.getRoleCode());
        }
        
        existingRole.setRoleCode(roleDTO.getRoleCode());
        existingRole.setRoleName(roleDTO.getRoleName());
        
        existingRole = roleRepository.save(existingRole);
        return convertToDTO(existingRole);
    }

    @Override
    public void deleteRole(Integer roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId));
        
        // Kiểm tra nếu vai trò đang được sử dụng bởi bất kỳ người dùng nào
        if (!role.getUsers().isEmpty()) {
            throw new BadRequestException("Cannot delete role that is assigned to users");
        }
        
        roleRepository.delete(role);
    }

    private RoleDTO convertToDTO(Role role) {
        RoleDTO dto = new RoleDTO();
        dto.setRoleId(role.getRoleId());
        dto.setRoleCode(role.getRoleCode());
        dto.setRoleName(role.getRoleName());
        return dto;
    }

    private Role convertToEntity(RoleDTO dto) {
        Role role = new Role();
        role.setRoleCode(dto.getRoleCode());
        role.setRoleName(dto.getRoleName());
        return role;
    }
}