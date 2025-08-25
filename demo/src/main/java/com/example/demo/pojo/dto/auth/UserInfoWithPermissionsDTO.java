package com.example.demo.pojo.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * 包含权限信息的用户信息DTO
 * 用于登录成功后返回给前端的用户信息，包含用户基本信息及其拥有的权限列表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoWithPermissionsDTO {
    
    /**
     * 用户ID
     * 系统中用户的唯一标识
     */
    private Long id;

    /**
     * 用户名
     * 用户登录账号
     */
    private String username;

    /**
     * 角色名称
     * 用户的主要角色名称
     */
    private String roleName;

    /**
     * 权限列表
     * 用户拥有的所有权限编码列表，用于前端进行权限控制
     */
    private List<String> permissions;

}
