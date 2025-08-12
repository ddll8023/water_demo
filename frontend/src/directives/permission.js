/**
 * 权限指令
 * 用于控制DOM元素的显示/隐藏基于用户权限
 */

import { useAuthStore } from '@/stores/modules/auth'

/**
 * 权限指令 v-permission
 * 
 * 使用方式：
 * v-permission="'system:user:view'"           // 单个权限
 * v-permission="['system:user:view', 'system:user:edit']"  // 多个权限（任一）
 * v-permission.all="['system:user:view', 'system:user:edit']"  // 多个权限（全部）
 * v-permission.role="'admin'"                 // 角色权限
 * v-permission.role.all="['admin', 'manager']"  // 多个角色（全部）
 */
export const permission = {
  mounted(el, binding) {
    checkPermission(el, binding)
  },
  
  updated(el, binding) {
    checkPermission(el, binding)
  }
}

/**
 * 检查权限并控制元素显示
 * @param {HTMLElement} el DOM元素
 * @param {Object} binding 指令绑定对象
 */
function checkPermission(el, binding) {
  const { value, modifiers } = binding
  const authStore = useAuthStore()
  
  if (!value) {
    console.warn('v-permission指令需要提供权限值')
    return
  }
  
  let hasPermission = false
  
  // 角色权限检查
  if (modifiers.role) {
    if (Array.isArray(value)) {
      if (modifiers.all) {
        // 检查是否拥有所有角色
        hasPermission = authStore.hasAllRoles ? authStore.hasAllRoles(value) : false
      } else {
        // 检查是否拥有任一角色
        hasPermission = authStore.hasAnyRole ? authStore.hasAnyRole(value) : false
      }
    } else {
      // 单个角色检查
      hasPermission = authStore.hasRole ? authStore.hasRole(value) : false
    }
  } else {
    // 权限检查
    if (Array.isArray(value)) {
      if (modifiers.all) {
        // 检查是否拥有所有权限
        hasPermission = authStore.hasAllPermissions ? authStore.hasAllPermissions(value) : false
      } else {
        // 检查是否拥有任一权限
        hasPermission = authStore.hasAnyPermission ? authStore.hasAnyPermission(value) : false
      }
    } else {
      // 单个权限检查
      hasPermission = authStore.hasPermission ? authStore.hasPermission(value) : false
    }
  }
  
  // 控制元素显示/隐藏
  if (!hasPermission) {
    // 移除元素
    if (el.parentNode) {
      el.parentNode.removeChild(el)
    }
  } else {
    // 确保元素可见（如果之前被隐藏）
    if (el.style.display === 'none') {
      el.style.display = ''
    }
  }
}

/**
 * 角色指令 v-role
 * 专门用于角色权限控制
 * 
 * 使用方式：
 * v-role="'admin'"                    // 单个角色
 * v-role="['admin', 'manager']"       // 多个角色（任一）
 * v-role.all="['admin', 'manager']"   // 多个角色（全部）
 */
export const role = {
  mounted(el, binding) {
    checkRole(el, binding)
  },
  
  updated(el, binding) {
    checkRole(el, binding)
  }
}

/**
 * 检查角色权限并控制元素显示
 * @param {HTMLElement} el DOM元素
 * @param {Object} binding 指令绑定对象
 */
function checkRole(el, binding) {
  const { value, modifiers } = binding
  const authStore = useAuthStore()
  
  if (!value) {
    console.warn('v-role指令需要提供角色值')
    return
  }
  
  let hasRole = false
  
  if (Array.isArray(value)) {
    if (modifiers.all) {
      // 检查是否拥有所有角色
      hasRole = authStore.hasAllRoles ? authStore.hasAllRoles(value) : false
    } else {
      // 检查是否拥有任一角色
      hasRole = authStore.hasAnyRole ? authStore.hasAnyRole(value) : false
    }
  } else {
    // 单个角色检查
    hasRole = authStore.hasRole ? authStore.hasRole(value) : false
  }
  
  // 控制元素显示/隐藏
  if (!hasRole) {
    if (el.parentNode) {
      el.parentNode.removeChild(el)
    }
  } else {
    if (el.style.display === 'none') {
      el.style.display = ''
    }
  }
}

/**
 * 权限检查函数（用于组合式API）
 * @param {string|Array} permissions 权限或权限数组
 * @param {Object} options 选项
 * @returns {boolean} 是否有权限
 */
export function usePermission(permissions, options = {}) {
  const authStore = useAuthStore()
  const { requireAll = false, type = 'permission' } = options
  
  if (!permissions) return true
  
  if (type === 'role') {
    if (Array.isArray(permissions)) {
      return requireAll 
        ? authStore.hasAllRoles?.(permissions) || false
        : authStore.hasAnyRole?.(permissions) || false
    } else {
      return authStore.hasRole?.(permissions) || false
    }
  } else {
    if (Array.isArray(permissions)) {
      return requireAll 
        ? authStore.hasAllPermissions?.(permissions) || false
        : authStore.hasAnyPermission?.(permissions) || false
    } else {
      return authStore.hasPermission?.(permissions) || false
    }
  }
}

/**
 * 角色检查函数（用于组合式API）
 * @param {string|Array} roles 角色或角色数组
 * @param {boolean} requireAll 是否需要全部角色
 * @returns {boolean} 是否有角色
 */
export function useRole(roles, requireAll = false) {
  return usePermission(roles, { requireAll, type: 'role' })
}

/**
 * 权限检查混入（用于选项式API）
 */
export const permissionMixin = {
  methods: {
    /**
     * 检查权限
     * @param {string|Array} permissions 权限
     * @param {boolean} requireAll 是否需要全部权限
     * @returns {boolean} 是否有权限
     */
    $hasPermission(permissions, requireAll = false) {
      return usePermission(permissions, { requireAll })
    },
    
    /**
     * 检查角色
     * @param {string|Array} roles 角色
     * @param {boolean} requireAll 是否需要全部角色
     * @returns {boolean} 是否有角色
     */
    $hasRole(roles, requireAll = false) {
      return useRole(roles, requireAll)
    }
  }
}

/**
 * 安装权限指令
 * @param {Object} app Vue应用实例
 */
export function setupPermissionDirectives(app) {
  app.directive('permission', permission)
  app.directive('role', role)
}

// 默认导出
export default {
  permission,
  role,
  usePermission,
  useRole,
  permissionMixin,
  setupPermissionDirectives
}
