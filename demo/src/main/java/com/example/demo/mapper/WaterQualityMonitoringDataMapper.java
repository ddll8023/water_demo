package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.pojo.entity.monitoring.WaterQualityMonitoringData;
import com.example.demo.pojo.DTO.monitoring.WaterQualityMonitoringDataResponseDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 水质监测数据Mapper接口
 * 提供水质监测数据相关的数据访问操作
 */
@Mapper
public interface WaterQualityMonitoringDataMapper extends BaseMapper<WaterQualityMonitoringData> {

    /**
     * 分页查询水质监测数据（包含关联信息）
     */
    List<WaterQualityMonitoringDataResponseDTO> selectWaterQualityMonitoringDataPage(
            @Param("stationId") Long stationId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("monitoringItemCode") String monitoringItemCode,
            @Param("dataQuality") Integer dataQuality,
            @Param("collectionMethod") String collectionMethod,
            @Param("dataSource") String dataSource,
            @Param("sort") String sort);

    /**
     * 查询水质统计数据
     */
    Map<String, Object> selectWaterQualityStatistics(@Param("stationId") Long stationId, @Param("timeRange") String timeRange);

    /**
     * 查询水质图表数据（按监测项目分组）
     */
    List<Map<String, Object>> selectWaterQualityChartData(
            @Param("stationId") Long stationId,
            @Param("monitoringItemCode") String monitoringItemCode,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("interval") String interval);

    /**
     * 查询所有站点的水质图表数据（汇总）
     */
    List<Map<String, Object>> selectAllStationsWaterQualityChartData(
            @Param("monitoringItemCode") String monitoringItemCode,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("interval") String interval);

    /**
     * 查询水质监测概览统计数据
     */
    Map<String, Object> selectWaterQualityMonitoringOverview();

    /**
     * 查询数据质量统计
     */
    List<Map<String, Object>> selectWaterQualityDataQualityStatsByStation(@Param("timeRange") String timeRange);

    /**
     * 查询数据质量趋势数据
     */
    List<Map<String, Object>> selectWaterQualityDataQualityTrend(@Param("timeRange") String timeRange);

    /**
     * 查询监测项目统计数据
     */
    List<Map<String, Object>> selectMonitoringItemStats(@Param("stationId") Long stationId, @Param("timeRange") String timeRange);

    /**
     * 查询最新监测数据（按监测项目）
     */
    List<Map<String, Object>> selectLatestWaterQualityData(@Param("stationId") Long stationId);

    /**
     * 根据站点ID和时间查询数据是否存在（用于导入去重）
     */
    int countByStationIdAndTime(
            @Param("stationId") Long stationId,
            @Param("monitoringTime") LocalDateTime monitoringTime);

    /**
     * 批量插入水质监测数据
     */
    int insertBatch(@Param("dataList") List<WaterQualityMonitoringData> dataList);

    /**
     * 查询异常数据统计
     */
    Map<String, Object> selectAbnormalDataStats(@Param("timeRange") String timeRange);

    /**
     * 查询监测项目趋势数据
     */
    List<Map<String, Object>> selectMonitoringItemTrend(
            @Param("stationId") Long stationId,
            @Param("monitoringItemCode") String monitoringItemCode,
            @Param("timeRange") String timeRange);
}
