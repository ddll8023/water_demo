package com.example.demo.pojo.DTO.monitoring;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

/**
 * 水质监测数据导入DTO
 * 用于Excel导入水质监测数据的数据传输（水平存储模式）
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class WaterQualityDataImportDTO {

    /**
     * Excel行号（用于错误定位）
     */
    private Integer rowNumber;

    /**
     * 监测站点编码
     */
    @NotBlank(message = "监测站点编码不能为空")
    private String stationCode;

    /**
     * 监测站点名称（可选，用于自动创建站点时作为名称）
     */
    private String stationName;

    /**
     * 监测时间
     */
    @NotBlank(message = "监测时间不能为空")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$",
             message = "监测时间格式错误，应为YYYY-MM-DD HH:mm:ss")
    private String monitoringTime;

    /**
     * 水温(℃)
     */
    private BigDecimal waterTemperature;

    /**
     * 浊度(NTU)
     */
    private BigDecimal turbidity;

    /**
     * PH值
     */
    private BigDecimal phValue;

    /**
     * 电导率(uS/cm)
     */
    private BigDecimal conductivity;

    /**
     * 溶解氧(mg/L)
     */
    private BigDecimal dissolvedOxygen;

    /**
     * 氨氮(mg/L)
     */
    private BigDecimal ammoniaNitrogen;

    /**
     * 化学需氧量(mg/L)
     */
    private BigDecimal codValue;

    /**
     * 余氯(mg/L)
     */
    private BigDecimal residualChlorine;

    /**
     * 数据质量(1:正常,2:异常,3:缺失)
     */
    private Integer dataQuality = 1;

    /**
     * 采集方式(AUTO:自动,MANUAL:手动)
     */
    @Pattern(regexp = "^(AUTO|MANUAL)$", message = "采集方式必须是AUTO或MANUAL")
    private String collectionMethod = "AUTO";

    /**
     * 数据来源设备
     */
    private String dataSource;

    /**
     * 备注信息
     */
    private String remark;
}
