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

<style scoped>
/**
 * ===================================
 * 页面基础布局样式
 * ===================================
 */
.organization-management {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: calc(100vh - 60px);
}

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
</style>
