package com.example.demo.mapper;

import com.example.demo.pojo.DTO.system.EngineeringServiceTabQueryDTO;
import com.example.demo.pojo.entity.system.EngineeringServiceTab;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工程信息服务Tab配置Mapper接口
 * 提供Tab配置相关的数据访问操作
 */
@Mapper
public interface EngineeringServiceTabMapper {

    /**
     * 分页查询Tab配置列表
     * 用于Tab配置列表页面显示完整信息
     * 支持PageHelper分页插件和多字段查询
     * <p>
     * 排序默认使用sortOrder升序
     */
    Page<EngineeringServiceTab> selectTabPageWithDetails(
            EngineeringServiceTabQueryDTO queryDTO);

    /**
     * 根据ID查询Tab配置
     */
    EngineeringServiceTab selectById(@Param("id") Long id);

    /**
     * 根据tabKey查询Tab配置
     */
    EngineeringServiceTab selectByTabKey(@Param("tabKey") String tabKey);

    /**
     * 检查tabKey是否已存在
     */
    int countByTabKey(@Param("tabKey") String tabKey);

    /**
     * 插入Tab配置记录
     */
    int insertTab(EngineeringServiceTab tab);

    /**
     * 根据ID更新Tab配置
     */
    int updateById(EngineeringServiceTab tab);

    /**
     * 根据ID删除Tab配置（软删除）
     */
    int deleteById(@Param("id") Long id);

    /**
     * 获取所有可见的Tab配置（用于工程服务页面）
     * 按sortOrder升序排序
     */
    List<EngineeringServiceTab> selectVisibleTabs();

    /**
     * 批量更新Tab配置的排序顺序
     */
    int batchUpdateSortOrder(@Param("tabs") List<EngineeringServiceTab> tabs);

    /**
     * 获取最大排序顺序值
     */
    Integer selectMaxSortOrder();

    /**
     * 根据tabKey更新显示状态
     */
    int updateVisibilityByTabKey(@Param("tabKey") String tabKey, @Param("isVisible") String isVisible);

} 