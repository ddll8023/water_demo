package com.example.demo.pojo.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 岗位查询结果VO
 * 用于分页查询和单个查询结果展示，包含完整展示数据和关联信息
 */
@Data
@Schema(description = "岗位查询结果VO")
public class PositionVO implements Serializable {

    @Schema(description = "岗位ID")
    private Long id;

    @Schema(description = "岗位名称")
    private String name;

    @Schema(description = "岗位描述")
    private String description;

    @Schema(description = "岗位职责")
    private String responsibilities;

    @Schema(description = "岗位级别")
    private String level;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
} 