package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.pojo.entity.warning.WarningRecord;
import com.example.demo.pojo.dto.warning.WarningRecordResponseDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 预警信息记录Mapper接口
 * 提供预警记录相关的数据访问操作
 */
@Mapper
public interface WarningRecordMapper extends BaseMapper<WarningRecord> {

    /**
     * 插入预警记录
     * 替代MyBatis-Plus的insert方法
     */
    int insertWarningRecord(WarningRecord record);
    
    /**
     * 根据ID查询预警记录
     * 替代MyBatis-Plus的selectById方法
     */
    WarningRecord selectWarningById(@Param("id") Long id);
    
    /**
     * 更新预警记录
     * 可用于更新记录各个字段，包括软删除操作
     */
    int updateWarningRecord(WarningRecord record);

    /**
     * 分页查询预警记录基础数据列表
     * 支持PageHelper分页插件和多字段查询
     * 返回的DTO对象中不包含字典翻译和时长计算结果，需要在服务层处理
     */
    List<WarningRecordResponseDTO> selectWarningRecordPageWithDetails(
            @Param("warningLocation") String warningLocation,
            @Param("warningType") String warningType,
            @Param("warningLevel") String warningLevel,
            @Param("warningStatus") String warningStatus,
            @Param("projectName") String projectName,
            @Param("startTime") java.time.LocalDateTime startTime,
            @Param("endTime") java.time.LocalDateTime endTime,
            @Param("sortField") String sortField,
            @Param("sortOrder") String sortOrder);

    /**
     * 根据ID查询预警记录详情（包含字典翻译和持续时长计算）
     */
    WarningRecordResponseDTO selectWarningRecordDetailById(@Param("id") Long id);

    /**
     * 解除预警
     */
    int resolveWarning(@Param("id") Long id);

    /**
     * 统计各等级预警数量
     */
    @MapKey("warning_level")
    List<Map<String, Object>> countByLevel();

    /**
     * 获取预警趋势数据
     */
    @MapKey("date")
    List<Map<String, Object>> getWarningTrend(@Param("days") Integer days);

    /**
     * 获取不重复的预警地点列表（用于下拉选择）
     */
    List<String> selectDistinctWarningLocations();

    /**
     * 获取当前活跃的预警信息（状态为"进行中"）
     * 用于"一张图"模块显示
     */
    List<WarningRecordResponseDTO> selectActiveWarnings();
}
