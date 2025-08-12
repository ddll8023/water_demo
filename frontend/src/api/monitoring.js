// 导入各类监测数据的API接口
import request from "./request";
import {
	getWaterConditionMonitoringData,
	getWaterConditionChartData,
	exportWaterConditionData,
} from "./monitoring/waterCondition";

/**
 * 获取可用的监测站点
 * @param {Object} params - 查询参数
 * @returns {Promise} - 返回Promise对象
 */
export const getAvailableMonitoringStations = (params) => {
	return request({
		url: "/engineering-service/monitoring-stations/available",
		method: "get",
		params,
	}).then((resp) => resp?.data || resp);
};

// 导出流量监测相关API
export {
	getFlowMonitoringData,
	getFlowChartData,
	exportFlowData,
	importFlowData,
} from "./monitoring/flow";

// 导出水位监测相关API
export {
	getWaterLevelMonitoringData,
	getWaterLevelChartData,
	exportWaterLevelData,
	importWaterLevelData,
} from "./monitoring/waterLevel";

// 导出水质监测相关API
export {
	getWaterQualityMonitoringData,
	getWaterQualityChartData,
	exportWaterQualityData,
	importWaterQualityData,
} from "./monitoring/waterQuality";

// 导出降雨监测相关API
export {
	getRainfallMonitoringData,
	getRainfallChartData,
	exportRainfallData,
} from "./monitoring/rainfall";

// 导出水情监测相关API
export {
	getWaterConditionMonitoringData,
	getWaterConditionChartData,
	exportWaterConditionData,
};
