package com.example.demo.pojo.DTO.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * 角色创建DTO
 * 用于创建新角色的请求
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "角色创建数据传输对象")
public class RoleCreateDTO {

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 100, message = "角色名称长度不能超过100个字符")
    @Schema(description = "角色名称", example = "系统管理员", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /**
     * 角色描述
     */
    @Size(max = 500, message = "角色描述长度不能超过500个字符")
    @Schema(description = "角色描述", example = "负责系统的整体管理和维护")
    private String description;

    /**
     * 排序值，数值越小优先级越高
     */
    @Min(value = 1, message = "排序值必须大于0")
    @Schema(description = "排序值，数值越小优先级越高", example = "50")
    private Integer sortOrder = 50;  // 默认排序值
    
    /**
     * 角色是否启用
     */
    @Schema(description = "角色是否启用", example = "1", allowableValues = {"0", "1"})
    private String isActive = "1";  // 默认为启用状态
    
    /**
     * 备注信息
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    @Schema(description = "备注信息", example = "角色备注")
    private String remark;
    
    /**
     * 权限ID列表
     */
    @Schema(description = "权限ID列表", example = "[1, 2, 3]")
    private List<Long> permissionIds;
}
