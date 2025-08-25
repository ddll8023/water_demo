package com.example.demo.pojo.DTO.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 刷新令牌响应DTO
 * 用于token刷新接口返回的数据对象，包含新的访问令牌和刷新令牌
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenResponseDTO {

    /**
     * 访问令牌
     * 用于API请求验证的JWT令牌，有较短的有效期
     */
    private String accessToken;

    /**
     * 刷新令牌
     * 用于获取新访问令牌的令牌，有较长的有效期
     */
    private String refreshToken;

    /**
     * 令牌类型
     * JWT令牌类型，默认为"Bearer"
     */
    private String tokenType = "Bearer";

    /**
     * 有效期
     * 访问令牌的有效期，单位为秒
     */
    private Long expiresIn;
}
