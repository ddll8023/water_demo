package com.example.demo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.example.demo.dto.common.PageResponseDTO;
import com.example.demo.dto.facility.*;
import com.example.demo.entity.facility.MonitoringStation;
import com.example.demo.mapper.MonitoringStationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 监测站点服务类
 */
@Service
@RequiredArgsConstructor
public class MonitoringStationService {

    private final MonitoringStationMapper monitoringStationMapper;

    /**
     * 分页查询监测站点列表
     */
    public PageResponseDTO<MonitoringStationResponseDTO> getMonitoringStationPage(MonitoringStationQueryDTO queryDTO) {
        // 参数验证
        int page = Math.max(queryDTO.getPage(), 1);
        int size = queryDTO.getSize() < 1 ? 10 : queryDTO.getSize();
        
        // 使用PageHelper开始分页
        PageHelper.startPage(page, size);
        
        // 查询数据，不需要传入分页参数
        List<MonitoringStationResponseDTO> stationList = monitoringStationMapper.selectMonitoringStationPage(
            queryDTO.getKeyword(),
            queryDTO.getMonitoringType()
        );
        
        // 获取分页信息
        PageInfo<MonitoringStationResponseDTO> pageInfo = new PageInfo<>(stationList);
        
        return new PageResponseDTO<>(
            pageInfo.getList(),
            (int) pageInfo.getTotal(),
            pageInfo.getPageNum(),
            pageInfo.getPageSize()
        );
    }

    /**
     * 根据ID查询监测站点详情
     */
    public MonitoringStationResponseDTO getMonitoringStationById(Long id) {
        MonitoringStationResponseDTO result = monitoringStationMapper.selectMonitoringStationById(id);
        if (result == null) {
            throw new RuntimeException("监测站点不存在");
        }
        return result;
    }

    /**
     * 创建监测站点
     */
    @Transactional
    public MonitoringStation createMonitoringStation(MonitoringStationCreateDTO createDTO) {
        // 检查编码是否已存在
        if (monitoringStationMapper.existsByStationCode(createDTO.getStationCode(), null)) {
            throw new RuntimeException("监测站点编码已存在");
        }

        MonitoringStation monitoringStation = new MonitoringStation();
        BeanUtils.copyProperties(createDTO, monitoringStation);
        
        monitoringStationMapper.insert(monitoringStation);
        return monitoringStation;
    }

    /**
     * 更新监测站点信息
     */
    @Transactional
    public MonitoringStation updateMonitoringStation(Long id, MonitoringStationUpdateDTO updateDTO) {
        MonitoringStation existingStation = monitoringStationMapper.selectById(id);
        if (existingStation == null) {
            throw new RuntimeException("监测站点不存在");
        }

        // 检查编码是否已被其他记录使用
        if (monitoringStationMapper.existsByStationCode(updateDTO.getStationCode(), id)) {
            throw new RuntimeException("监测站点编码已存在");
        }

        BeanUtils.copyProperties(updateDTO, existingStation, "id");
        monitoringStationMapper.updateById(existingStation);
        return existingStation;
    }

    /**
     * 删除监测站点
     */
    @Transactional
    public void deleteMonitoringStation(Long id) {
        MonitoringStation existingStation = monitoringStationMapper.selectById(id);
        if (existingStation == null) {
            throw new RuntimeException("监测站点不存在");
        }
        monitoringStationMapper.deleteById(id);
    }

    /**
     * 批量删除监测站点
     */
    @Transactional
    public void batchDeleteMonitoringStations(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("删除ID列表不能为空");
        }
        monitoringStationMapper.deleteBatchIds(ids);
    }

    /**
     * 获取所有可用的监测站点（用于下拉选择）
     */
    public List<MonitoringStation> getAvailableMonitoringStations() {
        return monitoringStationMapper.selectAvailableMonitoringStations();
    }

    /**
     * 根据监测类型获取可用的监测站点（用于下拉选择）
     */
    public List<MonitoringStation> getAvailableMonitoringStationsByType(String monitoringItemCode) {
        return monitoringStationMapper.selectAvailableMonitoringStationsByType(monitoringItemCode);
    }

    /**
     * 统计监测站点总数
     */
    public long countTotal() {
        return monitoringStationMapper.countTotal();
    }
}
