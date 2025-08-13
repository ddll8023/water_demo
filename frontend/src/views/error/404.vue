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
          <CustomButton type="secondary" size="large" @click="refresh">
            <i class="fa fa-refresh"></i>
            刷新页面
          </CustomButton>
        </div>



        <!-- 快速导航 -->
        <div class="quick-navigation">
          <h3>快速导航：</h3>
          <div class="nav-links">
            <el-link v-for="link in quickLinks" :key="link.path" :href="link.path" class="nav-link"
              @click.prevent="navigateTo(link.path)">
              <i class="fa" :class="link.icon"></i>
              {{ link.name }}
            </el-link>
          </div>
        </div>
      </div>

      <!-- 装饰元素 -->
      <div class="error-decoration">
        <div class="floating-element element-1"></div>
        <div class="floating-element element-2"></div>
        <div class="floating-element element-3"></div>
      </div>
    </div>

    <!-- 页脚信息 -->
    <div class="error-footer">
      <p>如果问题持续存在，请联系系统管理员</p>
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
 * 页面配置数据
 * ----------------------------------------
 */
// 快速导航链接配置
const quickLinks = ref([
  { name: '系统首页', path: '/dashboard', icon: 'fa-home' },
  { name: '一张图', path: '/map', icon: 'fa-map-o' },
  { name: '实时监测', path: '/monitoring', icon: 'fa-desktop' },
  { name: '预警管理', path: '/warning', icon: 'fa-warning' },
  { name: '管理服务', path: '/management', icon: 'fa-sitemap' },
  { name: '工程服务', path: '/engineering', icon: 'fa-cog' }
])

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

// 刷新当前页面
const refresh = () => {
  window.location.reload()
}

// 导航到指定路径
const navigateTo = (path) => {
  router.push(path)
  ElMessage.success(`正在跳转到${quickLinks.value.find(link => link.path === path)?.name}...`)
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
/**
 * ----------------------------------------
 * 404错误页面基础样式
 * ----------------------------------------
 */
.error-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 20px;
  position: relative;
  overflow: hidden;

  /**
   * 错误信息容器样式
   */
  .error-container {
    max-width: 800px;
    width: 100%;
    background: rgba(255, 255, 255, 0.95);
    border-radius: 20px;
    padding: 60px 40px;
    text-align: center;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
    backdrop-filter: blur(var(--blur-medium));
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
        color: var(--el-color-primary);
        line-height: 1;
        margin-bottom: 20px;
        text-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        background: linear-gradient(45deg, var(--el-color-primary), var(--el-color-primary-light-3));
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
      }

      // 错误图标样式
      .error-icon {
        font-size: 80px;
        color: var(--el-color-primary-light-3);
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
        color: var(--el-text-color-primary);
        margin-bottom: var(--spacing-base);
        font-weight: 600;
      }

      // 错误描述样式
      .error-description {
        font-size: 16px;
        color: var(--el-text-color-regular);
        margin-bottom: 32px;
        line-height: 1.6;
      }

      // 可能原因区域样式
      .error-reasons {
        text-align: left;
        margin-bottom: 32px;
        padding: 20px;
        background: var(--el-bg-color-light);
        border-radius: 8px;

        h3 {
          font-size: 16px;
          color: var(--el-text-color-primary);
          margin-bottom: 12px;
        }

        ul {
          margin: 0;
          padding-left: 20px;

          li {
            font-size: 14px;
            color: var(--el-text-color-regular);
            margin-bottom: 8px;
            line-height: 1.5;
          }
        }
      }

      // 操作按钮区域样式
      .error-actions {
        margin-bottom: 40px;
        display: flex;
        justify-content: center;
        gap: 16px;
        flex-wrap: wrap;
      }



      // 快速导航区域样式
      .quick-navigation {
        text-align: left;

        h3 {
          font-size: 16px;
          color: var(--el-text-color-primary);
          margin-bottom: var(--spacing-base);
          text-align: center;
        }

        .nav-links {
          display: grid;
          grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
          gap: 12px;

          .nav-link {
            display: flex;
            align-items: center;
            gap: 8px;
            padding: var(--padding-panel-header);
            background: var(--el-bg-color-light);
            border-radius: 8px;
            text-decoration: none;
            color: var(--el-text-color-primary);
            transition: all 0.3s;
            font-size: 14px;

            &:hover {
              background: var(--el-color-primary-light-9);
              color: var(--el-color-primary);
              transform: translateY(-2px);
            }

            .fa {
              font-size: 16px;
            }
          }
        }
      }
    }

    /**
     * 装饰元素样式
     */
    .error-decoration {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      pointer-events: none;
      z-index: -1;

      // 浮动装饰元素
      .floating-element {
        position: absolute;
        border-radius: 50%;
        background: linear-gradient(45deg, var(--el-color-primary-light-8), var(--el-color-primary-light-9));
        opacity: 0.6;
        animation: float 6s ease-in-out infinite;

        &.element-1 {
          width: 60px;
          height: 60px;
          top: 10%;
          left: 10%;
          animation-delay: 0s;
        }

        &.element-2 {
          width: 40px;
          height: 40px;
          top: 20%;
          right: 15%;
          animation-delay: 2s;
        }

        &.element-3 {
          width: 80px;
          height: 80px;
          bottom: 15%;
          left: 15%;
          animation-delay: 4s;
        }
      }
    }
  }

  /**
   * 页脚信息样式
   */
  .error-footer {
    margin-top: 40px;
    text-align: center;
    color: rgba(255, 255, 255, 0.8);
    font-size: 12px;
    z-index: 2;

    p {
      margin: 4px 0;
    }
  }
}

/**
 * ----------------------------------------
 * 动画效果定义
 * ----------------------------------------
 */
// 浮动动画
@keyframes float {

  0%,
  100% {
    transform: translateY(0px);
  }

  50% {
    transform: translateY(-20px);
  }
}

/**
 * ----------------------------------------
 * 响应式设计
 * ----------------------------------------
 */
// 移动端适配
@media (max-width: 768px) {
  .error-page {
    padding: 10px;

    .error-container {
      padding: 40px 20px;

      .error-visual .error-code {
        font-size: 80px;
      }

      .error-content {
        .error-title {
          font-size: 24px;
        }

        .error-actions {
          flex-direction: column;
          align-items: center;

          .custom-button {
            width: 100%;
            max-width: 200px;
          }
        }

        .quick-navigation .nav-links {
          grid-template-columns: 1fr;
        }
      }
    }
  }
}
</style>
