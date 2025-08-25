package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.pojo.DTO.common.PageResponseDTO;
import com.example.demo.pojo.DTO.management.DepartmentInfoResponseDTO;
import com.example.demo.pojo.DTO.management.DepartmentInfoUpdateDTO;
import com.example.demo.pojo.DTO.management.PersonnelInfoCreateDTO;
import com.example.demo.pojo.DTO.management.PersonnelInfoResponseDTO;
import com.example.demo.pojo.DTO.management.PersonnelInfoUpdateDTO;
import com.example.demo.service.ManagementInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
/**
 * 管理信息服务控制�? * 
 * 该控制器处理工程运行管理中的基础信息档案管理，主要包括：
 * - 人员信息档案的CRUD操作
 * - 部门信息档案的查询操�? * - 工号查重等辅助功�? * 
 * 所有接口都需�?business:manage'权限
 */
@RestController
@RequestMapping("/api/management-info")
 // 统一权限控制
@Api(tags = "管理信息服务", description = "人员和部门信息档案的CRUD操作")
public class ManagementInfoController {

    /**
     * 管理信息服务，处理人员和部门信息档案
     */
    @Autowired
    private ManagementInfoService managementInfoService;

    /**
     * 分页查询人员信息列表
     * 
     * @param name 人员姓名过滤（可选）
     * @param departmentId 部门ID过滤（可选）
     * @param positionId 岗位ID过滤（可选）
     * @param page 页码（默认为1）
     * @param size 每页大小，默认为10
     * @return 分页的人员信息列表
     */
    @GetMapping("/personnel")
    @ApiOperation(value = "分页查询人员信息列表", notes = "支持按姓名、部门、岗位等条件筛选")
    public ResponseEntity<ApiResponse<PageResponseDTO<PersonnelInfoResponseDTO>>> getPersonnelList(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long positionId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            PageResponseDTO<PersonnelInfoResponseDTO> result = managementInfoService.getPersonnelList(
                page, size, name, departmentId, null, positionId);
            return ResponseEntity.ok(ApiResponse.success("查询成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, "查询失败: " + e.getMessage()));
        }
    }

    /**
     * 创建人员信息
     * 
     * @param createDTO 人员信息创建数据传输对象
     * @return 创建成功的人员信息
     */
    @PostMapping("/personnel")
    @ApiOperation(value = "创建人员信息", notes = "创建新的人员信息，需提供必要的个人信息")
    public ResponseEntity<ApiResponse<PersonnelInfoResponseDTO>> createPersonnel(
            @Valid @RequestBody PersonnelInfoCreateDTO createDTO) {
        try {
            PersonnelInfoResponseDTO personnel = managementInfoService.createPersonnel(createDTO);
            return ResponseEntity.ok(ApiResponse.success("人员信息创建成功", personnel));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error(500, "系统内部错误"));
        }
    }

    /**
     * 更新人员信息
     * 
     * @param id 待更新的人员ID
     * @param updateDTO 人员信息更新数据传输对象
     * @return 更新后的人员信息
     */
    @PutMapping("/personnel/{id}")
    @ApiOperation(value = "更新人员信息", notes = "根据ID更新人员的基本信息")
    public ResponseEntity<ApiResponse<PersonnelInfoResponseDTO>> updatePersonnel(
            @PathVariable Long id,
            @Valid @RequestBody PersonnelInfoUpdateDTO updateDTO) {
        try {
            PersonnelInfoResponseDTO personnel = managementInfoService.updatePersonnel(id, updateDTO);
            return ResponseEntity.ok(ApiResponse.success("人员信息更新成功", personnel));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error(500, "系统内部错误"));
        }
    }

    /**
     * 删除人员信息
     * 
     * @param id 待删除的人员ID
     * @return 删除操作结果
     */
    @DeleteMapping("/personnel/{id}")
    @ApiOperation(value = "删除人员信息", notes = "根据ID删除人员信息（软删除）")
    public ResponseEntity<ApiResponse<Void>> deletePersonnel(
            @PathVariable Long id) {
        try {
            managementInfoService.deletePersonnel(id);
            return ResponseEntity.ok(ApiResponse.success("人员信息删除成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error(500, "系统内部错误"));
        }
    }

    /**
     * 批量删除人员信息
     * 
     * @param ids 待删除的人员ID列表
     * @return 批量删除操作结果
     */
    @DeleteMapping("/personnel/batch")
    @ApiOperation(value = "批量删除人员信息", notes = "根据ID列表批量删除多个人员信息（软删除）")
    public ResponseEntity<ApiResponse<Void>> batchDeletePersonnel(
            @RequestBody List<Long> ids) {
        try {
            // 批量删除逻辑
            if (ids == null || ids.isEmpty()) {
                throw new RuntimeException("删除ID列表不能为空");
            }
            for (Long id : ids) {
                managementInfoService.deletePersonnel(id);
            }
            return ResponseEntity.ok(ApiResponse.success("批量删除成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error(500, "系统内部错误"));
        }
    }
    
    /**
     * 获取部门树形结构
     * <p>
     * 返回完整的部门层级结构，用于展示组织架构
     * @return 部门树形结构，每个节点包含其子部门
     */
    @GetMapping("/departments/tree")
    @ApiOperation(value = "获取部门树形结构", notes = "返回完整的部门层级结构，用于展示组织架构")
    public ResponseEntity<ApiResponse<List<DepartmentInfoResponseDTO>>> getDepartmentTree() {
        try {
            // 获取部门的树形结构
            List<DepartmentInfoResponseDTO> tree = managementInfoService.getDepartmentTree();
            return ResponseEntity.ok(ApiResponse.success("查询成功", tree));
        } catch (Exception e) {
            // 处理异常
            return ResponseEntity.status(500)
                .body(ApiResponse.error(500, "系统内部错误"));
        }
    }
    
    /**
     * 更新部门信息
     * 
     * @param id 部门ID
     * @param updateDTO 部门更新DTO
     * @return 更新后的部门信息
     */
    @PutMapping("/departments/{id}")
    @ApiOperation(value = "更新部门信息", notes = "根据ID更新部门的基本信息")
    public ResponseEntity<ApiResponse<DepartmentInfoResponseDTO>> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentInfoUpdateDTO updateDTO) {
        try {
            DepartmentInfoResponseDTO department = managementInfoService.updateDepartment(id, updateDTO);
            return ResponseEntity.ok(ApiResponse.success("部门信息更新成功", department));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error(500, "系统内部错误"));
        }
    }
}
