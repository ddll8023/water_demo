package com.example.demo.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应DTO
 * 包含用户登录成功后返回的令牌信息和用户基本信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {

    /**
     * 访问令牌，用于用户身份验证
     */
    private String accessToken;

    /**
     * 刷新令牌，用于获取新的访问令牌
     */
    private String refreshToken;

    /**
     * 令牌类型，默认为"Bearer"
     */
    private String tokenType = "Bearer";

    /**
     * 访问令牌过期时间（秒）
     */
    private Long expiresIn;

    /**
     * 用户基本信息
     */
    private UserInfoDTO userInfo;

    /**
     * 用户权限列表
     */
    private java.util.List<String> permissions;

    /**
     * 用户角色
     */
    private String roleName;


    /**
     * 用户信息内嵌类
     * 包含用户基本身份信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfoDTO {

        /**
         * 用户ID
         */
        private Long id;

        /**
         * 用户名
         */
        private String username;

        /**
         * 角色名称
         */
        private String roleName;
    }
}
