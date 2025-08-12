// 鄂北地区水资源管理系统 - 用户状态管理
// 管理用户相关的状态和操作

import { defineStore } from "pinia";
import {
	getUserList,
	createUser,
	updateUser,
	deleteUser,
} from "@/api/user";

export const useUserStore = defineStore("user", {
	state: () => ({
		// 用户列表
		userList: [],
		// 用户列表总数
		userTotal: 0,
		// 当前页码
		currentPage: 1,
		// 每页大小
		pageSize: 10,
		// 加载状态
		loading: false,
		// 搜索条件
		searchParams: {},
		// 当前选中的用户
		selectedUser: null,
		// 用户详情
		userDetail: null,
	}),

	getters: {
		// 获取用户列表
		getUserListData: (state) => state.userList,

		// 获取分页信息
		getPaginationInfo: (state) => ({
			current: state.currentPage,
			pageSize: state.pageSize,
			total: state.userTotal,
		}),

		// 是否有用户数据
		hasUserData: (state) => state.userList.length > 0,

		// 获取启用的用户数量
		enabledUserCount: (state) =>
			state.userList.filter((user) => user.isActive).length,

		// 获取禁用的用户数量
		disabledUserCount: (state) =>
			state.userList.filter((user) => !user.isActive).length,
	},

	actions: {
		/**
		 * 获取用户列表
		 * @param {Object} params 查询参数
		 */
		async fetchUserList(params = {}) {
			this.loading = true;
			try {
				const queryParams = {
					page: this.currentPage,
					size: this.pageSize,
					...this.searchParams,
					...params,
				};

				const data = await getUserList(queryParams);

				this.userList = data.items || [];
				this.userTotal = data.total || 0;
				this.currentPage = data.page || 1;
				this.pageSize = data.size || 10;

				return data;
			} catch (error) {
				console.error("获取用户列表失败:", error);
				throw error;
			} finally {
				this.loading = false;
			}
		},
		/**
		 * 创建用户
		 * @param {Object} userData 用户数据
		 */
		async createUser(userData) {
			try {
				const data = await createUser(userData);

				// 刷新用户列表
				await this.fetchUserList();

				return data;
			} catch (error) {
				console.error("创建用户失败:", error);
				throw error;
			}
		},

		/**
		 * 更新用户
		 * @param {number} id 用户ID
		 * @param {Object} userData 用户数据
		 */
		async updateUser(id, userData) {
			try {
				const data = await updateUser(id, userData);

				// 更新本地用户列表中的数据
				const index = this.userList.findIndex((user) => user.id === id);
				if (index !== -1) {
					this.userList[index] = { ...this.userList[index], ...data };
				}

				// 如果是当前查看的用户详情，也要更新
				if (this.userDetail && this.userDetail.id === id) {
					this.userDetail = { ...this.userDetail, ...data };
				}

				return data;
			} catch (error) {
				console.error("更新用户失败:", error);
				throw error;
			}
		},

		/**
		 * 删除用户
		 * @param {number} id 用户ID
		 */
		async deleteUser(id) {
			try {
				await deleteUser(id);

				// 从本地列表中移除
				this.userList = this.userList.filter((user) => user.id !== id);
				this.userTotal -= 1;

				// 如果删除的是当前查看的用户，清空详情
				if (this.userDetail && this.userDetail.id === id) {
					this.userDetail = null;
				}

				return true;
			} catch (error) {
				console.error("删除用户失败:", error);
				throw error;
			}
		},

	},
});
