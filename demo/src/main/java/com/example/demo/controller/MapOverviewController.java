package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.dto.map.*;
import com.example.demo.dto.warning.WarningRecordResponseDTO;
import com.example.demo.service.MapOverviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * "一张图"模块控制器
 * 提供"一张图"模块相关的API接口
 * 
 * 包括：
 * - 获取所有"一张图"数据的概览接口
 * - 获取水利设施地理位置信息
 * - 获取管理体系信息
 * - 获取监测站点信息
 * - 获取预警信息
 * - 获取统计数据
 */
@RestController
@RequestMapping("/api/map")
@RequiredArgsConstructor
@Api(tags = "地图概览服务", description = "地图概览相关的数据查询操作")
public class MapOverviewController {

    private final MapOverviewService mapOverviewService;

    /**
     * 获取"一张图"模块的所有数据（概览接口）
     * 
     * @return "一张图"模块概览响应数据
     */
    @GetMapping("/overview")
    @PreAuthorize("hasAuthority('business:operate')")
    @ApiOperation(value = "获取地图概览数据", notes = "返回一张图模块的概览数据")
    public ResponseEntity<ApiResponse<MapOverviewResponseDTO>> getMapOverview() {
        MapOverviewResponseDTO result = mapOverviewService.getMapOverview();
        return ResponseEntity.ok(ApiResponse.success("获取一张图概览数据成功", result));
    }

    /**
     * 获取所有水利设施地理位置信息
     * 
     * @return 设施地理位置列表
     */
    @GetMapping("/facilities")
    @PreAuthorize("hasAuthority('business:operate')")
    @ApiOperation(value = "获取水利设施地理位置信息", notes = "返回所有水利设施的地理位置信息")
    public ResponseEntity<ApiResponse<List<FacilityLocationDTO>>> getAllFacilityLocations() {
        List<FacilityLocationDTO> result = mapOverviewService.getAllFacilityLocations();
        return ResponseEntity.ok(ApiResponse.success("获取设施位置信息成功", result));
    }

    /**
     * 获取管理体系信息（部门、人员及其负责区域）
     * 
     * @return 管理体系信息列表
     */
    @GetMapping("/management-system")
    @PreAuthorize("hasAuthority('business:operate')")
    @ApiOperation(value = "获取管理体系信息", notes = "返回管理体系信息，包括部门、人员及其负责区域")
    public ResponseEntity<ApiResponse<List<ManagementSystemDTO>>> getManagementSystem() {
        List<ManagementSystemDTO> result = mapOverviewService.getManagementSystem();
        return ResponseEntity.ok(ApiResponse.success("获取管理体系信息成功", result));
    }

    /**
     * 获取所有监测站点及其最新数据
     * 
     * @return 监测站点信息列表
     */
    @GetMapping("/monitoring-stations")
    @PreAuthorize("hasAuthority('business:operate')")
    @ApiOperation(value = "获取监测站点信息", notes = "返回所有监测站点的最新数据")
    public ResponseEntity<ApiResponse<List<MonitoringStationDTO>>> getMonitoringStationsWithLatestData() {
        List<MonitoringStationDTO> result = mapOverviewService.getMonitoringStationsWithLatestData();
        return ResponseEntity.ok(ApiResponse.success("获取监测站点信息成功", result));
    }

    /**
     * 获取当前活跃的预警信息
     * 
     * @return 预警信息列表
     */
    @GetMapping("/warnings")
    @PreAuthorize("hasAuthority('business:operate')")
    @ApiOperation(value = "获取活跃预警信息", notes = "返回当前活跃的预警信息")
    public ResponseEntity<ApiResponse<List<WarningRecordResponseDTO>>> getActiveWarnings() {
        List<WarningRecordResponseDTO> result = mapOverviewService.getActiveWarnings();
        return ResponseEntity.ok(ApiResponse.success("获取活跃预警信息成功", result));
    }

    /**
     * 获取基础数据统计信息
     * 
     * @return 统计数据
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('business:operate')")
    @ApiOperation(value = "获取统计数据", notes = "返回基础数据统计信息")
    public ResponseEntity<ApiResponse<WarningStatsDTO>> getWarningStats() {
        WarningStatsDTO result = mapOverviewService.getWarningStats();
        return ResponseEntity.ok(ApiResponse.success("获取统计数据成功", result));
    }
}