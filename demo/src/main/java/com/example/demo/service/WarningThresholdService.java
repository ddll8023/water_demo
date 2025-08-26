package com.example.demo.service;


import com.example.demo.common.PageResult;
import com.example.demo.pojo.DTO.warning.WarningThresholdCreateDTO;
import com.example.demo.pojo.DTO.warning.WarningThresholdResponseDTO;
import com.example.demo.pojo.DTO.warning.WarningThresholdUpdateDTO;
import com.example.demo.pojo.entity.warning.WarningThreshold;
import com.example.demo.mapper.WarningThresholdMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 预警指标设定管理服务
 * 提供预警指标的CRUD操作和业务逻辑处理
 */
@Service
@RequiredArgsConstructor
public class WarningThresholdService {

    private final WarningThresholdMapper warningThresholdMapper;

    /**
     * 分页查询预警指标列表
     */
    public PageResult<WarningThresholdResponseDTO> getWarningThresholds(int page, int size, String keyword,
                                                                        String stationName, String monitoringItem, String sort) {
        // 参数验证
        if (page < 1) page = 1;
        if (size < 1) size = 10;

        // 使用PageHelper开始分页
        PageHelper.startPage(page, size);

        // 使用分页查询
        List<WarningThresholdResponseDTO> warningThresholds = warningThresholdMapper.selectWarningThresholdPageWithDetails(
            keyword, stationName, monitoringItem, null, sort);

        // 获取分页信息
        PageInfo<WarningThresholdResponseDTO> pageInfo = new PageInfo<>(warningThresholds);

        return new PageResult<>(
            warningThresholds,
            (int) pageInfo.getTotal(),
            page,
            size
        );
    }

    /**
     * 根据ID查询预警指标详情
     */
    public WarningThresholdResponseDTO getWarningThresholdById(Long id) {
        WarningThresholdResponseDTO warningThreshold = warningThresholdMapper.selectWarningThresholdDetailById(id);
        if (warningThreshold == null) {
            throw new RuntimeException("预警指标不存在");
        }
        return warningThreshold;
    }

    /**
     * 创建预警指标
     */
    @Transactional
    public WarningThresholdResponseDTO createWarningThreshold(WarningThresholdCreateDTO createDTO) {
        // 检查测点名称和监测项组合是否已存在
        if (warningThresholdMapper.countByStationAndItemExcluding(
                createDTO.getStationName(), createDTO.getMonitoringItem(), null) > 0) {
            throw new RuntimeException("该测点的该监测项预警指标已存在");
        }

        WarningThreshold warningThreshold = new WarningThreshold();
        BeanUtils.copyProperties(createDTO, warningThreshold);
        warningThreshold.setCreatedAt(LocalDateTime.now());
        warningThreshold.setUpdatedAt(LocalDateTime.now());

        warningThresholdMapper.insertWarningThreshold(warningThreshold);
        return getWarningThresholdById(warningThreshold.getId());
    }

    /**
     * 更新预警指标
     */
    @Transactional
    public WarningThresholdResponseDTO updateWarningThreshold(WarningThresholdUpdateDTO updateDTO) {
        // 检查预警指标是否存在
        WarningThreshold existingWarningThreshold = warningThresholdMapper.selectWarningThresholdById(updateDTO.getId());
        if (existingWarningThreshold == null) {
            throw new RuntimeException("预警指标不存在");
        }

        // 检查测点名称和监测项组合是否已存在（排除当前记录）
        if (warningThresholdMapper.countByStationAndItemExcluding(
                updateDTO.getStationName(), updateDTO.getMonitoringItem(), updateDTO.getId()) > 0) {
            throw new RuntimeException("该测点的该监测项预警指标已存在");
        }

        WarningThreshold warningThreshold = new WarningThreshold();
        BeanUtils.copyProperties(updateDTO, warningThreshold);
        warningThreshold.setUpdatedAt(LocalDateTime.now());

        warningThresholdMapper.updateWarningThreshold(warningThreshold);
        return getWarningThresholdById(warningThreshold.getId());
    }

    /**
     * 删除预警指标
     */
    @Transactional
    public boolean deleteWarningThreshold(Long id) {
        // 检查预警指标是否存在
        WarningThreshold warningThreshold = warningThresholdMapper.selectWarningThresholdById(id);
        if (warningThreshold == null) {
            throw new RuntimeException("预警指标不存在");
        }

        // 软删除预警指标
        warningThreshold.setDeletedAt(LocalDateTime.now());
        warningThreshold.setUpdatedAt(LocalDateTime.now());
        return warningThresholdMapper.updateWarningThreshold(warningThreshold) > 0;
    }
    

    /**
 * 查询所有预警指标
 */
public List<WarningThresholdResponseDTO> getAllActiveWarningThresholds() {
    List<WarningThreshold> warningThresholds = warningThresholdMapper.selectAll();
    return warningThresholds.stream()
        .map(this::convertToResponseDTO)
        .collect(Collectors.toList());
}

    /**
     * 查询所有预警指标
     */
    public List<WarningThresholdResponseDTO> getAllWarningThresholds() {
        List<WarningThreshold> warningThresholds = warningThresholdMapper.selectAll();
        return warningThresholds.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
    }

    /**
     * 检查测点和监测项组合是否存在
     */
    public boolean checkStationAndItemExists(String stationName, String monitoringItem, Long excludeId) {
        return warningThresholdMapper.countByStationAndItemExcluding(stationName, monitoringItem, excludeId) > 0;
    }

    /**
     * 转换为响应DTO
     */
    private WarningThresholdResponseDTO convertToResponseDTO(WarningThreshold warningThreshold) {
        WarningThresholdResponseDTO dto = new WarningThresholdResponseDTO();
        BeanUtils.copyProperties(warningThreshold, dto);
        return dto;
    }
}
