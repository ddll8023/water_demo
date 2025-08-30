package com.example.demo.service;

import com.example.demo.common.PageResult;
import com.example.demo.constant.EngineeringDTOConstant;
import com.example.demo.constant.MessageConstant;
import com.example.demo.exception.facility.*;
import com.example.demo.pojo.DTO.common.FacilityQueryDTO;
import com.example.demo.pojo.entity.facility.FacilityType;
import com.example.demo.service.base.FacilityService;
import com.example.demo.service.base.FacilityServiceRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 设施服务路由器
 * 基于统一接口和服务注册器，消除方法名硬编码拼接问题
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FacilityServiceRouter {

    private final FacilityServiceRegistry facilityServiceRegistry;

    /**
     * 路由分页查询请求
     * 使用统一接口方法 queryPage，消除硬编码方法名拼接
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public PageResult<?> routePageQuery(String facilityType, FacilityQueryDTO facilityQueryDTO) {
        log.info("路由分页查询请求: facilityType={}, facilityQueryDTO={}", facilityType, facilityQueryDTO);
        
        // 获取对应的服务实例
        FacilityService service = facilityServiceRegistry.getFacilityService(facilityType);
        
        // 创建查询DTO对象
        Object queryDTO = createQueryDTO(facilityType, facilityQueryDTO);
        
        // 调用统一接口方法
        return service.queryPage(queryDTO);
    }

    /**
     * 路由详情查询请求
     * 使用统一接口方法 queryById，消除硬编码方法名拼接
     */
    @SuppressWarnings({"rawtypes"})
    public Object routeDetailQuery(String facilityType, Long id) {
        log.info("路由详情查询请求: facilityType={}, id={}", facilityType, id);
        
        FacilityService service = facilityServiceRegistry.getFacilityService(facilityType);
        Object result = service.queryById(id);
        if (result == null) {
            throw new FacilityNotFoundException(MessageConstant.FACILITY_NOT_EXIST + ": id=" + id);
        }
        return result;
    }

    /**
     * 路由创建请求
     * 使用统一接口方法 create，消除硬编码方法名拼接
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Object routeCreate(String facilityType, Object createDTO) {
        log.info("路由创建请求: facilityType={}, createDTO={}", facilityType, createDTO);
        
        FacilityService service = facilityServiceRegistry.getFacilityService(facilityType);
        return service.create(createDTO);
    }

    /**
     * 路由更新请求
     * 使用统一接口方法 update，消除硬编码方法名拼接和参数不一致问题
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Object routeUpdate(String facilityType, Long id, Object updateDTO) {
        log.info("路由更新请求: facilityType={}, id={}, updateDTO={}", facilityType, id, updateDTO);
        
        FacilityService service = facilityServiceRegistry.getFacilityService(facilityType);
        
        // 确保updateDTO包含ID字段
        ensureUpdateDTOHasId(updateDTO, id);
        
        return service.update(updateDTO);
    }

    /**
     * 路由删除请求
     * 使用统一接口方法 delete，消除硬编码方法名拼接
     */
    @SuppressWarnings({"rawtypes"})
    public void routeDelete(String facilityType, Long id) {
        log.info("路由删除请求: facilityType={}, id={}", facilityType, id);
        
        FacilityService service = facilityServiceRegistry.getFacilityService(facilityType);
        service.delete(id);
    }

    /**
     * 路由可用列表查询
     * 使用统一接口方法 queryAvailable，消除硬编码方法名拼接
     */
    @SuppressWarnings({"rawtypes"})
    public List<?> routeAvailableQuery(String facilityType) {
        log.info("路由可用列表查询: facilityType={}", facilityType);
        
        FacilityService service = facilityServiceRegistry.getFacilityService(facilityType);
        return service.queryAvailable();
    }

    /**
     * 路由统计请求
     * 使用统一接口方法 countTotal，消除硬编码方法名拼接
     */
    @SuppressWarnings({"rawtypes"})
    public long routeCount(String facilityType) {
        log.info("路由统计请求: facilityType={}", facilityType);
        
        FacilityService service = facilityServiceRegistry.getFacilityService(facilityType);
        return service.countTotal();
    }

    /**
     * 创建查询DTO对象
     */
    private Object createQueryDTO(String facilityType, FacilityQueryDTO facilityQueryDTO) {
        log.info("创建查询DTO对象: facilityType={}, facilityQueryDTO={}", facilityType, facilityQueryDTO);
        
        try {
            // 获取设施类型配置
            FacilityType config = facilityServiceRegistry.getFacilityConfig(facilityType);
            if (config == null) {
                throw new FacilityTypeNotSupportedException(MessageConstant.FACILITY_TYPE_CONFIG_NOT_FOUND + ": " + facilityType);
            }
            
            // 通过反射创建对应的DTO实例
            String dtoClassName = EngineeringDTOConstant.FACILITY_DTO_PACKAGE + config.getEntityClass() + EngineeringDTOConstant.QUERY_DTO_SUFFIX;
            Class<?> dtoClass = Class.forName(dtoClassName);
            Object dto = dtoClass.getDeclaredConstructor().newInstance();
            
            // 直接设置分页字段
            setFieldValue(dto, "page", facilityQueryDTO.getPage());
            setFieldValue(dto, "size", facilityQueryDTO.getSize());
            setFieldValue(dto, "keyword", facilityQueryDTO.getKeyword());
            
            log.info("设置分页字段: page={}, size={}, keyword={}", 
                facilityQueryDTO.getPage(), facilityQueryDTO.getSize(), facilityQueryDTO.getKeyword());

            // 设置特殊字段
            if ("floating-boats".equals(facilityType)) {
                setFieldValue(dto, "pumpingStatus", facilityQueryDTO.getPumpingStatus());
            } else if ("pipelines".equals(facilityType)) {
                setFieldValue(dto, "pipelineType", facilityQueryDTO.getPipelineType());
            }

            return dto;
        } catch (ClassNotFoundException e) {
            log.error("查询DTO类不存在: facilityType={}, error={}", facilityType, e.getMessage());
            throw new FacilityDTOConvertException(MessageConstant.FACILITY_DTO_CLASS_NOT_FOUND + ": " + facilityType);
        } catch (Exception e) {
            log.error("创建查询DTO失败: facilityType={}, error={}", facilityType, e.getMessage());
            throw new FacilityDTOConvertException(MessageConstant.FACILITY_DTO_INSTANTIATE_FAILED + ": " + e.getMessage());
        }
    }

    /**
     * 确保UpdateDTO包含ID字段
     */
    private void ensureUpdateDTOHasId(Object updateDTO, Long id) {
        try {
            Method setIdMethod = updateDTO.getClass().getMethod("setId", Long.class);
            setIdMethod.invoke(updateDTO, id);
            log.debug("设置UpdateDTO的ID字段: {}", id);
        } catch (NoSuchMethodException e) {
            log.warn("UpdateDTO {} 没有setId方法", updateDTO.getClass().getSimpleName());
        } catch (Exception e) {
            log.error("设置UpdateDTO的ID字段失败: {}", e.getMessage());
            throw new FacilityOperationException("设置UpdateDTO的ID字段失败: " + e.getMessage());
        }
    }

    /**
     * 设置对象字段值
     */
    private void setFieldValue(Object obj, String fieldName, Object value) throws FieldSettingException {
        if (value == null) {
            return;
        }
        
        String setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        
        try {
            log.debug("设置字段值: {}.{} = {}", obj.getClass().getSimpleName(), fieldName, value);
            // 获取setter方法
            Method setter = obj.getClass().getMethod(setterName, value.getClass());
            // 调用setter方法
            setter.invoke(obj, value);
        } catch (NoSuchMethodException e) {
            throw new FieldSettingException(String.format("字段设置失败：%s.%s方法不存在", obj.getClass().getSimpleName(), setterName), e);
        } catch (Exception e) {
            throw new FieldSettingException(String.format("字段设置失败：%s.%s = %s", obj.getClass().getSimpleName(), fieldName, value), e);
        }
    }
} 