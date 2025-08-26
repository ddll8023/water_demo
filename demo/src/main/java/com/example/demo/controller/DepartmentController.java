package com.example.demo.controller;

import com.example.demo.pojo.DTO.system.DepartmentCreateDTO;
import com.example.demo.pojo.DTO.system.DepartmentNameCheckDTO;
import com.example.demo.pojo.DTO.system.DepartmentQueryDTO;
import com.example.demo.pojo.DTO.system.DepartmentUpdateDTO;
import com.example.demo.pojo.VO.DepartmentVO;
import com.example.demo.common.ApiResponse;
import com.example.demo.common.PageResult;
import com.example.demo.service.DepartmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

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
@Tag(name = "部门管理", description = "部门相关的增删改查操作")
@Slf4j
@RequiredArgsConstructor
public class DepartmentController {
    
    private final DepartmentService departmentService;

    /**
     * 分页查询部门列表
     * 
     * @param departmentQueryDTO 查询条件，包含分页、搜索、筛选等参数
     * @return 部门分页数据，包含总数、当前页数据等信息
     */
    @GetMapping("")
    @Operation(summary = "获取部门分页数据", description = "支持按名称关键词模糊搜索，可以筛选启用/禁用状态、父部门、区域等")
    public ResponseEntity<ApiResponse<PageResult<DepartmentVO>>> getDepartmentPage(
            @Valid DepartmentQueryDTO departmentQueryDTO) {
        PageResult<DepartmentVO> result = departmentService.getDepartmentPage(departmentQueryDTO);
        return ResponseEntity.ok(ApiResponse.success("查询成功", result));
    }

    /**
    * 检查部门名称是否可用
     * <p>
     * 验证部门名称在同级部门中是否唯一，用于表单提交前的实时校验
     * 需要系统管理权限
     * @param departmentNameCheckDTO 部门名称检查请求参数，包含名称、父部门ID、排除的部门ID等
     * @return 名称可用性校验结果，包含是否可用、提示信息等
     */
    @GetMapping("/check-name")
    @Operation(summary = "检查部门名称是否可用", description = "验证部门名称在同级部门中是否唯一，用于表单提交前的实时校验")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkDepartmentNameAvailable(
            @Valid DepartmentNameCheckDTO departmentNameCheckDTO) {
        Map<String, Object> result = departmentService.checkNameAvailable(departmentNameCheckDTO);
        return ResponseEntity.ok(ApiResponse.success("检查完成", result));
    }

    /**
     * 创建部门
     * <p>
     * 创建新的部门信息，支持设置部门名称、描述、父部门
     * 需要系统管理权限
     * @param departmentCreateDTO 部门创建信息，包含名称、描述、父部门ID
     * @return 创建操作的结果
     */
    @PostMapping("")
    @Operation(summary = "创建部门", description = "创建新的部门信息，支持设置部门名称、描述、父部门")
    public ResponseEntity<ApiResponse<Void>> createDepartment(
            @Valid @RequestBody DepartmentCreateDTO departmentCreateDTO) {
        departmentService.createDepartment(departmentCreateDTO);
        return ResponseEntity.ok(ApiResponse.success("部门创建成功"));
    }

    /**
     * 更新部门信息
     * <p>
     * 更新指定部门的基本信息，包括名称、描述等
     * 需要系统管理权限
     * @param id 部门ID，标识要更新的部门
     * @param departmentUpdateDTO 部门更新信息，包含要更新的字段和值
     * @return 更新操作的结果
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新部门信息", description = "更新指定部门的基本信息，包括名称、描述等")
    public ResponseEntity<ApiResponse<Void>> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentUpdateDTO departmentUpdateDTO) {
        departmentService.updateDepartment(id, departmentUpdateDTO);
        return ResponseEntity.ok(ApiResponse.success("部门更新成功"));
    }

    /**
     * 删除部门
     * <p>
     * 删除指定的部门，如果部门下有子部门或用户则不允许删除
     * 需要系统管理权限
     * @param id 要删除的部门ID
     * @return 删除操作的结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除部门", description = "删除指定的部门，如果部门下有子部门或用户则不允许删除")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(
            @PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok(ApiResponse.success("部门删除成功"));
    }

}
