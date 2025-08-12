import request from "@/api/request";

/**
 * 获取水情监测数据
 * @param {Object} params - 查询参数
 * @returns {Promise} - 返回Promise对象
 */
export const getWaterConditionMonitoringData = (params) => {
	return request({
		url: "/monitoring/water-condition",
		method: "get",
		params,
	});
};

/**
 * 获取水情监测图表数据
 * @param {Object} params - 查询参数
 * @returns {Promise} - 返回Promise对象
 */
export const getWaterConditionChartData = (params) => {
	return request({
		url: "/monitoring/water-condition/chart",
		method: "get",
		params,
	});
};

/**
 * 导出水情监测数据
 * @param {Object} data - 查询参数
 * @returns {Promise<Blob>} - 返回Blob对象
 */
export const exportWaterConditionData = (data) => {
	return request({
		url: "/monitoring/water-condition/export",
		method: "post",
		data,
		responseType: "blob",
	});
};
