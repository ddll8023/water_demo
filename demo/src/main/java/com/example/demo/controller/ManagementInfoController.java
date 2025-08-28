package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.common.PageResult;
import com.example.demo.pojo.DTO.management.DepartmentInfoUpdateDTO;
import com.example.demo.pojo.DTO.management.PersonnelInfoCreateDTO;
import com.example.demo.pojo.DTO.management.PersonnelInfoQueryDTO;
import com.example.demo.pojo.DTO.management.PersonnelInfoUpdateDTO;
import com.example.demo.pojo.VO.DepartmentTreeVO;
import com.example.demo.pojo.VO.PersonnelInfoVO;
import com.example.demo.service.ManagementInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 管理信息服务控制器
 * 该控制器处理工程运行管理中的基础信息档案管理，主要包括：
 * - 人员信息档案的CRUD操作
 * - 部门信息档案的查询操作
 * - 工号查重等辅助功能
 * 所有接口都需business:manage权限
 */
@RestController
@RequestMapping("/api/management-info")
@RequiredArgsConstructor
@Tag(name = "管理信息服务", description = "人员和部门信息档案的CRUD操作")
public class ManagementInfoController {

    private final ManagementInfoService managementInfoService;

    /**
     * 分页查询人员信息列表
     *
     * @param personnelInfoQueryDTO 查询参数
     * @return 分页的人员信息列表
     */
    @GetMapping("/personnel")
    @Operation(summary = "分页查询人员信息列表", description = "支持按姓名、部门、岗位等条件筛选")
    public ResponseEntity<ApiResponse<PageResult<PersonnelInfoVO>>> getPersonnelList(@Valid PersonnelInfoQueryDTO personnelInfoQueryDTO) {
        PageResult<PersonnelInfoVO> result = managementInfoService.getPersonnelList(personnelInfoQueryDTO);
        return ResponseEntity.ok(ApiResponse.success("查询成功", result));
    }

    /**
     * 创建人员信息
     *
     * @param personnelInfoCreateDTO 人员信息创建数据传输对象
     * @return 创建成功的人员信息
     */
    @PostMapping("/personnel")
    @Operation(summary = "创建人员信息", description = "创建新的人员信息，需提供必要的个人信息")
    public ResponseEntity<ApiResponse<Void>> createPersonnel(@Valid @RequestBody PersonnelInfoCreateDTO personnelInfoCreateDTO) {
        managementInfoService.createPersonnel(personnelInfoCreateDTO);
        return ResponseEntity.ok(ApiResponse.success("人员信息创建成功"));
    }

    /**
     * 更新人员信息
     *
     * @param id                     待更新的人员ID
     * @param personnelInfoUpdateDTO 人员信息更新数据传输对象
     * @return 更新后的人员信息
     */
    @PutMapping("/personnel/{id}")
    @Operation(summary = "更新人员信息", description = "根据ID更新人员的基本信息")
    public ResponseEntity<ApiResponse<Void>> updatePersonnel(@PathVariable Long id, @Valid @RequestBody PersonnelInfoUpdateDTO personnelInfoUpdateDTO) {
        managementInfoService.updatePersonnel(id, personnelInfoUpdateDTO);
        return ResponseEntity.ok(ApiResponse.success("人员信息更新成功"));
    }

    /**
     * 删除人员信息
     *
     * @param id 待删除的人员ID
     * @return 删除操作结果
     */
    @DeleteMapping("/personnel/{id}")
    @Operation(summary = "删除人员信息", description = "根据ID删除人员信息（软删除）")
    public ResponseEntity<ApiResponse<Void>> deletePersonnel(@PathVariable Long id) {
        managementInfoService.deletePersonnel(id);
        return ResponseEntity.ok(ApiResponse.success("人员信息删除成功"));
    }

    /**
     * 批量删除人员信息
     *
     * @param ids 待删除的人员ID列表
     * @return 批量删除操作结果
     */
    @DeleteMapping("/personnel/batch")
    @Operation(summary = "批量删除人员信息", description = "根据ID列表批量删除多个人员信息（软删除）")
    public ResponseEntity<ApiResponse<Void>> batchDeletePersonnel(@RequestBody List<Long> ids) {
        managementInfoService.batchDeletePersonnel(ids);
        return ResponseEntity.ok(ApiResponse.success("批量删除成功"));
    }

    /**
     * 获取部门树形结构
     * <p>
     * 返回完整的部门层级结构，用于展示组织架构
     *
     * @return 部门树形结构，每个节点包含其子部门
     */
    @GetMapping("/departments/tree")
    @Operation(summary = "获取部门树形结构", description = "返回完整的部门层级结构，用于展示组织架构")
    public ResponseEntity<ApiResponse<List<DepartmentTreeVO>>> getDepartmentTree() {

        List<DepartmentTreeVO> tree = managementInfoService.getDepartmentTree();
        return ResponseEntity.ok(ApiResponse.success("查询成功", tree));

    }

    /**
     * 更新部门信息
     *
     * @param id                      部门ID
     * @param departmentInfoUpdateDTO 部门更新DTO
     * @return 更新后的部门信息
     */
    @PutMapping("/departments/{id}")
    @Operation(summary = "更新部门信息", description = "根据ID更新部门的基本信息")
    public ResponseEntity<ApiResponse<Void>> updateDepartment(@PathVariable Long id, @Valid @RequestBody DepartmentInfoUpdateDTO departmentInfoUpdateDTO) {
        managementInfoService.updateDepartment(id, departmentInfoUpdateDTO);
        return ResponseEntity.ok(ApiResponse.success("部门信息更新成功"));

    }
}
