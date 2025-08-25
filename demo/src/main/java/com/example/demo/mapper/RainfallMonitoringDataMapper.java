package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.pojo.DTO.monitoring.RainfallMonitoringDataResponseDTO;
import com.example.demo.pojo.entity.monitoring.RainfallMonitoringData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 雨情监测数据Mapper接口
 * 提供雨情监测数据相关的数据访问操作
 * 
 * 设计说明：
 * - 严格参考WaterLevelMonitoringDataMapper的实现规范
 * - 支持分页查询、统计分析、图表数据等功能
 * - 使用MyBatis-Plus的BaseMapper提供基础CRUD操作
 * - 自定义SQL查询支持复杂的业务需求
 */
@Mapper
public interface RainfallMonitoringDataMapper extends BaseMapper<RainfallMonitoringData> {

    /**
     * 分页查询雨情监测数据（包含关联信息）
     * 
     * @param stationId 监测站点ID（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param dataQuality 数据质量（可选）
     * @param collectionMethod 采集方式（可选）
     * @param dataSource 数据来源设备（可选）
     * @param sort 排序字段（可选）
     * @return 查询结果列表
     */
    List<RainfallMonitoringDataResponseDTO> selectRainfallMonitoringDataPage(
            @Param("stationId") Long stationId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("dataQuality") Integer dataQuality,
            @Param("collectionMethod") String collectionMethod,
            @Param("dataSource") String dataSource,
            @Param("sort") String sort);

    /**
     * 查询雨情统计数据
     * 
     * @param stationId 监测站点ID（可选）
     * @param timeRange 时间范围（today/week/month）
     * @return 统计数据Map
     */
    Map<String, Object> selectRainfallStatistics(
            @Param("stationId") Long stationId, 
            @Param("timeRange") String timeRange);

    /**
     * 查询雨情图表数据
     * 
     * @param stationId 监测站点ID
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param interval 时间间隔（hour/day/month）
     * @return 图表数据列表
     */
    List<Map<String, Object>> selectRainfallChartData(
            @Param("stationId") Long stationId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("interval") String interval);

    /**
     * 查询所有站点的雨情图表数据（汇总）
     * 
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param interval 时间间隔（hour/day/month）
     * @return 汇总图表数据列表
     */
    List<Map<String, Object>> selectAllStationsRainfallChartData(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("interval") String interval);

    /**
     * 查询监测概览统计数据
     * 
     * @return 概览统计数据Map
     */
    Map<String, Object> selectMonitoringOverview();

    /**
     * 查询数据质量统计（按站点分组）
     * 
     * @param timeRange 时间范围（today/week/month）
     * @return 各站点数据质量统计列表
     */
    List<Map<String, Object>> selectDataQualityStatsByStation(@Param("timeRange") String timeRange);

    /**
     * 查询数据质量趋势数据
     * 
     * @param timeRange 时间范围（today/week/month）
     * @return 数据质量趋势列表
     */
    List<Map<String, Object>> selectDataQualityTrend(@Param("timeRange") String timeRange);

    /**
     * 统计指定站点和时间的数据条数（用于重复性检查）
     * 
     * @param stationId 监测站点ID
     * @param monitoringTime 监测时间
     * @return 数据条数
     */
    int countByStationIdAndTime(
            @Param("stationId") Long stationId, 
            @Param("monitoringTime") LocalDateTime monitoringTime);
} 