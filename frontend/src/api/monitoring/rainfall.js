import request from "../request";

/**
 * 获取降雨监测数据
 * @param {Object} params - 查询参数
 * @returns {Promise} - 返回Promise对象
 */
export const getRainfallMonitoringData = (params) => {
	return request({
		url: "/monitoring/rainfall-data",
		method: "get",
		params,
	});
};

/**
 * 获取降雨监测图表数据
 * @param {Object} params - 查询参数
 * @returns {Promise} - 返回Promise对象
 */
export const getRainfallChartData = (params) => {
	return request({
		url: "/monitoring/rainfall-chart-data",
		method: "get",
		params,
	});
};

/**
 * 导出降雨监测数据
 * @param {Object} data - 查询参数
 * @returns {Promise<Blob>} - 返回Blob对象
 */
export const exportRainfallData = (data) => {
	return request({
		url: "/monitoring/rainfall/export",
		method: "post",
		data,
		responseType: "blob",
	});
};
