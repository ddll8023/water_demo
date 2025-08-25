package com.example.demo.pojo.DTO.inspection;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class InspectionTaskUpdateDTO {

    @NotNull(message = "任务ID不能为空")
    private Long id;

    private String title;

    private String facilityType;

    private Long facilityId;

    private String frequency;

    private String content;

    private Long assigneeId;

    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate scheduledDate;
} 