package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.pojo.dto.facility.VillageResponseDTO;
import com.example.demo.pojo.entity.facility.Village;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 村庄信息Mapper接口
 */
@Mapper
public interface VillageMapper extends BaseMapper<Village> {

    /**
     * 分页查询村庄信息列表
     * 已废弃，请使用 selectVillageList
     */
    @Deprecated
    IPage<VillageResponseDTO> selectVillagePage(
        IPage<VillageResponseDTO> page,
        @Param("keyword") String keyword
    );

    /**
     * 查询村庄信息列表
     * 配合 PageHelper 使用
     */
    List<VillageResponseDTO> selectVillageList(@Param("keyword") String keyword);

    /**
     * 根据ID查询村庄信息详情
     */
    VillageResponseDTO selectVillageById(@Param("id") Long id);

    /**
     * 检查村庄名称在同一行政区划下是否存在
     */
    boolean existsByNameAndAdministrativeCode(@Param("name") String name,
                                             @Param("administrativeCode") String administrativeCode,
                                             @Param("excludeId") Long excludeId);

    /**
     * 获取所有可用的村庄信息（用于下拉选择）
     */
    List<Village> selectAvailableVillages();

    /**
     * 统计村庄信息总数
     */
    long countTotal();
}
