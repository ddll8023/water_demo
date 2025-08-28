package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.common.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

// 动态导入各个服务
import com.example.demo.service.*;

/**
 * 工程信息服务总控制器
 * 
 * 提供统一的设施管理接口，支持所有设施类型的CRUD操作：
 * - 设施类型枚举查询
 * - 通用设施管理（分页查询、详情、创建、更新、删除）
 * - 支持动态设施类型处理，减少代码重复
 */
@RestController
@RequestMapping("/api/engineering-service")
@Tag(name = "工程信息服务", description = "统一的设施管理服务，支持所有设施类型的CRUD操作")
public class EngineeringServiceController {

    // 注入各个设施服务
    @Autowired
    private FloatingBoatService floatingBoatService;
    @Autowired
    private PumpingStationService pumpingStationService;
    @Autowired
    private ReservoirService reservoirService;
    @Autowired
    private MonitoringStationService monitoringStationService;
    @Autowired
    private WaterPlantService waterPlantService;
    @Autowired
    private PipelineService pipelineService;
    @Autowired
    private VillageService villageService;
    @Autowired
    private DisinfectionMaterialService disinfectionMaterialService;

    /**
     * 获取所有设施类型枚举
     * 
     * 返回系统中所有可用的设施类型列表，与实际设施实体对应
     *
     * @return 设施类型枚举列表
     */
    @GetMapping("/facility-types")
    @Operation(summary = "获取设施类型枚举", description = "返回系统中所有可用的设施类型列表，包含类型值、标签、描述、实体类和API路径信息")
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
     * 返回设施类型的键值对映射，用于快速查询和显示
     *
     * @return 设施类型映射
     */
    @GetMapping("/facility-type-map")    
    @Operation(summary = "获取设施类型映射", description = "返回设施类型的键值对映射，用于快速查询和显示")
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

    // =================================== 通用设施管理接口 ===================================

    /**
     * 通用设施分页查询
     * 
     * @param facilityType 设施类型（pumping-stations, water-plants, reservoirs等）
     * @param page 页码，默认为1
     * @param size 每页大小，默认为10
     * @param keyword 搜索关键词
     * @return 分页查询结果
     */
    @GetMapping("/{facilityType}")
    @Operation(summary = "通用设施分页查询", description = "根据设施类型进行分页查询，支持关键词搜索")
    public ResponseEntity<ApiResponse<PageResult<?>>> getFacilityPage(
            @Parameter(description = "设施类型", example = "pumping-stations") 
            @PathVariable String facilityType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String stationType,
            @RequestParam(required = false) String waterProject,
            @RequestParam(required = false) String operationMode,
            @RequestParam(required = false) String pumpingStatus) {
        try {
            PageResult<?> result = null;
            
            switch (facilityType) {
                case "pumping-stations":
                    result = pumpingStationService.getPumpingStationPage(page, size, keyword, name, stationType, waterProject, operationMode);
                    break;
                case "floating-boats":
                    // 创建查询DTO
                    com.example.demo.pojo.DTO.facility.FloatingBoatQueryDTO queryDTO = new com.example.demo.pojo.DTO.facility.FloatingBoatQueryDTO();
                    queryDTO.setPage(page);
                    queryDTO.setSize(size);
                    queryDTO.setKeyword(keyword);
                    queryDTO.setPumpingStatus(pumpingStatus);
                    result = floatingBoatService.getFloatingBoatPage(queryDTO);
                    break;
                case "reservoirs":
                    // 创建查询DTO
                    com.example.demo.pojo.DTO.facility.ReservoirQueryDTO reservoirQueryDTO = new com.example.demo.pojo.DTO.facility.ReservoirQueryDTO();
                    reservoirQueryDTO.setPage(page);
                    reservoirQueryDTO.setSize(size);
                    reservoirQueryDTO.setKeyword(keyword);
                    result = reservoirService.getReservoirPage(reservoirQueryDTO);
                    break;
                case "monitoring-stations":
                    // 创建查询DTO
                    com.example.demo.pojo.DTO.facility.MonitoringStationQueryDTO monitoringQueryDTO = new com.example.demo.pojo.DTO.facility.MonitoringStationQueryDTO();
                    monitoringQueryDTO.setPage(page);
                    monitoringQueryDTO.setSize(size);
                    monitoringQueryDTO.setKeyword(keyword);
                    result = monitoringStationService.getMonitoringStationPage(monitoringQueryDTO);
                    break;
                case "water-plants":
                    // 创建查询DTO
                    com.example.demo.pojo.DTO.facility.WaterPlantQueryDTO waterPlantQueryDTO = new com.example.demo.pojo.DTO.facility.WaterPlantQueryDTO();
                    waterPlantQueryDTO.setPage(page);
                    waterPlantQueryDTO.setSize(size);
                    waterPlantQueryDTO.setKeyword(keyword);
                    result = waterPlantService.getWaterPlantPage(waterPlantQueryDTO);
                    break;
                case "pipelines":
                    // 创建查询DTO
                    com.example.demo.pojo.DTO.facility.PipelineQueryDTO pipelineQueryDTO = new com.example.demo.pojo.DTO.facility.PipelineQueryDTO();
                    pipelineQueryDTO.setPage(page);
                    pipelineQueryDTO.setSize(size);
                    pipelineQueryDTO.setKeyword(keyword);
                    result = pipelineService.getPipelinePage(pipelineQueryDTO);
                    break;
                case "villages":
                    // 创建查询DTO
                    com.example.demo.pojo.DTO.facility.VillageQueryDTO villageQueryDTO = new com.example.demo.pojo.DTO.facility.VillageQueryDTO();
                    villageQueryDTO.setPage(page);
                    villageQueryDTO.setSize(size);
                    villageQueryDTO.setKeyword(keyword);
                    result = villageService.getVillagePage(villageQueryDTO);
                    break;
                case "disinfection-materials":
                    result = disinfectionMaterialService.getDisinfectionMaterialPage(page, size, keyword, null);
                    break;
                default:
                    return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "不支持的设施类型: " + facilityType));
            }
            
            return ResponseEntity.ok(ApiResponse.success("查询成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, "查询失败: " + e.getMessage()));
        }
    }

    /**
     * 根据ID查询设施详情
     * 
     * @param facilityType 设施类型
     * @param id 设施ID
     * @return 设施详情
     */
    @GetMapping("/{facilityType}/{id}")
    @Operation(summary = "查询设施详情", description = "根据设施类型和ID查询设施详细信息")
    public ResponseEntity<ApiResponse<?>> getFacilityById(
            @Parameter(description = "设施类型", example = "pumping-stations") 
            @PathVariable String facilityType,
            @PathVariable Long id) {
        try {
            Object result = null;
            
            switch (facilityType) {
                case "pumping-stations":
                    result = pumpingStationService.getPumpingStationById(id);
                    break;
                case "floating-boats":
                    result = floatingBoatService.getFloatingBoatById(id);
                    break;
                case "reservoirs":
                    result = reservoirService.getReservoirById(id);
                    break;
                case "monitoring-stations":
                    result = monitoringStationService.getMonitoringStationById(id);
                    break;
                case "water-plants":
                    result = waterPlantService.getWaterPlantById(id);
                    break;
                case "pipelines":
                    result = pipelineService.getPipelineById(id);
                    break;
                case "villages":
                    result = villageService.getVillageById(id);
                    break;
                case "disinfection-materials":
                    result = disinfectionMaterialService.getDisinfectionMaterialById(id);
                    break;
                default:
                    return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "不支持的设施类型: " + facilityType));
            }
            
            return ResponseEntity.ok(ApiResponse.success("查询成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, "查询失败: " + e.getMessage()));
        }
    }

    /**
     * 删除设施
     * 
     * @param facilityType 设施类型
     * @param id 设施ID
     * @return 删除结果
     */
    @DeleteMapping("/{facilityType}/{id}")
    @Operation(summary = "删除设施", description = "根据设施类型和ID删除设施（软删除）")
    public ResponseEntity<ApiResponse<Void>> deleteFacility(
            @Parameter(description = "设施类型", example = "pumping-stations") 
            @PathVariable String facilityType,
            @PathVariable Long id) {
        try {
            switch (facilityType) {
                case "pumping-stations":
                    pumpingStationService.deletePumpingStation(id);
                    break;
                case "floating-boats":
                    floatingBoatService.deleteFloatingBoat(id);
                    break;
                case "reservoirs":
                    reservoirService.deleteReservoir(id);
                    break;
                case "monitoring-stations":
                    monitoringStationService.deleteMonitoringStation(id);
                    break;
                case "water-plants":
                    waterPlantService.deleteWaterPlant(id);
                    break;
                case "pipelines":
                    pipelineService.deletePipeline(id);
                    break;
                case "villages":
                    villageService.deleteVillage(id);
                    break;
                case "disinfection-materials":
                    disinfectionMaterialService.deleteDisinfectionMaterial(id);
                    break;
                default:
                    return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "不支持的设施类型: " + facilityType));
            }
            
            return ResponseEntity.ok(ApiResponse.success("删除成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, "删除失败: " + e.getMessage()));
        }
    }

    /**
     * 获取可用设施列表（用于下拉选择）
     * 
     * @param facilityType 设施类型
     * @return 可用设施列表
     */
    @GetMapping("/{facilityType}/available")
    @Operation(summary = "获取可用设施列表", description = "获取指定类型的所有可用设施，通常用于下拉选择")
    public ResponseEntity<ApiResponse<List<?>>> getAvailableFacilities(
            @Parameter(description = "设施类型", example = "pumping-stations") 
            @PathVariable String facilityType) {
        try {
            List<?> result = null;
            
            switch (facilityType) {
                case "floating-boats":
                    result = floatingBoatService.getAvailableFloatingBoats();
                    break;
                case "reservoirs":
                    result = reservoirService.getAvailableReservoirs();
                    break;
                case "monitoring-stations":
                    result = monitoringStationService.getAvailableMonitoringStations();
                    break;
                case "water-plants":
                    result = waterPlantService.getAvailableWaterPlants();
                    break;
                case "pipelines":
                    result = pipelineService.getAvailablePipelines();
                    break;
                case "villages":
                    result = villageService.getAvailableVillages();
                    break;
                case "disinfection-materials":
                    result = disinfectionMaterialService.getAvailableDisinfectionMaterials();
                    break;
                default:
                    return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "不支持的设施类型: " + facilityType));
            }
            
            return ResponseEntity.ok(ApiResponse.success("查询成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, "查询失败: " + e.getMessage()));
        }
    }

    /**
     * 统计设施总数
     * 
     * @param facilityType 设施类型
     * @return 设施总数
     */
    @GetMapping("/{facilityType}/count")
    @Operation(summary = "统计设施总数", description = "统计指定类型设施的总数")
    public ResponseEntity<ApiResponse<Long>> countFacilities(
            @Parameter(description = "设施类型", example = "pumping-stations") 
            @PathVariable String facilityType) {
        try {
            long result = 0;
            
            switch (facilityType) {
                case "floating-boats":
                    result = floatingBoatService.countTotal();
                    break;
                case "reservoirs":
                    result = reservoirService.countTotal();
                    break;
                case "monitoring-stations":
                    result = monitoringStationService.countTotal();
                    break;
                case "water-plants":
                    result = waterPlantService.countTotal();
                    break;
                case "pipelines":
                    result = pipelineService.countTotal();
                    break;
                case "villages":
                    result = villageService.countTotal();
                    break;
                case "disinfection-materials":
                    result = disinfectionMaterialService.countTotal();
                    break;
                default:
                    return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "不支持的设施类型: " + facilityType));
            }
            
            return ResponseEntity.ok(ApiResponse.success("统计成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, "统计失败: " + e.getMessage()));
        }
    }

    /**
     * 创建设施
     * 
     * @param facilityType 设施类型
     * @param requestBody 请求体，包含设施数据
     * @return 创建结果
     */
    @PostMapping("/{facilityType}")
    @Operation(summary = "创建设施", description = "根据设施类型创建新的设施")
    public ResponseEntity<ApiResponse<?>> createFacility(
            @Parameter(description = "设施类型", example = "pumping-stations") 
            @PathVariable String facilityType,
            @RequestBody Map<String, Object> requestBody) {
        try {
            Object result = null;
            
            switch (facilityType) {
                case "pumping-stations":
                    // 转换为PumpingStationCreateDTO
                    com.example.demo.pojo.DTO.facility.PumpingStationCreateDTO pumpingCreateDTO = 
                        convertMapToDTO(requestBody, com.example.demo.pojo.DTO.facility.PumpingStationCreateDTO.class);
                    result = pumpingStationService.createPumpingStation(pumpingCreateDTO);
                    break;
                case "floating-boats":
                    // 转换为FloatingBoatCreateDTO
                    com.example.demo.pojo.DTO.facility.FloatingBoatCreateDTO floatingCreateDTO = 
                        convertMapToDTO(requestBody, com.example.demo.pojo.DTO.facility.FloatingBoatCreateDTO.class);
                    result = floatingBoatService.createFloatingBoat(floatingCreateDTO);
                    break;
                case "reservoirs":
                    // 转换为ReservoirCreateDTO
                    com.example.demo.pojo.DTO.facility.ReservoirCreateDTO reservoirCreateDTO = 
                        convertMapToDTO(requestBody, com.example.demo.pojo.DTO.facility.ReservoirCreateDTO.class);
                    result = reservoirService.createReservoir(reservoirCreateDTO);
                    break;
                case "monitoring-stations":
                    // 转换为MonitoringStationCreateDTO
                    com.example.demo.pojo.DTO.facility.MonitoringStationCreateDTO monitoringCreateDTO = 
                        convertMapToDTO(requestBody, com.example.demo.pojo.DTO.facility.MonitoringStationCreateDTO.class);
                    result = monitoringStationService.createMonitoringStation(monitoringCreateDTO);
                    break;
                case "water-plants":
                    // 转换为WaterPlantCreateDTO
                    com.example.demo.pojo.DTO.facility.WaterPlantCreateDTO waterPlantCreateDTO = 
                        convertMapToDTO(requestBody, com.example.demo.pojo.DTO.facility.WaterPlantCreateDTO.class);
                    result = waterPlantService.createWaterPlant(waterPlantCreateDTO);
                    break;
                case "pipelines":
                    // 转换为PipelineCreateDTO
                    com.example.demo.pojo.DTO.facility.PipelineCreateDTO pipelineCreateDTO = 
                        convertMapToDTO(requestBody, com.example.demo.pojo.DTO.facility.PipelineCreateDTO.class);
                    result = pipelineService.createPipeline(pipelineCreateDTO);
                    break;
                case "villages":
                    // 转换为VillageCreateDTO
                    com.example.demo.pojo.DTO.facility.VillageCreateDTO villageCreateDTO = 
                        convertMapToDTO(requestBody, com.example.demo.pojo.DTO.facility.VillageCreateDTO.class);
                    result = villageService.createVillage(villageCreateDTO);
                    break;
                case "disinfection-materials":
                    // 转换为DisinfectionMaterialCreateDTO
                    com.example.demo.pojo.DTO.facility.DisinfectionMaterialCreateDTO disinfectionCreateDTO = 
                        convertMapToDTO(requestBody, com.example.demo.pojo.DTO.facility.DisinfectionMaterialCreateDTO.class);
                    result = disinfectionMaterialService.createDisinfectionMaterial(disinfectionCreateDTO);
                    break;
                default:
                    return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "不支持的设施类型: " + facilityType));
            }
            
            return ResponseEntity.ok(ApiResponse.success("创建成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, "创建失败: " + e.getMessage()));
        }
    }

    /**
     * 更新设施
     * 
     * @param facilityType 设施类型
     * @param id 设施ID
     * @param requestBody 请求体，包含设施数据
     * @return 更新结果
     */
    @PutMapping("/{facilityType}/{id}")
    @Operation(summary = "更新设施", description = "根据设施类型和ID更新设施信息")
    public ResponseEntity<ApiResponse<?>> updateFacility(
            @Parameter(description = "设施类型", example = "pumping-stations") 
            @PathVariable String facilityType,
            @PathVariable Long id,
            @RequestBody Map<String, Object> requestBody) {
        try {
            Object result = null;
            
            // 确保requestBody中包含id
            requestBody.put("id", id);
            
            switch (facilityType) {
                case "pumping-stations":
                    // 转换为PumpingStationUpdateDTO
                    com.example.demo.pojo.DTO.facility.PumpingStationUpdateDTO pumpingUpdateDTO = 
                        convertMapToDTO(requestBody, com.example.demo.pojo.DTO.facility.PumpingStationUpdateDTO.class);
                    result = pumpingStationService.updatePumpingStation(pumpingUpdateDTO);
                    break;
                case "floating-boats":
                    // 转换为FloatingBoatUpdateDTO
                    com.example.demo.pojo.DTO.facility.FloatingBoatUpdateDTO floatingUpdateDTO = 
                        convertMapToDTO(requestBody, com.example.demo.pojo.DTO.facility.FloatingBoatUpdateDTO.class);
                    result = floatingBoatService.updateFloatingBoat(floatingUpdateDTO);
                    break;
                case "reservoirs":
                    // 转换为ReservoirUpdateDTO
                    com.example.demo.pojo.DTO.facility.ReservoirUpdateDTO reservoirUpdateDTO = 
                        convertMapToDTO(requestBody, com.example.demo.pojo.DTO.facility.ReservoirUpdateDTO.class);
                    result = reservoirService.updateReservoir(reservoirUpdateDTO);
                    break;
                case "monitoring-stations":
                    // 转换为MonitoringStationUpdateDTO
                    com.example.demo.pojo.DTO.facility.MonitoringStationUpdateDTO monitoringUpdateDTO = 
                        convertMapToDTO(requestBody, com.example.demo.pojo.DTO.facility.MonitoringStationUpdateDTO.class);
                    result = monitoringStationService.updateMonitoringStation(id, monitoringUpdateDTO);
                    break;
                case "water-plants":
                    // 转换为WaterPlantUpdateDTO
                    com.example.demo.pojo.DTO.facility.WaterPlantUpdateDTO waterPlantUpdateDTO = 
                        convertMapToDTO(requestBody, com.example.demo.pojo.DTO.facility.WaterPlantUpdateDTO.class);
                    result = waterPlantService.updateWaterPlant(waterPlantUpdateDTO);
                    break;
                case "pipelines":
                    // 转换为PipelineUpdateDTO
                    com.example.demo.pojo.DTO.facility.PipelineUpdateDTO pipelineUpdateDTO = 
                        convertMapToDTO(requestBody, com.example.demo.pojo.DTO.facility.PipelineUpdateDTO.class);
                    result = pipelineService.updatePipeline(pipelineUpdateDTO);
                    break;
                case "villages":
                    // 转换为VillageUpdateDTO
                    com.example.demo.pojo.DTO.facility.VillageUpdateDTO villageUpdateDTO = 
                        convertMapToDTO(requestBody, com.example.demo.pojo.DTO.facility.VillageUpdateDTO.class);
                    result = villageService.updateVillage(villageUpdateDTO);
                    break;
                case "disinfection-materials":
                    // 转换为DisinfectionMaterialUpdateDTO
                    com.example.demo.pojo.DTO.facility.DisinfectionMaterialUpdateDTO disinfectionUpdateDTO = 
                        convertMapToDTO(requestBody, com.example.demo.pojo.DTO.facility.DisinfectionMaterialUpdateDTO.class);
                    result = disinfectionMaterialService.updateDisinfectionMaterial(disinfectionUpdateDTO);
                    break;
                default:
                    return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "不支持的设施类型: " + facilityType));
            }
            
            return ResponseEntity.ok(ApiResponse.success("更新成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, "更新失败: " + e.getMessage()));
        }
    }

    /**
     * 获取选项数据
     * 
     * @param type 选项类型
     * @return 选项数据
     */
    @GetMapping("/options/{type}")
    @Operation(summary = "获取选项数据", description = "获取指定类型的选项数据，如部门、管理员、设施类型等")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getOptions(
            @Parameter(description = "选项类型", example = "departments") 
            @PathVariable String type) {
        try {
            List<Map<String, Object>> options = new ArrayList<>();
            
            switch (type) {
                case "facility-types":
                    // 返回设施类型选项
                    options.add(createOption("pumping_station", "加压泵站"));
                    options.add(createOption("water_plant", "水厂"));
                    options.add(createOption("reservoir", "水库"));
                    options.add(createOption("monitoring_station", "监测站"));
                    options.add(createOption("pipeline", "管道"));
                    options.add(createOption("village", "村庄"));
                    options.add(createOption("floating_boat", "漂浮船只"));
                    options.add(createOption("disinfection_material", "消毒材料"));
                    break;
                case "station-types":
                    // 返回泵站类型选项
                    options.add(createOption("booster", "增压泵站"));
                    options.add(createOption("water_intake", "取水泵站"));
                    options.add(createOption("distribution", "配水泵站"));
                    break;
                case "operation-modes":
                    // 返回运行方式选项
                    options.add(createOption("auto", "自动运行"));
                    options.add(createOption("manual", "手动运行"));
                    options.add(createOption("backup", "备用状态"));
                    break;
                case "engineering-grades":
                    // 返回工程等级选项
                    options.add(createOption("1", "一等"));
                    options.add(createOption("2", "二等"));
                    options.add(createOption("3", "三等"));
                    options.add(createOption("4", "四等"));
                    options.add(createOption("5", "五等"));
                    break;
                case "device-statuses":
                    // 返回设备状态选项
                    options.add(createOption("running", "运行中"));
                    options.add(createOption("stopped", "已停止"));
                    options.add(createOption("maintenance", "维护中"));
                    options.add(createOption("fault", "故障"));
                    break;
                default:
                    return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "不支持的选项类型: " + type));
            }
            
            return ResponseEntity.ok(ApiResponse.success("查询成功", options));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, "查询失败: " + e.getMessage()));
        }
    }

    /**
     * 辅助方法：创建选项
     */
    private Map<String, Object> createOption(String value, String label) {
        Map<String, Object> option = new HashMap<>();
        option.put("value", value);
        option.put("label", label);
        return option;
    }

    /**
     * 辅助方法：将Map转换为DTO对象
     */
    private <T> T convertMapToDTO(Map<String, Object> map, Class<T> dtoClass) {
        try {
            // 使用Jackson进行转换
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            
            String json = mapper.writeValueAsString(map);
            return mapper.readValue(json, dtoClass);
        } catch (Exception e) {
            throw new RuntimeException("DTO转换失败: " + e.getMessage(), e);
        }
    }
} 
