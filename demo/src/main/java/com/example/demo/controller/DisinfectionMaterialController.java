package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.pojo.DTO.common.PageResponseDTO;
import com.example.demo.pojo.DTO.facility.DisinfectionMaterialCreateDTO;
import com.example.demo.pojo.DTO.facility.DisinfectionMaterialResponseDTO;
import com.example.demo.pojo.DTO.facility.DisinfectionMaterialUpdateDTO;
import com.example.demo.pojo.entity.facility.DisinfectionMaterial;
import com.example.demo.service.DisinfectionMaterialService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 消毒药材管理控制器
 * <p>
 * 提供消毒药材相关的CRUD操作、批量操作、统计信息查询等功能
 * 所有方法都需"business:manage"权限
 * </p>
 */
@RestController
@RequestMapping("/api/engineering-service/disinfection-materials")
@Api(tags = "消毒药材管理", description = "消毒药材相关的增删改查操作")
public class DisinfectionMaterialController {

    /**
     * 消毒药材服务
     */
    @Autowired
    private DisinfectionMaterialService disinfectionMaterialService;

    /**
     * 分页查询消毒药材列表
     * <p>
     * 根据条件分页查询消毒药材信息，支持关键词搜索（药材名称）和条件筛选（所属水厂）
     * </p>
     *
     * @param page 页码，默认值为1
     * @param size 每页大小，默认值为10
     * @param keyword 搜索关键词（药材名称），可�?     * @param waterPlantId 所属水厂ID，可�?     * @return 包含分页消毒药材列表的响应     */
    @GetMapping
    
    @ApiOperation(value = "获取消毒药材分页数据", notes = "支持关键词搜索（药材名称）和条件筛选（所属水厂）")
    public ResponseEntity<ApiResponse<PageResponseDTO<DisinfectionMaterialResponseDTO>>> getDisinfectionMaterialPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long waterPlantId) {
        try {
            PageResponseDTO<DisinfectionMaterialResponseDTO> result = disinfectionMaterialService.getDisinfectionMaterialPage(page, size, keyword, waterPlantId);
            return ResponseEntity.ok(ApiResponse.success("查询成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 根据ID查询消毒药材详情
     * <p>
     * 根据消毒药材ID查询其详细信息
     * </p>
     *
     * @param id 消毒药材ID
     * @return 包含消毒药材详细信息的响应
     */
    @GetMapping("/{id}")
    
    @ApiOperation(value = "获取消毒药材详情", notes = "根据ID查询消毒药材详细信息")
    public ResponseEntity<ApiResponse<DisinfectionMaterialResponseDTO>> getDisinfectionMaterialById(
            @PathVariable Long id) {
        try {
            DisinfectionMaterialResponseDTO result = disinfectionMaterialService.getDisinfectionMaterialById(id);
            return ResponseEntity.ok(ApiResponse.success("查询成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 创建消毒药材
     * <p>
     * 创建新的消毒药材信息，需提供必要的药材信息
     * </p>
     *
     * @param createDTO 创建消毒药材所需的数据传输对象
     * @return 包含新创建的消毒药材信息的响应
     */
    @PostMapping
    
    @ApiOperation(value = "创建消毒药材", notes = "创建新的消毒药材信息，需提供必要的药材信息")
    public ResponseEntity<ApiResponse<DisinfectionMaterialResponseDTO>> createDisinfectionMaterial(
            @Valid @RequestBody DisinfectionMaterialCreateDTO createDTO) {
        try {
            DisinfectionMaterialResponseDTO result = disinfectionMaterialService.createDisinfectionMaterial(createDTO);
            return ResponseEntity.ok(ApiResponse.success("创建成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 更新消毒药材信息
     * <p>
     * 根据ID更新消毒药材的信息
     * </p>
     *
     * @param id 需要更新的消毒药材ID
     * @param updateDTO 更新消毒药材所需的数据传输对象
     * @return 包含更新后的消毒药材信息的响应
     */
    @PutMapping("/{id}")
    
    @ApiOperation(value = "更新消毒药材信息", notes = "根据ID更新消毒药材的信息")
    public ResponseEntity<ApiResponse<DisinfectionMaterialResponseDTO>> updateDisinfectionMaterial(
            @PathVariable Long id,
            @Valid @RequestBody DisinfectionMaterialUpdateDTO updateDTO) {
        try {
            updateDTO.setId(id);
            DisinfectionMaterialResponseDTO result = disinfectionMaterialService.updateDisinfectionMaterial(updateDTO);
            return ResponseEntity.ok(ApiResponse.success("更新成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 删除消毒药材
     * <p>
     * 根据ID删除消毒药材（软删除）
     * </p>
     *
     * @param id 需要删除的消毒药材ID
     * @return 删除操作的响应结果
     */
    @DeleteMapping("/{id}")
    
    @ApiOperation(value = "删除消毒药材", notes = "根据ID删除消毒药材（软删除）")
    public ResponseEntity<ApiResponse<Void>> deleteDisinfectionMaterial(
            @PathVariable Long id) {
        try {
            disinfectionMaterialService.deleteDisinfectionMaterial(id);
            return ResponseEntity.ok(ApiResponse.success("删除成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 批量删除消毒药材
     * <p>
     * 根据ID列表批量删除多个消毒药材（软删除）
     * </p>
     *
     * @param ids 需要删除的消毒药材ID列表
     * @return 批量删除操作的响应结果
     */
    @DeleteMapping("/batch")
    
    @ApiOperation(value = "批量删除消毒药材", notes = "根据ID列表批量删除多个消毒药材（软删除）")
    public ResponseEntity<ApiResponse<Void>> batchDeleteDisinfectionMaterials(
            @RequestBody List<Long> ids) {
        try {
            disinfectionMaterialService.batchDeleteDisinfectionMaterials(ids);
            return ResponseEntity.ok(ApiResponse.success("批量删除成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 获取所有可用消毒药材
     * <p>
     * 获取所有可用的消毒药材列表，通常用于下拉选择
     * </p>
     *
     * @return 包含所有可用消毒药材的响应
     */
    @GetMapping("/available")
    
    @ApiOperation(value = "获取所有可用消毒药材", notes = "获取所有可用的消毒药材列表，通常用于下拉选择")
    public ResponseEntity<ApiResponse<List<DisinfectionMaterial>>> getAvailableDisinfectionMaterials() {
        try {
            List<DisinfectionMaterial> result = disinfectionMaterialService.getAvailableDisinfectionMaterials();
            return ResponseEntity.ok(ApiResponse.success("查询成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 统计消毒药材总数
     * <p>
     * 统计系统中消毒药材的总数
     * </p>
     *
     * @return 包含消毒药材总数的响应
     */
    @GetMapping("/count")
    
    @ApiOperation(value = "统计消毒药材总数", notes = "统计系统中消毒药材的总数")
    public ResponseEntity<ApiResponse<Long>> countTotal() {
        try {
            long result = disinfectionMaterialService.countTotal();
            return ResponseEntity.ok(ApiResponse.success("统计成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 获取库存统计信息
     * <p>
     * 获取消毒药材的库存统计信息，包括总数、库存不足、即将过期等
     * </p>
     *
     * @return 包含库存统计信息的响应
     */
    @GetMapping("/statistics")
    
    @ApiOperation(value = "获取库存统计信息", notes = "获取消毒药材的库存统计信息，包括总数、库存不足、即将过期等")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStockStatistics() {
        try {
            Map<String, Object> result = disinfectionMaterialService.getStockStatistics();
            return ResponseEntity.ok(ApiResponse.success("统计成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }
}
