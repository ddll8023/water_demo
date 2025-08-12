package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.dto.common.PageResponseDTO;
import com.example.demo.dto.monitoring.*;
import com.example.demo.service.RainfallMonitoringDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 雨情监测数据管理控制器
 * 处理雨情监测数据的查询、统计和分析操作
 * 
 * 设计说明：
 * - 严格参考WaterLevelMonitoringDataController的实现规范
 * - 提供完整的雨情监测数据管理接口
 * - 支持分页查询、统计分析、图表数据、导入导出等功能
 * - 使用统一的权限控制和异常处理机制
 */
@Slf4j
@RestController
@RequestMapping("/api/monitoring")
@RequiredArgsConstructor
public class RainfallMonitoringDataController {

    /**
     * 雨情监测数据服务
     */
    private final RainfallMonitoringDataService rainfallMonitoringDataService;

    /**
     * 分页查询雨情监测数据列表
     * 
     * @param page 页码，默认为1
     * @param size 每页大小，默认为10
     * @param stationId 监测站点ID（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param dataQuality 数据质量（可选）
     * @param collectionMethod 采集方式（可选）
     * @param dataSource 数据来源设备（可选）
     * @param sort 排序字段（可选）
     * @return 分页的雨情监测数据列表
     */
    @GetMapping("/rainfall-data")
    @PreAuthorize("hasAuthority('business:operate')")
    public ResponseEntity<ApiResponse<PageResponseDTO<RainfallMonitoringDataResponseDTO>>> getRainfallMonitoringData(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long stationId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(required = false) Integer dataQuality,
            @RequestParam(required = false) String collectionMethod,
            @RequestParam(required = false) String dataSource,
            @RequestParam(required = false) String sort) {
        
        log.info("分页查询雨情监测数据 - 页码: {}, 大小: {}, 站点ID: {}, 开始时间: {}, 结束时间: {}", 
                page, size, stationId, startTime, endTime);

        // 构建查询对象
        RainfallMonitoringDataQueryDTO queryDTO = new RainfallMonitoringDataQueryDTO();
        queryDTO.setPage(page);
        queryDTO.setSize(size);
        queryDTO.setStationId(stationId);
        queryDTO.setStartTime(startTime);
        queryDTO.setEndTime(endTime);
        queryDTO.setDataQuality(dataQuality);
        queryDTO.setCollectionMethod(collectionMethod);
        queryDTO.setDataSource(dataSource);
        queryDTO.setSort(sort);

        // 调用服务获取分页数据
        PageResponseDTO<RainfallMonitoringDataResponseDTO> result =
                rainfallMonitoringDataService.getRainfallMonitoringDataPage(queryDTO);
        
        return ResponseEntity.ok(ApiResponse.success("查询成功", result));
    }

    /**
     * 获取用于图表展示的雨情监测数据
     *
     * @param stationId 监测站点ID（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param interval 时间间隔，例如：hour, day, week, month（默认为hour）
     * @return 图表所需的雨情监测数据
     */
    @GetMapping("/rainfall-chart-data")
    @PreAuthorize("hasAuthority('business:operate')")
    public ResponseEntity<ApiResponse<RainfallChartDataResponseDTO>> getRainfallChartData(
            @RequestParam(required = false) Long stationId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "hour") String interval) {
        
        log.info("获取雨情监测图表数据 - 站点ID: {}, 开始时间: {}, 结束时间: {}, 间隔: {}", 
                stationId, startTime, endTime, interval);
        
        // 获取图表数据
        RainfallChartDataResponseDTO chartData = 
                rainfallMonitoringDataService.getRainfallChartData(stationId, startTime, endTime, interval);
        
        return ResponseEntity.ok(ApiResponse.success("查询成功", chartData));
    }

    /**
     * 批量导入雨情监测数据
     *
     * @param stationId 监测站点ID
     * @param dataList 要导入的数据列表
     * @return 导入结果，包含成功和失败数量
     */
    @PostMapping("/rainfall-data/import")
    @PreAuthorize("hasAuthority('business:manage')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> importRainfallData(
            @RequestParam Long stationId,
            @Valid @RequestBody List<RainfallDataImportDTO> dataList) {

        log.info("导入雨情监测数据 - 站点ID: {}, 数据量: {}", stationId, dataList.size());

        // 执行数据导入
        Map<String, Object> result = rainfallMonitoringDataService.importRainfallData(dataList, stationId);

        int successCount = (int) result.get("successCount");
        int failCount = (int) result.get("failCount");
        int totalCount = successCount + failCount;

        // 根据导入结果返回不同响应
        if (successCount == 0 && failCount > 0) {
            // 全部失败
            log.warn("雨情监测数据导入全部失败，总数: {}, 失败: {}", totalCount, failCount);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("数据导入失败，请检查数据格式", result));
        } else if (successCount > 0 && failCount > 0) {
            // 部分成功
            log.info("雨情监测数据导入部分成功，总数: {}, 成功: {}, 失败: {}",
                    totalCount, successCount, failCount);
            return ResponseEntity.ok(ApiResponse.success(
                    String.format("数据导入完成，成功%d条，失败%d条", successCount, failCount),
                    result));
        } else {
            // 全部成功
            log.info("雨情监测数据导入全部成功，总数: {}, 成功: {}", totalCount, successCount);
            return ResponseEntity.ok(ApiResponse.success("数据导入成功", result));
        }
    }

} 