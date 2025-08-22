package com.example.demo.controller;

import com.example.demo.dto.system.UserCreateDTO;
import com.example.demo.dto.system.UserResponseDTO;
import com.example.demo.dto.system.UserUpdateDTO;
import com.example.demo.common.ApiResponse;
import com.example.demo.dto.common.PageResponseDTO;
import com.example.demo.entity.system.Role;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 用户管理控制器
 * 
 * 处理用户的CRUD操作、权限查询、状态管理等功能
 * 所有API都需要system:manage权限
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Api(tags = "用户管理", description = "用户相关的CRUD操作、权限查询、状态管理等功能")
public class UserController {

    /**
     * 用户服务
     */
    private final UserService userService;

    /**
     * 分页查询用户列表
     * 
     * @param page 页码，默认为1
     * @param size 每页大小，默认为10
     * @param username 按用户名筛选
     * @param roleId 按角色ID筛选
     * @param isActive 按激活状态筛选
     * @return 分页的用户信息列表
     */
    @ApiOperation(value = "分页查询用户列表", notes = "根据条件分页查询用户信息")
    @GetMapping
    @PreAuthorize("hasAuthority('system:manage')")
    public ResponseEntity<ApiResponse<PageResponseDTO<UserResponseDTO>>> getUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Long roleId,
            @RequestParam(required = false) Boolean isActive) {
        try {
            PageResponseDTO<UserResponseDTO> result = userService.getUsers(
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
    @ApiOperation(value = "查询用户详情", notes = "根据ID查询用户详细信息")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:manage')")
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
     * 创建用户
     * 
     * @param createDTO 用户创建信息
     * @return 创建成功的用户信息
     */
    @ApiOperation(value = "创建用户", notes = "创建新的用户信息")
    @PostMapping
    @PreAuthorize("hasAuthority('system:manage')")
    public ResponseEntity<ApiResponse<UserResponseDTO>> createUser(
            @Valid @RequestBody UserCreateDTO createDTO) {
        try {
            UserResponseDTO user = userService.createUser(createDTO);
            return ResponseEntity.ok(ApiResponse.success("用户创建成功", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 更新用户信息
     * 
     * @param id 用户ID
     * @param updateDTO 更新的用户信息
     * @return 更新后的用户信息
     */
    @ApiOperation(value = "更新用户信息", notes = "根据ID更新用户信息")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:manage')")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO updateDTO) {
        try {
            UserResponseDTO user = userService.updateUser(id, updateDTO);
            return ResponseEntity.ok(ApiResponse.success("用户更新成功", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 删除用户（软删除）
     * 
     * @param id 用户ID
     * @return 删除结果
     */
    @ApiOperation(value = "删除用户", notes = "根据ID删除用户信息（软删除）")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:manage')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.success("用户删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 获取用户的角色列表
     *
     * @param id 用户ID
     * @return 用户的角色列表
     */
    @ApiOperation(value = "获取用户角色列表", notes = "查询指定用户已分配的所有角色")
    @GetMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('system:manage')")
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
     * @return 更新结果
     */
    @ApiOperation(value = "为用户分配角色", notes = "更新指定用户的角色列表，会覆盖原有角色")
    @PutMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('system:manage')")
    public ResponseEntity<ApiResponse<Void>> assignUserRoles(
            @PathVariable Long id,
            @RequestBody List<Long> roleIds) {
        try {
            userService.assignUserRoles(id, roleIds);
            return ResponseEntity.ok(ApiResponse.success("角色分配成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }
}
