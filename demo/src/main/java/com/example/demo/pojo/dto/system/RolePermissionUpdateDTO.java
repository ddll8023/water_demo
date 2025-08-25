package com.example.demo.pojo.dto.system;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 角色权限更新DTO
 * 用于更新角色权限关联的请求
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RolePermissionUpdateDTO {

    /**
     * 将要分配给角色的权限 ID 的完整列表
     */
    @NotNull(message = "权限ID列表不能为空")
    private List<Long> permissionIds;
}
