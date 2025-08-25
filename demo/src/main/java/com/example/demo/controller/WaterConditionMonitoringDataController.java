package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.pojo.DTO.common.PageResponseDTO;
import com.example.demo.pojo.DTO.monitoring.WaterConditionMonitoringDataQueryDTO;
import com.example.demo.pojo.DTO.monitoring.WaterConditionMonitoringDataResponseDTO;
import com.example.demo.service.WaterConditionMonitoringDataService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 水情监测数据控制器
 * <p>
 * 提供水情监测数据的查询接口，支持分页查询水情数据
 * 可通过不同参数筛选查询结果
 * </p>
 * 
 * @author system
 * @since 1.0
 */
@RestController
@RequestMapping("/api/monitoring/water-condition")
@Api(tags = "水情监测数据管理", description = "水情监测数据的CRUD操作及相关统计功能")
public class WaterConditionMonitoringDataController {

    /**
     * 水情监测数据服务
     * 通过构造函数注入，负责处理水情监测数据的业务逻辑
     */
    @Autowired
    private WaterConditionMonitoringDataService waterConditionMonitoringDataService;

    /**
     * 分页查询水情监测数据
     * <p>
     * 根据查询条件分页获取水情监测数据，支持按时间范围、监测站点等条件过滤
     * </p>
     * 
     * @param queryDTO 查询参数对象，包含分页信息和过滤条件
     * @return 包含分页数据的API统一响应
     */
    @GetMapping
    @ApiOperation(value = "分页查询水情监测数据", notes = "根据条件分页获取水情监测数据")
    public ApiResponse<PageResponseDTO<WaterConditionMonitoringDataResponseDTO>> pageWaterConditionMonitoringData(WaterConditionMonitoringDataQueryDTO queryDTO) {
        PageResponseDTO<WaterConditionMonitoringDataResponseDTO> page = waterConditionMonitoringDataService.pageWaterConditionMonitoringData(queryDTO);
        return ApiResponse.success("查询成功", page);
    }
}
