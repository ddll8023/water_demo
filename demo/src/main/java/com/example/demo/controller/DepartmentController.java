package com.example.demo.controller;

import com.example.demo.dto.system.DepartmentCreateDTO;
import com.example.demo.dto.system.DepartmentResponseDTO;
import com.example.demo.dto.system.DepartmentUpdateDTO;
import com.example.demo.common.ApiResponse;
import com.example.demo.dto.common.PageResponseDTO;
import com.example.demo.dto.common.ValidationResponseDTO;
import com.example.demo.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 部门管理控制器
 * <p>
 * 提供部门相关的增删改查操作，包括：
 * - 部门列表的分页查询
 * - 部门详情查询
 * - 创建、更新、删除部门
 * - 部门树形结构获取
 * - 部门下用户查询
 * - 部门状态管理
 * - 名称唯一性校验
 * - 部门统计信息
 * <p>
 * 所有API均需要相应权限，主要面向系统管理员使用
 */
@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    /**
     * 部门服务组件，负责处理部门相关的业务逻辑
     * 通过构造函数注入（@RequiredArgsConstructor + final）
     */
    private final DepartmentService departmentService;

    /**
     * 分页查询部门列表
     * <p>
     * 支持按名称关键词模糊搜索，可以筛选启用/禁用状态
     * 需要系统管理权限
     *
     * @param page 当前页码，从1开始
     * @param size 每页记录数
     * @param keyword 搜索关键词，可选参数，用于部门名称的模糊匹配
     * @param isActive 部门状态筛选，可选参数，true表示启用，false表示禁用
     * @return 部门分页数据，包含总数、当前页数据等信息
     */
    @GetMapping
    @PreAuthorize("hasAuthority('system:manage')")
    public ResponseEntity<ApiResponse<PageResponseDTO<DepartmentResponseDTO>>> getDepartmentPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isActive) {
        try {
            // 调用服务层方法获取分页数据
            PageResponseDTO<DepartmentResponseDTO> result = departmentService.getDepartmentPage(page, size, keyword, isActive);
            return ResponseEntity.ok(ApiResponse.success("查询成功", result));
        } catch (Exception e) {
            // 处理查询异常
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 检查部门名称是否可用
     * <p>
     * 验证部门名称在同级部门中是否唯一，用于表单提交前的实时校验
     * 需要系统管理权限
     *
     * @param name 待检查的部门名称
     * @param parentId 父部门ID，可选参数，存在时检查在指定父部门下的唯一性
     * @param excludeId 排除的部门ID，可选参数，用于编辑时排除自身
     * @return 名称可用性校验结果，包含是否可用、提示信息等
     */
    @GetMapping("/check-name")
    @PreAuthorize("hasAuthority('system:manage')")
    public ResponseEntity<ApiResponse<ValidationResponseDTO>> checkDepartmentNameAvailable(
            @RequestParam String name,
            @RequestParam(required = false) Long parentId,
            @RequestParam(required = false) Long excludeId) {
        try {
            // 调用服务层方法检查名称是否可用
            ValidationResponseDTO result = departmentService.checkNameAvailable(name, parentId, excludeId);
            return ResponseEntity.ok(ApiResponse.success("检查完成", result));
        } catch (Exception e) {
            // 处理检查异常
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 创建部门
     * <p>
     * 创建新的部门信息，支持设置部门名称、描述、父部门等
     * 需要系统管理权限
     *
     * @param createDTO 部门创建信息，包含名称、描述、父部门ID等
     * @return 创建成功的部门信息
     * @throws RuntimeException 如果部门名称已存在或其他业务规则校验失败
     */
    @PostMapping
    @PreAuthorize("hasAuthority('system:manage')")
    public ResponseEntity<ApiResponse<DepartmentResponseDTO>> createDepartment(
            @Valid @RequestBody DepartmentCreateDTO createDTO) {
        try {
            // 创建新部门
            DepartmentResponseDTO department = departmentService.createDepartment(createDTO);
            return ResponseEntity.ok(ApiResponse.success("部门创建成功", department));
        } catch (RuntimeException e) {
            // 处理业务异常，如名称重复等
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            // 处理系统异常
            return ResponseEntity.status(500)
                .body(ApiResponse.error(500, "系统内部错误"));
        }
    }

    /**
     * 更新部门信息
     * <p>
     * 更新指定部门的基本信息，包括名称、描述等
     * 需要系统管理权限
     *
     * @param id 部门ID，标识要更新的部门
     * @param updateDTO 部门更新信息，包含要更新的字段和值
     * @return 更新后的部门信息
     * @throws RuntimeException 如果部门不存在、名称已被占用或其他业务规则校验失败
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:manage')")
    public ResponseEntity<ApiResponse<DepartmentResponseDTO>> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentUpdateDTO updateDTO) {
        try {
            // 更新部门信息
            DepartmentResponseDTO department = departmentService.updateDepartment(id, updateDTO);
            return ResponseEntity.ok(ApiResponse.success("部门更新成功", department));
        } catch (RuntimeException e) {
            // 处理业务异常
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            // 处理系统异常
            return ResponseEntity.status(500)
                .body(ApiResponse.error(500, "系统内部错误"));
        }
    }

    /**
     * 删除部门
     * <p>
     * 删除指定的部门，如果部门下有子部门或用户则不允许删除
     * 需要系统管理权限
     *
     * @param id 要删除的部门ID
     * @return 删除操作的结果
     * @throws RuntimeException 如果部门不存在或者存在子部门/用户无法删除
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:manage')")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(
            @PathVariable Long id) {
        try {
            // 删除指定部门
            departmentService.deleteDepartment(id);
            return ResponseEntity.ok(ApiResponse.success("部门删除成功"));
        } catch (RuntimeException e) {
            // 处理业务异常，如部门不存在、有子部门等
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            // 处理系统异常
            return ResponseEntity.status(500)
                .body(ApiResponse.error(500, "系统内部错误"));
        }
    }

}
