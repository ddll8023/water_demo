package com.example.demo.pojo.entity.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色表实体类
 * 对应数据库表: roles
 */
@Data
@Schema(name = "Role", description = "角色表")
public class Role {

    @Schema(name = "id", description = "角色ID")
    private Long id;

    @Schema(name = "name", description = "角色名称")
    private String name;

    @Schema(name = "description", description = "角色描述")
    private String description;

    @Schema(name = "sortOrder", description = "排序值")
    private Integer sortOrder;

    @Schema(name = "isActive", description = "角色是否启用")
    private String isActive;

    @Schema(name = "createdAt", description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(name = "updatedAt", description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @Schema(name = "deletedAt", description = "软删除时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deletedAt;

}
