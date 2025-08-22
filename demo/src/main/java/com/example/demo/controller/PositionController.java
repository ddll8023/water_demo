package com.example.demo.controller;

import com.example.demo.dto.system.PositionCreateDTO;
import com.example.demo.dto.system.PositionResponseDTO;
import com.example.demo.dto.system.PositionUpdateDTO;
import com.example.demo.common.ApiResponse;
import com.example.demo.dto.common.PageResponseDTO;
import com.example.demo.service.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 岗位管理控制器
 * 提供岗位的增删改查、统计等功能
 * 
 * @author system
 * @since 1.0
 */
@RestController
@RequestMapping("/api/positions")
@RequiredArgsConstructor
@Api(tags = "岗位管理", description = "岗位相关的CRUD操作")
public class PositionController {

    /**
     * 岗位服务接口，处理岗位相关的业务逻辑
     */
    private final PositionService positionService;

    /**
     * 分页查询岗位列表
     *
     * @param page 页码，默认为1
     * @param size 每页大小，默认为10
     * @param keyword 搜索关键词，可选
     * @return 分页的岗位列表数据
     */
    @GetMapping
    @PreAuthorize("hasAuthority('system:manage')")
    @ApiOperation(value = "分页查询岗位列表", notes = "根据条件分页查询岗位信息")
    public ResponseEntity<ApiResponse<PageResponseDTO<PositionResponseDTO>>> getPositionPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        try {
            // 调用服务层方法获取分页数据
            PageResponseDTO<PositionResponseDTO> result = positionService.getPositionPage(page, size, keyword);
            return ResponseEntity.ok(ApiResponse.success("查询成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 创建岗位
     *
     * @param createDTO 岗位创建信息DTO
     * @return 创建成功的岗位信息
     */
    @PostMapping
    @PreAuthorize("hasAuthority('system:manage')")
    @ApiOperation(value = "创建岗位", notes = "创建新的岗位信息")
    public ResponseEntity<ApiResponse<PositionResponseDTO>> createPosition(
            @Valid @RequestBody PositionCreateDTO createDTO) {
        try {
            // 创建新岗位
            PositionResponseDTO position = positionService.createPosition(createDTO);
            return ResponseEntity.ok(ApiResponse.success("岗位创建成功", position));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error(500, "系统内部错误"));
        }
    }

    /**
     * 更新岗位信息
     *
     * @param id 需要更新的岗位ID
     * @param updateDTO 岗位更新信息DTO
     * @return 更新后的岗位信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:manage')")
    @ApiOperation(value = "更新岗位信息", notes = "根据ID更新岗位信息")
    public ResponseEntity<ApiResponse<PositionResponseDTO>> updatePosition(
            @PathVariable Long id,
            @Valid @RequestBody PositionUpdateDTO updateDTO) {
        try {
            // 更新岗位信息
            PositionResponseDTO position = positionService.updatePosition(id, updateDTO);
            return ResponseEntity.ok(ApiResponse.success("岗位更新成功", position));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error(500, "系统内部错误"));
        }
    }

    /**
     * 删除岗位
     *
     * @param id 需要删除的岗位ID
     * @return 删除操作结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:manage')")
    @ApiOperation(value = "删除岗位", notes = "根据ID删除岗位信息（软删除）")
    public ResponseEntity<ApiResponse<Void>> deletePosition(
            @PathVariable Long id) {
        try {
            // 删除指定岗位
            positionService.deletePosition(id);
            return ResponseEntity.ok(ApiResponse.success("岗位删除成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error(500, "系统内部错误"));
        }
    }

    

    /**
     * 获取岗位下的人员列表
     *
     * @param id 岗位ID
     * @return 指定岗位下的人员列表
     */
    @GetMapping("/{id}/personnel")
    @PreAuthorize("hasAnyAuthority('system:manage', 'business:manage')")
    @ApiOperation(value = "获取岗位下的人员列表", notes = "根据岗位ID获取岗位下的人员列表")
    public ResponseEntity<ApiResponse<List<Object>>> getPositionPersonnel(
            @PathVariable Long id) {
        try {
            // 获取岗位下的人员列表
            List<Object> personnel = positionService.getPositionPersonnel(id);
            return ResponseEntity.ok(ApiResponse.success("查询成功", personnel));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error(500, "系统内部错误"));
        }
    }

    /**
     * 检查岗位名称是否可用
     *
     * @param name 需要检查的岗位名称
     * @param excludeId 排除的岗位ID（编辑时使用）
     * @return 名称可用状态
     */
    @GetMapping("/check-name")
    @PreAuthorize("hasAuthority('system:manage')")
    @ApiOperation(value = "检查岗位名称是否可用", notes = "检查指定的岗位名称是否已存在")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkPositionNameAvailable(
            @RequestParam String name,
            @RequestParam(required = false) Long excludeId) {
        try {
            // 检查岗位名称是否可用
            boolean available = positionService.checkNameAvailable(name, excludeId);
            Map<String, Boolean> result = new HashMap<>();
            result.put("available", available);
            return ResponseEntity.ok(ApiResponse.success("检查完成", result));
        } catch (Exception e) {
            // 记录详细错误信息
            System.err.println("检查岗位名称时发生错误: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body(ApiResponse.error(500, "系统内部错误: " + e.getMessage()));
        }
    }
}
