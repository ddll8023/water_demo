package com.example.demo.pojo.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 部门树形结构视图对象
 * 专门用于管理信息服务模块的部门树形结构查询
 */
@Data
@Schema(name = "DepartmentTreeVO", description = "部门树形结构视图对象")
public class DepartmentTreeVO implements Serializable {

    @Schema(name = "id", description = "部门ID")
    private Long id;

    @Schema(name = "name", description = "部门名称")
    private String name;

    @Schema(name = "description", description = "部门描述")
    private String description;

    @Schema(name = "parentDepartment", description = "上级部门名称")
    private String parentDepartment;

    @Schema(name = "parentId", description = "上级部门ID")
    private Long parentId;

    @Schema(name = "contactInfo", description = "联系信息")
    private String contactInfo;

    @Schema(name = "responsibilities", description = "职责范围")
    private String responsibilities;

    @Schema(name = "personnelCount", description = "人员数量（统计字段）")
    private Integer personnelCount;

    @Schema(name = "isActive", description = "是否启用")
    private String isActive;

    @Schema(name = "createdAt", description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(name = "updatedAt", description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    @Schema(name = "children", description = "子部门列表")
    private List<DepartmentTreeVO> children;
} 