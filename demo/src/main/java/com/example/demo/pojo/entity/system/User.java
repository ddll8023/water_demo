package com.example.demo.pojo.entity.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * 用户信息表实体类
 * 对应数据库表: users
 */
@Data
@Schema(name = "User", description = "用户信息表")
public class User implements Serializable {

    @Schema(name = "id", description = "用户ID")
    private Long id;

    @Schema(name = "username", description = "用户名")
    private String username;

    @Schema(name = "password", description = "加密后的密码")
    private String password;

    @Schema(name = "roleId", description = "角色ID")
    private Long roleId;

    @Schema(name = "isActive", description = "账户是否激活")
    private String isActive = "1";

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

}
