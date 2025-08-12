// 鄂北地区水资源管理系统 - 权限管理API接口
// 基于阶段一后端API的权限管理相关接口

import request from './request'

/**
 * 分页查询权限列表
 * @param {Object} params 查询参数
 * @param {number} params.page 页码
 * @param {number} params.size 每页大小
 * @param {string} params.keyword 关键词搜索（可选）
 * @param {string} params.type 权限类型（可选）
 * @param {number} params.parentId 父权限ID（可选）
 * @returns {Promise} 权限列表响应
 */
export function getPermissionList(params) {
  return request({
    url: '/permissions',
    method: 'get',
    params
  })
}

/**
 * 查询权限详情
 * @param {number} id 权限ID
 * @returns {Promise} 权限详情
 */
export function getPermissionDetail(id) {
  return request({
    url: `/permissions/${id}`,
    method: 'get'
  })
}

/**
 * 创建权限
 * @param {Object} data 权限数据
 * @param {string} data.name 权限名称
 * @param {string} data.code 权限编码
 * @param {string} data.type 权限类型
 * @param {number} data.parentId 父权限ID
 * @param {number} data.sortOrder 排序顺序
 * @param {string} data.description 权限描述
 * @returns {Promise} 创建结果
 */
export function createPermission(data) {
  return request({
    url: '/permissions',
    method: 'post',
    data
  })
}

/**
 * 更新权限信息
 * @param {number} id 权限ID
 * @param {Object} data 更新数据
 * @returns {Promise} 更新结果
 */
export function updatePermission(id, data) {
  return request({
    url: `/permissions/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除权限
 * @param {number} id 权限ID
 * @returns {Promise} 删除结果
 */
export function deletePermission(id) {
  return request({
    url: `/permissions/${id}`,
    method: 'delete'
  })
}

/**
 * 获取权限树结构
 * @returns {Promise} 权限树
 */
export function getPermissionTree() {
  return request({
    url: '/permissions/tree',
    method: 'get'
  })
}

/**
 * 获取菜单权限树
 * @returns {Promise} 菜单权限树
 */
export function getMenuPermissionTree() {
  return request({
    url: '/permissions/menu-tree',
    method: 'get'
  })
}

/**
 * 按类型查询权限
 * @param {string} type 权限类型（MENU/BUTTON/API）
 * @returns {Promise} 权限列表
 */
export function getPermissionsByType(type) {
  return request({
    url: '/permissions/by-type',
    method: 'get',
    params: { type }
  })
}

/**
 * 获取可用权限列表（用于下拉选择）
 * @returns {Promise} 可用权限列表
 */
export function getAvailablePermissions() {
  return request({
    url: '/permissions/available',
    method: 'get'
  })
}

/**
 * 批量删除权限
 * @param {Array<number>} ids 权限ID数组
 * @returns {Promise} 删除结果
 */
export function batchDeletePermissions(ids) {
  return request({
    url: '/permissions/batch',
    method: 'delete',
    data: { ids }
  })
}

/**
 * 检查权限编码是否可用
 * @param {string} code 权限编码
 * @param {number} excludeId 排除的权限ID（编辑时使用）
 * @returns {Promise} 检查结果
 */
export function checkPermissionCodeAvailable(code, excludeId = null) {
  return request({
    url: '/permissions/check-code',
    method: 'get',
    params: { code, excludeId }
  })
}

/**
 * 获取权限统计信息
 * @returns {Promise} 统计信息
 */
export function getPermissionStatistics() {
  return request({
    url: '/permissions/statistics',
    method: 'get'
  })
}

/**
 * 导出权限列表
 * @param {Object} params 导出参数
 * @returns {Promise} 导出文件
 */
export function exportPermissions(params) {
  return request({
    url: '/permissions/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

/**
 * 获取权限的子权限列表
 * @param {number} parentId 父权限ID
 * @returns {Promise} 子权限列表
 */
export function getChildPermissions(parentId) {
  return request({
    url: `/permissions/${parentId}/children`,
    method: 'get'
  })
}

/**
 * 移动权限到新的父级
 * @param {number} id 权限ID
 * @param {number} newParentId 新父权限ID
 * @returns {Promise} 移动结果
 */
export function movePermission(id, newParentId) {
  return request({
    url: `/permissions/${id}/move`,
    method: 'put',
    data: { newParentId }
  })
}

/**
 * 复制权限
 * @param {number} id 源权限ID
 * @param {string} newName 新权限名称
 * @param {string} newCode 新权限编码
 * @returns {Promise} 复制结果
 */
export function copyPermission(id, newName, newCode) {
  return request({
    url: `/permissions/${id}/copy`,
    method: 'post',
    data: { newName, newCode }
  })
}
