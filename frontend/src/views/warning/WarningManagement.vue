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
/**
 * ----------------------------------------
 * 页面基本样式
 * ----------------------------------------
 */
.warning-management {
  padding: 20px;
  background-color: var(--bg-secondary);
  min-height: calc(100vh - 60px);

  /**
   * 页面头部样式
   */
  .page-header {
    background: var(--bg-primary);
    padding: 20px;
    border-radius: 8px;
    margin-bottom: 20px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, .1);
    border: 1px solid var(--neutral-dark);

    .header-content {
      .page-title {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 24px;
        font-weight: 600;
        color: var(--text-primary);
        margin: 0 0 8px 0;

        .fa {
          color: var(--el-color-warning);
          font-size: 20px;
        }
      }

      .page-description {
        font-size: 14px;
        color: var(--text-tertiary);
        margin: 0;
      }
    }
  }

  /**
   * 标签页内容样式
   */
  .tab-content-wrapper {
    background: var(--bg-primary);
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, .1);
    border: 1px solid var(--neutral-dark);
    overflow: hidden;
  }
}

/**
 * ----------------------------------------
 * 响应式设计
 * ----------------------------------------
 */
// 中等屏幕适配
@media (max-width: 1200px) {
  .warning-management {
    .page-header {
      .header-content {
        .page-title {
          font-size: 20px;
          gap: 6px;
        }
      }
    }
  }
}

// 小屏幕适配
@media (max-width: 768px) {
  .warning-management {
    padding: 10px;

    .page-header {
      padding: 15px;

      .header-content {
        .page-title {
          font-size: 18px;
          gap: 6px;
        }
      }
    }
  }
}
</style>
