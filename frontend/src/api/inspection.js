import request from "./request";

// ================= 任务相关 =================
export function listTasks(params) {
	return request({ url: "/inspection/tasks", method: "get", params });
}

export function createTask(data) {
	return request({ url: "/inspection/tasks", method: "post", data });
}

export function updateTask(id, data) {
	return request({ url: `/inspection/tasks/${id}`, method: "put", data });
}

export function deleteTask(id) {
	return request({ url: `/inspection/tasks/${id}`, method: "delete" });
}

export function updateTaskStatus(id, status) {
	return request({
		url: `/inspection/tasks/${id}/status`,
		method: "patch",
		params: { status },
	});
}

// ================= 记录相关 =================
export function listRecords(params) {
	return request({ url: "/inspection/records", method: "get", params });
}

export function getRecordDetail(id) {
	return request({ url: `/inspection/records/${id}`, method: "get" });
}

export function getRecordAttachments(id) {
	return request({
		url: `/inspection/records/${id}/attachments`,
		method: "get",
	});
}

export function createRecord(formData) {
	return request({
		url: "/inspection/records",
		method: "post",
		data: formData,
		headers: { "Content-Type": "multipart/form-data" },
	});
}

// 新增：再次处理
export function resolveRecord(id, resolution) {
	return request({
		url: `/inspection/records/${id}/resolve`,
		method: "patch",
		params: { resolution },
	});
}
