package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.pojo.DTO.common.PageResponseDTO;
import com.example.demo.pojo.DTO.facility.VillageCreateDTO;
import com.example.demo.pojo.DTO.facility.VillageQueryDTO;
import com.example.demo.pojo.DTO.facility.VillageResponseDTO;
import com.example.demo.pojo.DTO.facility.VillageUpdateDTO;
import com.example.demo.pojo.entity.facility.Village;
import com.example.demo.service.VillageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import java.util.List;

/**
 * 村庄信息管理控制器
 * <p>
 * 提供村庄信息的增删改查等管理功能，支持分页查询、条件筛选、详情查看、新增、修改、删除等操作
 * </p>
 */
@RestController
@RequestMapping("/api/engineering-service/villages")
@RequiredArgsConstructor
@Api(tags = "村庄管理", description = "村庄相关的CRUD操作")
public class VillageController {

    /**
     * 村庄服务接口
     */
    private final VillageService villageService;

    /**
     * 分页查询村庄信息列表
     * <p>
     * 根据条件分页查询村庄信息，支持根据村庄名称关键词搜索和条件筛选
     * </p>
     *
     * @param page    页码，默认值为1
     * @param size    每页大小，默认值为10
     * @param keyword 搜索关键词（村庄名称）
     * @return 分页的村庄信息列表
     */
    @ApiOperation(value = "分页查询村庄列表", notes = "根据条件分页查询村庄信息")
    @GetMapping
    
    public ResponseEntity<ApiResponse<PageResponseDTO<VillageResponseDTO>>> getVillagePage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        try {
            VillageQueryDTO queryDTO = new VillageQueryDTO();
            queryDTO.setPage(page);
            queryDTO.setSize(size);
            queryDTO.setKeyword(keyword);

            PageResponseDTO<VillageResponseDTO> result = villageService.getVillagePage(queryDTO);
            return ResponseEntity.ok(ApiResponse.success("查询成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 根据ID查询村庄信息详情
     * <p>
     * 根据村庄信息ID查询详细信息，若ID不存在则会抛出异常
     * </p>
     *
     * @param id 村庄信息ID
     * @return 村庄详细信息
     */
    @ApiOperation(value = "查询村庄详情", notes = "根据ID查询村庄详细信息")
    @GetMapping("/{id}")
    
    public ResponseEntity<ApiResponse<VillageResponseDTO>> getVillageById(
            @PathVariable Long id) {
        try {
            VillageResponseDTO result = villageService.getVillageById(id);
            return ResponseEntity.ok(ApiResponse.success("查询成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 创建村庄信息
     * <p>
     * 创建新的村庄信息，表单数据通过@Valid注解进行验证
     * </p>
     *
     * @param createDTO 创建村庄的数据传输对象
     * @return 创建成功后的村庄信息
     */
    @ApiOperation(value = "创建村庄", notes = "创建新的村庄信息")
    @PostMapping
    
    public ResponseEntity<ApiResponse<VillageResponseDTO>> createVillage(
            @Valid @RequestBody VillageCreateDTO createDTO) {
        try {
            VillageResponseDTO result = villageService.createVillage(createDTO);
            return ResponseEntity.ok(ApiResponse.success("创建成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 更新村庄信息
     * <p>
     * 根据ID更新村庄信息，若ID不存在则会抛出异常
     * </p>
     *
     * @param id        村庄信息ID
     * @param updateDTO 更新村庄的数据传输对象
     * @return 更新后的村庄信息
     */
    @ApiOperation(value = "更新村庄信息", notes = "根据ID更新村庄信息")
    @PutMapping("/{id}")
    
    public ResponseEntity<ApiResponse<VillageResponseDTO>> updateVillage(
            @PathVariable Long id,
            @Valid @RequestBody VillageUpdateDTO updateDTO) {
        try {
            updateDTO.setId(id);
            VillageResponseDTO result = villageService.updateVillage(updateDTO);
            return ResponseEntity.ok(ApiResponse.success("更新成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 删除村庄信息
     * <p>
     * 根据ID删除村庄信息（软删除），若ID不存在则会抛出异常
     * </p>
     *
     * @param id 村庄信息ID
     * @return 删除操作响应
     */
    @ApiOperation(value = "删除村庄", notes = "根据ID删除村庄信息（软删除）")
    @DeleteMapping("/{id}")
    
    public ResponseEntity<ApiResponse<Void>> deleteVillage(
            @PathVariable Long id) {
        try {
            villageService.deleteVillage(id);
            return ResponseEntity.ok(ApiResponse.success("删除成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 批量删除村庄信息
     * <p>
     * 根据ID列表批量删除村庄信息（软删除），若任何ID不存在则会抛出异常
     * </p>
     *
     * @param ids 村庄信息ID列表
     * @return 批量删除操作响应
     */
    @ApiOperation(value = "批量删除村庄", notes = "根据ID列表批量删除村庄（软删除）")
    @DeleteMapping("/batch")
    
    public ResponseEntity<ApiResponse<Void>> batchDeleteVillages(
            @RequestBody List<Long> ids) {
        try {
            villageService.batchDeleteVillages(ids);
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
     * 获取所有可用村庄信息
     * <p>
     * 获取所有可用的村庄信息列表，用于下拉选择框等场景
     * </p>
     *
     * @return 所有可用的村庄信息列表
     */
    @ApiOperation(value = "获取所有可用村庄信息", notes = "获取系统中所有可用于选择村庄列表")
    @GetMapping("/available")
    
    public ResponseEntity<ApiResponse<List<Village>>> getAvailableVillages() {
        try {
            List<Village> result = villageService.getAvailableVillages();
            return ResponseEntity.ok(ApiResponse.success("查询成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 统计村庄信息总数
     * <p>
     * 统计系统中村庄信息的总数
     * </p>
     *
     * @return 村庄信息总数
     */
    @ApiOperation(value = "统计村庄总数", notes = "统计系统中村庄的总数")
    @GetMapping("/count")
    
    public ResponseEntity<ApiResponse<Long>> countTotal() {
        try {
            long result = villageService.countTotal();
            return ResponseEntity.ok(ApiResponse.success("统计成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }
}
