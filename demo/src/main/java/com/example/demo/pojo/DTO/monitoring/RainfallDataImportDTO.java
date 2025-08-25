package com.example.demo.pojo.DTO.monitoring;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;

/**
 * 雨情数据导入DTO
 * 用于Excel数据导入时的数据传输
 * 
 * @author System
 * @since 2025-07-10
 */
@Data
public class RainfallDataImportDTO {

    /**
     * Excel行号
     * 例如: 2
     * 不能为空
     */
    @NotNull(message = "Excel行号不能为空")
    private Integer rowNumber;

    /**
     * 监测时间
     * 格式: YYYY-MM-DD HH:mm:ss
     * 例如: 2025-07-05 15:00:00
     * 不能为空
     */
    @NotBlank(message = "监测时间不能为空")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$", 
             message = "监测时间格式错误，应为YYYY-MM-DD HH:mm:ss")
    private String monitoringTime;

    /**
     * 站码
     * 例如: 24083438
     * 不能为空
     */
    @NotBlank(message = "站码不能为空")
    private String stationCode;

    /**
     * 降雨量(mm)
     * 例如: 2.5
     * 范围: 0.0 - 999.9
     */
    @DecimalMin(value = "0.0", message = "降雨量不能小于0")
    @DecimalMax(value = "999.9", message = "降雨量不能大于999.9")
    private BigDecimal rainfall;

    /**
     * 降雨强度(mm/h)
     * 例如: 2.5
     * 范围: 0.0 - 999.9
     */
    @DecimalMin(value = "0.0", message = "降雨强度不能小于0")
    @DecimalMax(value = "999.9", message = "降雨强度不能大于999.9")
    private BigDecimal rainfallIntensity;

    /**
     * 累计降雨量(mm)
     * 例如: 30.0
     * 范围: 0.0 - 9999.9
     */
    @DecimalMin(value = "0.0", message = "累计降雨量不能小于0")
    @DecimalMax(value = "9999.9", message = "累计降雨量不能大于9999.9")
    private BigDecimal cumulativeRainfall;
} 