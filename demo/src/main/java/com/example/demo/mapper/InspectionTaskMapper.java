package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.dto.inspection.InspectionTaskResponseDTO;
import com.example.demo.entity.inspection.InspectionTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InspectionTaskMapper extends BaseMapper<InspectionTask> {

    List<InspectionTaskResponseDTO> selectTaskPage(
            @Param("status") String status,
            @Param("statuses") List<String> statuses,
            @Param("assigneeId") Long assigneeId,
            @Param("facilityType") String facilityType,
            @Param("facilityId") Long facilityId,
            @Param("keyword") String keyword,
            @Param("frequency") String frequency,
            @Param("scheduledDate") String scheduledDate,
            @Param("sort") String sort
    );

    InspectionTaskResponseDTO selectTaskById(@Param("id") Long id);

    int insertTask(InspectionTask task);

    int updateTask(InspectionTask task);

    int softDeleteTask(@Param("id") Long id);
} 