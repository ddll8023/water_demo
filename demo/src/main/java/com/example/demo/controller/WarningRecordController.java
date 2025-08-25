package com.example.demo.controller;

import com.example.demo.pojo.DTO.warning.WarningRecordCreateDTO;
import com.example.demo.pojo.DTO.warning.WarningRecordResponseDTO;
import com.example.demo.pojo.DTO.warning.WarningResolveDTO;
import com.example.demo.common.ApiResponse;
import com.example.demo.pojo.DTO.common.PageResponseDTO;
import com.example.demo.service.WarningRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 预警信息记录管理控制器
 * 处理预警记录的CRUD操作及相关统计功能
 * 包括：
 * - 预警记录的分页查询（支持多条件筛选）
 * - 预警记录的创建、解除、删除（单个和批量）
 * - 预警统计信息查询（按等级、趋势等）
 * - 预警地点列表获取（用于前端下拉选择）
 */
@RestController
@RequestMapping("/api/warning/records")
@RequiredArgsConstructor
@Tag(name = "预警信息记录管理", description = "预警记录的CRUD操作及相关统计功能")
public class WarningRecordController {

    /**
     * 预警记录服务
     */
    private final WarningRecordService warningRecordService;

    /**
     * 分页查询预警记录列表
     * 
     * @param page 页码，默认为1
     * @param size 每页大小，默认为10
     * @param warningLocation 预警地点（可选）
     * @param warningType 预警类型（可选）
     * @param warningLevel 预警等级（可选）
     * @param warningStatus 预警状态（可选）
     * @param projectName 所属工程（可选）
     * @param startTime 开始时间（可选），格式为yyyy-MM-dd HH:mm:ss
     * @param endTime 结束时间（可选），格式为yyyy-MM-dd HH:mm:ss
     * @param sort 排序字段，格式如：occurred_at,desc（可选）
     * @return 分页的预警记录响应数组
     */
    @GetMapping
    
    @Operation(summary = "分页查询预警记录列表", description = "根据条件分页查询预警记录信息")
    public ResponseEntity<ApiResponse<PageResponseDTO<WarningRecordResponseDTO>>> getWarningRecords(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String warningLocation,
            @RequestParam(required = false) String warningType,
            @RequestParam(required = false) String warningLevel,
            @RequestParam(required = false) String warningStatus,
            @RequestParam(required = false) String projectName,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(required = false) String sort) {

        PageResponseDTO<WarningRecordResponseDTO> result = warningRecordService.getWarningRecords(
            page, size, warningLocation, warningType, warningLevel, warningStatus,
            projectName, startTime, endTime, sort);

        return ResponseEntity.ok(ApiResponse.success("查询成功", result));
    }

    /**
     * 创建预警记录
     * 
     * @param createDTO 预警记录创建信息
     * @return 创建成功的预警记录信息
     */
    @PostMapping
    
    @Operation(summary = "创建预警记录", description = "创建新的预警记录信息")
    public ResponseEntity<ApiResponse<WarningRecordResponseDTO>> createWarningRecord(
            @Valid @RequestBody WarningRecordCreateDTO createDTO) {
        
        // 后端验证：接收到的数据
        System.out.println("后端接收验证:");
        System.out.println("  warningLocation: '" + createDTO.getWarningLocation() + "'");
        System.out.println("  warningType: '" + createDTO.getWarningType() + "'");
        System.out.println("  warningLevel: '" + createDTO.getWarningLevel() + "'");
        System.out.println("  warningContent: '" + createDTO.getWarningContent() + "'");
        
        try {
            WarningRecordResponseDTO result = warningRecordService.createWarningRecord(createDTO);
            return ResponseEntity.ok(ApiResponse.success("预警记录创建成功", result));
        } catch (Exception e) {
            System.out.println("创建异常: " + e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 解除预警
     * 
     * @param id 预警记录ID
     * @param resolveDTO 预警解除信息
     * @return 解除结果
     */
    @PutMapping("/{id}/resolve")
    
    @Operation(summary = "解除预警", description = "根据ID解除预警记录")
    public ResponseEntity<ApiResponse<Void>> resolveWarning(
            @PathVariable Long id,
            @Valid @RequestBody WarningResolveDTO resolveDTO) {
        try {
            resolveDTO.setId(id);
            warningRecordService.resolveWarning(resolveDTO);
            return ResponseEntity.ok(ApiResponse.success("预警解除成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 删除单个预警记录
     * 
     * @param id 预警记录ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    
    @Operation(summary = "删除预警记录", description = "根据ID删除预警记录")
    public ResponseEntity<ApiResponse<Void>> deleteWarningRecord(
            @PathVariable Long id) {
        try {
            warningRecordService.deleteWarningRecord(id);
            return ResponseEntity.ok(ApiResponse.success("预警记录删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 获取预警统计信息
     * 返回预警的整体统计数据
     * 
     * @return 预警统计信息
     */
    @GetMapping("/statistics")
    
    @Operation(summary = "获取预警统计信息", description = "获取预警的整体统计数据")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getWarningStatistics() {
        List<Map<String, Object>> result = warningRecordService.getWarningStatistics();
        return ResponseEntity.ok(ApiResponse.success("查询成功", result));
    }

    /**
     * 获取各等级预警数量统计
     * 按预警等级分类统计预警数量
     * 
     * @return 各等级预警数量统计信息
     */
    @GetMapping("/level-statistics")
    
    @Operation(summary = "获取各等级预警数量统计", description = "按预警等级分类统计预警数量")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getWarningLevelStatistics() {
        List<Map<String, Object>> result = warningRecordService.getWarningLevelStatistics();
        return ResponseEntity.ok(ApiResponse.success("查询成功", result));
    }

    /**
     * 获取预警趋势数据
     * 获取指定时间周期内的预警数量变化趋势
     * 
     * @param period 时间周期，默认为"day"
     * @param days 天数，默认为30
     * @return 预警趋势统计数据
     */
    @GetMapping("/trend")
    
    @Operation(summary = "获取预警趋势数据", description = "获取指定时间周期内的预警数量变化趋势")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getWarningTrend(
            @RequestParam(required = false, defaultValue = "day") String period,
            @RequestParam(required = false, defaultValue = "30") Integer days) {

        List<Map<String, Object>> result = warningRecordService.getWarningTrend(period, days);
        return ResponseEntity.ok(ApiResponse.success("查询成功", result));
    }

    /**
     * 获取预警地点列表
     * 从现有预警记录中获取不重复的预警地点列表，用于前端下拉选择
     * 
     * @return 预警地点列表
     */
    @GetMapping("/locations")
    
    @Operation(summary = "获取预警地点列表", description = "从现有预警记录中获取不重复的预警地点列表")
    public ResponseEntity<ApiResponse<List<String>>> getWarningLocationOptions() {
        List<String> result = warningRecordService.getWarningLocationOptions();
        return ResponseEntity.ok(ApiResponse.success("查询成功", result));
    }
}
