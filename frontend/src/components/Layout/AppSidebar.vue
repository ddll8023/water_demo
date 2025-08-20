<template>
  <div class="app-sidebar" :class="sidebarClasses">
    <!-- Logo区域 -->
    <div class="sidebar-logo">
      <img src="@/assets/images/logo.svg" alt="Logo" class="logo-img" />
      <span v-if="!isCollapsed" class="logo-text">水资源管理系统</span>
    </div>

    <!-- 菜单区域 -->
    <el-scrollbar class="sidebar-menu-wrapper">
      <el-menu :default-active="activeMenu" :collapse="isCollapsed" :unique-opened="true" :collapse-transition="false"
        mode="vertical" background-color="var(--bg-dark-secondary)" text-color="var(--text-on-dark-secondary)"
        active-text-color="var(--text-on-dark)" @select="handleMenuSelect">
        <sidebar-item v-for="route in menuRoutes" :key="route.path" :item="route" :base-path="route.path" />
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/stores/modules/app'
import { useAuthStore } from '@/stores/modules/auth'
import SidebarItem from './SidebarItem.vue'

/**
 * 组件依赖注入
 */
const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const authStore = useAuthStore()

/**
 * 状态与计算属性
 */
const isCollapsed = computed(() => appStore.sidebarCollapsed)
const isMobile = computed(() => appStore.isMobile)
const activeMenu = computed(() => route.path)

const sidebarClasses = computed(() => ({
  'is-collapsed': isCollapsed.value,
  'is-mobile': isMobile.value,
  'is-mobile-open': isMobile.value && !isCollapsed.value
}))

/**
 * 菜单路由配置
 */
const menuRoutes = computed(() => {
  const routes = [
    {
      path: '/introduction',
      name: 'Introduction',
      meta: {
        title: '工程简介',
        icon: 'fa-file-text-o',
        permission: 'data:view'  // 所有用户都可以查看
      }
    },
    {
      path: '/map',
      name: 'Map',
      meta: {
        title: '一张图',
        icon: 'fa-map-o',
        permission: 'data:view'  // 修改为实际权限代码
      }
    },
    {
      path: '/monitoring',
      name: 'Monitoring',
      meta: {
        title: '实时监测',
        icon: 'fa-line-chart',
        permission: 'data:view'
      },
      children: [
        {
          path: '/monitoring/water-level',
          name: 'WaterLevelMonitoring',
          meta: {
            title: '水位监测',
            icon: 'fa-bar-chart',
            permission: 'data:view'
          }
        },
        {
          path: '/monitoring/flow',
          name: 'FlowMonitoring',
          meta: {
            title: '流量检测',
            icon: 'fa-tint',
            permission: 'data:view'
          }
        },

        {
          path: '/monitoring/water-quality',
          name: 'WaterQualityMonitoring',
          meta: {
            title: '水质检测',
            icon: 'fa-flask',
            permission: 'data:view'
          }
        },
        {
          path: '/monitoring/water-condition',
          name: 'WaterConditionMonitoring',
          meta: {
            title: '水情监测',
            icon: 'fa-database',
            permission: 'data:view'
          }
        },
        {
          path: '/monitoring/rainfall',
          name: 'RainfallMonitoring',
          meta: {
            title: '雨情监测',
            icon: 'fa-tint',
            permission: 'data:view'
          }
        }
      ]
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
        icon: 'fa-cogs',
        permission: 'business:manage'
      }
    },
    {
      path: '/system',
      name: 'System',
      meta: {
        title: '系统管理',
        icon: 'fa-cog',
        permission: 'system:manage'
      },
      children: [
        {
          path: '/system/regions',
          name: 'SystemRegions',
          meta: {
            title: '行政区划管理',
            icon: 'fa-map-o',
            permission: 'system:manage'
          }
        },
        {
          path: '/system/organizations',
          name: 'SystemOrganizations',
          meta: {
            title: '组织机构管理',
            icon: 'fa-sitemap',
            permission: 'system:manage'
          }
        },
        {
          path: '/system/departments',
          name: 'SystemDepartments',
          meta: {
            title: '部门管理',
            icon: 'fa-building-o',
            permission: 'system:manage'
          }
        },
        {
          path: '/system/positions',
          name: 'SystemPositions',
          meta: {
            title: '岗位管理',
            icon: 'fa-briefcase',
            permission: 'system:manage'
          }
        },
        {
          path: '/system/roles',
          name: 'SystemRoles',
          meta: {
            title: '角色管理',
            icon: 'fa-users',
            permission: 'system:manage'
          }
        },
        {
          path: '/system/users',
          name: 'SystemUsers',
          meta: {
            title: '用户管理',
            icon: 'fa-user',
            permission: 'system:manage'
          }
        },
        {
          path: '/system/dictionaries',
          name: 'SystemDictionaries',
          meta: {
            title: '字典管理',
            icon: 'fa-book',
            permission: 'system:manage'
          }
        },
        {
          path: '/system/resources',
          name: 'SystemResources',
          meta: {
            title: '资源管理',
            icon: 'fa-folder-open-o',
            permission: 'system:manage'
          }
        }
      ]
    }
  ]

  // 根据用户权限过滤菜单
  return filterMenuByPermission(routes)
})

/**
 * 权限控制方法
 */
const filterMenuByPermission = (routes) => {
  return routes.filter(route => {
    // 严格的权限检查
    if (route.meta?.permission) {
      const hasPermission = authStore.hasPermission(route.meta.permission)

      // 严格权限检查：没有权限就不显示菜单
      if (!hasPermission) {
        return false
      }
    }

    if (route.children) {
      route.children = filterMenuByPermission(route.children)
      return route.children.length > 0
    }

    return true
  })
}

/**
 * 事件处理方法
 */
const handleMenuSelect = (index) => {
  if (index !== route.path) {
    router.push(index)
  }

  // 移动端选择菜单后自动收起
  if (isMobile.value) {
    appStore.toggleSidebar()
  }
}
</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;

/**
 * 侧边栏基础样式
 */
.app-sidebar {
  width: var(--sidebar-width);
  height: 100vh;
  background: var(--bg-dark-secondary);
  position: fixed;
  left: 0;
  top: 0;
  z-index: var(--z-index-fixed);
  transition: var(--transition-base);
  overflow: hidden;

  /**
   * 折叠状态样式
   */
  &.is-collapsed {
    width: var(--sidebar-collapsed-width);

    // 修复收缩状态下子菜单的显示问题
    :deep(.el-sub-menu) {
      .el-sub-menu__title {

        // 隐藏子菜单标题文字
        span {
          display: none !important;
        }

        // 隐藏展开/收缩箭头图标
        .el-sub-menu__icon-arrow {
          display: none !important;
        }

        // 确保图标居中显示
        .fa {
          margin-right: 0 !important;
          width: 100%;
          text-align: center;
        }

        // 调整子菜单标题的内边距，使图标居中
        padding-left: var(--spacing-large) !important;
        padding-right: var(--spacing-large) !important;
      }
    }
  }

  /**
   * 移动端样式
   */
  &.is-mobile {
    left: calc(-1 * var(--sidebar-width));
    transition: var(--transition-base);

    &.is-mobile-open {
      left: 0;
    }
  }

  /**
   * Logo区域样式
   */
  .sidebar-logo {
    height: var(--header-height);
    display: flex;
    align-items: center;
    padding: 0 var(--spacing-base);
    border-bottom: 1px solid var(--border-dark);

    .logo-img {
      width: var(--spacing-large);
      height: var(--spacing-large);
      border-radius: var(--border-radius-base);
    }

    .logo-text {
      margin-left: var(--spacing-medium);
      font-size: var(--font-size-medium);
      font-weight: var(--font-weight-bold);
      color: var(--text-on-dark);
      white-space: nowrap;
    }
  }

  /**
   * 菜单区域样式
   */
  .sidebar-menu-wrapper {
    height: calc(100vh - var(--header-height));

    :deep(.el-scrollbar__view) {
      height: 100%;
    }
  }

  /**
   * 菜单项样式
   */
  :deep(.el-menu) {
    border: none;
    height: 100%;
    width: 100% !important;

    .el-menu-item,
    .el-sub-menu .el-sub-menu__title {
      height: var(--table-row-height);
      line-height: var(--table-row-height);

      // Font Awesome 图标样式
      .fa {
        width: var(--icon-size-lg);
        text-align: center;
        margin-right: var(--spacing-small);
        font-size: var(--font-size-medium);
        color: var(--icon-color-on-dark);
      }

      &:hover {
        background-color: var(--white-transparent-light);

        .fa {
          color: var(--text-on-dark);
        }
      }
    }

    .el-menu-item {
      &.is-active {
        background-color: var(--primary-color) !important;
        color: var(--text-on-dark) !important;

        .fa {
          color: var(--text-on-dark);
        }
      }
    }

    .el-sub-menu {
      &.is-active>.el-sub-menu__title {
        .fa {
          color: var(--text-on-dark);
        }
      }
    }
  }
}

/**
 * 响应式设计
 */
@media (max-width: 1199px) {
  .app-sidebar:not(.is-mobile) {
    width: var(--sidebar-collapsed-width);
  }
}
</style>
