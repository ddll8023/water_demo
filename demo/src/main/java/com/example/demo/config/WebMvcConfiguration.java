package com.example.demo.config;

import com.example.demo.interceptor.JwtTokenInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Web MVC配置类
 * 统一管理Web层相关组件配置，包括：
 * 1. Knife4j API文档配置
 * 2. 静态资源映射配置
 * 3. CORS跨域配置
 * 4. JWT拦截器配置
 */
@Configuration
@Slf4j
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

    @Autowired
    private JwtTokenInterceptor jwtTokenInterceptor;

    /**
     * 注册自定义拦截器
     *
     * @param registry
     */
    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册JWT拦截器...");
        registry.addInterceptor(jwtTokenInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/**");
    }

    /**
     * CORS跨域配置
     * 解决前端（localhost:5173）访问后端API（localhost:8080）的跨域问题
     */
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        log.info("配置CORS跨域支持");
        registry.addMapping("/api/**")
                .allowedOrigins(
                        "http://localhost:5173",
                        "http://localhost:5174"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * 通过knife4j生成接口文档
     * @return Docket配置
     */
    @Bean
    public Docket createRestApi() {
        log.info("开始生成API接口文档");
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example.demo.controller"))
                .paths(PathSelectors.any())
                .build();
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
     * 设置静态资源映射
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始静态资源映射");
        // Knife4j文档相关资源
        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        // 上传文件资源映射
        registry.addResourceHandler("/uploads/**").addResourceLocations("file:uploads/");
    }
}
