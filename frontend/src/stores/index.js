// 鄂北地区水资源管理系统 - Pinia状态管理入口
// 统一导出所有store模块

import { createPinia } from 'pinia'

// 创建pinia实例
const pinia = createPinia()

// 导出store模块
export { useAuthStore } from './modules/auth'
export { useUserStore } from './modules/user'
export { useAppStore } from './modules/app'

// 导出pinia实例
export default pinia
