// 鄂北地区水资源管理系统 - 部门管理API接口
// 基于阶段一后端API的部门管理相关接口

import request from "./request";

/**
 * 分页查询部门列表
 * @param {Object} params 查询参数
 * @param {number} params.page 页码
 * @param {number} params.size 每页大小
 * @param {string} params.keyword 关键词搜索（可选）
 * @param {boolean} params.isActive 启用状态（可选）
 * @returns {Promise} 部门列表响应
 */
export function getDepartmentList(params) {
	return request({
		url: "/departments",
		method: "get",
		params,
	});
}

/**
 * 创建部门 - 严格按照后端API标准
 * @param {Object} data 部门数据
 * @param {string} data.name 部门名称
 * @param {number} data.parentId 父部门ID (可选)
 * @param {string} data.duty 部门职责 (可选)
 * @param {string} data.contact 联系方式 (可选)
 * @param {number} data.regionId 所属行政区域ID (可选)
 * @returns {Promise} 创建结果
 */
export function createDepartment(data) {
	return request({
		url: "/departments",
		method: "post",
		data,
	});
}

/**
 * 更新部门信息 - 严格按照后端API标准
 * @param {number} id 部门ID
 * @param {Object} data 更新数据
 * @param {string} data.name 部门名称 (可选)
 * @param {number} data.parentId 父部门ID (可选)
 * @param {string} data.duty 部门职责 (可选)
 * @param {string} data.contact 联系方式 (可选)
 * @param {number} data.regionId 所属行政区域ID (可选)
 * @returns {Promise} 更新结果
 */
export function updateDepartment(id, data) {
	return request({
		url: `/departments/${id}`,
		method: "put",
		data,
	});
}

/**
 * 删除部门
 * @param {number} id 部门ID
 * @returns {Promise} 删除结果
 */
export function deleteDepartment(id) {
	return request({
		url: `/departments/${id}`,
		method: "delete",
	});
}

/**
 * 检查部门名称是否可用
 * @param {string} name 部门名称
 * @param {number} parentId 父部门ID
 * @param {number} excludeId 排除的部门ID（编辑时使用）
 * @returns {Promise} 检查结果
 */
export function checkDepartmentNameAvailable(
	name,
	parentId = null,
	excludeId = null
) {
	return request({
		url: "/departments/check-name",
		method: "get",
		params: { name, parentId, excludeId },
	});
}
