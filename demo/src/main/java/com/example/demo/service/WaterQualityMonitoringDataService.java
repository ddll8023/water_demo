package com.example.demo.service;

import com.example.demo.pojo.dto.monitoring.*;
import com.example.demo.pojo.dto.common.PageResponseDTO;
import com.example.demo.pojo.entity.facility.MonitoringStation;
import com.example.demo.pojo.entity.monitoring.WaterQualityMonitoringData;
import com.example.demo.mapper.MonitoringStationMapper;
import com.example.demo.mapper.WaterQualityMonitoringDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.*;
import java.util.stream.Collectors;

// 导入必要的类
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/**
 * 水质监测数据服务类
 * 提供水质监测数据的业务逻辑处理，包括数据查询、统计分析、图表数据生成、导入导出等功能
 * 
 * 设计说明：
 * - 支持8种水质监测项目：水温、浊度、PH值、电导率、溶解氧、氨氮、化学需氧量、余氯
 * - 采用水平存储模式，每条记录包含同一时间点的所有监测项目数据
 * - 采用分层架构设计，与Controller和Mapper层解耦
 * - 使用事务管理确保数据一致性
 * - 提供完整的异常处理和数据验证
 * - 支持大批量数据导入和导出功能
 * - 提供数据格式转换以保持API向后兼容性
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WaterQualityMonitoringDataService {

    private final WaterQualityMonitoringDataMapper waterQualityMonitoringDataMapper;
    private final MonitoringStationMapper monitoringStationMapper;

    /**
     * 分页查询水质监测数据
     * 
     * @param queryDTO 查询条件DTO
     * @return 分页数据响应
     */
    public PageResponseDTO<WaterQualityMonitoringDataResponseDTO> getWaterQualityMonitoringDataPage(
            WaterQualityMonitoringDataQueryDTO queryDTO) {
        
        log.info("开始分页查询水质监测数据，查询条件: {}", queryDTO);

        // 参数验证
        if (queryDTO.getPage() == null || queryDTO.getPage() < 1) {
            queryDTO.setPage(1);
        }
        if (queryDTO.getSize() == null || queryDTO.getSize() < 1) {
            queryDTO.setSize(10);
        }

        // 使用PageHelper设置分页
        PageHelper.startPage(queryDTO.getPage(), queryDTO.getSize());
        
        // 执行查询
        List<WaterQualityMonitoringDataResponseDTO> list = waterQualityMonitoringDataMapper.selectWaterQualityMonitoringDataPage(
                queryDTO.getStationId(),
                queryDTO.getStartTime(),
                queryDTO.getEndTime(),
                queryDTO.getMonitoringItemCode(),
                queryDTO.getDataQuality(),
                queryDTO.getCollectionMethod(),
                queryDTO.getDataSource(),
                queryDTO.getSort()
        );
        
        // 使用PageInfo包装查询结果
        PageInfo<WaterQualityMonitoringDataResponseDTO> pageInfo = new PageInfo<>(list);

        log.info("水质监测数据查询完成，总记录数: {}, 当前页记录数: {}", 
                pageInfo.getTotal(), pageInfo.getList().size());

        // 返回分页数据
        return new PageResponseDTO<>(
                pageInfo.getList(),
                (int) pageInfo.getTotal(),
                queryDTO.getPage(),
                queryDTO.getSize()
        );
    }

    /**
     * 获取水质图表数据
     * 支持8种水质监测项目的图表数据查询
     *
     * @param stationId 监测站点ID（可选）
     * @param monitoringItemCode 监测项目代码
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param interval 时间间隔(hour:小时,day:天,month:月)
     * @return 图表数据响应
     */
    public WaterQualityChartDataResponseDTO getWaterQualityChartData(Long stationId, String monitoringItemCode,
                                                                     LocalDateTime startTime, LocalDateTime endTime,
                                                                     String interval) {
        log.info("开始查询水质图表数据，站点ID: {}, 监测项目: {}, 时间范围: {} - {}",
                stationId, monitoringItemCode, startTime, endTime);

        // 验证是否指定了监测站点
        if (stationId == null) {
            log.warn("未指定监测站点ID，返回空图表数据");
            WaterQualityChartDataResponseDTO result = new WaterQualityChartDataResponseDTO();
            result.setLabels(new ArrayList<>());
            result.setValues(new ArrayList<>());
            result.setDatasetName("请选择监测站点");
            result.setMonitoringItemCode(monitoringItemCode);
            result.setMonitoringItemName(getMonitoringItemName(monitoringItemCode));
            result.setUnit(getMonitoringItemUnit(monitoringItemCode));
            return result;
        }

        try {
            List<Map<String, Object>> chartData;
            String datasetName;

            // 返回指定站点的数据
            chartData = waterQualityMonitoringDataMapper.selectWaterQualityChartData(
                    stationId, monitoringItemCode, startTime, endTime, interval);
            datasetName = getMonitoringItemName(monitoringItemCode) + "数据";

            WaterQualityChartDataResponseDTO result = new WaterQualityChartDataResponseDTO();
            result.setLabels(chartData.stream()
                    .map(item -> (String) item.get("time_label"))
                    .collect(Collectors.toList()));
            result.setValues(chartData.stream()
                    .map(item -> getBigDecimalValue(item, "avg_value"))
                    .collect(Collectors.toList()));
            result.setDatasetName(datasetName);
            result.setMonitoringItemCode(monitoringItemCode);
            result.setMonitoringItemName(getMonitoringItemName(monitoringItemCode));
            result.setUnit(getMonitoringItemUnit(monitoringItemCode));

            log.info("水质图表数据查询完成，数据点数量: {}", result.getLabels().size());
            return result;
        } catch (Exception e) {
            log.error("查询水质图表数据失败", e);
            throw new RuntimeException("查询水质图表数据失败: " + e.getMessage());
        }
    }

    /**
     * 导出水质监测数据
     *
     * @param queryDTO 查询条件
     * @return 导出数据列表
     */
    public List<WaterQualityMonitoringDataResponseDTO> exportWaterQualityData(WaterQualityMonitoringDataQueryDTO queryDTO) {
        log.info("开始导出水质监测数据，查询条件: {}", queryDTO);

        try {
            // 设置大页面大小以获取所有数据
            queryDTO.setPage(1);
            queryDTO.setSize(Integer.MAX_VALUE);

            PageResponseDTO<WaterQualityMonitoringDataResponseDTO> pagedData = getWaterQualityMonitoringDataPage(queryDTO);
            List<WaterQualityMonitoringDataResponseDTO> exportData = pagedData.getItems();

            log.info("水质监测数据导出完成，导出记录数: {}", exportData.size());
            return exportData;
        } catch (Exception e) {
            log.error("导出水质监测数据失败", e);
            throw new RuntimeException("导出水质监测数据失败: " + e.getMessage());
        }
    }

    /**
     * 导入水质监测数据
     * 支持批量导入和数据验证
     *
     * @param dataList 导入数据列表
     * @return 导入结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ImportResultDTO importWaterQualityData(List<WaterQualityDataImportDTO> dataList) {
        log.info("开始导入水质监测数据，数据量: {}", dataList.size());

        if (dataList.isEmpty()) {
            throw new IllegalArgumentException("导入数据不能为空");
        }

        List<ImportResultDTO.ImportErrorDTO> errors = new ArrayList<>();
        List<WaterQualityMonitoringData> validDataList = new ArrayList<>();
        Set<String> duplicateKeys = new HashSet<>();

        // 收集并清洗站码集合（去空格、过滤空）
        Set<String> rawStationCodes = dataList.stream()
                .map(WaterQualityDataImportDTO::getStationCode)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(code -> !code.isEmpty())
                .collect(Collectors.toSet());

        // 构建 站码 -> 站点ID 的完整映射；缺失则自动建站（名称优先用导入的stationName，否则用站码）
        Map<String, Long> stationCodeToIdMap = ensureStationCodeToIdMap(dataList, rawStationCodes);

        // 数据验证和转换
        for (int i = 0; i < dataList.size(); i++) {
            WaterQualityDataImportDTO importDTO = dataList.get(i);
            int rowNumber = i + 1;

            try {
                // 验证必填字段
                List<String> validationErrors = validateImportData(importDTO);
                if (!validationErrors.isEmpty()) {
                    errors.add(ImportResultDTO.ImportErrorDTO.builder()
                            .rowNumber(rowNumber)
                            .stationCode(importDTO.getStationCode())
                            .error(String.join("; ", validationErrors))
                            .build());
                    continue;
                }

                // 获取站点ID（按清洗后的站码取）
                String trimmedCode = importDTO.getStationCode() == null ? null : importDTO.getStationCode().trim();
                Long stationId = trimmedCode == null ? null : stationCodeToIdMap.get(trimmedCode);

                if (stationId == null) {
                    // 正常情况下不会发生（ensure已创建），如发生说明站码为空或全空白
                    errors.add(ImportResultDTO.ImportErrorDTO.builder()
                            .rowNumber(rowNumber)
                            .stationCode(importDTO.getStationCode())
                            .error("无效站码")
                            .build());
                    continue;
                }

                // 解析监测时间
                LocalDateTime monitoringTime;
                try {
                    monitoringTime = LocalDateTime.parse(importDTO.getMonitoringTime(),
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                } catch (Exception e) {
                    errors.add(ImportResultDTO.ImportErrorDTO.builder()
                            .rowNumber(rowNumber)
                            .stationCode(importDTO.getStationCode())
                            .error("监测时间格式错误：" + e.getMessage())
                            .build());
                    continue;
                }

                // 检查重复数据（水平存储模式下按站点和时间检查）
                String duplicateKey = stationId + "_" + monitoringTime;
                if (duplicateKeys.contains(duplicateKey)) {
                    errors.add(ImportResultDTO.ImportErrorDTO.builder()
                            .rowNumber(rowNumber)
                            .stationCode(importDTO.getStationCode())
                            .error("数据重复：相同站点和时间的数据已存在")
                            .build());
                    continue;
                }

                // 检查数据库中是否已存在
                if (isDataExists(stationId, monitoringTime)) {
                    errors.add(ImportResultDTO.ImportErrorDTO.builder()
                            .rowNumber(rowNumber)
                            .stationCode(importDTO.getStationCode())
                            .error("数据已存在：数据库中已有相同站点和时间的数据")
                            .build());
                    continue;
                }

                duplicateKeys.add(duplicateKey);

                // 转换为实体对象
                WaterQualityMonitoringData entity = convertImportDTOToEntity(importDTO, stationId, monitoringTime);
                validDataList.add(entity);

            } catch (Exception e) {
                log.error("处理第{}行数据时发生异常", rowNumber, e);
                errors.add(ImportResultDTO.ImportErrorDTO.builder()
                        .rowNumber(rowNumber)
                        .stationCode(importDTO.getStationCode())
                        .error("数据处理异常: " + e.getMessage())
                        .build());
            }
        }

        // 批量插入有效数据
        int successCount = 0;
        if (!validDataList.isEmpty()) {
            try {
                for (WaterQualityMonitoringData entity : validDataList) {
                    waterQualityMonitoringDataMapper.insert(entity);
                }
                successCount = validDataList.size();
                log.info("批量插入水质监测数据成功，插入数量: {}", successCount);
            } catch (Exception e) {
                log.error("批量插入水质监测数据失败", e);
                throw new RuntimeException("批量插入数据失败: " + e.getMessage());
            }
        }

        // 构建导入结果
        ImportResultDTO result = new ImportResultDTO();
        result.setTotalRows(dataList.size());
        result.setSuccessRows(successCount);
        result.setErrorRows(errors.size());
        result.setErrors(errors);

        log.info("水质监测数据导入完成，总数: {}, 成功: {}, 失败: {}",
                result.getTotalRows(), result.getSuccessRows(), result.getErrorRows());

        return result;
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 获取监测项目名称
     */
    private String getMonitoringItemName(String code) {
        if (code == null) return "";

        switch (code.toUpperCase()) {
            case "WT": return "水温";
            case "TU": return "浊度";
            case "PH": return "PH值";
            case "EC": return "电导率";
            case "DO": return "溶解氧";
            case "AN": return "氨氮";
            case "COD": return "化学需氧量";
            case "RC": return "余氯";
            default: return code;
        }
    }

    /**
     * 获取监测项目单位
     */
    private String getMonitoringItemUnit(String code) {
        if (code == null) return "";

        switch (code.toUpperCase()) {
            case "WT": return "℃";
            case "TU": return "NTU";
            case "PH": return "";
            case "EC": return "uS/cm";
            case "DO": return "mg/L";
            case "AN": return "mg/L";
            case "COD": return "mg/L";
            case "RC": return "mg/L";
            default: return "";
        }
    }

    /**
     * 验证导入数据
     */
    private List<String> validateImportData(WaterQualityDataImportDTO importDTO) {
        List<String> errors = new ArrayList<>();

        if (importDTO.getStationCode() == null || importDTO.getStationCode().trim().isEmpty()) {
            errors.add("站码不能为空");
        }

        if (importDTO.getMonitoringTime() == null || importDTO.getMonitoringTime().trim().isEmpty()) {
            errors.add("监测时间不能为空");
        }

        // 验证至少有一个监测项目数据
        boolean hasData = importDTO.getWaterTemperature() != null ||
                         importDTO.getTurbidity() != null ||
                         importDTO.getPhValue() != null ||
                         importDTO.getConductivity() != null ||
                         importDTO.getDissolvedOxygen() != null ||
                         importDTO.getAmmoniaNitrogen() != null ||
                         importDTO.getCodValue() != null ||
                         importDTO.getResidualChlorine() != null;

        if (!hasData) {
            errors.add("至少需要提供一个监测项目的数据");
        }

        // 验证监测数值不能为负数
        if (importDTO.getWaterTemperature() != null && importDTO.getWaterTemperature().compareTo(BigDecimal.ZERO) < 0) {
            errors.add("水温不能为负数");
        }
        if (importDTO.getTurbidity() != null && importDTO.getTurbidity().compareTo(BigDecimal.ZERO) < 0) {
            errors.add("浊度不能为负数");
        }
        if (importDTO.getConductivity() != null && importDTO.getConductivity().compareTo(BigDecimal.ZERO) < 0) {
            errors.add("电导率不能为负数");
        }
        if (importDTO.getDissolvedOxygen() != null && importDTO.getDissolvedOxygen().compareTo(BigDecimal.ZERO) < 0) {
            errors.add("溶解氧不能为负数");
        }
        if (importDTO.getAmmoniaNitrogen() != null && importDTO.getAmmoniaNitrogen().compareTo(BigDecimal.ZERO) < 0) {
            errors.add("氨氮不能为负数");
        }
        if (importDTO.getCodValue() != null && importDTO.getCodValue().compareTo(BigDecimal.ZERO) < 0) {
            errors.add("化学需氧量不能为负数");
        }
        if (importDTO.getResidualChlorine() != null && importDTO.getResidualChlorine().compareTo(BigDecimal.ZERO) < 0) {
            errors.add("余氯不能为负数");
        }

        if (importDTO.getDataQuality() != null &&
            !Arrays.asList(1, 2, 3).contains(importDTO.getDataQuality())) {
            errors.add("数据质量值无效，支持的值：1(正常)、2(异常)、3(缺失)");
        }

        return errors;
    }

    /**
     * 获取站码到station_id的映射
     */
    private Map<String, Long> getStationCodeToIdMap(Set<String> stationCodes) {
        if (stationCodes.isEmpty()) {
            return new HashMap<>();
        }

        // 查询监测站点信息
        List<Map<String, Object>> stations = monitoringStationMapper.selectByStationCodes(new ArrayList<>(stationCodes));

        return stations.stream()
                .collect(Collectors.toMap(
                        station -> (String) station.get("station_code"),
                        station -> ((Number) station.get("id")).longValue(),
                        (existing, replacement) -> existing
                ));
    }

    /**
     * 基于导入数据与站码集合，确保返回完整的 站码->站点ID 映射。缺失时自动按照WQ创建站点。
     * 名称优先使用导入的 stationName；为空时使用站码作为名称。
     */
    private Map<String, Long> ensureStationCodeToIdMap(List<WaterQualityDataImportDTO> dataList, Set<String> stationCodes) {
        Map<String, Long> map = new HashMap<>();
        if (stationCodes.isEmpty()) {
            return map;
        }
        // 先查已有
        List<Map<String, Object>> stations = monitoringStationMapper.selectByStationCodes(new ArrayList<>(stationCodes));
        for (Map<String, Object> s : stations) {
            String code = (String) s.get("station_code");
            Long id = ((Number) s.get("id")).longValue();
            map.put(code, id);
        }
        // 找缺失
        Set<String> missing = new HashSet<>(stationCodes);
        missing.removeAll(map.keySet());
        if (missing.isEmpty()) {
            return map;
        }
        // 构建 站码 -> 导入名称 的映射（最后一次出现优先即可）
        Map<String, String> codeToName = new HashMap<>();
        for (WaterQualityDataImportDTO dto : dataList) {
            if (dto.getStationCode() == null) continue;
            String code = dto.getStationCode().trim();
            if (code.isEmpty()) continue;
            String name = dto.getStationName();
            if (name != null) {
                name = name.trim();
            }
            codeToName.put(code, (name != null && !name.isEmpty()) ? name : code);
        }
        // 逐个创建缺失站点
        for (String code : missing) {
            String name = codeToName.getOrDefault(code, code);
            MonitoringStation station = new MonitoringStation();
            station.setStationCode(code);
            station.setName(name);
            station.setMonitoringItemCode("WQ");
            monitoringStationMapper.insert(station);
            map.put(code, station.getId());
        }
        return map;
    }

    /**
     * 检查数据是否已存在（水平存储模式下按站点和时间检查）
     */
    private boolean isDataExists(Long stationId, LocalDateTime monitoringTime) {
        return waterQualityMonitoringDataMapper.countByStationIdAndTime(stationId, monitoringTime) > 0;
    }

    /**
     * 转换导入DTO为实体对象（水平存储模式）
     */
    private WaterQualityMonitoringData convertImportDTOToEntity(WaterQualityDataImportDTO importDTO,
                                                                Long stationId, LocalDateTime monitoringTime) {
        WaterQualityMonitoringData entity = new WaterQualityMonitoringData();
        entity.setStationId(stationId);
        entity.setMonitoringTime(monitoringTime);

        // 设置8个监测项目字段
        entity.setWaterTemperature(importDTO.getWaterTemperature());
        entity.setTurbidity(importDTO.getTurbidity());
        entity.setPhValue(importDTO.getPhValue());
        entity.setConductivity(importDTO.getConductivity());
        entity.setDissolvedOxygen(importDTO.getDissolvedOxygen());
        entity.setAmmoniaNitrogen(importDTO.getAmmoniaNitrogen());
        entity.setCodValue(importDTO.getCodValue());
        entity.setResidualChlorine(importDTO.getResidualChlorine());

        entity.setDataQuality(importDTO.getDataQuality() != null ? importDTO.getDataQuality() : 1);
        entity.setCollectionMethod(importDTO.getCollectionMethod() != null ? importDTO.getCollectionMethod() : "AUTO");
        entity.setDataSource(importDTO.getDataSource());
        entity.setRemark(importDTO.getRemark());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        return entity;
    }

    /**
     * 从Map中获取BigDecimal值
     */
    private BigDecimal getBigDecimalValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return new BigDecimal(value.toString());
        }
        try {
            return new BigDecimal(value.toString());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
}
