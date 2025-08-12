package com.example.demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT认证入口点
 * 
 * 本类是Spring Security认证异常处理的核心组件，主要负责：
 * 1. 捕获认证异常（如未认证、令牌无效、令牌过期等）
 * 2. 构建统一的认证错误响应
 * 3. 返回友好的401错误信息
 * 
 * 当用户尝试访问需要认证的资源，但未提供有效的JWT令牌时，
 * 系统将调用该组件返回标准格式的401错误响应。
 * 
 * 这样可以确保API的安全边界，同时为客户端提供明确的错误信息。
 */
@Component  // 标记为Spring组件，自动注册到Spring容器
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * 处理认证异常的方法
     * 
     * 当认证失败时Spring Security会调用此方法，由该方法负责构建并发送
     * 认证失败的响应信息给客户端。
     * 
     * @param request HTTP请求对象，包含原始请求信息
     * @param response HTTP响应对象，用于发送错误响应
     * @param authException 认证异常对象，包含认证失败的详细信息
     * @throws IOException 如果写入响应时发生IO错误
     */
    @Override
    public void commence(HttpServletRequest request, 
                        HttpServletResponse response,
                        AuthenticationException authException) throws IOException {
        
        // 设置响应类型为JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // 设置HTTP状态码为401（未授权）
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // 构建错误响应内容，采用统一的API响应格式
        Map<String, Object> body = new HashMap<>();
        body.put("code", 401);                                      // 错误码
        body.put("message", "未授权访问：请提供有效的认证信息");      // 错误消息
        body.put("data", null);                                     // 数据字段为空
        body.put("timestamp", System.currentTimeMillis());          // 时间戳
        body.put("path", request.getServletPath());                 // 请求路径

        // 使用Jackson库将错误信息转换为JSON格式并写入响应
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
        
        // 方法执行完毕后，响应会被发送回客户端
        // 客户端将收到格式化的JSON错误消息，而不是默认的错误页面
    }
}
