<template>
  <div class="page-container">
    <!-- 使用页面头部组件 -->
    <PageHeader title="资源管理" icon="fa-cogs" description="系统资源配置和管理中心，统一管理各类系统资源" />

    <!-- 功能选项卡 -->
    <TabSection v-model="activeTab" :tabs="tabOptions" />

    <!-- Tab内容容器 -->
    <div class="tab-content-wrapper">
      <!-- 工程服务Tab配置 -->
      <div v-if="activeTab === 'engineering-tab-config'" class="content-section">
        <EngineeringTabManagement />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import PageHeader from '@/components/Common/PageHeader.vue'
import TabSection from '@/components/Common/TabSection.vue'
import EngineeringTabManagement from '@/components/System/EngineeringTabManagement.vue'

// ================================
// 响应式数据
// ================================

// 当前激活的标签页
const activeTab = ref('engineering-tab-config')

// 标签选项配置
const tabOptions = [
  {
    name: 'engineering-tab-config',
    label: '工程服务Tab配置',
    icon: 'fa-cogs'
  }
]
</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;

.page-container {
  padding: var(--spacing-lg);
  background-color: var(--bg-secondary);
  min-height: calc(100vh - var(--header-height));

  .tab-content-wrapper {
    background: var(--bg-primary);
    border-radius: var(--border-radius-md);
    box-shadow: var(--shadow-card);
    border: 1px solid var(--border-light);
    overflow: hidden;
  }

  .content-section {
    padding: var(--spacing-lg);
  }

  .coming-soon {
    @include flex-center;
    min-height: 400px;

    .coming-soon-content {
      @include flex-center;
      flex-direction: column;
      text-align: center;
      color: var(--text-secondary);

      .fa {
        font-size: var(--font-size-icon-notice);
        margin-bottom: var(--spacing-lg);
        color: var(--primary-color);
        opacity: var(--opacity-medium);
      }

      h3 {
        font-size: var(--font-size-xl);
        font-weight: var(--font-weight-medium);
        color: var(--text-primary);
        margin: 0 0 var(--spacing-md) 0;
      }

      p {
        font-size: var(--font-size-base);
        color: var(--text-secondary);
        margin: 0;
      }
    }
  }

  @include respond-to(md) {
    padding: var(--spacing-md);

    .content-section {
      padding: var(--spacing-md);
    }
  }

  @include respond-to(sm) {
    padding: var(--spacing-sm);

    .content-section {
      padding: var(--spacing-sm);
    }

    .coming-soon {
      min-height: 300px;

      .coming-soon-content {
        .fa {
          font-size: var(--font-size-icon-large);
          margin-bottom: var(--spacing-md);
        }

        h3 {
          font-size: var(--font-size-lg);
          margin-bottom: var(--spacing-sm);
        }

        p {
          font-size: var(--font-size-sm);
        }
      }
    }
  }
}
</style>
