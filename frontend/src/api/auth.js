// 鄂北地区水资源管理系统 - 认证API接口
// 基于阶段一后端API的认证相关接口

import request from "./request";

/**
 * 用户登录
 * @param {Object} data 登录数据
 * @param {string} data.username 用户名
 * @param {string} data.password 密码
 * @returns {Promise} 登录响应
 */
export function login(data) {
	return request({
		url: "/auth/login",
		method: "post",
		data,
	});
}

/**
 * 用户登出
 * @returns {Promise} 登出响应
 */
export function logout() {
	return request({
		url: "/auth/logout",
		method: "post",
	});
}

/**
 * 获取当前用户信息
 * @returns {Promise} 用户信息
 */
export function getCurrentUser() {
	return request({
		url: "/auth/me",
		method: "get",
	});
}

/**
 * 用户注册（占位接口）
 * @param {Object} data 注册数据
 * @returns {Promise} 注册响应
 */
export function register(data) {
	return request({
		url: "/auth/register",
		method: "post",
		data,
	});
}

/**
 * 发送密码重置邮件
 * @param {string} email 邮箱地址
 * @returns {Promise} 发送结果
 */
export function sendPasswordResetEmail(email) {
	return request({
		url: "/auth/password/reset",
		method: "post",
		data: { email },
	});
}

/**
 * 重置密码
 * @param {Object} data 重置数据
 * @param {string} data.token 重置令牌
 * @param {string} data.password 新密码
 * @param {string} data.passwordConfirmation 确认密码
 * @returns {Promise} 重置结果
 */
export function resetPassword(data) {
	return request({
		url: "/auth/password/update",
		method: "post",
		data,
	});
}

/**
 * 修改密码
 * @param {Object} data 修改数据
 * @param {string} data.currentPassword 当前密码
 * @param {string} data.newPassword 新密码
 * @param {string} data.newPasswordConfirmation 确认新密码
 * @returns {Promise} 修改结果
 */
export function changePassword(data) {
	return request({
		url: "/auth/password/change",
		method: "post",
		data,
	});
}
