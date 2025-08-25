package com.example.demo.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.example.demo.pojo.dto.common.PageResponseDTO;
import com.example.demo.pojo.dto.facility.PipelineCreateDTO;
import com.example.demo.pojo.dto.facility.PipelineQueryDTO;
import com.example.demo.pojo.dto.facility.PipelineResponseDTO;
import com.example.demo.pojo.dto.facility.PipelineUpdateDTO;
import com.example.demo.pojo.entity.facility.Pipeline;
import com.example.demo.mapper.PipelineMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 管道信息服务类
 */
@Service
@RequiredArgsConstructor
public class PipelineService {

    private final PipelineMapper pipelineMapper;

    /**
     * 分页查询管道信息列表
     */
    public PageResponseDTO<PipelineResponseDTO> getPipelinePage(PipelineQueryDTO queryDTO) {
        // 参数验证
        if (queryDTO.getPage() < 1) queryDTO.setPage(1);
        if (queryDTO.getSize() < 1) queryDTO.setSize(10);
        
        // 使用PageHelper开始分页
        PageHelper.startPage(queryDTO.getPage(), queryDTO.getSize());
        
        // 查询数据，不需要传入分页参数
        List<PipelineResponseDTO> pipelines = pipelineMapper.selectPipelinePage(
            queryDTO.getKeyword(),
            queryDTO.getPipelineType(),
            queryDTO.getMaterial(),
            queryDTO.getOperationStatus()
        );
        
        // 获取分页信息
        PageInfo<PipelineResponseDTO> pageInfo = new PageInfo<>(pipelines);

        return new PageResponseDTO<>(
            pageInfo.getList(),
            (int) pageInfo.getTotal(),
            pageInfo.getPageNum(),
            pageInfo.getPageSize()
        );
    }

    /**
     * 根据ID查询管道信息详情
     */
    public PipelineResponseDTO getPipelineById(Long id) {
        PipelineResponseDTO result = pipelineMapper.selectPipelineById(id);
        if (result == null) {
            throw new RuntimeException("管道信息不存在，ID: " + id);
        }
        return result;
    }

    /**
     * 创建管道信息
     */
    @Transactional
    public PipelineResponseDTO createPipeline(PipelineCreateDTO createDTO) {
        // 如果没有提供编码，则自动生成唯一编码
        if (createDTO.getPipelineCode() == null || createDTO.getPipelineCode().trim().isEmpty()) {
            createDTO.setPipelineCode(generateUniquePipelineCode());
        } else {
            // 如果提供了编码，检查是否已存在
            if (pipelineMapper.existsByPipelineCode(createDTO.getPipelineCode(), null)) {
                throw new RuntimeException("管道编码已存在: " + createDTO.getPipelineCode());
            }
        }

        // 创建实体对象
        Pipeline pipeline = new Pipeline();
        BeanUtils.copyProperties(createDTO, pipeline);

        // 保存到数据库
        int result = pipelineMapper.insert(pipeline);
        if (result <= 0) {
            throw new RuntimeException("创建管道信息失败");
        }

        // 返回创建结果
        return getPipelineById(pipeline.getId());
    }

    /**
     * 更新管道信息
     */
    @Transactional
    public PipelineResponseDTO updatePipeline(PipelineUpdateDTO updateDTO) {
        // 检查管道信息是否存在
        Pipeline existingPipeline = pipelineMapper.selectById(updateDTO.getId());
        if (existingPipeline == null) {
            throw new RuntimeException("管道信息不存在，ID: " + updateDTO.getId());
        }

        // 检查编码是否已被其他管道使用
        if (pipelineMapper.existsByPipelineCode(updateDTO.getPipelineCode(), updateDTO.getId())) {
            throw new RuntimeException("管道编码已存在: " + updateDTO.getPipelineCode());
        }

        // 更新实体对象
        Pipeline pipeline = new Pipeline();
        BeanUtils.copyProperties(updateDTO, pipeline);

        // 更新数据库
        int result = pipelineMapper.updateById(pipeline);
        if (result <= 0) {
            throw new RuntimeException("更新管道信息失败");
        }

        // 返回更新结果
        return getPipelineById(updateDTO.getId());
    }

    /**
     * 删除管道信息
     */
    @Transactional
    public void deletePipeline(Long id) {
        // 检查管道信息是否存在
        Pipeline existingPipeline = pipelineMapper.selectById(id);
        if (existingPipeline == null) {
            throw new RuntimeException("管道信息不存在，ID: " + id);
        }

        // 软删除
        int result = pipelineMapper.deleteById(id);
        if (result <= 0) {
            throw new RuntimeException("删除管道信息失败");
        }
    }

    /**
     * 批量删除管道信息
     */
    @Transactional
    public void batchDeletePipelines(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("删除ID列表不能为空");
        }

        for (Long id : ids) {
            deletePipeline(id);
        }
    }

    /**
     * 获取所有可用管道信息（用于下拉选择）
     */
    public List<Pipeline> getAvailablePipelines() {
        return pipelineMapper.selectAvailablePipelines();
    }

    /**
     * 统计管道信息总数
     */
    public long countTotal() {
        return pipelineMapper.countTotal();
    }

    /**
     * 生成唯一的管道编码
     */
    private String generateUniquePipelineCode() {
        String prefix = "PL";
        int maxAttempts = 100; // 最大尝试次数，避免无限循环

        for (int i = 0; i < maxAttempts; i++) {
            // 生成格式：PL + 当前时间戳后6位 + 3位随机数
            long timestamp = System.currentTimeMillis();
            String timestampSuffix = String.valueOf(timestamp).substring(7); // 取后6位
            int randomNum = (int) (Math.random() * 1000); // 0-999的随机数
            String code = String.format("%s%s%03d", prefix, timestampSuffix, randomNum);

            // 检查编码是否已存在
            if (!pipelineMapper.existsByPipelineCode(code, null)) {
                return code;
            }

            // 如果编码已存在，稍微延迟后重试
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // 如果多次尝试都失败，使用UUID作为后备方案
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return prefix + uuid;
    }
}
