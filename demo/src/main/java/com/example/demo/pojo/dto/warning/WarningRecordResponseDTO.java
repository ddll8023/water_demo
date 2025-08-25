package com.example.demo.pojo.dto.warning;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 预警信息记录响应DTO
 * 用于返回预警记录信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class WarningRecordResponseDTO {

    /**
     * 预警记录ID
     */
    private Long id;

    /**
     * 预警地点
     */
    private String warningLocation;

    /**
     * 预警类型（水位、流量、水质等）
     */
    private String warningType;

    /**
     * 预警等级
     */
    private String warningLevel;

    /**
     * 预警等级名称（显示用）
     */
    private String warningLevelName;

    /**
     * 预警类型名称（显示用）
     * 通过关联查询dict_data表获取预警类型的显示名称
     */
    private String warningTypeName;

    /**
     * 预警内容
     */
    private String warningContent;

    /**
     * 预警状态（进行中、已解除）
     */
    private String warningStatus;

    /**
     * 预警状态名称（显示用）
     * 通过关联查询dict_data表获取预警状态的显示名称
     */
    private String warningStatusName;

    /**
     * 所属工程
     */
    private String projectName;

    /**
     * 发生时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime occurredAt;

    /**
     * 解除时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime resolvedAt;

    /**
     * 关联预警指标ID
     */
    private Long thresholdId;

    /**
     * 持续时长（显示用）
     */
    private String duration;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
