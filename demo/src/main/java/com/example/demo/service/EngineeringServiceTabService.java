package com.example.demo.service;

import com.example.demo.common.PageResult;
import com.example.demo.constant.CommonConstant;
import com.example.demo.constant.MessageConstant;
import com.example.demo.exception.system.UserCreateException;
import com.example.demo.exception.system.UserNotExistException;
import com.example.demo.mapper.EngineeringServiceTabMapper;
import com.example.demo.pojo.DTO.system.EngineeringServiceTabCreateDTO;
import com.example.demo.pojo.DTO.system.EngineeringServiceTabQueryDTO;
import com.example.demo.pojo.DTO.system.EngineeringServiceTabUpdateDTO;
import com.example.demo.pojo.VO.EngineeringServiceTabVO;
import com.example.demo.pojo.entity.system.EngineeringServiceTab;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 工程信息服务Tab配置管理服务
 * 专注于Tab配置的增删改查和业务逻辑处理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EngineeringServiceTabService {

    private final EngineeringServiceTabMapper tabMapper;

    /**
     * 分页查询Tab配置列表
     */
    public PageResult<EngineeringServiceTabVO> getTabConfigurations(EngineeringServiceTabQueryDTO queryDTO) {
        log.info("开始查询Tab配置分页数据，查询条件: {}", queryDTO);

        // 使用PageHelper进行分页
        PageHelper.startPage(queryDTO.getPage(), queryDTO.getSize());

        // 执行查询
        Page<EngineeringServiceTab> tabs = tabMapper.selectTabPageWithDetails(queryDTO);

        long total = tabs.getTotal();
        List<EngineeringServiceTab> records = tabs.getResult();

        List<EngineeringServiceTabVO> tabVOList = records.stream()
                .map(this::convertToTabVO)
                .collect(Collectors.toList());

        log.info("查询完成，返回 {} 条记录", total);

        return new PageResult<>(total, tabVOList);
    }

    /**
     * 创建Tab配置
     */
    public void createTabConfiguration(EngineeringServiceTabCreateDTO createDTO) {
        log.info("开始创建Tab配置: {}", createDTO);

        // 检查tabKey是否已存在
        if (tabMapper.countByTabKey(createDTO.getTabKey()) > 0) {
            log.error("Tab配置创建失败，tabKey已存在: {}", createDTO.getTabKey());
            throw new UserCreateException("Tab标识键已存在");
        }

        // 创建Tab配置实体
        EngineeringServiceTab tab = new EngineeringServiceTab();
        BeanUtils.copyProperties(createDTO, tab);

        // 如果没有设置排序顺序，则自动分配
        if (tab.getSortOrder() == null) {
            Integer maxSortOrder = tabMapper.selectMaxSortOrder();
            tab.setSortOrder(maxSortOrder + 1);
        }

        tab.setIsVisible(createDTO.getIsVisible() != null ? createDTO.getIsVisible() : CommonConstant.ACTIVE_STATUS_ENABLED);
        tab.setCreatedAt(LocalDateTime.now());
        tab.setUpdatedAt(LocalDateTime.now());

        // 保存Tab配置
        tabMapper.insertTab(tab);

        log.info("Tab配置创建成功，ID: {}", tab.getId());
    }

    /**
     * 更新Tab配置
     */
    public void updateTabConfiguration(Long id, EngineeringServiceTabUpdateDTO updateDTO) {
        log.info("开始更新Tab配置: {}", updateDTO);

        EngineeringServiceTab existingTab = tabMapper.selectById(id);
        // 检查Tab配置是否存在
        if (existingTab == null) {
            log.error("Tab配置更新失败，配置不存在: {}", id);
            throw new UserNotExistException(MessageConstant.ACCOUNT_NOT_EXIST);
        }

        // 检查tabKey是否重复（排除自身）
        if (updateDTO.getTabKey() != null && !updateDTO.getTabKey().equals(existingTab.getTabKey())) {
            if (tabMapper.countByTabKey(updateDTO.getTabKey()) > 0) {
                log.error("Tab配置更新失败，tabKey已存在: {}", updateDTO.getTabKey());
                throw new UserCreateException("Tab标识键已存在");
            }
        }

        // 更新Tab配置信息
        if (updateDTO.getTabName() != null) {
            existingTab.setTabName(updateDTO.getTabName());
        }
        if (updateDTO.getTabIcon() != null) {
            existingTab.setTabIcon(updateDTO.getTabIcon());
        }
        if (updateDTO.getSortOrder() != null) {
            existingTab.setSortOrder(updateDTO.getSortOrder());
        }
        if (updateDTO.getIsVisible() != null) {
            existingTab.setIsVisible(updateDTO.getIsVisible());
        }

        existingTab.setUpdatedAt(LocalDateTime.now());

        // 保存更新
        tabMapper.updateById(existingTab);
        log.info("Tab配置更新成功");
    }

    /**
     * 删除Tab配置（软删除）
     */
    public void deleteTabConfiguration(Long id) {
        log.info("开始删除Tab配置: {}", id);

        EngineeringServiceTab tab = tabMapper.selectById(id);
        if (tab == null) {
            log.error("Tab配置删除失败，配置不存在: {}", id);
            throw new UserNotExistException(MessageConstant.ACCOUNT_NOT_EXIST);
        }

        // 执行软删除
        tabMapper.deleteById(id);
        log.info("Tab配置删除成功");
    }

    /**
     * 获取所有可见的Tab配置（用于工程服务页面）
     */
    public List<EngineeringServiceTabVO> getVisibleTabConfigurations() {
        log.info("获取所有可见的Tab配置");

        List<EngineeringServiceTab> tabs = tabMapper.selectVisibleTabs();
        List<EngineeringServiceTabVO> tabVOList = tabs.stream()
                .map(this::convertToTabVO)
                .collect(Collectors.toList());

        log.info("获取到 {} 个可见Tab配置", tabVOList.size());
        return tabVOList;
    }

    /**
     * 批量更新Tab配置的排序顺序
     */
    public void batchUpdateSortOrder(List<EngineeringServiceTabUpdateDTO> updateDTOList) {
        log.info("开始批量更新Tab配置排序顺序，数量: {}", updateDTOList.size());

        List<EngineeringServiceTab> tabs = updateDTOList.stream()
                .map(dto -> {
                    EngineeringServiceTab tab = new EngineeringServiceTab();
                    tab.setId(dto.getId());
                    tab.setSortOrder(dto.getSortOrder());
                    return tab;
                })
                .collect(Collectors.toList());

        tabMapper.batchUpdateSortOrder(tabs);
        log.info("批量更新Tab配置排序顺序成功");
    }

    /**
     * 更新Tab配置的显示状态
     */
    public void updateTabVisibility(String tabKey, String isVisible) {
        log.info("更新Tab配置显示状态: tabKey={}, isVisible={}", tabKey, isVisible);

        tabMapper.updateVisibilityByTabKey(tabKey, isVisible);
        log.info("Tab配置显示状态更新成功");
    }

    /**
     * 实体转换为VO
     */
    private EngineeringServiceTabVO convertToTabVO(EngineeringServiceTab tab) {
        EngineeringServiceTabVO tabVO = new EngineeringServiceTabVO();
        BeanUtils.copyProperties(tab, tabVO);

        // 设置显示状态标签
        tabVO.setIsVisibleLabel(CommonConstant.ACTIVE_STATUS_ENABLED.equals(tab.getIsVisible()) ? "显示" : "隐藏");

        // TODO: 可以在这里查询设施数量等关联信息
        tabVO.setFacilityCount(0L);

        return tabVO;
    }

} 