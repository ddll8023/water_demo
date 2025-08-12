package com.example.demo.service;

import com.example.demo.dto.system.UserCreateDTO;
import com.example.demo.dto.system.UserResponseDTO;
import com.example.demo.dto.system.UserUpdateDTO;
import com.example.demo.dto.common.PageResponseDTO;
import com.example.demo.entity.system.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.mapper.RoleMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户管理服务
 * 专注于系统用户账号管理，采用标准RBAC模型
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 分页查询用户列表
     */
    public PageResponseDTO<UserResponseDTO> getUsers(int page, int size,
                                                     String username, Long roleId, Boolean isActive) {
        // 使用PageHelper进行分页
        PageHelper.startPage(page, size);

        // 执行查询
        List<User> users = userMapper.selectUserPageWithDetails(
            null, username, roleId, isActive, null);

        // 获取分页信息
        PageInfo<User> pageInfo = new PageInfo<>(users);

        // 转换为DTO
        List<UserResponseDTO> userDTOs = pageInfo.getList().stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());

        return new PageResponseDTO<>(
            userDTOs,
            (int) pageInfo.getTotal(),
            pageInfo.getPageNum(),
            pageInfo.getPageSize()
        );
    }

    /**
     * 根据ID查询用户
     */
    public UserResponseDTO getUserById(Long id) {
        User user = userMapper.selectUserDetailById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return convertToResponseDTO(user);
    }

    /**
     * 创建用户
     */
    public UserResponseDTO createUser(UserCreateDTO createDTO) {
        // 检查用户名是否已存在
        if (userMapper.countByUsername(createDTO.getUsername()) > 0) {
            throw new RuntimeException("用户名已存在");
        }

        // 创建用户实体
        User user = new User();
        BeanUtils.copyProperties(createDTO, user);

        // 加密密码
        user.setPasswordHash(passwordEncoder.encode(createDTO.getPassword()));
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // 保存用户
        userMapper.insertUser(user);

        return getUserById(user.getId());
    }

    /**
     * 更新用户
     */
    public UserResponseDTO updateUser(Long id, UserUpdateDTO updateDTO) {
        User existingUser = userMapper.selectById(id);
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }

        // 获取当前登录用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userMapper.selectByUsername(currentUsername);
        
        // 更新用户信息（排除不可更新的字段）
        BeanUtils.copyProperties(updateDTO, existingUser);
        
        // 安全检查：禁止用户禁用自己
        if (updateDTO.getIsActive() != null && !updateDTO.getIsActive()) {
            // 检查是否是当前登录用户
            if (currentUser != null && currentUser.getId().equals(id)) {
                throw new RuntimeException("不能禁用自己的账号");
            }
            
            // 获取当前用户角色
            List<com.example.demo.entity.system.Role> currentUserRoles = userMapper.selectUserRoles(currentUser.getId());
            boolean isCurrentUserSuperAdmin = false;
            for (com.example.demo.entity.system.Role role : currentUserRoles) {
                String roleName = role.getName();
                if (roleName != null && roleName.equals("超级管理员")) {
                    isCurrentUserSuperAdmin = true;
                    break;
                }
            }
            
            // 检查目标用户是否是超级管理员
            List<com.example.demo.entity.system.Role> userRoles = userMapper.selectUserRoles(id);
            for (com.example.demo.entity.system.Role role : userRoles) {
                String roleName = role.getName();
                if (roleName != null && roleName.equals("超级管理员")) {
                    // 检查系统中超级管理员数量，确保至少保留一个
                    int superAdminCount = userMapper.countSuperAdmins();
                    if (superAdminCount <= 1) {
                        throw new RuntimeException("不能禁用系统唯一的超级管理员账号");
                    }
                    
                    // 如果当前用户不是超级管理员，则不能禁用任何超级管理员
                    if (!isCurrentUserSuperAdmin) {
                        throw new RuntimeException("只有超级管理员才能禁用其他超级管理员账号");
                    }
                    break;
                }
            }
            
            existingUser.setIsActive(false);
        } else if (updateDTO.getIsActive() != null) {
            existingUser.setIsActive(true);
        }

        existingUser.setUpdatedAt(LocalDateTime.now());

        // 保存更新
        userMapper.updateById(existingUser);

        return getUserById(id);
    }

    /**
     * 删除用户（软删除）
     */
    public void deleteUser(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 执行软删除
        user.setDeletedAt(LocalDateTime.now());
        userMapper.updateById(user);
    }

    /**
     * 获取用户的角色列表
     * @param userId 用户ID
     * @return 角色列表
     */
    public List<com.example.demo.entity.system.Role> getUserRoles(Long userId) {
        // 验证用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 获取用户当前角色
        return userMapper.selectUserRoles(userId);
    }
    
    /**
     * 为用户分配角色
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     */
    @Transactional
    public void assignUserRoles(Long userId, List<Long> roleIds) {
        // 验证用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 先删除用户当前的所有角色关联
        userMapper.deleteUserRoles(userId);
        
        // 如果角色ID列表不为空，则添加新的角色关联
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                // 验证角色是否存在和有效
                if (!roleMapper.isRoleActive(roleId)) {
                    throw new RuntimeException("角色不存在或已禁用");
                }
                userMapper.insertUserRole(userId, roleId);
            }
        }
    }

    /**
     * 转换为响应DTO
     */
    private UserResponseDTO convertToResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        BeanUtils.copyProperties(user, dto);

        // 设置角色名称
        dto.setRoleName(user.getRoleName());

        return dto;
    }
}
