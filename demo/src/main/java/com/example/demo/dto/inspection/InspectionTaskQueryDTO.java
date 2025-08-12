package com.example.demo.dto.inspection;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class InspectionTaskQueryDTO {
    private Integer page;
    private Integer size;

    private String status;
    private List<String> statuses; // 新增：多状态过滤
    private Long assigneeId;
    private String facilityType;
    private Long facilityId;

    // 新增：关键词（工程名称/人员姓名），巡检频率精确
    private String keyword;
    private String frequency;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate scheduledDate;

    private String sort; // e.g. created_at,desc | scheduled_date,asc
} 