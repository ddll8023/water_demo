package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.pojo.DTO.common.PageResponseDTO;
import com.example.demo.pojo.DTO.facility.FloatingBoatCreateDTO;
import com.example.demo.pojo.DTO.facility.FloatingBoatQueryDTO;
import com.example.demo.pojo.DTO.facility.FloatingBoatResponseDTO;
import com.example.demo.pojo.DTO.facility.FloatingBoatUpdateDTO;
import com.example.demo.pojo.entity.facility.FloatingBoat;
import com.example.demo.service.FloatingBoatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 浮船信息管理控制器 * 
 * 提供浮船信息的CRUD操作接口，包括分页查询、详情查询、创建、更新、删除等功能
 * 使用RESTful风格的API设计，统一返回ApiResponse格式
 */
@RestController
@RequestMapping("/api/engineering-service/floating-boats")
@Api(tags = "浮船信息管理", description = "浮船信息相关的增删改查操作")
public class FloatingBoatController {

    /**
     * 浮船服务接口，通过构造器注入
     */
    @Autowired
    private FloatingBoatService floatingBoatService;

    /**
     * 分页查询浮船信息列表
     * 
     * @param page 页码，默认为1
     * @param size 每页大小，默认为10
     * @param keyword 搜索关键词（浮船名称）     * @param pumpingStatus 抽水状态     * @return 分页浮船信息列表
     */
    @GetMapping
    
    @ApiOperation(value = "获取浮船信息分页数据", notes = "支持关键词搜索（浮船名称）和条件筛选（抽水状态）")
    public ResponseEntity<ApiResponse<PageResponseDTO<FloatingBoatResponseDTO>>> getFloatingBoatPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String pumpingStatus) {
        try {
            FloatingBoatQueryDTO queryDTO = new FloatingBoatQueryDTO();
            queryDTO.setPage(page);
            queryDTO.setSize(size);
            queryDTO.setKeyword(keyword);
            queryDTO.setPumpingStatus(pumpingStatus);

            PageResponseDTO<FloatingBoatResponseDTO> result = floatingBoatService.getFloatingBoatPage(queryDTO);
            return ResponseEntity.ok(ApiResponse.success("查询成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 根据ID查询浮船信息详情
     * 
     * @param id 浮船信息ID
     * @return 浮船信息详情
     */
    @GetMapping("/{id}")
    
    @ApiOperation(value = "获取浮船信息详情", notes = "根据ID查询浮船详细信息")
    public ResponseEntity<ApiResponse<FloatingBoatResponseDTO>> getFloatingBoatById(
            @PathVariable Long id) {
        try {
            FloatingBoatResponseDTO result = floatingBoatService.getFloatingBoatById(id);
            return ResponseEntity.ok(ApiResponse.success("查询成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 创建浮船信息
     * 
     * @param createDTO 浮船信息创建数据传输对象
     * @return 创建后的浮船信息
     */
    @PostMapping
    
    @ApiOperation(value = "创建浮船信息", notes = "创建新的浮船信息，需提供必要的浮船信息")
    public ResponseEntity<ApiResponse<FloatingBoatResponseDTO>> createFloatingBoat(
            @Valid @RequestBody FloatingBoatCreateDTO createDTO) {
        try {
            FloatingBoatResponseDTO result = floatingBoatService.createFloatingBoat(createDTO);
            return ResponseEntity.ok(ApiResponse.success("创建成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 更新浮船信息
     * 
     * @param id 浮船信息ID
     * @param updateDTO 浮船信息更新数据传输对象
     * @return 更新后的浮船信息
     */
    @PutMapping("/{id}")
    
    @ApiOperation(value = "更新浮船信息", notes = "根据ID更新浮船的信息")
    public ResponseEntity<ApiResponse<FloatingBoatResponseDTO>> updateFloatingBoat(
            @PathVariable Long id,
            @Valid @RequestBody FloatingBoatUpdateDTO updateDTO) {
        try {
            updateDTO.setId(id);
            FloatingBoatResponseDTO result = floatingBoatService.updateFloatingBoat(updateDTO);
            return ResponseEntity.ok(ApiResponse.success("更新成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 删除浮船信息（软删除）     * 
     * @param id 浮船信息ID
     * @return 删除操作结果
     */
    @DeleteMapping("/{id}")
    
    @ApiOperation(value = "删除浮船信息", notes = "根据ID删除浮船信息（软删除）")
    public ResponseEntity<ApiResponse<Void>> deleteFloatingBoat(
            @PathVariable Long id) {
        try {
            floatingBoatService.deleteFloatingBoat(id);
            return ResponseEntity.ok(ApiResponse.success("删除成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 批量删除浮船信息（软删除）     * 
     * @param ids 浮船信息ID列表
     * @return 批量删除操作结果
     */
    @DeleteMapping("/batch")
    
    @ApiOperation(value = "批量删除浮船信息", notes = "根据ID列表批量删除多个浮船信息（软删除）")
    public ResponseEntity<ApiResponse<Void>> batchDeleteFloatingBoats(
            @RequestBody List<Long> ids) {
        try {
            floatingBoatService.batchDeleteFloatingBoats(ids);
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
     * 获取所有可用浮船信息（用于下拉选择）     * 
     * @return 所有可用浮船信息列表     */
    @GetMapping("/available")
    
    @ApiOperation(value = "获取所有可用浮船信息", notes = "获取所有可用的浮船信息列表，通常用于下拉选择")
    public ResponseEntity<ApiResponse<List<FloatingBoat>>> getAvailableFloatingBoats() {
        try {
            List<FloatingBoat> result = floatingBoatService.getAvailableFloatingBoats();
            return ResponseEntity.ok(ApiResponse.success("查询成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 统计浮船信息总数
     * 
     * @return 系统中浮船信息的总数     */
    @GetMapping("/count")
    
    @ApiOperation(value = "统计浮船信息总数", notes = "统计系统中浮船信息的总数")
    public ResponseEntity<ApiResponse<Long>> countTotal() {
        try {
            long result = floatingBoatService.countTotal();
            return ResponseEntity.ok(ApiResponse.success("统计成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }
}
