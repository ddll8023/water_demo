package com.example.demo.pojo.entity.facility;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 监测站点信息表实体类
 * 对应数据库表: monitoring_stations
 */
@Data
@Schema(name = "MonitoringStation", description = "监测站点信息实体")
public class MonitoringStation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "id", description = "主键ID")
    private Long id;

    @Schema(name = "stationCode", description = "监测站点编码")
    private String stationCode;

    @Schema(name = "name", description = "监测站点名称")
    private String name;

    @Schema(name = "monitoringItemCode", description = "监测项目码（关联dict_data.data_value，type_code=monitoring_item）")
    private String monitoringItemCode;

    @Schema(name = "adminRegionCode", description = "行政区划代码（关联regions.code）")
    private String adminRegionCode;

    @Schema(name = "establishmentDate", description = "设站年月")
    private LocalDate establishmentDate;

    @Schema(name = "longitude", description = "经度")
    private BigDecimal longitude;

    @Schema(name = "latitude", description = "纬度")
    private BigDecimal latitude;

    @Schema(name = "remark", description = "备注")
    private String remark;

    @Schema(name = "createdAt", description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(name = "updatedAt", description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @Schema(name = "deletedAt", description = "软删除标记")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deletedAt;
}
