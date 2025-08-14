<!-- 预警管理页面 - 预警信息管理和预警指标设定平台 -->
<template>
  <div class="warning-management">
    <!-- 使用页面头部组件 -->
    <PageHeader title="预警管理" icon="fa-exclamation-triangle" description="预警信息管理和预警指标设定平台" />

    <!-- 功能选项卡 -->
    <TabSection v-model="activeTab" :tabs="tabs" @tab-change="handleTabChange" />

    <!-- 标签页内容区域 -->
    <div class="tab-content-wrapper">
      <RecordsManagement v-if="activeTab === 'records'" />
      <ThresholdsManagement v-if="activeTab === 'thresholds'" />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import RecordsManagement from '@/components/Warning/RecordsManagement.vue'
import ThresholdsManagement from '@/components/Warning/ThresholdsManagement.vue'
import PageHeader from '@/components/Common/PageHeader.vue'
import TabSection from '@/components/Common/TabSection.vue'

/**
 * ----------------------------------------
 * 组件状态和引用
 * ----------------------------------------
 */
// 当前激活的标签页
const activeTab = ref('records')

// 选项卡配置
const tabs = [
  { name: 'records', label: '预警信息处理', icon: 'fa-bell' },
  { name: 'thresholds', label: '预警指标设定', icon: 'fa-cog' }
]

/**
 * ----------------------------------------
 * 事件处理方法
 * ----------------------------------------
 */
// 标签页切换处理
const handleTabChange = (tabName) => {
  // 可以在这里添加标签页切换时的逻辑
}
</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;

/**
 * ----------------------------------------
 * 页面基本样式
 * ----------------------------------------
 */
.warning-management {
  padding: var(--spacing-large);
  background-color: var(--bg-secondary);
  min-height: calc(100vh - var(--header-height));

  /**
   * 标签页内容样式
   */
  .tab-content-wrapper {
    background: var(--bg-primary);
    border-radius: var(--border-radius-large);
    box-shadow: var(--shadow-md);
    border: 1px solid var(--border-light);
    overflow: hidden;
  }
}

/**
 * ----------------------------------------
 * 响应式设计
 * ----------------------------------------
 */
// 小屏幕适配
@include respond-to(sm) {
  .warning-management {
    padding: var(--spacing-sm);
  }
}
</style>
