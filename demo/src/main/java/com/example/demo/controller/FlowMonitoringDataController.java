package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.dto.common.PageResponseDTO;
import com.example.demo.dto.monitoring.*;
import com.example.demo.service.FlowMonitoringDataService;
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

/**
 * 流量监测数据管理控制器
 * 处理流量监测数据的查询、统计和分析操作
 * 提供流量数据的CRUD、导入导出、统计分析等功能
 */
@Slf4j
@RestController
@RequestMapping("/api/monitoring")
@RequiredArgsConstructor
public class FlowMonitoringDataController {

    /**
     * 流量监测数据服务
     */
    private final FlowMonitoringDataService flowMonitoringDataService;

    /**
     * 分页查询流量监测数据列表
     * 
     * @param page 页码，默认为1
     * @param size 每页大小，默认为10
     * @param stationId 监测站点ID（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param dataQuality 数据质量(1:正常,2:异常,3:缺失)（可选）
     * @param collectionMethod 采集方式(AUTO:自动,MANUAL:手动)（可选）
     * @param dataSource 数据来源设备（可选）
     * @param sort 排序字段，默认为"monitoring_time,desc"
     * @return 流量监测数据分页结果
     */
    @GetMapping("/flow-data")
    @PreAuthorize("hasAuthority('business:operate')")
    public ResponseEntity<ApiResponse<PageResponseDTO<FlowMonitoringDataResponseDTO>>> getFlowMonitoringData(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long stationId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(required = false) Integer dataQuality,
            @RequestParam(required = false) String collectionMethod,
            @RequestParam(required = false) String dataSource,
            @RequestParam(required = false) String sort) {
        try {
            // 构建查询DTO对象
            FlowMonitoringDataQueryDTO queryDTO = new FlowMonitoringDataQueryDTO();
            queryDTO.setPage(page);
            queryDTO.setSize(size);
            queryDTO.setStationId(stationId);
            queryDTO.setStartTime(startTime);
            queryDTO.setEndTime(endTime);
            queryDTO.setDataQuality(dataQuality);
            queryDTO.setCollectionMethod(collectionMethod);
            queryDTO.setDataSource(dataSource);
            queryDTO.setSort(sort != null ? sort : "monitoring_time,desc");

            // 调用服务层获取分页数据
            PageResponseDTO<FlowMonitoringDataResponseDTO> result =
                    flowMonitoringDataService.getFlowMonitoringDataPage(queryDTO);
            return ResponseEntity.ok(ApiResponse.success("查询成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "查询失败: " + e.getMessage()));
        }
    }

    /**
     * 获取流量监测图表数据
     * 获取指定站点和时间范围的流量图表数据
     * 支持数据类型区分（瞬时流量/累计流量）
     * 
     * @param stationId 监测站点ID（必须提供）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param interval 时间间隔(hour:小时,day:天,month:月)，默认为hour
     * @param dataType 数据类型(flowRate:瞬时流量,cumulativeFlow:累计流量)，默认为flowRate
     * @return 流量图表数据
     */
    @GetMapping("/flow-chart-data")
    @PreAuthorize("hasAuthority('business:operate')")
    public ResponseEntity<ApiResponse<FlowChartDataResponseDTO>> getFlowChartData(
            @RequestParam(required = false) Long stationId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "hour") String interval,
            @RequestParam(defaultValue = "flowRate") String dataType) {
        try {
            // 站点ID参数验证
            if (stationId == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "查询失败: 必须提供监测站点ID"));
            }
            
            // 调用服务层获取图表数据
            FlowChartDataResponseDTO result = flowMonitoringDataService.getFlowChartData(
                    stationId, startTime, endTime, interval, dataType);
            return ResponseEntity.ok(ApiResponse.success("查询成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "查询失败: " + e.getMessage()));
        }
    }

    /**
     * 导出流量监测数据
     * 根据查询条件将流量监测数据导出为Excel文件
     * 
     * @param queryDTO 查询参数DTO，包含筛选条件
     * @return 导出的Excel文件字节数组
     */
    @PostMapping("/flow-data/export")
    @PreAuthorize("hasAuthority('business:operate')")
    public ResponseEntity<byte[]> exportFlowData(
            @RequestBody FlowMonitoringDataQueryDTO queryDTO) {
        try {
            // 限制导出数据量，防止内存溢出
            if (queryDTO.getSize() == null || queryDTO.getSize() > 10000) {
                queryDTO.setSize(10000);
            }

            // 调用服务层导出数据为Excel
            byte[] excelData = flowMonitoringDataService.exportToExcel(queryDTO);

            // 设置响应头信息
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment",
                "flow_monitoring_data_" + System.currentTimeMillis() + ".csv");
            headers.setContentLength(excelData.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelData);
        } catch (Exception e) {
            log.error("导出流量监测数据失败", e);
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
     * 导入流量监测数据
     * 批量导入Excel解析后的流量监测数据
     * 
     * @param dataList 导入的数据列表
     * @return 导入结果，包含成功和失败的数据统计
     */
    @PostMapping("/flow-data/import")
    @PreAuthorize("hasAuthority('business:operate')")
    public ResponseEntity<ApiResponse<ImportResultDTO>> importFlowData(
            @RequestBody @Valid List<FlowDataImportDTO> dataList) {

        try {
            log.info("开始导入流量监测数据，数据量: {}", dataList.size());

            // 参数验证
            if (dataList == null || dataList.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("导入数据不能为空"));
            }

            // 调用服务层处理导入
            ImportResultDTO result = flowMonitoringDataService.importFlowData(dataList);

            log.info("流量监测数据导入完成，总数: {}, 成功: {}, 失败: {}",
                    result.getTotalRows(), result.getSuccessRows(), result.getErrorRows());

            // 根据导入结果返回不同响应
            if (result.getSuccessRows() == 0 && result.getErrorRows() > 0) {
                // 全部失败
                log.warn("流量监测数据导入全部失败，总数: {}, 失败: {}", result.getTotalRows(), result.getErrorRows());
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("数据导入失败，请检查数据格式", result));
            } else if (result.getSuccessRows() > 0 && result.getErrorRows() > 0) {
                // 部分成功
                log.info("流量监测数据导入部分成功，总数: {}, 成功: {}, 失败: {}",
                        result.getTotalRows(), result.getSuccessRows(), result.getErrorRows());
                return ResponseEntity.ok(ApiResponse.success(
                        String.format("数据导入完成，成功%d条，失败%d条", result.getSuccessRows(), result.getErrorRows()),
                        result));
            } else {
                // 全部成功
                log.info("流量监测数据导入全部成功，总数: {}, 成功: {}", result.getTotalRows(), result.getSuccessRows());
                return ResponseEntity.ok(ApiResponse.success("数据导入成功", result));
            }

        } catch (IllegalArgumentException e) {
            log.warn("导入流量监测数据参数错误: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("参数错误: " + e.getMessage()));
        } catch (Exception e) {
            log.error("导入流量监测数据失败", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("导入失败: " + e.getMessage()));
        }
    }
}
