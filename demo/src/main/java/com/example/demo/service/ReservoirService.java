package com.example.demo.service;

import com.example.demo.pojo.dto.facility.ReservoirCreateDTO;
import com.example.demo.pojo.dto.facility.ReservoirQueryDTO;
import com.example.demo.pojo.dto.facility.ReservoirResponseDTO;
import com.example.demo.pojo.dto.facility.ReservoirUpdateDTO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.example.demo.pojo.dto.common.PageResponseDTO;
import com.example.demo.pojo.entity.facility.Reservoir;
import com.example.demo.mapper.ReservoirMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 水库服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReservoirService {

    private final ReservoirMapper reservoirMapper;

    /**
     * 分页查询水库列表
     */
    public PageResponseDTO<ReservoirResponseDTO> getReservoirPage(ReservoirQueryDTO queryDTO) {
        // 参数验证
        if (queryDTO.getPage() == null || queryDTO.getPage() < 1) {
            queryDTO.setPage(1);
        }
        if (queryDTO.getSize() == null || queryDTO.getSize() < 1 || queryDTO.getSize() > 100) {
            queryDTO.setSize(10);
        }

        // 使用PageHelper开始分页
        PageHelper.startPage(queryDTO.getPage(), queryDTO.getSize());
        
        // 查询数据，不需要传入分页参数
        List<ReservoirResponseDTO> reservoirs = reservoirMapper.selectReservoirList(
                queryDTO.getKeyword(),
                queryDTO.getEngineeringGrade()
        );
        
        // 获取分页信息
        PageInfo<ReservoirResponseDTO> pageInfo = new PageInfo<>(reservoirs);

        return new PageResponseDTO<>(
                pageInfo.getList(),
                (int) pageInfo.getTotal(),
                queryDTO.getPage(),
                queryDTO.getSize()
        );
    }

    /**
     * 根据ID查询水库详情
     */
    public ReservoirResponseDTO getReservoirById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("水库ID不能为空");
        }

        ReservoirResponseDTO reservoir = reservoirMapper.selectReservoirById(id);
        if (reservoir == null) {
            throw new RuntimeException("水库不存在");
        }
        return reservoir;
    }

    /**
     * 创建水库
     */
    @Transactional
    public ReservoirResponseDTO createReservoir(ReservoirCreateDTO createDTO) {
        // 验证水库名称是否已存在
        if (StringUtils.hasText(createDTO.getName()) && 
            reservoirMapper.existsByName(createDTO.getName(), null)) {
            throw new RuntimeException("水库名称已存在");
        }

        // 验证水库编码是否已存在
        if (StringUtils.hasText(createDTO.getReservoirCode()) && 
            reservoirMapper.existsByReservoirCode(createDTO.getReservoirCode(), null)) {
            throw new RuntimeException("水库编码已存在");
        }

        // 验证注册登记号是否已存在
        if (StringUtils.hasText(createDTO.getRegistrationNo()) && 
            reservoirMapper.existsByRegistrationNo(createDTO.getRegistrationNo(), null)) {
            throw new RuntimeException("注册登记号已存在");
        }

        Reservoir reservoir = new Reservoir();
        BeanUtils.copyProperties(createDTO, reservoir);
        reservoir.setCreatedAt(LocalDateTime.now());
        reservoir.setUpdatedAt(LocalDateTime.now());

        reservoirMapper.insert(reservoir);
        log.info("创建水库成功，水库ID: {}, 水库名称: {}", reservoir.getId(), reservoir.getName());

        return getReservoirById(reservoir.getId());
    }

    /**
     * 更新水库信息
     */
    @Transactional
    public ReservoirResponseDTO updateReservoir(ReservoirUpdateDTO updateDTO) {
        if (updateDTO.getId() == null) {
            throw new IllegalArgumentException("水库ID不能为空");
        }

        // 检查水库是否存在
        Reservoir existingReservoir = reservoirMapper.selectById(updateDTO.getId());
        if (existingReservoir == null) {
            throw new RuntimeException("水库不存在");
        }

        // 验证水库名称是否已存在（排除当前记录）
        if (StringUtils.hasText(updateDTO.getName()) && 
            reservoirMapper.existsByName(updateDTO.getName(), updateDTO.getId())) {
            throw new RuntimeException("水库名称已存在");
        }

        // 验证水库编码是否已存在（排除当前记录）
        if (StringUtils.hasText(updateDTO.getReservoirCode()) && 
            reservoirMapper.existsByReservoirCode(updateDTO.getReservoirCode(), updateDTO.getId())) {
            throw new RuntimeException("水库编码已存在");
        }

        // 验证注册登记号是否已存在（排除当前记录）
        if (StringUtils.hasText(updateDTO.getRegistrationNo()) && 
            reservoirMapper.existsByRegistrationNo(updateDTO.getRegistrationNo(), updateDTO.getId())) {
            throw new RuntimeException("注册登记号已存在");
        }

        Reservoir reservoir = new Reservoir();
        BeanUtils.copyProperties(updateDTO, reservoir);
        reservoir.setUpdatedAt(LocalDateTime.now());

        reservoirMapper.updateById(reservoir);
        log.info("更新水库成功，水库ID: {}, 水库名称: {}", reservoir.getId(), reservoir.getName());

        return getReservoirById(reservoir.getId());
    }

    /**
     * 删除水库
     */
    @Transactional
    public void deleteReservoir(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("水库ID不能为空");
        }

        // 检查水库是否存在
        Reservoir existingReservoir = reservoirMapper.selectById(id);
        if (existingReservoir == null) {
            throw new RuntimeException("水库不存在");
        }

        // 软删除
        Reservoir reservoir = new Reservoir();
        reservoir.setId(id);
        reservoir.setDeletedAt(LocalDateTime.now());
        reservoirMapper.updateById(reservoir);

        log.info("删除水库成功，水库ID: {}, 水库名称: {}", id, existingReservoir.getName());
    }

    /**
     * 批量删除水库
     */
    @Transactional
    public void batchDeleteReservoirs(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("删除ID列表不能为空");
        }

        for (Long id : ids) {
            deleteReservoir(id);
        }
    }

    /**
     * 获取所有可用水库（用于下拉选择）
     */
    public List<Reservoir> getAvailableReservoirs() {
        return reservoirMapper.selectAvailableReservoirs();
    }

    /**
     * 统计水库总数
     */
    public long countTotal() {
        return reservoirMapper.countTotal();
    }



    /**
     * 根据工程等级统计数量
     */
    public long countByEngineeringGrade(String engineeringGrade) {
        if (!StringUtils.hasText(engineeringGrade)) {
            return 0;
        }
        return reservoirMapper.countByEngineeringGrade(engineeringGrade);
    }
}
