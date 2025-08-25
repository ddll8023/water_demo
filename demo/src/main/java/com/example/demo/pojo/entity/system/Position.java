package com.example.demo.pojo.entity.system;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

/**
 * 岗位表实体类
 * 对应数据库表: positions
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("positions")
public class Position {

    /**
     * 岗位 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 岗位名称
     */
    @TableField("name")
    private String name;

    /**
     * 岗位描述
     */
    @TableField("description")
    private String description;

    /**
     * 岗位职责
     */
    @TableField("responsibilities")
    private String responsibilities;

    /**
     * 岗位级别
     */
    @TableField("level")
    private String level;

    /**
     * 人员数量（统计字段，不对应数据库字段）
     * 通过关联查询personnel表统计获取
     */
    @TableField(exist = false)
    private Integer personnelCount;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 软删除标记
     */
    @TableLogic
    @TableField("deleted_at")
    private LocalDateTime deletedAt;

    // 注意：MyBatis-Plus中不直接支持关联查询
    // 以下关联数据需要通过Service层或者自定义SQL来处理：
    // - 该岗位的人员列表：通过查询position_id = this.id的人员记录
}
