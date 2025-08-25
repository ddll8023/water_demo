package com.example.demo.pojo.dto.facility;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 村庄信息更新请求DTO
 * 用于更新现有村庄信息记录
 */
@Data
public class VillageUpdateDTO {

    /**
     * 村庄ID
     * 唯一标识符，不能为空
     */
    @NotNull(message = "村庄ID不能为空")
    private Long id;

    /**
     * 村庄名称
     * 不能为空
     * 例如：张家村
     */
    @NotBlank(message = "村庄名称不能为空")
    private String name;

    /**
     * 村庄地理位置-经度
     * 例如：114.123456
     */
    private BigDecimal longitude;

    /**
     * 村庄地理位置-纬度
     * 例如：30.123456
     */
    private BigDecimal latitude;

    /**
     * 行政区划代码
     * 例如：420100（武汉市）
     */
    private String administrativeCode;

    /**
     * 现状人口数量（单位：人）
     * 例如：500
     */
    private Integer currentPopulation;

    /**
     * 备注信息
     * 其他补充说明
     * 例如：山区村庄，主要依靠地下水供水
     */
    private String remark;
}
