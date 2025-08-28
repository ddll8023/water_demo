package com.example.demo.pojo.DTO.management;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 人员信息查询数据传输对象
 */
@Data
@Schema(description = "人员信息查询参数")
public class PersonnelInfoQueryDTO implements Serializable {

    @Schema(description = "人员姓名过滤", example = "张三")
    @Size(max = 50, message = "姓名长度不能超过50个字符")
    private String name;

    @Schema(description = "部门ID过滤", example = "1")
    private Long departmentId;

    @Schema(description = "岗位ID过滤", example = "1")
    private Long positionId;

    @Schema(description = "页码", example = "1")
    @Min(value = 1, message = "页码必须大于0")
    private Integer page = 1;

    @Schema(description = "每页大小", example = "10")
    @Min(value = 1, message = "每页大小必须大于0")
    private Integer size = 10;
} 