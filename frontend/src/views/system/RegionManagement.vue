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
.region-management {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: calc(100vh - 60px);

  /**
   * ===================================
   * 开发中提示样式
   * ===================================
   */
  .development-notice {
    margin-top: 40px;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .notice-card {
    max-width: 600px;
    width: 100%;
    border: none;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }

  .notice-content {
    display: flex;
    align-items: center;
    gap: 24px;
    padding: 20px;
  }

  .notice-icon {
    font-size: 64px;
    color: #409eff;
    opacity: 0.8;
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
    background: #f0f9ff;
    color: #409eff;
    border-radius: 16px;
    font-size: 14px;
    border: 1px solid #b3d8ff;

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
      font-size: 48px;
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
