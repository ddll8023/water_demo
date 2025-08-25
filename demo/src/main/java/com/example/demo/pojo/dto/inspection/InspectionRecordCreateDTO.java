package com.example.demo.pojo.dto.inspection;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class InspectionRecordCreateDTO {

    private Long taskId; // 可空

    @NotBlank(message = "设施类型不能为空")
    private String facilityType;

    @NotNull(message = "设施ID不能为空")
    private Long facilityId;

    @NotNull(message = "巡检时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime recordTime;

    @NotNull(message = "负责人不能为空")
    private Long inspectorId;

    private String deviceStatus; // 字典 device_status

    @NotNull(message = "是否发现问题不能为空")
    private Integer issueFlag; // 0/1

    private String issueDescription;

    private String resolution;
} 