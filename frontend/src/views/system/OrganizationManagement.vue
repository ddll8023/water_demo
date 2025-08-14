<template>
  <div class="organization-management">
    <!-- 使用页面头部组件 -->
    <PageHeader title="组织机构管理" icon="fa-building" description="管理系统组织架构、机构层级和机构基本信息" />

    <!-- 功能开发中提示 -->
    <div class="development-notice">
      <el-card class="notice-card">
        <div class="notice-content">
          <div class="notice-icon">
            <i class="fa fa-wrench"></i>
          </div>
          <div class="notice-text">
            <h3>功能开发中</h3>
            <p>组织机构管理功能正在开发中，敬请期待...</p>
            <div class="notice-features">
              <span class="feature-item">
                <i class="fa fa-sitemap"></i>
                组织架构管理
              </span>
              <span class="feature-item">
                <i class="fa fa-users"></i>
                机构层级管理
              </span>
              <span class="feature-item">
                <i class="fa fa-edit"></i>
                机构信息维护
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

/**
 * ===================================
 * 页面基础布局样式
 * ===================================
 */
.organization-management {
  padding: var(--spacing-large);
  background-color: var(--bg-secondary);
  min-height: calc(100vh - var(--header-height));
}

/**
 * ===================================
 * 开发中提示样式
 * ===================================
 */
.development-notice {
  margin-top: var(--form-item-height);
  display: flex;
  justify-content: center;
  align-items: center;
}

.notice-card {
  max-width: var(--panel-height-default);
  width: 100%;
  border: none;
  box-shadow: var(--shadow-card);
}

.notice-content {
  display: flex;
  align-items: center;
  gap: var(--spacing-extra-large);
  padding: var(--spacing-large);
}

.notice-icon {
  font-size: var(--font-size-notice-icon);
  color: var(--el-color-primary);
  opacity: var(--opacity-high);
}

.notice-text {
  flex: 1;

  h3 {
    font-size: var(--font-size-xl);
    color: var(--el-text-color-primary);
    margin: 0 0 8px 0;
    font-weight: var(--font-weight-semibold);
  }

  p {
    font-size: var(--font-size-medium);
    color: var(--el-text-color-regular);
    margin: 0 0 var(--spacing-base) 0;
    line-height: var(--line-height-base);
  }
}

.notice-features {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-medium);
}

.feature-item {
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-xs);
  padding: var(--spacing-xs) var(--spacing-medium);
  background: var(--hover-bg-light);
  color: var(--el-color-primary);
  border-radius: var(--border-radius-feature-tag);
  font-size: var(--font-size-small);
  border: 1px solid var(--hover-border-light);

  i {
    font-size: var(--spacing-medium);
  }
}

/**
 * ===================================
 * 响应式设计
 * ===================================
 */
@include respond-to(sm) {
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
    font-size: var(--font-size-small);
  }

  .notice-features {
    justify-content: center;
  }
}
</style>
