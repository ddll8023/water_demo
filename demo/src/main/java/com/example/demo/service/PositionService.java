package com.example.demo.service;

import com.example.demo.pojo.dto.system.PositionCreateDTO;
import com.example.demo.pojo.dto.system.PositionResponseDTO;
import com.example.demo.pojo.dto.system.PositionUpdateDTO;
import com.example.demo.pojo.dto.common.PageResponseDTO;
import com.example.demo.pojo.entity.system.Position;
import com.example.demo.mapper.PositionMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 岗位服务实现类
 * 专注于岗位定义和管理，不参与权限控制
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PositionService {

    private final PositionMapper positionMapper;

    /**
     * 分页查询岗位列表
     */
    public PageResponseDTO<PositionResponseDTO> getPositionPage(int page, int size, String keyword) {
        // 参数验证
        if (page < 1) page = 1;
        if (size < 1 || size > 100) size = 10;

        // 使用PageHelper进行分页
        PageHelper.startPage(page, size);
        List<Position> positions = positionMapper.selectPositionPageWithPersonnelCount(keyword);
        PageInfo<Position> pageInfo = new PageInfo<>(positions);

        // 转换为DTO
        List<PositionResponseDTO> records = positions.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());

        return new PageResponseDTO<>(records, (int) pageInfo.getTotal(), page, size);
    }

    /**
     * 根据ID获取岗位详情
     */
    public PositionResponseDTO getPositionById(Long id) {
        Position position = positionMapper.selectPositionById(id);
        if (position == null) {
            throw new RuntimeException("岗位不存在");
        }
        return convertToResponseDTO(position);
    }

    /**
     * 创建岗位
     */
    @Transactional
    public PositionResponseDTO createPosition(PositionCreateDTO createDTO) {
        // 检查岗位名称是否已存在
        if (positionMapper.countByName(createDTO.getName(), null) > 0) {
            throw new RuntimeException("岗位名称已存在");
        }

        Position position = new Position();
        BeanUtils.copyProperties(createDTO, position);
        position.setCreatedAt(LocalDateTime.now());
        position.setUpdatedAt(LocalDateTime.now());

        positionMapper.insert(position);
        log.info("创建岗位成功，岗位ID: {}, 岗位名称: {}", 
                position.getId(), position.getName());

        return getPositionById(position.getId());
    }

    /**
     * 更新岗位
     */
    @Transactional
    public PositionResponseDTO updatePosition(Long id, PositionUpdateDTO updateDTO) {
        Position position = positionMapper.selectById(id);
        if (position == null) {
            throw new RuntimeException("岗位不存在");
        }

        // 检查岗位名称是否已存在（排除当前岗位）
        if (updateDTO.getName() != null && 
            positionMapper.countByName(updateDTO.getName(), id) > 0) {
            throw new RuntimeException("岗位名称已存在");
        }

        BeanUtils.copyProperties(updateDTO, position);
        position.setUpdatedAt(LocalDateTime.now());

        positionMapper.updateById(position);
        log.info("更新岗位成功，岗位ID: {}, 岗位名称: {}", position.getId(), position.getName());

        return getPositionById(id);
    }

    /**
     * 删除岗位
     */
    @Transactional
    public void deletePosition(Long id) {
        Position position = positionMapper.selectById(id);
        if (position == null) {
            throw new RuntimeException("岗位不存在");
        }

        // 检查是否有人员关联此岗位
        List<Object> personnel = positionMapper.selectPersonnelByPositionId(id);
        if (!personnel.isEmpty()) {
            throw new RuntimeException("该岗位下还有人员，无法删除");
        }

        // 软删除
        position.setDeletedAt(LocalDateTime.now());
        positionMapper.updateById(position);
        log.info("删除岗位成功，岗位ID: {}, 岗位名称: {}", position.getId(), position.getName());
    }

    /**
     * 获取岗位下的人员列表
     */
    public List<Object> getPositionPersonnel(Long positionId) {
        return positionMapper.selectPersonnelByPositionId(positionId);
    }

    /**
     * 检查岗位名称是否可用
     */
    public boolean checkNameAvailable(String name, Long excludeId) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        int count = positionMapper.countByName(name.trim(), excludeId);
        return count == 0;
    }

    /**
     * 转换为响应DTO
     */
    private PositionResponseDTO convertToResponseDTO(Position position) {
        PositionResponseDTO dto = new PositionResponseDTO();
        BeanUtils.copyProperties(position, dto);
        return dto;
    }
}
