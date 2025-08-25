package com.example.demo.pojo.dto.system;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 字典数据创建DTO
 * 用于创建新字典数据的请求
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DictDataCreateDTO {

    /**
     * 关联字典类型ID
     */
    @NotNull(message = "字典类型ID不能为空")
    private Long typeId;

    /**
     * 字典标签（显示值）
     */
    @NotBlank(message = "字典标签不能为空")
    @Size(max = 100, message = "字典标签长度不能超过100个字符")
    private String dataLabel;

    /**
     * 字典键值（实际值）
     */
    @NotBlank(message = "字典键值不能为空")
    @Size(max = 100, message = "字典键值长度不能超过100个字符")
    private String dataValue;

    /**
     * 描述信息
     */
    @Size(max = 500, message = "描述信息长度不能超过500个字符")
    private String description;

    /**
     * 排序值
     */
    @NotNull(message = "排序值不能为空")
    private Integer sortOrder = 0;

    /**
     * 是否启用
     */
    private Boolean isActive = true;
}
