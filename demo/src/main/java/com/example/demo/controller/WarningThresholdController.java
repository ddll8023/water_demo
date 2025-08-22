package com.example.demo.controller;

import com.example.demo.dto.warning.WarningThresholdCreateDTO;
import com.example.demo.dto.warning.WarningThresholdResponseDTO;
import com.example.demo.dto.warning.WarningThresholdUpdateDTO;
import com.example.demo.common.ApiResponse;
import com.example.demo.dto.common.PageResponseDTO;
import com.example.demo.service.WarningThresholdService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 预警指标设定管理控制器
 * <p>
 * 该控制器负责处理预警指标的管理功能，包括：
 * - 预警指标的查询（分页、条件过滤）
 * - 预警指标的创建、修改和删除
 * - 批量删除预警指标
 * - 获取启用的预警指标
 * - 预警指标统计数据
 * - 重复性检查
 * </p>
 * 
 * @author system
 */
@RestController
@RequestMapping("/api/warning/thresholds")
@RequiredArgsConstructor
@Api(tags = "预警指标设定管理", description = "预警指标的CRUD操作及相关统计功能")
public class WarningThresholdController {

    /**
     * 预警指标服务
     */
    private final WarningThresholdService warningThresholdService;

    /**
     * 分页查询预警指标列表
     * 
     * @param page 页码，默认为1
     * @param size 每页大小，默认为10
     * @param keyword 关键词搜索（测点名称）
     * @param stationName 测点名称
     * @param monitoringItem 监测项
     * @param sort 排序字段，格式如：created_at,desc
     * @return 预警指标分页数据
     */
    @GetMapping
    @PreAuthorize("hasAuthority('business:operate')")
    @ApiOperation(value = "分页查询预警指标列表", notes = "根据条件分页查询预警指标信息")
    public ResponseEntity<ApiResponse<PageResponseDTO<WarningThresholdResponseDTO>>> getWarningThresholds(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String stationName,
            @RequestParam(required = false) String monitoringItem,
            @RequestParam(required = false) String sort) {

        PageResponseDTO<WarningThresholdResponseDTO> result = warningThresholdService.getWarningThresholds(
            page, size, keyword, stationName, monitoringItem, sort);

        return ResponseEntity.ok(ApiResponse.success("查询成功", result));
    }

    /**
     * 根据ID查询预警指标详情
     * 
     * @param id 预警指标ID
     * @return 预警指标详细信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('business:operate')")
    @ApiOperation(value = "查询预警指标详情", notes = "根据ID查询预警指标详细信息")
    public ResponseEntity<ApiResponse<WarningThresholdResponseDTO>> getWarningThresholdById(
            @PathVariable Long id) {

        WarningThresholdResponseDTO result = warningThresholdService.getWarningThresholdById(id);
        return ResponseEntity.ok(ApiResponse.success("查询成功", result));
    }

    /**
     * 创建预警指标
     * 
     * @param createDTO 预警指标创建信息
     * @return 创建成功的预警指标信息
     */
    @PostMapping
    @PreAuthorize("hasAuthority('business:operate')")
    @ApiOperation(value = "创建预警指标", notes = "创建新的预警指标信息")
    public ResponseEntity<ApiResponse<WarningThresholdResponseDTO>> createWarningThreshold(
            @Valid @RequestBody WarningThresholdCreateDTO createDTO) {
        try {
            WarningThresholdResponseDTO result = warningThresholdService.createWarningThreshold(createDTO);
            return ResponseEntity.ok(ApiResponse.success("预警指标创建成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 更新预警指标
     * 
     * @param id 预警指标ID
     * @param updateDTO 预警指标更新信息
     * @return 更新后的预警指标信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('business:operate')")
    @ApiOperation(value = "更新预警指标", notes = "根据ID更新预警指标信息")
    public ResponseEntity<ApiResponse<WarningThresholdResponseDTO>> updateWarningThreshold(
            @PathVariable Long id,
            @Valid @RequestBody WarningThresholdUpdateDTO updateDTO) {
        try {
            updateDTO.setId(id);
            WarningThresholdResponseDTO result = warningThresholdService.updateWarningThreshold(updateDTO);
            return ResponseEntity.ok(ApiResponse.success("预警指标更新成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 删除预警指标
     * 
     * @param id 预警指标ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('business:operate')")
    @ApiOperation(value = "删除预警指标", notes = "根据ID删除预警指标信息")
    public ResponseEntity<ApiResponse<Void>> deleteWarningThreshold(
            @PathVariable Long id) {
        try {
            warningThresholdService.deleteWarningThreshold(id);
            return ResponseEntity.ok(ApiResponse.success("预警指标删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 获取所有预警指标
     * 
     * @return 预警指标列表
     */
    @GetMapping("/active")
    @PreAuthorize("hasAuthority('business:operate')")
    @ApiOperation(value = "获取所有预警指标", notes = "获取系统中所有可用于选择预警指标列表")
    public ResponseEntity<ApiResponse<List<WarningThresholdResponseDTO>>> getAllWarningThresholds() {
        List<WarningThresholdResponseDTO> result = warningThresholdService.getAllActiveWarningThresholds();
        return ResponseEntity.ok(ApiResponse.success("查询成功", result));
    }

    /**
     * 获取预警指标统计信息
     * <p>
     * 返回预警指标的统计数据，包括：
     * - 指标总数
     * - 启用的指标数量
     * - 按监测项分组的指标数量
     * </p>
     * 
     * @return 预警指标统计信息
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('business:operate')")
    @ApiOperation(value = "获取预警指标统计信息", notes = "获取预警指标的统计数据")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getWarningThresholdStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // 获取基本统计信息
        List<WarningThresholdResponseDTO> allThresholds = warningThresholdService.getAllActiveWarningThresholds();
        statistics.put("totalCount", allThresholds.size());
        statistics.put("activeCount", allThresholds.size());

        // 按监测项分组统计
        Map<String, Long> byMonitoringItem = allThresholds.stream()
            .collect(Collectors.groupingBy(
                WarningThresholdResponseDTO::getMonitoringItem,
                Collectors.counting()
            ));
        statistics.put("byMonitoringItem", byMonitoringItem);

        return ResponseEntity.ok(ApiResponse.success("查询成功", statistics));
    }

    /**
     * 检查测点和监测项组合是否存在
     * <p>
     * 用于验证新增或修改预警指标时，指定的测点和监测项组合是否已存在
     * </p>
     * 
     * @param stationName 测点名称
     * @param monitoringItem 监测项
     * @param excludeId 需要排除的预警指标ID（用于修改场景）
     * @return 包含exists字段的Map，表示是否存在
     */
    @GetMapping("/check-duplicate")
    @PreAuthorize("hasAuthority('business:operate')")
    @ApiOperation(value = "检查测点和监测项组合是否存在", notes = "验证新增或修改预警指标时，指定的测点和监测项组合是否已存在")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkStationAndItemExists(
            @RequestParam String stationName,
            @RequestParam String monitoringItem,
            @RequestParam(required = false) Long excludeId) {

        boolean exists = warningThresholdService.checkStationAndItemExists(stationName, monitoringItem, excludeId);
        Map<String, Boolean> result = new HashMap<>();
        result.put("exists", exists);
        return ResponseEntity.ok(ApiResponse.success("检查完成", result));
    }
}
