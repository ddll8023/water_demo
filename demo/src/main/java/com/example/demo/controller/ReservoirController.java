package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.pojo.DTO.common.PageResponseDTO;
import com.example.demo.pojo.DTO.facility.ReservoirCreateDTO;
import com.example.demo.pojo.DTO.facility.ReservoirQueryDTO;
import com.example.demo.pojo.DTO.facility.ReservoirResponseDTO;
import com.example.demo.pojo.DTO.facility.ReservoirUpdateDTO;
import com.example.demo.pojo.entity.facility.Reservoir;
import com.example.demo.service.ReservoirService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import javax.validation.Valid;
import java.util.List;

/**
 * 水库管理控制器
 * 提供水库资源的CRUD操作和其他管理功能
 */
@RestController
@RequestMapping("/api/engineering-service/reservoirs")
@Tag(name = "水库管理", description = "水库相关的CRUD操作")
public class ReservoirController {

    /**
     * 水库服务
     */
    @Autowired
    private ReservoirService reservoirService;

    /**
     * 分页查询水库列表
     * 根据条件分页查询水库信息，支持关键词搜索和条件筛选
     * 
     * @param page 页码，默认为1
     * @param size 每页大小，默认为10
     * @param keyword 搜索关键词（水库名称）
     * @param reservoirLevel 工程等级
     * @return 分页的水库信息列表
     */
    @GetMapping
    
    @Operation(summary = "分页查询水库列表", description = "根据条件分页查询水库信息")
    public ResponseEntity<ApiResponse<PageResponseDTO<ReservoirResponseDTO>>> getReservoirPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String reservoirLevel) {
        try {
            ReservoirQueryDTO queryDTO = new ReservoirQueryDTO();
            queryDTO.setPage(page);
            queryDTO.setSize(size);
            queryDTO.setKeyword(keyword);
            queryDTO.setEngineeringGrade(reservoirLevel);

            PageResponseDTO<ReservoirResponseDTO> result = reservoirService.getReservoirPage(queryDTO);
            return ResponseEntity.ok(ApiResponse.success("查询成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 查询水库详情
     * 根据ID查询水库详细信息
     * 
     * @param id 水库ID
     * @return 水库详情信息
     */
    @GetMapping("/{id}")
    
    @Operation(summary = "查询水库详情", description = "根据ID查询水库详细信息")
    public ResponseEntity<ApiResponse<ReservoirResponseDTO>> getReservoirById(
            @PathVariable Long id) {
        try {
            ReservoirResponseDTO result = reservoirService.getReservoirById(id);
            return ResponseEntity.ok(ApiResponse.success("查询成功", result));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(404, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 创建水库
     * 创建新的水库信息
     * 
     * @param createDTO 水库创建数据传输对象
     * @return 创建成功的水库信息
     */
    @PostMapping
    
    @Operation(summary = "创建水库", description = "创建新的水库信息")
    public ResponseEntity<ApiResponse<ReservoirResponseDTO>> createReservoir(
            @Valid @RequestBody ReservoirCreateDTO createDTO) {
        try {
            ReservoirResponseDTO result = reservoirService.createReservoir(createDTO);
            return ResponseEntity.ok(ApiResponse.success("创建成功", result));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(500, "创建失败" + e.getMessage()));
        }
    }

    /**
     * 更新水库信息
     * 更新指定水库的详细信息
     * 
     * @param id 水库ID
     * @param updateDTO 水库更新数据传输对象
     * @return 更新后的水库信息
     */
    @PutMapping("/{id}")
    
    @Operation(summary = "更新水库信息", description = "根据ID更新水库信息")
    public ResponseEntity<ApiResponse<ReservoirResponseDTO>> updateReservoir(
            @PathVariable Long id,
            @Valid @RequestBody ReservoirUpdateDTO updateDTO) {
        try {
            updateDTO.setId(id);
            ReservoirResponseDTO result = reservoirService.updateReservoir(updateDTO);
            return ResponseEntity.ok(ApiResponse.success("更新成功", result));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(500, "更新失败" + e.getMessage()));
        }
    }

    /**
     * 删除水库
     * 删除指定的水库信息（软删除）
     * 
     * @param id 水库ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    
    @Operation(summary = "删除水库", description = "根据ID删除水库信息（软删除）")
    public ResponseEntity<ApiResponse<Void>> deleteReservoir(
            @PathVariable Long id) {
        try {
            reservoirService.deleteReservoir(id);
            return ResponseEntity.ok(ApiResponse.success("删除成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(404, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(500, "删除失败" + e.getMessage()));
        }
    }

    /**
     * 批量删除水库
     * 根据ID列表批量删除水库（软删除）
     * 
     * @param ids 水库ID列表
     * @return 操作结果
     */
    @DeleteMapping("/batch")
    
    @Operation(summary = "批量删除水库", description = "根据ID列表批量删除水库（软删除）")
    public ResponseEntity<ApiResponse<Void>> batchDeleteReservoirs(
            @RequestBody List<Long> ids) {
        try {
            reservoirService.batchDeleteReservoirs(ids);
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
     * 获取所有可用水库
     * 获取所有可用的水库列表，用于下拉选择
     * 
     * @return 可用水库列表
     */
    @GetMapping("/available")
    
    @Operation(summary = "获取所有可用水库", description = "获取系统中所有可用于选择的水库列表")
    public ResponseEntity<ApiResponse<List<Reservoir>>> getAvailableReservoirs() {
        try {
            List<Reservoir> result = reservoirService.getAvailableReservoirs();
            return ResponseEntity.ok(ApiResponse.success("查询成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 统计水库总数
     * 统计系统中水库的总数
     * 
     * @return 水库总数
     */
    @GetMapping("/count")
    
        @Operation(summary = "统计水库总数", description = "统计系统中水库的总数")
    public ResponseEntity<ApiResponse<Long>> countTotal() {
        try {
            long result = reservoirService.countTotal();
            return ResponseEntity.ok(ApiResponse.success("统计成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }
}
