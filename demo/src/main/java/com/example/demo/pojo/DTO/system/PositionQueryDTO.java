package com.example.demo.pojo.DTO.system;

import java.io.Serializable;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

/**
 * 岗位查询DTO
 * 用于岗位分页查询的请求参数
 */
@Data
@Schema(description = "岗位查询DTO")
public class PositionQueryDTO implements Serializable {

    @Min(value = 1, message = "页码必须大于0")
    @Schema(description = "当前页码", example = "1")
    private Integer page = 1;

    @Min(value = 1, message = "每页记录数必须大于0")
    @Max(value = 100, message = "每页记录数不能超过100")
    @Schema(description = "每页记录数", example = "10")
    private Integer size = 10;

    @Size(max = 100, message = "关键词长度不能超过100个字符")
    @Schema(description = "搜索关键词（岗位名称）", example = "经理")
    private String keyword;
} 