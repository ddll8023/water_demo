package com.example.demo.pojo.DTO.system;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 字典数据更新DTO
 * 用于更新字典数据信息的请求
 */
@Data
@Schema(description = "字典数据更新DTO")
public class DictDataUpdateDTO implements Serializable {

    @NotNull(message = "字典数据ID不能为空")
    @Schema(description = "字典数据ID", required = true, example = "1")
    private Long id;

    @NotNull(message = "字典类型ID不能为空")
    @Schema(description = "关联字典类型ID", required = true, example = "1")
    private Long typeId;

    @NotBlank(message = "字典标签不能为空")
    @Size(max = 100, message = "字典标签长度不能超过100个字符")
    @Schema(description = "字典标签（显示值）", required = true, example = "启用")
    private String dataLabel;

    @NotBlank(message = "字典键值不能为空")
    @Size(max = 100, message = "字典键值长度不能超过100个字符")
    @Schema(description = "字典键值（实际值）", required = true, example = "1")
    private String dataValue;

    @Size(max = 500, message = "描述信息长度不能超过500个字符")
    @Schema(description = "描述信息", example = "字典数据的详细描述")
    private String description;

    @NotNull(message = "排序值不能为空")
    @Schema(description = "排序值", required = true, example = "1")
    private Integer sortOrder;

    @NotNull(message = "启用状态不能为空")
    @Schema(description = "是否启用", required = true, example = "1")
    private String isActive = "1";
}
