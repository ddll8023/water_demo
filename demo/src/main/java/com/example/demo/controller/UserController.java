package com.example.demo.controller;

import com.example.demo.pojo.DTO.system.UserCreateDTO;
import com.example.demo.pojo.DTO.system.UserResponseDTO;
import com.example.demo.pojo.DTO.system.UserUpdateDTO;
import com.example.demo.common.ApiResponse;
import com.example.demo.common.PageResult;
import com.example.demo.pojo.entity.system.Role;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 用户管理控制器
 * 
 * 提供用户相关的CRUD操作
 * 需要system:manage权限
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "用户管理", description = "用户相关的CRUD操作，需要system:manage权限")
public class UserController {

    /**
     * 用户服务
     */
    @Autowired
    private UserService userService;

    /**
     * 分页查询用户列表
     * 
     * @param page 页码，默认为1
     * @param size 每页大小，默认为10
     * @param username 用户名搜索，可选参数
     * @param roleId 角色ID，可选参数
     * @param isActive 是否活跃（"1"启用，"0"禁用），可选参数
     * @return 分页的用户信息列表
     */
    @Operation(summary = "分页查询用户列表", description = "根据条件分页查询用户信息")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResult<UserResponseDTO>>> getUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Long roleId,
            @RequestParam(required = false) String isActive) {
        try {
            PageResult<UserResponseDTO> result = userService.getUsers(
                page, size, username, roleId, isActive);
            return ResponseEntity.ok(ApiResponse.success("查询成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, "查询失败: " + e.getMessage()));
        }
    }

    /**
     * 根据ID查询用户详情
     * 
     * @param id 用户ID
     * @return 用户详细信息
     */
    @Operation(summary = "根据ID查询用户详情", description = "根据用户ID查询用户详细信息")
    @GetMapping("/{id}")
    
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserById(
            @PathVariable Long id) {
        try {
            UserResponseDTO user = userService.getUserById(id);
            return ResponseEntity.ok(ApiResponse.success("查询成功", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 创建新用户
     * 
     * @param createDTO 创建用户请求DTO
     * @return 创建后的用户信息
     */
    @Operation(summary = "创建新用户", description = "创建新用户")
    @PostMapping
    
    public ResponseEntity<ApiResponse<UserResponseDTO>> createUser(
            @Valid @RequestBody UserCreateDTO createDTO) {
        try {
            UserResponseDTO user = userService.createUser(createDTO);
            return ResponseEntity.ok(ApiResponse.success("创建成功", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 更新用户信息
     * 
     * @param id 用户ID
     * @param updateDTO 更新用户请求DTO
     * @return ????????
     */
    @Operation(summary = "更新用户信息", description = "根据用户ID更新用户信息")
    @PutMapping("/{id}")
    
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO updateDTO) {
        try {
            UserResponseDTO user = userService.updateUser(id, updateDTO);
            return ResponseEntity.ok(ApiResponse.success("更新成功", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 删除用户
     * 
     * @param id 用户ID
     * @return 删除结果
     */
    @Operation(summary = "删除用户", description = "根据用户ID删除用户")
    @DeleteMapping("/{id}")
    
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.success("删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 获取用户角色列表
     *
     * @param id 用户ID
     * @return 用户角色列表
     */
    @Operation(summary = "获取用户角色列表", description = "根据用户ID获取用户角色列表")
    @GetMapping("/{id}/roles")
    
    public ResponseEntity<ApiResponse<List<Role>>> getUserRoles(
            @PathVariable Long id) {
        try {
            List<Role> roles = userService.getUserRoles(id);
            return ResponseEntity.ok(ApiResponse.success("查询成功", roles));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 为用户分配角色
     *
     * @param id 用户ID
     * @param roleIds 角色ID列表
     * @return 分配结果
     */
    @Operation(summary = "为用户分配角色", description = "根据用户ID和角色ID列表为用户分配角色")
    @PutMapping("/{id}/roles")

    public ResponseEntity<ApiResponse<Void>> assignUserRoles(
            @PathVariable Long id,
            @RequestBody List<Long> roleIds) {
        try {
            userService.assignUserRoles(id, roleIds);
            return ResponseEntity.ok(ApiResponse.success("分配成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }
}
