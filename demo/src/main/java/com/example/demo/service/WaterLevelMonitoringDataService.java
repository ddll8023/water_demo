package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.common.PageResult;
import com.example.demo.pojo.DTO.monitoring.*;
import com.example.demo.pojo.entity.facility.MonitoringStation;
import com.example.demo.pojo.entity.monitoring.WaterLevelMonitoringData;
import com.example.demo.mapper.WaterLevelMonitoringDataMapper;
import com.example.demo.mapper.MonitoringStationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/**
 * 水位监测数据服务实现类
 * 
 * 设计说明：
 * - 严格参考FlowMonitoringDataService的实现规范
 * - 支持水位监测数据的完整CRUD操作和业务逻辑
 * - 提供分页查询、统计分析、图表数据、导入导出等功能
 * - 使用统一的异常处理和参数验证机制
 * - 遵循现有的代码风格和命名规范
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WaterLevelMonitoringDataService extends ServiceImpl<WaterLevelMonitoringDataMapper, WaterLevelMonitoringData> {

    private final WaterLevelMonitoringDataMapper waterLevelMonitoringDataMapper;
    private final MonitoringStationMapper monitoringStationMapper;

    /**
     * 分页查询水位监测数据列表
     * 
     * @param queryDTO 查询条件DTO
     * @return 分页查询结果
     */
    public PageResult<WaterLevelMonitoringDataResponseDTO> getWaterLevelMonitoringDataPage(WaterLevelMonitoringDataQueryDTO queryDTO) {
        // 参数验证和安全检查
        validateQueryParameters(queryDTO);

        // 设置默认分页参数
        if (queryDTO.getPage() == null || queryDTO.getPage() < 1) {
            queryDTO.setPage(1);
        }
        if (queryDTO.getSize() == null || queryDTO.getSize() < 1) {
            queryDTO.setSize(10);
        }

        // 防止大数据量查询影响性能
        int maxQuerySize = 100000000;
        if (queryDTO.getSize() > maxQuerySize) {
            log.warn("查询数据量过大，已限制为{}条。请求大小: {}", maxQuerySize, queryDTO.getSize());
            queryDTO.setSize(maxQuerySize);
        }

        // 使用PageHelper设置分页
        PageHelper.startPage(queryDTO.getPage(), queryDTO.getSize());

        // 执行查询
        List<WaterLevelMonitoringDataResponseDTO> list = waterLevelMonitoringDataMapper.selectWaterLevelMonitoringDataPage(
                queryDTO.getStationId(),
                queryDTO.getStartTime(),
                queryDTO.getEndTime(),
                queryDTO.getDataQuality(),
                queryDTO.getCollectionMethod(),
                queryDTO.getDataSource(),
                queryDTO.getSort()
        );

        // 使用PageInfo包装查询结果
        PageInfo<WaterLevelMonitoringDataResponseDTO> pageInfo = new PageInfo<>(list);

        // 返回分页数据
        return new PageResult<>(
                pageInfo.getList(),
                (int) pageInfo.getTotal(),
                queryDTO.getPage(),
                queryDTO.getSize()
        );
    }

    /**
     * 获取水位图表数据
     * 
     * @param stationId 监测站点ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param interval 时间间隔
     * @return 图表数据响应DTO
     */
    public WaterLevelChartDataResponseDTO getWaterLevelChartData(Long stationId, LocalDateTime startTime,
                                                                 LocalDateTime endTime, String interval) {
        List<Map<String, Object>> chartData;
        String datasetName;

        if (stationId == null) {
            // 当没有指定站点ID时，返回所有站点的汇总数据
            chartData = waterLevelMonitoringDataMapper.selectAllStationsWaterLevelChartData(startTime, endTime, interval);
            datasetName = "所有站点水位数据";
        } else {
            // 返回指定站点的数据
            chartData = waterLevelMonitoringDataMapper.selectWaterLevelChartData(stationId, startTime, endTime, interval);
            datasetName = "水位数据";
        }

        WaterLevelChartDataResponseDTO result = new WaterLevelChartDataResponseDTO();
        result.setLabels(chartData.stream()
                .map(data -> (String) data.get("time_label"))
                .collect(Collectors.toList()));
        result.setValues(chartData.stream()
                .map(data -> getBigDecimalValue(data, "avg_water_level"))
                .collect(Collectors.toList()));
        result.setInterval(interval != null ? interval : "hour");
        result.setDatasetName(datasetName);

        // 计算统计信息
        List<BigDecimal> values = result.getValues();
        if (!values.isEmpty()) {
            result.setTotalCount(values.size());
            result.setMaxValue(values.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO));
            result.setMinValue(values.stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO));
            
            BigDecimal sum = values.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            result.setAvgValue(sum.divide(new BigDecimal(values.size()), 3, BigDecimal.ROUND_HALF_UP));
        }

        return result;
    }

    /**
     * 导出水位监测数据到Excel（CSV格式）
     *
     * @param queryDTO 查询条件DTO
     * @return CSV格式的字节数组
     */
    public byte[] exportToExcel(WaterLevelMonitoringDataQueryDTO queryDTO) {
        try {
            // 设置导出专用的分页参数，避免内存溢出
            queryDTO.setPage(1);
            if (queryDTO.getSize() == null || queryDTO.getSize() > 10000) {
                queryDTO.setSize(10000);
            }

            // 获取要导出的数据
            PageResult<WaterLevelMonitoringDataResponseDTO> dataPage = getWaterLevelMonitoringDataPage(queryDTO);
            List<WaterLevelMonitoringDataResponseDTO> dataList = dataPage.getItems();

            // 创建CSV内容
            StringBuilder csvContent = new StringBuilder();

            // 添加BOM以支持中文显示
            csvContent.append("\uFEFF");

            // 创建标题行
            String[] headers = {"序号", "监测站点", "站点编码", "监测时间", "水位高度(m)",
                              "数据质量", "采集方式", "数据来源", "备注"};
            csvContent.append(String.join(",", headers)).append("\n");

            // 创建数据行
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (int i = 0; i < dataList.size(); i++) {
                WaterLevelMonitoringDataResponseDTO data = dataList.get(i);

                String[] row = {
                    String.valueOf(i + 1),
                    escapeCsvValue(data.getStationName()),
                    escapeCsvValue(data.getStationCode()),
                    data.getMonitoringTime() != null ? data.getMonitoringTime().format(dateFormatter) : "",
                    data.getWaterLevel() != null ? data.getWaterLevel().toString() : "0",
                    escapeCsvValue(data.getDataQualityText()),
                    escapeCsvValue(data.getCollectionMethod()),
                    escapeCsvValue(data.getDataSource()),
                    escapeCsvValue(data.getRemark())
                };

                csvContent.append(String.join(",", row)).append("\n");
            }

            return csvContent.toString().getBytes(StandardCharsets.UTF_8);

        } catch (Exception e) {
            log.error("导出CSV失败", e);
            throw new RuntimeException("导出数据失败: " + e.getMessage());
        }
    }

    /**
     * 转义CSV值，处理包含逗号、引号、换行符的情况
     *
     * @param value 原始值
     * @return 转义后的值
     */
    private String escapeCsvValue(String value) {
        if (value == null) {
            return "";
        }

        // 如果包含逗号、引号或换行符，需要用引号包围并转义内部引号
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }

        return value;
    }

    /**
     * 导入水位监测数据
     *
     * @param dataList 导入数据列表
     * @return 导入结果DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public ImportResultDTO importWaterLevelData(List<WaterLevelDataImportDTO> dataList) {
        log.info("开始导入水位监测数据，数据量: {}", dataList.size());

        if (dataList.isEmpty()) {
            throw new IllegalArgumentException("导入数据不能为空");
        }

        List<ImportResultDTO.ImportErrorDTO> errors = new ArrayList<>();
        List<WaterLevelMonitoringData> validDataList = new ArrayList<>();
        Set<String> duplicateKeys = new HashSet<>();

        // 收集并清洗站码集合（去空格、过滤空）
        Set<String> rawStationCodes = dataList.stream()
                .map(WaterLevelDataImportDTO::getStationCode)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(code -> !code.isEmpty())
                .collect(Collectors.toSet());

        // 构建 站码 -> 站点ID 的完整映射；缺失则自动建站（名称优先用导入的stationName，否则用站码）
        Map<String, Long> stationCodeToIdMap = ensureStationCodeToIdMap(dataList, rawStationCodes);

        // 验证和转换数据
        for (WaterLevelDataImportDTO dto : dataList) {
            try {
                // 获取站点ID（按清洗后的站码取）
                String trimmedCode = dto.getStationCode() == null ? null : dto.getStationCode().trim();
                Long stationId = trimmedCode == null ? null : stationCodeToIdMap.get(trimmedCode);

                if (stationId == null) {
                    // 正常情况下不会发生（ensure已创建），如发生说明站码为空或全空白
                    errors.add(ImportResultDTO.ImportErrorDTO.builder()
                            .rowNumber(dto.getRowNumber())
                            .stationCode(dto.getStationCode())
                            .error("无效站码")
                            .build());
                    continue;
                }

                // 解析监测时间
                LocalDateTime monitoringTime;
                try {
                    monitoringTime = LocalDateTime.parse(dto.getMonitoringTime(),
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                } catch (Exception e) {
                    errors.add(ImportResultDTO.ImportErrorDTO.builder()
                            .rowNumber(dto.getRowNumber())
                            .stationCode(dto.getStationCode())
                            .error("监测时间格式错误")
                            .build());
                    continue;
                }

                // 检查重复性（同站点同时间）
                String duplicateKey = stationId + "_" + dto.getMonitoringTime();
                if (duplicateKeys.contains(duplicateKey)) {
                    errors.add(ImportResultDTO.ImportErrorDTO.builder()
                            .rowNumber(dto.getRowNumber())
                            .stationCode(dto.getStationCode())
                            .error("数据重复（同站点同时间）")
                            .build());
                    continue;
                }

                // 检查数据库中是否已存在
                if (isDataExists(stationId, monitoringTime)) {
                    errors.add(ImportResultDTO.ImportErrorDTO.builder()
                            .rowNumber(dto.getRowNumber())
                            .stationCode(dto.getStationCode())
                            .error("数据库中已存在相同记录")
                            .build());
                    continue;
                }

                duplicateKeys.add(duplicateKey);

                // 创建实体对象
                WaterLevelMonitoringData entity = new WaterLevelMonitoringData();
                entity.setStationId(stationId);
                entity.setMonitoringTime(monitoringTime);
                entity.setWaterLevel(dto.getWaterLevel());
                entity.setDataQuality(dto.getDataQuality() != null ? dto.getDataQuality() : 1); // 默认正常
                entity.setCollectionMethod(dto.getCollectionMethod() != null ? dto.getCollectionMethod() : "MANUAL"); // 手动导入
                entity.setDataSource(dto.getDataSource() != null ? dto.getDataSource() : "EXCEL_IMPORT"); // Excel导入
                entity.setRemark(dto.getRemark() != null ? dto.getRemark() : "Excel批量导入");
                entity.setCreatedAt(LocalDateTime.now());
                entity.setUpdatedAt(LocalDateTime.now());

                validDataList.add(entity);

            } catch (Exception e) {
                log.error("处理导入数据时发生错误，行号: {}, 错误: {}", dto.getRowNumber(), e.getMessage());
                errors.add(ImportResultDTO.ImportErrorDTO.builder()
                        .rowNumber(dto.getRowNumber())
                        .stationCode(dto.getStationCode())
                        .error("数据处理异常: " + e.getMessage())
                        .build());
            }
        }

        // 批量插入有效数据
        int successCount = 0;
        if (!validDataList.isEmpty()) {
            try {
                boolean result = saveBatch(validDataList);
                if (result) {
                    successCount = validDataList.size();
                    log.info("成功导入水位监测数据 {} 条", successCount);
                } else {
                    log.error("批量插入数据失败");
                    throw new RuntimeException("批量插入数据失败");
                }
            } catch (Exception e) {
                log.error("批量插入数据时发生异常", e);
                throw new RuntimeException("批量插入数据失败: " + e.getMessage());
            }
        }

        // 构建返回结果
        ImportResultDTO result = ImportResultDTO.builder()
                .totalRows(dataList.size())
                .successRows(successCount)
                .errorRows(errors.size())
                .duplicateRows((int) errors.stream()
                        .filter(error -> error.getError().contains("重复") || error.getError().contains("已存在"))
                        .count())
                .errors(errors.size() > 100 ? errors.subList(0, 100) : errors) // 最多返回100个错误
                .build();

        log.info("水位监测数据导入完成，总数: {}, 成功: {}, 失败: {}",
                result.getTotalRows(), result.getSuccessRows(), result.getErrorRows());

        return result;
    }

    /**
     * 基于导入数据与站码集合，确保返回完整的 站码->站点ID 映射。缺失时自动按照WL创建站点。
     * 名称优先使用导入的 stationName；为空时使用站码作为名称。
     */
    private Map<String, Long> ensureStationCodeToIdMap(List<WaterLevelDataImportDTO> dataList, Set<String> stationCodes) {
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
        for (WaterLevelDataImportDTO dto : dataList) {
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
            station.setMonitoringItemCode("H");
            monitoringStationMapper.insert(station);
            map.put(code, station.getId());
        }
        return map;
    }

    /**
     * 检查数据是否已存在
     *
     * @param stationId 监测站点ID
     * @param monitoringTime 监测时间
     * @return 是否存在
     */
    private boolean isDataExists(Long stationId, LocalDateTime monitoringTime) {
        return waterLevelMonitoringDataMapper.countByStationIdAndTime(stationId, monitoringTime) > 0;
    }

    /**
     * 验证查询参数的合理性
     *
     * @param queryDTO 查询参数DTO
     */
    private void validateQueryParameters(WaterLevelMonitoringDataQueryDTO queryDTO) {
        if (queryDTO == null) {
            throw new IllegalArgumentException("查询参数不能为空");
        }

        // 验证时间范围
        if (queryDTO.getStartTime() != null && queryDTO.getEndTime() != null) {
            if (queryDTO.getStartTime().isAfter(queryDTO.getEndTime())) {
                throw new IllegalArgumentException("开始时间不能晚于结束时间");
            }

            // 限制查询时间范围，防止查询过大数据量
            long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(
                queryDTO.getStartTime().toLocalDate(),
                queryDTO.getEndTime().toLocalDate());

            if (daysBetween > 365) {
                throw new IllegalArgumentException("查询时间范围不能超过365天");
            }
        }

        // 验证数据质量参数
        if (queryDTO.getDataQuality() != null) {
            if (queryDTO.getDataQuality() < 1 || queryDTO.getDataQuality() > 3) {
                throw new IllegalArgumentException("数据质量参数无效，有效值为1-3");
            }
        }

        // 验证采集方式
        if (queryDTO.getCollectionMethod() != null) {
            String method = queryDTO.getCollectionMethod().toUpperCase();
            if (!"AUTO".equals(method) && !"MANUAL".equals(method)) {
                throw new IllegalArgumentException("采集方式参数无效，有效值为AUTO或MANUAL");
            }
        }

        // 验证排序参数
        if (queryDTO.getSort() != null && !queryDTO.getSort().trim().isEmpty()) {
            String sort = queryDTO.getSort().toLowerCase();
            if (!sort.matches("^[a-z_]+,(asc|desc)$")) {
                throw new IllegalArgumentException("排序参数格式无效，正确格式为：字段名,asc或字段名,desc");
            }
        }
    }

    // ========== 辅助方法 ==========

    /**
     * 从Map中获取BigDecimal值
     *
     * @param map 数据Map
     * @param key 键名
     * @return BigDecimal值，如果不存在或无法转换则返回BigDecimal.ZERO
     */
    private BigDecimal getBigDecimalValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return new BigDecimal(value.toString());
        }
        return BigDecimal.ZERO;
    }
}
