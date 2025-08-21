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
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import CustomButton from '@/components/Common/CustomButton.vue'

const router = useRouter()
const currentTime = ref('')
let timeInterval = null

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

onMounted(() => {
  const updateTime = () => {
    currentTime.value = new Date().toLocaleString('zh-CN')
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
    max-width: 800px;
    width: 100%;
    background: var(--bg-primary);
    border-radius: 20px;
    padding: 60px 40px;
    text-align: center;
    box-shadow: var(--shadow-card);
    position: relative;
    z-index: 2;

    .error-visual {
      margin-bottom: 40px;
      position: relative;

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

      .error-icon {
        font-size: 80px;
        color: var(--primary-lighter);
        opacity: 0.8;
      }
    }

    .error-content {
      .error-title {
        font-size: 32px;
        color: var(--text-primary);
        margin-bottom: var(--spacing-base);
        font-weight: var(--font-weight-semibold);
      }

      .error-description {
        font-size: var(--font-size-medium);
        color: var(--text-secondary);
        margin-bottom: 32px;
        line-height: var(--line-height-large);
      }

      .error-reasons {
        text-align: left;
        margin-bottom: 32px;
        padding: var(--spacing-large);
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

      .error-actions {
        margin-bottom: var(--spacing-large);
        @include flex-center;
        gap: var(--spacing-base);
        flex-wrap: wrap;
      }
    }
  }

  .error-footer {
    margin-top: var(--spacing-large);
    text-align: center;
    color: var(--text-secondary);
    font-size: var(--font-size-extra-small);
    z-index: 2;

    p {
      margin: var(--spacing-mini) 0;
    }
  }
}

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
