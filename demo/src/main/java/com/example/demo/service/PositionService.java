package com.example.demo.service;

import com.example.demo.pojo.DTO.system.PositionCreateDTO;
import com.example.demo.pojo.DTO.system.PositionQueryDTO;
import com.example.demo.pojo.DTO.system.PositionUpdateDTO;
import com.example.demo.pojo.VO.PositionVO;
import com.example.demo.common.PageResult;
import com.example.demo.pojo.entity.system.Position;
import com.example.demo.mapper.PositionMapper;
import com.example.demo.constant.MessageConstant;
import com.example.demo.exception.PositionNotExistException;
import com.example.demo.exception.PositionNameDuplicateException;
import com.example.demo.exception.PositionHasPersonnelException;
import com.example.demo.exception.PositionNameEmptyException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 岗位服务实现类
 * 专注于岗位定义和管理，不参与权限控制
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PositionService {

    private final PositionMapper positionMapper;

    /**
     * 分页查询岗位列表
     */
    public PageResult<PositionVO> getPositionPage(PositionQueryDTO positionQueryDTO) {
        log.info("开始分页查询岗位列表，查询条件: {}", positionQueryDTO);

        // 使用PageHelper进行分页
        PageHelper.startPage(positionQueryDTO.getPage(), positionQueryDTO.getSize());
        Page<Position> positions = positionMapper.selectPositionPageWithPersonnelCount(positionQueryDTO.getKeyword());
        
        // 获取分页信息
        long total = positions.getTotal();
        List<Position> records = positions.getResult();

        // 如果没有数据，直接返回空结果
        if (records.isEmpty()) {
            log.info("分页查询岗位列表完成，未查询到数据");
            return new PageResult<>(total, new ArrayList<>());
        }

        // 转换为VO
        List<PositionVO> positionVOList = records.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        log.info("分页查询岗位列表完成，共查询到{}条记录", positionVOList.size());
        return new PageResult<>(total, positionVOList);
    }

    /**
     * 创建岗位
     */
    @Transactional
    public void createPosition(PositionCreateDTO createDTO) {
        log.info("开始创建岗位，岗位名称: {}", createDTO.getName());
        
        // 检查岗位名称是否已存在
        if (positionMapper.countByName(createDTO.getName(), null) > 0) {
            throw new PositionNameDuplicateException(MessageConstant.POSITION_NAME_DUPLICATE);
        }

        Position position = new Position();
        BeanUtils.copyProperties(createDTO, position);
        position.setCreatedAt(LocalDateTime.now());
        position.setUpdatedAt(LocalDateTime.now());

        positionMapper.insert(position);
        log.info("创建岗位成功，岗位ID: {}, 岗位名称: {}", 
                position.getId(), position.getName());
    }

    /**
     * 更新岗位
     */
    @Transactional
    public void updatePosition(Long id, PositionUpdateDTO positionUpdateDTO) {
        log.info("开始更新岗位，岗位ID: {}", id);
        
        Position position = positionMapper.selectById(id);
        if (position == null) {
            throw new PositionNotExistException(MessageConstant.POSITION_NOT_EXIST);
        }

        // 检查岗位名称是否已存在（排除当前岗位）
        if (positionUpdateDTO.getName() != null &&
            positionMapper.countByName(positionUpdateDTO.getName(), id) > 0) {
            throw new PositionNameDuplicateException(MessageConstant.POSITION_NAME_DUPLICATE);
        }

        BeanUtils.copyProperties(positionUpdateDTO, position);
        position.setUpdatedAt(LocalDateTime.now());

        positionMapper.updateById(position);
        log.info("更新岗位成功，岗位ID: {}, 岗位名称: {}", position.getId(), position.getName());
    }

    /**
     * 删除岗位
     */
    @Transactional
    public void deletePosition(Long id) {
        log.info("开始删除岗位，岗位ID: {}", id);
        
        Position position = positionMapper.selectById(id);
        if (position == null) {
            throw new PositionNotExistException(MessageConstant.POSITION_NOT_EXIST);
        }

        // 检查是否有人员关联此岗位]
        Integer personnelCount = positionMapper.selectPersonnelCountByPositionId(id);
        if (personnelCount > 0) {
            throw new PositionHasPersonnelException(MessageConstant.POSITION_HAS_PERSONNEL);
        }

        // 软删除
        position.setDeletedAt(LocalDateTime.now());
        positionMapper.updateById(position);
        log.info("删除岗位成功，岗位ID: {}, 岗位名称: {}", position.getId(), position.getName());
    }

    /**
     * 检查岗位名称是否可用
     */
    public Map<String, Boolean> checkNameAvailable(String name, Long excludeId) {
        if (name == null || name.trim().isEmpty()) {
            throw new PositionNameEmptyException(MessageConstant.POSITION_NAME_EMPTY);
        }
        Map<String,Boolean> result = new HashMap<>();
        result.put("available", positionMapper.countByName(name.trim(), excludeId)==0);

        return result;
    }

    /**
     * 转换为VO
     */
    private PositionVO convertToVO(Position position) {
        PositionVO vo = new PositionVO();
        BeanUtils.copyProperties(position, vo);
        
        return vo;
    }

}
