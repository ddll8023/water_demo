package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.dto.facility.WaterPlantResponseDTO;
import com.example.demo.entity.facility.WaterPlant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 水厂Mapper接口
 * 提供水厂相关的数据访问操作
 */
@Mapper
public interface WaterPlantMapper extends BaseMapper<WaterPlant> {

    /**
     * 分页查询水厂列表（包含关联信息）
     */
    List<WaterPlantResponseDTO> selectWaterPlantPage(@Param("keyword") String keyword);

    /**
     * 根据ID查询水厂详情（包含关联信息）
     */
    WaterPlantResponseDTO selectWaterPlantById(@Param("id") Long id);

    /**
     * 检查水厂编码是否存在
     */
    boolean existsByPlantCode(@Param("plantCode") String plantCode, @Param("excludeId") Long excludeId);

    /**
     * 检查水厂名称是否存在
     */
    boolean existsByName(@Param("name") String name, @Param("excludeId") Long excludeId);

    /**
     * 获取所有可用水厂（用于下拉选择）
     */
    List<WaterPlant> selectAvailableWaterPlants();

    /**
     * 统计水厂总数
     */
    long countTotal();

    /**
     * 根据部门ID统计水厂数量
     */
    long countByDepartmentId(@Param("departmentId") Long departmentId);

    /**
     * 根据负责人ID统计水厂数量
     */
    long countByManagerId(@Param("managerId") Long managerId);

    /**
     * 根据管理单位统计水厂数量
     */
    long countByManagementUnit(@Param("managementUnit") String managementUnit);
}
