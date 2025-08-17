// 鄂北地区水资源管理系统 - 管理信息服务API接口
// 专注于工程运行管理中的基础信息档案管理

import request from "./request";

/**
 * 分页查询人员信息列表
 * @param {Object} params 查询参数
 * @param {number} params.page 页码
 * @param {number} params.size 每页数量
 * @param {string} params.name 人员姓名（可选）
 * @param {number} params.departmentId 部门ID（可选）
 * @param {number} params.positionId 岗位ID（可选）
 * @returns {Promise} 人员列表响应
 */
export function getPersonnelList(params) {
	return request({
		url: "/management-info/personnel",
		method: "get",
		params,
	});
}

/**
 * 创建人员信息
 * @param {Object} data 人员信息数据
 * @param {string} data.fullName 姓名
 * @param {string} data.phone 联系电话
 * @param {string} data.email 电子邮箱（可选）
 * @param {number} data.positionId 岗位ID
 * @param {number} data.departmentId 部门ID
 * @param {string} data.hireDate 入职日期
 * @param {string} data.workResponsibilities 工作职责（可选）
 * @returns {Promise} 创建结果
 */
export function createPersonnel(data) {
	return request({
		url: "/management-info/personnel",
		method: "post",
		data,
	});
}

/**
 * 更新人员信息
 * @param {number} id 人员ID
 * @param {Object} data 人员信息数据
 * @param {string} data.fullName 姓名
 * @param {string} data.phone 联系电话
 * @param {string} data.email 电子邮箱（可选）
 * @param {number} data.positionId 岗位ID
 * @param {number} data.departmentId 部门ID
 * @param {number} data.userId 关联用户ID（可选）
 * @param {string} data.employeeNo 工号
 * @param {string} data.hireDate 入职日期（可选）
 * @param {string} data.workResponsibilities 工作职责（可选）
 * @returns {Promise} 更新结果
 */
export function updatePersonnel(id, data) {
	return request({
		url: `/management-info/personnel/${id}`,
		method: "put",
		data,
	});
}

/**
 * 删除人员信息
 * @param {number} id 人员ID
 * @returns {Promise} 删除结果
 */
export function deletePersonnel(id) {
	return request({
		url: `/management-info/personnel/${id}`,
		method: "delete",
	});
}

/**
 * 批量删除人员信息
 * @param {Array<number>} ids 人员ID数组
 * @returns {Promise} 删除结果
 */
export function batchDeletePersonnel(ids) {
	return request({
		url: "/management-info/personnel/batch",
		method: "delete",
		data: { ids },
	});
}

/**
 * 获取部门树形结构
 * @returns {Promise} 部门树形结构
 */
export function getDepartmentTree() {
	return request({
		url: "/management-info/departments/tree",
		method: "get",
	});
}

/**
 * 更新部门信息
 * @param {number} id 部门ID
 * @param {Object} data 部门信息数据
 * @param {string} data.name 部门名称
 * @param {number} data.parentId 上级部门ID（可选）
 * @param {string} data.duty 部门职责（可选）
 * @param {string} data.contact 联系信息（可选）
 * @param {string} data.status 状态（可选，"1"启用，"0"禁用）
 * @returns {Promise} 更新结果
 */
export function updateDepartment(id, data) {
	return request({
		url: `/management-info/departments/${id}`,
		method: "put",
		data,
	});
}
