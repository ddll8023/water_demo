package com.example.demo.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.example.demo.pojo.DTO.common.PageResponseDTO;
import com.example.demo.pojo.DTO.facility.VillageCreateDTO;
import com.example.demo.pojo.DTO.facility.VillageQueryDTO;
import com.example.demo.pojo.DTO.facility.VillageResponseDTO;
import com.example.demo.pojo.DTO.facility.VillageUpdateDTO;
import com.example.demo.pojo.entity.facility.Village;
import com.example.demo.mapper.VillageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 村庄信息服务类
 */
@Service
@RequiredArgsConstructor
public class VillageService {

    private final VillageMapper villageMapper;

    /**
     * 分页查询村庄信息列表
     */
    public PageResponseDTO<VillageResponseDTO> getVillagePage(VillageQueryDTO queryDTO) {
        // 参数验证
        if (queryDTO.getPage() < 1) {
            queryDTO.setPage(1);
        }
        if (queryDTO.getSize() < 1) {
            queryDTO.setSize(10);
        }

        // 使用PageHelper开始分页
        PageHelper.startPage(queryDTO.getPage(), queryDTO.getSize());
        
        // 查询数据，不需要传入分页参数
        List<VillageResponseDTO> villages = villageMapper.selectVillageList(
                queryDTO.getKeyword()
        );
        
        // 获取分页信息
        PageInfo<VillageResponseDTO> pageInfo = new PageInfo<>(villages);

        return new PageResponseDTO<VillageResponseDTO>(
            pageInfo.getList(),
            (int) pageInfo.getTotal(),
            queryDTO.getPage(),
            queryDTO.getSize()
        );
    }

    /**
     * 根据ID查询村庄信息详情
     */
    public VillageResponseDTO getVillageById(Long id) {
        VillageResponseDTO result = villageMapper.selectVillageById(id);
        if (result == null) {
            throw new RuntimeException("村庄信息不存在，ID: " + id);
        }
        return result;
    }

    /**
     * 创建村庄信息
     */
    @Transactional
    public VillageResponseDTO createVillage(VillageCreateDTO createDTO) {
        // 检查同一行政区划下村庄名称是否已存在
        if (villageMapper.existsByNameAndAdministrativeCode(createDTO.getName(), createDTO.getAdministrativeCode(), null)) {
            throw new RuntimeException("同一行政区划下村庄名称已存在: " + createDTO.getName());
        }

        // 创建实体对象
        Village village = new Village();
        BeanUtils.copyProperties(createDTO, village);

        // 保存到数据库
        int result = villageMapper.insert(village);
        if (result <= 0) {
            throw new RuntimeException("创建村庄信息失败");
        }

        // 返回创建结果
        return getVillageById(village.getId());
    }

    /**
     * 更新村庄信息
     */
    @Transactional
    public VillageResponseDTO updateVillage(VillageUpdateDTO updateDTO) {
        // 检查村庄信息是否存在
        Village existingVillage = villageMapper.selectById(updateDTO.getId());
        if (existingVillage == null) {
            throw new RuntimeException("村庄信息不存在，ID: " + updateDTO.getId());
        }

        // 检查同一行政区划下村庄名称是否已被其他村庄使用
        if (villageMapper.existsByNameAndAdministrativeCode(updateDTO.getName(), updateDTO.getAdministrativeCode(), updateDTO.getId())) {
            throw new RuntimeException("同一行政区划下村庄名称已存在: " + updateDTO.getName());
        }

        // 更新实体对象
        Village village = new Village();
        BeanUtils.copyProperties(updateDTO, village);

        // 更新数据库
        int result = villageMapper.updateById(village);
        if (result <= 0) {
            throw new RuntimeException("更新村庄信息失败");
        }

        // 返回更新结果
        return getVillageById(updateDTO.getId());
    }

    /**
     * 删除村庄信息
     */
    @Transactional
    public void deleteVillage(Long id) {
        // 检查村庄信息是否存在
        Village existingVillage = villageMapper.selectById(id);
        if (existingVillage == null) {
            throw new RuntimeException("村庄信息不存在，ID: " + id);
        }

        // 软删除
        int result = villageMapper.deleteById(id);
        if (result <= 0) {
            throw new RuntimeException("删除村庄信息失败");
        }
    }

    /**
     * 批量删除村庄信息
     */
    @Transactional
    public void batchDeleteVillages(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("删除ID列表不能为空");
        }

        for (Long id : ids) {
            deleteVillage(id);
        }
    }

    /**
     * 获取所有可用村庄信息（用于下拉选择）
     */
    public List<Village> getAvailableVillages() {
        return villageMapper.selectAvailableVillages();
    }

    /**
     * 统计村庄信息总数
     */
    public long countTotal() {
        return villageMapper.countTotal();
    }
}
