import { createApp } from "vue";
import { createPinia } from "pinia";

// Element Plus
import ElementPlus from "element-plus";
import "element-plus/dist/index.css";

// 全局样式 - 使用统一入口避免重复导入
import "@/assets/styles/index.scss";

import App from "./App.vue";
import router from "./router";
import pinia, { useAuthStore, useAppStore } from "./stores";
import { setupPermissionDirectives } from "@/directives/permission";

// 创建应用实例
const app = createApp(App);

// 注意：已移除Element Plus图标注册，改用Font Awesome 4.7.0

// 使用插件
app.use(pinia);
app.use(ElementPlus);
app.use(router);

// 设置权限指令
setupPermissionDirectives(app);

// 应用启动时恢复状态
const authStore = useAuthStore();
const appStore = useAppStore();

// 恢复应用状态
appStore.restoreAppState();

// 监听窗口大小变化
window.addEventListener("resize", () => {
	appStore.autoSetDevice();
});

// 初始设置设备类型
appStore.autoSetDevice();

// 应用启动后初始化认证状态
app.mount("#app");
// 异步初始化认证状态（避免阻塞应用启动）
setTimeout(() => {
	// 在初始化前先检查本地存储的令牌格式
	const storedToken = localStorage.getItem("water_resources_token");
	if (storedToken) {
		// 简单的JWT格式检查
		const parts = storedToken.split(".");
		if (
			parts.length !== 3 ||
			!parts.every((part) => part && /^[A-Za-z0-9+/=_-]*$/.test(part))
		) {
			console.warn("检测到无效的令牌格式，清除本地存储");
			localStorage.removeItem("water_resources_token");
			localStorage.removeItem("water_resources_refresh_token");
		}
	}

	authStore.initializeAuth().catch((error) => {
		console.error("认证状态初始化失败:", error);
	});
}, 100); // 延迟100ms确保应用完全挂载
