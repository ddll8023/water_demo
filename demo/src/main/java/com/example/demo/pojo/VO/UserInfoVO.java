package com.example.demo.pojo.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * 用户信息视图对象
 * 用于返回当前登录用户的基本信息和权限列表给前端
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "UserInfoVO", description = "用户信息视图对象")
public class UserInfoVO {
    
    /**
     * 用户ID
     * 系统中用户的唯一标识
     */
    @Schema(name = "id", description = "用户ID")
    private Long id;

    /**
     * 用户名
     * 用户登录账号
     */
    @Schema(name = "username", description = "用户名")
    private String username;

    /**
     * 角色名称
     * 用户的主要角色名称
     */
    @Schema(name = "roleName", description = "角色名称")
    private String roleName;

    /**
     * 权限列表
     * 用户拥有的所有权限编码列表，用于前端进行权限控制
     */
    @Schema(name = "permissions", description = "权限列表")
    private List<String> permissions;

} 