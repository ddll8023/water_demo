package com.example.demo.pojo.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.demo.pojo.entity.system.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户视图对象
 * 用于处理包含关联信息的用户数据
 */
@Data
@Schema(name = "UserVO", description = "用户视图对象")
public class UserVO {

    @Schema(name = "id", description = "用户ID")
    private Long id;

    @Schema(name = "username", description = "用户名")
    private String username;

    @Schema(name = "password", description = "加密后的密码")
    private String password;

    @Schema(name = "roleId", description = "角色ID")
    private Long roleId;

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

    @Schema(name = "deletedAt", description = "软删除时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deletedAt;

    // ========== 关联信息字段（非数据库字段，用于存储关联查询结果） ==========

    @Schema(name = "roleName", description = "角色名称")
    private String roleName;
    
    @Schema(name = "roles", description = "用户角色列表")
    private List<Role> roles;

} 