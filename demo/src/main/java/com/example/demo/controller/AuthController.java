package com.example.demo.controller;

import com.example.demo.dto.auth.LoginRequestDTO;
import com.example.demo.dto.auth.LoginResponseDTO;
import com.example.demo.dto.auth.RefreshTokenRequestDTO;
import com.example.demo.dto.auth.RefreshTokenResponseDTO;
import com.example.demo.dto.auth.UserInfoWithPermissionsDTO;
import com.example.demo.common.ApiResponse;
import com.example.demo.service.AuthService;
import com.example.demo.config.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 认证控制器
 * 处理用户登录、登出、Token刷新等认证相关请求
 * 
 * 主要功能包括：
 * 1. 用户登录认证并返回JWT令牌
 * 2. 用户登出处理
 * 3. 获取当前登录用户信息
 * 4. 刷新访问令牌
 * 5. 验证令牌有效性
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Api(tags = "认证管理", description = "用户登录、登出、令牌管理等认证相关接口")
public class AuthController {

    /**
     * 认证服务，处理具体的认证业务逻辑
     */
    private final AuthService authService;

    /**
     * JWT令牌工具类，用于Token的生成、解析和验证
     */
    private final JwtTokenUtil jwtTokenUtil;

    /**
     * 用户登录接口
     * 
     * @param loginRequest 登录请求DTO，包含用户名和密码
     * @return 登录成功返回用户信息和令牌；失败返回错误信息
     */
    @PostMapping("/login")
    @ApiOperation(value = "用户登录", notes = "验证用户凭据并返回JWT访问令牌和刷新令牌")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            // 调用认证服务进行用户登录验证
            LoginResponseDTO response = authService.login(loginRequest);
            // 登录成功，返回访问令牌和刷新令牌
            return ResponseEntity.ok(ApiResponse.success("登录成功", response));
        } catch (Exception e) {
            // 登录失败，可能是用户名或密码错误
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(401, e.getMessage()));
        }
    }

    /**
     * 用户登出接口
     * 
     * @param request HTTP请求对象，用于提取认证令牌
     * @return 登出成功返回成功信息；失败返回错误信息
     */
    @PostMapping("/logout")
    @ApiOperation(value = "用户登出", notes = "使当前用户的JWT令牌失效并执行登出操作")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        try {
            // 从请求头中提取Token
            String token = extractTokenFromRequest(request);
            if (token == null) {
                // 未提供Token，返回未授权错误
                return ResponseEntity.status(401)
                    .body(ApiResponse.error(401, "未提供认证Token"));
            }

            // 验证token有效性
            if (!jwtTokenUtil.validateToken(token)) {
                // Token无效或已过期，返回未授权错误
                return ResponseEntity.status(401)
                    .body(ApiResponse.error(401, "Token无效或已过期"));
            }

            // 调用服务执行登出操作（可能包括将Token加入黑名单等）
            authService.logout(token);
            return ResponseEntity.ok(ApiResponse.success("登出成功", null));
        } catch (Exception e) {
            // 登出过程中出现异常
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, "登出失败: " + e.getMessage()));
        }
    }

    /**
     * 获取当前用户信息接口
     * 
     * @param request HTTP请求对象，用于提取认证令牌
     * @return 成功返回用户详细信息；失败返回错误信息
     */
    @GetMapping("/me")
    @ApiOperation(value = "获取当前用户信息", notes = "根据JWT令牌获取当前登录用户的详细信息和权限列表")
    public ResponseEntity<ApiResponse<UserInfoWithPermissionsDTO>> getCurrentUser(
            HttpServletRequest request) {
        try {
            // 从请求头中提取Token
            String token = extractTokenFromRequest(request);
            if (token == null) {
                // 未提供Token，返回未授权错误
                return ResponseEntity.status(401)
                    .body(ApiResponse.error(401, "未提供认证Token"));
            }

            // 根据Token获取当前用户信息（包括权限列表）
            UserInfoWithPermissionsDTO userInfo = authService.getCurrentUser(token);
            return ResponseEntity.ok(ApiResponse.success("获取用户信息成功", userInfo));
        } catch (Exception e) {
            // 获取用户信息过程中出现异常
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, "获取用户信息失败: " + e.getMessage()));
        }
    }

    /**
     * 刷新Token接口
     * 当访问令牌即将过期时，客户端可以使用刷新令牌获取新的访问令牌
     * 
     * @param request 包含刷新令牌的请求DTO
     * @return 成功返回新的访问令牌；失败返回错误信息
     */
    @PostMapping("/refresh")
    @ApiOperation(value = "刷新Token", notes = "使用刷新令牌获取新的访问令牌")
    public ResponseEntity<ApiResponse<RefreshTokenResponseDTO>> refreshToken(
            @Valid @RequestBody RefreshTokenRequestDTO request) {
        try {
            // 使用刷新令牌获取新的访问令牌
            RefreshTokenResponseDTO response = authService.refreshToken(request.getRefreshToken());
            return ResponseEntity.ok(ApiResponse.success("Token刷新成功", response));
        } catch (Exception e) {
            // 刷新令牌无效或已过期
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 验证Token接口
     * 用于客户端验证当前持有的Token是否有效
     * 
     * @param request HTTP请求对象，用于提取认证令牌
     * @return 成功返回Token的有效性信息；失败返回错误信息
     */
    @GetMapping("/validate")
    @ApiOperation(value = "验证Token", notes = "验证当前持有的Token是否有效")
    public ResponseEntity<ApiResponse<java.util.Map<String, Object>>> validateToken(
            HttpServletRequest request) {
        try {
            // 从请求头中提取Token
            String token = extractTokenFromRequest(request);
            if (token == null) {
                // 未提供Token，返回未授权错误
                return ResponseEntity.status(401)
                    .body(ApiResponse.error(401, "未提供认证Token"));
            }

            // 验证Token是否有效
            boolean isValid = authService.validateToken(token);
            if (isValid) {
                // Token有效，提取Token中的信息
                String username = jwtTokenUtil.getUsernameFromToken(token);
                java.util.Date expirationDate = jwtTokenUtil.getExpirationDateFromToken(token);

                // 构建返回结果
                java.util.Map<String, Object> result = new java.util.HashMap<>();
                result.put("valid", true);
                result.put("username", username);
                result.put("expiresAt", expirationDate.toInstant().toString());

                return ResponseEntity.ok(ApiResponse.success("Token有效", result));
            } else {
                // Token无效或已过期
                return ResponseEntity.status(401)
                    .body(ApiResponse.error(401, "Token无效或已过期"));
            }
        } catch (Exception e) {
            // Token验证过程中出现异常
            return ResponseEntity.status(401)
                .body(ApiResponse.error(401, "Token验证失败: " + e.getMessage()));
        }
    }

    /**
     * 从HTTP请求中提取认证Token
     * 
     * @param request HTTP请求对象
     * @return 提取的Token字符串，如果没有找到则返回null
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        // 从Authorization请求头中提取Bearer令牌
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            // 去掉"Bearer "前缀，返回实际的Token
            return bearerToken.substring(7);
        }
        return null;
    }
}
