<template>
  <div class="app-layout">
    <AppSidebar />
    <div class="main-container" :class="{ 'sidebar-collapsed': sidebarCollapsed }">
      <AppHeader />
      <AppMain />
    </div>
    <!-- 移动端遮罩层 -->
    <div v-if="isMobile && !sidebarCollapsed" class="mobile-overlay" @click="toggleSidebar"></div>
  </div>
</template>

<script setup>
/**
 * 组件导入
 */
import { computed } from 'vue'
import { useAppStore } from '@/stores/modules/app'
import AppSidebar from './AppSidebar.vue'
import AppHeader from './AppHeader.vue'
import AppMain from './AppMain.vue'

/**
 * 状态管理
 */
const appStore = useAppStore()

/**
 * 计算属性
 */
const sidebarCollapsed = computed(() => appStore.sidebarCollapsed)
const isMobile = computed(() => appStore.isMobile)

/**
 * 事件处理
 */
const toggleSidebar = () => {
  appStore.toggleSidebar()
}
</script>

<style scoped lang="scss">
/**
 * 基础布局样式
 */
.app-layout {
  display: flex;
  height: 100vh;
  overflow: hidden;

  .main-container {
    flex: 1;
    display: flex;
    flex-direction: column;
    margin-left: 200px;
    transition: margin-left 0.28s ease;
    min-width: 0; // 防止flex子元素溢出

    &.sidebar-collapsed {
      margin-left: 64px;
    }
  }

  .mobile-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.3);
    z-index: 1000;
  }
}

/**
 * 响应式设计
 */
@media (max-width: 1199px) {
  .app-layout .main-container {
    margin-left: 64px;

    &.sidebar-collapsed {
      margin-left: 64px;
    }
  }
}

@media (max-width: 767px) {
  .app-layout .main-container {
    margin-left: 0;

    &.sidebar-collapsed {
      margin-left: 0;
    }
  }
}
</style>
