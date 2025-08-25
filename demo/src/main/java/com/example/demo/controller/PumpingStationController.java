package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.pojo.DTO.common.PageResponseDTO;
import com.example.demo.pojo.DTO.facility.PumpingStationCreateDTO;
import com.example.demo.pojo.DTO.facility.PumpingStationResponseDTO;
import com.example.demo.pojo.DTO.facility.PumpingStationUpdateDTO;
import com.example.demo.service.PumpingStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import javax.validation.Valid;

/**
 * 泵站管理控制�? * 
 * 提供泵站信息的增删改查等基础功能接口，包括：
 * - 分页查询泵站列表
 * - 查询泵站详情
 * - 创建泵站
 * - 更新泵站信息
 * - 删除泵站（单�?批量�? * - 获取可用泵站列表
 * - 统计泵站总数
 */
@RestController
@RequestMapping("/api/engineering-service/pumping-stations")
@Tag(name = "泵站管理", description = "泵站相关的CRUD操作")
public class PumpingStationController {

    /**
     * 泵站服务接口
     */
    @Autowired
    private PumpingStationService pumpingStationService;

    /**
     * 分页查询泵站列表
     * 
     * 根据条件分页查询泵站信息，支持关键词搜索和条件筛选
     * 
     * @param page 页码，默认为1
     * @param size 每页大小，默认为10
     * @param keyword 搜索关键词（泵站名称、编码、地址）
     * @param name 泵站名称
     * @param stationType 泵站类型
     * @param waterProject 所属供水工程
     * @param operationMode 运行方式
     * @return 分页查询结果，包含泵站信息列表
     */
    @GetMapping
    
    @Operation(summary = "分页查询泵站列表", description = "根据条件分页查询泵站信息")
    public ResponseEntity<ApiResponse<PageResponseDTO<PumpingStationResponseDTO>>> getPumpingStationPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String stationType,
            @RequestParam(required = false) String waterProject,
            @RequestParam(required = false) String operationMode) {
        try {
            PageResponseDTO<PumpingStationResponseDTO> result = pumpingStationService.getPumpingStationPage(
                page, size, keyword, name, stationType, waterProject, operationMode);
            return ResponseEntity.ok(ApiResponse.success("查询成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 查询泵站详情
     * 
     * 根据ID查询泵站的详细信息
     * 
     * @param id 泵站ID
     * @return 泵站详细信息
     */
    @GetMapping("/{id}")
    
    @Operation(summary = "查询泵站详情", description = "根据ID查询泵站的详细信息")
    public ResponseEntity<ApiResponse<PumpingStationResponseDTO>> getPumpingStationById(
            @PathVariable Long id) {
        try {
            PumpingStationResponseDTO result = pumpingStationService.getPumpingStationById(id);
            return ResponseEntity.ok(ApiResponse.success("查询成功", result));
        } catch (RuntimeException e) {
            // 找不到对应泵站记录时抛出的异常
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(404, e.getMessage()));
        } catch (Exception e) {
            // 其他未预期的异常
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 创建泵站
     * 
     * 创建新的泵站信息记录
     *
     * @param createDTO 泵站创建数据传输对象
     * @return 创建成功的泵站信息
     */
    @PostMapping
    
    @Operation(summary = "创建泵站", description = "创建新的泵站信息")
    public ResponseEntity<ApiResponse<PumpingStationResponseDTO>> createPumpingStation(
            @Valid @RequestBody PumpingStationCreateDTO createDTO) {
        try {
            PumpingStationResponseDTO result = pumpingStationService.createPumpingStation(createDTO);
            return ResponseEntity.ok(ApiResponse.success("创建成功", result));
        } catch (RuntimeException e) {
            // 参数校验失败或业务逻辑错误
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            // 其他服务器端异常
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(500, "创建失败" + e.getMessage()));
        }
    }

    /**
     * 更新泵站信息
     * 
     * 更新指定泵站的详细信息
     * 
     * @param id 泵站ID
     * @param updateDTO 泵站更新数据传输对象
     * @return 更新后的泵站信息
     */
    @PutMapping("/{id}")
    
    @Operation(summary = "更新泵站信息", description = "根据ID更新泵站信息")
    public ResponseEntity<ApiResponse<PumpingStationResponseDTO>> updatePumpingStation(
            @PathVariable Long id,
            @Valid @RequestBody PumpingStationUpdateDTO updateDTO) {
        try {
            updateDTO.setId(id);
            PumpingStationResponseDTO result = pumpingStationService.updatePumpingStation(updateDTO);
            return ResponseEntity.ok(ApiResponse.success("更新成功", result));
        } catch (RuntimeException e) {
            // 参数校验失败或业务逻辑错误
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            // 其他服务器端异常
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(500, "更新失败" + e.getMessage()));
        }
    }

    /**
     * 删除泵站
     * 
     * 删除指定的泵站信息（软删除）
     *
     * @param id 泵站ID
     * @return 删除操作结果
     */
    @DeleteMapping("/{id}")
    
        @Operation(summary = "删除泵站", description = "根据ID删除泵站信息（软删除）")
    public ResponseEntity<ApiResponse<Void>> deletePumpingStation(
            @PathVariable Long id) {
        try {
            pumpingStationService.deletePumpingStation(id);
            return ResponseEntity.ok(ApiResponse.success("删除成功"));
        } catch (RuntimeException e) {
            // 找不到对应泵站记录时抛出的异常
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(404, e.getMessage()));
        } catch (Exception e) {
            // 其他服务器端异常
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(500, "删除失败" + e.getMessage()));
        }
    }

}
