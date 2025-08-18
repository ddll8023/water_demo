package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.dto.common.PageResponseDTO;
import com.example.demo.dto.monitoring.*;
import com.example.demo.service.RainfallMonitoringDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
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
     * 支持数据类型区分（时段雨量/累计雨量）
     *
     * @param stationId 监测站点ID（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param interval 时间间隔，例如：hour, day, week, month（默认为hour）
     * @param dataType 数据类型(rainfall:时段雨量,cumulativeRainfall:累计雨量)，默认为rainfall
     * @return 图表所需的雨情监测数据
     */
    @GetMapping("/rainfall-chart-data")
    @PreAuthorize("hasAuthority('business:operate')")
    public ResponseEntity<ApiResponse<RainfallChartDataResponseDTO>> getRainfallChartData(
            @RequestParam(required = false) Long stationId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(defaultValue = "hour") String interval,
            @RequestParam(defaultValue = "rainfall") String dataType) {

        log.info("获取雨情监测图表数据 - 站点ID: {}, 开始时间: {}, 结束时间: {}, 间隔: {}, 数据类型: {}",
                stationId, startTime, endTime, interval, dataType);

        try {
            // 获取图表数据
            RainfallChartDataResponseDTO chartData =
                    rainfallMonitoringDataService.getRainfallChartData(stationId, startTime, endTime, interval, dataType);

            return ResponseEntity.ok(ApiResponse.success("查询成功", chartData));
        } catch (Exception e) {
            log.error("获取雨情监测图表数据失败", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "查询失败: " + e.getMessage()));
        }
    }

    /**
     * 导出雨情监测数据
     * 根据查询条件将雨情监测数据导出为Excel文件
     *
     * @param queryDTO 查询参数DTO，包含筛选条件
     * @return 导出的Excel文件字节数组
     */
    @PostMapping("/rainfall/export")
    @PreAuthorize("hasAuthority('business:operate')")
    public ResponseEntity<byte[]> exportRainfallData(
            @RequestBody RainfallMonitoringDataQueryDTO queryDTO) {
        try {
            log.info("导出雨情监测数据 - 站点ID: {}", queryDTO.getStationId());

            // 限制导出数据量，防止内存溢出
            if (queryDTO.getSize() == null || queryDTO.getSize() > 10000) {
                queryDTO.setSize(10000);
            }

            // 调用服务层导出数据为Excel
            byte[] excelData = rainfallMonitoringDataService.exportToExcel(queryDTO);

            // 设置响应头信息
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment",
                "rainfall_monitoring_data_" + System.currentTimeMillis() + ".csv");
            headers.setContentLength(excelData.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelData);
        } catch (Exception e) {
            log.error("导出雨情监测数据失败", e);
            // 对于文件下载接口，返回错误时需要特殊处理
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String errorJson = "{\"success\":false,\"message\":\"导出失败: " + e.getMessage() + "\"}";
            return ResponseEntity.badRequest()
                    .headers(headers)
                    .body(errorJson.getBytes());
        }
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