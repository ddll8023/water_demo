package com.example.demo.service;

import com.example.demo.config.JwtTokenUtil;
import com.example.demo.dto.auth.LoginRequestDTO;
import com.example.demo.dto.auth.LoginResponseDTO;
import com.example.demo.dto.auth.RefreshTokenResponseDTO;
import com.example.demo.dto.auth.UserInfoWithPermissionsDTO;
import com.example.demo.entity.system.User;
import com.example.demo.entity.system.Permission;
import com.example.demo.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 认证服务
 * 处理用户登录、登出、Token刷新等认证相关业务
 * 该服务负责处理所有与用户认证、授权相关的业务逻辑，包括：
 * 1. 用户登录认证与Token生成
 * 2. Token刷新
 * 3. 获取当前已认证用户信息
 * 4. 用户登出
 * 5. Token验证
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    /** Spring Security认证管理器，负责处理认证请求 */
    private final AuthenticationManager authenticationManager;

    /** 用户详情服务，用于加载用户信息 */
    private final UserDetailsService userDetailsService;

    /** JWT工具类，用于生成和验证Token */
    private final JwtTokenUtil jwtTokenUtil;

    /** 用户数据访问接口，用于操作用户数据 */
    private final UserMapper userMapper;

    /**
     * 用户登录
     * 
     * @param loginRequest 登录请求DTO，包含用户名和密码
     * @return 登录响应DTO，包含访问令牌、刷新令牌、用户信息、权限列表等
     */
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        log.info("开始处理用户登录请求: {}", loginRequest.getUsername());
        try {
            // 使用Spring Security进行身份验证
            log.debug("开始进行身份验证: {}", loginRequest.getUsername());
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );
            log.info("用户身份验证成功: {}", loginRequest.getUsername());

            // 获取用户详情
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            log.debug("获取到用户详情: {}, 权限列表: {}", userDetails.getUsername(), userDetails.getAuthorities());

            // 查询用户完整信息（包含角色信息）
            User user = userMapper.selectByUsernameWithRole(loginRequest.getUsername());
            log.debug("用户完整信息查询成功: {}, 角色: {}", user.getUsername(), user.getRoleName());

            // 更新最后登录时间
            user.setLastLogin(LocalDateTime.now());
            // updateById为MyBatis-Plus的更新方法，会自动填充更新时间字段
            userMapper.updateById(user);
            log.debug("更新用户最后登录时间: {}", user.getLastLogin());

            // 生成Token
            // 在claims中添加额外信息，便于前端使用和后续验证
            log.debug("开始生成Token");
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", user.getId());
            claims.put("username", user.getUsername());
            claims.put("roleName", user.getRoleName()); // 添加角色名称到Token中

            // 生成访问令牌和刷新令牌
            String accessToken = jwtTokenUtil.generateToken(userDetails, claims);
            String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);
            log.debug("Token生成成功");

            // 构建响应
            LoginResponseDTO loginresponse = new LoginResponseDTO();
            loginresponse.setAccessToken(accessToken);
            loginresponse.setRefreshToken(refreshToken);
            loginresponse.setTokenType("Bearer"); // OAuth2标准的令牌类型
            loginresponse.setExpiresIn(Long.MAX_VALUE); // 设置为最大值，表示永不过期

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
                // 将权限对象转换为权限代码字符串列表
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

        } catch (BadCredentialsException e) {
            // 捕获密码错误的异常
            log.error("用户名或密码错误: {}", loginRequest.getUsername(), e);
            throw new BadCredentialsException("用户名或密码错误");
        } catch (Exception e) {
            // 捕获其他所有异常
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

            // 查询用户详细信息（包含部门、岗位、角色信息）
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
                // 将权限对象转换为权限代码字符串列表
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
            // 从关联查询结果中获取角色名称
            userInfo.setRoleName(user.getRoleName() != null ? user.getRoleName() : "未分配角色");
            userInfo.setPermissions(permissions);

            log.debug("用户信息构建成功: {}, 权限数量: {}", user.getUsername(), permissions.size());
            return userInfo;

        } catch (Exception e) {
            // 捕获所有异常
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
            // 确保Token有效且是刷新令牌类型
            if (!jwtTokenUtil.validateToken(refreshToken) || !jwtTokenUtil.isRefreshToken(refreshToken)) {
                log.error("无效的刷新Token");
                throw new RuntimeException("无效的刷新Token");
            }
            log.debug("刷新Token验证成功");

            // 从刷新Token中获取用户名
            String username = jwtTokenUtil.getUsernameFromToken(refreshToken);
            log.debug("从刷新Token中获取到用户名: {}", username);

            // 加载用户详情
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            log.debug("用户详情加载成功: {}", username);

            // 生成新的访问Token
            String newAccessToken = jwtTokenUtil.generateToken(userDetails);
            log.debug("新的访问Token生成成功");

            // 构建响应对象
            RefreshTokenResponseDTO response = new RefreshTokenResponseDTO();
            response.setAccessToken(newAccessToken);
            response.setRefreshToken(refreshToken); // 保持原有的刷新令牌
            response.setTokenType("Bearer");        // OAuth2标准的令牌类型
            response.setExpiresIn(Long.MAX_VALUE);  // 设置为最大值，表示永不过期

            log.debug("刷新Token响应构建完成");
            return response;

        } catch (Exception e) {
            // 捕获所有异常
            log.error("刷新Token失败, 异常类型: {}, 异常消息: {}", e.getClass().getName(), e.getMessage(), e);
            throw new RuntimeException("刷新Token失败: " + e.getMessage());
        }
    }

    /**
     * 用户登出
     * 注意：JWT是无状态的，实际的登出逻辑需要在前端清除Token
     * 这里可以实现Token黑名单机制（可选）
     * 
     * @param token 用户的JWT访问令牌
     */
    public void logout(String token) {
        log.debug("处理用户登出请求");
        // TODO: 可以实现Token黑名单机制
        // 将Token加入黑名单，防止被继续使用
        // 可以使用Redis缓存来存储黑名单Token，并设置过期时间
        
        // 目前只是一个占位方法
        // 实际的登出主要依赖前端清除Token
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
            // 使用JWT工具类验证Token
            boolean isValid = jwtTokenUtil.validateToken(token);
            log.debug("Token验证结果: {}", isValid ? "有效" : "无效");
            return isValid;
        } catch (Exception e) {
            // 捕获验证过程中的异常
            log.error("Token验证失败, 异常类型: {}, 异常消息: {}", e.getClass().getName(), e.getMessage());
            return false;
        }
    }
}
