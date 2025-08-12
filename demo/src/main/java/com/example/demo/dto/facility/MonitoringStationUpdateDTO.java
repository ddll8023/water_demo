package com.example.demo.dto.facility;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 监测站点更新请求DTO
 */
@Data
public class MonitoringStationUpdateDTO {

    /**
     * 监测站点ID
     * 示例: 1
     */
    @NotNull(message = "监测站点ID不能为空")
    private Long id;

    /**
     * 监测站点编码
     * 示例: MP001
     */
    @NotBlank(message = "监测站点编码不能为空")
    private String stationCode;

    /**
     * 监测站点名称
     * 示例: 水库入口监测点
     */
    @NotBlank(message = "监测站点名称不能为空")
    private String name;

    /**
     * 水系名称
     * 示例: 长江
     */
    private String waterSystemName;

    /**
     * 河流名称
     * 示例: 汉江
     */
    private String riverName;

    /**
     * 监测项目码
     * 示例: Q
     */
    @NotBlank(message = "监测项目码不能为空")
    private String monitoringItemCode;

    /**
     * 行政区划代码
     * 示例: 420100
     */
    private String adminRegionCode;

    /**
     * 设站年月
     * 示例: 2020-01-15
     */
    private LocalDate establishmentDate;

    /**
     * 经度
     * 示例: 112.123456
     */
    private BigDecimal longitude;

    /**
     * 纬度
     * 示例: 30.654321
     */
    private BigDecimal latitude;

    /**
     * 备注
     * 示例: 重要监测点
     */
    private String remark;
}
