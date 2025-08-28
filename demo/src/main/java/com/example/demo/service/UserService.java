package com.example.demo.service;

import com.example.demo.common.PageResult;
import com.example.demo.constant.CommonConstant;
import com.example.demo.constant.MessageConstant;
import com.example.demo.exception.system.UserCreateException;
import com.example.demo.exception.system.UserNotExistException;
import com.example.demo.exception.system.UserSelfDisableException;
import com.example.demo.exception.system.UsernameDuplicateException;
import com.example.demo.mapper.RoleMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.pojo.DTO.system.UserCreateDTO;
import com.example.demo.pojo.DTO.system.UserQueryDTO;
import com.example.demo.pojo.DTO.system.UserUpdateDTO;
import com.example.demo.pojo.VO.UserVO;
import com.example.demo.pojo.entity.system.Role;
import com.example.demo.pojo.entity.system.User;
import com.example.demo.utils.BaseContext;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户管理服务
 * 专注于系统用户账号管理，采用标准RBAC模型
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

    /**
     * 分页查询用户列表
     */
    public PageResult<UserVO> getUsers(UserQueryDTO userQueryDTO) {
        log.info("开始查询用户分页数据，查询条件: {}", userQueryDTO);

        // 使用PageHelper进行分页
        PageHelper.startPage(userQueryDTO.getPage(), userQueryDTO.getSize());

        // 执行查询（返回UserVO包含关联信息）
        Page<User> users = userMapper.selectUserPageWithDetails(
                userQueryDTO);

        long total = users.getTotal();
        List<User> records = users.getResult();

        List<UserVO> userVOList = records.stream().map(this::convertToUserVO).collect(Collectors.toList());

        log.info("查询完成，返回 {} 条记录", total);

        return new PageResult<>(
                total,userVOList
        );
    }

    /**
     * 创建用户
     */
    public void createUser(UserCreateDTO userCreateDTO) {
        log.info("开始创建用户:{}",userCreateDTO);
        
        // 检查用户名是否已存在
        if (userMapper.countByUsername(userCreateDTO.getUsername()) > 0) {
            log.error("用户创建失败，用户名已存在: {}", userCreateDTO.getUsername());
            throw new UsernameDuplicateException(MessageConstant.USER_USERNAME_DUPLICATE);
        }

        // 验证角色是否存在和有效
        if (!roleMapper.isRoleActive(userCreateDTO.getRoleId())) {
            log.error("用户创建失败，角色不存在或已禁用，角色ID: {}", userCreateDTO.getRoleId());
            throw new UserCreateException(MessageConstant.ROLE_NOT_EXIST_OR_DISABLED);
        }

        // 创建用户实体
        User user = new User();
        BeanUtils.copyProperties(userCreateDTO, user);

        // 加密密码
        String password = DigestUtils.md5DigestAsHex(userCreateDTO.getPassword().getBytes());
        user.setPassword(password);
        user.setIsActive(userCreateDTO.getIsActive() != null ? userCreateDTO.getIsActive() : CommonConstant.ACTIVE_STATUS_ENABLED);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // 保存用户
        userMapper.insertUser(user);
        
        log.info("用户创建成功");
    }

    /**
     * 更新用户
     */
    public void updateUser(Long id, UserUpdateDTO userUpdateDTO) {
        log.info("开始更新用户:{}",userUpdateDTO);
        User existingUser = userMapper.selectById(id);
        // 检查用户是否存在
        if (existingUser == null) {
            log.error("用户更新失败，用户不存在: {}", id);
            throw new UserNotExistException(MessageConstant.ACCOUNT_NOT_EXIST);
        }

        // 获取当前登录用户信息
        Long currentUserId = BaseContext.getCurrentId();
        if (currentUserId == null) {
            log.error("用户更新失败，获取当前用户失败");
            throw new UserCreateException(MessageConstant.USER_GET_CURRENT_USER_FAILED);
        }
        User currentUser = userMapper.selectById(currentUserId);

        // 检查用户名是否重复（排除自身）
        if (userUpdateDTO.getUsername() != null && !userUpdateDTO.getUsername().equals(existingUser.getUsername())) {
            if (userMapper.countByUsername(userUpdateDTO.getUsername()) > 0) {
                log.error("用户更新失败，用户名已存在: {}", userUpdateDTO.getUsername());
                throw new UsernameDuplicateException(MessageConstant.USER_USERNAME_DUPLICATE);
            }
        }

        // 更新用户信息（排除不可更新的字段）
        User newUser = new User();
        BeanUtils.copyProperties(userUpdateDTO, newUser);
        
        // 如果提供了密码，则加密后更新
        if (userUpdateDTO.getPassword() != null && !userUpdateDTO.getPassword().trim().isEmpty()) {
            newUser.setPassword(DigestUtils.md5DigestAsHex(userUpdateDTO.getPassword().getBytes()));
        }

        // 安全检查：禁止用户禁用自己
        if (userUpdateDTO.getIsActive() != null && CommonConstant.ACTIVE_STATUS_DISABLED.equals(userUpdateDTO.getIsActive())) {
            // 检查是否是当前登录用户
            if (currentUser != null && currentUser.getId().equals(id)) {
                log.error("用户更新失败，当前用户不能禁用自己");
                throw new UserSelfDisableException(MessageConstant.USER_CANNOT_DISABLE_SELF);
            }

            // 检查目标用户是否是超级管理员，如果是则直接禁止
            Role targetUserRole = userMapper.selectUserRole(id);
            if (targetUserRole != null && CommonConstant.ROLE_SUPER_ADMIN.equals(targetUserRole.getName())) {
                log.error("用户更新失败，不允许禁用超级管理员账号，用户ID: {}", id);
                throw new UserSelfDisableException(MessageConstant.USER_CANNOT_DISABLE_SUPER_ADMIN);
            }

            existingUser.setIsActive(CommonConstant.ACTIVE_STATUS_DISABLED);
        } else if (userUpdateDTO.getIsActive() != null) {
            existingUser.setIsActive(CommonConstant.ACTIVE_STATUS_ENABLED);
        }

        existingUser.setUpdatedAt(LocalDateTime.now());

        // 保存更新
        userMapper.updateById(existingUser);
        log.info("用户更新成功" );
    }

    /**
     * 删除用户（软删除）
     */
    public void deleteUser(Long id) {
        log.info("开始删除用户:{}",id);
        User user = userMapper.selectById(id);
        if (user == null) {
            log.error("用户删除失败，用户不存在: {}", id);
            throw new UserNotExistException(MessageConstant.ACCOUNT_NOT_EXIST);
        }

        // 执行软删除
        user.setDeletedAt(LocalDateTime.now());
        userMapper.updateById(user);
        log.info("用户删除成功");
    }

    private UserVO convertToUserVO(User user){
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        
        // 查询并设置关联信息
        userVO.setRoleName(userMapper.selectRoleNameByUserId(user.getId()));
        userVO.setRoles(userMapper.selectRolesByUserId(user.getId()));
        
        return userVO;
    }
}
