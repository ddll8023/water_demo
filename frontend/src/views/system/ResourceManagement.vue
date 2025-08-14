<template>
  <div class="resource-management">
    <!-- 使用页面头部组件 -->
    <PageHeader title="资源管理" icon="fa-files-o" description="管理系统中的文档、图片、视频等资源文件" />

    <!-- 功能开发中提示 -->
    <div class="development-notice">
      <el-card class="notice-card">
        <div class="notice-content">
          <div class="notice-icon">
            <i class="fa fa-cloud-upload"></i>
          </div>
          <div class="notice-text">
            <h3>功能开发中</h3>
            <p>资源管理功能正在开发中，敬请期待...</p>
            <div class="notice-features">
              <span class="feature-item">
                <i class="fa fa-upload"></i>
                文件上传下载
              </span>
              <span class="feature-item">
                <i class="fa fa-folder-open"></i>
                文件夹管理
              </span>
              <span class="feature-item">
                <i class="fa fa-eye"></i>
                文件预览
              </span>
              <span class="feature-item">
                <i class="fa fa-search"></i>
                文件搜索
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
// ===========================
// 页面整体布局
// ===========================
.resource-management {
  background-color: #f5f5f5;
  min-height: calc(100vh - var(--header-height));

  /**
   * ===================================
   * 开发中提示样式
   * ===================================
   */
  .development-notice {
    margin-top: var(--spacing-40);
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .notice-card {
    max-width: var(--panel-width-default);
    width: 100%;
    border: none;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }

  .notice-content {
    display: flex;
    align-items: center;
    gap: 24px;
    padding: var(--card-padding);
  }

  .notice-icon {
    font-size: var(--font-size-icon-notice);
    color: #409eff;
    opacity: var(--opacity-high);
  }

  .notice-text {
    flex: 1;

    h3 {
      font-size: 24px;
      color: #303133;
      margin: 0 0 8px 0;
      font-weight: 600;
    }

    p {
      font-size: 16px;
      color: #606266;
      margin: 0 0 16px 0;
      line-height: 1.5;
    }
  }

  .notice-features {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
  }

  .feature-item {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 6px 12px;
    background: var(--hover-bg-light);
    color: #409eff;
    border-radius: var(--border-radius-feature-tag);
    font-size: 14px;
    border: 1px solid var(--hover-border-light);

    i {
      font-size: 12px;
    }
  }

  /**
   * ===================================
   * 响应式设计
   * ===================================
   */
  @media (max-width: 768px) {
    .notice-content {
      flex-direction: column;
      text-align: center;
      gap: 16px;
    }

    .notice-icon {
      font-size: var(--icon-size-xxl);
    }

    .notice-text h3 {
      font-size: 20px;
    }

    .notice-text p {
      font-size: 14px;
    }

    .notice-features {
      justify-content: center;
    }
  }
}
</style>
