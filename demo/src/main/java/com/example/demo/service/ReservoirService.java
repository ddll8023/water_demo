package com.example.demo.service;

import com.example.demo.common.PageResult;
import com.example.demo.pojo.DTO.facility.ReservoirCreateDTO;
import com.example.demo.pojo.DTO.facility.ReservoirQueryDTO;
import com.example.demo.pojo.DTO.facility.ReservoirResponseDTO;
import com.example.demo.pojo.DTO.facility.ReservoirUpdateDTO;
import com.example.demo.pojo.VO.ReservoirVO;
import com.example.demo.pojo.entity.facility.Reservoir;
import com.example.demo.mapper.ReservoirMapper;
import com.example.demo.service.base.FacilityService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 水库服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReservoirService implements FacilityService<Reservoir, ReservoirResponseDTO, ReservoirQueryDTO, ReservoirCreateDTO, ReservoirUpdateDTO, ReservoirVO> {

    private final ReservoirMapper reservoirMapper;

    /**
     * 分页查询水库列表
     * 实现统一接口方法：queryPage
     */
    @Override
    public PageResult<ReservoirVO> queryPage(ReservoirQueryDTO queryDTO) {
        // 参数验证
        if (queryDTO.getPage() == null || queryDTO.getPage() < 1) {
            queryDTO.setPage(1);
        }
        if (queryDTO.getSize() == null || queryDTO.getSize() < 1 || queryDTO.getSize() > 100) {
            queryDTO.setSize(10);
        }

        // 使用PageHelper进行分页
        PageHelper.startPage(queryDTO.getPage(), queryDTO.getSize());
        Page<Reservoir> reservoirs = reservoirMapper.selectReservoirList(
                queryDTO.getKeyword()
        );

        // 获取分页信息
        long total = reservoirs.getTotal();
        List<Reservoir> records = reservoirs.getResult();

        // 转换为VO格式并补充关联信息
        List<ReservoirVO> voList = records.stream()
            .map(this::convertToVO)
            .collect(java.util.stream.Collectors.toList());

        return new PageResult<>(
                total,
                voList
        );
    }

    /**
     * 根据ID查询水库详情
     * 实现统一接口方法：queryById
     */
    @Override
    public ReservoirResponseDTO queryById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("水库ID不能为空");
        }

        Reservoir reservoir = reservoirMapper.selectReservoirById(id);
        if (reservoir == null) {
            throw new RuntimeException("水库不存在");
        }
        
        // 转换为ResponseDTO并补充关联信息
        ReservoirResponseDTO responseDTO = new ReservoirResponseDTO();
        BeanUtils.copyProperties(reservoir, responseDTO);
        
        // 查询并设置关联信息
        if (reservoir.getEngineeringGrade() != null) {
            String engineeringGradeName = reservoirMapper.selectEngineeringGradeNameByReservoirId(reservoir.getId());
            responseDTO.setEngineeringGradeName(engineeringGradeName);
        }
        
        if (reservoir.getEngineeringScale() != null) {
            String engineeringScaleName = reservoirMapper.selectEngineeringScaleNameByReservoirId(reservoir.getId());
            responseDTO.setEngineeringScaleName(engineeringScaleName);
        }
        
        return responseDTO;
    }

    /**
     * 创建水库
     * 实现统一接口方法：create
     */
    @Override
    @Transactional
    public ReservoirResponseDTO create(ReservoirCreateDTO createDTO) {
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

        Reservoir reservoir = new Reservoir();
        BeanUtils.copyProperties(createDTO, reservoir);
        reservoir.setCreatedAt(LocalDateTime.now());
        reservoir.setUpdatedAt(LocalDateTime.now());

        reservoirMapper.insert(reservoir);
        log.info("创建水库成功，水库ID: {}, 水库名称: {}", reservoir.getId(), reservoir.getName());

        return queryById(reservoir.getId());
    }

    /**
     * 更新水库信息
     * 实现统一接口方法：update
     */
    @Override
    @Transactional
    public ReservoirResponseDTO update(ReservoirUpdateDTO updateDTO) {
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

        Reservoir reservoir = new Reservoir();
        BeanUtils.copyProperties(updateDTO, reservoir);
        reservoir.setUpdatedAt(LocalDateTime.now());

        reservoirMapper.updateById(reservoir);
        log.info("更新水库成功，水库ID: {}, 水库名称: {}", reservoir.getId(), reservoir.getName());

        return queryById(updateDTO.getId());
    }

    /**
     * 删除水库
     * 实现统一接口方法：delete
     */
    @Override
    @Transactional
    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("水库ID不能为空");
        }

        // 检查水库是否存在
        Reservoir existingReservoir = reservoirMapper.selectById(id);
        if (existingReservoir == null) {
            throw new RuntimeException("水库不存在");
        }

        // 软删除：设置删除时间
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
            throw new IllegalArgumentException("删除ID列表不能为空");
        }

        for (Long id : ids) {
            delete(id);
        }
    }

    /**
     * 获取可用水库列表（用于下拉选择）
     * 实现统一接口方法：queryAvailable
     */
    @Override
    public List<ReservoirVO> queryAvailable() {
        List<Reservoir> reservoirs = reservoirMapper.selectAvailableReservoirs();
        return reservoirs.stream().map(this::convertToVO).collect(java.util.stream.Collectors.toList());
    }

    /**
     * 统计水库总数
     * 实现统一接口方法：countTotal
     */
    @Override
    public long countTotal() {
        return reservoirMapper.countTotal();
    }

    /**
     * 将Reservoir实体转换为VO，并补充关联信息
     */
    private ReservoirVO convertToVO(Reservoir reservoir) {
        ReservoirVO vo = new ReservoirVO();
        BeanUtils.copyProperties(reservoir, vo);
        
        // 查询并设置关联信息
        if (reservoir.getEngineeringGrade() != null) {
            String engineeringGradeName = reservoirMapper.selectEngineeringGradeNameByReservoirId(reservoir.getId());
            vo.setEngineeringGradeLabel(engineeringGradeName);
        }
        
        if (reservoir.getEngineeringScale() != null) {
            String engineeringScaleName = reservoirMapper.selectEngineeringScaleNameByReservoirId(reservoir.getId());
            vo.setEngineeringScaleLabel(engineeringScaleName);
        }
        
        return vo;
    }
}
