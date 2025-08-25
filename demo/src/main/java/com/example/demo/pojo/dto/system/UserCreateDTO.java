package com.example.demo.pojo.dto.system;

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
public class UserCreateDTO {

    /**
     * 用户名 (用于登录)
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度必须在6-100个字符之间")
    private String password;

    /**
     * 电子邮箱
     */
    @Email(message = "电子邮箱格式不正确")
    private String email;

    /**
     * 角色 ID
     */
    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    /**
     * 账户是否激活
     */
    private String isActive = "1";

}
