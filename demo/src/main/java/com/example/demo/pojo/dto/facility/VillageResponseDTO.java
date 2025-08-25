package com.example.demo.pojo.dto.facility;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 村庄信息响应DTO
 * 用于返回村庄信息查询结果
 */
@Data
public class VillageResponseDTO {

    /**
     * 村庄ID
     * 唯一标识符
     */
    private Long id;

    /**
     * 村庄名称
     * 例如：张家村
     */
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

    /**
     * 记录创建时间
     * 格式：yyyy-MM-dd HH:mm:ss
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 记录更新时间
     * 格式：yyyy-MM-dd HH:mm:ss
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
