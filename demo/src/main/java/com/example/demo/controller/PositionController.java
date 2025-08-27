package com.example.demo.controller;

import com.example.demo.pojo.DTO.system.PositionCreateDTO;
import com.example.demo.pojo.DTO.system.PositionQueryDTO;
import com.example.demo.pojo.DTO.system.PositionResponseDTO;
import com.example.demo.pojo.DTO.system.PositionUpdateDTO;
import com.example.demo.pojo.VO.PositionVO;
import com.example.demo.common.ApiResponse;
import com.example.demo.common.PageResult;
import com.example.demo.service.PositionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

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
@Tag(name = "岗位管理", description = "岗位相关的CRUD操作")
public class PositionController {

    private final PositionService positionService;

    /**
     * 分页查询岗位列表
     *
     * @param positionQueryDTO 查询条件DTO
     * @return 分页的岗位列表数据
     */
    @GetMapping("")
    @Operation(summary = "分页查询岗位列表", description = "根据条件分页查询岗位信息")
    public ResponseEntity<ApiResponse<PageResult<PositionVO>>> getPositionPage(
            @Valid PositionQueryDTO positionQueryDTO) {
        // 调用服务层方法获取分页数据
        PageResult<PositionVO> result = positionService.getPositionPage(positionQueryDTO);
        return ResponseEntity.ok(ApiResponse.success("查询成功", result));
    }

    /**
     * 创建岗位
     *
     * @param positionCreateDTO 岗位创建信息DTO
     * @return 创建操作结果
     */
    @PostMapping("/create")
    @Operation(summary = "创建岗位", description = "创建新的岗位信息")
    public ResponseEntity<ApiResponse<Void>> createPosition(
            @Valid @RequestBody PositionCreateDTO positionCreateDTO) {
        // 创建新岗位
        positionService.createPosition(positionCreateDTO);
        return ResponseEntity.ok(ApiResponse.success("岗位创建成功"));
    }

    /**
     * 更新岗位信息
     *
     * @param id 需要更新的岗位ID
     * @param positionUpdateDTO 岗位更新信息DTO
     * @return 更新操作结果
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新岗位信息", description = "根据ID更新岗位信息")
    public ResponseEntity<ApiResponse<Void>> updatePosition(
            @PathVariable Long id,
            @Valid @RequestBody PositionUpdateDTO positionUpdateDTO) {
        // 更新岗位信息
        positionService.updatePosition(id, positionUpdateDTO);
        return ResponseEntity.ok(ApiResponse.success("岗位更新成功"));
    }

    /**
     * 删除岗位
     *
     * @param id 需要删除的岗位ID
     * @return 删除操作结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除岗位", description = "根据ID删除岗位信息（软删除）")
    public ResponseEntity<ApiResponse<Void>> deletePosition(
            @PathVariable Long id) {
        // 删除指定岗位
        positionService.deletePosition(id);
        return ResponseEntity.ok(ApiResponse.success("岗位删除成功"));
    }

    /**
     * 检查岗位名称是否可用
     * 
     * @param name 需要检查的岗位名称
     * @param excludeId 排除的岗位ID（编辑时使用）
     * @return 名称可用状态
     */
    @GetMapping("/check-name")
    @Operation(summary = "检查岗位名称是否可用", description = "检查指定的岗位名称是否已存在")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkPositionNameAvailable(
            @RequestParam String name,
            @RequestParam(required = false) Long excludeId) {
        // 检查岗位名称是否可用
        Map<String, Boolean> result = positionService.checkNameAvailable(name, excludeId);

        return ResponseEntity.ok(ApiResponse.success("检查完成", result));
    }
}
