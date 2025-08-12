package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.dto.common.PageResponseDTO;
import com.example.demo.dto.facility.*;
import com.example.demo.entity.facility.PumpingStation;
import com.example.demo.mapper.PumpingStationMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 泵站服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PumpingStationService extends ServiceImpl<PumpingStationMapper, PumpingStation> {

    private final PumpingStationMapper pumpingStationMapper;

    /**
     * 分页查询泵站列表
     */
    public PageResponseDTO<PumpingStationResponseDTO> getPumpingStationPage(
            int page, 
            int size, 
            String keyword, 
            String name, 
            String stationType, 
            String waterProject, 
            String operationMode) {
        
        // 参数验证
        if (page < 1) {
            page = 1;
        }
        if (size < 1 || size > 100) {
            size = 10;
        }
        
        // 使用PageHelper进行分页
        PageHelper.startPage(page, size);
        List<PumpingStationResponseDTO> pumpingStations = pumpingStationMapper.selectPumpingStationPage(
                keyword,
                name,
                stationType,
                waterProject,
                operationMode
        );
        
        // 使用PageInfo包装查询结果
        PageInfo<PumpingStationResponseDTO> pageInfo = new PageInfo<>(pumpingStations);

        return new PageResponseDTO<>(
                pageInfo.getList(),
                (int) pageInfo.getTotal(),
                page,
                size
        );
    }

    /**
     * 根据ID查询泵站详情
     */
    public PumpingStationResponseDTO getPumpingStationById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("泵站ID不能为空");
        }

        PumpingStationResponseDTO pumpingStation = pumpingStationMapper.selectPumpingStationById(id);
        if (pumpingStation == null) {
            throw new RuntimeException("泵站不存在");
        }
        return pumpingStation;
    }

    /**
     * 创建泵站
     */
    @Transactional
    public PumpingStationResponseDTO createPumpingStation(PumpingStationCreateDTO createDTO) {
        // 验证泵站名称是否已存在
        if (StringUtils.hasText(createDTO.getName()) && 
            pumpingStationMapper.existsByName(createDTO.getName(), null)) {
            throw new RuntimeException("泵站名称已存在");
        }

        // 验证泵站编码是否已存在
        if (StringUtils.hasText(createDTO.getStationCode()) && 
            pumpingStationMapper.existsByStationCode(createDTO.getStationCode(), null)) {
            throw new RuntimeException("泵站编码已存在");
        }

        PumpingStation pumpingStation = new PumpingStation();
        BeanUtils.copyProperties(createDTO, pumpingStation);
        pumpingStation.setCreatedAt(LocalDateTime.now());
        pumpingStation.setUpdatedAt(LocalDateTime.now());

        // 使用自定义实现替换 MyBatis-Plus 的 insert 方法
        pumpingStationMapper.insertPumpingStation(pumpingStation);
        log.info("创建泵站成功，泵站ID: {}, 泵站名称: {}", pumpingStation.getId(), pumpingStation.getName());

        return getPumpingStationById(pumpingStation.getId());
    }

    /**
     * 更新泵站信息
     */
    @Transactional
    public PumpingStationResponseDTO updatePumpingStation(PumpingStationUpdateDTO updateDTO) {
        if (updateDTO.getId() == null) {
            throw new IllegalArgumentException("泵站ID不能为空");
        }

        // 检查泵站是否存在
        PumpingStation existingPumpingStation = pumpingStationMapper.selectById(updateDTO.getId());
        if (existingPumpingStation == null) {
            throw new RuntimeException("泵站不存在");
        }

        // 验证泵站名称是否已存在（排除当前记录）
        if (StringUtils.hasText(updateDTO.getName()) && 
            pumpingStationMapper.existsByName(updateDTO.getName(), updateDTO.getId())) {
            throw new RuntimeException("泵站名称已存在");
        }

        // 验证泵站编码是否已存在（排除当前记录）
        if (StringUtils.hasText(updateDTO.getStationCode()) && 
            pumpingStationMapper.existsByStationCode(updateDTO.getStationCode(), updateDTO.getId())) {
            throw new RuntimeException("泵站编码已存在");
        }

        PumpingStation pumpingStation = new PumpingStation();
        BeanUtils.copyProperties(updateDTO, pumpingStation);
        pumpingStation.setUpdatedAt(LocalDateTime.now());

        // 使用自定义实现替换 MyBatis-Plus 的 updateById 方法
        pumpingStationMapper.updatePumpingStationById(pumpingStation);
        log.info("更新泵站成功，泵站ID: {}, 泵站名称: {}", pumpingStation.getId(), pumpingStation.getName());

        return getPumpingStationById(pumpingStation.getId());
    }

    /**
     * 删除泵站
     */
    @Transactional
    public void deletePumpingStation(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("泵站ID不能为空");
        }

        // 检查泵站是否存在
        PumpingStation existingPumpingStation = pumpingStationMapper.selectById(id);
        if (existingPumpingStation == null) {
            throw new RuntimeException("泵站不存在");
        }

        // 使用updatePumpingStationById方法实现软删除
        PumpingStation pumpingStation = new PumpingStation();
        pumpingStation.setId(id);
        pumpingStation.setDeletedAt(LocalDateTime.now());
        pumpingStationMapper.updatePumpingStationById(pumpingStation);

        log.info("删除泵站成功，泵站ID: {}, 泵站名称: {}", id, existingPumpingStation.getName());
    }

}
