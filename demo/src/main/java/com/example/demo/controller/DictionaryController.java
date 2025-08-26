package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.common.PageResult;
import com.example.demo.pojo.DTO.system.*;
import com.example.demo.service.DictionaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 数据字典管理控制器
 * 整合了字典类型和字典数据的RESTful API接口
 */
@RestController
@RequestMapping("/api/system/dict")
@Slf4j
@Tag(name = "数据字典管理", description = "字典类型和字典数据相关的增删改查操作")
public class DictionaryController {

    @Autowired
    private DictionaryService dictionaryService;

    // =================== 字典类型相关接口 ===================

    /**
     * 分页查询字典类型列表
     */
    @GetMapping("/types")
    
    @Operation(summary = "获取字典类型分页数据", description = "支持按名称关键词模糊搜索，可以筛选启用/禁用状态")
    public ResponseEntity<ApiResponse<PageResult<DictTypeResponseDTO>>> getDictTypes(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String typeCode,
            @RequestParam(required = false) String typeName,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String sort) {

        PageResult<DictTypeResponseDTO> result = dictionaryService.getDictTypes(
            page, size, keyword, typeCode, typeName, isActive, sort);

        return ResponseEntity.ok(ApiResponse.success("查询成功", result));
    }

    /**
     * 根据ID查询字典类型详情
     */
    @GetMapping("/types/{id}")
    
    @Operation(summary = "获取字典类型详情", description = "根据ID查询字典类型详细信息")
    public ResponseEntity<ApiResponse<DictTypeResponseDTO>> getDictTypeById(
            @PathVariable Long id) {

        DictTypeResponseDTO result = dictionaryService.getDictTypeById(id);
        return ResponseEntity.ok(ApiResponse.success("查询成功", result));
    }

    /**
     * 根据类型编码查询字典类型
     */
    @GetMapping("/types/code/{typeCode}")
    
    @Operation(summary = "根据类型编码查询字典类型", description = "根据类型编码查询字典类型详细信息")
    public ResponseEntity<ApiResponse<DictTypeResponseDTO>> getDictTypeByCode(
            @PathVariable String typeCode) {

        DictTypeResponseDTO result = dictionaryService.getDictTypeByCode(typeCode);
        return ResponseEntity.ok(ApiResponse.success("查询成功", result));
    }

    /**
     * 创建字典类型
     */
    @PostMapping("/types")
    
    @Operation(summary = "创建字典类型", description = "创建新的字典类型信息，支持设置类型编码、名称、描述等")
    public ResponseEntity<ApiResponse<DictTypeResponseDTO>> createDictType(
            @Valid @RequestBody DictTypeCreateDTO createDTO) {

        DictTypeResponseDTO result = dictionaryService.createDictType(createDTO);
        return ResponseEntity.ok(ApiResponse.success("字典类型创建成功", result));
    }

    /**
     * 更新字典类型
     */
    @PutMapping("/types/{id}")
    
    @Operation(summary = "更新字典类型信息", description = "更新指定字典类型的基本信息，包括名称、描述等")
    public ResponseEntity<ApiResponse<DictTypeResponseDTO>> updateDictType(
            @PathVariable Long id,
            @Valid @RequestBody DictTypeUpdateDTO updateDTO) {

        // 确保ID一�?
        updateDTO.setId(id);
        DictTypeResponseDTO result = dictionaryService.updateDictType(updateDTO);
        return ResponseEntity.ok(ApiResponse.success("字典类型更新成功", result));
    }

    /**
     * 删除字典类型
     */
    @DeleteMapping("/types/{id}")
    
    @Operation(summary = "删除字典类型", description = "删除指定的字典类型，如果类型下有数据则不允许删除")
    public ResponseEntity<ApiResponse<Void>> deleteDictType(
            @PathVariable Long id) {

        dictionaryService.deleteDictType(id);
        return ResponseEntity.ok(ApiResponse.success("字典类型删除成功"));
    }

    /**
     * 检查类型编码是否存在
     */
    @GetMapping("/types/check-code")
    
    @Operation(summary = "检查类型编码是否存在", description = "验证类型编码在同级类型中是否唯一，用于表单提交前的实时校验")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkTypeCodeExists(
            @RequestParam String typeCode,
            @RequestParam(required = false) Long excludeId) {

        boolean exists = dictionaryService.checkTypeCodeExists(typeCode, excludeId);
        Map<String, Boolean> result = new HashMap<>();
        result.put("exists", exists);
        return ResponseEntity.ok(ApiResponse.success("检查完成", result));
    }

    // =================== 字典数据相关接口 ===================

    /**
     * 根据类型编码查询字典数据
     */
    @GetMapping("/data/type/{typeCode}")
    
    @Operation(summary = "根据类型编码查询字典数据", description = "根据类型编码查询字典数据列表")
    public ResponseEntity<ApiResponse<List<DictDataResponseDTO>>> getDictDataByTypeCode(
            @PathVariable String typeCode) {

        // 调用服务层方法获取指定类型编码的字典数据列表
        List<DictDataResponseDTO> result = dictionaryService.getDictDataByTypeCode(typeCode);
        return ResponseEntity.ok(ApiResponse.success("查询成功", result));
    }

    /**
     * 根据类型ID查询字典数据
     */
    @GetMapping("/data/type-id/{typeId}")
    
    @Operation(summary = "根据类型ID查询字典数据", description = "根据类型ID查询字典数据列表")
    public ResponseEntity<ApiResponse<List<DictDataResponseDTO>>> getDictDataByTypeId(
            @PathVariable Long typeId) {

        // 调用服务层方法获取指定类型ID的字典数据列表
        List<DictDataResponseDTO> result = dictionaryService.getDictDataByTypeId(typeId);
        return ResponseEntity.ok(ApiResponse.success("查询成功", result));
    }

    /**
     * 创建字典数据
     */
    @PostMapping("/data")
    
        @Operation(summary = "创建字典数据", description = "创建新的字典数据信息，支持设置类型编码、数据值、数据标签等")
    public ResponseEntity<ApiResponse<DictDataResponseDTO>> createDictData(
            @Valid @RequestBody DictDataCreateDTO createDTO) {

        // 调用服务层方法创建字典数据
        DictDataResponseDTO result = dictionaryService.createDictData(createDTO);
        return ResponseEntity.ok(ApiResponse.success("字典数据创建成功", result));
    }

    /**
     * 更新字典数据
     */
    @PutMapping("/data/{id}")
    
    @Operation(summary = "更新字典数据信息", description = "更新指定字典数据的基本信息，包括数据值、标签等")
    public ResponseEntity<ApiResponse<DictDataResponseDTO>> updateDictData(
            @PathVariable Long id,
            @Valid @RequestBody DictDataUpdateDTO updateDTO) {

        // 确保ID一致，防止URI与请求体中ID不匹配
        updateDTO.setId(id);
        // 调用服务层方法更新字典数据
        DictDataResponseDTO result = dictionaryService.updateDictData(updateDTO);
        return ResponseEntity.ok(ApiResponse.success("字典数据更新成功", result));
    }

    /**
     * 删除字典数据
     */
    @DeleteMapping("/data/{id}")
    
    @Operation(summary = "删除字典数据", description = "删除指定的字典数据，如果数据关联了其他业务则不允许删除")
    public ResponseEntity<ApiResponse<Void>> deleteDictData(
            @PathVariable Long id) {

        // 调用服务层方法删除指定ID的字典数据
        dictionaryService.deleteDictData(id);
        return ResponseEntity.ok(ApiResponse.success("字典数据删除成功"));
    }


} 
