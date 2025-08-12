<template>
  <div class="login-container">
    <div class="login-card">
      <!-- Logo和标题区域 -->
      <div class="login-header">
        <div class="login-logo">
          <img src="@/assets/images/logo.svg" alt="系统Logo" />
        </div>
        <h2 class="login-title">鄂北地区水资源管理系统</h2>
        <p class="login-subtitle">请输入您的账号和密码</p>
      </div>

      <!-- 登录表单 -->
      <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" class="login-form" @keyup.enter="handleLogin">
        <el-form-item prop="username">
          <CustomInput v-model="loginForm.username" placeholder="请输入用户名" size="large" clearable :disabled="loading"
            prefix-icon="fa-user" />
        </el-form-item>

        <el-form-item prop="password">
          <CustomInput v-model="loginForm.password" type="password" placeholder="请输入密码" size="large" show-password
            clearable :disabled="loading" prefix-icon="fa-lock" />
        </el-form-item>

        <el-form-item>
          <div class="login-options">
            <el-checkbox v-model="rememberMe" :disabled="loading">
              记住密码
            </el-checkbox>
            <el-link type="primary" :disabled="loading">
              忘记密码？
            </el-link>
          </div>
        </el-form-item>

        <el-form-item>
          <CustomButton type="primary" size="large" :loading="loading" @click="handleLogin" class="login-button">
            {{ loading ? '登录中...' : '登录' }}
          </CustomButton>
        </el-form-item>
      </el-form>

      <!-- 底部信息 -->
      <div class="login-footer">
        <p class="copyright">© 2025 鄂北地区水资源管理系统</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import CustomInput from '@/components/Common/CustomInput.vue'
import CustomButton from '@/components/Common/CustomButton.vue'
import { useAuthStore } from '@/stores/modules/auth'
import {
  getRememberedCredentials,
  setRememberedCredentials,
  clearRememberedCredentials
} from '@/utils/auth'

/**
 * ----------------------------------------
 * 组件状态和引用
 * ----------------------------------------
 */
// 路由相关
const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

// 表单引用
const loginFormRef = ref()

// 加载状态
const loading = ref(false)

// 记住密码状态
const rememberMe = ref(false)

/**
 * ----------------------------------------
 * 表单配置
 * ----------------------------------------
 */
// 登录表单数据
const loginForm = reactive({
  username: '',
  password: ''
})

// 表单验证规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' },
    {
      pattern: /^[a-zA-Z0-9_]+$/,
      message: '用户名只能包含字母、数字和下划线',
      trigger: 'blur'
    }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

/**
 * ----------------------------------------
 * 事件处理方法
 * ----------------------------------------
 */
/**
 * 处理登录
 */
const handleLogin = async () => {
  if (!loginFormRef.value) return

  try {
    // 表单验证
    await loginFormRef.value.validate()

    loading.value = true

    // 调用登录API
    await authStore.login({
      username: loginForm.username.trim(),
      password: loginForm.password
    })

    // 处理记住密码
    if (rememberMe.value) {
      setRememberedCredentials(loginForm.username, loginForm.password)
    } else {
      clearRememberedCredentials()
    }

    ElMessage.success('登录成功')

    // 跳转到目标页面或首页
    const redirect = route.query.redirect || '/'
    await nextTick()
    router.push(redirect)

  } catch (error) {
    console.error('登录失败:', error)

    // 根据错误类型显示不同的提示信息
    let errorMessage = '登录失败，请重试'

    if (error.response) {
      const { status, data } = error.response
      switch (status) {
        case 401:
          errorMessage = '用户名或密码错误'
          break
        case 403:
          errorMessage = '账户已被禁用，请联系管理员'
          break
        case 429:
          errorMessage = '登录尝试过于频繁，请稍后再试'
          break
        case 500:
          errorMessage = '服务器错误，请稍后再试'
          break
        default:
          errorMessage = data?.message || '登录失败，请重试'
      }
    } else if (error.code === 'NETWORK_ERROR') {
      errorMessage = '网络连接失败，请检查网络设置'
    }

    ElMessage.error(errorMessage)
  } finally {
    loading.value = false
  }
}

/**
 * ----------------------------------------
 * 生命周期钩子和初始化
 * ----------------------------------------
 */
/**
 * 初始化页面
 */
const initializePage = () => {
  // 如果已经登录，直接跳转
  if (authStore.isAuthenticated) {
    const redirect = route.query.redirect || '/'
    router.push(redirect)
    return
  }

  // 加载记住的凭据
  loadRememberedCredentials()

  // 聚焦到用户名输入框
  focusUsernameInput()
}

/**
 * 加载记住的凭据
 */
const loadRememberedCredentials = () => {
  const credentials = getRememberedCredentials()
  if (credentials) {
    loginForm.username = credentials.username
    loginForm.password = credentials.password
    rememberMe.value = true
  }
}

/**
 * 聚焦到用户名输入框
 */
const focusUsernameInput = () => {
  nextTick(() => {
    if (loginFormRef.value) {
      const usernameInput = loginFormRef.value.$el.querySelector('input[type="text"]')
      if (usernameInput && !loginForm.username) {
        usernameInput.focus()
      }
    }
  })
}

// 组件挂载后执行
onMounted(() => {
  initializePage()
})
</script>

<style scoped lang="scss">
/**
 * ----------------------------------------
 * 页面基本样式
 * ----------------------------------------
 */
.login-container {
  background: #f5f5f5;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;

  /**
   * 登录卡片样式
   */
  .login-card {
    width: 400px;
    background: #ffffff;
    border-radius: 8px;
    border: 1px solid #e4e7ed;
    padding: 32px;

    /**
     * 登录头部样式
     */
    .login-header {
      text-align: center;
      margin-bottom: 24px;

      .login-logo {
        margin-bottom: 12px;

        img {
          width: 48px;
          height: 48px;
          border-radius: 4px;
        }
      }

      .login-title {
        font-size: 20px;
        color: #303133;
        font-weight: 500;
        margin: 0 0 6px 0;
        line-height: 1.2;
      }

      .login-subtitle {
        color: #909399;
        font-size: 13px;
        margin: 0;
        line-height: 1.4;
      }
    }

    /**
     * 登录表单样式
     */
    .login-form {
      .login-options {
        display: flex;
        justify-content: space-between;
        align-items: center;
        width: 100%;
        margin-bottom: 6px;

        .el-checkbox {
          color: #606266;
        }
      }

      .login-button {
        width: 100%;
        height: 44px;
        font-size: 14px;
        font-weight: 400;
        border-radius: 4px;
        margin-top: 6px;
      }
    }

    /**
     * 登录底部样式
     */
    .login-footer {
      text-align: center;
      margin-top: 20px;

      .copyright {
        color: #c0c4cc;
        font-size: 12px;
        margin: 0;
      }
    }
  }
}

/**
 * ----------------------------------------
 * 响应式设计
 * ----------------------------------------
 */
@media (max-width: 768px) {
  .login-container {
    padding: 16px;

    .login-card {
      width: 100%;
      max-width: 360px;
      padding: 24px 20px;

      .login-header {
        margin-bottom: 20px;

        .login-title {
          font-size: 18px;
        }
      }
    }
  }
}
</style>
