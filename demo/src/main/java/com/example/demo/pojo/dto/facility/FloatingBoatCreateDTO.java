package com.example.demo.pojo.dto.facility;

import lombok.Data;

import javax.validation.constraints.NotBlank;

import java.math.BigDecimal;

/**
 * 浮船信息创建请求DTO
 * 用于创建新的浮船设施信息
 */
@Data
public class FloatingBoatCreateDTO {

    /**
     * 浮船名称，例如：取水浮船1号
     */
    @NotBlank(message = "浮船名称不能为空")
    private String name;

    /**
     * 经度，表示浮船所在位置的经度坐标
     */
    private BigDecimal longitude;

    /**
     * 纬度，表示浮船所在位置的纬度坐标
     */
    private BigDecimal latitude;

    /**
     * 抽水能力（m³/h），表示浮船每小时可抽取的水量
     */
    private BigDecimal capacity;

    /**
     * 功率（kW），表示浮船设备的功率消耗
     */
    private BigDecimal powerConsumption;

    /**
     * 抽水状态，例如：working（工作中）、stopped（已停止）等
     */
    private String pumpingStatus;

    /**
     * 备注，用于记录浮船的其他相关信息
     */
    private String remark;
}
