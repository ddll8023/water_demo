package com.example.demo.pojo.DTO.system;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 字典类型查询参数DTO
 */
@Data
@Schema(description = "字典类型查询参数")
public class DictTypeQueryDTO implements Serializable {

    @Schema(description = "页码", example = "1")
    @Min(value = 1, message = "页码必须大于0")
    private Integer page = 1;

    @Schema(description = "页面大小", example = "10")
    @Min(value = 1, message = "页面大小必须大于0")
    @Max(value = 100, message = "页面大小不能超过100")
    private Integer size = 10;

    @Schema(description = "关键词搜索", example = "系统")
    @Size(max = 100, message = "关键词长度不能超过100个字符")
    private String keyword;

    @Schema(description = "类型编码", example = "sys_status")
    @Size(max = 50, message = "类型编码长度不能超过50个字符")
    private String typeCode;

    @Schema(description = "类型名称", example = "系统状态")
    @Size(max = 100, message = "类型名称长度不能超过100个字符")
    private String typeName;

    @Schema(description = "是否启用", example = "1")
    private String isActive;

    @Schema(description = "排序字段", example = "createdAt desc")
    @Size(max = 50, message = "排序字段长度不能超过50个字符")
    private String sort;
} 