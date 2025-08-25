package com.example.demo.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.pojo.DTO.common.PageResponseDTO;
import com.example.demo.pojo.DTO.monitoring.RainfallChartDataResponseDTO;
import com.example.demo.pojo.DTO.monitoring.RainfallDataImportDTO;
import com.example.demo.pojo.DTO.monitoring.RainfallMonitoringDataQueryDTO;
import com.example.demo.pojo.DTO.monitoring.RainfallMonitoringDataResponseDTO;
import com.example.demo.pojo.entity.monitoring.RainfallMonitoringData;
import com.example.demo.mapper.RainfallMonitoringDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 雨情监测数据服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RainfallMonitoringDataService extends ServiceImpl<RainfallMonitoringDataMapper, RainfallMonitoringData> {

    private final RainfallMonitoringDataMapper rainfallMonitoringDataMapper;
    
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 分页查询雨情监测数据列表
     * 
     * @param queryDTO 查询条件DTO
     * @return 分页查询结果
     */
    public PageResponseDTO<RainfallMonitoringDataResponseDTO> getRainfallMonitoringDataPage(RainfallMonitoringDataQueryDTO queryDTO) {
        // 参数验证和默认值设置
        int page = queryDTO.getPage() != null ? queryDTO.getPage() : 1;
        int size = queryDTO.getSize() != null ? queryDTO.getSize() : 10;
        
        // 使用PageHelper开始分页
        PageHelper.startPage(page, size);
        
        // 查询数据，不需要传入分页参数
        List<RainfallMonitoringDataResponseDTO> dataList = rainfallMonitoringDataMapper.selectRainfallMonitoringDataPage(
            queryDTO.getStationId(),
            queryDTO.getStartTime(),
            queryDTO.getEndTime(),
            queryDTO.getDataQuality(),
            queryDTO.getCollectionMethod(),
            queryDTO.getDataSource(),
            queryDTO.getSort()
        );
        
        // 获取分页信息
        PageInfo<RainfallMonitoringDataResponseDTO> pageInfo = new PageInfo<>(dataList);
        
        // 转换为统一的分页响应格式
        return new PageResponseDTO<>(
            pageInfo.getList(),
            (int) pageInfo.getTotal(),
            pageInfo.getPageNum(),
            pageInfo.getPageSize()
        );
    }

    /**
     * 获取雨情图表数据
     * 支持数据类型区分（时段雨量/累计雨量）
     *
     * @param stationId 监测站点ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param interval 时间间隔（hour/day/month）
     * @param dataType 数据类型(rainfall:时段雨量,cumulativeRainfall:累计雨量)
     * @return 图表数据
     */
    public RainfallChartDataResponseDTO getRainfallChartData(Long stationId, LocalDateTime startTime, LocalDateTime endTime, String interval, String dataType) {
        List<Map<String, Object>> chartData = rainfallMonitoringDataMapper.selectRainfallChartData(stationId, startTime, endTime, interval);

        RainfallChartDataResponseDTO responseDTO = new RainfallChartDataResponseDTO();
        List<String> labels = new ArrayList<>();
        List<BigDecimal> values = new ArrayList<>();

        String datasetName;
        String dataFieldName;

        // 根据数据类型确定查询字段和数据集名称
        if ("cumulativeRainfall".equals(dataType)) {
            dataFieldName = "max_cumulative_rainfall";
            datasetName = "累计雨量数据";
        } else {
            // 默认为时段雨量
            dataFieldName = "avg_rainfall";
            datasetName = "时段雨量数据";
        }

        DateTimeFormatter formatter;
        if ("hour".equals(interval)) {
            formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm");
        } else if ("day".equals(interval)) {
            formatter = DateTimeFormatter.ofPattern("MM-dd");
        } else {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        }

        for (Map<String, Object> data : chartData) {
            // 处理时间标签
            Object timeObj = data.get("time_label");
            String timeLabel;
            if (timeObj instanceof String) {
                timeLabel = (String) timeObj;
            } else if (timeObj instanceof LocalDateTime) {
                timeLabel = formatter.format((LocalDateTime) timeObj);
            } else {
                continue; // 跳过无效数据
            }

            labels.add(timeLabel);

            // 根据数据类型获取对应的值
            BigDecimal value = (BigDecimal) data.get(dataFieldName);
            values.add(value != null ? value : BigDecimal.ZERO);
        }

        responseDTO.setLabels(labels);
        responseDTO.setValues(values);
        responseDTO.setDatasetName(datasetName);
        responseDTO.setInterval(interval);
        
        // 获取站点名称 - 这里需要在Mapper中添加方法
        if (stationId != null) {
            // 由于Mapper中没有这个方法，我们可以通过其他方式获取站点名称
            // 或者在Mapper中添加这个方法
            responseDTO.setStationName("雨情监测站点");
            responseDTO.setDatasetName("雨情监测数据");
        }
        
        return responseDTO;
    }



    /**
     * 导出雨情监测数据到Excel（CSV格式）
     *
     * @param queryDTO 查询条件DTO
     * @return CSV格式的字节数组
     */
    public byte[] exportToExcel(RainfallMonitoringDataQueryDTO queryDTO) {
        try {
            // 设置导出专用的分页参数，避免内存溢出
            queryDTO.setPage(1);
            if (queryDTO.getSize() == null || queryDTO.getSize() > 10000) {
                queryDTO.setSize(10000);
            }

            // 获取要导出的数据
            PageResponseDTO<RainfallMonitoringDataResponseDTO> dataPage = getRainfallMonitoringDataPage(queryDTO);
            List<RainfallMonitoringDataResponseDTO> dataList = dataPage.getItems();

            // 创建CSV内容
            StringBuilder csvContent = new StringBuilder();

            // 添加BOM以支持中文显示
            csvContent.append("\uFEFF");

            // 创建标题行
            String[] headers = {"序号", "监测站点", "站点编码", "监测时间", "时段雨量(mm)",
                              "累计雨量(mm)", "数据质量", "采集方式", "数据来源", "备注"};
            csvContent.append(String.join(",", headers)).append("\n");

            // 处理数据行
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            int rowNumber = 1;

            for (RainfallMonitoringDataResponseDTO data : dataList) {
                String[] row = {
                    String.valueOf(rowNumber++),
                    escapeCsvValue(data.getStationName()),
                    escapeCsvValue(data.getStationCode()),
                    data.getMonitoringTime() != null ? data.getMonitoringTime().format(dateFormatter) : "",
                    data.getRainfall() != null ? data.getRainfall().toString() : "0",
                    data.getCumulativeRainfall() != null ? data.getCumulativeRainfall().toString() : "0",
                    escapeCsvValue(data.getDataQualityText()),
                    escapeCsvValue(data.getCollectionMethod()),
                    escapeCsvValue(data.getDataSource()),
                    escapeCsvValue(data.getRemark())
                };

                csvContent.append(String.join(",", row)).append("\n");
            }

            log.info("CSV导出完成，总记录数: {}", dataList.size());

            return csvContent.toString().getBytes(StandardCharsets.UTF_8);

        } catch (Exception e) {
            log.error("导出CSV失败", e);
            throw new RuntimeException("导出数据失败: " + e.getMessage());
        }
    }

    /**
     * CSV值转义处理
     *
     * @param value 原始值
     * @return 转义后的值
     */
    private String escapeCsvValue(String value) {
        if (value == null) {
            return "";
        }

        // 如果包含逗号、双引号或换行符，需要用双引号包围，并转义内部的双引号
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }

        return value;
    }

    /**
     * 导入雨情监测数据
     *
     * @param dataList 导入数据列表
     * @param stationId 监测站点ID
     * @return 导入结果
     */
    @Transactional
    public Map<String, Object> importRainfallData(List<RainfallDataImportDTO> dataList, Long stationId) {
        Map<String, Object> result = new HashMap<>();
        int successCount = 0;
        int failCount = 0;
        List<String> errorMessages = new ArrayList<>();
        
        for (RainfallDataImportDTO dto : dataList) {
            try {
                RainfallMonitoringData data = new RainfallMonitoringData();
                data.setStationId(stationId);
                
                // 将字符串时间转换为LocalDateTime
                try {
                    LocalDateTime monitoringTime = LocalDateTime.parse(dto.getMonitoringTime(), DATE_TIME_FORMATTER);
                    data.setMonitoringTime(monitoringTime);
                } catch (DateTimeParseException e) {
                    throw new RuntimeException("监测时间格式错误: " + dto.getMonitoringTime());
                }
                
                data.setRainfall(dto.getRainfall());
                data.setRainfallIntensity(dto.getRainfallIntensity());
                data.setCumulativeRainfall(dto.getCumulativeRainfall());
                data.setDataQuality(1); // 默认为正常数据
                data.setCollectionMethod("MANUAL");
                data.setDataSource("IMPORT");
                data.setRemark("批量导入");
                
                int insertResult = rainfallMonitoringDataMapper.insert(data);
                if (insertResult > 0) {
                    successCount++;
                } else {
                    failCount++;
                    errorMessages.add("第" + dto.getRowNumber() + "行：插入失败");
                }
            } catch (Exception e) {
                failCount++;
                errorMessages.add("第" + dto.getRowNumber() + "行：" + e.getMessage());
            }
        }
        
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        result.put("errorMessages", errorMessages);
        
        return result;
    }

} 