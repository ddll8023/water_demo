package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.warning.WarningThreshold;
import com.example.demo.dto.warning.WarningThresholdResponseDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 预警指标设定Mapper接口
 * 提供预警指标相关的数据访问操作
 */
@Mapper
public interface WarningThresholdMapper extends BaseMapper<WarningThreshold> {

    /**
     * 根据测点名称和监测项查询是否存在（排除指定ID）
     */
    int countByStationAndItemExcluding(@Param("stationName") String stationName,
                                      @Param("monitoringItem") String monitoringItem,
                                      @Param("excludeId") Long excludeId);

    /**
     * 分页查询预警指标列表（包含字典翻译）
     * 支持PageHelper分页插件和多字段查询
     */
    List<WarningThresholdResponseDTO> selectWarningThresholdPageWithDetails(
            @Param("keyword") String keyword,
            @Param("stationName") String stationName,
            @Param("monitoringItem") String monitoringItem,
            @Param("isActive") Boolean isActive,
            @Param("sort") String sort);

    /**
     * 根据ID查询预警指标详情（包含字典翻译）
     */
    WarningThresholdResponseDTO selectWarningThresholdDetailById(@Param("id") Long id);

    /**
     * 查询所有预警指标（不考虑是否启用）
     */
    List<WarningThreshold> selectAll();
    
    /**
     * 插入预警阈值
     */
    int insertWarningThreshold(WarningThreshold threshold);
    
    /**
     * 根据ID查询预警阈值
     */
    WarningThreshold selectWarningThresholdById(@Param("id") Long id);
    
    /**
     * 更新预警阈值
     */
    int updateWarningThreshold(WarningThreshold threshold);
}
