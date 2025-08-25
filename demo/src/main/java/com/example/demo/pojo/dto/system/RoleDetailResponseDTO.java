package com.example.demo.pojo.dto.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色详细响应DTO
 * 用于API响应，包含角色的完整信息和权限列表
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RoleDetailResponseDTO {

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
     * 该角色拥有的权限列表
     */
    private List<PermissionResponseDTO> permissions;
}
