package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.pojo.dto.common.PageResponseDTO;
import com.example.demo.pojo.dto.monitoring.*;
import com.example.demo.service.WaterLevelMonitoringDataService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 水位监测数据管理控制器
 * 处理水位监测数据的查询、统计和分析操作
 * 
 * 设计说明：
 * - 严格参考FlowMonitoringDataController的实现
 * - 提供完整的水位监测数据管理接口
 * - 支持分页查询、统计分析、图表数据、导入导出等功能
 * - 使用统一的权限控制和异常处理机制
 */
@Slf4j
@RestController
@RequestMapping("/api/monitoring")
@Api(tags = "水位监测数据管理", description = "水位监测数据的CRUD操作及相关统计功能")
public class WaterLevelMonitoringDataController {

    /**
     * 水位监测数据服务
     */
    @Autowired
    private WaterLevelMonitoringDataService waterLevelMonitoringDataService;

    /**
     * 分页查询水位监测数据列表
     * 
     * @param page 页码，默认为1
     * @param size 每页记录数，默认10
     * @param stationId 监测站点ID，可为空
     * @param startTime 开始时间，可为空
     * @param endTime 结束时间，可为空
     * @param dataQuality 数据质量，可为空
     * @param collectionMethod 采集方式，可为空
     * @param dataSource 数据来源设备，可为空
     * @param sort 排序字段，可选，格式如："monitoring_time,desc"
     * @return 分页的水位监测数据列表
     */
    @GetMapping("/water-level-data")
    
    @ApiOperation(value = "分页查询水位监测数据", notes = "根据条件分页查询水位监测数据")
    public ResponseEntity<ApiResponse<PageResponseDTO<WaterLevelMonitoringDataResponseDTO>>> getWaterLevelMonitoringData(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long stationId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(required = false) Integer dataQuality,
            @RequestParam(required = false) String collectionMethod,
            @RequestParam(required = false) String dataSource,
            @RequestParam(required = false) String sort) {
        
        log.info("分页查询水位监测数据 - 页码: {}, 大小: {}, 站点ID: {}, 开始时间: {}, 结束时间: {}", 
                page, size, stationId, startTime, endTime);

        // 构建查询DTO对象
        WaterLevelMonitoringDataQueryDTO queryDTO = new WaterLevelMonitoringDataQueryDTO();
        queryDTO.setPage(page);
        queryDTO.setSize(size);
        queryDTO.setStationId(stationId);
        queryDTO.setStartTime(startTime);
        queryDTO.setEndTime(endTime);
        queryDTO.setDataQuality(dataQuality);
        queryDTO.setCollectionMethod(collectionMethod);
        queryDTO.setDataSource(dataSource);
        queryDTO.setSort(sort);

        // 调用服务层获取分页数据
        PageResponseDTO<WaterLevelMonitoringDataResponseDTO> result =
                waterLevelMonitoringDataService.getWaterLevelMonitoringDataPage(queryDTO);
        
        return ResponseEntity.ok(ApiResponse.success("查询成功", result));
    }

    /**
     * 获取水位监测图表数据
     * 
     * @param stationId 监测站点ID，可为空
     * @param startTime 开始时间，可为空
     * @param endTime 结束时间，可为空
     * @param interval 时间间隔，例如："hour"、"day"、"week"
     * @return 用于图表展示的水位监测数据
     */
    @GetMapping("/water-level-chart-data")
    
    @ApiOperation(value = "获取水位监测图表数据", notes = "根据站点ID和时间范围获取水位图表数据")
    public ResponseEntity<ApiResponse<WaterLevelChartDataResponseDTO>> getWaterLevelChartData(
            @RequestParam(required = false) Long stationId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(defaultValue = "hour") String interval) {
        
        log.info("获取水位监测图表数据 - 站点ID: {}, 开始时间: {}, 结束时间: {}, 间隔: {}", 
                stationId, startTime, endTime, interval);
        
        // 调用服务获取图表数据
        WaterLevelChartDataResponseDTO chartData = 
                waterLevelMonitoringDataService.getWaterLevelChartData(stationId, startTime, endTime, interval);
        
        return ResponseEntity.ok(ApiResponse.success("查询成功", chartData));
    }

    /**
     * 导出水位监测数据
     * 
     * @param queryDTO 查询条件DTO
     * @return 导出的Excel文件内容
     */
    @PostMapping("/water-level-data/export")
    
    @ApiOperation(value = "导出水位监测数据", notes = "根据查询条件将水位监测数据导出为Excel文件")
    public ResponseEntity<byte[]> exportWaterLevelData(
            @Valid @RequestBody WaterLevelMonitoringDataQueryDTO queryDTO) {

        log.info("导出水位监测数据 - 站点ID: {}", queryDTO.getStationId());

        // 调用服务导出数据为Excel文件
        byte[] fileContent = waterLevelMonitoringDataService.exportToExcel(queryDTO);

        // 生成文件名
        String filename = "water_level_data_" + System.currentTimeMillis() + ".csv";

        // 设置响应头和内容类型
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(fileContent);
    }

    /**
     * 导入水位监测数据
     * 
     * @param dataList 要导入的水位监测数据列表
     * @return 导入结果，包含成功和失败的记录数
     */
    @PostMapping("/water-level-data/import")
    
    @ApiOperation(value = "导入水位监测数据", notes = "批量导入Excel解析后的水位监测数据")
        public ResponseEntity<ApiResponse<ImportResultDTO>> importWaterLevelData(
            @Valid @RequestBody List<WaterLevelDataImportDTO> dataList) {

        log.info("导入水位监测数据 - 数据量: {}", dataList.size());

        // 调用服务导入数据
        ImportResultDTO result = waterLevelMonitoringDataService.importWaterLevelData(dataList);

        // 根据导入结果返回不同响应
        if (result.getSuccessRows() == 0 && result.getErrorRows() > 0) {
            // 全部失败
            log.warn("水位监测数据导入全部失败，总数: {}, 失败: {}", result.getTotalRows(), result.getErrorRows());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("数据导入失败，请检查数据格式", result));
        } else if (result.getSuccessRows() > 0 && result.getErrorRows() > 0) {
            // 部分成功
            log.info("水位监测数据导入部分成功，总数: {}, 成功: {}, 失败: {}",
                    result.getTotalRows(), result.getSuccessRows(), result.getErrorRows());
            return ResponseEntity.ok(ApiResponse.success(
                    String.format("数据导入完成，成功%d条，失败%d条", result.getSuccessRows(), result.getErrorRows()),
                    result));
        } else {
            // 全部成功
            log.info("水位监测数据导入全部成功，总数: {}, 成功: {}", result.getTotalRows(), result.getSuccessRows());
            return ResponseEntity.ok(ApiResponse.success("数据导入成功", result));
        }
    }
}
