package com.example.demo.dto.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 岗位响应DTO
 * 用于API响应，包含岗位的完整信息
 * 岗位是纯业务概念，不参与权限控制
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PositionResponseDTO {

    /**
     * 岗位 ID
     */
    private Long id;

    /**
     * 岗位名称
     */
    private String name;

    /**
     * 岗位描述
     */
    private String description;

    /**
     * 岗位职责
     */
    private String responsibilities;

    /**
     * 岗位级别
     */
    private String level;

    /**
     * 人员数量（统计字段）
     */
    private Integer personnelCount;

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
}
