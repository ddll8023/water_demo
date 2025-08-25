package com.example.demo.pojo.dto.inspection;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class InspectionTaskResponseDTO {
    private Long id;
    private String title;
    private String facilityType;
    private Long facilityId;
    private String frequency;
    // 新增：频次文本
    private String frequencyText;
    private String content;
    private Long assigneeId;
    // 新增：指派人姓名
    private String assigneeName;
    private String status;
    // 新增：状态文本
    private String statusText;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate scheduledDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 新增：工程名称与统计/时间字段
    private String facilityName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    private Integer taskCount;
    private Integer completedCount;
} 