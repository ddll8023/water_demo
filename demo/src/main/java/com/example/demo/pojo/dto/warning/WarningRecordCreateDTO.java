package com.example.demo.pojo.dto.warning;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 预警信息记录创建DTO
 * 用于创建新预警记录的请求
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class WarningRecordCreateDTO {

    /**
     * 预警地点
     */
    @NotBlank(message = "预警地点不能为空")
    @Size(max = 255, message = "预警地点长度不能超过255个字符")
    private String warningLocation;

    /**
     * 预警类型（水位、流量、水质等）
     */
    @NotBlank(message = "预警类型不能为空")
    @Size(max = 100, message = "预警类型长度不能超过100个字符")
    private String warningType;

    /**
     * 预警等级
     */
    @NotBlank(message = "预警等级不能为空")
    @Size(max = 50, message = "预警等级长度不能超过50个字符")
    private String warningLevel;

    /**
     * 预警内容
     */
    @NotBlank(message = "预警内容不能为空")
    private String warningContent;

    /**
     * 所属工程
     */
    @Size(max = 255, message = "所属工程长度不能超过255个字符")
    private String projectName;

    /**
     * 发生时间
     */
    @NotNull(message = "发生时间不能为空")
    @PastOrPresent(message = "发生时间不能设置为未来时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime occurredAt;

    /**
     * 关联预警指标ID
     */
    private Long thresholdId;
}
