package com.example.demo.service;

import com.example.demo.common.PageResult;
import com.example.demo.constant.CommonConstant;
import com.example.demo.constant.MessageConstant;
import com.example.demo.exception.*;
import com.example.demo.mapper.DictionaryMapper;
import com.example.demo.pojo.DTO.system.*;
import com.example.demo.pojo.VO.DictDataVO;
import com.example.demo.pojo.VO.DictTypeVO;
import com.example.demo.pojo.entity.system.DictData;
import com.example.demo.pojo.entity.system.DictType;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
     *
     * @param dictTypeQueryDTO 字典类型查询参数，包含分页信息和过滤条件
     * @return PageResult<DictTypeVO> 分页查询结果，包含字典类型列表和分页信息
     */
    public PageResult<DictTypeVO> getDictTypes(DictTypeQueryDTO dictTypeQueryDTO) {
        log.info("开始查询字典类型分页数据，查询条件: {}", dictTypeQueryDTO);

        // 使用PageHelper开始分页
        PageHelper.startPage(dictTypeQueryDTO.getPage(), dictTypeQueryDTO.getSize());

        // 查询基础字典类型数据
        Page<DictType> dictTypes = dictionaryMapper.selectDictTypeList(dictTypeQueryDTO);

        // 获取分页信息
        long total = dictTypes.getTotal();
        List<DictType> records = dictTypes.getResult();

        // 转换为VO并补充关联信息
        List<DictTypeVO> dictTypeVOList = records.stream()
                .map(this::convertToVO)  // 转换为VO
                .collect(Collectors.toList());

        log.info("查询完成，返回 {} 条记录，总计 {} 条", dictTypeVOList.size(), total);
        return new PageResult<>(total, dictTypeVOList);
    }

    /**
     * 将DictType实体转换为DictTypeVO，并补充关联信息
     */
    private DictTypeVO convertToVO(DictType dictType) {
        if (dictType == null) {
            return null;
        }
        DictTypeVO dictTypeVO = new DictTypeVO();
        BeanUtils.copyProperties(dictType, dictTypeVO);

        // 查询并设置关联信息
        dictTypeVO.setDataCount(dictionaryMapper.countDictDataByTypeId(dictType.getId()));

        return dictTypeVO;
    }

    /**
     * 将DictData实体转换为DictDataVO，并补充关联信息
     */
    private DictDataVO convertToDictDataVO(DictData dictData) {
        if (dictData == null) {
            return null;
        }
        DictDataVO dictDataVO = new DictDataVO();
        BeanUtils.copyProperties(dictData, dictDataVO);

        // 查询并设置关联的字典类型信息
        if (dictData.getTypeId() != null) {
            DictType dictType = dictionaryMapper.selectDictTypeById(dictData.getTypeId());
            if (dictType != null) {
                dictDataVO.setTypeCode(dictType.getTypeCode());
                dictDataVO.setTypeName(dictType.getTypeName());
            }
        }

        return dictDataVO;
    }

    /**
     * 根据ID查询字典类型详情
     */
    public DictTypeVO getDictTypeById(Long id) {
        log.info("根据ID查询字典类型详情: id={}", id);

        // 参数验证
        if (id == null) {
            throw new DictQueryParamInvalidException(MessageConstant.DICT_TYPE_ID_NULL);
        }

        DictType dictType = dictionaryMapper.selectDictTypeById(id);
        if (dictType == null) {
            throw new DictTypeNotExistException(MessageConstant.DICT_TYPE_NOT_EXIST);
        }

        // 使用convertToVO方法，确保包含完整的关联信息
        DictTypeVO dictTypeVO = convertToVO(dictType);

        log.info("字典类型查询成功: id={}, typeCode={}", id, dictType.getTypeCode());
        return dictTypeVO;
    }

    /**
     * 根据类型编码查询字典类型
     */
    public DictTypeVO getDictTypeByCode(String typeCode) {
        log.info("根据类型编码查询字典类型: typeCode={}", typeCode);

        // 参数验证
        if (typeCode == null || typeCode.trim().isEmpty()) {
            throw new DictQueryParamInvalidException(MessageConstant.DICT_TYPE_CODE_NULL);
        }

        DictType dictType = dictionaryMapper.selectDictTypeByCode(typeCode);
        if (dictType == null) {
            throw new DictTypeNotExistException(MessageConstant.DICT_TYPE_NOT_EXIST);
        }

        // 使用convertToVO方法，确保包含完整的关联信息
        DictTypeVO dictTypeVO = convertToVO(dictType);

        log.info("字典类型查询成功: typeCode={}, typeName={}", typeCode, dictType.getTypeName());
        return dictTypeVO;
    }

    /**
     * 创建字典类型
     */
    @Transactional
    public void createDictType(DictTypeCreateDTO dictTypeCreateDTO) {
        log.info("创建字典类型: {}", dictTypeCreateDTO);

        // 参数验证
        if (dictTypeCreateDTO == null) {
            throw new DictTypeCreateException(MessageConstant.DICT_TYPE_CREATE_PARAM_INVALID);
        }

        // 检查类型编码是否已存在
        if (dictionaryMapper.countDictTypeByCodeExcluding(dictTypeCreateDTO.getTypeCode(), null) > 0) {
            throw new DictTypeCreateException(MessageConstant.DICT_TYPE_CODE_DUPLICATE);
        }

        DictType dictType = new DictType();
        BeanUtils.copyProperties(dictTypeCreateDTO, dictType);

        // 确保isActive字段有默认值
        if (dictTypeCreateDTO.getIsActive() == null) {
            dictType.setIsActive(CommonConstant.ACTIVE_STATUS_ENABLED); // 默认启用
        }

        dictType.setCreatedAt(LocalDateTime.now());
        dictType.setUpdatedAt(LocalDateTime.now());

        // 使用自定义的插入方法
        int rows = dictionaryMapper.insertDictType(dictType);
        if (rows <= 0) {
            log.error("字典类型创建失败");
            throw new DictTypeCreateException(MessageConstant.DICT_TYPE_CREATE_FAILED);
        }

        log.info("字典类型创建成功: id={}", dictType.getId());
    }

    /**
     * 更新字典类型
     */
    @Transactional
    public void updateDictType(DictTypeUpdateDTO dictTypeUpdateDTO) {
        log.info("更新字典类型: {}", dictTypeUpdateDTO);

        // 检查字典类型是否存在
        DictType existingDictType = dictionaryMapper.selectDictTypeById(dictTypeUpdateDTO.getId());
        if (existingDictType == null) {
            throw new DictTypeNotExistException(MessageConstant.DICT_TYPE_NOT_EXIST);
        }

        // 检查类型编码是否已存在（排除当前记录）
        if (dictionaryMapper.countDictTypeByCodeExcluding(dictTypeUpdateDTO.getTypeCode(), dictTypeUpdateDTO.getId()) > 0) {
            throw new DictTypeUpdateException(MessageConstant.DICT_TYPE_CODE_DUPLICATE);
        }

        DictType dictType = new DictType();
        BeanUtils.copyProperties(dictTypeUpdateDTO, dictType);
        dictType.setUpdatedAt(LocalDateTime.now());

        int rows = dictionaryMapper.updateDictType(dictType);
        if (rows <= 0) {
            throw new DictTypeUpdateException(MessageConstant.DICT_TYPE_UPDATE_FAILED);
        }

        log.info("字典类型更新成功: id={}", dictType.getId());
    }

    /**
     * 删除字典类型
     */
    @Transactional
    public void deleteDictType(Long id) {
        log.info("开始删除字典类型: id={}", id);

        // 参数验证
        if (id == null) {
            throw new DictQueryParamInvalidException(MessageConstant.DICT_TYPE_ID_NULL);
        }

        // 检查字典类型是否存在
        DictType dictType = dictionaryMapper.selectDictTypeById(id);
        if (dictType == null) {
            throw new DictTypeNotExistException(MessageConstant.DICT_TYPE_NOT_EXIST);
        }

        // 检查是否有关联的字典数据
        int dataCount = dictionaryMapper.countDictDataByTypeId(id);
        if (dataCount > 0) {
            throw new DictTypeDeleteException(MessageConstant.DICT_TYPE_HAS_DATA);
        }

        // 执行软删除
        dictType.setDeletedAt(LocalDateTime.now());

        int rows = dictionaryMapper.updateDictType(dictType);
        if (rows <= 0) {
            log.error("字典类型删除失败: id={}", id);
            throw new DictTypeDeleteException(MessageConstant.DICT_TYPE_DELETE_FAILED);
        }

        log.info("字典类型删除成功: id={}", id);
    }

    /**
     * 检查类型编码是否存在
     */
    public Map<String, Boolean> checkTypeCodeExists(String typeCode, Long excludeId) {
        log.info("开始检查类型编码是否存在: typeCode={}, excludeId={}", typeCode, excludeId);

        // 参数验证
        if (typeCode == null || typeCode.trim().isEmpty()) {
            throw new DictQueryParamInvalidException(MessageConstant.DICT_TYPE_CODE_NULL);
        }

        boolean exists = dictionaryMapper.countDictTypeByCodeExcluding(typeCode, excludeId) > 0;

        Map<String, Boolean> result = new HashMap<>();
        result.put("exists", exists);

        log.info("类型编码检查完成: typeCode={}, exists={}", typeCode, exists);
        return result;
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
     *
     * @deprecated 此方法返回ResponseDTO用于Service内部调用的兼容性
     * 新的Controller层应使用getDictDataVOByTypeCode方法获取包含关联信息的VO对象
     * 当前保留此方法是为了兼容WarningRecordService和InspectionService的调用
     */
    public List<DictDataResponseDTO> getDictDataByTypeCode(String typeCode) {
        log.info("根据类型编码查询字典数据: typeCode={}", typeCode);

        // 参数验证
        if (typeCode == null || typeCode.trim().isEmpty()) {
            throw new DictQueryParamInvalidException(MessageConstant.DICT_DATA_TYPE_CODE_NULL);
        }

        List<DictData> dictDataList = dictionaryMapper.selectDictDataByTypeCode(typeCode);
        log.debug("查询到字典数据记录数: {}", dictDataList.size());

        // 转换为ResponseDTO（兼容现有Service调用）
        List<DictDataResponseDTO> dictDataResponseDTOList = dictDataList.stream()
                .map(dictData -> {
                    if (dictData == null) {
                        return null;
                    }
                    DictDataResponseDTO dto = new DictDataResponseDTO();
                    BeanUtils.copyProperties(dictData, dto);
                    return dto;
                })
                .collect(Collectors.toList());

        log.info("字典数据查询完成: typeCode={}, 返回 {} 条记录", typeCode, dictDataResponseDTOList.size());
        return dictDataResponseDTOList;
    }

    /**
     * 根据类型编码查询字典数据（返回VO）
     * 用于Controller层，返回包含关联信息的完整VO对象
     */
    public List<DictDataVO> getDictDataVOByTypeCode(String typeCode) {
        log.info("根据类型编码查询字典数据VO: typeCode={}", typeCode);

        // 参数验证
        if (typeCode == null || typeCode.trim().isEmpty()) {
            throw new DictQueryParamInvalidException(MessageConstant.DICT_DATA_TYPE_CODE_NULL);
        }

        List<DictData> dictDataList = dictionaryMapper.selectDictDataByTypeCode(typeCode);
        log.debug("查询到字典数据记录数: {}", dictDataList.size());

        // 转换为VO并补充关联信息
        List<DictDataVO> dictDataVOList = dictDataList.stream()
                .map(this::convertToDictDataVO)
                .collect(Collectors.toList());

        log.info("字典数据VO查询完成: typeCode={}, 返回 {} 条记录", typeCode, dictDataVOList.size());
        return dictDataVOList;
    }

    /**
     * 根据类型ID查询字典数据（返回VO）
     * 用于Controller层，返回包含关联信息的完整VO对象
     */
    public List<DictDataVO> getDictDataVOByTypeId(Long typeId) {
        log.info("根据类型ID查询字典数据VO: typeId={}", typeId);

        // 参数验证
        if (typeId == null) {
            throw new DictQueryParamInvalidException(MessageConstant.DICT_DATA_TYPE_ID_NULL);
        }

        List<DictData> dictDataList = dictionaryMapper.selectDictDataByTypeId(typeId);
        log.debug("查询到字典数据记录数: {}", dictDataList.size());

        // 转换为VO并补充关联信息
        List<DictDataVO> dictDataVOList = dictDataList.stream()
                .map(this::convertToDictDataVO)
                .collect(Collectors.toList());

        log.info("字典数据VO查询完成: typeId={}, 返回 {} 条记录", typeId, dictDataVOList.size());
        return dictDataVOList;
    }

    /**
     * 创建或更新字典数据
     */
    @Transactional
    public void createDictData(DictDataCreateDTO createDTO) {
        log.info("创建或更新字典数据: {}", createDTO);

        // 参数验证
        if (createDTO == null) {
            throw new DictDataCreateException(MessageConstant.DICT_DATA_CREATE_PARAM_INVALID);
        }

        // 检查字典类型是否存在
        if (dictionaryMapper.selectDictTypeById(createDTO.getTypeId()) == null) {
            throw new DictTypeNotExistException(MessageConstant.DICT_TYPE_NOT_EXIST);
        }

        DictData dictData = new DictData();
        BeanUtils.copyProperties(createDTO, dictData);

        // 确保isActive字段有默认值
        if (createDTO.getIsActive() == null) {
            dictData.setIsActive(CommonConstant.ACTIVE_STATUS_ENABLED); // 默认启用
        }

        dictData.setCreatedAt(LocalDateTime.now());
        dictData.setUpdatedAt(LocalDateTime.now());

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
    }

    /**
     * 更新字典数据
     */
    @Transactional
    public void updateDictData(DictDataUpdateDTO dictDataUpdateDTO) {
        log.info("开始更新字典数据，入参: {}", dictDataUpdateDTO);
        
        // 检查字典数据是否存在
        DictData existingDictData = dictionaryMapper.selectDictDataById(dictDataUpdateDTO.getId());
        if (existingDictData == null) {
            log.error("待更新的字典数据不存在: id={}", dictDataUpdateDTO.getId());
            throw new DictQueryParamInvalidException(MessageConstant.DICT_DATA_NOT_EXIST);
        }

        // 检查字典类型是否存在
        if (dictionaryMapper.selectDictTypeById(dictDataUpdateDTO.getTypeId()) == null) {
            log.error("字典类型不存在: typeId={}", dictDataUpdateDTO.getTypeId());
            throw new DictTypeNotExistException(MessageConstant.DICT_TYPE_NOT_EXIST);
        }

        // 检查同一类型下键值是否已存在（排除当前记录）
        if (dictionaryMapper.countDictDataByTypeIdAndValueExcluding(
                dictDataUpdateDTO.getTypeId(), dictDataUpdateDTO.getDataValue(), dictDataUpdateDTO.getId(), false) > 0) {
            log.error("同一字典类型下的键值已存在: typeId={}, dataValue={}", dictDataUpdateDTO.getTypeId(), dictDataUpdateDTO.getDataValue());
            throw new DictDataCreateException(MessageConstant.DICT_DATA_VALUE_DUPLICATE);
        }

        DictData dictData = new DictData();
        BeanUtils.copyProperties(dictDataUpdateDTO, dictData);
        dictData.setUpdatedAt(LocalDateTime.now());

        dictionaryMapper.updateDictData(dictData);
        log.info("字典数据更新成功: id={}", dictData.getId());
    }

    /**
     * 删除字典数据
     */
    @Transactional
    public void deleteDictData(Long id) {
        log.info("删除字典数据: id={}", id);

        // 参数验证
        if (id == null) {
            throw new DictQueryParamInvalidException(MessageConstant.DICT_QUERY_PARAM_INVALID);
        }

        // 检查字典数据是否存在
        DictData dictData = dictionaryMapper.selectDictDataById(id);
        if (dictData == null) {
            log.error("待删除的字典数据不存在: id={}", id);
            throw new DictQueryParamInvalidException(MessageConstant.DICT_DATA_NOT_EXIST);
        }

        int rows = dictionaryMapper.deleteDictDataById(id);
        if (rows <= 0) {
            log.error("字典数据删除失败: id={}", id);
            throw new DictDataDeleteException(MessageConstant.DICT_DATA_DELETE_FAILED);
        }

        log.info("字典数据删除成功: id={}", id);
    }


}