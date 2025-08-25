package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.pojo.DTO.common.PageResponseDTO;
import com.example.demo.pojo.DTO.monitoring.*;
import com.example.demo.service.WaterQualityMonitoringDataService;
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
 * 水质监测数据管理控制器
 * 本控制器负责处理水质监测数据的查询、统计、分析、导入和导出等操作
 * 支持8种水质监测项目的数据管理，包括：
 * - 水温(WT)
 * - 浊度(TU)
 * - PH(PH)
 * - 电导(EC)
 * - 溶解氧(DO)
 * - 氨氮(AN)
 * - 化学需氧量(COD)
 * - 余氯(RC)
 * 
 * 提供完整的CRUD操作和数据分析功能，使用统一的权限控制和异常处理机制
 */
@Slf4j
@RestController
@RequestMapping("/api/monitoring")
@Api(tags = "水质监测数据管理", description = "水质监测数据的CRUD操作及相关统计功能")
public class WaterQualityMonitoringDataController {

    @Autowired
    private WaterQualityMonitoringDataService waterQualityMonitoringDataService;

    /**
     * 分页查询水质监测数据列表
     * 
     * 根据条件分页查询水质监测数据，支持多种筛选条件：
     * - 监测站点ID
     * - 时间范围
     * - 监测项目代码
     * - 数据质量
     * - 采集方式
     * - 数据来源
     * 
     * @param page 页码，默认为1
     * @param size 每页大小，默认为10
     * @param stationId 监测站点ID（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param monitoringItemCode 监测项目代码(WT/TU/PH/EC/DO/AN/COD/RC)（可选）
     * @param dataQuality 数据质量(1:正常,2:异常,3:缺失)（可选）
     * @param collectionMethod 采集方式(AUTO:自动,MANUAL:手动)（可选）
     * @param dataSource 数据来源设备（可选）
     * @param sort 排序字段，格式为"字段,排序方式"，例如"monitoring_time,desc"
     * @return 分页的水质监测数据列表
     */
    @GetMapping("/water-quality-data")
    
    @ApiOperation(value = "分页查询水质监测数据", notes = "根据条件分页查询水质监测数据")
    public ResponseEntity<ApiResponse<PageResponseDTO<WaterQualityMonitoringDataResponseDTO>>> getWaterQualityMonitoringData(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long stationId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(required = false) String monitoringItemCode,
            @RequestParam(required = false) Integer dataQuality,
            @RequestParam(required = false) String collectionMethod,
            @RequestParam(required = false) String dataSource,
            @RequestParam(required = false) String sort) {
        try {
            WaterQualityMonitoringDataQueryDTO queryDTO = new WaterQualityMonitoringDataQueryDTO();
            queryDTO.setPage(page);
            queryDTO.setSize(size);
            queryDTO.setStationId(stationId);
            queryDTO.setStartTime(startTime);
            queryDTO.setEndTime(endTime);
            queryDTO.setMonitoringItemCode(monitoringItemCode);
            queryDTO.setDataQuality(dataQuality);
            queryDTO.setCollectionMethod(collectionMethod);
            queryDTO.setDataSource(dataSource);
            queryDTO.setSort(sort != null ? sort : "monitoring_time,desc");

            PageResponseDTO<WaterQualityMonitoringDataResponseDTO> result =
                    waterQualityMonitoringDataService.getWaterQualityMonitoringDataPage(queryDTO);
            return ResponseEntity.ok(ApiResponse.success("查询成功", result));
        } catch (Exception e) {
            log.error("查询水质监测数据失败", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "查询失败: " + e.getMessage()));
        }
    }

    /**
     * 获取水质监测图表数据
     * 
     * 获取指定站点、监测项目和时间范围的水质图表数据
     * 用于前端绘制趋势图表，支持不同时间间隔的数据聚合
     * 
     * @param stationId 监测站点ID（可选）
     * @param monitoringItemCode 监测项目代码(WT/TU/PH/EC/DO/AN/COD/RC)（必填）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param interval 时间间隔(hour:小时,day:天,month:月,默认为hour
     * @return 适用于图表展示的水质监测数据
     */
    @GetMapping("/water-quality-chart-data")
    
    @ApiOperation(value = "获取水质监测图表数据", notes = "根据站点ID和时间范围获取水质图表数据")
    public ResponseEntity<ApiResponse<WaterQualityChartDataResponseDTO>> getWaterQualityChartData(
            @RequestParam(required = false) Long stationId,
            @RequestParam String monitoringItemCode,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(defaultValue = "hour") String interval) {
        try {
            WaterQualityChartDataResponseDTO result = waterQualityMonitoringDataService.getWaterQualityChartData(
                    stationId, monitoringItemCode, startTime, endTime, interval);
            return ResponseEntity.ok(ApiResponse.success("查询成功", result));
        } catch (Exception e) {
            log.error("查询水质图表数据失败", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "查询失败: " + e.getMessage()));
        }
    }

    /**
     * 导出水质监测数据
     * 
     * 根据条件导出水质监测数据到Excel/CSV文件，支持多种筛选条件
     * 导出数据量上限为10000条，以防止内存溢出
     * 
     * @param queryDTO 导出查询条件，包含监测站点、时间范围、监测项目等筛选参数
     * @return 包含导出数据的文件流
     */
    @PostMapping("/water-quality/export")
    
    @ApiOperation(value = "导出水质监测数据", notes = "根据查询条件将水质监测数据导出为Excel文件")
    public ResponseEntity<byte[]> exportWaterQualityData(
            @RequestBody WaterQualityMonitoringDataQueryDTO queryDTO) {
        try {
            // 限制导出数据量，防止内存溢出
            if (queryDTO.getSize() == null || queryDTO.getSize() > 10000) {
                queryDTO.setSize(10000);
            }

            List<WaterQualityMonitoringDataResponseDTO> exportData = waterQualityMonitoringDataService.exportWaterQualityData(queryDTO);

            // 这里应该调用Excel生成工具类，暂时返回CSV格式数据
            StringBuilder csvContent = new StringBuilder();
            csvContent.append("监测时间,监测站点,站码,水温(�?,浊度(NTU),PH�?电导�?uS/cm),溶解�?mg/L),氨氮(mg/L),化学需氧量(mg/L),余氯(mg/L),数据质量,采集方式,数据来源,备注\n");

            for (WaterQualityMonitoringDataResponseDTO data : exportData) {
                csvContent.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                    data.getMonitoringTime() != null ? data.getMonitoringTime().toString() : "",
                    data.getStationName() != null ? data.getStationName() : "",
                    data.getStationCode() != null ? data.getStationCode() : "",
                    data.getWaterTemperature() != null ? data.getWaterTemperature().toString() : "",
                    data.getTurbidity() != null ? data.getTurbidity().toString() : "",
                    data.getPhValue() != null ? data.getPhValue().toString() : "",
                    data.getConductivity() != null ? data.getConductivity().toString() : "",
                    data.getDissolvedOxygen() != null ? data.getDissolvedOxygen().toString() : "",
                    data.getAmmoniaNitrogen() != null ? data.getAmmoniaNitrogen().toString() : "",
                    data.getCodValue() != null ? data.getCodValue().toString() : "",
                    data.getResidualChlorine() != null ? data.getResidualChlorine().toString() : "",
                    data.getDataQualityText() != null ? data.getDataQualityText() : "",
                    data.getCollectionMethod() != null ? data.getCollectionMethod() : "",
                    data.getDataSource() != null ? data.getDataSource() : "",
                    data.getRemark() != null ? data.getRemark() : ""
                ));
            }

            byte[] excelData = csvContent.toString().getBytes("UTF-8");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment",
                "water_quality_monitoring_data_" + System.currentTimeMillis() + ".xlsx");
            headers.setContentLength(excelData.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelData);
        } catch (Exception e) {
            log.error("导出水质监测数据失败", e);
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
     * 导入水质监测数据
     * 
     * 批量导入Excel解析后的水质监测数据
     * 支持导入验证，返回成功和失败的数据条数及错误信息
     * 
     * @param dataList 待导入的水质数据列表
     * @return 导入结果，包含总条数、成功条数、失败条数及错误信息
     */
    @PostMapping("/water-quality/import")
    
    @ApiOperation(value = "导入水质监测数据", notes = "批量导入Excel解析后的水质监测数据")
    public ResponseEntity<ApiResponse<ImportResultDTO>> importWaterQualityData(
            @RequestBody @Valid List<WaterQualityDataImportDTO> dataList) {

        try {
            log.info("开始导入水质监测数据，数据量: {}", dataList.size());

            // 参数验证
            if (dataList == null || dataList.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("导入数据不能为空"));
            }

            // 调用服务层处理导入
            ImportResultDTO result = waterQualityMonitoringDataService.importWaterQualityData(dataList);

            log.info("水质监测数据导入完成，总数: {}, 成功: {}, 失败: {}",
                    result.getTotalRows(), result.getSuccessRows(), result.getErrorRows());

            // 根据导入结果返回不同响应
            if (result.getSuccessRows() == 0 && result.getErrorRows() > 0) {
                // 全部失败
                log.warn("水质监测数据导入全部失败，总数: {}, 失败: {}", result.getTotalRows(), result.getErrorRows());
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("数据导入失败，请检查数据格式", result));
            } else if (result.getSuccessRows() > 0 && result.getErrorRows() > 0) {
                // 部分成功
                log.info("水质监测数据导入部分成功，总数: {}, 成功: {}, 失败: {}",
                        result.getTotalRows(), result.getSuccessRows(), result.getErrorRows());
                return ResponseEntity.ok(ApiResponse.success(
                            String.format("数据导入完成，成功%d条，失败%d条", result.getSuccessRows(), result.getErrorRows()),
                        result));
            } else {
                // 全部成功
                log.info("水质监测数据导入全部成功，总数: {}, 成功: {}", result.getTotalRows(), result.getSuccessRows());
                return ResponseEntity.ok(ApiResponse.success("数据导入成功", result));
            }

        } catch (IllegalArgumentException e) {
            log.warn("导入水质监测数据参数错误: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("参数错误: " + e.getMessage()));
        } catch (Exception e) {
            log.error("导入水质监测数据失败", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("导入失败: " + e.getMessage()));
        }
    }
}
