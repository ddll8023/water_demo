package com.example.demo.mapper;

import com.example.demo.entity.system.Position;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
    Position selectById(Long id);
    
    /**
     * 分页查询岗位列表（包含人员统计）
     */
    List<Position> selectPositionPageWithPersonnelCount(@Param("keyword") String keyword);

    /**
     * 检查岗位名称是否已存在
     */
    int countByName(@Param("name") String name, @Param("excludeId") Long excludeId);

    /**
     * 获取岗位下的人员列表
     */
    List<Object> selectPersonnelByPositionId(@Param("positionId") Long positionId);

    /**
     * 根据岗位ID查询岗位详情
     */
    Position selectPositionById(@Param("positionId") Long positionId);

}
