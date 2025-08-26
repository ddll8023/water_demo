package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.pojo.DTO.monitoring.WaterConditionMonitoringDataQueryDTO;
import com.example.demo.pojo.DTO.monitoring.WaterConditionMonitoringDataResponseDTO;
import com.example.demo.common.PageResult;
import com.example.demo.pojo.entity.monitoring.ReservoirMonitoringData;
import com.example.demo.mapper.WaterConditionMonitoringDataMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 水情监测数据服务
 */
@Service
public class WaterConditionMonitoringDataService extends ServiceImpl<WaterConditionMonitoringDataMapper, ReservoirMonitoringData> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 分页查询水情监测数据
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    public PageResult<WaterConditionMonitoringDataResponseDTO> pageWaterConditionMonitoringData(WaterConditionMonitoringDataQueryDTO queryDTO) {
        // 参数验证和默认值设置
        if (queryDTO.getPage() == null || queryDTO.getPage() < 1) {
            queryDTO.setPage(1);
        }
        if (queryDTO.getSize() == null || queryDTO.getSize() < 1) {
            queryDTO.setSize(10);
        }
        
        String startTime = queryDTO.getStartTime() != null ? queryDTO.getStartTime().format(DATE_TIME_FORMATTER) : null;
        String endTime = queryDTO.getEndTime() != null ? queryDTO.getEndTime().format(DATE_TIME_FORMATTER) : null;

        // 使用PageHelper设置分页
        PageHelper.startPage(queryDTO.getPage(), queryDTO.getSize());

        // 查询数据
        List<WaterConditionMonitoringDataResponseDTO> list = baseMapper.pageWaterConditionMonitoringData(
                queryDTO.getStationId(),
                queryDTO.getStationName(),
                startTime,
                endTime,
                queryDTO.getDataQuality()
        );
        
        // 使用PageInfo包装查询结果
        PageInfo<WaterConditionMonitoringDataResponseDTO> pageInfo = new PageInfo<>(list);
        
        // 返回分页数据
        return new PageResult<>(
                pageInfo.getList(),
                (int) pageInfo.getTotal(),
                queryDTO.getPage(),
                queryDTO.getSize()
        );
    }
}
