package com.example.demo.pojo.DTO.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 部门响应DTO
 * 用于API响应，包含部门的完整信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DepartmentResponseDTO {

    /**
     * 部门 ID
     */
    private Long id;

    /**
     * 部门名称
     */
    private String name;

    /**
     * 父部门 ID (支持层级)
     */
    private Long parentId;

    /**
     * 父部门名称
     */
    private String parentName;

    /**
     * 部门职责
     */
    private String duty;

    /**
     * 联系方式
     */
    private String contact;

    /**
     * 所属行政区域 ID
     */
    private Long regionId;

    /**
     * 所属行政区域名称
     */
    private String regionName;

    /**
     * 部门是否启用
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
     * 部门人员数量（统计字段）
     * 通过关联查询personnel表统计获取
     */
    private Integer personnelCount;

    /**
     * 子部门列表（用于构建树形结构）
     */
    private List<DepartmentResponseDTO> children;
}
