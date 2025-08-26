package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.common.PageResult;
import com.example.demo.pojo.DTO.facility.PipelineCreateDTO;
import com.example.demo.pojo.DTO.facility.PipelineQueryDTO;
import com.example.demo.pojo.DTO.facility.PipelineResponseDTO;
import com.example.demo.pojo.DTO.facility.PipelineUpdateDTO;
import com.example.demo.pojo.entity.facility.Pipeline;
import com.example.demo.service.PipelineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;


import javax.validation.Valid;
import java.util.List;

/**
 * 管道信息管理控制器
 * <p>
 * 提供管道信息的增删改查等操作，包括：
 * - 分页查询管道信息
 * - 查询单个管道详情
 * - 创建管道信息
 * - 更新管道信息
 * - 删除管道信息
 * - 批量删除管道信息
 * - 获取可用管道列表
 * - 统计管道总数
 * </p>
 */
@RestController
@RequestMapping("/api/engineering-service/pipelines")
@Tag(name = "管道管理", description = "管道相关的CRUD操作")
public class PipelineController {

    /**
     * 管道服务接口
     */
    @Autowired
    private PipelineService pipelineService;

    /**
     * 分页查询管道信息列表
     *
     * @param page 页码，默认为1
     * @param size 每页大小，默认为10
     * @param keyword 搜索关键词（管道名称、编码）
     * @param pipelineType 管道类型
     * @param departmentId 管理部门ID
     * @param managerId 负责人ID
     * @param material 管道材质
     * @param operationStatus 运行状态
     * @return 分页管道信息列表
     */
    @Operation(summary = "分页查询管道信息", description = "根据条件分页查询管道信息")
    @GetMapping
    
    public ResponseEntity<ApiResponse<PageResult<PipelineResponseDTO>>> getPipelinePage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String pipelineType,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long managerId,
            @RequestParam(required = false) String material,
            @RequestParam(required = false) String operationStatus) {
        try {
            // 构建查询条件对象
            PipelineQueryDTO queryDTO = new PipelineQueryDTO();
            queryDTO.setPage(page);
            queryDTO.setSize(size);
            queryDTO.setKeyword(keyword);
            queryDTO.setPipelineType(pipelineType);
            queryDTO.setMaterial(material);
            queryDTO.setOperationStatus(operationStatus);

            // 调用服务层获取分页数据
            PageResult<PipelineResponseDTO> result = pipelineService.getPipelinePage(queryDTO);
            return ResponseEntity.ok(ApiResponse.success("查询成功", result));
        } catch (Exception e) {
            // 异常处理
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 根据ID查询管道信息详情
     *
     * @param id 管道信息ID
     * @return 管道详细信息
     */
    @Operation(summary = "根据ID查询管道信息详情", description = "根据ID查询管道详细信息")
    @GetMapping("/{id}")
    
    public ResponseEntity<ApiResponse<PipelineResponseDTO>> getPipelineById(
            @PathVariable Long id) {
        try {
            // 调用服务查询管道详情
            PipelineResponseDTO result = pipelineService.getPipelineById(id);
            return ResponseEntity.ok(ApiResponse.success("查询成功", result));
        } catch (Exception e) {
            // 异常处理
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 创建管道信息
     *
     * @param createDTO 管道创建数据传输对象
     * @return 创建成功的管道信息
     */
    @Operation(summary = "创建管道信息", description = "创建新的管道信息")
    @PostMapping
    
    public ResponseEntity<ApiResponse<PipelineResponseDTO>> createPipeline(
            @Valid @RequestBody PipelineCreateDTO createDTO) {
        try {
            // 调用服务创建管道
            PipelineResponseDTO result = pipelineService.createPipeline(createDTO);
            return ResponseEntity.ok(ApiResponse.success("创建成功", result));
        } catch (Exception e) {
            // 异常处理
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 更新管道信息
     *
     * @param id 管道信息ID
     * @param updateDTO 管道更新数据传输对象
     * @return 更新后的管道信息
     */
    @Operation(summary = "更新管道信息", description = "根据ID更新管道信息")
    @PutMapping("/{id}")
    
    public ResponseEntity<ApiResponse<PipelineResponseDTO>> updatePipeline(
            @PathVariable Long id,
            @Valid @RequestBody PipelineUpdateDTO updateDTO) {
        try {
            // 设置ID并调用服务更新管道
            updateDTO.setId(id);
            PipelineResponseDTO result = pipelineService.updatePipeline(updateDTO);
            return ResponseEntity.ok(ApiResponse.success("更新成功", result));
        } catch (Exception e) {
            // 异常处理
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 删除管道信息（软删除）
     * 
     * @param id 管道信息ID
     * @return 删除结果
     */
    @Operation(summary = "删除管道信息", description = "根据ID删除管道信息（软删除）")
    @DeleteMapping("/{id}")
    
    public ResponseEntity<ApiResponse<Void>> deletePipeline(
            @PathVariable Long id) {
        try {
            // 调用服务删除管道
            pipelineService.deletePipeline(id);
            return ResponseEntity.ok(ApiResponse.success("删除成功"));
        } catch (Exception e) {
            // 异常处理
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 批量删除管道信息（软删除）
     * 
     * @param ids 管道信息ID列表
     * @return 批量删除结果
     */
    @Operation(summary = "批量删除管道信息", description = "根据ID列表批量删除管道信息（软删除）")
    @DeleteMapping("/batch")
    
    public ResponseEntity<ApiResponse<Void>> batchDeletePipelines(
            @RequestBody List<Long> ids) {
        try {
            // 调用服务批量删除管道
            pipelineService.batchDeletePipelines(ids);
            return ResponseEntity.ok(ApiResponse.success("批量删除成功"));
        } catch (RuntimeException e) {
            // 运行时异常处理
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            // 其他异常处理
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 获取所有可用管道信息（用于下拉选择）
     * 
     * @return 可用管道列表
     */
    @Operation(summary = "获取所有可用管道信息", description = "获取系统中所有可用于选择的管道列表")
    @GetMapping("/available")
    
    public ResponseEntity<ApiResponse<List<Pipeline>>> getAvailablePipelines() {
        try {
            // 调用服务获取可用管道列表
            List<Pipeline> result = pipelineService.getAvailablePipelines();
            return ResponseEntity.ok(ApiResponse.success("查询成功", result));
        } catch (Exception e) {
            // 异常处理
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 统计管道信息总数
     *
     * @return 管道信息总数
     */
    @Operation(summary = "统计管道信息总数", description = "统计系统中管道信息的总数")
    @GetMapping("/count")
    
    public ResponseEntity<ApiResponse<Long>> countTotal() {
        try {
            // 调用服务统计管道总数
            long result = pipelineService.countTotal();
            return ResponseEntity.ok(ApiResponse.success("统计成功", result));
        } catch (Exception e) {
            // 异常处理
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }
}
