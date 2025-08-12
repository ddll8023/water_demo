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

        <!-- 权限说明 -->
        <div class="permission-info">
          <h3>权限说明：</h3>
          <div class="info-grid">
            <div class="info-item">
              <i class="fa fa-user info-icon"></i>
              <div class="info-content">
                <div class="info-title">当前用户</div>
                <div class="info-value">{{ currentUser }}</div>
              </div>
            </div>
            <div class="info-item">
              <i class="fa fa-user info-icon"></i>
              <div class="info-content">
                <div class="info-title">用户角色</div>
                <div class="info-value">{{ userRole }}</div>
              </div>
            </div>
            <div class="info-item">
              <i class="fa fa-key info-icon"></i>
              <div class="info-content">
                <div class="info-title">所需权限</div>
                <div class="info-value">{{ requiredPermission }}</div>
              </div>
            </div>
            <div class="info-item">
              <i class="fa fa-clock-o info-icon"></i>
              <div class="info-content">
                <div class="info-title">访问时间</div>
                <div class="info-value">{{ accessTime }}</div>
              </div>
            </div>
          </div>
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
          <CustomButton type="secondary" size="large" @click="contactAdmin">
            <i class="fa fa-envelope"></i>
            联系管理员
          </CustomButton>
        </div>

        <!-- 权限申请 -->
        <div class="permission-request">
          <h3>申请权限：</h3>
          <p>如果您需要访问此页面，请填写权限申请：</p>
          <el-form ref="requestFormRef" :model="requestForm" :rules="requestRules" label-width="100px"
            class="request-form">
            <el-form-item label="申请理由" prop="reason">
              <CustomTextarea v-model="requestForm.reason" :rows="3" placeholder="请说明申请此权限的理由..." />
            </el-form-item>
            <el-form-item label="联系方式" prop="contact">
              <CustomInput v-model="requestForm.contact" placeholder="请输入您的联系方式（邮箱或电话）" />
            </el-form-item>
            <el-form-item>
              <CustomButton type="primary" @click="submitRequest">
                <i class="fa fa-paper-plane"></i>
                提交申请
              </CustomButton>
              <CustomButton type="secondary" @click="resetForm">
                <i class="fa fa-refresh"></i>
                重置
              </CustomButton>
            </el-form-item>
          </el-form>
        </div>

        <!-- 可访问的页面 -->
        <div class="accessible-pages">
          <h3>您可以访问的页面：</h3>
          <div class="page-links">
            <el-link v-for="page in accessiblePages" :key="page.path" :href="page.path" class="page-link"
              @click.prevent="navigateTo(page.path)">
              <i class="fa" :class="page.icon"></i>
              {{ page.name }}
              <el-tag size="small" type="success">有权限</el-tag>
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
      <p>如需帮助，请联系系统管理员：admin@waterresource.com</p>
      <p>错误代码：403 | 时间：{{ currentTime }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import CustomInput from '@/components/Common/CustomInput.vue'
import CustomTextarea from '@/components/Common/CustomTextarea.vue'
import CustomButton from '@/components/Common/CustomButton.vue'

const router = useRouter()

// ==================== 基础数据定义 ====================
/**
 * 页面展示所需的基础响应式数据
 * 包括当前时间、用户信息、权限信息等
 */
const currentTime = ref('')
const currentUser = ref('张三')
const userRole = ref('普通用户')
const requiredPermission = ref('系统管理权限')
const accessTime = ref('')

// 可访问的页面列表
const accessiblePages = ref([
  { name: '系统首页', path: '/dashboard', icon: 'HomeFilled' },
  { name: '一张图', path: '/map', icon: 'MapLocation' },
  { name: '实时监测', path: '/monitoring', icon: 'Monitor' },
  { name: '个人中心', path: '/profile', icon: 'User' }
])

// ==================== 表单处理模块 ====================
/**
 * 权限申请表单相关逻辑
 * 包括表单数据、验证规则、提交和重置方法
 */
// 表单引用和数据
const requestFormRef = ref()
const requestForm = ref({
  reason: '',
  contact: ''
})

// 表单验证规则
const requestRules = {
  reason: [
    { required: true, message: '请输入申请理由', trigger: 'blur' },
    { min: 10, message: '申请理由至少10个字符', trigger: 'blur' }
  ],
  contact: [
    { required: true, message: '请输入联系方式', trigger: 'blur' },
    { pattern: /^(\w+@\w+\.\w+|1[3-9]\d{9})$/, message: '请输入正确的邮箱或手机号', trigger: 'blur' }
  ]
}

// 表单提交方法
const submitRequest = async () => {
  try {
    await requestFormRef.value?.validate()
    ElMessage.success('权限申请已提交，管理员将在1-2个工作日内处理')
    resetForm()
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}

// 表单重置方法
const resetForm = () => {
  requestFormRef.value?.resetFields()
}

// ==================== 页面导航模块 ====================
/**
 * 页面导航相关方法
 * 包括返回首页、返回上一页、联系管理员、页面跳转等
 */
// 返回首页
const goHome = () => {
  router.push('/dashboard')
  ElMessage.success('正在返回首页...')
}

// 返回上一页
const goBack = () => {
  if (window.history.length > 1) {
    router.go(-1)
  } else {
    router.push('/dashboard')
  }
  ElMessage.info('正在返回上一页...')
}

// 联系管理员
const contactAdmin = () => {
  ElMessage.info('正在打开联系管理员功能...')
  // 这里可以实现联系管理员的功能，比如打开邮件客户端或在线客服
}

// 页面跳转
const navigateTo = (path) => {
  router.push(path)
  ElMessage.success(`正在跳转到${accessiblePages.value.find(page => page.path === path)?.name}...`)
}

// ==================== 时间处理模块 ====================
/**
 * 时间相关处理方法
 * 包括更新当前时间和访问时间
 */
const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleString('zh-CN')
  accessTime.value = now.toLocaleString('zh-CN')
}

// ==================== 生命周期处理 ====================
/**
 * 组件生命周期钩子
 * 组件挂载时初始化和设置定时器
 */
onMounted(() => {
  // 初始化时间
  updateTime()
  // 每秒更新时间
  setInterval(updateTime, 1000)
})
</script>

<style scoped lang="scss">
/* ==================== 页面基础布局 ==================== */
/**
 * 错误页面的基础样式和容器布局
 */
.error-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 20px;
  position: relative;
  overflow: hidden;

  .error-container {
    max-width: 900px;
    width: 100%;
    background: rgba(255, 255, 255, 0.95);
    border-radius: 20px;
    padding: 60px 40px;
    text-align: center;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
    backdrop-filter: blur(10px);
    position: relative;
    z-index: 2;

    /* ==================== 错误视觉元素 ==================== */
    /**
     * 错误代码和图标的视觉样式
     */
    .error-visual {
      margin-bottom: 40px;
      position: relative;

      .error-code {
        font-size: 120px;
        font-weight: 900;
        color: #ff6b6b;
        line-height: 1;
        margin-bottom: 20px;
        text-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        background: linear-gradient(45deg, #ff6b6b, #ff8e8e);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
      }

      .error-icon {
        font-size: 80px;
        color: #ff8e8e;
        opacity: 0.8;
      }
    }

    /* ==================== 内容区样式 ==================== */
    /**
     * 错误信息和内容区域的样式
     */
    .error-content {
      .error-title {
        font-size: 32px;
        color: var(--el-text-color-primary);
        margin-bottom: 16px;
        font-weight: 600;
      }

      .error-description {
        font-size: 16px;
        color: var(--el-text-color-regular);
        margin-bottom: 32px;
        line-height: 1.6;
      }

      /* ==================== 权限信息区域 ==================== */
      /**
       * 权限信息模块的样式
       */
      .permission-info {
        text-align: left;
        margin-bottom: 32px;
        padding: 20px;
        background: var(--el-bg-color-light);
        border-radius: 8px;

        h3 {
          font-size: 16px;
          color: var(--el-text-color-primary);
          margin-bottom: 16px;
          text-align: center;
        }

        .info-grid {
          display: grid;
          grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
          gap: 16px;

          .info-item {
            display: flex;
            align-items: center;
            gap: 12px;
            padding: 12px;
            background: #ffffff;
            border-radius: 6px;

            .info-icon {
              font-size: 20px;
              color: var(--el-color-primary);
            }

            .info-content {
              flex: 1;

              .info-title {
                font-size: 12px;
                color: var(--el-text-color-secondary);
                margin-bottom: 4px;
              }

              .info-value {
                font-size: 14px;
                color: var(--el-text-color-primary);
                font-weight: 500;
              }
            }
          }
        }
      }

      /* ==================== 操作按钮区域 ==================== */
      /**
       * 操作按钮区域的样式
       */
      .error-actions {
        margin-bottom: 40px;
        display: flex;
        justify-content: center;
        gap: 16px;
        flex-wrap: wrap;
      }

      /* ==================== 权限申请表单 ==================== */
      /**
       * 权限申请表单区域的样式
       */
      .permission-request {
        text-align: left;
        margin-bottom: 32px;
        padding: 20px;
        background: var(--el-bg-color-light);
        border-radius: 8px;

        h3 {
          font-size: 16px;
          color: var(--el-text-color-primary);
          margin-bottom: 8px;
          text-align: center;
        }

        p {
          font-size: 14px;
          color: var(--el-text-color-regular);
          margin-bottom: 16px;
          text-align: center;
        }

        .request-form {
          max-width: 500px;
          margin: 0 auto;
        }
      }

      /* ==================== 可访问页面区域 ==================== */
      /**
       * 可访问页面列表区域的样式
       */
      .accessible-pages {
        text-align: left;

        h3 {
          font-size: 16px;
          color: var(--el-text-color-primary);
          margin-bottom: 16px;
          text-align: center;
        }

        .page-links {
          display: grid;
          grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
          gap: 12px;

          .page-link {
            display: flex;
            align-items: center;
            gap: 8px;
            padding: 12px 16px;
            background: var(--el-color-success-light-9);
            border-radius: 8px;
            text-decoration: none;
            color: var(--el-text-color-primary);
            transition: all 0.3s;
            font-size: 14px;

            &:hover {
              background: var(--el-color-success-light-8);
              transform: translateY(-2px);
            }

            .fa {
              font-size: 16px;
              color: var(--el-color-success);
            }

            .el-tag {
              margin-left: auto;
            }
          }
        }
      }
    }

    /* ==================== 装饰元素 ==================== */
    /**
     * 背景装饰元素的样式
     */
    .error-decoration {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      pointer-events: none;
      z-index: -1;

      .floating-element {
        position: absolute;
        border-radius: 50%;
        background: linear-gradient(45deg, rgba(255, 107, 107, 0.1), rgba(255, 142, 142, 0.1));
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

  /* ==================== 页脚样式 ==================== */
  /**
   * 页脚信息区域的样式
   */
  .error-footer {
    margin-top: 40px;
    text-align: center;
    color: rgba(255, 255, 255, 0.9);
    font-size: 12px;
    z-index: 2;

    p {
      margin: 4px 0;
    }
  }
}

/* ==================== 动画效果 ==================== */
/**
 * 定义浮动元素的动画效果
 */
@keyframes float {

  0%,
  100% {
    transform: translateY(0px);
  }

  50% {
    transform: translateY(-20px);
  }
}

/* ==================== 响应式设计 ==================== */
/**
 * 移动设备适配的响应式样式
 */
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

        .permission-info .info-grid {
          grid-template-columns: 1fr;
        }

        .accessible-pages .page-links {
          grid-template-columns: 1fr;
        }
      }
    }
  }
}
</style>
