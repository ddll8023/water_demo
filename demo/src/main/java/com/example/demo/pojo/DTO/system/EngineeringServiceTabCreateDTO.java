package com.example.demo.pojo.DTO.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;

/**
 * 工程信息服务Tab配置创建DTO
 * 用于创建新的Tab配置信息
 * 支持管理员配置工程服务页面的Tab显示
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "EngineeringServiceTabCreateDTO", description = "工程信息服务Tab配置创建DTO")
public class EngineeringServiceTabCreateDTO {

    @Schema(name = "tabKey", description = "Tab标识键，如monitoring-stations", example = "monitoring-stations")
    @NotBlank(message = "Tab标识键不能为空")
    @Size(min = 2, max = 100, message = "Tab标识键长度必须在2-100个字符之间")
    private String tabKey;

    @Schema(name = "tabName", description = "Tab显示名称", example = "监测站点")
    @NotBlank(message = "Tab名称不能为空")
    @Size(min = 1, max = 100, message = "Tab名称长度必须在1-100个字符之间")
    private String tabName;

    @Schema(name = "tabIcon", description = "Tab图标类名", example = "fa-map-marker")
    @NotBlank(message = "Tab图标不能为空")
    @Size(min = 1, max = 100, message = "Tab图标长度必须在1-100个字符之间")
    private String tabIcon;

    @Schema(name = "sortOrder", description = "排序顺序，数值越小优先级越高", example = "1")
    @NotNull(message = "排序顺序不能为空")
    @Min(value = 0, message = "排序顺序不能小于0")
    @Max(value = 999, message = "排序顺序不能大于999")
    private Integer sortOrder;

    @Schema(name = "isVisible", description = "是否显示，1-显示，0-隐藏", example = "1")
    private String isVisible = "1";

} 