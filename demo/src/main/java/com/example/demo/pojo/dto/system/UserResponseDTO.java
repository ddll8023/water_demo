package com.example.demo.pojo.dto.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户响应DTO
 * 用于返回系统用户账号信息
 * 专注于系统登录账号管理，不包含业务人员信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserResponseDTO {

    /**
     * 用户 ID
     */
    private Long id;

    /**
     * 用户名 (用于登录)
     */
    private String username;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 角色 ID（兼容保留）
     */
    private Long roleId;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 账户是否激活
     */
    private String isActive;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLogin;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

}
