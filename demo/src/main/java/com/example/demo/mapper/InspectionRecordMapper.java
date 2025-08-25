package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.pojo.dto.inspection.InspectionRecordResponseDTO;
import com.example.demo.pojo.entity.inspection.InspectionRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface InspectionRecordMapper extends BaseMapper<InspectionRecord> {

    List<InspectionRecordResponseDTO> selectRecordPage(
            @Param("facilityType") String facilityType,
            @Param("facilityId") Long facilityId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("issueFlag") Integer issueFlag,
            @Param("processed") Integer processed,
            @Param("inspectorId") Long inspectorId,
            @Param("sort") String sort
    );

    InspectionRecordResponseDTO selectRecordById(@Param("id") Long id);

    int insertRecord(InspectionRecord record);

    int updateResolution(@Param("id") Long id, @Param("resolution") String resolution);
} 