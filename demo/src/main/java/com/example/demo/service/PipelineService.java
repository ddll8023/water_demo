package com.example.demo.service;

import com.example.demo.common.PageResult;
import com.example.demo.pojo.DTO.facility.PipelineCreateDTO;
import com.example.demo.pojo.DTO.facility.PipelineQueryDTO;
import com.example.demo.pojo.DTO.facility.PipelineResponseDTO;
import com.example.demo.pojo.DTO.facility.PipelineUpdateDTO;
import com.example.demo.pojo.VO.PipelineVO;
import com.example.demo.pojo.entity.facility.Pipeline;
import com.example.demo.mapper.PipelineMapper;
import com.example.demo.service.base.FacilityService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管道服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PipelineService implements FacilityService<Pipeline, PipelineResponseDTO, PipelineQueryDTO, PipelineCreateDTO, PipelineUpdateDTO, PipelineVO> {

    private final PipelineMapper pipelineMapper;

    /**
     * 分页查询管道列表
     * 实现统一接口方法：queryPage
     */
    @Override
    public PageResult<PipelineVO> queryPage(PipelineQueryDTO queryDTO) {

        // 使用PageHelper进行分页
        PageHelper.startPage(queryDTO.getPage(), queryDTO.getSize());
        Page<Pipeline> pipelines = pipelineMapper.selectPipelinePage(
                queryDTO.getKeyword(),
                queryDTO.getPipelineType()
        );

        // 获取分页信息
        long total = pipelines.getTotal();
        List<Pipeline> records = pipelines.getResult();

        // 转换为VO格式并补充关联信息
        List<PipelineVO> voList = records.stream()
            .map(this::convertToVO)
            .collect(java.util.stream.Collectors.toList());

        return new PageResult<>(
                total,
                voList
        );
    }

    /**
     * 根据ID查询管道详情
     * 实现统一接口方法：queryById
     */
    @Override
    public PipelineResponseDTO queryById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("管道ID不能为空");
        }

        Pipeline pipeline = pipelineMapper.selectPipelineById(id);
        if (pipeline == null) {
            throw new RuntimeException("管道不存在");
        }
        
        // 转换为ResponseDTO并补充关联信息
        PipelineResponseDTO responseDTO = new PipelineResponseDTO();
        BeanUtils.copyProperties(pipeline, responseDTO);
        
        // 查询并设置管道类型名称
        if (pipeline.getPipelineType() != null) {
            String pipelineTypeName = pipelineMapper.selectPipelineTypeNameByPipelineId(pipeline.getId());
            responseDTO.setPipelineTypeName(pipelineTypeName);
        }
        
        return responseDTO;
    }

    /**
     * 创建管道
     * 实现统一接口方法：create
     */
    @Override
    @Transactional
    public PipelineResponseDTO create(PipelineCreateDTO createDTO) {
        // 验证管道编码是否已存在
        if (pipelineMapper.existsByPipelineCode(createDTO.getPipelineCode(), null)) {
            throw new RuntimeException("管道编码已存在");
        }

        Pipeline pipeline = new Pipeline();
        BeanUtils.copyProperties(createDTO, pipeline);
        pipeline.setCreatedAt(LocalDateTime.now());
        pipeline.setUpdatedAt(LocalDateTime.now());

        pipelineMapper.insert(pipeline);
        log.info("创建管道成功，管道ID: {}, 管道名称: {}", pipeline.getId(), pipeline.getName());

        return queryById(pipeline.getId());
    }

    /**
     * 更新管道信息
     * 实现统一接口方法：update
     */
    @Override
    @Transactional
    public PipelineResponseDTO update(PipelineUpdateDTO updateDTO) {
        if (updateDTO.getId() == null) {
            throw new IllegalArgumentException("管道ID不能为空");
        }

        // 检查管道是否存在
        Pipeline existingPipeline = pipelineMapper.selectById(updateDTO.getId());
        if (existingPipeline == null) {
            throw new RuntimeException("管道不存在");
        }

        // 验证管道编码是否已存在（排除当前记录）
        if (pipelineMapper.existsByPipelineCode(updateDTO.getPipelineCode(), updateDTO.getId())) {
            throw new RuntimeException("管道编码已存在");
        }

        Pipeline pipeline = new Pipeline();
        BeanUtils.copyProperties(updateDTO, pipeline);
        pipeline.setUpdatedAt(LocalDateTime.now());

        pipelineMapper.updateById(pipeline);
        log.info("更新管道成功，管道ID: {}, 管道名称: {}", pipeline.getId(), pipeline.getName());

        return queryById(updateDTO.getId());
    }

    /**
     * 删除管道
     * 实现统一接口方法：delete
     */
    @Override
    @Transactional
    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("管道ID不能为空");
        }

        // 检查管道是否存在
        Pipeline existingPipeline = pipelineMapper.selectById(id);
        if (existingPipeline == null) {
            throw new RuntimeException("管道不存在");
        }

        // 软删除：设置删除时间
        Pipeline pipeline = new Pipeline();
        pipeline.setId(id);
        pipeline.setDeletedAt(LocalDateTime.now());
        pipelineMapper.updateById(pipeline);

        log.info("删除管道成功，管道ID: {}, 管道名称: {}", id, existingPipeline.getName());
    }

    /**
     * 批量删除管道
     */
    @Transactional
    public void batchDeletePipelines(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("删除ID列表不能为空");
        }

        for (Long id : ids) {
            delete(id);
        }
    }

    /**
     * 获取可用管道列表（用于下拉选择）
     * 实现统一接口方法：queryAvailable
     */
    @Override
    public List<PipelineVO> queryAvailable() {
        List<Pipeline> pipelines = pipelineMapper.selectAvailablePipelines();
        return pipelines.stream().map(this::convertToVO).collect(java.util.stream.Collectors.toList());
    }

    /**
     * 统计管道总数
     * 实现统一接口方法：countTotal
     */
    @Override
    public long countTotal() {
        return pipelineMapper.countTotal();
    }

    /**
     * 将Pipeline实体转换为VO，并补充关联信息
     */
    private PipelineVO convertToVO(Pipeline pipeline) {
        PipelineVO vo = new PipelineVO();
        BeanUtils.copyProperties(pipeline, vo);
        
        // 查询并设置管道类型名称
        if (pipeline.getPipelineType() != null) {
            String pipelineTypeName = pipelineMapper.selectPipelineTypeNameByPipelineId(pipeline.getId());
            vo.setPipelineTypeLabel(pipelineTypeName);
        }
        
        return vo;
    }
}
