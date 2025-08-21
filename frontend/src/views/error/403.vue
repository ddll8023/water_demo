<template>
  <div class="error-page error-403">
    <div class="error-container">
      <!-- 错误图标和代码 -->
      <div class="error-visual">
        <div class="error-code">403</div>
        <div class="error-icon">
          <i class="fa fa-lock"></i>
        </div>
      </div>

      <!-- 错误信息 -->
      <div class="error-content">
        <h1 class="error-title">访问被拒绝</h1>
        <p class="error-description">
          抱歉，您没有权限访问此页面。
          <br>
          请联系系统管理员获取相应权限，或返回有权限的页面。
        </p>

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
      <p>如需帮助，请联系系统管理员：admin@waterresource.com</p>
      <p>错误代码：403 | 时间：{{ currentTime }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import CustomButton from '@/components/Common/CustomButton.vue'

const router = useRouter()

// 基础数据定义
const currentTime = ref('')
let timeInterval = null

// 页面导航模块
const goHome = () => {
  router.push('/')
}

const goBack = () => {
  if (window.history.length > 1) {
    router.go(-1)
  } else {
    router.push('/')
  }
}

// 生命周期处理
onMounted(() => {
  const updateTime = () => {
    const now = new Date()
    currentTime.value = now.toLocaleString('zh-CN')
  }

  updateTime()
  timeInterval = setInterval(updateTime, 1000)
})

onUnmounted(() => {
  if (timeInterval) {
    clearInterval(timeInterval)
    timeInterval = null
  }
})
</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;

/* 页面基础布局 */
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

  .error-container {
    max-width: 900px;
    width: 100%;
    background: var(--bg-primary);
    border-radius: var(--spacing-large);
    padding: 60px var(--spacing-large);
    text-align: center;
    box-shadow: var(--shadow-card);
    position: relative;
    z-index: 2;

    /* 错误视觉元素 */
    .error-visual {
      margin-bottom: var(--spacing-large);
      position: relative;

      .error-code {
        font-size: 120px;
        font-weight: 900;
        color: var(--danger-color);
        line-height: 1;
        margin-bottom: var(--spacing-large);
        text-shadow: 0 4px 8px var(--black-transparent-light-10);
      }

      .error-icon {
        font-size: 80px;
        color: var(--danger-color);
        opacity: 0.8;
      }
    }

    /* 内容区样式 */
    .error-content {
      .error-title {
        font-size: var(--font-size-xl);
        color: var(--text-primary);
        margin-bottom: var(--spacing-base);
        font-weight: 600;
      }

      .error-description {
        font-size: var(--font-size-medium);
        color: var(--text-secondary);
        margin-bottom: var(--spacing-large);
        line-height: var(--line-height-large);
      }

      /* 操作按钮区域 */
      .error-actions {
        margin-bottom: var(--spacing-large);
        @include flex-center;
        gap: var(--spacing-base);
        flex-wrap: wrap;
      }
    }
  }

  /* 页脚样式 */
  .error-footer {
    margin-top: var(--spacing-large);
    text-align: center;
    color: var(--text-secondary);
    font-size: var(--font-size-extra-small);
    z-index: 2;

    p {
      margin: 4px 0;
    }
  }

  /* 响应式设计 */
  @include respond-to(sm) {
    .error-page {
      padding: var(--spacing-sm);

      .error-container {
        padding: var(--spacing-large) var(--spacing-large);

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
}
</style>
