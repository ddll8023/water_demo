package com.example.demo.service;

import com.example.demo.common.PageResult;
import com.example.demo.pojo.DTO.facility.FloatingBoatCreateDTO;
import com.example.demo.pojo.DTO.facility.FloatingBoatQueryDTO;
import com.example.demo.pojo.DTO.facility.FloatingBoatResponseDTO;
import com.example.demo.pojo.DTO.facility.FloatingBoatUpdateDTO;
import com.example.demo.pojo.entity.facility.FloatingBoat;
import com.example.demo.mapper.FloatingBoatMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 浮船信息服务类
 */
@Service
@RequiredArgsConstructor
public class FloatingBoatService {

    private final FloatingBoatMapper floatingBoatMapper;

    /**
     * 分页查询浮船信息列表
     */
    public PageResult<FloatingBoatResponseDTO> getFloatingBoatPage(FloatingBoatQueryDTO queryDTO) {
        PageHelper.startPage(queryDTO.getPage(), queryDTO.getSize());
        List<FloatingBoatResponseDTO> result = floatingBoatMapper.selectFloatingBoatPage(
            queryDTO.getKeyword(),
            queryDTO.getPumpingStatus()
        );
        PageInfo<FloatingBoatResponseDTO> pageInfo = new PageInfo<>(result);

        return new PageResult<>(
            pageInfo.getList(),
            (int) pageInfo.getTotal(),
            pageInfo.getPageNum(),
            pageInfo.getPageSize()
        );
    }

    /**
     * 根据ID查询浮船信息详情
     */
    public FloatingBoatResponseDTO getFloatingBoatById(Long id) {
        FloatingBoatResponseDTO result = floatingBoatMapper.selectFloatingBoatById(id);
        if (result == null) {
            throw new RuntimeException("浮船信息不存在，ID: " + id);
        }
        return result;
    }

    /**
     * 创建浮船信息
     */
    @Transactional
    public FloatingBoatResponseDTO createFloatingBoat(FloatingBoatCreateDTO createDTO) {
        // 浮船编码字段已移除，无需检查编码重复

        // 创建实体对象
        FloatingBoat floatingBoat = new FloatingBoat();
        BeanUtils.copyProperties(createDTO, floatingBoat);

        // 保存到数据库
        int result = floatingBoatMapper.insert(floatingBoat);
        if (result <= 0) {
            throw new RuntimeException("创建浮船信息失败");
        }

        // 返回创建结果
        return getFloatingBoatById(floatingBoat.getId());
    }

    /**
     * 更新浮船信息
     */
    @Transactional
    public FloatingBoatResponseDTO updateFloatingBoat(FloatingBoatUpdateDTO updateDTO) {
        // 检查浮船信息是否存在
        FloatingBoat existingFloatingBoat = floatingBoatMapper.selectById(updateDTO.getId());
        if (existingFloatingBoat == null) {
            throw new RuntimeException("浮船信息不存在，ID: " + updateDTO.getId());
        }

        // 浮船编码字段已移除，无需检查编码重复

        // 更新实体对象
        FloatingBoat floatingBoat = new FloatingBoat();
        BeanUtils.copyProperties(updateDTO, floatingBoat);

        // 更新数据库
        int result = floatingBoatMapper.updateById(floatingBoat);
        if (result <= 0) {
            throw new RuntimeException("更新浮船信息失败");
        }

        // 返回更新结果
        return getFloatingBoatById(updateDTO.getId());
    }

    /**
     * 删除浮船信息
     */
    @Transactional
    public void deleteFloatingBoat(Long id) {
        // 检查浮船信息是否存在
        FloatingBoat existingFloatingBoat = floatingBoatMapper.selectById(id);
        if (existingFloatingBoat == null) {
            throw new RuntimeException("浮船信息不存在，ID: " + id);
        }

        // 软删除
        int result = floatingBoatMapper.deleteById(id);
        if (result <= 0) {
            throw new RuntimeException("删除浮船信息失败");
        }
    }

    /**
     * 批量删除浮船信息
     */
    @Transactional
    public void batchDeleteFloatingBoats(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("删除ID列表不能为空");
        }

        for (Long id : ids) {
            deleteFloatingBoat(id);
        }
    }

    /**
     * 获取所有可用浮船信息（用于下拉选择）
     */
    public List<FloatingBoat> getAvailableFloatingBoats() {
        return floatingBoatMapper.selectAvailableFloatingBoats();
    }

    /**
     * 统计浮船信息总数
     */
    public long countTotal() {
        return floatingBoatMapper.countTotal();
    }
}
