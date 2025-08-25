package com.example.demo.pojo.DTO.facility;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 监测站点创建请求DTO
 */
@Data
public class MonitoringStationCreateDTO {

    /**
     * 监测站点编码
     * 例如: MS001
     */
    @NotBlank(message = "监测站点编码不能为空")
    private String stationCode;

    /**
     * 监测站点名称
     * 例如: 水库入口监测站
     */
    @NotBlank(message = "监测站点名称不能为空")
    private String name;

    /**
     * 水系名称
     * 例如: 长江
     */
    private String waterSystemName;

    /**
     * 河流名称
     * 例如: 汉江
     */
    private String riverName;

    /**
     * 监测项目码
     * 例如: Q(流量)、Z(水位)、P(降雨)、WQ(水质)
     */
    @NotBlank(message = "监测项目码不能为空")
    private String monitoringItemCode;

    /**
     * 行政区划代码
     * 例如: 420100
     */
    private String adminRegionCode;

    /**
     * 设站年月
     * 例如: 2020-01-15
     */
    private LocalDate establishmentDate;

    /**
     * 经度
     * 例如: 112.123456
     */
    private BigDecimal longitude;

    /**
     * 纬度
     * 例如: 30.654321
     */
    private BigDecimal latitude;

    /**
     * 备注
     * 例如: 重要监测点
     */
    private String remark;
}
