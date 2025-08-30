package com.example.demo.service.base;

import com.example.demo.constant.MessageConstant;
import com.example.demo.exception.facility.FacilityServiceNotRegisteredException;
import com.example.demo.exception.facility.FacilityTypeNotSupportedException;
import com.example.demo.mapper.FacilityTypeMapper;
import com.example.demo.pojo.entity.facility.FacilityType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设施服务注册器
 * 基于Spring Bean名称管理设施服务，替代反射调用，消除方法名硬编码拼接问题
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FacilityServiceRegistry {

    private final ApplicationContext applicationContext;
    private final FacilityTypeMapper facilityTypeMapper;

    // 缓存设施类型配置
    private final Map<String, FacilityType> facilityConfigCache = new HashMap<>();
    
    // 缓存设施服务Bean实例
    private final Map<String, FacilityService<?, ?, ?, ?, ?, ?>> facilityServiceCache = new HashMap<>();

    @PostConstruct
    public void initRegistry() {
        log.info("初始化设施服务注册器");
        
        // 初始化设施类型配置缓存
        initFacilityConfigCache();
        
        // 初始化设施服务Bean缓存
        initFacilityServiceCache();
        
        log.info("设施服务注册器初始化完成，共{}种设施类型，{}个服务Bean", 
            facilityConfigCache.size(), facilityServiceCache.size());
    }

    /**
     * 根据设施类型获取对应的服务Bean
     */
    @SuppressWarnings("unchecked")
    public <T extends FacilityService<?, ?, ?, ?, ?, ?>> T getFacilityService(String facilityTypeName) {
        log.debug("获取设施服务: {}", facilityTypeName);
        
        // 先从缓存获取
        FacilityService<?, ?, ?, ?, ?, ?> service = facilityServiceCache.get(facilityTypeName);
        if (service != null) {
            return (T) service;
        }
        
        // 缓存未命中，尝试动态获取
        FacilityType config = getFacilityConfig(facilityTypeName);
        if (config == null) {
            throw new FacilityTypeNotSupportedException(MessageConstant.FACILITY_TYPE_CONFIG_NOT_FOUND + ": " + facilityTypeName);
        }
        
        service = loadFacilityService(config);
        if (service == null) {
            throw new FacilityServiceNotRegisteredException(MessageConstant.FACILITY_SERVICE_NOT_REGISTERED + ": " + facilityTypeName);
        }
        
        facilityServiceCache.put(facilityTypeName, service);
        return (T) service;
    }

    /**
     * 获取设施类型配置
     */
    public FacilityType getFacilityConfig(String facilityTypeName) {
        FacilityType config = facilityConfigCache.get(facilityTypeName);
        if (config == null) {
            // 缓存未命中，从数据库查询
            config = facilityTypeMapper.selectByApiPath(facilityTypeName);
            if (config != null) {
                facilityConfigCache.put(facilityTypeName, config);
            }
        }
        return config;
    }

    /**
     * 初始化设施类型配置缓存
     */
    private void initFacilityConfigCache() {
        List<FacilityType> facilityTypes = facilityTypeMapper.selectActiveFacilityTypesSorted();
        for (FacilityType facilityType : facilityTypes) {
            facilityConfigCache.put(facilityType.getApiPath(), facilityType);
        }
        log.info("设施类型配置缓存初始化完成，共{}种类型", facilityConfigCache.size());
    }

    /**
     * 初始化设施服务Bean缓存
     */
    private void initFacilityServiceCache() {
        for (FacilityType config : facilityConfigCache.values()) {
            FacilityService<?, ?, ?, ?, ?, ?> service = loadFacilityService(config);
            if (service != null) {
                facilityServiceCache.put(config.getApiPath(), service);
                log.debug("注册设施服务: {} -> {}", config.getApiPath(), service.getClass().getSimpleName());
            }
        }
    }

    /**
     * 加载设施服务Bean
     * 使用标准Bean命名规则：entityClassService
     */
    private FacilityService<?, ?, ?, ?, ?, ?> loadFacilityService(FacilityType config) {
        try {
            String entityClass = config.getEntityClass();
            String serviceName = entityClass.substring(0, 1).toLowerCase() + entityClass.substring(1) + "Service";
            
            log.debug("查找服务Bean: {} -> {}", entityClass, serviceName);
            Object bean = applicationContext.getBean(serviceName);
            
            if (bean instanceof FacilityService) {
                log.debug("成功加载设施服务Bean: {}", serviceName);
                return (FacilityService<?, ?, ?, ?, ?, ?>) bean;
            } else {
                log.error("Bean {} 未实现 FacilityService 接口", serviceName);
                return null;
            }
        } catch (org.springframework.beans.factory.NoSuchBeanDefinitionException e) {
            log.error("服务Bean不存在: {} - {}", config.getEntityClass(), e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("加载设施服务Bean失败: {} - {}", config.getEntityClass(), e.getMessage());
            return null;
        }
    }

    /**
     * 检查设施类型是否支持
     */
    public boolean isSupported(String facilityTypeName) {
        return facilityConfigCache.containsKey(facilityTypeName) || 
               facilityTypeMapper.selectByApiPath(facilityTypeName) != null;
    }

    /**
     * 获取所有支持的设施类型
     */
    public List<FacilityType> getSupportedFacilityTypes() {
        return facilityTypeMapper.selectActiveFacilityTypesSorted();
    }
} 