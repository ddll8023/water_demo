package com.example.demo.service;

import com.example.demo.pojo.dto.common.PageResponseDTO;
import com.example.demo.pojo.dto.system.*;
import com.example.demo.pojo.entity.system.DictData;
import com.example.demo.pojo.entity.system.DictType;
import com.example.demo.mapper.DictionaryMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据字典服务
 * 整合了字典类型和字典数据的业务逻辑处理
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DictionaryService {

    private final DictionaryMapper dictionaryMapper;

    // =================== 字典类型相关服务 ===================

    /**
     * 分页查询字典类型列表
     */
    public PageResponseDTO<DictTypeResponseDTO> getDictTypes(int page, int size, String keyword,
                                                             String typeCode, String typeName, Boolean isActive, String sort) {
        log.info("分页查询字典类型: page={}, size={}, keyword={}, typeCode={}, typeName={}, isActive={}, sort={}",
                page, size, keyword, typeCode, typeName, isActive, sort);
        
        // 使用PageHelper进行分页查询
        PageHelper.startPage(page, size);
        
        // 执行查询，不需要传入分页参数
        List<DictType> dictTypeList = dictionaryMapper.selectDictTypeList(
            keyword, typeCode, typeName, isActive, sort);
        
        // 获取分页信息
        PageInfo<DictType> pageInfo = new PageInfo<>(dictTypeList);

        // 转换为DTO
        List<DictTypeResponseDTO> dictTypeDTOs = dictTypeList.stream()
            .map(dictType -> {
                if (dictType == null) {
                    return null;
                }
                DictTypeResponseDTO dto = new DictTypeResponseDTO();
                BeanUtils.copyProperties(dictType, dto);
                return dto;
            })
            .collect(Collectors.toList());

        return new PageResponseDTO<>(
            dictTypeDTOs,
            (int) pageInfo.getTotal(),
            pageInfo.getPageNum(),
            pageInfo.getPageSize()
        );
    }

    /**
     * 根据ID查询字典类型详情
     */
    public DictTypeResponseDTO getDictTypeById(Long id) {
        log.info("根据ID查询字典类型详情: id={}", id);
        DictType dictType = dictionaryMapper.selectDictTypeById(id);
        if (dictType == null) {
            log.error("字典类型不存在: id={}", id);
            throw new RuntimeException("字典类型不存在");
        }
        
        DictTypeResponseDTO dto = new DictTypeResponseDTO();
        BeanUtils.copyProperties(dictType, dto);
        return dto;
    }

    /**
     * 根据类型编码查询字典类型
     */
    public DictTypeResponseDTO getDictTypeByCode(String typeCode) {
        log.info("根据类型编码查询字典类型: typeCode={}", typeCode);
        DictType dictType = dictionaryMapper.selectDictTypeByCode(typeCode);
        if (dictType == null) {
            log.error("字典类型不存在: typeCode={}", typeCode);
            throw new RuntimeException("字典类型不存在");
        }
        
        DictTypeResponseDTO dto = new DictTypeResponseDTO();
        BeanUtils.copyProperties(dictType, dto);
        return dto;
    }

    /**
     * 创建字典类型
     */
    @Transactional
    public DictTypeResponseDTO createDictType(DictTypeCreateDTO createDTO) {
        log.info("创建字典类型: {}", createDTO);
        // 检查类型编码是否已存在
        if (dictionaryMapper.countDictTypeByCodeExcluding(createDTO.getTypeCode(), null) > 0) {
            log.error("字典类型编码已存在: typeCode={}", createDTO.getTypeCode());
            throw new RuntimeException("字典类型编码已存在");
        }

        DictType dictType = new DictType();
        BeanUtils.copyProperties(createDTO, dictType);
        dictType.setCreatedAt(LocalDateTime.now());
        dictType.setUpdatedAt(LocalDateTime.now());

        // 使用自定义的插入方法
        int rows = dictionaryMapper.insertDictType(dictType);
        if (rows <= 0) {
            log.error("字典类型创建失败");
            throw new RuntimeException("字典类型创建失败");
        }
        
        log.info("字典类型创建成功: id={}", dictType.getId());
        
        return getDictTypeById(dictType.getId());
    }

    /**
     * 更新字典类型
     */
    @Transactional
    public DictTypeResponseDTO updateDictType(DictTypeUpdateDTO updateDTO) {
        log.info("更新字典类型: {}", updateDTO);
        // 检查字典类型是否存在
        DictType existingDictType = dictionaryMapper.selectDictTypeById(updateDTO.getId());
        if (existingDictType == null) {
            log.error("字典类型不存在: id={}", updateDTO.getId());
            throw new RuntimeException("字典类型不存在");
        }

        // 检查类型编码是否已存在（排除当前记录）
        if (dictionaryMapper.countDictTypeByCodeExcluding(updateDTO.getTypeCode(), updateDTO.getId()) > 0) {
            log.error("字典类型编码已存在: typeCode={}", updateDTO.getTypeCode());
            throw new RuntimeException("字典类型编码已存在");
        }

        DictType dictType = new DictType();
        BeanUtils.copyProperties(updateDTO, dictType);
        dictType.setUpdatedAt(LocalDateTime.now());

        dictionaryMapper.updateDictType(dictType);
        log.info("字典类型更新成功: id={}", dictType.getId());
        
        return getDictTypeById(dictType.getId());
    }

    /**
     * 删除字典类型
     */
    @Transactional
    public void deleteDictType(Long id) {
        log.info("删除字典类型: id={}", id);
        // 检查字典类型是否存在
        DictType dictType = dictionaryMapper.selectDictTypeById(id);
        if (dictType == null) {
            log.error("字典类型不存在: id={}", id);
            throw new RuntimeException("字典类型不存在");
        }

        // 删除关联的字典数据
        dictType.setDeletedAt(LocalDateTime.now());
        log.info("删除字典类型: {}", dictType);
        dictionaryMapper.updateDictType(dictType);
        log.info("删除字典类型关联的字典数据: typeId={}", id);

    }

    /**
     * 检查类型编码是否存在
     */
    public boolean checkTypeCodeExists(String typeCode, Long excludeId) {
        log.debug("检查类型编码是否存在: typeCode={}, excludeId={}", typeCode, excludeId);
        return dictionaryMapper.countDictTypeByCodeExcluding(typeCode, excludeId) > 0;
    }

    // =================== 字典数据相关服务 ===================

    /**
     * 根据ID查询字典数据详情
     */
    public DictDataResponseDTO getDictDataById(Long id) {
        log.info("根据ID查询字典数据详情: id={}", id);
        DictData dictData = dictionaryMapper.selectDictDataById(id);
        if (dictData == null) {
            log.error("字典数据不存在: id={}", id);
            throw new RuntimeException("字典数据不存在");
        }
        
        // 直接转换为DTO
        DictDataResponseDTO dto = new DictDataResponseDTO();
        BeanUtils.copyProperties(dictData, dto);
        return dto;
    }

    /**
     * 根据类型编码查询字典数据
     */
    public List<DictDataResponseDTO> getDictDataByTypeCode(String typeCode) {
        log.info("根据类型编码查询字典数据: typeCode={}", typeCode);
        List<DictData> dictDataList = dictionaryMapper.selectDictDataByTypeCode(typeCode);
        log.debug(dictDataList.toString());
        return dictDataList.stream()
            .map(dictData -> {
                if (dictData == null) {
                    return null;
                }
                DictDataResponseDTO dto = new DictDataResponseDTO();
                BeanUtils.copyProperties(dictData, dto);
                return dto;
            })
            .collect(Collectors.toList());
    }

    /**
     * 根据类型ID查询字典数据
     */
    public List<DictDataResponseDTO> getDictDataByTypeId(Long typeId) {
        log.info("根据类型ID查询字典数据: typeId={}", typeId);
        List<DictData> dictDataList = dictionaryMapper.selectDictDataByTypeId(typeId);
        return dictDataList.stream()
            .map(dictData -> {
                if (dictData == null) {
                    return null;
                }
                DictDataResponseDTO dto = new DictDataResponseDTO();
                BeanUtils.copyProperties(dictData, dto);
                return dto;
            })
            .collect(Collectors.toList());
    }

    /**
     * 创建或更新字典数据
     */
    @Transactional
    public DictDataResponseDTO createDictData(DictDataCreateDTO createDTO) {
        log.info("创建或更新字典数据: {}", createDTO);
        // 检查字典类型是否存在
        if (dictionaryMapper.selectDictTypeById(createDTO.getTypeId()) == null) {
            log.error("字典类型不存在: typeId={}", createDTO.getTypeId());
            throw new RuntimeException("字典类型不存在");
        }

        // 不再检查键值是否存在，因为我们使用ON DUPLICATE KEY UPDATE语法
        // 如果存在相同的type_id和data_value组合，将会更新该记录
        
        DictData dictData = new DictData();
        BeanUtils.copyProperties(createDTO, dictData);
        dictData.setCreatedAt(LocalDateTime.now());
        dictData.setUpdatedAt(LocalDateTime.now());

        try {
            // 使用已定义的字典数据插入或更新方法
            dictionaryMapper.insertDictData(dictData);
            
            // 检查是否为更新操作（如果id为0，说明没有实际插入新记录，而是更新了现有记录）
            if (dictData.getId() == 0) {
                // 查询更新后的记录ID
                DictData existingData = dictionaryMapper.selectDictDataByTypeIdAndValue(
                    createDTO.getTypeId(), createDTO.getDataValue());
                if (existingData != null) {
                    dictData.setId(existingData.getId());
                    log.info("字典数据更新成功: id={}", dictData.getId());
                }
            } else {
                log.info("字典数据创建成功: id={}", dictData.getId());
            }
            
            return getDictDataById(dictData.getId());
        } catch (Exception e) {
            // 其他异常直接抛出
            throw e;
        }
    }

    /**
     * 更新字典数据
     */
    @Transactional
    public DictDataResponseDTO updateDictData(DictDataUpdateDTO updateDTO) {
        log.info("更新字典数据: {}", updateDTO);
        // 检查字典数据是否存在
        DictData existingDictData = dictionaryMapper.selectDictDataById(updateDTO.getId());
        if (existingDictData == null) {
            log.error("待更新的字典数据不存在: id={}", updateDTO.getId());
            throw new RuntimeException("字典数据不存在");
        }

        // 检查字典类型是否存在
        if (dictionaryMapper.selectDictTypeById(updateDTO.getTypeId()) == null) {
            log.error("字典类型不存在: typeId={}", updateDTO.getTypeId());
            throw new RuntimeException("字典类型不存在");
        }

        // 检查同一类型下键值是否已存在（排除当前记录）
        if (dictionaryMapper.countDictDataByTypeIdAndValueExcluding(
                updateDTO.getTypeId(), updateDTO.getDataValue(), updateDTO.getId(), false) > 0) {
            log.error("同一字典类型下的键值已存在: typeId={}, dataValue={}", updateDTO.getTypeId(), updateDTO.getDataValue());
            throw new RuntimeException("同一字典类型下的键值已存在");
        }

        DictData dictData = new DictData();
        BeanUtils.copyProperties(updateDTO, dictData);
        dictData.setUpdatedAt(LocalDateTime.now());

        dictionaryMapper.updateDictData(dictData);
        log.info("字典数据更新成功: id={}", dictData.getId());
        return getDictDataById(dictData.getId());
    }

    /**
     * 删除字典数据
     */
    @Transactional
    public boolean deleteDictData(Long id) {
        log.info("删除字典数据: id={}", id);
        // 检查字典数据是否存在
        DictData dictData = dictionaryMapper.selectDictDataById(id);
        if (dictData == null) {
            log.error("待删除的字典数据不存在: id={}", id);
            throw new RuntimeException("字典数据不存在");
        }

        boolean success = dictionaryMapper.deleteDictDataById(id) > 0;
        if (success) {
            log.info("字典数据删除成功: id={}", id);
        } else {
            log.error("字典数据删除失败: id={}", id);
        }
        return success;
    }

    /**
     * 检查同一类型下键值是否存在
     */
    public boolean checkDataValueExists(Long typeId, String dataValue, Long excludeId) {
        // 默认不包含已删除记录
        return checkDataValueExists(typeId, dataValue, excludeId, false);
    }
    
    /**
     * 检查同一类型下键值是否存在（可选包含已删除记录）
     */
    public boolean checkDataValueExists(Long typeId, String dataValue, Long excludeId, boolean includeDeleted) {
        log.debug("检查键值是否存在: typeId={}, dataValue={}, excludeId={}, includeDeleted={}", typeId, dataValue, excludeId, includeDeleted);
        return dictionaryMapper.countDictDataByTypeIdAndValueExcluding(typeId, dataValue, excludeId, includeDeleted) > 0;
    }


}