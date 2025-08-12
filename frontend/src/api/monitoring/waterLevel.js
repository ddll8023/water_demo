import request from "../request";

/**
 * 获取水位监测数据
 * @param {Object} params - 查询参数
 * @returns {Promise} - 返回Promise对象
 */
export const getWaterLevelMonitoringData = (params) => {
	return request({
		url: "/monitoring/water-level-data",
		method: "get",
		params,
	});
};

/**
 * 获取水位监测图表数据
 * @param {Object} params - 查询参数
 * @returns {Promise} - 返回Promise对象
 */
export const getWaterLevelChartData = (params) => {
	return request({
		url: "/monitoring/water-level-chart-data",
		method: "get",
		params,
	});
};

/**
 * 导出水位监测数据
 * @param {Object} data - 查询参数
 * @returns {Promise<Blob>} - 返回Blob对象
 */
export const exportWaterLevelData = (data) => {
	return request({
		url: "/monitoring/water-level-data/export",
		method: "post",
		data,
		responseType: "blob",
	});
};

/**
 * 导入水位监测数据
 * @param {Object} data - 导入数据
 * @returns {Promise} - 返回Promise对象
 */
export const importWaterLevelData = (data) => {
	return request({
		url: "/monitoring/water-level-data/import",
		method: "post",
		data,
	});
};
