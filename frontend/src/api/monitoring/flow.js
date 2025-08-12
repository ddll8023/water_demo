import request from "../request";

/**
 * 获取流量监测数据
 * @param {Object} params - 查询参数
 * @returns {Promise} - 返回Promise对象
 */
export const getFlowMonitoringData = (params) => {
	return request({
		url: "/monitoring/flow-data",
		method: "get",
		params,
	});
};

/**
 * 获取流量监测图表数据
 * @param {Object} params - 查询参数
 * @returns {Promise} - 返回Promise对象
 */
export const getFlowChartData = (params) => {
	return request({
		url: "/monitoring/flow-chart-data",
		method: "get",
		params,
	});
};

/**
 * 导出流量监测数据
 * @param {Object} data - 查询参数
 * @returns {Promise<Blob>} - 返回Blob对象
 */
export const exportFlowData = (data) => {
	return request({
		url: "/monitoring/flow-data/export",
		method: "post",
		data,
		responseType: "blob",
	});
};

/**
 * 导入流量监测数据
 * @param {Object} data - 导入数据
 * @returns {Promise} - 返回Promise对象
 */
export const importFlowData = (data) => {
	return request({
		url: "/monitoring/flow-data/import",
		method: "post",
		data,
	});
};
