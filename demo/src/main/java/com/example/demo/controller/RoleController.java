package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.common.PageResult;
import com.example.demo.pojo.DTO.system.RoleCreateDTO;
import com.example.demo.pojo.DTO.system.RoleQueryDTO;

import com.example.demo.pojo.DTO.system.RoleUpdateDTO;
import com.example.demo.pojo.VO.RoleVO;
import com.example.demo.pojo.VO.RolePermissionVO;
import com.example.demo.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色管理控制器
 * 处理角色的CRUD操作和权限分配
 * 提供角色的创建、查询、更新、删除以及权限分配等功能
 * 所有操作均需要system:manage权限
 */
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "角色管理", description = "角色相关的CRUD操作和权限分配")
public class RoleController {
    /**
     * 角色服务
     */
    private final RoleService roleService;

    /**
     * 分页查询角色列表
     *
     * @param roleQueryDTO 角色查询条件，包含分页、搜索等参数
     * @return 返回分页的角色列表
     */
    @Operation(summary = "分页查询角色列表", description = "根据条件分页查询角色信息")
    @GetMapping("")
    public ResponseEntity<ApiResponse<PageResult<RoleVO>>> getRoles(
            @Valid RoleQueryDTO roleQueryDTO) {
        PageResult<RoleVO> result = roleService.getRoles(roleQueryDTO);
        return ResponseEntity.ok(ApiResponse.success("查询成功", result));
    }

    /**
     * 根据ID查询角色详情
     *
     * @param id 角色ID
     * @return 返回角色的详细信息
     */
    @Operation(summary = "查询角色详情", description = "根据ID查询角色详细信息")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleVO>> getRoleById(
            @PathVariable Long id) {
        RoleVO role = roleService.getRoleById(id);
        return ResponseEntity.ok(ApiResponse.success("查询成功", role));
    }

    /**
     * 创建角色
     *
     * @param roleCreateDTO 角色创建数据传输对象，包含角色名称、编码、描述等信息
     * @return 返回创建成功的角色信息
     */
    @Operation(summary = "创建角色", description = "创建新的角色信息")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Void>> createRole(
            @Valid @RequestBody RoleCreateDTO roleCreateDTO) {
        roleService.createRole(roleCreateDTO);
        return ResponseEntity.ok(ApiResponse.success("角色创建成功", null));
    }

    /**
     * 更新角色信息
     *
     * @param id        需要更新的角色ID
     * @param roleUpdateDTO 角色更新数据传输对象，包含需要更新的角色信息
     * @return 返回更新操作结果
     */
    @Operation(summary = "更新角色信息", description = "根据ID更新角色信息")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleUpdateDTO roleUpdateDTO) {
        roleService.updateRole(id, roleUpdateDTO);
        return ResponseEntity.ok(ApiResponse.success("角色更新成功", null));
    }

    /**
     * 删除角色
     * 执行角色的软删除操作
     *
     * @param id 需要删除的角色ID
     * @return 返回删除操作结果
     */
    @Operation(summary = "删除角色", description = "根据ID删除角色信息（软删除）")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRole(
            @PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok(ApiResponse.success("角色删除成功", null));
    }

    /**
     * 获取角色权限列表
     * 查询指定角色已分配的所有权限
     *
     * @param id 角色ID
     * @return 返回该角色拥有的权限列表
     */
    @Operation(summary = "获取角色权限列表", description = "查询指定角色已分配的所有权限")
    @GetMapping("/{id}/permissions")
    public ResponseEntity<ApiResponse<List<RolePermissionVO>>> getRolePermissions(
            @PathVariable Long id) {
        List<RolePermissionVO> permissions = roleService.getRolePermissions(id);
        return ResponseEntity.ok(ApiResponse.success("查询成功", permissions));
    }

    /**
     * 为角色分配权限
     * 更新指定角色的权限列表，会覆盖原有权限
     *
     * @param id            角色ID
     * @param permissionIds 需要分配的权限ID列表
     * @return 返回权限分配操作结果
     */
    @Operation(summary = "为角色分配权限", description = "更新指定角色的权限列表，会覆盖原有权限")
    @PutMapping("/{id}/permissions")
    public ResponseEntity<ApiResponse<Void>> assignPermissions(
            @PathVariable Long id,
            @RequestBody List<Long> permissionIds) {
        roleService.assignPermissions(id, permissionIds);
        return ResponseEntity.ok(ApiResponse.success("权限分配成功", null));
    }

    /**
     * 获取所有可用角色
     * 用于在前端下拉选择框中展示
     *
     * @return 返回所有可用的角色列表
     */
    @Operation(summary = "获取所有可用角色", description = "获取系统中所有可用于选择的角色列表")
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<RoleVO>>> getAllAvailableRoles() {
        List<RoleVO> roles = roleService.getAllAvailableRoles();
        return ResponseEntity.ok(ApiResponse.success("查询成功", roles));
    }

    /**
     * 检查角色名称是否可用
     * 验证角色名称是否已被使用
     *
     * @param name      需要检查的角色名称
     * @param excludeId 在更新操作时，需要排除的角色ID
     * @return 返回包含available字段和message字段的Map，true表示名称可用，false表示已被使用
     */
    @Operation(summary = "检查角色名称是否可用", description = "验证角色名称是否已被使用")
    @GetMapping("/check-name")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkRoleNameAvailable(
            @RequestParam String name,
            @RequestParam(required = false) Long excludeId) {
        Map<String, Object> result = roleService.checkNameAvailable(name, excludeId);
        return ResponseEntity.ok(ApiResponse.success("检查完成", result));
    }
}
