// 鄂北地区水资源管理系统 - 岗位管理API接口
// 基于阶段一后端API的岗位管理相关接口

import request from "./request";

/**
 * 分页查询岗位列表
 * @param {Object} params 查询参数
 * @param {number} params.page 页码
 * @param {number} params.size 每页大小
 * @param {string} params.keyword 关键词搜索（可选）
 * @param {string} params.name 岗位名称（可选）
 * @param {string} params.sort 排序字段（可选）
 * @returns {Promise} 岗位列表响应
 */
export function getPositionList(params) {
	return request({
		url: "/positions",
		method: "get",
		params,
	});
}

/**
 * 创建岗位
 * @param {Object} data 岗位数据
 * @param {string} data.name 岗位名称
 * @param {string} data.description 岗位描述
 * @param {string} data.responsibilities 岗位职责
 * @param {string} data.level 岗位级别
 * @returns {Promise} 创建结果
 */
export function createPosition(data) {
	return request({
		url: "/positions/create",
		method: "post",
		data,
	});
}

/**
 * 更新岗位信息
 * @param {number} id 岗位ID
 * @param {Object} data 更新数据
 * @returns {Promise} 更新结果
 */
export function updatePosition(id, data) {
	return request({
		url: `/positions/${id}`,
		method: "put",
		data,
	});
}

/**
 * 删除岗位
 * @param {number} id 岗位ID
 * @returns {Promise} 删除结果
 */
export function deletePosition(id) {
	return request({
		url: `/positions/${id}`,
		method: "delete",
	});
}
/**
 * 检查岗位名称是否可用
 * @param {string} name 岗位名称
 * @param {number} excludeId 排除的岗位ID（编辑时使用）
 * @returns {Promise} 检查结果
 */
export function checkPositionNameAvailable(name, excludeId = null) {
	return request({
		url: "/positions/check-name",
		method: "get",
		params: { name, excludeId },
	});
}
