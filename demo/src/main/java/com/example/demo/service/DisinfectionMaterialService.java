package com.example.demo.service;

import com.example.demo.common.PageResult;
import com.example.demo.mapper.DisinfectionMaterialMapper;
import com.example.demo.pojo.DTO.facility.DisinfectionMaterialCreateDTO;
import com.example.demo.pojo.DTO.facility.DisinfectionMaterialQueryDTO;
import com.example.demo.pojo.DTO.facility.DisinfectionMaterialResponseDTO;
import com.example.demo.pojo.DTO.facility.DisinfectionMaterialUpdateDTO;
import com.example.demo.pojo.VO.DisinfectionMaterialVO;
import com.example.demo.pojo.entity.facility.DisinfectionMaterial;
import com.example.demo.service.base.FacilityService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消毒药材服务类
 */
@Service
@RequiredArgsConstructor
public class DisinfectionMaterialService implements FacilityService<DisinfectionMaterial, DisinfectionMaterialResponseDTO, DisinfectionMaterialQueryDTO, DisinfectionMaterialCreateDTO, DisinfectionMaterialUpdateDTO, DisinfectionMaterialVO> {

    private final DisinfectionMaterialMapper disinfectionMaterialMapper;

    /**
     * 分页查询消毒药材列表 - 使用DTO参数
     * 实现统一接口方法：queryPage
     */
    @Override
    public PageResult<DisinfectionMaterialVO> queryPage(DisinfectionMaterialQueryDTO queryDTO) {

        PageHelper.startPage(queryDTO.getPage(), queryDTO.getSize());
        Page<DisinfectionMaterial> page = disinfectionMaterialMapper.selectDisinfectionMaterialPage(queryDTO.getKeyword());

        long total = page.getTotal();
        List<DisinfectionMaterial> records = page.getResult();

        // 转换为VO格式并补充关联信息
        List<DisinfectionMaterialVO> voList = records.stream().map(this::convertToVO).collect(java.util.stream.Collectors.toList());

        return new PageResult<>(total, voList);
    }

    /**
     * 根据ID查询消毒药材详情
     * 实现统一接口方法：queryById
     */
    @Override
    public DisinfectionMaterialResponseDTO queryById(Long id) {
        DisinfectionMaterialResponseDTO result = disinfectionMaterialMapper.selectDisinfectionMaterialById(id);
        if (result == null) {
            throw new RuntimeException("消毒药材不存在，ID: " + id);
        }
        return result;
    }

    /**
     * 创建消毒药材
     * 实现统一接口方法：create
     */
    @Override
    @Transactional
    public DisinfectionMaterialResponseDTO create(DisinfectionMaterialCreateDTO createDTO) {
        // 创建消毒药材

        // 创建实体对象
        DisinfectionMaterial disinfectionMaterial = new DisinfectionMaterial();
        BeanUtils.copyProperties(createDTO, disinfectionMaterial);

        // 保存到数据库
        int result = disinfectionMaterialMapper.insert(disinfectionMaterial);
        if (result <= 0) {
            throw new RuntimeException("创建消毒药材失败");
        }

        // 返回创建结果
        return queryById(disinfectionMaterial.getId());
    }

    /**
     * 更新消毒药材信息
     * 实现统一接口方法：update
     */
    @Override
    @Transactional
    public DisinfectionMaterialResponseDTO update(DisinfectionMaterialUpdateDTO updateDTO) {
        // 检查消毒药材是否存在
        DisinfectionMaterial existingMaterial = disinfectionMaterialMapper.selectById(updateDTO.getId());
        if (existingMaterial == null) {
            throw new RuntimeException("消毒药材不存在，ID: " + updateDTO.getId());
        }

        // 更新消毒药材

        // 更新实体对象
        DisinfectionMaterial disinfectionMaterial = new DisinfectionMaterial();
        BeanUtils.copyProperties(updateDTO, disinfectionMaterial);

        // 更新数据库
        int result = disinfectionMaterialMapper.updateById(disinfectionMaterial);
        if (result <= 0) {
            throw new RuntimeException("更新消毒药材失败");
        }

        // 返回更新结果
        return queryById(updateDTO.getId());
    }

    /**
     * 删除消毒药材
     * 实现统一接口方法：delete
     */
    @Override
    @Transactional
    public void delete(Long id) {
        // 检查消毒药材是否存在
        DisinfectionMaterial existingMaterial = disinfectionMaterialMapper.selectById(id);
        if (existingMaterial == null) {
            throw new RuntimeException("消毒药材不存在，ID: " + id);
        }

        // 软删除
        int result = disinfectionMaterialMapper.deleteById(id);
        if (result <= 0) {
            throw new RuntimeException("删除消毒药材失败");
        }
    }

    /**
     * 批量删除消毒药材
     */
    @Transactional
    public void batchDeleteDisinfectionMaterials(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("删除ID列表不能为空");
        }

        for (Long id : ids) {
            delete(id);
        }
    }

    /**
     * 获取所有可用消毒药材（用于下拉选择）
     * 实现统一接口方法：queryAvailable
     */
    @Override
    public List<DisinfectionMaterialVO> queryAvailable() {
        List<DisinfectionMaterial> materials = disinfectionMaterialMapper.selectAvailableDisinfectionMaterials();
        return materials.stream().map(this::convertToVO).collect(java.util.stream.Collectors.toList());
    }

    /**
     * 统计消毒药材总数
     * 实现统一接口方法：countTotal
     */
    @Override
    public long countTotal() {
        return disinfectionMaterialMapper.countTotal();
    }

    /**
     * 获取库存统计信息
     */
    public Map<String, Object> getStockStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("total", disinfectionMaterialMapper.countTotal());
        statistics.put("lowStock", disinfectionMaterialMapper.countLowStock());
        statistics.put("nearExpiry", disinfectionMaterialMapper.countNearExpiry());
        return statistics;
    }

    /**
     * 将DisinfectionMaterial实体转换为VO，并补充关联信息
     */
    private DisinfectionMaterialVO convertToVO(DisinfectionMaterial material) {
        DisinfectionMaterialVO vo = new DisinfectionMaterialVO();
        BeanUtils.copyProperties(material, vo);

        // 查询并设置水厂名称
        if (material.getWaterPlantId() != null) {
            String waterPlantName = disinfectionMaterialMapper.selectWaterPlantNameByMaterialId(material.getId());
            vo.setWaterPlantName(waterPlantName);
        }

        return vo;
    }
}
