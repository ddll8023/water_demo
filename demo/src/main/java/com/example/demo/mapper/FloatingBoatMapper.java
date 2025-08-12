package com.example.demo.mapper;

import com.example.demo.dto.facility.FloatingBoatResponseDTO;
import com.example.demo.entity.facility.FloatingBoat;
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
     * 分页查询浮船信息列表（包含关联信息）
     */
    List<FloatingBoatResponseDTO> selectFloatingBoatPage(
        @Param("keyword") String keyword,
        @Param("pumpingStatus") String pumpingStatus
    );

    /**
     * 根据ID查询浮船信息详情（包含关联信息）
     */
    FloatingBoatResponseDTO selectFloatingBoatById(@Param("id") Long id);

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
