package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.common.PageResult;
import com.example.demo.pojo.DTO.common.FacilityQueryDTO;
import com.example.demo.service.EngineeringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 工程信息服务总控制器
 * <p>
 * 提供统一的设施管理接口，支持所有设施类型的CRUD操作：
 * - 设施类型枚举查询
 * - 通用设施管理（分页查询、详情、创建、更新、删除）
 * - 支持动态设施类型处理，减少代码重复
 */
@RestController
@RequestMapping("/api/engineering-service")
@RequiredArgsConstructor
@Tag(name = "工程信息服务", description = "统一的设施管理服务，支持所有设施类型的CRUD操作")
public class EngineeringServiceController {

    // 注入工程服务（统一管理所有设施服务）
    private final EngineeringService engineeringService;

    /**
     * 获取所有设施类型枚举
     * <p>
     * 返回系统中所有可用的设施类型列表，与实际设施实体对应
     *
     * @return 设施类型枚举列表
     */
    @GetMapping("/facility-types")
    @Operation(summary = "获取设施类型枚举", description = "返回系统中所有可用的设施类型列表，包含类型值、标签、描述、实体类和API路径信息")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getFacilityTypes() {
        List<Map<String, Object>> facilityTypes = engineeringService.getFacilityTypes();
        return ResponseEntity.ok(ApiResponse.success("查询成功", facilityTypes));
    }

    /**
     * 获取设施类型映射信息
     * <p>
     * 返回设施类型的键值对映射，用于快速查询和显示
     *
     * @return 设施类型映射
     */
    @GetMapping("/facility-type-map")
    @Operation(summary = "获取设施类型映射", description = "返回设施类型的键值对映射，用于快速查询和显示")
    public ResponseEntity<ApiResponse<Map<String, String>>> getFacilityTypeMap() {
        Map<String, String> facilityTypeMap = engineeringService.getFacilityTypeMap();
        return ResponseEntity.ok(ApiResponse.success("查询成功", facilityTypeMap));
    }

    // =================================== 通用设施管理接口 ===================================

    /**
     * 通用设施分页查询
     *
     * @param facilityType     设施类型（pumping-stations, water-plants, reservoirs等）
     * @param facilityQueryDTO 查询参数DTO
     * @return 分页查询结果
     */
    @GetMapping("/{facilityType}")
    @Operation(summary = "通用设施分页查询", description = "根据设施类型进行分页查询，支持关键词搜索")
    public ResponseEntity<ApiResponse<PageResult<?>>> getFacilityPage(@PathVariable String facilityType, @Valid FacilityQueryDTO facilityQueryDTO) {
        PageResult<?> result = engineeringService.getFacilityPage(facilityType, facilityQueryDTO);
        return ResponseEntity.ok(ApiResponse.success("查询成功", result));
    }

    /**
     * 根据ID查询设施详情
     *
     * @param facilityType 设施类型
     * @param id           设施ID
     * @return 设施详情
     */
    @GetMapping("/{facilityType}/{id}")
    @Operation(summary = "查询设施详情", description = "根据设施类型和ID查询设施详细信息")
    public ResponseEntity<ApiResponse<?>> getFacilityById(@Parameter(description = "设施类型", example = "pumping-stations") @PathVariable String facilityType, @PathVariable Long id) {
        Object result = engineeringService.getFacilityById(facilityType, id);
        return ResponseEntity.ok(ApiResponse.success("查询成功", result));
    }

    /**
     * 删除设施
     *
     * @param facilityType 设施类型
     * @param id           设施ID
     * @return 删除结果
     */
    @DeleteMapping("/{facilityType}/{id}")
    @Operation(summary = "删除设施", description = "根据设施类型和ID删除设施（软删除）")
    public ResponseEntity<ApiResponse<Void>> deleteFacility(@Parameter(description = "设施类型", example = "pumping-stations") @PathVariable String facilityType, @PathVariable Long id) {
        engineeringService.deleteFacility(facilityType, id);
        return ResponseEntity.ok(ApiResponse.success("删除成功"));
    }

    /**
     * 获取可用设施列表（用于下拉选择）
     *
     * @param facilityType 设施类型
     * @return 可用设施列表
     */
    @GetMapping("/{facilityType}/available")
    @Operation(summary = "获取可用设施列表", description = "获取指定类型的所有可用设施，通常用于下拉选择")
    public ResponseEntity<ApiResponse<List<?>>> getAvailableFacilities(@Parameter(description = "设施类型", example = "pumping-stations") @PathVariable String facilityType) {
        List<?> result = engineeringService.getAvailableFacilities(facilityType);
        return ResponseEntity.ok(ApiResponse.success("查询成功", result));
    }

    /**
     * 统计设施总数
     *
     * @param facilityType 设施类型
     * @return 设施总数
     */
    @GetMapping("/{facilityType}/count")
    @Operation(summary = "统计设施总数", description = "统计指定类型设施的总数")
    public ResponseEntity<ApiResponse<Long>> countFacilities(@Parameter(description = "设施类型", example = "pumping-stations") @PathVariable String facilityType) {
        long result = engineeringService.countFacilities(facilityType);
        return ResponseEntity.ok(ApiResponse.success("统计成功", result));
    }

    /**
     * 创建设施
     *
     * @param facilityType 设施类型
     * @param requestBody  请求体，包含设施数据
     * @return 创建结果
     */
    @PostMapping("/{facilityType}")
    @Operation(summary = "创建设施", description = "根据设施类型创建新的设施")
    public ResponseEntity<ApiResponse<?>> createFacility(@Parameter(description = "设施类型", example = "pumping-stations") @PathVariable String facilityType, @RequestBody Map<String, Object> requestBody) {
        Object result = engineeringService.createFacility(facilityType, requestBody);
        return ResponseEntity.ok(ApiResponse.success("创建成功", result));
    }

    /**
     * 更新设施
     *
     * @param facilityType 设施类型
     * @param id           设施ID
     * @param requestBody  请求体，包含设施数据
     * @return 更新结果
     */
    @PutMapping("/{facilityType}/{id}")
    @Operation(summary = "更新设施", description = "根据设施类型和ID更新设施信息")
    public ResponseEntity<ApiResponse<?>> updateFacility(@Parameter(description = "设施类型", example = "pumping-stations") @PathVariable String facilityType, @PathVariable Long id, @RequestBody Map<String, Object> requestBody) {
        Object result = engineeringService.updateFacility(facilityType, id, requestBody);
        return ResponseEntity.ok(ApiResponse.success("更新成功", result));
    }




} 
