package com.example.demo.pojo.DTO.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 查询DTO基类
 * 统一实现分页和排序功能，为所有QueryDTO提供通用字段
 */
@Data
@Schema(name = "BaseQueryDTO", description = "查询DTO基类")
public abstract class BaseQueryDTO implements Serializable {

    @Min(value = 1, message = "页码必须大于0")
    @Schema(name = "page", description = "当前页码", example = "1")
    private Integer page = 1;

    @Min(value = 1, message = "每页记录数必须大于0")
    @Max(value = 100, message = "每页记录数不能超过100")
    @Schema(name = "size", description = "每页记录数", example = "10")
    private Integer size = 10;

    @Size(max = 100, message = "搜索关键词长度不能超过100")
    @Schema(name = "keyword", description = "搜索关键词", example = "")
    private String keyword;

    @Size(max = 50, message = "排序字段长度不能超过50")
    @Schema(name = "sortBy", description = "排序字段", example = "createdAt")
    private String sortBy = "createdAt";

    @Size(max = 10, message = "排序方向长度不能超过10")
    @Schema(name = "sortOrder", description = "排序方向(ASC/DESC)", example = "DESC")
    private String sortOrder = "DESC";
} 