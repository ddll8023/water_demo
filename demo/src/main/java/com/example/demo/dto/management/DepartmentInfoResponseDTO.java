package com.example.demo.dto.management;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 部门信息响应DTO
 * 用于管理信息服务模块的部门档案管理
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DepartmentInfoResponseDTO {

    /**
     * 部门ID
     */
    private Long id;

    /**
     * 部门名称
     */
    private String name;

    /**
     * 部门描述
     */
    private String description;

    /**
     * 上级部门名称
     */
    private String parentDepartment;

    /**
     * 上级部门ID
     */
    private Long parentId;

    /**
     * 联系信息
     */
    private String contactInfo;

    /**
     * 职责范围
     */
    private String responsibilities;

    /**
     * 人员数量（统计字段）
     */
    private Integer personnelCount;

    /**
     * 是否启用
     */
    private Boolean isActive;

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
     * 子部门列表
     */
    private List<DepartmentInfoResponseDTO> children;
}
