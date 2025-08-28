package com.example.demo.service;

import com.example.demo.common.PageResult;
import com.example.demo.constant.MessageConstant;
import com.example.demo.exception.system.RoleAlreadyExistException;
import com.example.demo.exception.system.RoleHasUsersException;
import com.example.demo.exception.system.RoleNotExistException;
import com.example.demo.mapper.RoleMapper;
import com.example.demo.pojo.DTO.system.RoleCreateDTO;
import com.example.demo.pojo.DTO.system.RoleQueryDTO;

import com.example.demo.pojo.DTO.system.RoleUpdateDTO;
import com.example.demo.pojo.VO.RoleVO;
import com.example.demo.pojo.VO.RolePermissionVO;
import com.example.demo.pojo.entity.system.Permission;
import com.example.demo.pojo.entity.system.Role;
import com.example.demo.pojo.entity.system.RolePermission;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 角色管理服务
 * 处理角色的CRUD操作和权限分配
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleMapper roleMapper;

    /**
     * 分页查询角色列表
     */
    public PageResult<RoleVO> getRoles(RoleQueryDTO roleQueryDTO) {
        log.info("开始查询角色分页数据，查询条件: {}", roleQueryDTO);

        // 使用PageHelper分页
        PageHelper.startPage(roleQueryDTO.getPage(), roleQueryDTO.getSize());

        // 使用新的查询方法
        Page<Role> roles = roleMapper.selectRoleList(roleQueryDTO.getName());

        // 获取分页信息
        long total = roles.getTotal();
        List<Role> records = roles.getResult();

        // 转换为VO并补充关联信息
        List<RoleVO> roleVOList = records.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        log.info("查询完成，返回 {} 条记录，总计 {} 条", roleVOList.size(), total);
        return new PageResult<>(total, roleVOList);
    }

    /**
     * 根据ID查询角色
     */
    public RoleVO getRoleById(Long id) {
        log.info("开始查询角色详情，角色ID: {}", id);
        
        Role role = roleMapper.selectRoleById(id);
        if (role == null) {
            log.error("角色查询失败，角色不存在，ID: {}", id);
            throw new RoleNotExistException(MessageConstant.ROLE_NOT_EXIST);
        }
        
        RoleVO roleVO = convertToVO(role);
        log.info("角色查询成功，角色ID: {}, 角色名称: {}", id, role.getName());
        return roleVO;
    }

    /**
     * 创建角色
     */
    @Transactional
    public void createRole(RoleCreateDTO createDTO) {
        log.info("开始创建角色，角色名称: {}", createDTO.getName());
        
        // 检查角色名称是否已存在
        if (roleMapper.countByName(createDTO.getName(), null) > 0) {
            log.error("角色创建失败，角色名称已存在: {}", createDTO.getName());
            throw new RoleAlreadyExistException(MessageConstant.ROLE_NAME_DUPLICATE);
        }

        // 创建角色实体
        Role role = new Role();
        BeanUtils.copyProperties(createDTO, role);
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());

        // 保存角色
        roleMapper.insertRole(role);

        // 分配权限（如果有）
        if (createDTO.getPermissionIds() != null && !createDTO.getPermissionIds().isEmpty()) {
            assignPermissionsInternal(role.getId(), createDTO.getPermissionIds());
        }

        log.info("角色创建成功，角色ID: {}, 角色名称: {}", role.getId(), role.getName());
    }

    /**
     * 更新角色
     */
    @Transactional
    public void updateRole(Long id, RoleUpdateDTO roleUpdateDTO) {
        log.info("开始更新角色，角色ID: {}", id);
        
        Role existingRole = roleMapper.selectRoleById(id);
        if (existingRole == null) {
            log.error("角色更新失败，角色不存在，ID: {}", id);
            throw new RoleNotExistException(MessageConstant.ROLE_NOT_EXIST);
        }

        // 检查角色名称是否被其他角色使用
        if (StringUtils.hasText(roleUpdateDTO.getName()) &&
                !roleUpdateDTO.getName().equals(existingRole.getName())) {
            if (roleMapper.countByName(roleUpdateDTO.getName(), id) > 0) {
                log.error("角色更新失败，角色名称已存在: {}", roleUpdateDTO.getName());
                throw new RoleAlreadyExistException(MessageConstant.ROLE_NAME_DUPLICATE);
            }
        }
        Role newRole = new Role();
        // 更新角色信息
        BeanUtils.copyProperties(roleUpdateDTO, newRole);
        newRole.setId(id); // 设置角色ID
        newRole.setUpdatedAt(LocalDateTime.now());

        // 保存更新
        roleMapper.updateRole(newRole);

        // 更新权限（如果提供了权限列表）
        if (roleUpdateDTO.getPermissionIds() != null) {
            assignPermissionsInternal(id, roleUpdateDTO.getPermissionIds());
        }

        log.info("角色更新成功，角色ID: {}, 角色名称: {}", id, newRole.getName());
    }

    /**
     * 删除角色（软删除）
     */
    @Transactional
    public void deleteRole(Long id) {
        log.info("开始删除角色，角色ID: {}", id);
        Role role = roleMapper.selectRoleById(id);
        if (role == null) {
            throw new RoleNotExistException(MessageConstant.ROLE_NOT_EXIST);
        }

        // 检查是否有用户使用此角色
        int userCount = roleMapper.countUsersByRoleId(id);
        if (userCount > 0) {
            throw new RoleHasUsersException(MessageConstant.ROLE_HAS_USERS);
        }

        // 删除角色权限关联
        roleMapper.rolePermissionDeleteByRoleId(id);

        // 执行软删除
        role.setDeletedAt(LocalDateTime.now());
        roleMapper.updateRole(role);
        log.info("删除成功:{}",role.getName());
    }

    /**
     * 获取角色权限列表
     */
    public List<RolePermissionVO> getRolePermissions(Long roleId) {
        log.info("开始查询角色权限列表，角色ID: {}", roleId);
        
        // 检查角色是否存在
        Role role = roleMapper.selectRoleById(roleId);
        if (role == null) {
            log.error("查询角色权限失败，角色不存在，角色ID: {}", roleId);
            throw new RoleNotExistException(MessageConstant.ROLE_NOT_EXIST);
        }
        
        List<Permission> permissions = roleMapper.selectPermissionsByRoleId(roleId);
        List<RolePermissionVO> permissionVOs = permissions.stream()
                .map(this::convertToRolePermissionVO)
                .collect(Collectors.toList());
        
        log.info("角色权限查询完成，角色ID: {}, 权限数量: {}", roleId, permissionVOs.size());
        return permissionVOs;
    }

    /**
     * 为角色分配权限
     */
    @Transactional
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        log.info("开始给角色分配权限，角色ID: {}", roleId);
        // 检查角色是否存在
        Role role = roleMapper.selectRoleById(roleId);
        if (role == null) {
            throw new RoleNotExistException(MessageConstant.ROLE_NOT_EXIST);
        }

        assignPermissionsInternal(roleId, permissionIds);
        log.info("权限分配成功，角色: {}", role.getName());
    }

    /**
     * 内部权限分配方法，不进行角色存在性检查
     */
    private void assignPermissionsInternal(Long roleId, List<Long> permissionIds) {
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
    public List<RoleVO> getAllAvailableRoles() {
        log.info("获取所有可用角色");
        List<Role> roles = roleMapper.selectAllAvailable();
        return roles.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 检查角色名称是否可用
     */
    public Map<String, Object> checkNameAvailable(String name, Long excludeId) {
        log.info("开始检查角色名称是否可用，角色名称: {}, 排除ID: {}", name, excludeId);
        
        Map<String, Object> result = new HashMap<>();
        
        if (name == null || name.trim().isEmpty()) {
            log.warn("角色名称检查失败，名称为空");
            result.put("available", false);
            result.put("message", MessageConstant.ROLE_NAME_EMPTY);
            return result;
        }

        int count = roleMapper.countByName(name.trim(), excludeId);
        boolean available = count == 0;
        
        result.put("available", available);
        result.put("message", available ? MessageConstant.ROLE_NAME_AVAILABLE : MessageConstant.ROLE_NAME_DUPLICATE);
        
        log.info("角色名称检查完成，名称: {}, 可用性: {}", name.trim(), available);
        return result;
    }



    /**
     * 转换为视图对象
     */
    private RoleVO convertToVO(Role role) {
        RoleVO vo = new RoleVO();
        BeanUtils.copyProperties(role, vo);
        return vo;
    }

    /**
     * 转换为角色权限视图对象
     */
    private RolePermissionVO convertToRolePermissionVO(Permission permission) {
        RolePermissionVO vo = new RolePermissionVO();
        BeanUtils.copyProperties(permission, vo);
        return vo;
    }
}
