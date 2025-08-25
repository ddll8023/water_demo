package com.example.demo.pojo.dto.inspection;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class InspectionRecordQueryDTO {
    private Integer page;
    private Integer size;

    private String facilityType;
    private Long facilityId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    private Integer issueFlag; // 0/1

    // 新增：处理状态（0 未处理，1 已处理）
    private Integer processed;

    // 新增：负责人（巡检人员）
    private Long inspectorId;

    private String sort; // e.g. record_time,desc | created_at,desc
} 