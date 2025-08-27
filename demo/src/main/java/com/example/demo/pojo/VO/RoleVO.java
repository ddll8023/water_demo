package com.example.demo.pojo.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 角色视图对象
 * 用于API响应，包含角色的完整信息和关联统计数据
 */
@Data
@Schema(name = "RoleVO", description = "角色视图对象")
public class RoleVO implements Serializable {

    @Schema(name = "id", description = "角色ID")
    private Long id;

    @Schema(name = "name", description = "角色名称")
    private String name;

    @Schema(name = "description", description = "角色描述")
    private String description;

    @Schema(name = "sortOrder", description = "排序值，数值越小优先级越高")
    private Integer sortOrder;

    @Schema(name = "isActive", description = "角色是否启用")
    private String isActive;

    @Schema(name = "createdAt", description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(name = "updatedAt", description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
} 