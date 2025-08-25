// 鄂北地区水资源管理系统 - 认证状态管理
// 管理用户登录状态、令牌和权限信息

import { defineStore } from "pinia";
import { login, logout, getCurrentUser } from "@/api/auth";
import { getToken, setToken, removeToken, parseToken } from "@/utils/auth";

export const useAuthStore = defineStore("auth", {
	state: () => ({
		// 访问令牌
		token: getToken(),

		// 用户信息
		userInfo: null,
		// 用户权限列表
		permissions: [],
		// 用户角色列表
		rolename: "",
		// 登录状态
		isLoggedIn: false,
	}),

	getters: {
		// 是否已登录
		isAuthenticated: (state) => !!state.token && !!state.userInfo,

		// 用户ID
		userId: (state) => state.userInfo?.id,

		// 用户名
		username: (state) => state.userInfo?.username,

		// 用户全名
		fullName: (state) => state.userInfo?.fullName,

		// 用户头像
		avatar: (state) => state.userInfo?.avatar,

		// 检查是否有指定权限
		hasPermission: (state) => (permission) => {
			if (!permission) return true;
			// 安全检查：确保permissions是数组
			const permissions = Array.isArray(state.permissions)
				? state.permissions
				: [];
			return permissions.includes(permission);
		},

		// 检查是否有任一权限
		hasAnyPermission: (state) => (permissions) => {
			if (!permissions || permissions.length === 0) return true;
			// 安全检查：确保state.permissions是数组
			const userPermissions = Array.isArray(state.permissions)
				? state.permissions
				: [];
			return permissions.some((permission) =>
				userPermissions.includes(permission)
			);
		},

		// 检查是否有所有权限
		hasAllPermissions: (state) => (permissions) => {
			if (!permissions || permissions.length === 0) return true;
			// 安全检查：确保state.permissions是数组
			const userPermissions = Array.isArray(state.permissions)
				? state.permissions
				: [];
			return permissions.every((permission) =>
				userPermissions.includes(permission)
			);
		},
	},

	actions: {
		/**
		 * 用户登录
		 * @param {Object} loginForm 登录表单数据
		 * @returns {Promise} 登录结果
		 */
		async login(loginForm) {
			try {
				const data = await login(loginForm);

				// 保存令牌
				this.token = data.accessToken;
				setToken(data.accessToken);

				// 保存用户信息
				this.userInfo = data.userInfo;
				this.permissions = data.permissions || [];
				this.rolename = data.rolename;
				this.isLoggedIn = true;

				return data;
			} catch (error) {
				console.error("登录失败:", error);
				throw error;
			}
		},

		/**
		 * 用户登出
		 */
		async logout() {
			try {
				// 调用登出API
				if (this.token) {
					await logout();
				}
			} catch (error) {
				console.error("登出API调用失败:", error);
			} finally {
				// 清除本地状态
				this.clearAuthData();
			}
		},

		/**
		 * 获取用户信息
		 * @returns {Promise} 用户信息
		 */
		async getUserInfo() {
			try {
				const data = await getCurrentUser();

				// 处理后端返回的数据结构
				if (data && typeof data === "object") {
					// 如果data直接包含用户信息字段
					if (data.id || data.username) {
						this.userInfo = data;
						this.permissions = data.permissions || [];
						this.roles = data.roles || [];
					}
					// 如果data包含userInfo字段
					else if (data.userInfo) {
						this.userInfo = data.userInfo;
						this.permissions = data.permissions || [];
						this.roles = data.roles || [];
					}
					// 其他情况，尝试直接使用data
					else {
						this.userInfo = data;
						this.permissions = [];
						this.roles = [];
					}
				} else {
					throw new Error("用户信息格式无效");
				}

				this.isLoggedIn = true;

				return {
					userInfo: this.userInfo,
					permissions: this.permissions,
					roles: this.roles,
				};
			} catch (error) {
				console.error("获取用户信息失败:", error);
				// 如果获取用户信息失败，清除认证状态
				this.clearAuthData();
				throw error;
			}
		},

		/**
		 * 清除认证数据
		 */
		clearAuthData() {
			this.token = null;
			this.userInfo = null;
			this.permissions = [];
			this.roles = [];
			this.isLoggedIn = false;

			// 清除本地存储
			removeToken();
		},
		/**
		 * 验证JWT令牌格式
		 * @param {string} token JWT令牌
		 * @returns {boolean} 是否为有效的JWT格式
		 */
		isValidJWTFormat(token) {
			if (!token || typeof token !== "string") {
				console.debug("令牌为空或不是字符串");
				return false;
			}

			// 检查JWT基本格式：应该有3个部分，用.分隔
			const parts = token.split(".");
			if (parts.length !== 3) {
				console.debug("令牌格式无效，部分数量:", parts.length);
				return false;
			}

			// 检查每个部分是否为有效的Base64字符串
			for (let i = 0; i < parts.length; i++) {
				if (!parts[i] || !/^[A-Za-z0-9+/=_-]*$/.test(parts[i])) {
					console.debug(`令牌第${i + 1}部分格式无效:`, parts[i]);
					return false;
				}
			}

			return true;
		},

		/**
		 * 初始化认证状态
		 * 应用启动时调用，检查本地存储的令牌
		 */
		async initializeAuth() {
			console.debug("开始初始化认证状态");

			if (!this.token) {
				console.debug("没有令牌，跳过认证状态初始化");
				return;
			}

			// 首先验证令牌格式
			if (!this.isValidJWTFormat(this.token)) {
				console.error("存储的令牌格式无效，清除认证状态");
				this.clearAuthData();
				return;
			}

			try {
				console.debug("令牌格式有效，继续初始化");

				// 直接使用现有令牌进行认证
				if (!this.token) {
					console.debug("令牌已被清除，停止初始化");
					return;
				}

				// 获取用户信息
				console.debug("开始获取用户信息");
				await this.getUserInfo();
				console.debug("用户信息获取成功，认证状态初始化完成");
			} catch (error) {
				console.error("初始化认证状态失败:", error);
				// 初始化失败时清除认证状态，但不抛出错误
				this.clearAuthData();
			}
		},
	},
});
