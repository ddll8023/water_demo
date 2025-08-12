/**
 * 通用工具函数
 * 提供项目中常用的工具方法
 *
 */

/**
 * 格式化日期时间
 * @param {string|Date} dateTime 日期时间
 * @param {string} format 格式化模式
 * @returns {string} 格式化后的日期时间字符串
 */
export function formatDateTime(dateTime, format = "YYYY-MM-DD HH:mm:ss") {
	if (!dateTime) return "-";

	const date = new Date(dateTime);
	if (isNaN(date.getTime())) return "-";

	const year = date.getFullYear();
	const month = String(date.getMonth() + 1).padStart(2, "0");
	const day = String(date.getDate()).padStart(2, "0");
	const hours = String(date.getHours()).padStart(2, "0");
	const minutes = String(date.getMinutes()).padStart(2, "0");
	const seconds = String(date.getSeconds()).padStart(2, "0");

	return format
		.replace("YYYY", year)
		.replace("MM", month)
		.replace("DD", day)
		.replace("HH", hours)
		.replace("mm", minutes)
		.replace("ss", seconds);
}

/**
 * 将本地时间字符串转换为后端API期望的格式（yyyy-MM-dd HH:mm:ss）
 * @param {string|Date} localTime 本地时间字符串或Date对象
 * @returns {string} 格式化后的时间字符串
 */
export function formatLocalTimeForAPI(localTime) {
	if (!localTime) return null;

	// 转换为Date对象
	const date = new Date(localTime);
	if (isNaN(date.getTime())) return null;

	// 直接格式化为Java后端接受的格式：yyyy-MM-dd HH:mm:ss
	const year = date.getFullYear();
	const month = String(date.getMonth() + 1).padStart(2, "0");
	const day = String(date.getDate()).padStart(2, "0");
	const hours = String(date.getHours()).padStart(2, "0");
	const minutes = String(date.getMinutes()).padStart(2, "0");
	const seconds = String(date.getSeconds()).padStart(2, "0");

	// 返回适合后端LocalDateTime的格式，使用空格分隔日期和时间
	return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}

/**
 * 格式化持续时间
 * @param {string|Date} startTime 开始时间
 * @param {string|Date} endTime 结束时间，默认为当前时间
 * @returns {string} 格式化后的持续时间字符串
 */
export function formatDuration(startTime, endTime = null) {
	if (!startTime) return "-";

	const start = new Date(startTime);
	const end = endTime ? new Date(endTime) : new Date();

	if (isNaN(start.getTime()) || isNaN(end.getTime())) return "-";

	const diffMs = end - start;
	const diffMinutes = Math.floor(diffMs / (1000 * 60));

	if (diffMinutes < 0) return "0分钟";

	if (diffMinutes < 60) {
		return `${diffMinutes}分钟`;
	}

	const hours = Math.floor(diffMinutes / 60);
	const minutes = diffMinutes % 60;

	if (hours < 24) {
		return `${hours}小时${minutes}分钟`;
	}

	const days = Math.floor(hours / 24);
	const remainingHours = hours % 24;

	return `${days}天${remainingHours}小时`;
}
