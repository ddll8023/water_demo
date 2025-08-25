package com.example.demo.service;

import com.example.demo.constant.CommonConstant;
import com.example.demo.constant.MessageConstant;
import com.example.demo.exception.AccountNotExistException;
import com.example.demo.exception.BaseException;
import com.example.demo.exception.PasswordErrorException;
import com.example.demo.exception.TokenInvalidException;
import com.example.demo.exception.UserDisabledException;
import com.example.demo.mapper.RoleMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.pojo.DTO.auth.LoginDTO;

import com.example.demo.pojo.VO.UserInfoVO;
import com.example.demo.pojo.VO.LoginUserVO;
import com.example.demo.pojo.VO.UserVO;
import com.example.demo.pojo.entity.system.Permission;
import com.example.demo.pojo.entity.system.Role;
import com.example.demo.pojo.entity.system.User;
import com.example.demo.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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

    /**
     * JWT工具类，用于生成和验证Token
     */
    private final JwtTokenUtil jwtTokenUtil;

    /**
     * 用户数据访问接口，用于操作用户数据
     */
    private final UserMapper userMapper;

    /**
     * 角色数据访问接口，用于查询角色信息
     */
    private final RoleMapper roleMapper;

    /**
     * 用户登录
     *
     * @param loginDTO 登录请求数据传输对象
     * @return 登录用户视图对象，包含访问令牌、用户信息和权限
     * @throws BaseException 当用户不存在、密码错误或用户被禁用时抛出
     */
    public LoginUserVO login(LoginDTO loginDTO) {
        log.info("开始处理用户登录请求: {}", loginDTO.getUsername());

        // 第一步：查询User实体进行基本验证
        User user = userMapper.selectByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new AccountNotExistException(MessageConstant.ACCOUNT_NOT_EXIST);
        }

        // 检查用户状态
        if (!"1".equals(user.getIsActive())) {
            throw new UserDisabledException(MessageConstant.USER_DISABLED);
        }

        // 验证密码
        String password = DigestUtils.md5DigestAsHex(loginDTO.getPassword().getBytes());
        log.info("加密后密码：{}",password);
        if (!password.equals(user.getPassword())) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        log.info("用户身份验证成功: {}", loginDTO.getUsername());

        // 第二步：验证成功后，查询角色信息
        String roleName = "未分配角色";
        if (user.getRoleId() != null) {
            Role role = roleMapper.selectRoleById(user.getRoleId());
            if (role != null) {
                roleName = role.getName();
            }
        }

        // 第三步：构建LoginUserVO对象
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        loginUserVO.setRoleName(roleName);

        // 更新最后登录时间
        user.setLastLogin(LocalDateTime.now());
        userMapper.updateById(user);

        loginUserVO.setLastLogin(user.getLastLogin());
        log.debug("更新用户最后登录时间: {}", user.getLastLogin());

        // 生成Token
        log.debug("开始生成Token");
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", loginUserVO.getId());

        // 生成访问令牌
        String accessToken = jwtTokenUtil.generateToken(loginUserVO.getUsername(), claims);
        log.debug("Token生成成功：{}", accessToken);

        // 设置Token信息
        loginUserVO.setAccessToken(accessToken);
        loginUserVO.setTokenType(CommonConstant.TOKEN_TYPE);
        loginUserVO.setExpiresIn(CommonConstant.EXPIRES_IN);

        // 查询用户权限
        log.debug("查询用户权限信息: {}", loginUserVO.getId());
        List<Permission> userPermissions = userMapper.selectPermissionsByUserId(loginUserVO.getId());

        // 设置权限列表
        List<String> permissions = new ArrayList<>();
        if (userPermissions != null && !userPermissions.isEmpty()) {

            // 将权限列表转换为字符串列表
            permissions = userPermissions.stream()
                    .map(permission -> permission.getCode())
                    .collect(Collectors.toList());
            log.debug("用户权限列表: {}", permissions);
        } else {
            log.warn("用户没有任何权限: {}", loginUserVO.getUsername());
        }
        loginUserVO.setPermissions(permissions);

        return loginUserVO;
    }

    /**
     * 获取当前用户信息
     *
     * @param token 用户的JWT访问令牌
     * @return UserInfoVO 用户信息和权限
     * @throws TokenInvalidException 当Token无效或已过期时抛出
     * @throws AccountNotExistException 当用户不存在时抛出
     */
    public UserInfoVO getCurrentUserInfo(String token) {
        log.info("处理获取当前用户信息请求");

        // 验证Token有效性
        if (!jwtTokenUtil.validateToken(token)) {
            throw new TokenInvalidException(MessageConstant.TOKEN_INVALID);
        }

        // 提取用户ID
        Long userId = jwtTokenUtil.getUserIdFromToken(token);
        if (userId == null) {
            throw new TokenInvalidException(MessageConstant.TOKEN_INVALID);
        }
        log.info("Token验证成功，用户ID: {}", userId);

        // 查询用户基本信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new AccountNotExistException(MessageConstant.ACCOUNT_NOT_EXIST);
        }
        
        // 查询角色信息
        String roleName = "未分配角色";
        if (user.getRoleId() != null) {
            Role role = roleMapper.selectRoleById(user.getRoleId());
            if (role != null) {
                roleName = role.getName();
            }
        }
        
        log.info("用户详细信息查询成功: {}, 角色: {}", user.getUsername(), roleName);

        // 查询用户权限
        List<Permission> userPermissions = userMapper.selectPermissionsByUserId(user.getId());

        // 构建响应数据
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(user, userInfoVO);
        userInfoVO.setRoleName(roleName);

        // 处理权限列表
        List<String> permissions = new ArrayList<>();
        if (userPermissions != null && !userPermissions.isEmpty()) {
            permissions = userPermissions.stream()
                    .map(permission ->  permission.getCode())
                    .collect(Collectors.toList());
            log.debug("用户权限处理完成: {}", permissions);
        } else {
            log.debug("用户暂无权限: {}", user.getUsername());
        }
        userInfoVO.setPermissions(permissions);

        return userInfoVO;

    }



    /**
     * 用户登出
     * 注意：由于使用JWT无状态认证，登出操作主要是客户端删除Token
     * 如需实现服务端Token失效，可考虑使用黑名单机制
     *
     * @param token 访问令牌
     */
    public void logout(String token) {
        log.debug("处理用户登出请求");
        if (jwtTokenUtil.validateToken(token)) {
            String username = jwtTokenUtil.getUsernameFromToken(token);
            log.info("用户登出: {}", username);
        }

    }


}
