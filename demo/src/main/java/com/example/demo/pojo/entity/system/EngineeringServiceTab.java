package com.example.demo.pojo.entity.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * 工程信息服务Tab配置表实体类
 * 对应数据库表: engineering_service_tabs
 * 用于管理工程信息服务页面的Tab显示配置
 */
@Data
@Schema(name = "EngineeringServiceTab", description = "工程信息服务Tab配置表")
public class EngineeringServiceTab implements Serializable {

    @Schema(name = "id", description = "Tab配置ID")
    private Long id;

    @Schema(name = "tabKey", description = "Tab标识键")
    private String tabKey;

    @Schema(name = "tabName", description = "Tab显示名称")
    private String tabName;

    @Schema(name = "tabIcon", description = "Tab图标类名")
    private String tabIcon;

    @Schema(name = "sortOrder", description = "排序顺序")
    private Integer sortOrder;

    @Schema(name = "isVisible", description = "是否显示")
    private String isVisible = "1";

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