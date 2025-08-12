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
/**
 * 主内容区样式
 */

// ===========================
// 主容器样式
// ===========================
.app-main {
  flex: 1;
  background: var(--bg-secondary);
  padding: 20px;
  overflow-y: auto;
  min-height: 0; // 确保可以正确滚动
}

// ===========================
// 过渡动画效果
// ===========================
.app-main {

  // 页面切换动画
  .fade-transform-leave-active,
  .fade-transform-enter-active {
    transition: all 0.3s;
  }

  .fade-transform-enter-from {
    opacity: 0;
    transform: translateX(30px);
  }

  .fade-transform-leave-to {
    opacity: 0;
    transform: translateX(-30px);
  }
}

// ===========================
// 响应式适配
// ===========================
@media (max-width: 767px) {
  .app-main {
    padding: 12px;
  }
}

// ===========================
// 滚动条样式
// ===========================
.app-main::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

.app-main::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.app-main::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;

  &:hover {
    background: #a8a8a8;
  }
}
</style>
