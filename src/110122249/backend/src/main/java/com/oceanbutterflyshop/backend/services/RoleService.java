package com.oceanbutterflyshop.backend.services;

import java.util.List;

import com.oceanbutterflyshop.backend.dtos.RoleDTO;

public interface RoleService {
    List<RoleDTO> getAllRoles();
    RoleDTO getRoleById(Integer roleId);
    RoleDTO getRoleByCode(String roleCode);
    RoleDTO createRole(RoleDTO roleDTO);
    RoleDTO updateRole(Integer roleId, RoleDTO roleDTO);
    void deleteRole(Integer roleId);
}