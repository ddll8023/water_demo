<template>
  <div class="error-page error-404">
    <div class="error-container">
      <!-- 错误图标和代码 -->
      <div class="error-visual">
        <div class="error-code">404</div>
        <div class="error-icon">
          <i class="fa fa-file-o"></i>
        </div>
      </div>

      <!-- 错误信息 -->
      <div class="error-content">
        <h1 class="error-title">页面未找到</h1>
        <p class="error-description">
          抱歉，您访问的页面不存在或已被移除。
          <br>
          请检查网址是否正确，或返回首页继续浏览。
        </p>

        <!-- 可能的原因 -->
        <div class="error-reasons">
          <h3>可能的原因：</h3>
          <ul>
            <li>网址输入错误</li>
            <li>页面已被删除或移动</li>
            <li>链接已过期</li>
            <li>您没有访问权限</li>
          </ul>
        </div>

        <!-- 操作按钮 -->
        <div class="error-actions">
          <CustomButton type="primary" size="large" @click="goHome">
            <i class="fa fa-home"></i>
            返回首页
          </CustomButton>
          <CustomButton type="secondary" size="large" @click="goBack">
            <i class="fa fa-arrow-left"></i>
            返回上页
          </CustomButton>
        </div>
      </div>
    </div>

    <!-- 页脚信息 -->
    <div class="error-footer">
      <p>错误代码：404 | 时间：{{ currentTime }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

import CustomButton from '@/components/Common/CustomButton.vue'
// 使用Font Awesome图标，无需导入

/**
 * ----------------------------------------
 * 组件状态和引用
 * ----------------------------------------
 */
// 路由实例
const router = useRouter()

// 当前时间显示
const currentTime = ref('')

/**
 * ----------------------------------------
 * 页面导航方法
 * ----------------------------------------
 */
// 返回首页
const goHome = () => {
  router.push('/')
  ElMessage.success('正在返回首页...')
}

// 返回上一页
const goBack = () => {
  if (window.history.length > 1) {
    router.go(-1)
  } else {
    router.push('/')
  }
  ElMessage.info('正在返回上一页...')
}

/**
 * ----------------------------------------
 * 工具方法
 * ----------------------------------------
 */
// 更新当前时间显示
const updateTime = () => {
  currentTime.value = new Date().toLocaleString('zh-CN')
}

/**
 * ----------------------------------------
 * 生命周期钩子
 * ----------------------------------------
 */
// 组件挂载后执行
onMounted(() => {
  // 初始化时间显示
  updateTime()
  // 每秒更新时间
  setInterval(updateTime, 1000)
})
</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;

/**
 * ----------------------------------------
 * 404错误页面基础样式
 * ----------------------------------------
 */
.error-page {
  min-height: 100vh;
  background: var(--bg-secondary);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: var(--spacing-large);
  position: relative;
  overflow: hidden;

  /**
   * 错误信息容器样式
   */
  .error-container {
    max-width: 800px;
    width: 100%;
    background: var(--bg-primary);
    border-radius: 20px;
    padding: 60px 40px;
    text-align: center;
    box-shadow: var(--shadow-card);
    position: relative;
    z-index: 2;

    /**
     * 错误视觉元素样式
     */
    .error-visual {
      margin-bottom: 40px;
      position: relative;

      // 404错误代码样式
      .error-code {
        font-size: 120px;
        font-weight: 900;
        color: var(--primary-color);
        line-height: 1;
        margin-bottom: var(--spacing-large);
        text-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        background: linear-gradient(45deg, var(--primary-color), var(--primary-lighter));
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
      }

      // 错误图标样式
      .error-icon {
        font-size: 80px;
        color: var(--primary-lighter);
        opacity: var(--opacity-high);
      }
    }

    /**
     * 错误内容区域样式
     */
    .error-content {

      // 错误标题样式
      .error-title {
        font-size: 32px;
        color: var(--text-primary);
        margin-bottom: var(--spacing-base);
        font-weight: var(--font-weight-semibold);
      }

      // 错误描述样式
      .error-description {
        font-size: var(--font-size-medium);
        color: var(--text-secondary);
        margin-bottom: 32px;
        line-height: var(--line-height-large);
      }

      // 可能原因区域样式
      .error-reasons {
        text-align: left;
        margin-bottom: 32px;
        padding: var(--card-padding);
        background: var(--bg-tertiary);
        border-radius: var(--border-radius-large);

        h3 {
          font-size: var(--font-size-medium);
          color: var(--text-primary);
          margin-bottom: var(--spacing-medium);
        }

        ul {
          margin: 0;
          padding-left: var(--spacing-large);

          li {
            font-size: var(--font-size-base);
            color: var(--text-secondary);
            margin-bottom: var(--spacing-small);
            line-height: var(--line-height-base);
          }
        }
      }

      // 操作按钮区域样式
      .error-actions {
        margin-bottom: var(--spacing-large);
        @include flex-center;
        gap: var(--spacing-base);
        flex-wrap: wrap;
      }
    }
  }

  /**
   * 页脚信息样式
   */
  .error-footer {
    margin-top: var(--spacing-40);
    text-align: center;
    color: var(--text-secondary);
    font-size: var(--font-size-extra-small);
    z-index: 2;

    p {
      margin: var(--spacing-mini) 0;
    }
  }
}

/**
 * ----------------------------------------
 * 响应式设计
 * ----------------------------------------
 */
// 移动端适配
@include respond-to(md) {
  .error-page {
    padding: 10px;

    .error-container {
      padding: 40px 20px;

      .error-visual .error-code {
        font-size: 80px;
      }

      .error-content {
        .error-title {
          font-size: var(--font-size-xl);
        }

        .error-actions {
          flex-direction: column;
          align-items: center;

          .custom-button {
            width: 100%;
            max-width: 200px;
          }
        }
      }
    }
  }
}
</style>
