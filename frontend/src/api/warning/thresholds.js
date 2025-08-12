// 鄂北地区水资源管理系统 - 预警指标设定API接口
// 基于预警模块后端API的预警指标设定相关接口

import request from "../request";

// ================================
// 预警指标设定相关接口
// ================================

/**
 * 分页查询预警指标列表
 * @param {Object} params 查询参数
 * @param {number} params.page 页码
 * @param {number} params.size 每页大小
 * @param {string} params.stationName 测点名称（可选）
 * @param {string} params.monitoringItem 监测项（可选）
 * @param {string} params.sort 排序字段（可选）
 * @returns {Promise} 预警指标列表响应
 */
export function getThresholdList(params) {
	return request({
		url: "/warning/thresholds",
		method: "get",
		params,
	});
}

/**
 * 查询预警指标详情
 * @param {number} id 预警指标ID
 * @returns {Promise} 预警指标详情
 */
export function getThresholdDetail(id) {
	return request({
		url: `/warning/thresholds/${id}`,
		method: "get",
	});
}

/**
 * 创建预警指标
 * @param {Object} data 预警指标数据
 * @param {string} data.stationName 测点名称
 * @param {string} data.monitoringItem 监测项
 * @param {number} data.upperUpperLimit 上上限
 * @param {number} data.upperLimit 上限
 * @param {number} data.lowerLimit 下限
 * @param {number} data.lowerLowerLimit 下下限
 * @param {string} data.unit 单位
 * @returns {Promise} 创建结果
 */
export function createThreshold(data) {
	return request({
		url: "/warning/thresholds",
		method: "post",
		data,
	});
}

/**
 * 更新预警指标
 * @param {number} id 预警指标ID
 * @param {Object} data 预警指标数据
 * @returns {Promise} 更新结果
 */
export function updateThreshold(id, data) {
	return request({
		url: `/warning/thresholds/${id}`,
		method: "put",
		data: { ...data, id },
	});
}

/**
 * 删除预警指标
 * @param {number} id 预警指标ID
 * @returns {Promise} 删除结果
 */
export function deleteThreshold(id) {
	return request({
		url: `/warning/thresholds/${id}`,
		method: "delete",
	});
}

/**
 * 检查预警指标重复
 * @param {string} stationName 测点名称
 * @param {string} monitoringItem 监测项
 * @param {number} excludeId 排除的ID（可选）
 * @returns {Promise} 检查结果
 */
export function checkThresholdDuplicate(
	stationName,
	monitoringItem,
	excludeId
) {
	return request({
		url: "/warning/thresholds/check-duplicate",
		method: "get",
		params: { stationName, monitoringItem, excludeId },
	});
}

/**
 * 获取所有预警指标
 * @returns {Promise} 预警指标列表
 */
export function getActiveThresholds() {
	return request({
		url: "/warning/thresholds/active",
		method: "get",
	});
}

// ================================
// 预警指标设定辅助接口
// ================================

/**
 * 获取预警指标统计信息
 * @returns {Promise} 统计信息
 */
export function getThresholdStatistics() {
	return request({
		url: "/warning/thresholds/statistics",
		method: "get",
	});
}
