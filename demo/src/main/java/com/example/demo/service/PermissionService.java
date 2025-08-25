package com.example.demo.service;


import com.example.demo.pojo.DTO.system.PermissionResponseDTO;
import com.example.demo.pojo.DTO.common.PageResponseDTO;
import com.example.demo.pojo.entity.system.Permission;
import com.example.demo.mapper.PermissionMapper;
import com.example.demo.mapper.RoleMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 权限管理服务
 * 处理权限的查询和管理，构建权限树结构
 */
@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionMapper permissionMapper;

    private final RoleMapper roleMapper;

    /**
     * 分页查询权限列表
     */
    public PageResponseDTO<PermissionResponseDTO> getPermissions(int page, int size, String keyword, String type) {
        // 参数验证
        if (page < 1) page = 1;
        if (size < 1) size = 10;

        // 使用PageHelper开始分页
        PageHelper.startPage(page, size);
        
        // 查询数据，不需要传入分页参数
        List<Permission> permissions = permissionMapper.selectPermissionListWithFilter(keyword, type);
        
        // 获取分页信息
        PageInfo<Permission> pageInfo = new PageInfo<>(permissions);

        // 转换为DTO
        List<PermissionResponseDTO> permissionDTOs = permissions.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());

        return new PageResponseDTO<>(
            permissionDTOs,
            (int) pageInfo.getTotal(),
            page,
            size
        );
    }

    /**
     * 根据ID查询权限
     */
    public PermissionResponseDTO getPermissionById(Long id) {
        Permission permission = permissionMapper.selectById(id);
        if (permission == null) {
            throw new RuntimeException("权限不存在");
        }
        return convertToResponseDTO(permission);
    }

    /**
     * 获取权限树结构
     */
    public List<PermissionResponseDTO> getPermissionTree() {
        // 获取所有权限
        List<Permission> allPermissions = permissionMapper.selectAllAvailable();
        
        // 转换为DTO
        List<PermissionResponseDTO> permissionDTOs = allPermissions.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());

        // 构建树结构
        return buildPermissionTree(permissionDTOs);
    }

    /**
     * 获取菜单权限树（用于前端菜单构建）
     */
    public List<PermissionResponseDTO> getMenuTree() {
        // 获取所有菜单权限
        List<Permission> menuPermissions = permissionMapper.selectAllMenus();
        
        // 转换为DTO
        List<PermissionResponseDTO> menuDTOs = menuPermissions.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());

        // 构建树结构
        return buildPermissionTree(menuDTOs);
    }

    /**
     * 根据权限类型获取权限列表
     */
    public List<PermissionResponseDTO> getPermissionsByType(String type) {
        List<Permission> permissions = permissionMapper.selectByType(type);
        return permissions.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
    }

    /**
     * 获取所有可用权限（用于角色分配）
     */
    public List<PermissionResponseDTO> getAllAvailablePermissions() {
        List<Permission> permissions = permissionMapper.selectAllAvailable();
        return permissions.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
    }

    /**
     * 删除权限（软删除）
     */
    @Transactional
    public void deletePermission(Long id) {
        Permission permission = permissionMapper.selectById(id);
        if (permission == null) {
            throw new RuntimeException("权限不存在");
        }

        // 检查是否有子权限
        List<Permission> children = permissionMapper.selectByParentId(id);
        if (!children.isEmpty()) {
            throw new RuntimeException("存在子权限，无法删除");
        }

        // 删除角色权限关联
        roleMapper.rolePermissionDeleteByPermissionId(id);
        
        // 执行软删除
        permissionMapper.deleteById(id);
    }

    /**
     * 构建权限树结构
     */
    private List<PermissionResponseDTO> buildPermissionTree(List<PermissionResponseDTO> permissions) {
        // 按父ID分组
        Map<Long, List<PermissionResponseDTO>> parentMap = permissions.stream()
            .filter(p -> p.getParentId() != null)
            .collect(Collectors.groupingBy(PermissionResponseDTO::getParentId));

        // 设置子权限
        for (PermissionResponseDTO permission : permissions) {
            List<PermissionResponseDTO> children = parentMap.get(permission.getId());
            if (children != null) {
                permission.setChildren(children);
            } else {
                permission.setChildren(new ArrayList<>());
            }
        }

        // 返回根权限
        return permissions.stream()
            .filter(p -> p.getParentId() == null)
            .collect(Collectors.toList());
    }

    /**
     * 检查权限编码是否可用
     */
    public boolean checkCodeAvailable(String code, Long excludeId) {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }

        int count = permissionMapper.countByCodeExcluding(code.trim(), excludeId);
        return count == 0;
    }

    /**
     * 转换为响应DTO
     */
    private PermissionResponseDTO convertToResponseDTO(Permission permission) {
        PermissionResponseDTO dto = new PermissionResponseDTO();
        BeanUtils.copyProperties(permission, dto);
        return dto;
    }
}
