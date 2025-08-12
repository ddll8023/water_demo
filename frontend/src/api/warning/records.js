// 鄂北地区水资源管理系统 - 预警信息处理API接口
// 基于预警模块后端API的预警信息处理相关接口

import request from "../request";

// ================================
// 预警信息记录相关接口
// ================================

/**
 * 分页查询预警记录列表
 * @param {Object} params 查询参数
 * @param {number} params.page 页码
 * @param {number} params.size 每页大小
 * @param {string} params.warningLocation 预警地点（可选）
 * @param {string} params.warningType 预警类型（可选）
 * @param {string} params.warningLevel 预警等级（可选）
 * @param {string} params.warningStatus 预警状态（可选）
 * @param {string} params.projectName 所属工程（可选）
 * @param {string} params.startTime 开始时间（可选）
 * @param {string} params.endTime 结束时间（可选）
 * @param {string} params.sort 排序字段（可选）
 * @returns {Promise} 预警记录列表响应
 */
export function getRecordList(params) {
	return request({
		url: "/warning/records",
		method: "get",
		params,
	});
}

/**
 * 创建预警记录
 * @param {Object} data 预警记录数据
 * @param {string} data.warningLocation 预警地点
 * @param {string} data.warningType 预警类型
 * @param {string} data.warningLevel 预警等级
 * @param {string} data.warningContent 预警内容
 * @param {string} data.projectName 所属工程
 * @param {string} data.occurredAt 发生时间
 * @param {number} data.thresholdId 关联预警指标ID
 * @returns {Promise} 创建结果
 */
export function createRecord(data) {
	console.log("API层验证 - 字段检查:", {
		hasWarningLocation: !!data.warningLocation,
		hasWarningType: !!data.warningType,
		hasWarningLevel: !!data.warningLevel,
		hasWarningContent: !!data.warningContent,
	});

	return request({
		url: "/warning/records",
		method: "post",
		data,
	});
}

/**
 * 解除预警
 * @param {number} id 预警记录ID
 * @param {Object} data 解除数据
 * @param {string} data.resolveRemark 解除备注
 * @returns {Promise} 解除结果
 */
export function resolveWarning(id, data) {
	return request({
		url: `/warning/records/${id}/resolve`,
		method: "put",
		data,
	});
}

/**
 * 删除预警记录
 * @param {number} id 预警记录ID
 * @returns {Promise} 删除结果
 */
export function deleteRecord(id) {
	return request({
		url: `/warning/records/${id}`,
		method: "delete",
	});
}

// ================================
// 预警信息统计相关接口
// ================================

/**
 * 获取预警统计信息
 * @param {Object} params 统计参数
 * @param {string} params.startTime 开始时间（可选）
 * @param {string} params.endTime 结束时间（可选）
 * @param {string} params.groupBy 分组方式（可选）
 * @returns {Promise} 统计信息
 */
export function getWarningStatistics(params) {
	return request({
		url: "/warning/records/statistics",
		method: "get",
		params,
	});
}

/**
 * 获取各等级预警数量统计
 * @returns {Promise} 等级统计信息
 */
export function getWarningLevelStatistics() {
	return request({
		url: "/warning/records/level-statistics",
		method: "get",
	});
}

/**
 * 获取预警趋势数据
 * @param {Object} params 趋势参数
 * @param {string} params.period 时间周期（day/week/month）
 * @param {number} params.days 天数
 * @returns {Promise} 趋势数据
 */
export function getWarningTrend(params) {
	return request({
		url: "/warning/records/trend",
		method: "get",
		params,
	});
}

// ================================
// 预警信息处理辅助接口
// ================================

/**
 * 获取预警地点列表（用于下拉选择）
 * 从现有预警记录中获取不重复的预警地点列表
 * @returns {Promise} 预警地点列表
 */
export function getWarningLocationOptions() {
	return request({
		url: "/warning/records/locations",
		method: "get",
	});
}
