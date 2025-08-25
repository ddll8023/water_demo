package com.example.demo.pojo.entity.inspection;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("inspection_attachments")
public class InspectionAttachment {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("record_id")
    private Long recordId;

    @TableField("file_name")
    private String fileName;

    @TableField("file_path")
    private String filePath;

    @TableField("content_type")
    private String contentType;

    @TableField("file_size")
    private Long fileSize;

    @TableField("created_at")
    private LocalDateTime createdAt;
} 