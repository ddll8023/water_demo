package com.example.demo.service;

import com.example.demo.entity.system.Permission;
import com.example.demo.entity.system.User;
import com.example.demo.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义用户详情服务
 * 实现Spring Security的UserDetailsService接口
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("开始加载用户详情: {}", username);
        
        // 从数据库查询用户信息
        User user = userMapper.selectByUsernameWithRole(username);

        if (user == null) {
            log.error("用户不存在: {}", username);
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        if (!user.getIsActive()) {
            log.error("用户已被禁用: {}", username);
            throw new UsernameNotFoundException("用户已被禁用: " + username);
        }

        log.debug("用户信息查询成功: {}, 角色: {}", user.getUsername(), user.getRoleName());
        
        UserDetails userDetails = buildUserDetails(user);
        log.info("用户详情加载完成: {}", username);
        
        return userDetails;
    }



    /**
     * 将User实体转换为UserDetails
     */
    private UserDetails buildUserDetails(User user) {
        log.debug("开始构建用户详情: {}", user.getUsername());
        
        // 查询用户权限
        log.debug("开始查询用户权限: userId={}", user.getId());
        List<Permission> permissions = userMapper.selectPermissionsByUserId(user.getId());
        
        if (permissions == null || permissions.isEmpty()) {
            log.warn("用户没有任何权限: {}, userId={}", user.getUsername(), user.getId());
        } else {
            log.debug("查询到用户权限数量: {}, 权限列表: {}", 
                permissions.size(), 
                permissions.stream().map(Permission::getCode).collect(Collectors.joining(", ")));
        }
        
        Collection<GrantedAuthority> authorities = buildAuthorities(permissions);
        
        log.debug("构建用户详情完成: {}, 权限数量: {}", user.getUsername(), authorities.size());

        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPasswordHash())
            .disabled(!user.getIsActive())
            .authorities(authorities)
            .build();
    }

    /**
     * 构建用户权限
     */
    private Collection<GrantedAuthority> buildAuthorities(List<Permission> permissions) {
        log.debug("开始构建用户权限列表");
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (permissions != null) {
            for (Permission permission : permissions) {
                // 添加权限代码
                log.trace("添加权限: {}", permission.getCode());
                authorities.add(new SimpleGrantedAuthority(permission.getCode()));
            }
        }

        log.debug("用户权限列表构建完成, 共 {} 个权限", authorities.size());
        return authorities;
    }
}
