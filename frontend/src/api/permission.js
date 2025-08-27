// 鄂北地区水资源管理系统 - 权限管理API接口
// 基于阶段一后端API的权限管理相关接口

import request from './request'
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
