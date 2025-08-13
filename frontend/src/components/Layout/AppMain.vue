<template>
  <div class="app-main">
    <router-view v-slot="{ Component, route }">
      <transition name="fade-transform" mode="out-in">
        <keep-alive :include="cachedViews">
          <component :is="Component" :key="route.path" />
        </keep-alive>
      </transition>
    </router-view>
  </div>
</template>

<script setup>
/**
 * @description 主内容区组件，用于展示路由页面内容，支持页面缓存和过渡动画
 */

// ===========================
// 导入依赖
// ===========================
import { computed } from 'vue'
import { useAppStore } from '@/stores/modules/app'

// ===========================
// 状态管理
// ===========================
const appStore = useAppStore()

// ===========================
// 计算属性
// ===========================
/**
 * 需要被缓存的页面组件名称列表
 */
const cachedViews = computed(() => appStore.cachedViews)
</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;

/**
 * 主内容区样式
 */

// ===========================
// 主容器样式
// ===========================
.app-main {
  flex: 1;
  background: var(--bg-secondary);
  padding: var(--spacing-large);
  overflow-y: auto;
  min-height: 0; // 确保可以正确滚动
}

// ===========================
// 过渡动画效果
// ===========================
.app-main {

  // 页面切换动画
  :deep(.fade-transform-leave-active),
  :deep(.fade-transform-enter-active) {
    transition: var(--transition-base);
  }

  :deep(.fade-transform-enter-from) {
    opacity: 0;
    transform: translateX(var(--spacing-extra-large));
  }

  :deep(.fade-transform-leave-to) {
    opacity: 0;
    transform: translateX(calc(-1 * var(--spacing-extra-large)));
  }
}

// ===========================
// 响应式适配
// ===========================
@include respond-to(sm) {
  .app-main {
    padding: var(--spacing-medium);
  }
}

// ===========================
// 滚动条样式
// ===========================
.app-main {
  @include custom-scrollbar();
}
</style>
