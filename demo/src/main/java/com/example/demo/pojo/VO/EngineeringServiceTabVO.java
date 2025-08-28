package com.example.demo.pojo.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 工程信息服务Tab配置视图对象
 * 用于处理包含关联信息的Tab配置数据
 * 适用于前端展示和API响应
 */
@Data
@Schema(name = "EngineeringServiceTabVO", description = "工程信息服务Tab配置视图对象")
public class EngineeringServiceTabVO implements Serializable {

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
    private String isVisible;

    @Schema(name = "createdAt", description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(name = "updatedAt", description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @Schema(name = "deletedAt", description = "软删除时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deletedAt;

    // ========== 关联信息字段（非数据库字段，用于存储关联查询结果） ==========

    @Schema(name = "isVisibleLabel", description = "显示状态标签")
    private String isVisibleLabel;

    @Schema(name = "facilityCount", description = "该Tab对应的设施数量")
    private Long facilityCount;

} 