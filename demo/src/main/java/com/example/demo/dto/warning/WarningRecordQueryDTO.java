package com.example.demo.dto.warning;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 预警信息记录查询条件DTO
 * 用于封装前端传递的预警记录查询参数
 * 
 * @author system
 * @since 1.0
 */
@Data
public class WarningRecordQueryDTO {

    /**
     * 当前页码，默认为1
     */
    private int page = 1;

    /**
     * 每页显示记录数，默认为10
     */
    private int size = 10;

    /**
     * 搜索关键词，主要用于预警地点模糊查询
     */
    private String keyword;

    /**
     * 预警发生地点，如水库、水电站、泵站等
     */
    private String warningLocation;

    /**
     * 预警类型，如水位、水质、流量等
     */
    private String warningType;

    /**
     * 预警等级，1-4级，数字越小级别越高
     */
    private String warningLevel;

    /**
     * 预警状态，如进行中、已解除、已忽略等
     */
    private String warningStatus;

    /**
     * 所属工程名称
     */
    private String projectName;

    /**
     * 查询的开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 查询的结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 排序字段，默认按发生时间降序排列
     */
    private String sort = "occurred_at,desc";
}
