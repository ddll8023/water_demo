package com.example.demo.pojo.DTO.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色响应DTO
 * 用于API响应，包含角色的完整信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RoleResponseDTO {

    /**
     * 角色 ID
     */
    private Long id;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 排序值，数值越小优先级越高
     */
    private Integer sortOrder;

    /**
     * 角色是否启用
     */
    private String isActive;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * 角色权限代码列表
     */
    private List<String> permissions;

    /**
     * 角色下用户数量（统计字段）
     */
    private Integer userCount;
}
