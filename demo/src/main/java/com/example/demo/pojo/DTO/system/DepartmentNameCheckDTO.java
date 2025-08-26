package com.example.demo.pojo.DTO.system;

import java.io.Serializable;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 部门名称检查DTO
 * 用于检查部门名称是否可用的请求参数
 */
@Data
@Schema(description = "部门名称检查DTO")
public class DepartmentNameCheckDTO implements Serializable {

    @NotBlank(message = "部门名称不能为空")
    @Size(max = 255, message = "部门名称长度不能超过255个字符")
    @Schema(description = "待检查的部门名称", example = "研发部门", required = true)
    private String name;

    @Schema(description = "父部门ID，可选参数，存在时检查在指定父部门下的唯一性", example = "1")
    private Long parentId;

    @Schema(description = "排除的部门ID，可选参数，用于编辑时排除自身", example = "2")
    private Long excludeId;
} 