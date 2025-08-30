package com.example.demo.service;

import com.example.demo.common.PageResult;
import com.example.demo.constant.EngineeringDTOConstant;
import com.example.demo.constant.MessageConstant;
import com.example.demo.exception.facility.*;
import com.example.demo.mapper.FacilityTypeMapper;
import com.example.demo.pojo.DTO.common.FacilityQueryDTO;
import com.example.demo.pojo.entity.facility.FacilityType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 工程信息服务
 * 负责工程相关的业务逻辑处理，包括设施类型管理等
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EngineeringService {

    private final FacilityTypeMapper facilityTypeMapper;
    private final FacilityServiceRouter facilityServiceRouter;

    // =================================== 设施类型管理功能 ===================================

    /**
     * 获取所有设施类型枚举
     * <p>
     * 返回系统中所有可用的设施类型列表，与实际设施实体对应
     *
     * @return 设施类型枚举列表
     */
    public List<Map<String, Object>> getFacilityTypes() {
        log.info("开始获取设施类型枚举列表");

        List<FacilityType> facilityTypes = facilityTypeMapper.selectActiveFacilityTypesSorted();

        List<Map<String, Object>> enums = facilityTypes.stream().map(this::convertToEnumMap).collect(Collectors.toList());

        log.info("成功获取设施类型枚举列表，共{}种类型", enums.size());
        return enums;
    }

    /**
     * 获取设施类型映射
     * <p>
     * 返回设施类型的键值对映射，用于快速查询和显示
     *
     * @return 设施类型映射 Map<typeCode, typeName>
     */
    public Map<String, String> getFacilityTypeMap() {
        log.info("开始获取设施类型映射");

        List<FacilityType> facilityTypes = facilityTypeMapper.selectActiveFacilityTypesSorted();

        Map<String, String> typeMap = facilityTypes.stream().collect(Collectors.toMap(FacilityType::getTypeCode, FacilityType::getTypeName, (existing, replacement) -> existing // 处理重复key，保留已存在的值
        ));

        log.info("成功获取设施类型映射，共{}种类型", typeMap.size());
        return typeMap;
    }

    /**
     * 将设施类型实体转换为枚举Map格式
     *
     * @param facilityType 设施类型实体
     * @return 枚举Map
     */
    private Map<String, Object> convertToEnumMap(FacilityType facilityType) {
        Map<String, Object> enumMap = new HashMap<>();
        BeanUtils.copyProperties(facilityType, enumMap);

        // 重命名关键字段以符合前端枚举格式
        enumMap.put("value", enumMap.remove("typeCode"));
        enumMap.put("label", enumMap.remove("typeName"));

        return enumMap;
    }

    // =================================== 设施查询功能 ===================================

    /**
     * 统一设施分页查询
     * 优化：简化调用链，增强错误处理
     */
    public PageResult<?> getFacilityPage(String facilityType, FacilityQueryDTO facilityQueryDTO) {
        log.info("开始查询设施分页数据: facilityType={}, queryDTO={}", facilityType, facilityQueryDTO);
        
        PageResult<?> result = facilityServiceRouter.routePageQuery(facilityType, facilityQueryDTO);
        log.info("完成设施分页查询: facilityType={}, 结果数量={}", facilityType, result.getTotal());
        return result;
    }

    /**
     * 统一设施详情查询
     * 优化：增强错误处理
     */
    public Object getFacilityById(String facilityType, Long id) {
        log.info("查询设施详情: facilityType={}, id={}", facilityType, id);
        
        return facilityServiceRouter.routeDetailQuery(facilityType, id);
    }

    /**
     * 统一可用设施查询
     * 优化：增强错误处理
     */
    public List<?> getAvailableFacilities(String facilityType) {
        log.info("查询可用设施列表: facilityType={}", facilityType);
        
        return facilityServiceRouter.routeAvailableQuery(facilityType);
    }

    /**
     * 统一设施统计
     * 优化：增强错误处理
     */
    public long countFacilities(String facilityType) {
        log.info("统计设施总数: facilityType={}", facilityType);
        
        return facilityServiceRouter.routeCount(facilityType);
    }

    // =================================== 设施管理功能 ===================================

    /**
     * 统一设施创建
     * 优化：改进DTO转换和错误处理
     */
    public Object createFacility(String facilityType, Map<String, Object> requestBody) {
        log.info("创建设施: facilityType={}, requestBody={}", facilityType, requestBody);
        
        Object createDTO = convertMapToCreateDTO(facilityType, requestBody);
        Object result = facilityServiceRouter.routeCreate(facilityType, createDTO);
        log.info("设施创建成功: facilityType={}", facilityType);
        return result;
    }

    /**
     * 统一设施更新
     * 优化：改进DTO转换和错误处理
     */
    public Object updateFacility(String facilityType, Long id, Map<String, Object> requestBody) {
        log.info("更新设施: facilityType={}, id={}, requestBody={}", facilityType, id, requestBody);
        
        // 确保ID在请求体中
        requestBody.put("id", id);
        Object updateDTO = convertMapToUpdateDTO(facilityType, requestBody);
        Object result = facilityServiceRouter.routeUpdate(facilityType, id, updateDTO);
        log.info("设施更新成功: facilityType={}, id={}", facilityType, id);
        return result;
    }

    /**
     * 统一设施删除
     * 优化：增强错误处理
     */
    public void deleteFacility(String facilityType, Long id) {
        log.info("删除设施: facilityType={}, id={}", facilityType, id);
        
        facilityServiceRouter.routeDelete(facilityType, id);
        log.info("设施删除成功: facilityType={}, id={}", facilityType, id);
    }

    // =================================== DTO转换功能 ===================================

    /**
     * 将Map转换为CreateDTO对象
     * 优化：增强错误处理和日志
     */
    private Object convertMapToCreateDTO(String facilityType, Map<String, Object> map) {
        FacilityType config = facilityTypeMapper.selectByApiPath(facilityType);
        if (config == null) {
            throw new FacilityTypeNotSupportedException(MessageConstant.FACILITY_TYPE_CONFIG_NOT_FOUND + ": " + facilityType);
        }
        String dtoClassName = EngineeringDTOConstant.FACILITY_DTO_PACKAGE + config.getEntityClass() + EngineeringDTOConstant.CREATE_DTO_SUFFIX;
        log.debug("转换为CreateDTO: {} -> {}", facilityType, dtoClassName);
        return convertMapToDTO(map, dtoClassName);
    }

    /**
     * 将Map转换为UpdateDTO对象
     * 优化：增强错误处理和日志
     */
    private Object convertMapToUpdateDTO(String facilityType, Map<String, Object> map) {
        FacilityType config = facilityTypeMapper.selectByApiPath(facilityType);
        if (config == null) {
            throw new FacilityTypeNotSupportedException(MessageConstant.FACILITY_TYPE_CONFIG_NOT_FOUND + ": " + facilityType);
        }
        String dtoClassName = EngineeringDTOConstant.FACILITY_DTO_PACKAGE + config.getEntityClass() + EngineeringDTOConstant.UPDATE_DTO_SUFFIX;
        log.debug("转换为UpdateDTO: {} -> {}", facilityType, dtoClassName);
        return convertMapToDTO(map, dtoClassName);
    }

    /**
     * 辅助方法：将Map转换为DTO对象
     * 优化：增强错误处理和配置
     */
    private Object convertMapToDTO(Map<String, Object> map, String dtoClassName) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            // 忽略未知属性，增强兼容性
            mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            String json = mapper.writeValueAsString(map);
            Class<?> dtoClass = Class.forName(dtoClassName);
            Object dto = mapper.readValue(json, dtoClass);
            
            log.debug("DTO转换成功: {} -> {}", dtoClassName, dto);
            return dto;
        } catch (ClassNotFoundException e) {
            log.error("DTO类不存在: className={}, error={}", dtoClassName, e.getMessage());
            throw new FacilityDTOConvertException(MessageConstant.FACILITY_DTO_CLASS_NOT_FOUND + ": " + dtoClassName);
        } catch (Exception e) {
            log.error("DTO转换失败: className={}, map={}, error={}", dtoClassName, map, e.getMessage());
            throw new FacilityDTOConvertException(MessageConstant.FACILITY_JSON_CONVERT_FAILED + ": " + e.getMessage());
        }
    }
} 