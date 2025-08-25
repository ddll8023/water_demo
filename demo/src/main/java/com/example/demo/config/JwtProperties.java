package com.example.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT配置属性类
 * 统一管理JWT相关的配置参数
 */
@Component
@ConfigurationProperties(prefix = "demo.jwt")
@Data
public class JwtProperties {

    /**
     * JWT签名密钥
     */
    private String adminSecretKey;

    /**
     * 访问令牌过期时间（毫秒）
     */
    private Long adminTtl;

    /**
     * 刷新令牌过期时间（毫秒）
     */
    private Long adminRefreshTtl;

    /**
     * 令牌名称
     */
    private String adminTokenName = "Authorization";
} 