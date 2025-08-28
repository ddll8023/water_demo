package com.example.demo.pojo.DTO.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 用户创建DTO
 * 用于创建新的系统用户账号
 * 专注于系统登录账号管理，不包含业务人员信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "UserCreateDTO", description = "用户创建DTO")
public class UserCreateDTO {

    @Schema(name = "username", description = "用户名，用于登录", example = "admin")
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    private String username;

    @Schema(name = "password", description = "登录密码", example = "123456")
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度必须在6-100个字符之间")
    private String password;

    @Schema(name = "email", description = "电子邮箱地址", example = "user@example.com")
    @Email(message = "电子邮箱格式不正确")
    @Size(max = 100, message = "电子邮箱长度不能超过100个字符")
    private String email;

    @Schema(name = "roleId", description = "角色ID", example = "1")
    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    @Schema(name = "isActive", description = "账户是否激活，1-启用，0-禁用", example = "1")
    private String isActive = "1";

}
