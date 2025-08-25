package com.example.demo.pojo.DTO.facility;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 浮船信息更新请求DTO
 */
@Data
public class FloatingBoatUpdateDTO {

    /**
     * 浮船ID
     * 例如: 1
     */
    @NotNull(message = "浮船ID不能为空")
    private Long id;

    /**
     * 浮船名称
     * 例如: 取水浮船1号
     */
    @NotBlank(message = "浮船名称不能为空")
    private String name;

    /**
     * 经度
     * 例如: 114.123456
     */
    private BigDecimal longitude;

    /**
     * 纬度
     * 例如: 30.123456
     */
    private BigDecimal latitude;

    /**
     * 抽水能力（m³/h）
     * 例如: 200
     */
    private BigDecimal capacity;

    /**
     * 功率（kW）
     * 例如: 50
     */
    private BigDecimal powerConsumption;

    /**
     * 抽水状态
     * 例如: working, maintenance, fault
     */
    private String pumpingStatus;

    /**
     * 备注
     * 例如: 正常运行
     */
    private String remark;
}
