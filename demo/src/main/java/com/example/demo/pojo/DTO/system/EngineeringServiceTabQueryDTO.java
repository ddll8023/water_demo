package com.example.demo.pojo.DTO.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 工程信息服务Tab配置查询DTO
 * 用于查询Tab配置信息的条件和分页参数
 * 支持按名称、显示状态等条件进行查询
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "EngineeringServiceTabQueryDTO", description = "工程信息服务Tab配置查询DTO")
public class EngineeringServiceTabQueryDTO {

    @Schema(name = "page", description = "页码，从1开始", example = "1")
    private Integer page = 1;

    @Schema(name = "size", description = "每页大小", example = "10")
    private Integer size = 10;

    @Schema(name = "tabKey", description = "Tab标识键，支持精确匹配", example = "monitoring-stations")
    private String tabKey;

    @Schema(name = "tabName", description = "Tab名称，支持模糊查询", example = "监测")
    private String tabName;

    @Schema(name = "isVisible", description = "是否显示，1-显示，0-隐藏", example = "1")
    private String isVisible;

    @Schema(name = "sortOrder", description = "排序字段，默认按sortOrder升序", example = "sortOrder")
    private String sortBy = "sortOrder";

    @Schema(name = "sortDirection", description = "排序方向，ASC-升序，DESC-降序", example = "ASC")
    private String sortDirection = "ASC";

} 