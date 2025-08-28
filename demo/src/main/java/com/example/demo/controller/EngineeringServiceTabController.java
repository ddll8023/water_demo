package com.example.demo.controller;

import com.example.demo.pojo.DTO.system.EngineeringServiceTabCreateDTO;
import com.example.demo.pojo.DTO.system.EngineeringServiceTabQueryDTO;
import com.example.demo.pojo.DTO.system.EngineeringServiceTabUpdateDTO;
import com.example.demo.pojo.VO.EngineeringServiceTabVO;
import com.example.demo.common.ApiResponse;
import com.example.demo.common.PageResult;
import com.example.demo.service.EngineeringServiceTabService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 工程信息服务Tab配置控制器
 * <p>
 * 提供工程信息服务Tab配置相关的增删改查操作，包括：
 * - Tab配置列表的分页查询
 * - Tab配置详情查询
 * - 创建、更新、删除Tab配置
 * - Tab配置显示状态管理
 * - Tab配置排序管理
 * - 获取可见Tab配置列表
 * <p>
 * 所有API均需要相应权限，主要面向系统管理员使用
 */
@RestController
@RequestMapping("/api/engineering-service-tabs")
@Tag(name = "工程信息服务Tab配置管理", description = "管理工程信息服务页面的Tab显示配置")
@Slf4j
@RequiredArgsConstructor
public class EngineeringServiceTabController {

    private final EngineeringServiceTabService tabService;

    /**
     * 分页查询Tab配置列表
     * 
     * @param queryDTO 查询条件，包含分页、搜索、筛选等参数
     * @return Tab配置分页数据，包含总数、当前页数据等信息
     */
    @GetMapping("")
    @Operation(summary = "获取Tab配置分页数据", description = "支持按Tab名称模糊搜索，可以筛选显示/隐藏状态等")
    public ResponseEntity<ApiResponse<PageResult<EngineeringServiceTabVO>>> getTabConfigurationPage(
            @Valid EngineeringServiceTabQueryDTO queryDTO) {
        PageResult<EngineeringServiceTabVO> result = tabService.getTabConfigurations(queryDTO);
        return ResponseEntity.ok(ApiResponse.success("查询成功", result));
    }

    /**
     * 获取所有可见的Tab配置列表
     * <p>
     * 用于工程信息服务页面动态加载Tab配置
     * 按排序顺序升序返回
     * @return 可见的Tab配置列表
     */
    @GetMapping("/visible")
    @Operation(summary = "获取可见Tab配置列表", description = "用于工程信息服务页面动态加载Tab配置，按排序顺序返回")
    public ResponseEntity<ApiResponse<List<EngineeringServiceTabVO>>> getVisibleTabConfigurations() {
        List<EngineeringServiceTabVO> result = tabService.getVisibleTabConfigurations();
        return ResponseEntity.ok(ApiResponse.success("查询成功", result));
    }

    /**
     * 创建Tab配置
     * <p>
     * 创建新的Tab配置信息，支持设置Tab标识、名称、图标、排序等
     * 需要系统管理权限
     * @param createDTO Tab配置创建信息，包含Tab标识、名称、图标等
     * @return 创建操作的结果
     */
    @PostMapping("")
    @Operation(summary = "创建Tab配置", description = "创建新的Tab配置信息，支持设置Tab标识、名称、图标、排序等")
    public ResponseEntity<ApiResponse<Void>> createTabConfiguration(
            @Valid @RequestBody EngineeringServiceTabCreateDTO createDTO) {
        tabService.createTabConfiguration(createDTO);
        return ResponseEntity.ok(ApiResponse.success("Tab配置创建成功"));
    }

    /**
     * 更新Tab配置信息
     * <p>
     * 更新指定Tab配置的基本信息，包括名称、图标、排序等
     * 需要系统管理权限
     * @param id Tab配置ID，标识要更新的Tab配置
     * @param updateDTO Tab配置更新信息，包含要更新的字段和值
     * @return 更新操作的结果
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新Tab配置信息", description = "更新指定Tab配置的基本信息，包括名称、图标、排序等")
    public ResponseEntity<ApiResponse<Void>> updateTabConfiguration(
            @PathVariable Long id,
            @Valid @RequestBody EngineeringServiceTabUpdateDTO updateDTO) {
        tabService.updateTabConfiguration(id, updateDTO);
        return ResponseEntity.ok(ApiResponse.success("Tab配置更新成功"));
    }

    /**
     * 删除Tab配置
     * <p>
     * 删除指定的Tab配置（软删除）
     * 需要系统管理权限
     * @param id 要删除的Tab配置ID
     * @return 删除操作的结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除Tab配置", description = "删除指定的Tab配置（软删除）")
    public ResponseEntity<ApiResponse<Void>> deleteTabConfiguration(
            @PathVariable Long id) {
        tabService.deleteTabConfiguration(id);
        return ResponseEntity.ok(ApiResponse.success("Tab配置删除成功"));
    }

    /**
     * 批量更新Tab配置排序顺序
     * <p>
     * 批量更新多个Tab配置的排序顺序，用于拖拽排序功能
     * 需要系统管理权限
     * @param updateDTOList Tab配置更新列表，包含ID和新的排序顺序
     * @return 批量更新操作的结果
     */
    @PutMapping("/batch-sort")
    @Operation(summary = "批量更新Tab配置排序", description = "批量更新多个Tab配置的排序顺序，用于拖拽排序功能")
    public ResponseEntity<ApiResponse<Void>> batchUpdateSortOrder(
            @Valid @RequestBody List<EngineeringServiceTabUpdateDTO> updateDTOList) {
        tabService.batchUpdateSortOrder(updateDTOList);
        return ResponseEntity.ok(ApiResponse.success("Tab配置排序更新成功"));
    }

    /**
     * 更新Tab配置显示状态
     * <p>
     * 更新指定Tab配置的显示/隐藏状态
     * 需要系统管理权限
     * @param tabKey Tab标识键
     * @param isVisible 是否显示（1-显示，0-隐藏）
     * @return 更新操作的结果
     */
    @PutMapping("/{tabKey}/visibility")
    @Operation(summary = "更新Tab配置显示状态", description = "更新指定Tab配置的显示/隐藏状态")
    public ResponseEntity<ApiResponse<Void>> updateTabVisibility(
            @PathVariable String tabKey,
            @RequestParam String isVisible) {
        tabService.updateTabVisibility(tabKey, isVisible);
        return ResponseEntity.ok(ApiResponse.success("Tab配置显示状态更新成功"));
    }

} 