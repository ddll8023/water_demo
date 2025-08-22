package com.example.demo.config;

import com.example.demo.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security主配置类
 * 
 * 本类是系统安全配置的核心类，主要完成以下功能：
 * 1. 配置JWT认证机制，替代传统的session认证
 * 2. 配置权限控制，定义哪些URL可以公开访问，哪些需要认证
 * 3. 配置安全策略，如CSRF防护、CORS支持等
 * 4. 集成自定义用户认证服务和JWT过滤器
 * 
 * 继承WebSecurityConfigurerAdapter以便自定义安全配置
 */
@Configuration  // 标记为Spring配置类
@EnableWebSecurity  // 启用Spring Security的Web安全支持
@EnableGlobalMethodSecurity(prePostEnabled = true)  // 启用方法级别的权限控制，支持@PreAuthorize注解
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * JWT认证异常处理点
     * 当认证失败或未授权时，由此组件处理返回401错误
     */
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    /**
     * 自定义用户详情服务
     * 负责从数据库加载用户信息并构建UserDetails对象
     */
    private final CustomUserDetailsService userDetailsService;

    /**
     * JWT认证过滤器
     * 拦截所有请求，提取并验证JWT令牌，设置认证上下文
     */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 密码编码器
     * 用于加密密码和验证密码匹配
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * 配置认证管理器
     * 
     * 设置自定义的用户详情服务和密码编码器：
     * 1. userDetailsService定义如何加载用户详情
     * 2. passwordEncoder定义如何验证密码匹配
     * 
     * 认证过程：用户提供凭证 → userDetailsService加载用户 → passwordEncoder验证密码
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    /**
     * 认证管理器Bean
     * 
     * 将AuthenticationManager暴露为Spring Bean，以便在其他组件中注入使用，
     * 特别是在AuthController中用于验证用户名和密码
     *
     * @return AuthenticationManager 认证管理器实例
     * @throws Exception 如果Bean创建失败
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * HTTP安全配置
     * 
     * 配置Web安全的核心方法，定义：
     * 1. 哪些URL路径需要什么级别的权限
     * 2. 安全防护机制（CSRF、CORS等）
     * 3. 认证机制（JWT）
     * 4. 异常处理策略
     * 5. 会话管理策略
     *
     * @param httpSecurity HTTP安全配置对象
     * @throws Exception 如果配置过程出错
     */
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            // 禁用CSRF（跨站请求伪造）防护
            // 原因：使用JWT基于令牌的认证不需要CSRF防护，因为JWT不依赖Cookie
            .csrf().disable()

            // 启用CORS（跨域资源共享）支持
            // 允许前端应用从不同域名/端口访问API
            .cors().and()
            
            // 配置请求授权规则
            .authorizeRequests()
                // 认证接口允许公开访问（用于登录、注册等功能）
                .antMatchers("/api/auth/**").permitAll()
                
                // Knife4j API文档相关路径允许公开访问（方便开发和调试）
                .antMatchers("/doc.html", "/webjars/**", "/swagger-resources/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
                
                // 静态资源允许公开访问
                .antMatchers("/", "/static/**", "/uploads/**").permitAll()
                
                // 其他所有请求都需要通过认证才能访问
                // 这确保了API的安全性，未认证用户无法访问业务功能
                .anyRequest().authenticated()
            .and()
            
            // 配置异常处理机制
            .exceptionHandling()
                // 设置认证入口点，处理认证异常（如令牌无效、过期等）
                // 当访问需要认证的资源但未提供有效认证时，由jwtAuthenticationEntryPoint处理
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .and()
            
            // 配置会话管理策略
            .sessionManagement()
                // 设置为无状态（STATELESS）模式
                // 不使用Session存储认证信息，完全依赖JWT令牌
                // 这使API服务更适合分布式和微服务架构
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // 添加JWT认证过滤器到Spring Security过滤链中
        // 确保在UsernamePasswordAuthenticationFilter之前执行
        // 这样可以在原生认证之前验证JWT令牌并设置认证上下文
        httpSecurity.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
