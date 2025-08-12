// 鄂北地区水资源管理系统 - 主路由配置
// 基于Vue Router 4的路由管理

import { createRouter, createWebHistory } from "vue-router";

// 基础路由配置
const routes = [
	{
		path: "/login",
		name: "Login",
		component: () => import("@/views/auth/Login.vue"),
		meta: {
			title: "登录",
			requiresAuth: false,
			hideInMenu: true,
		},
	},
	{
		path: "/",
		component: () => import("@/components/Layout/AppLayout.vue"),
		redirect: "/introduction",
		children: [
			{
				path: "introduction",
				name: "Introduction",
				component: () => import("@/views/introduction/Introduction.vue"),
				meta: {
					title: "工程简介",
					icon: "fa-file-text-o",
					requiresAuth: true,
					permission: "data:view",
				},
			},

			// 预警管理 - 使用标签页结构
			{
				path: "warning",
				name: "WarningManagement",
				component: () => import("@/views/warning/WarningManagement.vue"),
				meta: {
					title: "预警管理",
					icon: "fa-exclamation-triangle",
					requiresAuth: true,
					permission: "business:operate",
				},
			},
			// 工程巡检模块
			{
				path: "inspection",
				name: "Inspection",
				component: () => import("@/views/inspection/InspectionManagement.vue"),
				meta: {
					title: "工程巡检",
					icon: "fa-search",
					requiresAuth: true,
					permission: "business:operate",
				},
			},
			// 实时监测模块 - 使用平级路由结构，符合设计规范
			{
				path: "monitoring",
				redirect: "/monitoring/flow", // 默认重定向到流量检测
				meta: {
					title: "实时监测",
					icon: "fa-line-chart",
					requiresAuth: true,
					permission: "data:view",
				},
			},
			{
				path: "monitoring/flow",
				name: "FlowMonitoring",
				component: () => import("@/views/monitoring/FlowMonitoring.vue"),
				meta: {
					title: "流量检测",
					icon: "fa-tint",
					requiresAuth: true,
					permission: "data:view",
					group: "实时监测",
				},
			},
			{
				path: "monitoring/water-level",
				name: "WaterLevelMonitoring",
				component: () => import("@/views/monitoring/WaterLevelMonitoring.vue"),
				meta: {
					title: "水位监测",
					icon: "fa-bar-chart",
					requiresAuth: true,
					permission: "data:view",
					group: "实时监测",
				},
			},
			{
				path: "monitoring/water-quality",
				name: "WaterQualityMonitoring",
				component: () =>
					import("@/views/monitoring/WaterQualityMonitoring.vue"),
				meta: {
					title: "水质检测",
					icon: "fa-flask",
					requiresAuth: true,
					permission: "data:view",
					group: "实时监测",
				},
			},
			{
				path: "monitoring/water-condition",
				name: "WaterConditionMonitoring",
				component: () =>
					import("@/views/monitoring/WaterConditionMonitoring.vue"),
				meta: {
					title: "水情监测",
					icon: "fa-database",
					requiresAuth: true,
					permission: "data:view",
					group: "实时监测",
				},
			},
			{
				path: "monitoring/rainfall",
				name: "RainfallMonitoring",
				component: () => import("@/views/monitoring/RainfallMonitoring.vue"),
				meta: {
					title: "雨情监测",
					icon: "fa-umbrella",
					requiresAuth: true,
					permission: "data:view",
					group: "实时监测",
				},
			},
			{
				path: "management",
				name: "ManagementService",
				component: () => import("@/views/management/ManagementService.vue"),
				meta: {
					title: "管理信息服务",
					icon: "fa-sitemap",
					requiresAuth: true,
					permission: "business:manage",
				},
			},
			{
				path: "engineering",
				name: "EngineeringService",
				component: () => import("@/views/engineering/EngineeringService.vue"),
				meta: {
					title: "工程信息服务",
					icon: "fa-wrench",
					requiresAuth: true,
					permission: "business:manage",
				},
			},
			{
				path: "map",
				name: "MapOverview",
				component: () => import("@/views/map/MapOverview.vue"),
				meta: {
					title: "一张图",
					icon: "fa-map",
					requiresAuth: true,
					permission: "business:operate",
				},
			},
			// 系统管理模块 - 改为平级路由结构，符合设计规范
			{
				path: "system",
				redirect: "/system/users", // 默认重定向到用户管理
				meta: {
					title: "系统管理",
					icon: "fa-cog",
					requiresAuth: true,
					permission: "system:manage",
				},
			},
			{
				path: "system/regions",
				name: "RegionManagement",
				component: () => import("@/views/system/RegionManagement.vue"),
				meta: {
					title: "行政区划管理",
					icon: "fa-map-o",
					requiresAuth: true,
					permission: "system:manage",
					group: "系统管理",
				},
			},
			{
				path: "system/organizations",
				name: "OrganizationManagement",
				component: () => import("@/views/system/OrganizationManagement.vue"),
				meta: {
					title: "组织机构管理",
					icon: "fa-sitemap",
					requiresAuth: true,
					permission: "system:manage",
					group: "系统管理",
				},
			},
			{
				path: "system/departments",
				name: "DepartmentManagement",
				component: () => import("@/views/system/DepartmentManagement.vue"),
				meta: {
					title: "部门管理",
					icon: "fa-building-o",
					requiresAuth: true,
					permission: "system:manage",
					group: "系统管理",
				},
			},
			{
				path: "system/positions",
				name: "PositionManagement",
				component: () => import("@/views/system/PositionManagement.vue"),
				meta: {
					title: "岗位管理",
					icon: "fa-briefcase",
					requiresAuth: true,
					permission: "system:manage",
					group: "系统管理",
				},
			},
			{
				path: "system/roles",
				name: "RoleManagement",
				component: () => import("@/views/system/RoleManagement.vue"),
				meta: {
					title: "角色管理",
					icon: "fa-users",
					requiresAuth: true,
					permission: "system:manage",
					group: "系统管理",
				},
			},
			{
				path: "system/users",
				name: "UserManagement",
				component: () => import("@/views/system/UserManagement.vue"),
				meta: {
					title: "用户管理",
					icon: "fa-user",
					requiresAuth: true,
					permission: "system:manage",
					group: "系统管理",
				},
			},
			{
				path: "system/dictionaries",
				name: "DictionaryManagement",
				component: () => import("@/views/system/DictionaryManagement.vue"),
				meta: {
					title: "字典管理",
					icon: "fa-book",
					requiresAuth: true,
					permission: "system:manage",
					group: "系统管理",
				},
			},
			{
				path: "system/resources",
				name: "ResourceManagement",
				component: () => import("@/views/system/ResourceManagement.vue"),
				meta: {
					title: "资源管理",
					icon: "fa-folder-open-o",
					requiresAuth: true,
					permission: "system:manage",
					group: "系统管理",
				},
			},
		],
	},
	{
		path: "/403",
		name: "Forbidden",
		component: () => import("@/views/error/403.vue"),
		meta: {
			title: "访问被拒绝",
			hideInMenu: true,
		},
	},
	{
		path: "/404",
		name: "NotFound",
		component: () => import("@/views/error/404.vue"),
		meta: {
			title: "页面不存在",
			hideInMenu: true,
		},
	},
	{
		path: "/:pathMatch(.*)*",
		redirect: "/404",
	},
];

// 创建路由实例
const router = createRouter({
	history: createWebHistory(),
	routes,
	scrollBehavior(to, from, savedPosition) {
		if (savedPosition) {
			return savedPosition;
		} else {
			return { top: 0 };
		}
	},
});

// 路由守卫
router.beforeEach(async (to, from, next) => {
	// 动态导入store，避免循环依赖
	const { useAuthStore } = await import("@/stores/modules/auth");
	const { useAppStore } = await import("@/stores/modules/app");

	const authStore = useAuthStore();
	const appStore = useAppStore();

	// 设置页面加载状态
	appStore.setPageLoading(true);

	// 设置页面标题
	document.title = to.meta?.title
		? `${to.meta.title} - 鄂北地区水资源管理系统`
		: "鄂北地区水资源管理系统";

	// 生成面包屑
	generateBreadcrumbs(to, appStore);

	// 白名单路由（不需要认证）
	const whiteList = ["/login", "/404", "/403"];

	// 检查是否需要认证
	if (to.meta?.requiresAuth !== false && !whiteList.includes(to.path)) {
		// 检查是否已登录
		if (!authStore.token) {
			// 保存目标路由，登录后跳转
			next(`/login?redirect=${encodeURIComponent(to.fullPath)}`);
			return;
		}

		// 检查用户信息是否存在
		if (!authStore.userInfo) {
			try {
				// 尝试获取用户信息
				await authStore.getUserInfo();
			} catch (error) {
				console.error("获取用户信息失败:", error);
				// 清除无效的认证状态
				authStore.clearAuthData();
				next(`/login?redirect=${encodeURIComponent(to.fullPath)}`);
				return;
			}
		}

		// 检查Token是否即将过期
		if (authStore.shouldRefreshToken && authStore.shouldRefreshToken()) {
			try {
				await authStore.refreshAccessToken();
			} catch (error) {
				console.error("Token刷新失败:", error);
				authStore.clearAuthData();
				next(`/login?redirect=${encodeURIComponent(to.fullPath)}`);
				return;
			}
		}

		// 检查页面权限
		if (to.meta?.permission && !authStore.hasPermission(to.meta.permission)) {
			next("/403");
			return;
		}

		// 检查角色权限
		if (to.meta?.roles && !authStore.hasAnyRole(to.meta.roles)) {
			next("/403");
			return;
		}
	}

	// 如果已登录用户访问登录页，重定向到首页
	if (to.path === "/login" && authStore.isAuthenticated) {
		next("/");
		return;
	}

	// 添加访问过的视图
	if (to.meta?.title && authStore.isAuthenticated) {
		appStore.addVisitedView({
			name: to.name,
			path: to.path,
			fullPath: to.fullPath,
			meta: to.meta,
		});
	}

	next();
});

// 路由后置守卫
router.afterEach(async (to, from) => {
	// 动态导入store
	const { useAppStore } = await import("@/stores/modules/app");
	const appStore = useAppStore();

	// 关闭页面加载状态
	appStore.setPageLoading(false);

	// 移除页面访问统计调试信息

	// 滚动到顶部
	window.scrollTo(0, 0);
});

/**
 * 生成面包屑导航
 * @param {Object} route 当前路由
 * @param {Object} appStore 应用状态管理
 */
function generateBreadcrumbs(route, appStore) {
	const breadcrumbs = [];
	const matched = route.matched.filter((item) => item.meta && item.meta.title);

	matched.forEach((item) => {
		breadcrumbs.push({
			title: item.meta.title,
			path: item.path,
			name: item.name,
		});
	});

	appStore.setBreadcrumbs(breadcrumbs);
}

export default router;
