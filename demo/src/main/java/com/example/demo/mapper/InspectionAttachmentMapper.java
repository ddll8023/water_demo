package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.inspection.InspectionAttachment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InspectionAttachmentMapper extends BaseMapper<InspectionAttachment> {

    int insertBatch(@Param("list") List<InspectionAttachment> attachments);

    List<InspectionAttachment> selectByRecordId(@Param("recordId") Long recordId);
} 