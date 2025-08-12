// 鄂北地区水资源管理系统 - HTTP请求配置
// 基于Axios的统一请求处理

import axios from "axios";
import { ElMessage, ElMessageBox } from "element-plus";
import { useAuthStore } from "@/stores/modules/auth";
import router from "@/router";

// 创建axios实例
const request = axios.create({
	baseURL: "/api",
	timeout: 10000,
	headers: {
		"Content-Type": "application/json",
	},
});

// 请求拦截器
request.interceptors.request.use(
	(config) => {
		// 添加认证Token
		const authStore = useAuthStore();
		if (authStore.token) {
			config.headers.Authorization = `Bearer ${authStore.token}`;
		}

		// 添加请求时间戳（防止缓存）
		if (config.method === "get") {
			config.params = {
				...config.params,
				_t: Date.now(),
			};
		}

		// HTTP请求验证
		if (config.method === "post" && config.url.includes("/warning/records")) {
			console.log(
				"HTTP请求验证 - Content-Type:",
				config.headers["Content-Type"]
			);
		}

		return config;
	},
	(error) => {
		console.error("请求拦截器错误:", error);
		return Promise.reject(error);
	}
);

// 响应拦截器
request.interceptors.response.use(
	(response) => {
		// 对于二进制数据响应，直接返回原始数据
		const contentType = response.headers["content-type"];
		if (
			response.config.responseType === "blob" ||
			(contentType && contentType.includes("application/octet-stream")) ||
			(contentType && contentType.includes("application/vnd.ms-excel")) ||
			(contentType && contentType.includes("application/vnd.openxmlformats"))
		) {
			return response.data;
		}

		// 处理业务响应
		const { data } = response;
		if (data.code === 200) {
			return data.data;
		} else {
			// 业务错误处理
			const message = data.message || "请求失败";
			ElMessage.error(message);
			return Promise.reject(new Error(message));
		}
	},
	(error) => {
		// 网络错误处理
		console.error("响应拦截器错误:", error);

		if (error.response) {
			const { status, data } = error.response;

			switch (status) {
				case 401:
					// 未授权 - 尝试刷新Token或跳转到登录页
					return handleUnauthorized(error)
						.then((token) => {
							if (token) {
								// Token刷新成功，重新发送原请求
								error.config.headers.Authorization = `Bearer ${token}`;
								return request(error.config);
							} else {
								return Promise.reject(error);
							}
						})
						.catch(() => {
							// Token刷新失败，返回原错误
							return Promise.reject(error);
						});
					break;
				case 400:
					// 400错误详细信息
					if (error.config?.url?.includes("/warning/records")) {
						console.log("400错误:", data?.message);
						console.log("验证错误:", data?.data);
					}

					// 客户端错误 - 对于导入失败等业务错误，返回详细信息而不是直接显示错误消息
					if (data && data.data) {
						// 如果有详细数据（如导入结果），返回完整响应供业务层处理
						return Promise.reject({
							response: error.response,
							businessError: true,
							data: data,
						});
					} else {
						// 普通的400错误
						const message = data?.message || "请求参数错误";
						ElMessage.error(message);
					}
					break;
				case 403:
					// 禁止访问
					ElMessage.error("没有权限访问该资源");
					break;
				case 404:
					// 资源不存在
					ElMessage.error("请求的资源不存在");
					break;
				case 422:
					// 表单验证错误
					handleValidationError(data);
					break;
				case 500:
					// 服务器错误
					ElMessage.error("服务器内部错误，请稍后重试");
					break;
				case 502:
				case 503:
				case 504:
					// 服务不可用
					ElMessage.error("服务暂时不可用，请稍后重试");
					break;
				default:
					// 其他错误
					const message = data?.message || error.message || "网络错误";
					ElMessage.error(message);
			}
		} else if (error.code === "ECONNABORTED") {
			// 请求超时
			ElMessage.error("请求超时，请检查网络连接");
		} else {
			// 网络连接错误
			ElMessage.error("网络连接失败，请检查网络设置");
		}

		return Promise.reject(error);
	}
);

// Token刷新状态
let isRefreshing = false;
let failedQueue = [];

// 处理队列中的请求
function processQueue(error, token = null) {
	failedQueue.forEach(({ resolve, reject }) => {
		if (error) {
			reject(error);
		} else {
			resolve(token);
		}
	});

	failedQueue = [];
}

// 处理未授权错误
async function handleUnauthorized(originalError) {
	const authStore = useAuthStore();

	// 如果是刷新令牌的请求失败，直接跳转登录页
	if (originalError.config?.url?.includes("/auth/refresh")) {
		console.error("刷新令牌请求失败，跳转登录页");
		authStore.logout();
		router.push("/login");
		return null;
	}

	// 如果正在刷新Token，将请求加入队列
	if (isRefreshing) {
		return new Promise((resolve, reject) => {
			failedQueue.push({ resolve, reject });
		});
	}

	// 尝试刷新Token
	if (authStore.refreshToken && !isRefreshing) {
		isRefreshing = true;

		try {
			console.debug("开始刷新令牌");
			const result = await authStore.refreshAccessToken();
			console.debug("令牌刷新成功");

			processQueue(null, authStore.token);
			return authStore.token;
		} catch (error) {
			console.error("令牌刷新失败:", error);
			processQueue(error, null);

			// 刷新失败，提示用户重新登录
			ElMessageBox.confirm("登录状态已过期，请重新登录", "提示", {
				confirmButtonText: "重新登录",
				cancelButtonText: "取消",
				type: "warning",
			})
				.then(() => {
					authStore.logout();
					router.push("/login");
				})
				.catch(() => {
					// 用户取消
				});

			return null;
		} finally {
			isRefreshing = false;
		}
	} else {
		// 没有刷新Token，直接跳转登录页
		console.debug("没有刷新令牌，跳转登录页");
		authStore.logout();
		router.push("/login");
		return null;
	}
}

// 处理表单验证错误
function handleValidationError(data) {
	if (data && data.errors) {
		// 显示第一个验证错误
		const firstError = Object.values(data.errors)[0];
		if (Array.isArray(firstError) && firstError.length > 0) {
			ElMessage.error(firstError[0]);
		}
	} else {
		ElMessage.error(data?.message || "表单验证失败");
	}
}

// 导出请求实例和工具函数
export default request;
