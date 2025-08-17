package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工程信息服务总控制器
 * 
 * 提供工程信息服务的通用接口，包括：
 * - 设施类型枚举查询
 * - 设施类型映射信息
 */
@RestController
@RequestMapping("/api/engineering-service")
@RequiredArgsConstructor
public class EngineeringServiceController {

    /**
     * 获取所有设施类型枚举
     * 
     * 返回系统中所有可用的设施类型列表，与实际设施实体对应
     *
     * @return 设施类型枚举列表
     */
    @GetMapping("/facility-types")
    @PreAuthorize("hasAuthority('business:manage')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getFacilityTypes() {
        try {
            List<Map<String, Object>> facilityTypes = new ArrayList<>();
            
            // 加压泵站
            Map<String, Object> pumpingStation = new HashMap<>();
            pumpingStation.put("value", "pumping_station");
            pumpingStation.put("label", "加压泵站");
            pumpingStation.put("description", "用于加压供水的泵站");
            pumpingStation.put("entityClass", "PumpingStation");
            pumpingStation.put("apiPath", "pumping-stations");
            facilityTypes.add(pumpingStation);
            
            // 水厂
            Map<String, Object> waterPlant = new HashMap<>();
            waterPlant.put("value", "water_plant");
            waterPlant.put("label", "水厂");
            waterPlant.put("description", "水处理厂");
            waterPlant.put("entityClass", "WaterPlant");
            waterPlant.put("apiPath", "water-plants");
            facilityTypes.add(waterPlant);
            
            // 水库
            Map<String, Object> reservoir = new HashMap<>();
            reservoir.put("value", "reservoir");
            reservoir.put("label", "水库");
            reservoir.put("description", "蓄水水库");
            reservoir.put("entityClass", "Reservoir");
            reservoir.put("apiPath", "reservoirs");
            facilityTypes.add(reservoir);
            
            // 监测站
            Map<String, Object> monitoringStation = new HashMap<>();
            monitoringStation.put("value", "monitoring_station");
            monitoringStation.put("label", "监测站");
            monitoringStation.put("description", "监测站设施");
            monitoringStation.put("entityClass", "MonitoringStation");
            monitoringStation.put("apiPath", "monitoring-stations");
            facilityTypes.add(monitoringStation);
            
            // 管道
            Map<String, Object> pipeline = new HashMap<>();
            pipeline.put("value", "pipeline");
            pipeline.put("label", "管道");
            pipeline.put("description", "供水管道");
            pipeline.put("entityClass", "Pipeline");
            pipeline.put("apiPath", "pipelines");
            facilityTypes.add(pipeline);
            
            // 村庄
            Map<String, Object> village = new HashMap<>();
            village.put("value", "village");
            village.put("label", "村庄");
            village.put("description", "供水覆盖村庄");
            village.put("entityClass", "Village");
            village.put("apiPath", "villages");
            facilityTypes.add(village);
            
            // 漂浮船只
            Map<String, Object> floatingBoat = new HashMap<>();
            floatingBoat.put("value", "floating_boat");
            floatingBoat.put("label", "漂浮船只");
            floatingBoat.put("description", "水面清洁漂浮船只");
            floatingBoat.put("entityClass", "FloatingBoat");
            floatingBoat.put("apiPath", "floating-boats");
            facilityTypes.add(floatingBoat);
            
            // 消毒材料
            Map<String, Object> disinfectionMaterial = new HashMap<>();
            disinfectionMaterial.put("value", "disinfection_material");
            disinfectionMaterial.put("label", "消毒材料");
            disinfectionMaterial.put("description", "水处理消毒材料");
            disinfectionMaterial.put("entityClass", "DisinfectionMaterial");
            disinfectionMaterial.put("apiPath", "disinfection-materials");
            facilityTypes.add(disinfectionMaterial);
            
            return ResponseEntity.ok(ApiResponse.success("查询成功", facilityTypes));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, "查询失败: " + e.getMessage()));
        }
    }

    /**
     * 获取设施类型映射信息
     * 
     * 返回设施类型的键值对映射，用于快速查询
     *
     * @return 设施类型映射
     */
    @GetMapping("/facility-type-map")
    @PreAuthorize("hasAuthority('business:manage')")
    public ResponseEntity<ApiResponse<Map<String, String>>> getFacilityTypeMap() {
        try {
            Map<String, String> facilityTypeMap = new HashMap<>();
            facilityTypeMap.put("pumping_station", "加压泵站");
            facilityTypeMap.put("water_plant", "水厂");
            facilityTypeMap.put("reservoir", "水库");
            facilityTypeMap.put("monitoring_station", "监测站");
            facilityTypeMap.put("pipeline", "管道");
            facilityTypeMap.put("village", "村庄");
            facilityTypeMap.put("floating_boat", "漂浮船只");
            facilityTypeMap.put("disinfection_material", "消毒材料");
            
            return ResponseEntity.ok(ApiResponse.success("查询成功", facilityTypeMap));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, "查询失败: " + e.getMessage()));
        }
    }
} 