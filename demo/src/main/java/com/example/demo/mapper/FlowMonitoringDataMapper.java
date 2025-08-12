package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.dto.monitoring.FlowMonitoringDataResponseDTO;
import com.example.demo.entity.monitoring.FlowMonitoringData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.MapKey;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 流量监测数据Mapper接口
 * 提供流量监测数据相关的数据访问操作
 */
@Mapper
public interface FlowMonitoringDataMapper extends BaseMapper<FlowMonitoringData> {

    /**
     * 分页查询流量监测数据（包含关联信息）
     */
    List<FlowMonitoringDataResponseDTO> selectFlowMonitoringDataPage(
            @Param("stationId") Long stationId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("dataQuality") Integer dataQuality,
            @Param("collectionMethod") String collectionMethod,
            @Param("dataSource") String dataSource,
            @Param("sort") String sort);

    /**
     * 查询流量图表数据
     */
    @MapKey("time_label")
    List<Map<String, Object>> selectFlowChartData(
            @Param("stationId") Long stationId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("interval") String interval,
            @Param("dataType") String dataType);

    /**
     * 批量检查指定站点下多个时间点是否已存在记录
     * 返回所有匹配的记录，供调用方进一步处理
     * 
     * @param stationId 站点ID
     * @param times 时间点列表
     * @return 已存在的记录列表（含monitoring_time字段）
     */
    @MapKey("monitoring_time")
    List<Map<String, Object>> batchCountByStationIdAndTimes(@Param("stationId") Long stationId, @Param("times") List<LocalDateTime> times);
}
