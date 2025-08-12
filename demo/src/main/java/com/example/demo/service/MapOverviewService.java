package com.example.demo.service;

import com.example.demo.dto.map.*;
import com.example.demo.dto.warning.WarningRecordResponseDTO;
import com.example.demo.mapper.MapOverviewMapper;
import com.example.demo.mapper.WarningRecordMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * "一张图"模块服务类
 * 提供"一张图"模块相关的业务逻辑处理
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MapOverviewService {

    private final MapOverviewMapper mapOverviewMapper;
    private final WarningRecordMapper warningRecordMapper;

    /**
     * 获取"一张图"模块的所有数据（概览接口）
     * 
     * @return "一张图"模块概览响应数据
     */
    public MapOverviewResponseDTO getMapOverview() {
        log.info("获取一张图模块概览数据");
        
        try {
            MapOverviewResponseDTO response = new MapOverviewResponseDTO();
            
            // 并行获取各模块数据以提高性能
            // 获取设施地理位置信息
            List<FacilityLocationDTO> facilities = getAllFacilityLocations();
            response.setFacilities(facilities);
            
            // 获取管理体系信息
            List<ManagementSystemDTO> managementSystems = getManagementSystem();
            response.setManagementSystems(managementSystems);
            
            // 获取监测站点信息
            List<MonitoringStationDTO> monitoringStations = getMonitoringStationsWithLatestData();
            response.setMonitoringStations(monitoringStations);
            
            // 获取活跃预警信息
            List<WarningRecordResponseDTO> warnings = getActiveWarnings();
            response.setWarnings(warnings);
            
            // 获取统计数据
            WarningStatsDTO stats = getWarningStats();
            response.setStats(stats);
            
            log.info("一张图模块概览数据获取完成，设施数量: {}, 管理体系数量: {}, 监测站点数量: {}, 预警数量: {}", 
                    facilities.size(), managementSystems.size(), monitoringStations.size(), warnings.size());
            
            return response;
            
        } catch (Exception e) {
            log.error("获取一张图模块概览数据失败", e);
            throw new RuntimeException("获取一张图概览数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有水利设施地理位置信息
     * 
     * @return 设施地理位置列表
     */
    public List<FacilityLocationDTO> getAllFacilityLocations() {
        log.debug("获取所有水利设施地理位置信息");
        
        try {
            List<FacilityLocationDTO> facilities = mapOverviewMapper.selectAllFacilityLocations();
            log.debug("获取到 {} 个水利设施", facilities.size());
            return facilities;
            
        } catch (Exception e) {
            log.error("获取水利设施地理位置信息失败", e);
            throw new RuntimeException("获取设施位置信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取管理体系信息（部门、人员及其负责区域）
     * 
     * @return 管理体系信息列表
     */
    public List<ManagementSystemDTO> getManagementSystem() {
        log.debug("获取管理体系信息");
        
        try {
            List<ManagementSystemDTO> managementSystems = mapOverviewMapper.selectManagementSystem();
            log.debug("获取到 {} 个管理体系信息", managementSystems.size());
            return managementSystems;
            
        } catch (Exception e) {
            log.error("获取管理体系信息失败", e);
            throw new RuntimeException("获取管理体系信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有监测站点及其最新数据
     * 
     * @return 监测站点信息列表
     */
    public List<MonitoringStationDTO> getMonitoringStationsWithLatestData() {
        log.debug("获取所有监测站点及其最新数据");
        
        try {
            List<MonitoringStationDTO> monitoringStations = mapOverviewMapper.selectMonitoringStationsWithLatestData();
            log.debug("获取到 {} 个监测站点", monitoringStations.size());
            return monitoringStations;
            
        } catch (Exception e) {
            log.error("获取监测站点信息失败", e);
            throw new RuntimeException("获取监测站点信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前活跃的预警信息
     * 
     * @return 预警信息列表
     */
    public List<WarningRecordResponseDTO> getActiveWarnings() {
        log.debug("获取当前活跃的预警信息");
        
        try {
            // 查询状态为"进行中"的预警记录
            List<WarningRecordResponseDTO> activeWarnings = warningRecordMapper.selectActiveWarnings();
            log.debug("获取到 {} 个活跃预警", activeWarnings.size());
            return activeWarnings;
            
        } catch (Exception e) {
            log.error("获取活跃预警信息失败", e);
            throw new RuntimeException("获取预警信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取基础数据统计信息
     * 
     * @return 统计数据
     */
    public WarningStatsDTO getWarningStats() {
        log.debug("获取基础数据统计信息");
        
        try {
            WarningStatsDTO stats = mapOverviewMapper.selectWarningStats();
            log.debug("获取统计数据完成，设施总数: {}, 监测点总数: {}, 预警总数: {}", 
                    stats.getFacilityCount(), stats.getStationCount(), stats.getTotalWarnings());
            return stats;
            
        } catch (Exception e) {
            log.error("获取基础数据统计信息失败", e);
            throw new RuntimeException("获取统计信息失败: " + e.getMessage());
        }
    }
}