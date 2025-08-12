package com.example.demo.mapper;

import com.example.demo.dto.map.FacilityLocationDTO;
import com.example.demo.dto.map.ManagementSystemDTO;
import com.example.demo.dto.map.MonitoringStationDTO;
import com.example.demo.dto.map.WarningStatsDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * "一张图"模块Mapper接口
 * 提供"一张图"模块相关的数据访问操作
 */
@Mapper
public interface MapOverviewMapper {

    /**
     * 获取所有水利设施地理位置信息
     * 
     * @return 设施地理位置列表
     */
    List<FacilityLocationDTO> selectAllFacilityLocations();

    /**
     * 获取管理体系信息（部门、人员及其负责区域）
     * 
     * @return 管理体系信息列表
     */
    List<ManagementSystemDTO> selectManagementSystem();

    /**
     * 获取所有监测站点及其最新数据
     * 
     * @return 监测站点信息列表
     */
    List<MonitoringStationDTO> selectMonitoringStationsWithLatestData();

    /**
     * 获取基础数据统计信息
     * 
     * @return 统计数据
     */
    WarningStatsDTO selectWarningStats();
}