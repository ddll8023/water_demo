package com.example.demo.controller;

import com.example.demo.pojo.DTO.auth.LoginDTO;


import com.example.demo.pojo.VO.UserInfoVO;
import com.example.demo.pojo.VO.LoginUserVO;
import com.example.demo.common.ApiResponse;
import com.example.demo.service.AuthService;
import com.example.demo.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 认证管理控制器
 * 
 * 提供用户登录、登出、令牌管理等认证相关接口
 * 
 * 包括：
 * - 用户登录
 * - 用户登出
 * - 获取当前用户信息
 * - 刷新Token
 * - 验证Token
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证管理", description = "用户登录、登出、令牌管理等认证相关接口")
@RequiredArgsConstructor
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
     * @param loginDTO 登录请求DTO，包含用户名和密码
     * @return 登录成功返回用户信息和令牌；失败返回错误信息
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "验证用户凭据并返回JWT访问令牌和刷新令牌")
    public ResponseEntity<ApiResponse<LoginUserVO>> login(
            @Valid @RequestBody LoginDTO loginDTO) {
        LoginUserVO loginUserVO = authService.login(loginDTO);
        return ResponseEntity.ok(ApiResponse.success("登录成功", loginUserVO));
    }

    /**
     * 用户登出接口
     * 
     * @param request HTTP请求对象，用于提取认证令牌
     * @return 登出成功返回成功信息；失败返回错误信息
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "使当前用户的JWT令牌失效并执行登出操作")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        // 从请求头中提取Token
        String token = jwtTokenUtil.extractTokenFromRequest(request);
        if (token == null) {
            // 未提供Token，返回未授权错误
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(401, "未提供认证Token"));
        }

        // 调用服务执行登出操作（可能包括将Token加入黑名单等）
        authService.logout(token);
        return ResponseEntity.ok(ApiResponse.success("登出成功", null));
    }

    /**
     * 获取当前用户信息接口
     * 
     * @param request HTTP请求对象，用于提取认证令牌
     * @return 成功返回用户详细信息；失败返回错误信息
     */
    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息", description = "根据JWT令牌获取当前登录用户的详细信息和权限列表")
    public ResponseEntity<ApiResponse<UserInfoVO>> getCurrentUser(
            HttpServletRequest request) {
        // 从请求头中提取Token
        String token = jwtTokenUtil.extractTokenFromRequest(request);
        // 根据Token获取当前用户信息（包括权限列表）
        UserInfoVO userInfo = authService.getCurrentUserInfo(token);
        return ResponseEntity.ok(ApiResponse.success("获取用户信息成功", userInfo));
    }





}
