package com.example.demo.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.dto.common.PageResponseDTO;
import com.example.demo.dto.monitoring.RainfallChartDataResponseDTO;
import com.example.demo.dto.monitoring.RainfallDataImportDTO;
import com.example.demo.dto.monitoring.RainfallMonitoringDataQueryDTO;
import com.example.demo.dto.monitoring.RainfallMonitoringDataResponseDTO;
import com.example.demo.entity.monitoring.RainfallMonitoringData;
import com.example.demo.mapper.RainfallMonitoringDataMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * 
     * @param stationId 监测站点ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param interval 时间间隔（hour/day/month）
     * @return 图表数据
     */
    public RainfallChartDataResponseDTO getRainfallChartData(Long stationId, LocalDateTime startTime, LocalDateTime endTime, String interval) {
        List<Map<String, Object>> chartData = rainfallMonitoringDataMapper.selectRainfallChartData(stationId, startTime, endTime, interval);
        
        RainfallChartDataResponseDTO responseDTO = new RainfallChartDataResponseDTO();
        List<String> labels = new ArrayList<>();
        List<BigDecimal> rainfallValues = new ArrayList<>();
        List<BigDecimal> cumulativeValues = new ArrayList<>();
        List<BigDecimal> intensityValues = new ArrayList<>();
        
        DateTimeFormatter formatter;
        if ("hour".equals(interval)) {
            formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm");
        } else if ("day".equals(interval)) {
            formatter = DateTimeFormatter.ofPattern("MM-dd");
        } else {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        }
        
        for (Map<String, Object> data : chartData) {
            LocalDateTime time = (LocalDateTime) data.get("time");
            labels.add(formatter.format(time));
            rainfallValues.add((BigDecimal) data.get("rainfall"));
            cumulativeValues.add((BigDecimal) data.get("cumulativeRainfall"));
            intensityValues.add((BigDecimal) data.get("rainfallIntensity"));
        }
        
        responseDTO.setLabels(labels);
        responseDTO.setRainfallValues(rainfallValues);
        responseDTO.setCumulativeValues(cumulativeValues);
        responseDTO.setIntensityValues(intensityValues);
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