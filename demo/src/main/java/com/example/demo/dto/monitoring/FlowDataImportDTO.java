package com.example.demo.dto.monitoring;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;

/**
 * 流量数据导入DTO
 * 用于Excel数据导入时的数据传输对象，存储从Excel表格读取的流量监测数据
 * 每一条记录对应Excel表格中的一行数据
 */
@Data
public class FlowDataImportDTO {

    /**
     * Excel行号
     * 用于标识数据在Excel表格中的位置，方便进行错误定位
     * 从2开始计数（1通常为表头）
     */
    @NotNull(message = "Excel行号不能为空")
    private Integer rowNumber;

    /**
     * 监测时间
     * 记录流量监测的具体时间点
     * 格式要求：YYYY-MM-DD HH:mm:ss，例如：2025-02-27 18:55:00
     */
    @NotBlank(message = "监测时间不能为空")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$", 
             message = "监测时间格式错误，应为YYYY-MM-DD HH:mm:ss")
    private String monitoringTime;

    /**
     * 监测站码
     * 唯一标识监测站点的8位字符串编码
     * 用于关联监测数据与具体的监测站点
     */
    @NotBlank(message = "站码不能为空")
    @Pattern(regexp = "^.{8}$", message = "站码必须是8位字符串")
    private String stationCode;

    /**
     * 监测站点名称（可选，用于自动创建站点时作为名称）
     */
    private String stationName;

    /**
     * 瞬时流量
     * 单位：立方米/秒(m³/s)
     * 记录某一时刻的水流通过断面的体积流量
     * 取值范围：-1000.0 ~ 10000.0
     */
    @DecimalMin(value = "-1000.0", message = "瞬时流量不能小于-1000")
    @DecimalMax(value = "10000.0", message = "瞬时流量不能大于10000")
    private BigDecimal instantFlow;

    /**
     * 累计流量
     * 单位：立方米(m³)
     * 记录从计量开始至当前时间点的总流量
     * 取值范围：0.0 ~ 999999999.0
     */
    @DecimalMin(value = "0.0", message = "累计流量不能小于0")
    @DecimalMax(value = "999999999.0", message = "累计流量不能大于999999999")
    private BigDecimal cumulativeFlow;
}
