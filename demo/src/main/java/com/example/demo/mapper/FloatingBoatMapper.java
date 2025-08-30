package com.example.demo.mapper;

import com.example.demo.pojo.DTO.facility.FloatingBoatResponseDTO;
import com.example.demo.pojo.entity.facility.FloatingBoat;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 浮船信息Mapper接口
 * 提供浮船信息相关的数据访问操作
 */
@Mapper
public interface FloatingBoatMapper {

    /**
     * 分页查询浮船信息列表（基础信息）- 用于PageHelper分页
     */
    Page<FloatingBoat> selectFloatingBoatPage(
        @Param("keyword") String keyword
    );

    /**
     * 根据ID查询浮船信息详情（基础信息）
     */
    FloatingBoat selectFloatingBoatById(@Param("id") Long id);

    /**
     * 查询抽水状态名称
     */
    String selectPumpingStatusNameByBoatId(@Param("boatId") Long boatId);

    /**
     * 获取所有可用的浮船信息（用于下拉选择）
     */
    List<FloatingBoat> selectAvailableFloatingBoats();

    /**
     * 统计浮船信息总数
     */
    long countTotal();

    int insert(FloatingBoat floatingBoat);

    int updateById(FloatingBoat floatingBoat);

    FloatingBoat selectById(Long id);

    int deleteById(Long id);
}
