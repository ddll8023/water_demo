package com.example.demo.pojo.entity.facility;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 设施类型管理表实体类
 * 对应数据库表: facility_types
 */
@Data
@Schema(name = "FacilityType", description = "设施类型管理实体")
public class FacilityType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "id", description = "设施类型ID")
    private Long id;

    @Schema(name = "typeCode", description = "设施类型编码，唯一标识")
    private String typeCode;

    @Schema(name = "typeName", description = "设施类型名称")
    private String typeName;

    @Schema(name = "description", description = "设施类型描述")
    private String description;

    @Schema(name = "entityTable", description = "对应数据库表名")
    private String entityTable;

    @Schema(name = "entityClass", description = "对应实体类名")
    private String entityClass;

    @Schema(name = "apiPath", description = "API路径标识")
    private String apiPath;

    @Schema(name = "iconClass", description = "图标样式类")
    private String iconClass;

    @Schema(name = "mapIcon", description = "地图图标标识")
    private String mapIcon;

    @Schema(name = "sortOrder", description = "排序值，数值越小优先级越高")
    private Integer sortOrder;

    @Schema(name = "isActive", description = "是否启用(1:启用,0:禁用)")
    private String isActive = "1";

    @Schema(name = "createdAt", description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(name = "updatedAt", description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @Schema(name = "deletedAt", description = "软删除标记")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deletedAt;
} 