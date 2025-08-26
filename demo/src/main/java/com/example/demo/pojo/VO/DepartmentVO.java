package com.example.demo.pojo.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 部门视图对象
 * 用于API响应，包含部门的完整信息
 */
@Data
@Schema(name = "DepartmentVO", description = "部门视图对象")
public class DepartmentVO implements Serializable {

    @Schema(name = "id", description = "部门ID")
    private Long id;

    @Schema(name = "name", description = "部门名称")
    private String name;

    @Schema(name = "parentId", description = "父部门ID")
    private Long parentId;

    @Schema(name = "parentName", description = "父部门名称")
    private String parentName;

    @Schema(name = "duty", description = "部门职责")
    private String duty;

    @Schema(name = "contact", description = "联系方式")
    private String contact;

    @Schema(name = "regionId", description = "所属行政区域ID")
    private Long regionId;

    @Schema(name = "regionName", description = "所属行政区域名称")
    private String regionName;

    @Schema(name = "isActive", description = "部门是否启用")
    private String isActive;

    @Schema(name = "createdAt", description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(name = "updatedAt", description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;


    @Schema(name = "personnelCount", description = "部门人员数量")
    private Integer personnelCount;

    
    @Schema(name = "children", description = "子部门列表")
    private List<DepartmentVO> children;
} 