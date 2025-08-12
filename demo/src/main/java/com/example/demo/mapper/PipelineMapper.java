package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.dto.facility.PipelineResponseDTO;
import com.example.demo.entity.facility.Pipeline;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 管道信息Mapper接口
 * 提供管道信息相关的数据访问操作
 */
@Mapper
public interface PipelineMapper extends BaseMapper<Pipeline> {

    /**
     * 分页查询管道信息列表（包含关联信息）
     */
    List<PipelineResponseDTO> selectPipelinePage(
        @Param("keyword") String keyword,
        @Param("pipelineType") String pipelineType,
        @Param("material") String material,
        @Param("operationStatus") String operationStatus
    );

    /**
     * 根据ID查询管道信息详情（包含关联信息）
     */
    PipelineResponseDTO selectPipelineById(@Param("id") Long id);

    /**
     * 检查管道编码是否存在
     */
    boolean existsByPipelineCode(@Param("pipelineCode") String pipelineCode,
                                @Param("excludeId") Long excludeId);

    /**
     * 获取所有可用的管道信息（用于下拉选择）
     */
    List<Pipeline> selectAvailablePipelines();

    /**
     * 统计管道信息总数
     */
    long countTotal();
}
