// 鄂北地区水资源管理系统 - 应用状态管理
// 管理应用全局状态，如侧边栏、设备类型、面包屑导航等

import { defineStore } from "pinia";

export const useAppStore = defineStore("app", {
	state: () => ({
		// 侧边栏状态
		sidebarCollapsed: false,
		// 设备类型
		device: "desktop", // desktop, tablet, mobile

		// 面包屑导航
		breadcrumbs: [],
		// 页面加载状态
		pageLoading: false,

		// 缓存的视图组件名称列表
		cachedViews: [],
		// 访问过的视图列表
		visitedViews: [],
	}),

	getters: {
		// 是否为移动设备
		isMobile: (state) => state.device === "mobile",

		// 是否为桌面设备
		isDesktop: (state) => state.device === "desktop",
	},

	actions: {
		/**
		 * 切换侧边栏折叠状态
		 */
		toggleSidebar() {
			this.sidebarCollapsed = !this.sidebarCollapsed;
			this.saveSidebarState();
		},

		/**
		 * 保存侧边栏状态到本地存储
		 */
		saveSidebarState() {
			localStorage.setItem(
				"sidebar_collapsed",
				JSON.stringify(this.sidebarCollapsed)
			);
		},

		/**
		 * 从本地存储恢复侧边栏状态
		 */
		restoreSidebarState() {
			const saved = localStorage.getItem("sidebar_collapsed");
			if (saved !== null) {
				this.sidebarCollapsed = JSON.parse(saved);
			}
		},

		/**
		 * 设置设备类型
		 * @param {string} device 设备类型
		 */
		setDevice(device) {
			this.device = device;

			// 移动设备自动折叠侧边栏
			if (device === "mobile") {
				this.sidebarCollapsed = true;
			}
		},

		/**
		 * 根据窗口大小自动设置设备类型
		 */
		autoSetDevice() {
			const width = window.innerWidth;

			if (width < 768) {
				this.setDevice("mobile");
			} else if (width < 992) {
				this.setDevice("tablet");
			} else {
				this.setDevice("desktop");
			}
		},

		/**
		 * 设置面包屑导航
		 * @param {Array} breadcrumbs 面包屑数组
		 */
		setBreadcrumbs(breadcrumbs) {
			this.breadcrumbs = breadcrumbs;
		},

		/**
		 * 设置页面加载状态
		 * @param {boolean} loading 是否加载中
		 */
		setPageLoading(loading) {
			this.pageLoading = loading;
		},

		/**
		 * 恢复应用状态
		 * 应用启动时调用
		 */
		restoreAppState() {
			this.restoreSidebarState();
			this.autoSetDevice();
		},

		/**
		 * 添加访问过的视图
		 * @param {Object} view 视图对象
		 */
		addVisitedView(view) {
			if (this.visitedViews.some((v) => v.path === view.path)) return;
			this.visitedViews.push(
				Object.assign({}, view, {
					title: view.meta?.title || "no-name",
				})
			);
		},
	},
});
