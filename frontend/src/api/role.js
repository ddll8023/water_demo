// 鄂北地区水资源管理系统 - 角色管理API接口
// 基于阶段一后端API的角色管理相关接口

import request from "./request";

/**
 * 分页查询角色列表
 * @param {Object} params 查询参数
 * @param {number} params.page 页码
 * @param {number} params.size 每页大小
 * @param {string} params.name 角色名称搜索（可选）
 * @returns {Promise} 角色列表响应
 */
export function getRoleList(params) {
	return request({
		url: "/roles",
		method: "get",
		params,
	});
}

/**
 * 查询角色详情
 * @param {number} id 角色ID
 * @returns {Promise} 角色详情
 */
export function getRoleDetail(id) {
	return request({
		url: `/roles/${id}`,
		method: "get",
	});
}

/**
 * 创建角色
 * @param {Object} data 角色数据
 * @param {string} data.name 角色名称
 * @param {string} data.description 角色描述
 * @param {number} data.sortOrder 排序顺序
 * @returns {Promise} 创建结果
 */
export function createRole(data) {
	return request({
		url: "/roles",
		method: "post",
		data,
	});
}

/**
 * 更新角色信息
 * @param {number} id 角色ID
 * @param {Object} data 更新数据
 * @returns {Promise} 更新结果
 */
export function updateRole(id, data) {
	return request({
		url: `/roles/${id}`,
		method: "put",
		data,
	});
}

/**
 * 删除角色
 * @param {number} id 角色ID
 * @returns {Promise} 删除结果
 */
export function deleteRole(id) {
	return request({
		url: `/roles/${id}`,
		method: "delete",
	});
}

/**
 * 获取角色权限列表
 * @param {number} id 角色ID
 * @returns {Promise} 权限列表
 */
export function getRolePermissions(id) {
	return request({
		url: `/roles/${id}/permissions`,
		method: "get",
	});
}

/**
 * 为角色分配权限
 * @param {number} id 角色ID
 * @param {Array<number>} permissionIds 权限ID数组
 * @returns {Promise} 分配结果
 */
export function assignRolePermissions(id, permissionIds) {
	return request({
		url: `/roles/${id}/permissions`,
		method: "put",
		data: permissionIds,
	});
}

/**
 * 获取角色选项（用于表单下拉选择）
 * @returns {Promise} 角色选项列表
 */
export function getRoleOptions() {
	return request({
		url: "/roles/available",
		method: "get",
	});
}

/**
 * 检查角色名称是否可用
 * @param {string} name 角色名称
 * @param {number} excludeId 排除的角色ID（编辑时使用）
 * @returns {Promise} 检查结果
 */
export function checkRoleNameAvailable(name, excludeId = null) {
	return request({
		url: "/roles/check-name",
		method: "get",
		params: { name, excludeId },
	});
}
