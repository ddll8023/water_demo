package com.example.demo.service;

import com.example.demo.dto.system.RoleCreateDTO;
import com.example.demo.dto.system.RoleResponseDTO;
import com.example.demo.dto.system.RoleUpdateDTO;
import com.example.demo.dto.common.PageResponseDTO;
import com.example.demo.entity.system.Permission;
import com.example.demo.entity.system.Role;
import com.example.demo.entity.system.RolePermission;
import com.example.demo.mapper.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
// 添加PageHelper相关导入
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色管理服务
 * 处理角色的CRUD操作和权限分配
 */
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleMapper roleMapper;

    /**
     * 分页查询角色列表
     */
    public PageResponseDTO<RoleResponseDTO> getRoles(int page, int size, String name) {
        // 使用PageHelper分页
        PageHelper.startPage(page, size);
        
        // 使用新的查询方法替代MyBatis-Plus查询
        List<Role> roles = roleMapper.selectRoleList(name);
        
        // 使用PageInfo包装查询结果
        PageInfo<Role> pageInfo = new PageInfo<>(roles);
        
        // 转换为DTO
        List<RoleResponseDTO> roleDTOs = pageInfo.getList().stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());

        return new PageResponseDTO<>(
            roleDTOs,
            (int) pageInfo.getTotal(),
            pageInfo.getPageNum(),
            pageInfo.getPageSize()
        );
    }

    /**
     * 根据ID查询角色
     */
    public RoleResponseDTO getRoleById(Long id) {
        Role role = roleMapper.selectRoleById(id);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        return convertToResponseDTO(role);
    }

    /**
     * 创建角色
     */
    public RoleResponseDTO createRole(RoleCreateDTO createDTO) {
        // 检查角色名称是否已存在
        if (roleMapper.countByName(createDTO.getName(), null) > 0) {
            throw new RuntimeException("角色名称已存在");
        }

        // 创建角色实体
        Role role = new Role();
        BeanUtils.copyProperties(createDTO, role);
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());

        // 保存角色
        roleMapper.insertRole(role);

        return convertToResponseDTO(role);
    }

    /**
     * 更新角色
     */
    public RoleResponseDTO updateRole(Long id, RoleUpdateDTO updateDTO) {
        Role existingRole = roleMapper.selectRoleById(id);
        if (existingRole == null) {
            throw new RuntimeException("角色不存在");
        }

        // 检查角色名称是否被其他角色使用
        if (StringUtils.hasText(updateDTO.getName()) &&
            !updateDTO.getName().equals(existingRole.getName())) {
            if (roleMapper.countByName(updateDTO.getName(), id) > 0) {
                throw new RuntimeException("角色名称已存在");
            }
        }

        // 更新角色信息
        BeanUtils.copyProperties(updateDTO, existingRole, "id", "createdAt");
        existingRole.setUpdatedAt(LocalDateTime.now());

        // 保存更新
        roleMapper.updateRole(existingRole);

        return convertToResponseDTO(existingRole);
    }

    /**
     * 删除角色（软删除）
     */
    @Transactional
    public void deleteRole(Long id) {
        Role role = roleMapper.selectRoleById(id);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }

        // 检查是否有用户使用此角色
        int userCount = roleMapper.countUsersByRoleId(id);
        if (userCount > 0) {
            throw new RuntimeException("该角色下存在" + userCount + "个用户，无法删除");
        }
        
        // 删除角色权限关联
        roleMapper.rolePermissionDeleteByRoleId(id);
        
        // 执行软删除 - 使用updateRole方法替代deleteRoleById
        role.setDeletedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());
        roleMapper.updateRole(role);
    }

    /**
     * 获取角色权限列表
     */
    public List<Permission> getRolePermissions(Long roleId) {
        return roleMapper.selectPermissionsByRoleId(roleId);
    }

    /**
     * 为角色分配权限
     */
    @Transactional
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        // 检查角色是否存在
        Role role = roleMapper.selectRoleById(roleId);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }

        // 先删除现有的权限关联
        roleMapper.rolePermissionDeleteByRoleId(roleId);

        // 添加新的权限关联
        if (permissionIds != null && !permissionIds.isEmpty()) {
            List<RolePermission> rolePermissions = permissionIds.stream()
                .map(permissionId -> {
                    RolePermission rp = new RolePermission();
                    rp.setRoleId(roleId);
                    rp.setPermissionId(permissionId);
                    rp.setCreatedAt(LocalDateTime.now());
                    rp.setUpdatedAt(LocalDateTime.now());
                    return rp;
                })
                .collect(Collectors.toList());

            roleMapper.rolePermissionInsertBatch(rolePermissions);
        }
    }

    /**
     * 获取所有可用角色（用于下拉选择）
     */
    public List<RoleResponseDTO> getAllAvailableRoles() {
        List<Role> roles = roleMapper.selectAllAvailable();
        return roles.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
    }

    /**
     * 检查角色名称是否可用
     */
    public boolean checkNameAvailable(String name, Long excludeId) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        int count = roleMapper.countByName(name.trim(), excludeId);
        return count == 0;
    }

    /**
     * 转换为响应DTO
     */
    private RoleResponseDTO convertToResponseDTO(Role role) {
        RoleResponseDTO dto = new RoleResponseDTO();
        BeanUtils.copyProperties(role, dto);
        return dto;
    }
}
