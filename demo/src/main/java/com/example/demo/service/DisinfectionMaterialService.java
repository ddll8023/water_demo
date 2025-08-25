package com.example.demo.service;

import com.example.demo.pojo.DTO.common.PageResponseDTO;
import com.example.demo.pojo.DTO.facility.DisinfectionMaterialCreateDTO;
import com.example.demo.pojo.DTO.facility.DisinfectionMaterialResponseDTO;
import com.example.demo.pojo.DTO.facility.DisinfectionMaterialUpdateDTO;
import com.example.demo.pojo.entity.facility.DisinfectionMaterial;
import com.example.demo.mapper.DisinfectionMaterialMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
public class DisinfectionMaterialService {

    private final DisinfectionMaterialMapper disinfectionMaterialMapper;

    /**
     * 分页查询消毒药材列表 - 直接接收参数
     */
    public PageResponseDTO<DisinfectionMaterialResponseDTO> getDisinfectionMaterialPage(int page, int size, String keyword, Long waterPlantId) {
        PageHelper.startPage(page, size);
        List<DisinfectionMaterialResponseDTO> list = disinfectionMaterialMapper.selectDisinfectionMaterialPage(keyword, waterPlantId);
        PageInfo<DisinfectionMaterialResponseDTO> pageInfo = new PageInfo<>(list);

        return new PageResponseDTO<>(
                pageInfo.getList(),
                (int) pageInfo.getTotal(),
                pageInfo.getPageNum(),
                pageInfo.getPageSize()
        );
    }

    /**
     * 根据ID查询消毒药材详情
     */
    public DisinfectionMaterialResponseDTO getDisinfectionMaterialById(Long id) {
        DisinfectionMaterialResponseDTO result = disinfectionMaterialMapper.selectDisinfectionMaterialById(id);
        if (result == null) {
            throw new RuntimeException("消毒药材不存在，ID: " + id);
        }
        return result;
    }

    /**
     * 创建消毒药材
     */
    @Transactional
    public DisinfectionMaterialResponseDTO createDisinfectionMaterial(DisinfectionMaterialCreateDTO createDTO) {
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
        return getDisinfectionMaterialById(disinfectionMaterial.getId());
    }

    /**
     * 更新消毒药材信息
     */
    @Transactional
    public DisinfectionMaterialResponseDTO updateDisinfectionMaterial(DisinfectionMaterialUpdateDTO updateDTO) {
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
        return getDisinfectionMaterialById(updateDTO.getId());
    }

    /**
     * 删除消毒药材
     */
    @Transactional
    public void deleteDisinfectionMaterial(Long id) {
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
            deleteDisinfectionMaterial(id);
        }
    }

    /**
     * 获取所有可用消毒药材（用于下拉选择）
     */
    public List<DisinfectionMaterial> getAvailableDisinfectionMaterials() {
        return disinfectionMaterialMapper.selectAvailableDisinfectionMaterials();
    }

    /**
     * 统计消毒药材总数
     */
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
}
