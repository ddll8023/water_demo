package com.example.demo.interceptor;

import com.example.demo.config.JwtTokenUtil;
import com.example.demo.config.JwtProperties;
import com.example.demo.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * JWT令牌校验拦截器
 */
@Component
@Slf4j
public class JwtTokenInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 校验JWT令牌
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            // 当前拦截到的不是动态方法，直接放行
            return true;
        }

        // 1、从请求头中获取令牌
        String token = request.getHeader(jwtProperties.getAdminTokenName());
        
        // 处理Bearer格式的token
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 2、校验令牌
        try {
            log.info("jwt校验:{}", token);
            boolean isValid = jwtTokenUtil.validateToken(token);
            if (isValid) {
                String username = jwtTokenUtil.getUsernameFromToken(token);
                Long userId = jwtTokenUtil.getUserIdFromToken(token);
                log.info("当前用户：{}, 用户ID：{}", username, userId);
                
                // 将用户ID存储到ThreadLocal中
                BaseContext.setCurrentId(userId);
                
                // 3、通过，放行
                return true;
            } else {
                // 令牌无效，响应401状态码
                response.setStatus(401);
                return false;
            }
        } catch (Exception ex) {
            // 4、不通过，响应401状态码
            response.setStatus(401);
            return false;
        }
    }

    /**
     * 请求完成后清理ThreadLocal，防止内存泄漏
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        BaseContext.removeCurrentId();
    }
} 