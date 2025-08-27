package com.example.demo.pojo.DTO.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Size;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * 角色更新DTO
 * 用于更新角色信息的请求
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "角色更新数据传输对象")
public class RoleUpdateDTO {

    /**
     * 角色名称
     */
    @Size(max = 100, message = "角色名称长度不能超过100个字符")
    @Schema(description = "角色名称", example = "系统管理员")
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
    private Integer sortOrder;
    
    /**
     * 角色是否启用
     */
    @Schema(description = "角色是否启用", example = "1", allowableValues = {"0", "1"})
    private String isActive;
    
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
