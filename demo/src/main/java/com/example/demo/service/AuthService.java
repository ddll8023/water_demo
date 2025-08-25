package com.example.demo.service;

import com.example.demo.config.JwtTokenUtil;
import com.example.demo.pojo.dto.auth.LoginRequestDTO;
import com.example.demo.pojo.dto.auth.LoginResponseDTO;
import com.example.demo.pojo.dto.auth.RefreshTokenResponseDTO;
import com.example.demo.pojo.dto.auth.UserInfoWithPermissionsDTO;
import com.example.demo.pojo.entity.system.User;
import com.example.demo.pojo.entity.system.Permission;
import com.example.demo.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 认证服务
 * 处理用户登录、登出、Token刷新等认证相关业务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    /** JWT工具类，用于生成和验证Token */
    private final JwtTokenUtil jwtTokenUtil;

    /** 用户数据访问接口，用于操作用户数据 */
    private final UserMapper userMapper;

    /**
     * 验证密码 (简化版本)
     * 实际项目中应使用BCrypt等安全加密方式
     */
    private boolean verifyPassword(String rawPassword, String hashedPassword) {
        // 如果数据库中存储的是明文密码，直接比较
        if (rawPassword.equals(hashedPassword)) {
            return true;
        }
        
        // 如果数据库中存储的是MD5加密密码，则进行MD5比较
        String md5Password = DigestUtils.md5DigestAsHex(rawPassword.getBytes());
        return md5Password.equals(hashedPassword);
    }

    /**
     * 用户登录
     * 
     * @param loginRequest 登录请求DTO，包含用户名和密码
     * @return 登录响应DTO，包含访问令牌、刷新令牌、用户信息、权限列表等
     */
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        log.info("开始处理用户登录请求: {}", loginRequest.getUsername());
        try {
            // 查询用户信息
            User user = userMapper.selectByUsernameWithRole(loginRequest.getUsername());
            if (user == null) {
                log.error("用户不存在: {}", loginRequest.getUsername());
                throw new RuntimeException("用户名或密码错误");
            }

            // 检查用户状态
            if (!"1".equals(user.getIsActive())) {
                log.error("用户已被禁用: {}", loginRequest.getUsername());
                throw new RuntimeException("用户已被禁用");
            }

            // 验证密码 (简化版本，实际项目中应使用BCrypt等安全加密方式)
            if (!verifyPassword(loginRequest.getPassword(), user.getPasswordHash())) {
                log.error("密码错误: {}", loginRequest.getUsername());
                throw new RuntimeException("用户名或密码错误");
            }

            log.info("用户身份验证成功: {}", loginRequest.getUsername());

            // 更新最后登录时间
            user.setLastLogin(LocalDateTime.now());
            userMapper.updateById(user);
            log.debug("更新用户最后登录时间: {}", user.getLastLogin());

            // 生成Token
            log.debug("开始生成Token");
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", user.getId());
            claims.put("username", user.getUsername());
            claims.put("roleName", user.getRoleName());

            // 生成访问令牌和刷新令牌
            String accessToken = jwtTokenUtil.generateToken(user.getUsername(), claims);
            String refreshToken = jwtTokenUtil.generateRefreshToken(user.getUsername());
            log.debug("Token生成成功");

            // 构建响应
            LoginResponseDTO loginresponse = new LoginResponseDTO();
            loginresponse.setAccessToken(accessToken);
            loginresponse.setRefreshToken(refreshToken);
            loginresponse.setTokenType("Bearer");
            loginresponse.setExpiresIn(Long.MAX_VALUE);

            // 设置用户信息
            LoginResponseDTO.UserInfoDTO userInfo = new LoginResponseDTO.UserInfoDTO();
            userInfo.setId(user.getId());
            userInfo.setUsername(user.getUsername());
            userInfo.setRoleName(user.getRoleName() != null ? user.getRoleName() : "未分配角色");
            loginresponse.setUserInfo(userInfo);

            // 查询用户权限
            log.debug("查询用户权限信息: {}", user.getId());
            List<Permission> userPermissions = userMapper.selectPermissionsByUserId(user.getId());

            // 设置权限列表
            List<String> permissions = new ArrayList<>();
            if (userPermissions != null && !userPermissions.isEmpty()) {
                permissions = userPermissions.stream()
                    .map(Permission::getCode)
                    .collect(java.util.stream.Collectors.toList());
                log.debug("用户权限列表: {}", permissions);
            } else {
                log.warn("用户没有任何权限: {}", user.getUsername());
            }
            loginresponse.setPermissions(permissions);

            // 设置角色
            loginresponse.setRoleName(user.getRoleName() != null ? user.getRoleName() : "未分配角色");
            return loginresponse;

        } catch (Exception e) {
            log.error("登录失败: {}, 异常类型: {}, 异常消息: {}", loginRequest.getUsername(), e.getClass().getName(), e.getMessage(), e);
            throw new RuntimeException("登录失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     * 
     * @param token 用户的JWT访问令牌
     * @return 包含用户信息和权限的DTO
     */
    public UserInfoWithPermissionsDTO getCurrentUser(String token) {
        log.debug("开始获取当前用户信息");
        try {
            // 从Token中获取用户名
            String username = jwtTokenUtil.getUsernameFromToken(token);
            log.debug("从Token中获取到用户名: {}", username);

            // 查询用户详细信息
            log.debug("查询用户详细信息: {}", username);
            User user;
            try {
                // 先获取基本用户信息
                User basicUser = userMapper.selectByUsername(username);
                if (basicUser == null) {
                    log.error("用户不存在: {}", username);
                    throw new RuntimeException("用户不存在");
                }
                
                // 再查询详细信息
                user = userMapper.selectUserDetailById(basicUser.getId());
                if (user == null) {
                    log.error("用户详情查询失败: {}", username);
                    throw new RuntimeException("获取用户详情失败");
                }
            } catch (RuntimeException e) {
                log.error("用户信息查询失败: {}", username, e);
                throw new RuntimeException("用户信息查询失败: " + e.getMessage(), e);
            }

            log.debug("用户详细信息查询成功: {}, 角色: {}", user.getUsername(), user.getRoleName());

            // 查询用户权限
            log.debug("查询用户权限信息: {}", user.getUsername());
            List<Permission> userPermissions = userMapper.selectPermissionsByUserId(user.getId());

            // 设置权限列表
            List<String> permissions = new ArrayList<>();
            if (userPermissions != null && !userPermissions.isEmpty()) {
                permissions = userPermissions.stream()
                    .map(Permission::getCode)
                    .collect(Collectors.toList());
                log.debug("用户权限列表: {}", permissions);
            } else {
                log.warn("用户没有任何权限: {}", user.getUsername());
            }

            // 构建用户信息
            UserInfoWithPermissionsDTO userInfo = new UserInfoWithPermissionsDTO();
            userInfo.setId(user.getId());
            userInfo.setUsername(user.getUsername());
            userInfo.setRoleName(user.getRoleName() != null ? user.getRoleName() : "未分配角色");
            userInfo.setPermissions(permissions);

            log.debug("用户信息构建成功: {}, 权限数量: {}", user.getUsername(), permissions.size());
            return userInfo;

        } catch (Exception e) {
            log.error("获取用户信息失败, 异常类型: {}, 异常消息: {}", e.getClass().getName(), e.getMessage(), e);
            throw new RuntimeException("获取用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 刷新Token
     * 
     * @param refreshToken 用户的刷新令牌
     * @return 包含新的访问令牌和原有刷新令牌的DTO
     */
    public RefreshTokenResponseDTO refreshToken(String refreshToken) {
        log.debug("开始刷新Token");
        try {
            // 验证刷新Token
            if (!jwtTokenUtil.validateToken(refreshToken) || !jwtTokenUtil.isRefreshToken(refreshToken)) {
                log.error("无效的刷新Token");
                throw new RuntimeException("无效的刷新Token");
            }
            log.debug("刷新Token验证成功");

            // 从刷新Token中获取用户名
            String username = jwtTokenUtil.getUsernameFromToken(refreshToken);
            log.debug("从刷新Token中获取到用户名: {}", username);

            // 查询用户信息
            User user = userMapper.selectByUsername(username);
            if (user == null) {
                log.error("用户不存在: {}", username);
                throw new RuntimeException("用户不存在");
            }

            // 生成新的访问Token
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", user.getId());
            claims.put("username", user.getUsername());
            String newAccessToken = jwtTokenUtil.generateToken(username, claims);
            log.debug("新的访问Token生成成功");

            // 构建响应对象
            RefreshTokenResponseDTO response = new RefreshTokenResponseDTO();
            response.setAccessToken(newAccessToken);
            response.setRefreshToken(refreshToken);
            response.setTokenType("Bearer");
            response.setExpiresIn(Long.MAX_VALUE);

            log.debug("刷新Token响应构建完成");
            return response;

        } catch (Exception e) {
            log.error("刷新Token失败, 异常类型: {}, 异常消息: {}", e.getClass().getName(), e.getMessage(), e);
            throw new RuntimeException("刷新Token失败: " + e.getMessage());
        }
    }

    /**
     * 用户登出
     * 注意：JWT是无状态的，实际的登出逻辑需要在前端清除Token
     * 
     * @param token 用户的JWT访问令牌
     */
    public void logout(String token) {
        log.debug("处理用户登出请求");
        // JWT无状态，主要依赖前端清除Token
        log.debug("用户登出处理完成");
    }

    /**
     * 验证Token是否有效
     * 
     * @param token 用户的JWT访问令牌
     * @return 布尔值，表示Token是否有效
     */
    public boolean validateToken(String token) {
        log.debug("开始验证Token有效性");
        try {
            boolean isValid = jwtTokenUtil.validateToken(token);
            log.debug("Token验证结果: {}", isValid ? "有效" : "无效");
            return isValid;
        } catch (Exception e) {
            log.error("Token验证失败, 异常类型: {}, 异常消息: {}", e.getClass().getName(), e.getMessage());
            return false;
        }
    }
}
