<template>
  <div class="app-header">
    <div class="header-left">
      <CustomButton type="text" @click="toggleSidebar" class="sidebar-toggle">
        <i class="fa" :class="sidebarCollapsed ? 'fa-expand' : 'fa-compress'" style="font-size: 18px;"></i>
      </CustomButton>

      <!-- 面包屑导航 -->
      <el-breadcrumb separator="/" class="breadcrumb">
        <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.path" :to="item.path">
          {{ item.title }}
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="header-right">
      <!-- 用户信息下拉菜单 -->
      <el-dropdown @command="handleCommand" trigger="click">
        <span class="user-info">
          <el-avatar :size="32" :src="userInfo?.avatar">
            {{ userInfo?.fullName?.charAt(0) }}
          </el-avatar>
          <span class="username">{{ userInfo?.fullName }}</span>
          <i class="fa fa-angle-down dropdown-icon"></i>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">
              <i class="fa fa-user"></i>
              个人信息
            </el-dropdown-item>
            <el-dropdown-item command="settings">
              <i class="fa fa-cog"></i>
              系统设置
            </el-dropdown-item>
            <el-dropdown-item divided command="logout">
              <i class="fa fa-sign-out"></i>
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
/**
 * =============================================================================
 * 头部组件
 * 负责显示面包屑导航和用户信息管理
 * =============================================================================
 */

/**
 * 组件依赖导入
 * -----------------------------------------------------------------------------
 */
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/modules/auth'
import { useAppStore } from '@/stores/modules/app'
import CustomButton from '@/components/Common/CustomButton.vue'

/**
 * 组件设置
 * -----------------------------------------------------------------------------
 */
const router = useRouter()
const authStore = useAuthStore()
const appStore = useAppStore()

/**
 * 计算属性
 * -----------------------------------------------------------------------------
 */
const userInfo = computed(() => authStore.userInfo)
const sidebarCollapsed = computed(() => appStore.sidebarCollapsed)
const breadcrumbs = computed(() => appStore.breadcrumbs)

/**
 * 布局控制功能
 * -----------------------------------------------------------------------------
 */
const toggleSidebar = () => {
  appStore.toggleSidebar()
}

/**
 * 用户交互功能
 * -----------------------------------------------------------------------------
 */
// 用户菜单命令处理
const handleCommand = async (command) => {
  switch (command) {
    case 'profile':
      // 跳转到个人信息页面
      router.push('/profile')
      break
    case 'settings':
      // 跳转到系统设置页面
      router.push('/settings')
      break
    case 'logout':
      await handleLogout()
      break
  }
}

// 用户退出登录处理
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await authStore.logout()
    ElMessage.success('退出登录成功')
    router.push('/login')
  } catch (error) {
    // 用户取消或其他错误
    if (error !== 'cancel') {
      console.error('Logout error:', error)
    }
  }
}
</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;

/**
 * =============================================================================
 * 头部组件样式
 * =============================================================================
 */

/**
 * 主容器样式
 * -----------------------------------------------------------------------------
 */
.app-header {
  height: var(--header-height);
  background: var(--bg-primary);
  border-bottom: 1px solid var(--border-light);
  @include flex-between;
  padding: 0 var(--main-content-padding);
  box-shadow: var(--shadow-light);
  position: relative;
  z-index: var(--z-index-sticky);
}

/**
 * 头部左侧区域样式
 * -----------------------------------------------------------------------------
 */
.header-left {
  @include flex-start;
  gap: var(--spacing-base);

  .sidebar-toggle {
    padding: var(--spacing-small);
    border-radius: var(--border-radius-base);

    &:hover {
      background: var(--bg-secondary);
    }
  }

  .breadcrumb {
    font-size: var(--font-size-base);
  }
}

/**
 * 头部右侧区域样式
 * -----------------------------------------------------------------------------
 */
.header-right {
  .user-info {
    @include flex-start;
    gap: var(--spacing-small);
    cursor: pointer;
    padding: var(--spacing-small) var(--spacing-medium);
    border-radius: var(--border-radius-base);
    transition: var(--transition-base);

    &:hover {
      background: var(--bg-secondary);
    }

    .username {
      color: var(--text-primary);
      font-weight: var(--font-weight-medium);
      font-size: var(--font-size-base);
    }

    .dropdown-icon {
      font-size: var(--font-size-extra-small);
      color: var(--text-secondary);
      transition: var(--transition-transform);
    }
  }
}

/**
 * 响应式布局适配
 * -----------------------------------------------------------------------------
 */
@include respond-to(sm) {
  .app-header {
    padding: 0 var(--spacing-medium);

    .header-left {
      gap: var(--spacing-medium);

      .breadcrumb {
        display: none; // 移动端隐藏面包屑
      }
    }

    .header-right .user-info {
      .username {
        display: none; // 移动端隐藏用户名
      }
    }
  }
}
</style>
