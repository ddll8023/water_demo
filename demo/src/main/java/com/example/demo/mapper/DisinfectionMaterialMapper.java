package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.pojo.DTO.facility.DisinfectionMaterialResponseDTO;
import com.example.demo.pojo.entity.facility.DisinfectionMaterial;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 消毒药材Mapper接口
 * 提供消毒药材相关的数据访问操作
 */
@Mapper
public interface DisinfectionMaterialMapper extends BaseMapper<DisinfectionMaterial> {

    /**
     * 分页查询消毒药材列表（基础信息）
     */
    Page<DisinfectionMaterial> selectDisinfectionMaterialPage(
            @Param("keyword") String keyword
    );

    /**
     * 根据ID查询消毒药材详情（包含关联信息）
     */
    DisinfectionMaterialResponseDTO selectDisinfectionMaterialById(@Param("id") Long id);

    /**
     * 根据消毒药材ID查询水厂名称
     */
    String selectWaterPlantNameByMaterialId(@Param("materialId") Long materialId);

    /**
     * 检查药材名称是否存在
     */
    boolean existsByName(@Param("name") String name,
                        @Param("excludeId") Long excludeId);

    /**
     * 获取所有可用的消毒药材（用于下拉选择）
     */
    List<DisinfectionMaterial> selectAvailableDisinfectionMaterials();

    /**
     * 统计消毒药材总数
     */
    long countTotal();

    /**
     * 统计库存不足的药材数量
     */
    long countLowStock();

    /**
     * 统计即将过期的药材数量
     */
    long countNearExpiry();
}
