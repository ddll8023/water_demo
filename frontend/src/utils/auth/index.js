// 鄂北地区水资源管理系统 - 认证工具函数
// 提供令牌管理、权限检查等认证相关工具

// 存储键名常量
const TOKEN_KEY = "water_resources_token";
const REFRESH_TOKEN_KEY = "water_resources_refresh_token";
const CREDENTIALS_KEY = "water_resources_credentials";

// ==================== 令牌管理 ====================

/**
 * 获取访问令牌
 * @returns {string|null} 访问令牌
 */
export function getToken() {
	return localStorage.getItem(TOKEN_KEY);
}

/**
 * 设置访问令牌
 * @param {string} token 访问令牌
 */
export function setToken(token) {
	localStorage.setItem(TOKEN_KEY, token);
}

/**
 * 移除访问令牌
 */
export function removeToken() {
	localStorage.removeItem(TOKEN_KEY);
	localStorage.removeItem(REFRESH_TOKEN_KEY);
}

/**
 * 获取刷新令牌
 * @returns {string|null} 刷新令牌
 */
export function getRefreshToken() {
	return localStorage.getItem(REFRESH_TOKEN_KEY);
}

/**
 * 设置刷新令牌
 * @param {string} refreshToken 刷新令牌
 */
export function setRefreshToken(refreshToken) {
	localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
}

/**
 * 移除刷新令牌
 */
export function removeRefreshToken() {
	localStorage.removeItem(REFRESH_TOKEN_KEY);
}

// ==================== 记住密码功能 ====================

/**
 * 获取记住的登录凭据
 * @returns {Object|null} 登录凭据
 */
export function getRememberedCredentials() {
	try {
		const credentials = localStorage.getItem(CREDENTIALS_KEY);
		return credentials ? JSON.parse(credentials) : null;
	} catch (error) {
		console.error("解析记住的凭据失败:", error);
		return null;
	}
}

/**
 * 设置记住的登录凭据
 * @param {string} username 用户名
 * @param {string} password 密码
 */
export function setRememberedCredentials(username, password) {
	const credentials = { username, password };
	localStorage.setItem(CREDENTIALS_KEY, JSON.stringify(credentials));
}

/**
 * 清除记住的登录凭据
 */
export function clearRememberedCredentials() {
	localStorage.removeItem(CREDENTIALS_KEY);
}

// ==================== 令牌解析和验证 ====================

/**
 * 验证JWT令牌格式
 * @param {string} token JWT令牌
 * @returns {boolean} 是否为有效的JWT格式
 */
export function isValidJWTFormat(token) {
	if (!token || typeof token !== "string") {
		return false;
	}

	// 检查JWT基本格式：应该有3个部分，用.分隔
	const parts = token.split(".");
	if (parts.length !== 3) {
		return false;
	}

	// 检查每个部分是否为有效的Base64字符串
	for (let i = 0; i < parts.length; i++) {
		if (!parts[i] || !/^[A-Za-z0-9+/=_-]*$/.test(parts[i])) {
			return false;
		}
	}

	return true;
}

/**
 * 解析JWT令牌
 * @param {string} token JWT令牌
 * @returns {Object|null} 解析后的载荷
 */
export function parseToken(token) {
	if (!token) return null;

	// 首先验证令牌格式
	if (!isValidJWTFormat(token)) {
		console.error("令牌格式无效");
		return null;
	}

	try {
		const parts = token.split(".");

		// 处理Base64URL编码（JWT标准）
		let base64Payload = parts[1];

		// 替换URL安全字符为标准Base64字符
		base64Payload = base64Payload.replace(/-/g, "+").replace(/_/g, "/");

		// 补齐Base64填充
		base64Payload += "=".repeat((4 - (base64Payload.length % 4)) % 4);

		const payload = JSON.parse(atob(base64Payload));
		return payload;
	} catch (error) {
		console.error("解析令牌失败:", error);
		console.error("令牌内容:", token);
		console.error("令牌部分:", token.split("."));
		return null;
	}
}

/**
 * 检查令牌是否过期
 * @param {string} token JWT令牌
 * @returns {boolean} 是否过期
 */
export function isTokenExpired(token) {
	const payload = parseToken(token);
	if (!payload || !payload.exp) return true;

	return Date.now() >= payload.exp * 1000;
}

/**
 * 获取令牌剩余有效时间（秒）
 * @param {string} token JWT令牌
 * @returns {number} 剩余秒数，-1表示已过期或无效
 */
export function getTokenRemainingTime(token) {
	const payload = parseToken(token);
	if (!payload || !payload.exp) return -1;

	const remainingTime = payload.exp * 1000 - Date.now();
	return remainingTime > 0 ? Math.floor(remainingTime / 1000) : -1;
}

/**
 * 检查令牌是否即将过期
 * @param {string} token JWT令牌
 * @param {number} threshold 提前时间阈值（秒）
 * @returns {boolean} 是否即将过期
 */
export function isTokenExpiringSoon(token, threshold = 300) {
	const remainingTime = getTokenRemainingTime(token);
	return remainingTime !== -1 && remainingTime < threshold;
}

// ==================== 权限检查工具 ====================

/**
 * 检查用户是否有指定权限
 * @param {Array<string>} userPermissions 用户权限列表
 * @param {string} requiredPermission 需要的权限
 * @returns {boolean} 是否有权限
 */
export function hasPermission(userPermissions, requiredPermission) {
	if (!requiredPermission) return true;
	if (!userPermissions || userPermissions.length === 0) return false;

	return userPermissions.includes(requiredPermission);
}

/**
 * 检查用户是否有任一权限
 * @param {Array<string>} userPermissions 用户权限列表
 * @param {Array<string>} requiredPermissions 需要的权限列表
 * @returns {boolean} 是否有任一权限
 */
export function hasAnyPermission(userPermissions, requiredPermissions) {
	if (!requiredPermissions || requiredPermissions.length === 0) return true;
	if (!userPermissions || userPermissions.length === 0) return false;

	return requiredPermissions.some((permission) =>
		userPermissions.includes(permission)
	);
}

/**
 * 检查用户是否有所有权限
 * @param {Array<string>} userPermissions 用户权限列表
 * @param {Array<string>} requiredPermissions 需要的权限列表
 * @returns {boolean} 是否有所有权限
 */
export function hasAllPermissions(userPermissions, requiredPermissions) {
	if (!requiredPermissions || requiredPermissions.length === 0) return true;
	if (!userPermissions || userPermissions.length === 0) return false;

	return requiredPermissions.every((permission) =>
		userPermissions.includes(permission)
	);
}

/**
 * 检查用户是否有指定角色
 * @param {Array<string>} userRoles 用户角色列表
 * @param {string} requiredRole 需要的角色
 * @returns {boolean} 是否有角色
 */
export function hasRole(userRoles, requiredRole) {
	if (!requiredRole) return true;
	if (!userRoles || userRoles.length === 0) return false;

	return userRoles.includes(requiredRole);
}

/**
 * 检查用户是否有任一角色
 * @param {Array<string>} userRoles 用户角色列表
 * @param {Array<string>} requiredRoles 需要的角色列表
 * @returns {boolean} 是否有任一角色
 */
export function hasAnyRole(userRoles, requiredRoles) {
	if (!requiredRoles || requiredRoles.length === 0) return true;
	if (!userRoles || userRoles.length === 0) return false;

	return requiredRoles.some((role) => userRoles.includes(role));
}

/**
 * 检查用户是否有所有角色
 * @param {Array<string>} userRoles 用户角色列表
 * @param {Array<string>} requiredRoles 需要的角色列表
 * @returns {boolean} 是否有所有角色
 */
export function hasAllRoles(userRoles, requiredRoles) {
	if (!requiredRoles || requiredRoles.length === 0) return true;
	if (!userRoles || userRoles.length === 0) return false;

	return requiredRoles.every((role) => userRoles.includes(role));
}

// ==================== 安全工具 ====================

/**
 * 生成随机字符串
 * @param {number} length 字符串长度
 * @returns {string} 随机字符串
 */
export function generateRandomString(length = 32) {
	const chars =
		"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	let result = "";
	for (let i = 0; i < length; i++) {
		result += chars.charAt(Math.floor(Math.random() * chars.length));
	}
	return result;
}

/**
 * 简单的密码强度检查
 * @param {string} password 密码
 * @returns {Object} 强度检查结果
 */
export function checkPasswordStrength(password) {
	const result = {
		score: 0,
		level: "weak",
		suggestions: [],
	};

	if (!password) {
		result.suggestions.push("请输入密码");
		return result;
	}

	// 长度检查
	if (password.length >= 8) {
		result.score += 1;
	} else {
		result.suggestions.push("密码长度至少8位");
	}

	// 包含小写字母
	if (/[a-z]/.test(password)) {
		result.score += 1;
	} else {
		result.suggestions.push("包含小写字母");
	}

	// 包含大写字母
	if (/[A-Z]/.test(password)) {
		result.score += 1;
	} else {
		result.suggestions.push("包含大写字母");
	}

	// 包含数字
	if (/\d/.test(password)) {
		result.score += 1;
	} else {
		result.suggestions.push("包含数字");
	}

	// 包含特殊字符
	if (/[!@#$%^&*(),.?":{}|<>]/.test(password)) {
		result.score += 1;
	} else {
		result.suggestions.push("包含特殊字符");
	}

	// 确定强度等级
	if (result.score >= 4) {
		result.level = "strong";
	} else if (result.score >= 3) {
		result.level = "medium";
	} else {
		result.level = "weak";
	}

	return result;
}

/**
 * 获取设备指纹（简单版本）
 * @returns {string} 设备指纹
 */
export function getDeviceFingerprint() {
	const canvas = document.createElement("canvas");
	const ctx = canvas.getContext("2d");
	ctx.textBaseline = "top";
	ctx.font = "14px Arial";
	ctx.fillText("Device fingerprint", 2, 2);

	const fingerprint = [
		navigator.userAgent,
		navigator.language,
		screen.width + "x" + screen.height,
		new Date().getTimezoneOffset(),
		canvas.toDataURL(),
	].join("|");

	// 简单哈希
	let hash = 0;
	for (let i = 0; i < fingerprint.length; i++) {
		const char = fingerprint.charCodeAt(i);
		hash = (hash << 5) - hash + char;
		hash = hash & hash; // 转换为32位整数
	}

	return Math.abs(hash).toString(36);
}

/**
 * 安全存储敏感数据
 * @param {string} key 存储键
 * @param {any} value 存储值
 * @param {boolean} encrypt 是否加密
 */
export function secureStorage(key, value, encrypt = false) {
	try {
		let data = value;

		if (encrypt && typeof value === "string") {
			// 简单的Base64编码（实际项目中应使用更安全的加密方式）
			data = btoa(value);
		}

		if (typeof data === "object") {
			data = JSON.stringify(data);
		}

		localStorage.setItem(key, data);
	} catch (error) {
		console.error("安全存储失败:", error);
	}
}

/**
 * 安全读取敏感数据
 * @param {string} key 存储键
 * @param {boolean} decrypt 是否解密
 * @returns {any} 存储值
 */
export function secureRetrieve(key, decrypt = false) {
	try {
		let data = localStorage.getItem(key);

		if (!data) return null;

		if (decrypt) {
			// 简单的Base64解码
			data = atob(data);
		}

		try {
			return JSON.parse(data);
		} catch {
			return data;
		}
	} catch (error) {
		console.error("安全读取失败:", error);
		return null;
	}
}
