package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.dto.common.PageResponseDTO;
import com.example.demo.dto.monitoring.*;
import com.example.demo.entity.monitoring.FlowMonitoringData;
import com.example.demo.mapper.FlowMonitoringDataMapper;
import com.example.demo.mapper.MonitoringStationMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 流量监测数据服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FlowMonitoringDataService extends ServiceImpl<FlowMonitoringDataMapper, FlowMonitoringData> {

    private final FlowMonitoringDataMapper flowMonitoringDataMapper;
    private final MonitoringStationMapper monitoringStationMapper;

    /**
     * 分页查询流量监测数据列表
     */
    public PageResponseDTO<FlowMonitoringDataResponseDTO> getFlowMonitoringDataPage(FlowMonitoringDataQueryDTO queryDTO) {
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
            log.warn("查询数据量过大，已限制为{}条。请求大小: {}",maxQuerySize, queryDTO.getSize());
            queryDTO.setSize(maxQuerySize);
        }

        // 使用PageHelper进行分页
        PageHelper.startPage(queryDTO.getPage(), queryDTO.getSize());
        
        // 查询数据列表
        List<FlowMonitoringDataResponseDTO> list = flowMonitoringDataMapper.selectFlowMonitoringDataPage(
                queryDTO.getStationId(),
                queryDTO.getStartTime(),
                queryDTO.getEndTime(),
                queryDTO.getDataQuality(),
                queryDTO.getCollectionMethod(),
                queryDTO.getDataSource(),
                queryDTO.getSort()
        );
        
        // 使用PageInfo包装查询结果
        PageInfo<FlowMonitoringDataResponseDTO> pageInfo = new PageInfo<>(list);

        return new PageResponseDTO<>(
                pageInfo.getList(),
                (int) pageInfo.getTotal(),
                pageInfo.getPageNum(),
                pageInfo.getPageSize()
        );
    }

    /**
     * 获取流量图表数据
     */
    public FlowChartDataResponseDTO getFlowChartData(Long stationId, LocalDateTime startTime,
                                                     LocalDateTime endTime, String interval, String dataType) {
        // 参数验证，必须提供站点ID
        if (stationId == null) {
            throw new IllegalArgumentException("必须提供监测站点ID");
        }
        
        List<Map<String, Object>> chartData;
        String datasetName;
        String dataFieldName;

        // 根据数据类型确定查询字段和数据集名称
        if ("cumulativeFlow".equals(dataType)) {
            dataFieldName = "avg_cumulative_flow";
            datasetName = "累计流量数据";
        } else {
            // 默认为瞬时流量
            dataFieldName = "avg_flow_rate";
            datasetName = "瞬时流量数据";
        }

        // 返回指定站点的数据
        chartData = flowMonitoringDataMapper.selectFlowChartData(stationId, startTime, endTime, interval, dataType);

        FlowChartDataResponseDTO result = new FlowChartDataResponseDTO();
        result.setLabels(chartData.stream()
                .map(data -> (String) data.get("time_label"))
                .collect(Collectors.toList()));
        result.setValues(chartData.stream()
                .map(data -> getBigDecimalValue(data, dataFieldName))
                .collect(Collectors.toList()));
        result.setInterval(interval);
        result.setDatasetName(datasetName);

        return result;
    }

    /**
     * 导出流量监测数据到Excel（CSV格式）
     */
    public byte[] exportToExcel(FlowMonitoringDataQueryDTO queryDTO) {
        try {
            // 使用分批处理机制导出大数据量
            final int BATCH_SIZE = 5000; // 每批次导出数量
            StringBuilder csvContent = new StringBuilder();
            
            // 添加BOM以支持中文显示
            csvContent.append("\uFEFF");

            // 创建标题行
            String[] headers = {"序号", "监测站点", "站点编码", "监测时间", "瞬时流量(m³/s)",
                              "累计流量(m³)", "数据质量", "采集方式", "数据来源", "备注"};
            csvContent.append(String.join(",", headers)).append("\n");

            // 准备日期格式化器，避免重复创建
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
            // 计算总记录数
            queryDTO.setPage(1);
            queryDTO.setSize(1);
            PageResponseDTO<FlowMonitoringDataResponseDTO> countPage = getFlowMonitoringDataPage(queryDTO);
            int totalRecords = countPage.getTotal();
            int totalPages = (totalRecords + BATCH_SIZE - 1) / BATCH_SIZE;
            
            log.info("开始导出CSV，总记录数: {}，分{}批处理", totalRecords, totalPages);
            
            // 分批获取并处理数据
            int globalRowNumber = 1;
            for (int pageNum = 1; pageNum <= totalPages; pageNum++) {
                // 设置当前批次的分页参数
                queryDTO.setPage(pageNum);
                queryDTO.setSize(BATCH_SIZE);
                
                log.info("导出CSV - 正在处理第{}/{}批", pageNum, totalPages);
                
                // 获取当前批次的数据
                PageResponseDTO<FlowMonitoringDataResponseDTO> dataPage = getFlowMonitoringDataPage(queryDTO);
                List<FlowMonitoringDataResponseDTO> dataList = dataPage.getItems();
                
                if (dataList.isEmpty()) {
                    break;
                }
                
                // 处理当前批次数据
                for (FlowMonitoringDataResponseDTO data : dataList) {
                    String[] row = {
                        String.valueOf(globalRowNumber++),
                        escapeCsvValue(data.getStationName()),
                        escapeCsvValue(data.getStationCode()),
                        data.getMonitoringTime() != null ? data.getMonitoringTime().format(dateFormatter) : "",
                        data.getFlowRate() != null ? data.getFlowRate().toString() : "0",
                        data.getCumulativeFlow() != null ? data.getCumulativeFlow().toString() : "0",
                        escapeCsvValue(data.getDataQualityText()),
                        escapeCsvValue(data.getCollectionMethod()),
                        escapeCsvValue(data.getDataSource()),
                        escapeCsvValue(data.getRemarks())
                    };
                    
                    csvContent.append(String.join(",", row)).append("\n");
                }
                
                log.info("已处理{}/{}条记录", Math.min(globalRowNumber - 1, totalRecords), totalRecords);
            }

            log.info("CSV导出完成，总记录数: {}", globalRowNumber - 1);
            
            return csvContent.toString().getBytes(StandardCharsets.UTF_8);

        } catch (Exception e) {
            log.error("导出CSV失败", e);
            throw new RuntimeException("导出数据失败: " + e.getMessage());
        }
    }

    /**
     * 导入流量监测数据
     * @param dataList 导入数据列表
     * @return 导入结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ImportResultDTO importFlowData(List<FlowDataImportDTO> dataList) {
        log.info("开始导入流量监测数据，数据量: {}", dataList.size());

        if (dataList.isEmpty()) {
            throw new IllegalArgumentException("导入数据不能为空");
        }

        // 移除导入数量限制，支持大批量数据导入
        
        // 分批处理大数据量导入，每批次大小
        final int BATCH_SIZE = 5000;
        List<ImportResultDTO.ImportErrorDTO> errors = new ArrayList<>();
        int totalSuccess = 0;
        int processedCount = 0;
        
        // 计算需要处理的批次数
        int totalBatches = (dataList.size() + BATCH_SIZE - 1) / BATCH_SIZE;
        log.info("数据量较大，将分{}批处理导入数据", totalBatches);
        
        // 分批处理导入数据
        for (int batchIndex = 0; batchIndex < totalBatches; batchIndex++) {
            int fromIndex = batchIndex * BATCH_SIZE;
            int toIndex = Math.min(fromIndex + BATCH_SIZE, dataList.size());
            List<FlowDataImportDTO> batchDataList = dataList.subList(fromIndex, toIndex);
            
            log.info("正在处理第{}批数据，共{}条 ({}/{})", batchIndex + 1, batchDataList.size(), toIndex, dataList.size());
            
            // 处理当前批次
            ImportBatchResult batchResult = processImportBatch(batchDataList);
            
            // 累计结果
            errors.addAll(batchResult.getErrors());
            totalSuccess += batchResult.getSuccessCount();
            processedCount = toIndex;
            
            // 打印进度
            log.info("第{}批处理完成，成功{}条，失败{}条，总进度: {}/{}", 
                    batchIndex + 1, batchResult.getSuccessCount(), batchResult.getErrors().size(), 
                    processedCount, dataList.size());
        }

        // 构建返回结果
        ImportResultDTO result = ImportResultDTO.builder()
                .totalRows(dataList.size())
                .successRows(totalSuccess)
                .errorRows(errors.size())
                .duplicateRows((int) errors.stream()
                        .filter(error -> error.getError().contains("重复") || error.getError().contains("已存在"))
                        .count())
                .errors(errors.size() > 100 ? errors.subList(0, 100) : errors) // 最多返回100个错误
                .build();

        log.info("流量监测数据导入完成，总数: {}, 成功: {}, 失败: {}",
                result.getTotalRows(), result.getSuccessRows(), result.getErrorRows());

        return result;
    }

    // ==================== 辅助方法 ====================

    /**
     * 转义CSV值，处理包含逗号、引号、换行符的情况
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
     * 处理单批次导入数据
     * @param batchDataList 批次数据列表
     * @return 批次处理结果
     */
    private ImportBatchResult processImportBatch(List<FlowDataImportDTO> batchDataList) {
        List<ImportResultDTO.ImportErrorDTO> errors = new ArrayList<>();
        List<FlowMonitoringData> validDataList = new ArrayList<>();
        
        // 使用BitSet替代HashSet来存储重复键，减少内存使用
        // 为简化实现，这里仍然使用HashSet，实际优化应考虑使用BloomFilter或其他数据结构
        Set<String> duplicateKeys = new HashSet<>();

        // 收集并清洗站码集合（去空格、过滤空）
        Set<String> rawStationCodes = batchDataList.stream()
                .map(FlowDataImportDTO::getStationCode)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(code -> !code.isEmpty())
                .collect(Collectors.toSet());

        // 构建 站码 -> 站点ID 的完整映射；缺失则自动建站（名称优先用导入的stationName，否则用站码）
        Map<String, Long> stationCodeToIdMap = ensureStationCodeToIdMap(batchDataList, rawStationCodes);
        
        // 预处理导入数据，收集所有有效的站点ID和监测时间组合
        Map<Long, Set<LocalDateTime>> validStationTimes = new HashMap<>();
        Map<Integer, FlowDataImportDTO> rowIndexMap = new HashMap<>();
        
        for (FlowDataImportDTO dto : batchDataList) {
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
                
                // 构建高效的重复键格式: stationId_YYYYMMDDHHMISS
                // 优化字符串连接操作，减少内存占用
                String duplicateKey = stationId + "_" + monitoringTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                if (duplicateKeys.contains(duplicateKey)) {
                    errors.add(ImportResultDTO.ImportErrorDTO.builder()
                            .rowNumber(dto.getRowNumber())
                            .stationCode(dto.getStationCode())
                            .error("数据重复（同站点同时间）")
                            .build());
                    continue;
                }
                
                // 添加到内存重复检查集合
                duplicateKeys.add(duplicateKey);
                
                // 添加到批量检查集合
                if (!validStationTimes.containsKey(stationId)) {
                    validStationTimes.put(stationId, new HashSet<>());
                }
                validStationTimes.get(stationId).add(monitoringTime);
                
                // 保存行索引，用于后续处理
                rowIndexMap.put(dto.getRowNumber(), dto);
                
            } catch (Exception e) {
                log.error("预处理导入数据时发生错误，行号: {}, 错误: {}", dto.getRowNumber(), e.getMessage());
                errors.add(ImportResultDTO.ImportErrorDTO.builder()
                        .rowNumber(dto.getRowNumber())
                        .stationCode(dto.getStationCode())
                        .error("数据处理异常: " + e.getMessage())
                        .build());
            }
        }
        
        // 批量查询数据库中已存在的记录
        Map<String, Boolean> existingRecords = new HashMap<>();
        if (!validStationTimes.isEmpty()) {
            existingRecords = batchCheckExistingRecords(validStationTimes);
        }
        
        // 处理有效数据
        for (Integer rowNumber : rowIndexMap.keySet()) {
            FlowDataImportDTO dto = rowIndexMap.get(rowNumber);
            String trimmedCode = dto.getStationCode() == null ? null : dto.getStationCode().trim();
            Long stationId = trimmedCode == null ? null : stationCodeToIdMap.get(trimmedCode);
            LocalDateTime monitoringTime = LocalDateTime.parse(dto.getMonitoringTime(), 
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            // 检查数据库中是否已存在
            // 优化重复键格式，确保与batchCheckExistingRecords方法返回的格式一致
            String existingKey = stationId + "_" + monitoringTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            if (existingRecords.getOrDefault(existingKey, false)) {
                errors.add(ImportResultDTO.ImportErrorDTO.builder()
                        .rowNumber(rowNumber)
                        .stationCode(dto.getStationCode())
                        .error("数据库中已存在相同记录")
                        .build());
                continue;
            }
            
            // 创建实体对象
            FlowMonitoringData entity = new FlowMonitoringData();
            entity.setStationId(stationId);
            entity.setMonitoringTime(monitoringTime);
            entity.setInstantFlow(dto.getInstantFlow());
            entity.setCumulativeFlow(dto.getCumulativeFlow());
            entity.setDataQuality(1); // 默认正常
            entity.setCollectionMethod("MANUAL"); // 手动导入
            entity.setDataSource("EXCEL_IMPORT"); // Excel导入
            entity.setRemark("Excel批量导入");
            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());

            validDataList.add(entity);
        }

        // 批量插入有效数据
        int successCount = 0;
        if (!validDataList.isEmpty()) {
            try {
                boolean result = saveBatch(validDataList);
                if (result) {
                    successCount = validDataList.size();
                    log.info("成功导入流量监测数据 {} 条", successCount);
                } else {
                    log.error("批量插入数据失败");
                }
            } catch (Exception e) {
                log.error("批量插入数据时发生异常", e);
                throw new RuntimeException("批量插入数据失败: " + e.getMessage());
            }
        }
        
        // 释放不再需要的集合，帮助GC回收内存
        duplicateKeys.clear();
        validStationTimes.clear();
        rowIndexMap.clear();
        
        return new ImportBatchResult(successCount, errors);
    }
    
    /**
     * 批处理结果内部类
     */
    @Getter
    private static class ImportBatchResult {
        private final int successCount;
        private final List<ImportResultDTO.ImportErrorDTO> errors;
        
        public ImportBatchResult(int successCount, List<ImportResultDTO.ImportErrorDTO> errors) {
            this.successCount = successCount;
            this.errors = errors;
        }

    }

    /**
     * 批量检查记录是否已存在于数据库
     * @param stationTimes 站点ID到时间集合的映射
     * @return 站点ID_时间 到布尔值的映射，表示记录是否存在
     */
    private Map<String, Boolean> batchCheckExistingRecords(Map<Long, Set<LocalDateTime>> stationTimes) {
        Map<String, Boolean> result = new HashMap<>();
        
        if (stationTimes.isEmpty()) {
            return result;
        }
        
        try {
            // 为了减少数据库压力，可以按站点ID分批查询
            for (Map.Entry<Long, Set<LocalDateTime>> entry : stationTimes.entrySet()) {
                Long stationId = entry.getKey();
                Set<LocalDateTime> times = entry.getValue();
                
                if (times.isEmpty()) {
                    continue;
                }
                
                // 每次最多查询500个时间点，避免SQL过长
                List<LocalDateTime> timesList = new ArrayList<>(times);
                int batchSize = 500;
                int totalBatches = (times.size() + batchSize - 1) / batchSize; // 向上取整
                
                for (int i = 0; i < totalBatches; i++) {
                    int fromIndex = i * batchSize;
                    int toIndex = Math.min(fromIndex + batchSize, timesList.size());
                    List<LocalDateTime> batchTimes = timesList.subList(fromIndex, toIndex);
                    
                    // 查询数据库中已存在的记录
                    List<Map<String, Object>> existingData = flowMonitoringDataMapper.batchCountByStationIdAndTimes(
                            stationId, batchTimes);
                    
                    // 将查询结果添加到结果映射中
                    for (Map<String, Object> data : existingData) {
                        LocalDateTime time = (LocalDateTime) data.get("monitoring_time");
                        if (time != null) {
                            String key = stationId + "_" + time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                            result.put(key, true);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("批量检查记录是否存在时发生错误", e);
        }
        
        return result;
    }

    /**
     * 基于导入数据与站码集合，确保返回完整的 站码->站点ID 映射。缺失时自动按照FL创建站点。
     * 名称优先使用导入的 stationName；为空时使用站码作为名称。
     */
    private Map<String, Long> ensureStationCodeToIdMap(List<FlowDataImportDTO> dataList, Set<String> stationCodes) {
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
        for (FlowDataImportDTO dto : dataList) {
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
            com.example.demo.entity.facility.MonitoringStation station = new com.example.demo.entity.facility.MonitoringStation();
            station.setStationCode(code);
            station.setName(name);
            station.setMonitoringItemCode("Q");
            monitoringStationMapper.insert(station);
            map.put(code, station.getId());
        }
        return map;
    }

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
