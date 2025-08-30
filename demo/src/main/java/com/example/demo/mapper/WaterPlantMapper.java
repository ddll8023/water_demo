package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.pojo.entity.facility.WaterPlant;
import com.github.pagehelper.Page;
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
     * 分页查询水厂列表（基础信息）- 用于PageHelper分页
     */
    Page<WaterPlant> selectWaterPlantPageByKeyword(@Param("keyword") String keyword);

    /**
     * 根据ID查询水厂基础信息
     */
    WaterPlant selectById(@Param("id") Long id);

    /**
     * 查询部门名称
     */
    String selectDepartmentNameByWaterPlantId(@Param("waterPlantId") Long waterPlantId);

    /**
     * 查询负责人姓名
     */
    String selectManagerNameByWaterPlantId(@Param("waterPlantId") Long waterPlantId);

    /**
     * 查询负责人电话
     */
    String selectManagerPhoneByWaterPlantId(@Param("waterPlantId") Long waterPlantId);

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
