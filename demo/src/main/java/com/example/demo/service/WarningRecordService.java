package com.example.demo.service;

import com.example.demo.common.PageResult;
import com.example.demo.pojo.DTO.system.DictDataResponseDTO;
import com.example.demo.pojo.DTO.warning.WarningRecordCreateDTO;
import com.example.demo.pojo.DTO.warning.WarningRecordResponseDTO;
import com.example.demo.pojo.DTO.warning.WarningResolveDTO;
import com.example.demo.pojo.entity.warning.WarningRecord;
import com.example.demo.mapper.WarningRecordMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 预警信息记录管理服务
 * 提供预警记录的CRUD操作和业务逻辑处理
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WarningRecordService {

    private final WarningRecordMapper warningRecordMapper;
    private final DictionaryService dictionaryService;
    
    /**
     * 计算预警持续时间并格式化为"Xh Ym"格式
     * 
     * @param occurredAt 预警发生时间
     * @param resolvedAt 预警解除时间，如果为null表示预警仍在进行中
     * @param warningStatus 预警状态，用于判断是否进行中
     * @return 格式化后的持续时间字符串
     */
    private String calculateDuration(LocalDateTime occurredAt, LocalDateTime resolvedAt, String warningStatus) {
        if (occurredAt == null) {
            return "-";
        }
        
        LocalDateTime endTime;
        // 如果已解除，使用解除时间计算，否则使用当前时间
        if (resolvedAt != null) {
            endTime = resolvedAt;
        } else if ("进行中".equals(warningStatus)) {
            endTime = LocalDateTime.now();
        } else {
            return "-";
        }
        
        // 计算时间差（分钟）
        long minutesDiff = Math.abs(ChronoUnit.MINUTES.between(occurredAt, endTime));
        
        // 格式化为小时和分钟
        long hours = minutesDiff / 60;
        long minutes = minutesDiff % 60;
        
        return hours + "h" + minutes + "m";
    }
    
    /**
     * 获取字典值对应的标签名称
     *
     * @param dictTypeCode 字典类型编码
     * @return 字典值到标签的映射
     */
    private Map<String, String> getDictValueLabelMap(String dictTypeCode) {
        try {
            // 通过字典类型编码查询字典数据
            List<DictDataResponseDTO> dictDataList = dictionaryService.getDictDataByTypeCode(dictTypeCode);
            
            // 构建字典值到标签的映射
            return dictDataList.stream()
                .collect(Collectors.toMap(
                    DictDataResponseDTO::getDataValue,  // 字典值作为key
                    DictDataResponseDTO::getDataLabel,  // 字典标签作为value
                    (v1, v2) -> v1  // 如果有重复的key，保留第一个值
                ));
        } catch (Exception e) {
            // 记录异常，但不影响主流程
            log.error("获取字典数据失败: dictTypeCode={}, error={}", dictTypeCode, e.getMessage());
            return new HashMap<>();
        }
    }
    
    /**
     * 根据字典类型和值获取对应的标签
     * 
     * @param dictValueLabelMap 字典值到标签的映射
     * @param dictValue 字典值
     * @return 字典标签，如果不存在则返回原值
     */
    private String getDictLabel(Map<String, String> dictValueLabelMap, String dictValue) {
        if (dictValue == null || dictValueLabelMap == null) {
            return dictValue;
        }
        return dictValueLabelMap.getOrDefault(dictValue, dictValue);
    }

    /**
     * 处理排序参数，将前端传入的排序字符串转换为排序字段和排序方向
     *
     * @param sort 前端传入的排序字符串，如"occurred_at desc"
     * @return 包含排序字段和排序方向的数组，[0]为排序字段，[1]为排序方向
     */
    private String[] parseSortParameter(String sort) {
        String sortField = "occurred_at";  // 默认排序字段
        String sortOrder = "DESC";         // 默认排序方向
        
        if (sort != null && !sort.trim().isEmpty()) {
            sort = sort.trim();
            
            // 解析排序字段和方向
            if (sort.contains("desc")) {
                sortOrder = "DESC";
                sortField = sort.replace("desc", "").trim();
            } else if (sort.contains("asc")) {
                sortOrder = "ASC";
                sortField = sort.replace("asc", "").trim();
            } else {
                // 如果没有指定方向，默认为DESC
                sortField = sort;
            }
            
            // 验证排序字段是否有效
            if (!isValidSortField(sortField)) {
                log.warn("无效的排序字段: {}, 使用默认排序", sortField);
                sortField = "occurred_at";
                sortOrder = "DESC";
            }
        }
        
        return new String[] { sortField, sortOrder };
    }
    
    /**
     * 验证排序字段是否有效
     *
     * @param field 排序字段
     * @return 是否有效
     */
    private boolean isValidSortField(String field) {
        // 定义允许排序的字段列表
        String[] validFields = {
            "occurred_at", "created_at", "warning_level", "warning_status"
        };
        
        if (field == null || field.trim().isEmpty()) {
            return false;
        }
        
        for (String validField : validFields) {
            if (validField.equals(field.trim())) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * 分页查询预警记录列表
     * 包含字典翻译和持续时长计算功能
     */
    public PageResult<WarningRecordResponseDTO> getWarningRecords(int page, int size,
                                                                  String warningLocation, String warningType, String warningLevel, String warningStatus,
                                                                  String projectName, LocalDateTime startTime, LocalDateTime endTime, String sort) {
        log.info("分页查询预警记录: page={}, size={}, warningType={}, warningLevel={}, warningStatus={}, sort={}", 
                page, size, warningType, warningLevel, warningStatus, sort);
        
        // 参数验证
        if (page < 1) page = 1;
        if (size < 1) size = 10;
        
        // 处理排序参数
        String[] sortParams = parseSortParameter(sort);
        String sortField = sortParams[0];
        String sortOrder = sortParams[1];
        
        log.debug("解析排序参数: sortField={}, sortOrder={}", sortField, sortOrder);
        
        // 使用PageHelper开始分页
        PageHelper.startPage(page, size);

        // 使用分页查询获取基础数据
        List<WarningRecordResponseDTO> warningRecords = warningRecordMapper.selectWarningRecordPageWithDetails(
            warningLocation, warningType, warningLevel, warningStatus,
            projectName, startTime, endTime, sortField, sortOrder);
            
        // 获取分页信息
        PageInfo<WarningRecordResponseDTO> pageInfo = new PageInfo<>(warningRecords);
        
        // 批量获取字典数据，用于字典翻译
        Map<String, String> warningLevelDict = getDictValueLabelMap("warning_level");
        
        // 处理每条记录，添加持续时间和字典翻译
        for (WarningRecordResponseDTO record : warningRecords) {
            if (record != null) {
                // 添加持续时间计算
                record.setDuration(calculateDuration(
                    record.getOccurredAt(), 
                    record.getResolvedAt(), 
                    record.getWarningStatus()
                ));
                
                // 添加字典翻译
                record.setWarningLevelName(getDictLabel(warningLevelDict, record.getWarningLevel()));
            }
        }

        return new PageResult<>(
            warningRecords,
            (int) pageInfo.getTotal(),
            page,
            size
        );
    }

    /**
     * 根据ID查询预警记录详情
     * 包含字典翻译和持续时长计算
     */
    public WarningRecordResponseDTO getWarningRecordById(Long id) {
        log.info("根据ID查询预警记录详情: id={}", id);
        
        // 查询基础数据
        WarningRecordResponseDTO record = warningRecordMapper.selectWarningRecordDetailById(id);
        if (record == null) {
            log.error("预警记录不存在: id={}", id);
            throw new RuntimeException("预警记录不存在");
        }
        
        // 获取字典数据
        Map<String, String> warningLevelDict = getDictValueLabelMap("warning_level");
        
        // 添加持续时间计算
        record.setDuration(calculateDuration(
            record.getOccurredAt(), 
            record.getResolvedAt(), 
            record.getWarningStatus()
        ));
        
        // 添加字典翻译
        record.setWarningLevelName(getDictLabel(warningLevelDict, record.getWarningLevel()));
        
        return record;
    }

    /**
     * 创建预警记录
     */
    @Transactional
    public WarningRecordResponseDTO createWarningRecord(WarningRecordCreateDTO createDTO) {
        WarningRecord warningRecord = new WarningRecord();
        BeanUtils.copyProperties(createDTO, warningRecord);
        warningRecord.setWarningStatus("进行中"); // 默认状态为进行中
        warningRecord.setCreatedAt(LocalDateTime.now());
        warningRecord.setUpdatedAt(LocalDateTime.now());

        warningRecordMapper.insertWarningRecord(warningRecord);

        // 查询刚创建的记录详情
        return getWarningRecordById(warningRecord.getId());
    }

    /**
     * 解除预警
     */
    @Transactional
    public boolean resolveWarning(WarningResolveDTO resolveDTO) {
        // 检查预警记录是否存在且为进行中状态
        WarningRecord warningRecord = warningRecordMapper.selectWarningById(resolveDTO.getId());
        if (warningRecord == null) {
            throw new RuntimeException("预警记录不存在");
        }
        if (!"进行中".equals(warningRecord.getWarningStatus())) {
            throw new RuntimeException("只能解除进行中的预警");
        }

        // 解除预警
        int result = warningRecordMapper.resolveWarning(resolveDTO.getId());
        if (result == 0) {
            throw new RuntimeException("预警解除失败");
        }
        return true;
    }

    /**
     * 删除预警记录
     */
    @Transactional
    public boolean deleteWarningRecord(Long id) {
        // 检查预警记录是否存在
        WarningRecord warningRecord = warningRecordMapper.selectWarningById(id);
        if (warningRecord == null) {
            throw new RuntimeException("预警记录不存在");
        }

        // 设置软删除标记
        warningRecord.setDeletedAt(LocalDateTime.now());
        warningRecord.setUpdatedAt(LocalDateTime.now());
        
        // 更新记录实现软删除
        return warningRecordMapper.updateWarningRecord(warningRecord) > 0;
    }

    /**
     * 获取预警统计信息
     */
    public List<Map<String, Object>> getWarningStatistics() {
        return warningRecordMapper.countByLevel();
    }

    /**
     * 获取各等级预警数量统计
     */
    public List<Map<String, Object>> getWarningLevelStatistics() {
        return warningRecordMapper.countByLevel();
    }

    /**
     * 获取预警趋势数据
     */
    public List<Map<String, Object>> getWarningTrend(String period, Integer days) {
        return warningRecordMapper.getWarningTrend(days);
    }

    /**
     * 获取预警地点列表（用于下拉选择）
     * 从现有预警记录中获取不重复的预警地点列表
     */
    public List<String> getWarningLocationOptions() {
        return warningRecordMapper.selectDistinctWarningLocations();
    }
}
