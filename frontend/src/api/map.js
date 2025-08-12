import request from "./request";

/**
 * "一张图"模块API接口
 */

/**
 * 获取"一张图"模块的所有数据（概览接口）
 * @returns {Promise} 概览数据
 */
export const getMapOverview = () => {
	return request({
		url: "/map/overview",
		method: "get",
	});
};

/**
 * 获取所有水利设施地理位置信息
 * @returns {Promise} 设施位置列表
 */
export const getAllFacilityLocations = () => {
	return request({
		url: "/map/facilities",
		method: "get",
	});
};

/**
 * 获取管理体系信息（部门、人员及其负责区域）
 * @returns {Promise} 管理体系信息列表
 */
export const getManagementSystem = () => {
	return request({
		url: "/map/management-system",
		method: "get",
	});
};

/**
 * 获取所有监测站点及其最新数据
 * @returns {Promise} 监测站点信息列表
 */
export const getMonitoringStationsWithLatestData = () => {
	return request({
		url: "/map/monitoring-stations",
		method: "get",
	});
};

/**
 * 获取当前活跃的预警信息
 * @returns {Promise} 预警信息列表
 */
export const getActiveWarnings = () => {
	return request({
		url: "/map/warnings",
		method: "get",
	});
};

/**
 * 获取基础数据统计信息
 * @returns {Promise} 统计数据
 */
export const getWarningStats = () => {
	return request({
		url: "/map/stats",
		method: "get",
	});
};
