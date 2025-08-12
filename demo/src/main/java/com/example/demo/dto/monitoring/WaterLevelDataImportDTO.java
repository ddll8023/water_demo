package com.example.demo.dto.monitoring;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

/**
 * 水位监测数据导入DTO
 * 用于Excel批量导入水位监测数据
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class WaterLevelDataImportDTO {

    /**
     * 行号（用于错误定位）
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
             message = "监测时间格式错误，正确格式为：yyyy-MM-dd HH:mm:ss")
    private String monitoringTime;

    /**
     * 水位高度(m)
     */
    @NotNull(message = "水位高度不能为空")
    private BigDecimal waterLevel;

    /**
     * 数据质量(1:正常,2:异常,3:缺失)
     */
    private Integer dataQuality = 1;

    /**
     * 采集方式(AUTO:自动,MANUAL:手动)
     */
    private String collectionMethod = "MANUAL";

    /**
     * 数据来源设备
     */
    private String dataSource;

    /**
     * 备注信息
     */
    private String remark;
}
