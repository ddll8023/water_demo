package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.pojo.DTO.monitoring.WaterConditionMonitoringDataResponseDTO;
import com.example.demo.pojo.entity.monitoring.ReservoirMonitoringData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 水情监测数据Mapper接口
 */
@Mapper
public interface WaterConditionMonitoringDataMapper extends BaseMapper<ReservoirMonitoringData> {

    /**
     * 分页查询水情监测数据
     *
     * @param stationId 监测站点ID
     * @param stationName 监测站点名称
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param dataQuality 数据质量
     * @return 结果列表
     */
    List<WaterConditionMonitoringDataResponseDTO> pageWaterConditionMonitoringData(
            @Param("stationId") Long stationId,
            @Param("stationName") String stationName,
            @Param("startTime") String startTime,
            @Param("endTime") String endTime,
            @Param("dataQuality") Integer dataQuality);
}
