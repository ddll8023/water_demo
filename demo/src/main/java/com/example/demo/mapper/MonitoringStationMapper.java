package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.pojo.dto.facility.MonitoringStationResponseDTO;
import com.example.demo.pojo.entity.facility.MonitoringStation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 监测站点Mapper接口
 */
@Mapper
public interface MonitoringStationMapper extends BaseMapper<MonitoringStation> {

    /**
     * 分页查询监测站点列表（包含关联信息）
     */
    List<MonitoringStationResponseDTO> selectMonitoringStationPage(
            @Param("keyword") String keyword,
            @Param("monitoringType") String monitoringType
    );

    /**
     * 根据ID查询监测站点详情（包含关联信息）
     */
    MonitoringStationResponseDTO selectMonitoringStationById(@Param("id") Long id);

    /**
     * 检查监测站点编码是否存在
     */
    boolean existsByStationCode(@Param("stationCode") String stationCode,
                               @Param("excludeId") Long excludeId);

    /**
     * 获取所有可用的监测站点（用于下拉选择）
     */
    List<MonitoringStation> selectAvailableMonitoringStations();

    /**
     * 根据监测类型获取可用的监测站点（用于下拉选择）
     */
    List<MonitoringStation> selectAvailableMonitoringStationsByType(@Param("monitoringItemCode") String monitoringItemCode);

    /**
     * 统计监测站点总数
     */
    long countTotal();

    /**
     * 根据站码列表查询监测站点信息
     */
    List<Map<String, Object>> selectByStationCodes(@Param("stationCodes") List<String> stationCodes);

    /**
     * 根据站码查询站点ID（未删除）
     */
    Long selectIdByStationCode(@Param("stationCode") String stationCode);

    /**
     * 根据站名与监测项目查询站点ID（未删除）
     */
    Long selectIdByNameAndItem(@Param("name") String name, @Param("monitoringItemCode") String monitoringItemCode);
}