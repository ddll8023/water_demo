package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.common.PageResult;
import com.example.demo.pojo.DTO.facility.WaterPlantCreateDTO;
import com.example.demo.pojo.DTO.facility.WaterPlantQueryDTO;
import com.example.demo.pojo.DTO.facility.WaterPlantResponseDTO;
import com.example.demo.pojo.DTO.facility.WaterPlantUpdateDTO;
import com.example.demo.pojo.entity.facility.WaterPlant;
import com.example.demo.mapper.WaterPlantMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

// 导入必要的类
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/**
 * 水厂服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WaterPlantService extends ServiceImpl<WaterPlantMapper, WaterPlant> {

    private final WaterPlantMapper waterPlantMapper;

    /**
     * 分页查询水厂列表
     */
    public PageResult<WaterPlantResponseDTO> getWaterPlantPage(WaterPlantQueryDTO queryDTO) {
        // 参数验证
        if (queryDTO.getPage() == null || queryDTO.getPage() < 1) {
            queryDTO.setPage(1);
        }
        if (queryDTO.getSize() == null || queryDTO.getSize() < 1 || queryDTO.getSize() > 100) {
            queryDTO.setSize(10);
        }

        // 使用PageHelper设置分页
        PageHelper.startPage(queryDTO.getPage(), queryDTO.getSize());
        
        // 执行查询
        List<WaterPlantResponseDTO> list = waterPlantMapper.selectWaterPlantPage(queryDTO.getKeyword());
        
        // 使用PageInfo包装查询结果
        PageInfo<WaterPlantResponseDTO> pageInfo = new PageInfo<>(list);

        // 返回分页数据
        return new PageResult<>(
                pageInfo.getList(),
                (int) pageInfo.getTotal(),
                queryDTO.getPage(),
                queryDTO.getSize()
        );
    }

    /**
     * 根据ID查询水厂详情
     */
    public WaterPlantResponseDTO getWaterPlantById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("水厂ID不能为空");
        }

        WaterPlantResponseDTO waterPlant = waterPlantMapper.selectWaterPlantById(id);
        if (waterPlant == null) {
            throw new RuntimeException("水厂不存在");
        }
        return waterPlant;
    }

    /**
     * 创建水厂
     */
    @Transactional
    public WaterPlantResponseDTO createWaterPlant(WaterPlantCreateDTO createDTO) {
        // 验证水厂名称是否已存在
        if (StringUtils.hasText(createDTO.getName()) && 
            waterPlantMapper.existsByName(createDTO.getName(), null)) {
            throw new RuntimeException("水厂名称已存在");
        }

        // 验证水厂编码是否已存在
        if (StringUtils.hasText(createDTO.getPlantCode()) && 
            waterPlantMapper.existsByPlantCode(createDTO.getPlantCode(), null)) {
            throw new RuntimeException("水厂编码已存在");
        }

        WaterPlant waterPlant = new WaterPlant();
        BeanUtils.copyProperties(createDTO, waterPlant);
        waterPlant.setCreatedAt(LocalDateTime.now());
        waterPlant.setUpdatedAt(LocalDateTime.now());

        waterPlantMapper.insert(waterPlant);
        log.info("创建水厂成功，水厂ID: {}, 水厂名称: {}", waterPlant.getId(), waterPlant.getName());

        return getWaterPlantById(waterPlant.getId());
    }

    /**
     * 更新水厂信息
     */
    @Transactional
    public WaterPlantResponseDTO updateWaterPlant(WaterPlantUpdateDTO updateDTO) {
        if (updateDTO.getId() == null) {
            throw new IllegalArgumentException("水厂ID不能为空");
        }

        // 检查水厂是否存在
        WaterPlant existingWaterPlant = waterPlantMapper.selectById(updateDTO.getId());
        if (existingWaterPlant == null) {
            throw new RuntimeException("水厂不存在");
        }

        // 验证水厂名称是否已存在（排除当前记录）
        if (StringUtils.hasText(updateDTO.getName()) && 
            waterPlantMapper.existsByName(updateDTO.getName(), updateDTO.getId())) {
            throw new RuntimeException("水厂名称已存在");
        }

        // 验证水厂编码是否已存在（排除当前记录）
        if (StringUtils.hasText(updateDTO.getPlantCode()) && 
            waterPlantMapper.existsByPlantCode(updateDTO.getPlantCode(), updateDTO.getId())) {
            throw new RuntimeException("水厂编码已存在");
        }

        WaterPlant waterPlant = new WaterPlant();
        BeanUtils.copyProperties(updateDTO, waterPlant);
        waterPlant.setUpdatedAt(LocalDateTime.now());

        waterPlantMapper.updateById(waterPlant);
        log.info("更新水厂成功，水厂ID: {}, 水厂名称: {}", waterPlant.getId(), waterPlant.getName());

        return getWaterPlantById(waterPlant.getId());
    }

    /**
     * 删除水厂
     */
    @Transactional
    public void deleteWaterPlant(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("水厂ID不能为空");
        }

        // 检查水厂是否存在
        WaterPlant existingWaterPlant = waterPlantMapper.selectById(id);
        if (existingWaterPlant == null) {
            throw new RuntimeException("水厂不存在");
        }

        // 软删除
        WaterPlant waterPlant = new WaterPlant();
        waterPlant.setId(id);
        waterPlant.setDeletedAt(LocalDateTime.now());
        waterPlantMapper.updateById(waterPlant);

        log.info("删除水厂成功，水厂ID: {}, 水厂名称: {}", id, existingWaterPlant.getName());
    }

    /**
     * 批量删除水厂
     */
    @Transactional
    public void batchDeleteWaterPlants(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("删除ID列表不能为空");
        }

        for (Long id : ids) {
            deleteWaterPlant(id);
        }
    }

    /**
     * 获取所有可用水厂（用于下拉选择）
     */
    public List<WaterPlant> getAvailableWaterPlants() {
        return waterPlantMapper.selectAvailableWaterPlants();
    }

    /**
     * 统计水厂总数
     */
    public long countTotal() {
        return waterPlantMapper.countTotal();
    }

    /**
     * 根据部门ID统计水厂数量
     */
    public long countByDepartmentId(Long departmentId) {
        if (departmentId == null) {
            return 0;
        }
        return waterPlantMapper.countByDepartmentId(departmentId);
    }

    /**
     * 根据负责人ID统计水厂数量
     */
    public long countByManagerId(Long managerId) {
        if (managerId == null) {
            return 0;
        }
        return waterPlantMapper.countByManagerId(managerId);
    }

    /**
     * 根据管理单位统计水厂数量
     */
    public long countByManagementUnit(String managementUnit) {
        if (!StringUtils.hasText(managementUnit)) {
            return 0;
        }
        return waterPlantMapper.countByManagementUnit(managementUnit);
    }
}
