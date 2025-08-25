package com.example.demo.pojo.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 登录用户视图对象
 * 用于登录成功后返回的完整响应信息，包含用户信息、Token信息和权限
 */
@Data
@Schema(name = "LoginUserVO", description = "登录用户视图对象")
public class LoginUserVO {

    @Schema(name = "id", description = "用户ID")
    private Long id;

    @Schema(name = "username", description = "用户名")
    private String username;

    @Schema(name = "roleId", description = "角色ID")
    private Long roleId;

    @Schema(name = "roleName", description = "角色名称")
    private String roleName;

    @Schema(name = "isActive", description = "账户是否激活")
    private String isActive;

    @Schema(name = "lastLogin", description = "最后登录时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLogin;

    @Schema(name = "createdAt", description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(name = "updatedAt", description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // 以下为登录响应相关字段
    
    @Schema(name = "accessToken", description = "访问令牌")
    private String accessToken;



    @Schema(name = "tokenType", description = "令牌类型")
    private String tokenType = "Bearer";

    @Schema(name = "expiresIn", description = "访问令牌过期时间（秒）")
    private Long expiresIn;

    @Schema(name = "permissions", description = "用户权限列表")
    private List<String> permissions;
} 