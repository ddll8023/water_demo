package com.example.demo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * OpenAPI 3.0 配置
 * 配置Swagger文档生成和JWT认证
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(Arrays.asList(
                    new Server().url("http://localhost:8080").description("开发环境"),
                    new Server().url("https://api.example.com").description("生产环境")
                ))
                .components(new Components()
                    .addSecuritySchemes("Bearer", securityScheme())
                )
                .addSecurityItem(new SecurityRequirement().addList("Bearer"));
    }

    /**
     * API信息配置
     */
    private Info apiInfo() {
        return new Info()
                .title("鄂北地区水资源项目 API 文档")
                .description("鄂北地区水资源项目后端API接口文档，包含认证、用户管理、角色权限等功能模块")
                .version("v1.0")
                .contact(new Contact()
                    .name("开发团队")
                    .email("dev@example.com")
                    .url("https://example.com")
                )
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")
                );
    }

    /**
     * JWT安全认证配置
     */
    private SecurityScheme securityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("请在此处输入JWT Token，格式为：Bearer {token}");
    }
}