package com.example.demo.pojo.DTO.warning;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * 预警指标设定更新DTO
 * 用于更新预警指标信息的请求
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class WarningThresholdUpdateDTO {

    /**
     * 预警指标ID
     */
    @NotNull(message = "预警指标ID不能为空")
    private Long id;

    /**
     * 测点名称
     */
    @NotBlank(message = "测点名称不能为空")
    @Size(max = 255, message = "测点名称长度不能超过255个字符")
    private String stationName;

    /**
     * 监测项
     */
    @NotBlank(message = "监测项不能为空")
    @Size(max = 50, message = "监测项长度不能超过50个字符")
    private String monitoringItem;

    /**
     * 上上限
     */
    @DecimalMin(value = "0", message = "上上限必须大于等于0")
    private BigDecimal upperUpperLimit;

    /**
     * 上限
     */
    @DecimalMin(value = "0", message = "上限必须大于等于0")
    private BigDecimal upperLimit;

    /**
     * 下限
     */
    @DecimalMin(value = "0", message = "下限必须大于等于0")
    private BigDecimal lowerLimit;

    /**
     * 下下限
     */
    @DecimalMin(value = "0", message = "下下限必须大于等于0")
    private BigDecimal lowerLowerLimit;

    /**
     * 单位
     */
    @Size(max = 20, message = "单位长度不能超过20个字符")
    private String unit;

    /**
     * 是否启用
     */
    @NotNull(message = "启用状态不能为空")
    private String isActive;
}
