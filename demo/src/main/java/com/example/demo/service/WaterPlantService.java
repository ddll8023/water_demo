package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.common.PageResult;
import com.example.demo.mapper.WaterPlantMapper;
import com.example.demo.pojo.DTO.facility.WaterPlantCreateDTO;
import com.example.demo.pojo.DTO.facility.WaterPlantQueryDTO;
import com.example.demo.pojo.DTO.facility.WaterPlantResponseDTO;
import com.example.demo.pojo.DTO.facility.WaterPlantUpdateDTO;
import com.example.demo.pojo.VO.WaterPlantVO;
import com.example.demo.pojo.entity.facility.WaterPlant;
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
 * 水厂服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WaterPlantService extends ServiceImpl<WaterPlantMapper, WaterPlant> implements FacilityService<WaterPlant, WaterPlantResponseDTO, WaterPlantQueryDTO, WaterPlantCreateDTO, WaterPlantUpdateDTO, WaterPlantVO> {

    private final WaterPlantMapper waterPlantMapper;

    /**
     * 分页查询水厂列表
     * 实现统一接口方法：queryPage，返回VO格式
     */
    @Override
    public PageResult<WaterPlantVO> queryPage(WaterPlantQueryDTO waterPlantQueryDTO) {

        // 使用PageHelper设置分页
        PageHelper.startPage(waterPlantQueryDTO.getPage(), waterPlantQueryDTO.getSize());

        // 查询基础水厂数据
        Page<WaterPlant> waterPlants = waterPlantMapper.selectWaterPlantPageByKeyword(waterPlantQueryDTO.getKeyword());

        // 获取分页信息
        long total = waterPlants.getTotal();
        List<WaterPlant> records = waterPlants.getResult();

        // 转换为VO格式并补充关联信息
        List<WaterPlantVO> voList = records.stream().map(this::convertToVO).collect(java.util.stream.Collectors.toList());

        // 返回分页数据
        return new PageResult<>(total, voList);
    }

    /**
     * 根据ID查询水厂详情
     * 实现统一接口方法：queryById
     */
    @Override
    public WaterPlantResponseDTO queryById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("水厂ID不能为空");
        }

        WaterPlant waterPlant = waterPlantMapper.selectById(id);
        if (waterPlant == null) {
            throw new RuntimeException("水厂不存在");
        }

        // 构建响应DTO并补充关联信息
        WaterPlantResponseDTO responseDTO = new WaterPlantResponseDTO();
        BeanUtils.copyProperties(waterPlant, responseDTO);

        // 补充关联信息
        responseDTO.setDepartmentName(waterPlantMapper.selectDepartmentNameByWaterPlantId(id));
        responseDTO.setManagerName(waterPlantMapper.selectManagerNameByWaterPlantId(id));
        responseDTO.setManagerPhone(waterPlantMapper.selectManagerPhoneByWaterPlantId(id));

        return responseDTO;
    }

    /**
     * 创建水厂
     * 实现统一接口方法：create
     */
    @Override
    @Transactional
    public WaterPlantResponseDTO create(WaterPlantCreateDTO createDTO) {
        // 验证水厂名称是否已存在
        if (StringUtils.hasText(createDTO.getName()) && waterPlantMapper.existsByName(createDTO.getName(), null)) {
            throw new RuntimeException("水厂名称已存在");
        }

        // 验证水厂编码是否已存在
        if (StringUtils.hasText(createDTO.getPlantCode()) && waterPlantMapper.existsByPlantCode(createDTO.getPlantCode(), null)) {
            throw new RuntimeException("水厂编码已存在");
        }

        WaterPlant waterPlant = new WaterPlant();
        BeanUtils.copyProperties(createDTO, waterPlant);
        waterPlant.setCreatedAt(LocalDateTime.now());
        waterPlant.setUpdatedAt(LocalDateTime.now());

        waterPlantMapper.insert(waterPlant);
        log.info("创建水厂成功，水厂ID: {}, 水厂名称: {}", waterPlant.getId(), waterPlant.getName());

        return queryById(waterPlant.getId());
    }

    /**
     * 更新水厂信息
     * 实现统一接口方法：update
     */
    @Override
    @Transactional
    public WaterPlantResponseDTO update(WaterPlantUpdateDTO updateDTO) {
        if (updateDTO.getId() == null) {
            throw new IllegalArgumentException("水厂ID不能为空");
        }

        // 检查水厂是否存在
        WaterPlant existingWaterPlant = waterPlantMapper.selectById(updateDTO.getId());
        if (existingWaterPlant == null) {
            throw new RuntimeException("水厂不存在");
        }

        // 验证水厂名称是否已存在（排除当前记录）
        if (StringUtils.hasText(updateDTO.getName()) && waterPlantMapper.existsByName(updateDTO.getName(), updateDTO.getId())) {
            throw new RuntimeException("水厂名称已存在");
        }

        // 验证水厂编码是否已存在（排除当前记录）
        if (StringUtils.hasText(updateDTO.getPlantCode()) && waterPlantMapper.existsByPlantCode(updateDTO.getPlantCode(), updateDTO.getId())) {
            throw new RuntimeException("水厂编码已存在");
        }

        WaterPlant waterPlant = new WaterPlant();
        BeanUtils.copyProperties(updateDTO, waterPlant);
        waterPlant.setUpdatedAt(LocalDateTime.now());

        waterPlantMapper.updateById(waterPlant);
        log.info("更新水厂成功，水厂ID: {}, 水厂名称: {}", waterPlant.getId(), waterPlant.getName());

        return queryById(updateDTO.getId());
    }

    /**
     * 删除水厂
     * 实现统一接口方法：delete
     */
    @Override
    @Transactional
    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("水厂ID不能为空");
        }

        // 检查水厂是否存在
        WaterPlant existingWaterPlant = waterPlantMapper.selectById(id);
        if (existingWaterPlant == null) {
            throw new RuntimeException("水厂不存在");
        }

        // 软删除：设置删除时间
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
            throw new IllegalArgumentException("删除ID列表不能为空");
        }

        for (Long id : ids) {
            delete(id);
        }
    }

    /**
     * 获取可用水厂列表（用于下拉选择）
     * 实现统一接口方法：queryAvailable
     */
    @Override
    public List<WaterPlantVO> queryAvailable() {
        List<WaterPlant> waterPlants = list(); // 使用 MyBatis-Plus 的 list 方法获取所有记录
        return waterPlants.stream().map(this::convertToVO).collect(java.util.stream.Collectors.toList());
    }

    /**
     * 统计水厂总数
     * 实现统一接口方法：countTotal
     */
    @Override
    public long countTotal() {
        return count(); // 使用 MyBatis-Plus 的 count 方法
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

    /**
     * 将WaterPlant实体转换为VO，并补充关联信息
     */
    private WaterPlantVO convertToVO(WaterPlant waterPlant) {
        WaterPlantVO vo = new WaterPlantVO();
        BeanUtils.copyProperties(waterPlant, vo);

        // 查询并设置关联信息
        vo.setDepartmentName(waterPlantMapper.selectDepartmentNameByWaterPlantId(waterPlant.getId()));
        vo.setManagerName(waterPlantMapper.selectManagerNameByWaterPlantId(waterPlant.getId()));

        return vo;
    }
}
