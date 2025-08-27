package com.example.demo.pojo.DTO.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 字典数据创建DTO
 * 用于创建新字典数据的请求
 */
@Data
@Schema(name = "DictDataCreateDTO", description = "字典数据创建DTO")
public class DictDataCreateDTO {

    @NotNull(message = "字典类型ID不能为空")
    @Schema(name = "typeId", description = "关联字典类型ID")
    private Long typeId;

    @NotBlank(message = "字典标签不能为空")
    @Size(max = 100, message = "字典标签长度不能超过100个字符")
    @Schema(name = "dataLabel", description = "字典标签（显示值）")
    private String dataLabel;

    @NotBlank(message = "字典键值不能为空")
    @Size(max = 100, message = "字典键值长度不能超过100个字符")
    @Schema(name = "dataValue", description = "字典键值（实际值）")
    private String dataValue;

    @Size(max = 500, message = "描述信息长度不能超过500个字符")
    @Schema(name = "description", description = "描述信息")
    private String description;

    @NotNull(message = "排序值不能为空")
    @Schema(name = "sortOrder", description = "排序值")
    private Integer sortOrder = 0;

    @Schema(name = "isActive", description = "是否启用")
    private String isActive = "1";
}
