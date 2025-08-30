package com.example.demo.service;

import com.example.demo.common.PageResult;
import com.example.demo.pojo.DTO.facility.FloatingBoatCreateDTO;
import com.example.demo.pojo.DTO.facility.FloatingBoatQueryDTO;
import com.example.demo.pojo.DTO.facility.FloatingBoatResponseDTO;
import com.example.demo.pojo.DTO.facility.FloatingBoatUpdateDTO;
import com.example.demo.pojo.VO.FloatingBoatVO;
import com.example.demo.pojo.entity.facility.FloatingBoat;
import com.example.demo.mapper.FloatingBoatMapper;
import com.example.demo.service.base.FacilityService;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import com.github.pagehelper.Page;

/**
 * 漂浮船只服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FloatingBoatService implements FacilityService<FloatingBoat, FloatingBoatResponseDTO, FloatingBoatQueryDTO, FloatingBoatCreateDTO, FloatingBoatUpdateDTO, FloatingBoatVO> {

    private final FloatingBoatMapper floatingBoatMapper;

    /**
     * 分页查询漂浮船只列表
     * 实现统一接口方法：queryPage
     */
    @Override
    public PageResult<FloatingBoatVO> queryPage(FloatingBoatQueryDTO floatingBoatQueryDTO) {
        PageHelper.startPage(floatingBoatQueryDTO.getPage(), floatingBoatQueryDTO.getSize());
        Page<FloatingBoat> floatingBoats = floatingBoatMapper.selectFloatingBoatPage(
                floatingBoatQueryDTO.getKeyword()
        );

        // 获取分页信息
        long total = floatingBoats.getTotal();
        List<FloatingBoat> records = floatingBoats.getResult();

        // 转换为VO格式并补充关联信息
        List<FloatingBoatVO> voList = records.stream()
            .map(this::convertToVO)
            .collect(java.util.stream.Collectors.toList());

        return new PageResult<>(
                total,
                voList
        );
    }

    /**
     * 根据ID查询漂浮船只详情
     * 实现统一接口方法：queryById
     */
    @Override
    public FloatingBoatResponseDTO queryById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("漂浮船只ID不能为空");
        }

        FloatingBoat floatingBoat = floatingBoatMapper.selectFloatingBoatById(id);
        if (floatingBoat == null) {
            throw new RuntimeException("漂浮船只不存在");
        }
        
        // 转换为ResponseDTO并补充关联信息
        FloatingBoatResponseDTO responseDTO = new FloatingBoatResponseDTO();
        BeanUtils.copyProperties(floatingBoat, responseDTO);
        
        // 查询并设置抽水状态名称
        if (floatingBoat.getPumpingStatus() != null) {
            String pumpingStatusName = floatingBoatMapper.selectPumpingStatusNameByBoatId(floatingBoat.getId());
            responseDTO.setPumpingStatusName(pumpingStatusName);
        }
        
        return responseDTO;
    }

    /**
     * 创建漂浮船只
     * 实现统一接口方法：create
     */
    @Override
    @Transactional
    public FloatingBoatResponseDTO create(FloatingBoatCreateDTO createDTO) {
        // 船只编码字段已移除，无需检查编码重复

        FloatingBoat floatingBoat = new FloatingBoat();
        BeanUtils.copyProperties(createDTO, floatingBoat);
        floatingBoat.setCreatedAt(LocalDateTime.now());
        floatingBoat.setUpdatedAt(LocalDateTime.now());

        floatingBoatMapper.insert(floatingBoat);
        log.info("创建漂浮船只成功，船只ID: {}, 船只名称: {}", floatingBoat.getId(), floatingBoat.getName());

        return queryById(floatingBoat.getId());
    }

    /**
     * 更新漂浮船只信息
     * 实现统一接口方法：update
     */
    @Override
    @Transactional
    public FloatingBoatResponseDTO update(FloatingBoatUpdateDTO updateDTO) {
        if (updateDTO.getId() == null) {
            throw new IllegalArgumentException("漂浮船只ID不能为空");
        }

        // 检查船只是否存在
        FloatingBoat existingBoat = floatingBoatMapper.selectById(updateDTO.getId());
        if (existingBoat == null) {
            throw new RuntimeException("漂浮船只不存在");
        }

        // 船只编码字段已移除，无需检查编码重复

        FloatingBoat floatingBoat = new FloatingBoat();
        BeanUtils.copyProperties(updateDTO, floatingBoat);
        floatingBoat.setUpdatedAt(LocalDateTime.now());

        floatingBoatMapper.updateById(floatingBoat);
        log.info("更新漂浮船只成功，船只ID: {}, 船只名称: {}", floatingBoat.getId(), floatingBoat.getName());

        return queryById(updateDTO.getId());
    }

    /**
     * 删除漂浮船只
     * 实现统一接口方法：delete
     */
    @Override
    @Transactional
    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("漂浮船只ID不能为空");
        }

        // 检查船只是否存在
        FloatingBoat existingBoat = floatingBoatMapper.selectById(id);
        if (existingBoat == null) {
            throw new RuntimeException("漂浮船只不存在");
        }

        // 软删除：设置删除时间
        FloatingBoat floatingBoat = new FloatingBoat();
        floatingBoat.setId(id);
        floatingBoat.setDeletedAt(LocalDateTime.now());
        floatingBoatMapper.updateById(floatingBoat);

        log.info("删除漂浮船只成功，船只ID: {}, 船只名称: {}", id, existingBoat.getName());
    }

    /**
     * 批量删除漂浮船只
     */
    @Transactional
    public void batchDeleteFloatingBoats(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("删除ID列表不能为空");
        }

        for (Long id : ids) {
            delete(id);
        }
    }

    /**
     * 获取可用漂浮船只列表（用于下拉选择）
     * 实现统一接口方法：queryAvailable
     */
    @Override
    public List<FloatingBoatVO> queryAvailable() {
        List<FloatingBoat> floatingBoats = floatingBoatMapper.selectAvailableFloatingBoats();
        return floatingBoats.stream().map(this::convertToVO).collect(java.util.stream.Collectors.toList());
    }

    /**
     * 统计漂浮船只总数
     * 实现统一接口方法：countTotal
     */
    @Override
    public long countTotal() {
        return floatingBoatMapper.countTotal();
    }

    /**
     * 将FloatingBoat实体转换为VO，并补充关联信息
     */
    private FloatingBoatVO convertToVO(FloatingBoat floatingBoat) {
        FloatingBoatVO vo = new FloatingBoatVO();
        BeanUtils.copyProperties(floatingBoat, vo);
        
        // 查询并设置抽水状态名称
        if (floatingBoat.getPumpingStatus() != null) {
            String pumpingStatusName = floatingBoatMapper.selectPumpingStatusNameByBoatId(floatingBoat.getId());
            vo.setPumpingStatusLabel(pumpingStatusName);
        }
        
        return vo;
    }
}
