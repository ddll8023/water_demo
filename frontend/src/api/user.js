// 鄂北地区水资源管理系统 - 用户管理API接口
// 基于阶段一后端API的用户管理相关接口

import request from "./request";

/**
 * 分页查询用户列表
 * @param {Object} params 查询参数
 * @param {number} params.page 页码
 * @param {number} params.size 每页大小
 * @param {string} params.username 用户名（可选）
 * @param {number} params.roleId 角色ID（可选）
 * @param {boolean} params.isActive 启用状态（可选）
 * @returns {Promise} 用户列表响应
 */
export function getUserList(params) {
	return request({
		url: "/users",
		method: "get",
		params,
	});
}

/**
 * 查询用户详情
 * @param {number} id 用户ID
 * @returns {Promise} 用户详情
 */
export function getUserDetail(id) {
	return request({
		url: `/users/${id}`,
		method: "get",
	});
}

/**
 * 创建用户
 * @param {Object} data 用户数据
 * @param {string} data.username 用户名
 * @param {string} data.password 密码
 * @param {string} data.email 邮箱
 * @param {number} data.roleId 角色ID
 * @param {boolean} data.isActive 启用状态
 * @returns {Promise} 创建结果
 */
export function createUser(data) {
	return request({
		url: "/users",
		method: "post",
		data,
	});
}

/**
 * 更新用户信息
 * @param {number} id 用户ID
 * @param {Object} data 更新数据
 * @param {string} data.username 用户名（编辑时不可修改）
 * @param {string} data.password 密码（可选，不修改则不传）
 * @param {string} data.email 邮箱
 * @param {number} data.roleId 角色ID
 * @param {boolean} data.isActive 启用状态
 * @returns {Promise} 更新结果
 */
export function updateUser(id, data) {
	return request({
		url: `/users/${id}`,
		method: "put",
		data,
	});
}

/**
 * 删除用户
 * @param {number} id 用户ID
 * @returns {Promise} 删除结果
 */
export function deleteUser(id) {
	return request({
		url: `/users/${id}`,
		method: "delete",
	});
}

/**
 * 获取用户的角色列表
 * @param {number} id 用户ID
 * @returns {Promise} 用户角色列表
 */
export function getUserRoles(id) {
	return request({
		url: `/users/${id}/roles`,
		method: "get",
	});
}

/**
 * 为用户分配角色
 * @param {number} id 用户ID
 * @param {Array<number>} roleIds 角色ID数组
 * @returns {Promise} 分配结果
 */
export function assignUserRoles(id, roleIds) {
	return request({
		url: `/users/${id}/roles`,
		method: "put",
		data: roleIds,
	});
}
