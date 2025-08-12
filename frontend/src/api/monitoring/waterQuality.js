import request from "../request";

/**
 * 获取水质监测数据
 * @param {Object} params - 查询参数
 * @returns {Promise} - 返回Promise对象
 */
export const getWaterQualityMonitoringData = (params) => {
	return request({
		url: "/monitoring/water-quality-data",
		method: "get",
		params,
	});
};

/**
 * 获取水质监测图表数据
 * @param {Object} params - 查询参数
 * @returns {Promise} - 返回Promise对象
 */
export const getWaterQualityChartData = (params) => {
	return request({
		url: "/monitoring/water-quality-chart-data",
		method: "get",
		params,
	});
};

/**
 * 导出水质监测数据
 * @param {Object} data - 查询参数
 * @returns {Promise<Blob>} - 返回Blob对象
 */
export const exportWaterQualityData = (data) => {
	return request({
		url: "/monitoring/water-quality/export",
		method: "post",
		data,
		responseType: "blob",
	});
};

/**
 * 导入水质监测数据
 * @param {Object} data - 导入数据
 * @returns {Promise} - 返回Promise对象
 */
export const importWaterQualityData = (data) => {
	return request({
		url: "/monitoring/water-quality/import",
		method: "post",
		data,
	});
};
