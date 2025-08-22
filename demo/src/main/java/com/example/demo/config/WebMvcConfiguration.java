package com.example.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import java.util.Arrays;
import java.util.List;

/**
 * 配置类，注册web层相关组件
 * 使用Knife4j生成接口文档
 */
@Configuration
@Slf4j
public class WebMvcConfiguration implements WebMvcConfigurer {

    /**
     * 通过knife4j生成接口文档
     * @return Docket配置
     */
    @Bean
    public Docket createRestApi() {
        log.info("开始生产API接口文档");
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example.demo.controller"))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    /**
     * API信息配置
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("鄂北地区水资源项目 API 文档")
                .description("鄂北地区水资源项目后端API接口文档，包含认证、用户管理、角色权限等功能模块")
                .version("v1.0")
                .contact(new Contact("开发团队", "https://example.com", "dev@example.com"))
                .license("MIT License")
                .licenseUrl("https://opensource.org/licenses/MIT")
                .build();
    }

    /**
     * 安全模式配置
     */
    private List<SecurityScheme> securitySchemes() {
        return Arrays.asList(
            new ApiKey("Bearer", "Authorization", "header")
        );
    }

    /**
     * 安全上下文配置
     */
    private List<SecurityContext> securityContexts() {
        return Arrays.asList(
            SecurityContext.builder()
                .securityReferences(securityReferences())
                .forPaths(PathSelectors.regex("/api/.*"))
                .build()
        );
    }

    /**
     * 安全引用配置
     */
    private List<SecurityReference> securityReferences() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(
            new SecurityReference("Bearer", authorizationScopes)
        );
    }

    /**
     * 设置静态资源映射
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始静态资源映射");
        // Knife4j文档相关资源
        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        // 上传文件资源映射
        registry.addResourceHandler("/uploads/**").addResourceLocations("file:uploads/");
    }
}
