package com.example.demo.controller;

import com.example.demo.pojo.DTO.system.PermissionResponseDTO;
import com.example.demo.common.ApiResponse;
import com.example.demo.pojo.DTO.common.PageResponseDTO;
import com.example.demo.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 权限管理控制器
 * 该控制器负责处理与系统权限相关的所有操作，包括：
 * - 权限的分页查询和条件筛选
 * - 权限详情查询
 * - 权限树结构构建
 * - 菜单权限管理
 * - 权限删除
 * - 权限编码验证
 */
@RestController
@RequestMapping("/api/permissions")
@Api(tags = "权限管理", description = "权限相关的CRUD操作")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    /**
     * 分页查询权限列表
     * 
     * 根据传入的查询条件（关键词、类型）分页获取权限信息列表
     * 
     * @param page 页码，默认为1
     * @param size 每页大小，默认为10
     * @param keyword 搜索关键词，可根据权限名称或编码模糊查询
     * @param type 权限类型，如"菜单"或"按钮"
     * @return 权限分页信息，包含总数和权限列表
     */
    @GetMapping
    
    @ApiOperation(value = "分页查询权限列表", notes = "根据条件分页查询权限信息")
    public ResponseEntity<ApiResponse<PageResponseDTO<PermissionResponseDTO>>> getPermissions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type) {
        try {
            PageResponseDTO<PermissionResponseDTO> result =
                permissionService.getPermissions(page, size, keyword, type);
            return ResponseEntity.ok(ApiResponse.success("查询成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, "查询失败: " + e.getMessage()));
        }
    }

    /**
     * 根据ID查询权限详情
     * 
     * 获取指定权限ID的详细信息
     * 
     * @param id 权限ID
     * @return 权限详细信息
     */
    @GetMapping("/{id}")
    
    @ApiOperation(value = "根据ID查询权限详情", notes = "根据ID查询权限详细信息")
    public ResponseEntity<ApiResponse<PermissionResponseDTO>> getPermissionById(
            @PathVariable Long id) {
        try {
            PermissionResponseDTO permission = permissionService.getPermissionById(id);
            return ResponseEntity.ok(ApiResponse.success("查询成功", permission));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 获取权限树结构
     * 
     * 构建完整的权限层级树，用于权限管理界面展示
     * 权限树按照父子关系进行组织，清晰展示权限的层级结构
     * 
     * @return 权限树列表，包含层级关系
     */
    @GetMapping("/tree")
    
    @ApiOperation(value = "获取权限树结构", notes = "获取完整的权限层级树结构")
    public ResponseEntity<ApiResponse<List<PermissionResponseDTO>>> getPermissionTree() {
        try {
            List<PermissionResponseDTO> tree = permissionService.getPermissionTree();
            return ResponseEntity.ok(ApiResponse.success("查询成功", tree));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, "查询失败: " + e.getMessage()));
        }
    }

    /**
     * 获取菜单权限树
     * 
     * 获取仅包含菜单类型的权限树，用于前端侧边栏菜单的构建
     * 区别于完整权限树，该方法仅返回类型为菜单的权限项
     * 
     * @return 菜单权限树列表
     */
    @GetMapping("/menu-tree")
    
    @ApiOperation(value = "获取菜单权限树", notes = "获取仅包含菜单类型的权限树")
    public ResponseEntity<ApiResponse<List<PermissionResponseDTO>>> getMenuTree() {
        try {
            List<PermissionResponseDTO> menuTree = permissionService.getMenuTree();
            return ResponseEntity.ok(ApiResponse.success("查询成功", menuTree));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, "查询失败: " + e.getMessage()));
        }
    }

    /**
     * 根据权限类型获取权限列表
     * 
     * 按指定类型筛选权限，如仅获取"按钮"类型的权限列表
     * 
     * @param type 权限类型，如"MENU"（菜单）或"BUTTON"（按钮）
     * @return 指定类型的权限列表
     */
    @GetMapping("/by-type")
    
    @ApiOperation(value = "根据权限类型获取权限列表", notes = "根据权限类型获取权限列表")
    public ResponseEntity<ApiResponse<List<PermissionResponseDTO>>> getPermissionsByType(
            @RequestParam String type) {
        try {
            List<PermissionResponseDTO> permissions = permissionService.getPermissionsByType(type);
            return ResponseEntity.ok(ApiResponse.success("查询成功", permissions));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, "查询失败: " + e.getMessage()));
        }
    }

    /**
     * 获取所有可用权限
     * 
     * 获取系统中所有可用于分配的权限列表，主要用于角色权限分配时的权限选择
     * 
     * @return 所有可用于分配的权限列表
     */
    @GetMapping("/available")
    
    @ApiOperation(value = "获取所有可用权限", notes = "获取系统中所有可用于分配的权限列表")
    public ResponseEntity<ApiResponse<List<PermissionResponseDTO>>> getAllAvailablePermissions() {
        try {
            List<PermissionResponseDTO> permissions = permissionService.getAllAvailablePermissions();
            return ResponseEntity.ok(ApiResponse.success("查询成功", permissions));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, "查询失败: " + e.getMessage()));
        }
    }

    /**
     * 删除权限
     * 
     * 删除指定ID的权限（软删除）
     * 注意：如果该权限有子权限，将无法删除，需要先删除所有子权限
     * 
     * @param id 要删除的权限ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    
    @ApiOperation(value = "删除权限", notes = "根据ID删除权限信息（软删除）")
    public ResponseEntity<ApiResponse<Void>> deletePermission(
            @PathVariable Long id) {
        try {
            permissionService.deletePermission(id);
            return ResponseEntity.ok(ApiResponse.success("权限删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 检查权限编码是否可用
     * 
     * 验证指定的权限编码是否已存在，用于新增或编辑权限时的编码唯一性检查
     * 
     * @param code 待检查的权限编码
     * @param excludeId 排除的权限ID（在编辑权限时使用，排除自身）
     * @return 包含权限编码可用性的结果
     */
    @GetMapping("/check-code")
    
    @ApiOperation(value = "检查权限编码是否可用", notes = "检查指定的权限编码是否已存在")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkPermissionCodeAvailable(
            @RequestParam String code,
            @RequestParam(required = false) Long excludeId) {
        try {
            boolean available = permissionService.checkCodeAvailable(code, excludeId);
            Map<String, Boolean> result = new HashMap<>();
            result.put("available", available);
            return ResponseEntity.ok(ApiResponse.success("检查完成", result));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error(500, "系统内部错误"));
        }
    }
}
