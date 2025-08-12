package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.dto.common.PageResponseDTO;
import com.example.demo.dto.facility.*;
import com.example.demo.entity.facility.MonitoringStation;
import com.example.demo.service.MonitoringStationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 监测站点管理控制器
 * 
 * 用于处理与监测站点相关的API请求，包括查询、创建、更新和删除监测站点信息。
 * 提供监测站点的CRUD操作以及统计、批量处理等功能。
 * 
 * @author system
 * @version 1.0
 */
@RestController
@RequestMapping("/api/engineering-service/monitoring-stations")
@RequiredArgsConstructor
public class MonitoringStationController {

    /**
     * 监测站点服务
     */
    private final MonitoringStationService monitoringStationService;

    /**
     * 分页查询监测站点列表
     * 
     * 根据条件分页查询监测站点信息，支持关键词搜索和条件筛选。
     * 
     * @param page 页码，默认值为1
     * @param size 每页大小，默认值为10
     * @param keyword 搜索关键词（站点名称、编码、安装位置）
     * @param monitoringType 监测类型
     * @param operationStatus 运行状态
     * @param transmissionMethod 数据传输方式
     * @return 包含监测站点分页信息的API响应
     */
    @GetMapping
    @PreAuthorize("hasAuthority('business:manage')")
    public ResponseEntity<ApiResponse<PageResponseDTO<MonitoringStationResponseDTO>>> getMonitoringStationPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String monitoringType,
            @RequestParam(required = false) String operationStatus,
            @RequestParam(required = false) String transmissionMethod) {
        try {
            MonitoringStationQueryDTO queryDTO = new MonitoringStationQueryDTO();
            queryDTO.setPage(page);
            queryDTO.setSize(size);
            queryDTO.setKeyword(keyword);
            queryDTO.setMonitoringType(monitoringType);
            queryDTO.setOperationStatus(operationStatus);
            queryDTO.setTransmissionMethod(transmissionMethod);

            PageResponseDTO<MonitoringStationResponseDTO> result = monitoringStationService.getMonitoringStationPage(queryDTO);
            return ResponseEntity.ok(ApiResponse.success("查询成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 创建监测站点
     * 
     * 创建新的监测站点信息并保存到数据库。
     * 
     * @param createDTO 监测站点创建信息DTO
     * @return 包含创建成功的监测站点实体的API响应
     */
    @PostMapping
    @PreAuthorize("hasAuthority('business:manage')")
    public ResponseEntity<ApiResponse<MonitoringStation>> createMonitoringStation(
            @Valid @RequestBody MonitoringStationCreateDTO createDTO) {
        try {
            MonitoringStation result = monitoringStationService.createMonitoringStation(createDTO);
            return ResponseEntity.ok(ApiResponse.success("创建成功", result));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 更新监测站点信息
     * 
     * 根据ID更新监测站点的信息。
     * 
     * @param id 需要更新的监测站点ID
     * @param updateDTO 监测站点更新信息DTO
     * @return 包含更新后的监测站点实体的API响应
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('business:manage')")
    public ResponseEntity<ApiResponse<MonitoringStation>> updateMonitoringStation(
            @PathVariable Long id,
            @Valid @RequestBody MonitoringStationUpdateDTO updateDTO) {
        try {
            MonitoringStation result = monitoringStationService.updateMonitoringStation(id, updateDTO);
            return ResponseEntity.ok(ApiResponse.success("更新成功", result));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 删除监测站点
     * 
     * 根据ID删除监测站点信息（软删除）。
     * 
     * @param id 需要删除的监测站点ID
     * @return API响应
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('business:manage')")
    public ResponseEntity<ApiResponse<Void>> deleteMonitoringStation(
            @PathVariable Long id) {
        try {
            monitoringStationService.deleteMonitoringStation(id);
            return ResponseEntity.ok(ApiResponse.success("删除成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(404, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 批量删除监测站点
     * 
     * 根据ID列表批量删除多个监测站点（软删除）。
     * 
     * @param ids 需要批量删除的监测站点ID列表
     * @return API响应
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('business:manage')")
    public ResponseEntity<ApiResponse<Void>> batchDeleteMonitoringStations(
            @RequestBody List<Long> ids) {
        try {
            monitoringStationService.batchDeleteMonitoringStations(ids);
            return ResponseEntity.ok(ApiResponse.success("批量删除成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 获取所有可用监测站点
     * 
     * 获取所有可用的监测站点列表，用于下拉选择。
     * 可以通过监测项目代码筛选特定类型的监测站点。
     * 
     * @param monitoringItemCode 监测项目代码（可选）
     * @return 包含可用监测站点列表的API响应
     */
    @GetMapping("/available")
    @PreAuthorize("hasAuthority('business:manage')")
    public ResponseEntity<ApiResponse<List<MonitoringStation>>> getAvailableMonitoringStations(
            @RequestParam(value = "monitoringItemCode", required = false) String monitoringItemCode) {
        try {
            List<MonitoringStation> result;
            if (monitoringItemCode != null && !monitoringItemCode.trim().isEmpty()) {
                result = monitoringStationService.getAvailableMonitoringStationsByType(monitoringItemCode);
            } else {
                result = monitoringStationService.getAvailableMonitoringStations();
            }
            return ResponseEntity.ok(ApiResponse.success("查询成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 统计监测站点总数
     * 
     * 统计系统中监测站点的总数量。
     * 
     * @return 包含监测站点总数的API响应
     */
    @GetMapping("/count")
    @PreAuthorize("hasAuthority('business:manage')")
    public ResponseEntity<ApiResponse<Long>> countTotal() {
        try {
            long result = monitoringStationService.countTotal();
            return ResponseEntity.ok(ApiResponse.success("统计成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }
}
