package com.example.demo.pojo.dto.system;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 用户更新DTO
 * 用于更新系统用户账号信息的请求
 * 专注于系统登录账号管理，不包含业务人员信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserUpdateDTO {

    /**
     * 角色 ID
     */
    private Long roleId;

    /**
     * 账户是否激活
     */
    private String isActive;
}
