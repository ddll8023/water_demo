<template>
  <div class="region-management">
    <!-- 使用页面头部组件 -->
    <PageHeader title="行政区划管理" icon="fa-map" description="维护系统中的行政区域基础数据，为其他模块提供区域筛选支持" />

    <!-- 功能开发中提示 -->
    <div class="development-notice">
      <el-card class="notice-card">
        <div class="notice-content">
          <div class="notice-icon">
            <i class="fa fa-cogs"></i>
          </div>
          <div class="notice-text">
            <h3>功能开发中</h3>
            <p>行政区划管理功能正在开发中，敬请期待...</p>
            <div class="notice-features">
              <span class="feature-item">
                <i class="fa fa-map-marker"></i>
                行政区域管理
              </span>
              <span class="feature-item">
                <i class="fa fa-sitemap"></i>
                区划层级管理
              </span>
              <span class="feature-item">
                <i class="fa fa-database"></i>
                基础数据维护
              </span>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import PageHeader from '@/components/Common/PageHeader.vue'

// 页面基础配置
const loading = ref(false)
</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;

.region-management {
  background-color: var(--bg-secondary);
  min-height: calc(100vh - var(--header-height));

  /**
   * ===================================
   * 开发中提示样式
   * ===================================
   */
  .development-notice {
    margin-top: var(--spacing-40);
    @include flex-center;
  }

  .notice-card {
    max-width: var(--panel-width-default);
    width: 100%;
    border: none;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }

  .notice-content {
    @include flex-start;
    gap: var(--spacing-extra-large);
    padding: var(--spacing-large);
  }

  .notice-icon {
    font-size: var(--font-size-icon-notice);
    color: var(--primary-color);
    opacity: var(--opacity-high);
  }

  .notice-text {
    flex: 1;

    h3 {
      font-size: var(--font-size-xl);
      color: var(--text-primary);
      margin: 0 0 var(--spacing-small) 0;
      font-weight: var(--font-weight-semibold);
    }

    p {
      font-size: var(--font-size-medium);
      color: var(--text-secondary);
      margin: 0 0 var(--spacing-base) 0;
      line-height: var(--line-height-base);
    }
  }

  .notice-features {
    @include flex-start;
    flex-wrap: wrap;
    gap: var(--spacing-medium);
  }

  .feature-item {
    @include flex-start;
    gap: var(--spacing-xs);
    padding: var(--spacing-xs) var(--spacing-medium);
    background: var(--hover-bg-light);
    color: var(--primary-color);
    border-radius: var(--border-radius-feature-tag);
    font-size: var(--font-size-base);
    border: 1px solid var(--hover-border-light);

    i {
      font-size: var(--font-size-sm);
    }
  }

  /**
   * ===================================
   * 响应式设计
   * ===================================
   */
  @include respond-to(md) {
    .notice-content {
      flex-direction: column;
      text-align: center;
      gap: var(--spacing-base);
    }

    .notice-icon {
      font-size: var(--icon-size-xxl);
    }

    .notice-text h3 {
      font-size: var(--font-size-lg);
    }

    .notice-text p {
      font-size: var(--font-size-base);
    }

    .notice-features {
      justify-content: center;
    }
  }
}
</style>
