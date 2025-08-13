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
@use "@/assets/styles/index.scss" as *;

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
    margin-left: var(--sidebar-width);
    transition: margin-left var(--transition-base);
    min-width: 0; // 防止flex子元素溢出

    &.sidebar-collapsed {
      margin-left: var(--sidebar-collapsed-width);
    }
  }

  .mobile-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: var(--black-transparent-overlay);
    z-index: var(--z-index-modal-backdrop);
  }
}

/**
 * 响应式设计
 */
// 大屏幕以下（lg breakpoint: 1200px以下）- 侧边栏默认折叠
@include respond-to(lg) {
  .app-layout .main-container {
    margin-left: var(--sidebar-collapsed-width);

    &.sidebar-collapsed {
      margin-left: var(--sidebar-collapsed-width);
    }
  }
}

// 小屏幕以下（sm breakpoint: 768px以下）- 移动端模式，主内容占满屏幕
@include respond-to(sm) {
  .app-layout .main-container {
    margin-left: 0;

    &.sidebar-collapsed {
      margin-left: 0;
    }
  }
}
</style>
