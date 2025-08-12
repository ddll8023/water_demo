package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT认证过滤器
 * 
 * 此过滤器是JWT认证机制的核心组件，主要职责包括：
 * 1. 拦截所有HTTP请求
 * 2. 从请求头中提取JWT令牌
 * 3. 验证令牌的有效性
 * 4. 加载用户信息
 * 5. 设置Spring Security的认证上下文
 * 
 * 继承OncePerRequestFilter确保每个请求只会被过滤一次，
 * 避免在一次请求处理中多次执行过滤逻辑
 */
@Component  // 标记为Spring组件，会被自动注册到Spring容器
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * 用户详情服务
     * 用于根据从JWT令牌中提取的用户名加载完整的用户信息
     */
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * JWT令牌工具类
     * 提供解析、验证JWT令牌的功能
     */
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * 过滤器的核心方法
     * 
     * 处理所有HTTP请求的JWT认证逻辑：
     * 1. 从请求头提取令牌
     * 2. 解析用户名
     * 3. 验证令牌
     * 4. 设置认证信息
     * 
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @param chain 过滤器链，用于将请求传递给下一个过滤器或目标资源
     * @throws ServletException 如果处理请求时发生servlet异常
     * @throws IOException 如果处理请求时发生IO异常
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request, 
            @NonNull HttpServletResponse response, 
            @NonNull FilterChain chain) throws ServletException, IOException {
        
        // 1. 从HTTP请求头中获取Authorization值
        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;

        // 2. 提取JWT令牌
        // HTTP请求头格式为: Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            // 去除"Bearer "前缀，获取实际的JWT令牌字符串
            jwtToken = requestTokenHeader.substring(7);
            try {
                // 3. 从令牌中提取用户名
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (Exception e) {
                // 如令牌过期、签名无效、格式错误等情况会触发异常
                logger.warn("无法获取JWT Token中的用户名: " + e.getMessage());
            }
        } else {
            // 请求头不存在或格式不正确时记录调试信息
            logger.debug("JWT Token不存在或格式不正确");
        }

        // 4. 验证令牌并设置安全上下文
        // 只有成功提取到用户名且当前没有认证信息时才进行处理
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 5. 从数据库加载完整的用户信息（权限等）
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 6. 验证令牌是否有效（检查用户名匹配、令牌是否过期等）
            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                // 7. 创建认证对象
                // 第一个参数是主体(principal)，即用户
                // 第二个参数是凭证(credentials)，通常是密码，这里设为null因为已经验证过了
                // 第三个参数是用户权限列表
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                
                // 8. 设置认证详情（如远程地址、会话ID等）
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // 9. 将认证信息设置到Spring Security上下文
                // 这使得后续的请求处理器能够知道用户是谁以及拥有什么权限
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        // 10. 继续过滤器链的处理
        // 无论是否成功认证，都将请求传递给下一个过滤器
        chain.doFilter(request, response);
    }
}
