package com.example.demo.dto.inspection;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InspectionRecordResponseDTO {
    private Long id;
    private Long taskId;
    private Long inspectorId;
    // 新增：巡检人员姓名
    private String inspectorName;
    private String facilityType;
    private Long facilityId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime recordTime;
    private String deviceStatus;
    private Integer issueFlag;
    private String issueDescription;
    private String resolution;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime resolvedAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    // 新增：封面图（第一张图片或最近一张）
    private String coverImage;
} 