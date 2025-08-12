package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.dto.common.PageResponseDTO;
import com.example.demo.dto.facility.*;
import com.example.demo.entity.facility.WaterPlant;
import com.example.demo.service.WaterPlantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 水厂管理控制器
 * 提供水厂信息的CRUD操作和其他管理功能
 * 路径: /api/engineering-service/water-plants
 */
@RestController
@RequestMapping("/api/engineering-service/water-plants")
@RequiredArgsConstructor
public class WaterPlantController {

    /**
     * 水厂服务接口
     */
    private final WaterPlantService waterPlantService;

    /**
     * 分页查询水厂列表
     * 
     * @param page 页码，默认为1
     * @param size 每页大小，默认为10
     * @param keyword 搜索关键词（水厂名称）
     * @return 分页的水厂信息列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('business:manage')")
    public ResponseEntity<ApiResponse<PageResponseDTO<WaterPlantResponseDTO>>> getWaterPlantPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        try {
            // 构建查询参数
            WaterPlantQueryDTO queryDTO = new WaterPlantQueryDTO();
            queryDTO.setPage(page);
            queryDTO.setSize(size);
            queryDTO.setKeyword(keyword);

            // 调用服务层方法获取分页结果
            PageResponseDTO<WaterPlantResponseDTO> result = waterPlantService.getWaterPlantPage(queryDTO);
            return ResponseEntity.ok(ApiResponse.success("查询成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 查询水厂详情
     * 
     * @param id 水厂ID
     * @return 水厂详细信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('business:manage')")
    public ResponseEntity<ApiResponse<WaterPlantResponseDTO>> getWaterPlantById(
            @PathVariable Long id) {
        try {
            // 根据ID查询水厂详情
            WaterPlantResponseDTO result = waterPlantService.getWaterPlantById(id);
            return ResponseEntity.ok(ApiResponse.success("查询成功", result));
        } catch (RuntimeException e) {
            // 处理资源不存在等运行时异常
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(404, e.getMessage()));
        } catch (Exception e) {
            // 处理其他异常
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 创建水厂
     * 
     * @param createDTO 水厂创建信息
     * @return 创建成功的水厂信息
     */
    @PostMapping
    @PreAuthorize("hasAuthority('business:manage')")
    public ResponseEntity<ApiResponse<WaterPlantResponseDTO>> createWaterPlant(
            @Valid @RequestBody WaterPlantCreateDTO createDTO) {
        try {
            // 创建水厂
            WaterPlantResponseDTO result = waterPlantService.createWaterPlant(createDTO);
            return ResponseEntity.ok(ApiResponse.success("创建成功", result));
        } catch (RuntimeException e) {
            // 处理参数验证等运行时异常
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            // 处理其他异常
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(500, "创建失败：" + e.getMessage()));
        }
    }

    /**
     * 更新水厂信息
     * 
     * @param id 水厂ID
     * @param updateDTO 水厂更新信息
     * @return 更新后的水厂信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('business:manage')")
    public ResponseEntity<ApiResponse<WaterPlantResponseDTO>> updateWaterPlant(
            @PathVariable Long id,
            @Valid @RequestBody WaterPlantUpdateDTO updateDTO) {
        try {
            // 设置ID并更新水厂信息
            updateDTO.setId(id);
            WaterPlantResponseDTO result = waterPlantService.updateWaterPlant(updateDTO);
            return ResponseEntity.ok(ApiResponse.success("更新成功", result));
        } catch (RuntimeException e) {
            // 处理资源不存在等运行时异常
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            // 处理其他异常
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(500, "更新失败：" + e.getMessage()));
        }
    }

    /**
     * 删除水厂
     * 
     * @param id 水厂ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('business:manage')")
    public ResponseEntity<ApiResponse<Void>> deleteWaterPlant(
            @PathVariable Long id) {
        try {
            // 删除指定ID的水厂（软删除）
            waterPlantService.deleteWaterPlant(id);
            return ResponseEntity.ok(ApiResponse.success("删除成功"));
        } catch (RuntimeException e) {
            // 处理资源不存在等运行时异常
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(404, e.getMessage()));
        } catch (Exception e) {
            // 处理其他异常
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(500, "删除失败：" + e.getMessage()));
        }
    }

    /**
     * 批量删除水厂
     * 
     * @param ids 水厂ID列表
     * @return 批量删除结果
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('business:manage')")
    public ResponseEntity<ApiResponse<Void>> batchDeleteWaterPlants(
            @RequestBody List<Long> ids) {
        try {
            // 批量删除水厂（软删除）
            waterPlantService.batchDeleteWaterPlants(ids);
            return ResponseEntity.ok(ApiResponse.success("批量删除成功"));
        } catch (RuntimeException e) {
            // 处理参数验证等运行时异常
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            // 处理其他异常
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 获取所有可用水厂（用于下拉选择）
     * 
     * @return 可用水厂列表
     */
    @GetMapping("/available")
    @PreAuthorize("hasAuthority('business:manage')")
    public ResponseEntity<ApiResponse<List<WaterPlant>>> getAvailableWaterPlants() {
        try {
            // 获取所有可用的水厂列表
            List<WaterPlant> result = waterPlantService.getAvailableWaterPlants();
            return ResponseEntity.ok(ApiResponse.success("查询成功", result));
        } catch (Exception e) {
            // 处理异常
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 统计水厂总数
     * 
     * @return 水厂总数
     */
    @GetMapping("/count")
    @PreAuthorize("hasAuthority('business:manage')")
    public ResponseEntity<ApiResponse<Long>> countTotal() {
        try {
            // 统计系统中水厂的总数量
            long result = waterPlantService.countTotal();
            return ResponseEntity.ok(ApiResponse.success("统计成功", result));
        } catch (Exception e) {
            // 处理异常
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }
}
