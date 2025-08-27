package com.example.demo.controller;

import com.example.demo.pojo.VO.PermissionVO;
import com.example.demo.common.ApiResponse;
import com.example.demo.service.PermissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 权限管理控制器
 * 该控制器负责处理与系统权限相关的操作
 */
@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@Tag(name = "权限管理", description = "权限相关操作")
public class PermissionController {

    private final PermissionService permissionService;

    /**
     * 获取权限树结构
     * 
     * 构建完整的权限层级树，用于权限管理界面展示
     * 权限树按照父子关系进行组织，清晰展示权限的层级结构
     * 
     * @return 权限树列表，包含层级关系
     */
    @GetMapping("/tree")
    @Operation(summary = "获取权限树结构", description = "获取完整的权限层级树结构")
    public ResponseEntity<ApiResponse<List<PermissionVO>>> getPermissionTree() {
        List<PermissionVO> tree = permissionService.getPermissionTree();
        return ResponseEntity.ok(ApiResponse.success("查询成功", tree));
    }
}
