/**
 * 菜单相关工具函数
 */

/**
 * 根据路由生成菜单数据
 * @param {Array} routes 路由配置
 * @param {Array} permissions 用户权限列表
 * @returns {Array} 菜单数据
 */
export function generateMenus(routes, permissions = []) {
  const menus = []
  
  routes.forEach(route => {
    // 跳过隐藏的路由
    if (route.meta?.hidden) return
    
    // 检查权限
    if (route.meta?.permission && !permissions.includes(route.meta.permission)) {
      return
    }
    
    const menu = {
      path: route.path,
      name: route.name,
      meta: route.meta,
      children: []
    }
    
    // 处理子路由
    if (route.children && route.children.length > 0) {
      menu.children = generateMenus(route.children, permissions)
      // 如果所有子菜单都被过滤掉了，则不显示父菜单
      if (menu.children.length === 0 && route.meta?.permission) {
        return
      }
    }
    
    menus.push(menu)
  })
  
  return menus
}

/**
 * 获取默认菜单配置
 * @returns {Array} 默认菜单配置
 */
export function getDefaultMenus() {
  return [
    {
      path: '/map',
      name: 'Map',
      meta: {
        title: '一张图',
        icon: 'fa-map-o',
        permission: 'data:view'
      }
    },
    {
      path: '/monitoring',
      name: 'Monitoring',
      meta: {
        title: '实时监测',
        icon: 'fa-desktop',
        permission: 'data:view'
      }
    },
    {
      path: '/inspection',
      name: 'Inspection',
      meta: {
        title: '工程巡检',
        icon: 'fa-search',
        permission: 'business:operate'
      }
    },
    {
      path: '/warning',
      name: 'Warning',
      meta: {
        title: '预警管理',
        icon: 'fa-warning',
        permission: 'business:operate'
      }
    },
    {
      path: '/management',
      name: 'Management',
      meta: {
        title: '管理信息服务',
        icon: 'fa-sitemap',
        permission: 'business:manage'
      }
    },
    {
      path: '/engineering',
      name: 'Engineering',
      meta: {
        title: '工程信息服务',
        icon: 'Tools',
        permission: 'business:manage'
      }
    },
    {
      path: '/system',
      name: 'System',
      meta: {
        title: '系统管理',
        icon: 'Setting',
        permission: 'system:manage'
      },
      children: [
        {
          path: '/system/regions',
          name: 'SystemRegions',
          meta: {
            title: '行政区划管理',
            permission: 'system:region:manage'
          }
        },
        {
          path: '/system/organizations',
          name: 'SystemOrganizations',
          meta: {
            title: '组织机构管理',
            permission: 'system:organization:manage'
          }
        },
        {
          path: '/system/departments',
          name: 'SystemDepartments',
          meta: {
            title: '部门管理',
            permission: 'system:department:manage'
          }
        },
        {
          path: '/system/positions',
          name: 'SystemPositions',
          meta: {
            title: '岗位管理',
            permission: 'system:position:manage'
          }
        },
        {
          path: '/system/roles',
          name: 'SystemRoles',
          meta: {
            title: '角色管理',
            permission: 'system:role:manage'
          }
        },
        {
          path: '/system/users',
          name: 'SystemUsers',
          meta: {
            title: '用户管理',
            permission: 'system:user:manage'
          }
        },
        {
          path: '/system/dictionaries',
          name: 'SystemDictionaries',
          meta: {
            title: '字典管理',
            permission: 'system:dictionary:manage'
          }
        },
        {
          path: '/system/resources',
          name: 'SystemResources',
          meta: {
            title: '资源管理',
            permission: 'system:resource:manage'
          }
        }
      ]
    }
  ]
}

/**
 * 根据权限过滤菜单
 * @param {Array} menus 菜单列表
 * @param {Array} permissions 权限列表
 * @returns {Array} 过滤后的菜单
 */
export function filterMenusByPermission(menus, permissions = []) {
  return menus.filter(menu => {
    // 检查当前菜单权限
    if (menu.meta?.permission && !permissions.includes(menu.meta.permission)) {
      return false
    }
    
    // 递归过滤子菜单
    if (menu.children && menu.children.length > 0) {
      menu.children = filterMenusByPermission(menu.children, permissions)
      // 如果有子菜单但都被过滤掉了，检查是否还需要显示父菜单
      if (menu.children.length === 0 && menu.meta?.permission) {
        return false
      }
    }
    
    return true
  })
}

/**
 * 查找菜单项
 * @param {Array} menus 菜单列表
 * @param {string} path 路径
 * @returns {Object|null} 找到的菜单项
 */
export function findMenuItem(menus, path) {
  for (const menu of menus) {
    if (menu.path === path) {
      return menu
    }
    
    if (menu.children && menu.children.length > 0) {
      const found = findMenuItem(menu.children, path)
      if (found) return found
    }
  }
  
  return null
}

/**
 * 获取菜单路径数组（面包屑）
 * @param {Array} menus 菜单列表
 * @param {string} path 当前路径
 * @returns {Array} 路径数组
 */
export function getMenuPath(menus, path) {
  const result = []
  
  function findPath(menuList, targetPath, currentPath = []) {
    for (const menu of menuList) {
      const newPath = [...currentPath, menu]
      
      if (menu.path === targetPath) {
        result.push(...newPath)
        return true
      }
      
      if (menu.children && menu.children.length > 0) {
        if (findPath(menu.children, targetPath, newPath)) {
          return true
        }
      }
    }
    return false
  }
  
  findPath(menus, path)
  return result
}

/**
 * 扁平化菜单结构
 * @param {Array} menus 菜单列表
 * @returns {Array} 扁平化后的菜单
 */
export function flattenMenus(menus) {
  const result = []
  
  function flatten(menuList) {
    menuList.forEach(menu => {
      result.push({
        path: menu.path,
        name: menu.name,
        meta: menu.meta
      })
      
      if (menu.children && menu.children.length > 0) {
        flatten(menu.children)
      }
    })
  }
  
  flatten(menus)
  return result
}

/**
 * 检查菜单是否有权限
 * @param {Object} menu 菜单项
 * @param {Array} permissions 权限列表
 * @returns {boolean} 是否有权限
 */
export function hasMenuPermission(menu, permissions = []) {
  if (!menu.meta?.permission) return true
  return permissions.includes(menu.meta.permission)
}
