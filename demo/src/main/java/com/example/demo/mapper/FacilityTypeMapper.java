package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.pojo.entity.facility.FacilityType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 设施类型管理 Mapper 接口
 */
@Mapper
public interface FacilityTypeMapper extends BaseMapper<FacilityType> {

    /**
     * 根据排序查询设施类型
     *
     * @return 设施类型列表（按排序值升序）
     */
    List<FacilityType> selectActiveFacilityTypesSorted();

    /**
     * 根据API路径查询设施类型配置
     *
     * @param apiPath API路径标识
     * @return 设施类型配置
     */
    FacilityType selectByApiPath(@Param("apiPath") String apiPath);
} 