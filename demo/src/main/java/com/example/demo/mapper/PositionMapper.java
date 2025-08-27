package com.example.demo.mapper;

import com.example.demo.pojo.entity.system.Position;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 岗位Mapper接口
 * 提供岗位相关的数据访问操作
 */
@Mapper
public interface PositionMapper {

    /**
     * 新增岗位
     */
    int insert(Position position);
    
    /**
     * 更新岗位
     */
    int updateById(Position position);
    
    /**
     * 根据ID查询岗位
     */
    Position selectById(@Param("id") Long id);
    
    /**
     * 分页查询岗位列表（包含人员统计）
     */
    Page<Position> selectPositionPageWithPersonnelCount(@Param("keyword") String keyword);

    /**
     * 检查岗位名称是否已存在
     */
    int countByName(@Param("name") String name, @Param("excludeId") Long excludeId);

    /**
     * 查询岗位下的人员数量
     */
    Integer selectPersonnelCountByPositionId(@Param("positionId") Long positionId);

}
