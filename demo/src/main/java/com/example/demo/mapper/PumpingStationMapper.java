package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.pojo.entity.facility.PumpingStation;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 泵站Mapper接口
 * 提供泵站相关的数据访问操作
 */
@Mapper
public interface PumpingStationMapper extends BaseMapper<PumpingStation> {

    /**
     * 分页查询泵站列表（基础信息）- 用于PageHelper分页
     */
    Page<PumpingStation> selectPumpingStationPage(@Param("keyword") String keyword);

    /**
     * 查询运行模式名称
     */
    String selectOperationModeNameByStationId(@Param("stationId") Long stationId);

    /**
     * 根据ID查询泵站详情（基础信息）
     */
    PumpingStation selectPumpingStationById(@Param("id") Long id);

    /**
     * 检查泵站编码是否存在
     */
    boolean existsByStationCode(@Param("stationCode") String stationCode, @Param("excludeId") Long excludeId);

    /**
     * 检查泵站名称是否存在
     */
    boolean existsByName(@Param("name") String name, @Param("excludeId") Long excludeId);

    /**
     * 插入泵站记录
     */
    int insertPumpingStation(PumpingStation pumpingStation);
    
    /**
     * 根据ID更新泵站信息
     */
    int updatePumpingStationById(PumpingStation pumpingStation);
    
    /**
     * 根据ID查询泵站实体
     */
    PumpingStation selectById(@Param("id") Long id);

}
