package com.example.demo.pojo.DTO.system;

import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 字典类型创建DTO
 * 用于创建新字典类型的请求
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "字典类型创建DTO")
public class DictTypeCreateDTO implements Serializable {

    /**
     * 字典类型编码
     */
    @NotBlank(message = "字典类型编码不能为空")
    @Size(max = 100, message = "字典类型编码长度不能超过100个字符")
    @Schema(description = "字典类型编码", required = true, example = "user_status")
    private String typeCode;

    /**
     * 字典类型名称
     */
    @NotBlank(message = "字典类型名称不能为空")
    @Size(max = 100, message = "字典类型名称长度不能超过100个字符")
    @Schema(description = "字典类型名称", required = true, example = "用户状态")
    private String typeName;

    /**
     * 描述信息
     */
    @Size(max = 500, message = "描述信息长度不能超过500个字符")
    @Schema(description = "描述信息", example = "用户状态相关的字典数据")
    private String description;

    /**
     * 排序值
     */
    @NotNull(message = "排序值不能为空")
    @Schema(description = "排序值", required = true, example = "1")
    private Integer sortOrder = 0;

    /**
     * 是否启用
     */
    @Schema(description = "是否启用", example = "true")
    private String isActive = "1";
}
