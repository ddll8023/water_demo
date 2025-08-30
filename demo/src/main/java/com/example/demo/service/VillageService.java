package com.example.demo.service;

import com.example.demo.common.PageResult;
import com.example.demo.pojo.DTO.facility.VillageCreateDTO;
import com.example.demo.pojo.DTO.facility.VillageQueryDTO;
import com.example.demo.pojo.DTO.facility.VillageResponseDTO;
import com.example.demo.pojo.DTO.facility.VillageUpdateDTO;
import com.example.demo.pojo.VO.VillageVO;
import com.example.demo.pojo.entity.facility.Village;
import com.example.demo.mapper.VillageMapper;
import com.example.demo.service.base.FacilityService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 村庄服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VillageService implements FacilityService<Village, VillageResponseDTO, VillageQueryDTO, VillageCreateDTO, VillageUpdateDTO, VillageVO> {

    private final VillageMapper villageMapper;

    /**
     * 分页查询村庄列表
     * 实现统一接口方法：queryPage，返回VO格式
     */
    @Override
    public PageResult<VillageVO> queryPage(VillageQueryDTO villageQueryDTO) {
        // 使用PageHelper进行分页
        PageHelper.startPage(villageQueryDTO.getPage(), villageQueryDTO.getSize());
        
        // 查询基础村庄数据
        Page<Village> villages = villageMapper.selectVillagePageByKeyword(
                villageQueryDTO.getKeyword()
        );

        // 获取分页信息
        long total = villages.getTotal();
        List<Village> records = villages.getResult();

        // 转换为VO格式并补充关联信息
        List<VillageVO> voList = records.stream()
            .map(this::convertToVO)
            .collect(java.util.stream.Collectors.toList());

        return new PageResult<>(
                total,
                voList
        );
    }

    /**
     * 根据ID查询村庄详情
     * 实现统一接口方法：queryById
     */
    @Override
    public VillageResponseDTO queryById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("村庄ID不能为空");
        }

        VillageResponseDTO village = villageMapper.selectVillageById(id);
        if (village == null) {
            throw new RuntimeException("村庄不存在");
        }
        return village;
    }

    /**
     * 创建村庄
     * 实现统一接口方法：create
     */
    @Override
    @Transactional
    public VillageResponseDTO create(VillageCreateDTO createDTO) {
        // 验证村庄名称是否已存在
        if (villageMapper.existsByNameAndAdministrativeCode(createDTO.getName(), createDTO.getAdministrativeCode(), null)) {
            throw new RuntimeException("村庄名称已存在");
        }

        Village village = new Village();
        BeanUtils.copyProperties(createDTO, village);
        village.setCreatedAt(LocalDateTime.now());
        village.setUpdatedAt(LocalDateTime.now());

        villageMapper.insert(village);
        log.info("创建村庄成功，村庄ID: {}, 村庄名称: {}", village.getId(), village.getName());

        return queryById(village.getId());
    }

    /**
     * 更新村庄信息
     * 实现统一接口方法：update
     */
    @Override
    @Transactional
    public VillageResponseDTO update(VillageUpdateDTO updateDTO) {
        if (updateDTO.getId() == null) {
            throw new IllegalArgumentException("村庄ID不能为空");
        }

        // 检查村庄是否存在
        Village existingVillage = villageMapper.selectById(updateDTO.getId());
        if (existingVillage == null) {
            throw new RuntimeException("村庄不存在");
        }

        // 验证村庄名称是否已存在（排除当前记录）
        if (villageMapper.existsByNameAndAdministrativeCode(updateDTO.getName(), updateDTO.getAdministrativeCode(), updateDTO.getId())) {
            throw new RuntimeException("村庄名称已存在");
        }

        Village village = new Village();
        BeanUtils.copyProperties(updateDTO, village);
        village.setUpdatedAt(LocalDateTime.now());

        villageMapper.updateById(village);
        log.info("更新村庄成功，村庄ID: {}, 村庄名称: {}", village.getId(), village.getName());

        return queryById(updateDTO.getId());
    }

    /**
     * 删除村庄
     * 实现统一接口方法：delete
     */
    @Override
    @Transactional
    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("村庄ID不能为空");
        }

        // 检查村庄是否存在
        Village existingVillage = villageMapper.selectById(id);
        if (existingVillage == null) {
            throw new RuntimeException("村庄不存在");
        }

        // 软删除：设置删除时间
        Village village = new Village();
        village.setId(id);
        village.setDeletedAt(LocalDateTime.now());
        villageMapper.updateById(village);

        log.info("删除村庄成功，村庄ID: {}, 村庄名称: {}", id, existingVillage.getName());
    }

    /**
     * 批量删除村庄
     */
    @Transactional
    public void batchDeleteVillages(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("删除ID列表不能为空");
        }

        for (Long id : ids) {
            delete(id);
        }
    }

    /**
     * 获取可用村庄列表（用于下拉选择）
     * 实现统一接口方法：queryAvailable
     */
    @Override
    public List<VillageVO> queryAvailable() {
        List<Village> villages = villageMapper.selectAvailableVillages();
        return villages.stream().map(this::convertToVO).collect(java.util.stream.Collectors.toList());
    }

    /**
     * 统计村庄总数
     * 实现统一接口方法：countTotal
     */
    @Override
    public long countTotal() {
        return villageMapper.countTotal();
    }

    /**
     * 将Village实体转换为VO，并补充关联信息
     */
    private VillageVO convertToVO(Village village) {
        VillageVO vo = new VillageVO();
        BeanUtils.copyProperties(village, vo);
        
        // 这里可以添加关联信息查询，比如：
        // vo.setExtraInfo(villageMapper.selectExtraInfoByVillageId(village.getId()));
        
        return vo;
    }
}
