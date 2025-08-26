package com.example.demo.pojo.DTO.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 字典类型更新DTO
 * 用于更新字典类型信息的请求
 */
@Data
@Schema(description = "字典类型更新DTO")
public class DictTypeUpdateDTO implements Serializable {

    @NotNull(message = "字典类型ID不能为空")
    @Schema(description = "字典类型ID", required = true, example = "1")
    private Long id;

    @NotBlank(message = "字典类型编码不能为空")
    @Size(max = 100, message = "字典类型编码长度不能超过100个字符")
    @Schema(description = "字典类型编码", required = true, example = "user_status")
    private String typeCode;

    @NotBlank(message = "字典类型名称不能为空")
    @Size(max = 100, message = "字典类型名称长度不能超过100个字符")
    @Schema(description = "字典类型名称", required = true, example = "用户状态")
    private String typeName;

    @Size(max = 500, message = "描述信息长度不能超过500个字符")
    @Schema(description = "描述信息", example = "用户状态相关的字典数据")
    private String description;

    @NotNull(message = "排序值不能为空")
    @Schema(description = "排序值", required = true, example = "1")
    private Integer sortOrder;

    @NotNull(message = "启用状态不能为空")
    @Schema(description = "是否启用", required = true, example = "true")
    private String isActive;
}
