package com.example.demo.service;

import com.example.demo.pojo.DTO.facility.MonitoringStationCreateDTO;
import com.example.demo.pojo.DTO.facility.MonitoringStationQueryDTO;
import com.example.demo.pojo.DTO.facility.MonitoringStationResponseDTO;
import com.example.demo.pojo.DTO.facility.MonitoringStationUpdateDTO;
import com.example.demo.pojo.VO.MonitoringStationVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.example.demo.common.PageResult;
import com.example.demo.pojo.entity.facility.MonitoringStation;
import com.example.demo.mapper.MonitoringStationMapper;
import com.example.demo.service.base.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 监测站点服务类
 */
@Service
@RequiredArgsConstructor
public class MonitoringStationService implements FacilityService<MonitoringStation, MonitoringStationResponseDTO, MonitoringStationQueryDTO, MonitoringStationCreateDTO, MonitoringStationUpdateDTO, MonitoringStationVO> {

    private final MonitoringStationMapper monitoringStationMapper;

    /**
     * 分页查询监测站点列表
     * 实现统一接口方法：queryPage，返回VO格式
     */
    @Override
    public PageResult<MonitoringStationVO> queryPage(MonitoringStationQueryDTO monitoringStationQueryDTO) {
        // 参数验证
        int page = Math.max(monitoringStationQueryDTO.getPage(), 1);
        int size = monitoringStationQueryDTO.getSize() < 1 ? 10 : monitoringStationQueryDTO.getSize();
        
        // 使用PageHelper开始分页
        PageHelper.startPage(page, size);
        
        // 查询基础数据
        Page<MonitoringStation> pageResult = monitoringStationMapper.selectMonitoringStationPage(
            monitoringStationQueryDTO.getKeyword()
        );

        long total = pageResult.getTotal();
        List<MonitoringStation> records = pageResult.getResult();
        
        // 转换为VO并补充关联信息
        List<MonitoringStationVO> voList = records.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
        
        return new PageResult<>(total, voList);
    }

    /**
     * 根据ID查询监测站点详情
     * 实现统一接口方法：queryById
     */
    @Override
    public MonitoringStationResponseDTO queryById(Long id) {
        MonitoringStationResponseDTO result = monitoringStationMapper.selectMonitoringStationById(id);
        if (result == null) {
            throw new RuntimeException("监测站点不存在");
        }
        return result;
    }

    /**
     * 创建监测站点
     * 实现统一接口方法：create，统一返回ResponseDTO
     */
    @Override
    @Transactional
    public MonitoringStationResponseDTO create(MonitoringStationCreateDTO createDTO) {
        // 检查编码是否已存在
        if (monitoringStationMapper.existsByStationCode(createDTO.getStationCode(), null)) {
            throw new RuntimeException("监测站点编码已存在");
        }

        MonitoringStation monitoringStation = new MonitoringStation();
        BeanUtils.copyProperties(createDTO, monitoringStation);
        
        monitoringStationMapper.insert(monitoringStation);
        return queryById(monitoringStation.getId());
    }

    /**
     * 更新监测站点信息
     * 实现统一接口方法：update，统一方法签名和返回类型
     */
    @Override
    @Transactional
    public MonitoringStationResponseDTO update(MonitoringStationUpdateDTO updateDTO) {
        Long id = updateDTO.getId();
        if (id == null) {
            throw new IllegalArgumentException("监测站点ID不能为空");
        }
        
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
        return queryById(id);
    }

    /**
     * 删除监测站点
     * 实现统一接口方法：delete
     */
    @Override
    @Transactional
    public void delete(Long id) {
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
     * 实现统一接口方法：queryAvailable
     */
    @Override
    public List<MonitoringStationVO> queryAvailable() {
        List<MonitoringStation> stations = monitoringStationMapper.selectAvailableMonitoringStations();
        return stations.stream().map(this::convertToVO).collect(java.util.stream.Collectors.toList());
    }

    /**
     * 根据监测类型获取可用的监测站点（用于下拉选择）
     */
    public List<MonitoringStation> getAvailableMonitoringStationsByType(String monitoringItemCode) {
        return monitoringStationMapper.selectAvailableMonitoringStationsByType(monitoringItemCode);
    }

    /**
     * 统计监测站点总数
     * 实现统一接口方法：countTotal
     */
    @Override
    public long countTotal() {
        return monitoringStationMapper.countTotal();
    }

    /**
     * 将MonitoringStation实体转换为VO，并补充关联信息
     */
    private MonitoringStationVO convertToVO(MonitoringStation station) {
        MonitoringStationVO vo = new MonitoringStationVO();
        BeanUtils.copyProperties(station, vo);
        
        // 补充关联信息
        if (station.getMonitoringItemCode() != null) {
            String monitoringItemName = monitoringStationMapper.selectMonitoringItemNameById(station.getMonitoringItemCode());
            vo.setMonitoringItemName(monitoringItemName);
        }
        
        return vo;
    }
}
