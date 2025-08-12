package com.example.demo.dto.inspection;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class InspectionTaskCreateDTO {

    @NotBlank(message = "任务标题不能为空")
    private String title;

    @NotBlank(message = "设施类型不能为空")
    private String facilityType;

    @NotNull(message = "设施ID不能为空")
    private Long facilityId;

    // 字典 inspection_frequency
    private String frequency;

    private String content;

    @NotNull(message = "责任人不能为空")
    private Long assigneeId;

    // 字典 inspection_status，创建默认PENDING，由服务层兜底
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate scheduledDate;
} 