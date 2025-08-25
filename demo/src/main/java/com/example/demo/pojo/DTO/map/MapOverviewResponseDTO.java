package com.example.demo.pojo.DTO.map;

import com.example.demo.pojo.DTO.warning.WarningRecordResponseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * "一张图"模块概览响应DTO
 * 用于聚合所有"一张图"模块的数据
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MapOverviewResponseDTO {

    /**
     * 设施列表
     */
    private List<FacilityLocationDTO> facilities;

    /**
     * 管理体系列表
     */
    private List<ManagementSystemDTO> managementSystems;

    /**
     * 监测站点列表
     */
    private List<MonitoringStationDTO> monitoringStations;

    /**
     * 当前活跃的预警信息列表
     */
    private List<WarningRecordResponseDTO> warnings;

    /**
     * 统计数据
     */
    private WarningStatsDTO stats;
}