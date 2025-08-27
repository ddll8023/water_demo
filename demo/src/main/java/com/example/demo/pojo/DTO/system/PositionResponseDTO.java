package com.example.demo.pojo.DTO.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 岗位响应DTO
 * 用于API响应，包含岗位的完整信息
 * 岗位是纯业务概念，不参与权限控制
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "岗位响应DTO")
public class PositionResponseDTO {

    /**
     * 岗位 ID
     */
    @Schema(description = "岗位ID", example = "1")
    private Long id;

    /**
     * 岗位名称
     */
    @Schema(description = "岗位名称", example = "项目经理")
    private String name;

    /**
     * 岗位描述
     */
    @Schema(description = "岗位描述", example = "负责项目的整体规划和管理")
    private String description;

    /**
     * 岗位职责
     */
    @Schema(description = "岗位职责", example = "1. 制定项目计划；2. 协调团队工作；3. 监控项目进度")
    private String responsibilities;

    /**
     * 岗位级别
     */
    @Schema(description = "岗位级别", example = "中级")
    private String level;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间", example = "2024-01-01 10:00:00")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间", example = "2024-01-01 10:00:00")
    private LocalDateTime updatedAt;
}
