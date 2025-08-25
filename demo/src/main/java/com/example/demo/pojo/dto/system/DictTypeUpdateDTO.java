package com.example.demo.pojo.dto.system;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 字典类型更新DTO
 * 用于更新字典类型信息的请求
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DictTypeUpdateDTO {

    /**
     * 字典类型ID
     */
    @NotNull(message = "字典类型ID不能为空")
    private Long id;

    /**
     * 字典类型编码
     */
    @NotBlank(message = "字典类型编码不能为空")
    @Size(max = 100, message = "字典类型编码长度不能超过100个字符")
    private String typeCode;

    /**
     * 字典类型名称
     */
    @NotBlank(message = "字典类型名称不能为空")
    @Size(max = 100, message = "字典类型名称长度不能超过100个字符")
    private String typeName;

    /**
     * 描述信息
     */
    @Size(max = 500, message = "描述信息长度不能超过500个字符")
    private String description;

    /**
     * 排序值
     */
    @NotNull(message = "排序值不能为空")
    private Integer sortOrder;

    /**
     * 是否启用
     */
    @NotNull(message = "启用状态不能为空")
    private Boolean isActive;
}
