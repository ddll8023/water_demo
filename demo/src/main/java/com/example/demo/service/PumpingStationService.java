package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.common.PageResult;
import com.example.demo.pojo.DTO.facility.PumpingStationCreateDTO;
import com.example.demo.pojo.DTO.facility.PumpingStationQueryDTO;
import com.example.demo.pojo.DTO.facility.PumpingStationResponseDTO;
import com.example.demo.pojo.DTO.facility.PumpingStationUpdateDTO;
import com.example.demo.pojo.VO.PumpingStationVO;
import com.example.demo.pojo.entity.facility.PumpingStation;
import com.example.demo.mapper.PumpingStationMapper;
import com.example.demo.service.base.FacilityService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
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
public class PumpingStationService extends ServiceImpl<PumpingStationMapper, PumpingStation> 
    implements FacilityService<PumpingStation, PumpingStationResponseDTO, PumpingStationQueryDTO, PumpingStationCreateDTO, PumpingStationUpdateDTO, PumpingStationVO> {

    private final PumpingStationMapper pumpingStationMapper;

    /**
     * 分页查询泵站列表
     * 实现统一接口方法：queryPage，返回VO格式
     */
    @Override
    public PageResult<PumpingStationVO> queryPage(PumpingStationQueryDTO pumpingStationQueryDTO) {

        // 使用PageHelper进行分页
        PageHelper.startPage(pumpingStationQueryDTO.getPage(), pumpingStationQueryDTO.getSize());
        Page<PumpingStation> pumpingStations = pumpingStationMapper.selectPumpingStationPage(
                pumpingStationQueryDTO.getKeyword()
        );
        
        // 获取分页信息
        long total = pumpingStations.getTotal();
        List<PumpingStation> records = pumpingStations.getResult();

        // 转换为VO格式并补充关联信息
        List<PumpingStationVO> voList = records.stream()
            .map(this::convertToVO)
            .collect(java.util.stream.Collectors.toList());

        return new PageResult<>(
                total,
                voList
        );
    }

    /**
     * 根据ID查询泵站详情
     * 实现统一接口方法：queryById
     */
    @Override
    public PumpingStationResponseDTO queryById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("泵站ID不能为空");
        }

        PumpingStation pumpingStation = pumpingStationMapper.selectPumpingStationById(id);
        if (pumpingStation == null) {
            throw new RuntimeException("泵站不存在");
        }
        
        // 转换为ResponseDTO并补充关联信息
        PumpingStationResponseDTO responseDTO = new PumpingStationResponseDTO();
        BeanUtils.copyProperties(pumpingStation, responseDTO);
        
        // 查询并设置运行模式名称
        if (pumpingStation.getOperationMode() != null) {
            String operationModeName = pumpingStationMapper.selectOperationModeNameByStationId(pumpingStation.getId());
            responseDTO.setOperationModeName(operationModeName);
        }
        
        return responseDTO;
    }

    /**
     * 创建泵站
     * 实现统一接口方法：create
     */
    @Override
    @Transactional
    public PumpingStationResponseDTO create(PumpingStationCreateDTO createDTO) {
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

        return queryById(pumpingStation.getId());
    }

    /**
     * 更新泵站信息
     * 实现统一接口方法：update
     */
    @Override
    @Transactional
    public PumpingStationResponseDTO update(PumpingStationUpdateDTO updateDTO) {
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

        return queryById(pumpingStation.getId());
    }

    /**
     * 删除泵站
     * 实现统一接口方法：delete
     */
    @Override
    @Transactional
    public void delete(Long id) {
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

    /**
     * 获取可用泵站列表
     * 实现统一接口方法：queryAvailable
     */
    @Override
    public List<PumpingStationVO> queryAvailable() {
        List<PumpingStation> pumpingStations = list(); // 使用 MyBatis-Plus 的 list 方法获取所有记录
        return pumpingStations.stream().map(this::convertToVO).collect(java.util.stream.Collectors.toList());
    }

    /**
     * 统计泵站总数
     * 实现统一接口方法：countTotal
     */
    @Override
    public long countTotal() {
        return count(); // 使用 MyBatis-Plus 的 count 方法
    }

    /**
     * 将PumpingStation实体转换为VO，并补充关联信息
     */
    private PumpingStationVO convertToVO(PumpingStation pumpingStation) {
        PumpingStationVO vo = new PumpingStationVO();
        BeanUtils.copyProperties(pumpingStation, vo);
        
        // 查询并设置运行模式名称
        if (pumpingStation.getOperationMode() != null) {
            String operationModeName = pumpingStationMapper.selectOperationModeNameByStationId(pumpingStation.getId());
            vo.setOperationModeLabel(operationModeName);
        }
        
        return vo;
    }

}
