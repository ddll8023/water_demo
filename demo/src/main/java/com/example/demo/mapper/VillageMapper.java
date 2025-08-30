package com.example.demo.mapper;

import com.example.demo.pojo.DTO.facility.VillageResponseDTO;
import com.example.demo.pojo.entity.facility.Village;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 村庄信息Mapper接口
 * 提供村庄信息相关的数据访问操作
 */
@Mapper
public interface VillageMapper {

    /**
     * 分页查询村庄列表（基础信息）- 用于PageHelper分页
     */
    Page<Village> selectVillagePageByKeyword(@Param("keyword") String keyword);

    /**
     * 查询村庄信息列表 - PageHelper 方式
     */
    List<VillageResponseDTO> selectVillageList(@Param("keyword") String keyword);

    /**
     * 根据ID查询村庄信息详情
     */
    VillageResponseDTO selectVillageById(@Param("id") Long id);

    /**
     * 检查村庄名称在同一行政区划下是否存在
     */
    boolean existsByNameAndAdministrativeCode(@Param("name") String name, @Param("administrativeCode") String administrativeCode, @Param("excludeId") Long excludeId);

    /**
     * 获取所有可用的村庄信息（用于下拉选择）
     */
    List<Village> selectAvailableVillages();

    /**
     * 统计村庄信息总数
     */
    long countTotal();

    int insert(Village village);

    int updateById(Village village);

    Village selectById(Long id);

    int deleteById(Long id);
}
