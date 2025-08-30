package com.example.demo.pojo.entity.facility;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 泵站信息表实体类
 * 对应数据库表: pumping_stations
 */
@Data
@Schema(name = "PumpingStation", description = "泵站信息实体")
public class PumpingStation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "id", description = "主键ID")
    private Long id;

    @Schema(name = "name", description = "泵站名称")
    private String name;

    @Schema(name = "stationCode", description = "泵站编码")
    private String stationCode;

    @Schema(name = "stationType", description = "泵站类型（通过工程服务API获取类型选项）")
    private String stationType;

    @Schema(name = "waterProject", description = "所属供水工程")
    private String waterProject;

    @Schema(name = "waterCompany", description = "所属供水公司")
    private String waterCompany;

    @Schema(name = "longitude", description = "经度")
    private BigDecimal longitude;

    @Schema(name = "latitude", description = "纬度")
    private BigDecimal latitude;

    @Schema(name = "address", description = "地址")
    private String address;

    @Schema(name = "operationMode", description = "运行方式（关联dict_data.data_value，type_code=operation_mode）")
    private String operationMode;

    @Schema(name = "unitCount", description = "机组数量（台）")
    private Integer unitCount;

    @Schema(name = "designScale", description = "设计规模（m³/天）")
    private BigDecimal designScale;

    @Schema(name = "installedCapacity", description = "装机容量（kW）")
    private BigDecimal installedCapacity;

    @Schema(name = "liftHead", description = "扬程（m）")
    private BigDecimal liftHead;

    @Schema(name = "establishmentDate", description = "建站年月")
    private LocalDate establishmentDate;

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
