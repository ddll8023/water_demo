<!-- 用户管理页面 -->
<template>
  <div class="user-management">
    <!-- 使用页面头部组件 -->
    <PageHeader title="用户管理" icon="fa-user" description="管理系统登录账号和权限分配" />

    <!-- 搜索区域 -->
    <CommonSearch v-model="searchForm" :items="searchFields" :single-row="true" @search="handleSearch"
      @reset="handleResetSearch">
      <template #actions>
        <CustomButton type="primary" @click="handleAdd" v-permission="'system:manage'">
          <i class="fa fa-plus"></i>
          新增用户
        </CustomButton>
      </template>
    </CommonSearch>

    <!-- 用户列表表格 -->
    <div class="table-section">
      <CommonTable :data="userList" :columns="tableColumns" :loading="loading" :total="pagination.total"
        :current-page="pagination.currentPage" :page-size="pagination.pageSize" :show-index="true" :show-toolbar="false"
        :actions-width="240" :actions-fixed="false" @size-change="handleSizeChange"
        @current-change="handleCurrentChange">
        <template #isActive="{ row }">
          <el-tag :type="row.isActive ? 'success' : 'danger'" size="small">
            {{ getUserStatusLabel(row.isActive) }}
          </el-tag>
        </template>

        <template #createdAt="{ row }">
          {{ row.createdAt }}
        </template>

        <template #actions="{ row }">
          <div class="action-buttons">
            <CustomButton type="text" text-type="primary" size="small" @click="handleEdit(row)"
              v-permission="'system:manage'">
              编辑
            </CustomButton>
            <CustomButton type="text" text-type="danger" size="small" @click="handleDelete(row)"
              v-permission="'system:manage'">
              删除
            </CustomButton>
          </div>
        </template>
      </CommonTable>

      <CustomPagination v-model:current-page="pagination.currentPage" v-model:page-size="pagination.pageSize"
        :total="pagination.total" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </div>

    <!-- 用户表单弹窗 -->
    <CustomDialog v-model:visible="formDialogVisible" :title="isEdit ? '编辑用户信息' : '新增用户'" width="800px"
      :close-on-click-modal="false" :loading="saving" @confirm="handleSaveUser" @cancel="formDialogVisible = false">
      <CommonForm ref="userFormRef" v-model="currentUser" :items="formItems" :rules="formRules" label-width="120px"
        label-position="right" size="default" :disabled="saving" :show-actions="false" />
    </CustomDialog>

    <!-- 用户角色分配弹窗 -->
    <CustomDialog v-model:visible="roleDialogVisible" :title="`为用户「${currentUserName}」分配角色`"
      width="var(--panel-height-default)" :close-on-click-modal="false" :close-on-press-escape="false"
      :loading="assignRoleLoading" @close="handleRoleDialogClose" @cancel="handleRoleDialogClose"
      @confirm="handleAssignRoleConfirm">
      <div class="role-assignment">
        <div class="assignment-header">
          <div class="user-info">
            <el-tag type="primary" size="large">{{ currentUserName }}</el-tag>
            <span class="user-desc">{{ currentUserDepartment }}</span>
          </div>
          <div class="role-stats">
            <span>{{ selectedRoles.length ? '已选择角色' : '请选择角色' }}</span>
          </div>
        </div>

        <div class="assignment-content">
          <CommonTable :data="roleList" :columns="roleTableColumns" :show-toolbar="false" :show-selection="true"
            :show-index="false" :show-actions="false" :show-pagination="false" :selectable-function="isRoleSelectable"
            :select-mode="'radio'" @selection-change="handleRoleSelectionChange">
            <template #name="{ row }">
              <div class="role-name">
                <span>{{ row.name }}</span>
                <el-tag v-if="!row.isActive" type="danger" size="small" class="status-tag">
                  已禁用
                </el-tag>
              </div>
            </template>

            <template #permissionCount="{ row }">
              <span>{{ row.permissionCount || 0 }}</span>
            </template>
          </CommonTable>
        </div>
      </div>
    </CustomDialog>
  </div>
</template>

<script setup>
/**
 * 导入依赖
 * -------------------------------------------------------
 */
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import CommonSearch from '@/components/Common/CommonSearch.vue'
import CommonTable from '@/components/Common/CommonTable.vue'
import CommonForm from '@/components/Common/CommonForm.vue'
import CustomButton from '@/components/Common/CustomButton.vue'
import CustomDialog from '@/components/Common/CustomDialog.vue'
import PageHeader from '@/components/Common/PageHeader.vue'
import CustomPagination from '@/components/Common/CustomPagination.vue'

import { useUserStore } from '@/stores/modules/user'
import { useAuthStore } from '@/stores/modules/auth'
import { getRoleOptions } from '@/api/role'
import { getUserRoles, assignUserRoles } from '@/api/user'
import { useDictionary, DICT_TYPES } from '@/composables/useDictionary'

/**
 * 状态管理与全局设置
 * -------------------------------------------------------
 */
// 状态管理
const userStore = useUserStore()
const authStore = useAuthStore()

// 字典功能
const { getDictData, getDictLabel } = useDictionary()

/**
 * 响应式数据定义
 * -------------------------------------------------------
 */
// 页面状态控制
const loading = ref(false)
const saving = ref(false)
const formDialogVisible = ref(false)
const roleDialogVisible = ref(false)
const assignRoleLoading = ref(false)
const isEdit = ref(false)
const userFormRef = ref()

// 选项数据
const userStatusOptions = ref([])
const roleOptions = ref([])

// 当前用户数据
const currentUser = ref({
  username: '',
  password: '',
  roleId: null, // 修改为单一角色ID
  isActive: true
})

// 用户角色分配相关数据
const roleList = ref([])
const selectedRoles = ref([])
const currentUserName = ref('')
const currentUserDepartment = ref('')

// 搜索表单
const searchForm = reactive({
  username: '', // 用户名搜索
  roleId: null,
  isActive: null
})

// 分页信息
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

/**
 * 计算属性
 * -------------------------------------------------------
 */
// 用户列表数据
const userList = computed(() => userStore.getUserListData)

// 用户表单配置项
const formItems = computed(() => {
  const items = [
    {
      prop: 'username',
      label: '用户名',
      type: 'input',
      placeholder: '请输入用户名',
      disabled: isEdit.value,
      clearable: true,
      required: true
    }
  ]

  // 只在新增模式下显示密码字段
  if (!isEdit.value) {
    items.push({
      prop: 'password',
      label: '密码',
      type: 'input',
      inputType: 'password',
      placeholder: '请输入密码',
      showPassword: true,
      clearable: true,
      required: true,
      fullWidth: true
    })
  }

  // 角色选择 - 单选模式
  items.push({
    prop: 'roleId',
    label: '角色分配',
    type: 'select',
    placeholder: '请选择角色',
    clearable: true,
    required: true,
    options: roleSelectOptions.value
  })

  // 状态选择 - 在新增和编辑模式下都显示
  items.push({
    prop: 'isActive',
    label: '状态',
    type: 'radio',
    options: [
      { label: '启用', value: true },
      { label: '禁用', value: false }
    ],
    defaultValue: true,
    required: true
  })

  return items
})

// 搜索字段配置
const searchFields = computed(() => [
  {
    prop: 'username',
    label: '用户名',
    type: 'input',
    placeholder: '请输入用户名',
    span: 4,
    labelWidth: 'var(--form-label-width-search)'
  },
  {
    prop: 'roleId',
    label: '角色',
    type: 'select',
    placeholder: '请选择角色',
    span: 4,
    options: roleOptions.value?.map(role => ({
      label: role.name,
      value: role.id
    })) || [],
    labelWidth: 'var(--form-label-width-compact)'
  },
  {
    prop: 'isActive',
    label: '状态',
    type: 'select',
    placeholder: '请选择状态',
    clearable: true,
    span: 4,
    options: userStatusOptions.value,
    labelWidth: 'var(--form-label-width-compact)'
  }
])

// 表格列配置
const tableColumns = [
  { prop: 'id', label: 'ID', width: 80, align: 'center' },
  { prop: 'username', label: '用户名', minWidth: 120 },
  { prop: 'roleName', label: '角色', minWidth: 120 },
  { prop: 'isActive', label: '状态', width: 80, align: 'center' },
  { prop: 'lastLogin', label: '最后登录', width: 160 },
  { prop: 'createdAt', label: '创建时间', width: 160 }
]

// 角色表格列配置
const roleTableColumns = [
  { prop: 'name', label: '角色名称', minWidth: 120 },
  { prop: 'description', label: '角色描述', minWidth: 150, showOverflowTooltip: true },
  { prop: 'sortOrder', label: '排序', width: 80 },
  { prop: 'permissionCount', label: '权限数量', width: 100 }
]

// 转换角色选项数据格式
const roleSelectOptions = computed(() => {
  return roleOptions.value.map(role => {
    // 如果is_active不存在，默认为true（可选状态）
    const isActive = role.is_active !== undefined ? role.is_active : true
    return {
      value: role.id,
      label: role.name,
      disabled: !isActive // 只有明确设置为false时才禁用
    }
  })
})

// 表单验证规则
const formRules = computed(() => ({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  password: [
    { required: !isEdit.value, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  roleId: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],
  isActive: [
    { required: true, message: '请选择用户状态', trigger: 'change' }
  ]
}))

/**
 * 数据查询与搜索相关方法
 * -------------------------------------------------------
 */
// 查询用户列表
const handleSearch = async (searchData = null) => {
  loading.value = true
  try {
    // 使用传入的搜索数据或当前的搜索表单数据
    const currentSearchData = searchData || searchForm

    const searchParams = {
      page: pagination.currentPage,
      size: pagination.pageSize,
      ...currentSearchData
    }

    // 过滤掉空值和null值，但保留page和size参数
    const filteredParams = Object.fromEntries(
      Object.entries(searchParams).filter(([key, value]) => {
        // 保留page和size参数
        if (key === 'page' || key === 'size') {
          return true
        }
        // 过滤掉空字符串、null和undefined
        return value !== '' && value !== null && value !== undefined
      })
    )

    await userStore.fetchUserList(filteredParams)
    pagination.total = userStore.userTotal
  } catch (error) {
    ElMessage.error('查询用户列表失败')
  } finally {
    loading.value = false
  }
}

// 重置搜索条件
const handleResetSearch = (searchData = null) => {
  // CommonSearch组件的handleReset方法已经触发了search事件
  // 不需要在这里再调用handleSearch，只需重置分页
  pagination.currentPage = 1

  // 如果没有传入searchData，则手动重置searchForm
  if (!searchData) {
    Object.keys(searchForm).forEach(key => {
      if (key === 'isActive') {
        searchForm[key] = null
      } else {
        searchForm[key] = ''
      }
    })
    // 由于searchData为空，CommonSearch可能没有触发搜索，这种情况下需要手动触发
    handleSearch()
  }
}

/**
 * 分页控制方法
 * -------------------------------------------------------
 */
// 切换每页显示数量
const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.currentPage = 1
  handleSearch()
}

// 切换当前页码
const handleCurrentChange = (page) => {
  pagination.currentPage = page
  handleSearch()
}



/**
 * 用户管理操作方法
 * -------------------------------------------------------
 */
// 新增用户
const handleAdd = async () => {
  isEdit.value = false
  currentUser.value = {
    username: '',
    password: '',
    roleId: null, // 初始化为null
    isActive: true
  }

  // 确保选项数据已加载
  await loadRoleOptions()

  formDialogVisible.value = true
}

// 编辑用户
const handleEdit = async (row) => {
  try {
    isEdit.value = true
    // 复制用户基本信息
    currentUser.value = { ...row }

    // 确保选项数据已加载
    await loadRoleOptions()

    // 加载用户当前角色
    const userRoles = await getUserRoles(row.id)
    if (userRoles && Array.isArray(userRoles) && userRoles.length > 0) {
      // 只取第一个角色
      currentUser.value.roleId = userRoles[0].id
    } else {
      currentUser.value.roleId = null
    }

    formDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取用户信息失败')
  }
}

// 保存用户信息
const handleSaveUser = async () => {
  if (!userFormRef.value) return

  try {
    // 使用CommonForm的validate方法进行验证
    const isValid = await userFormRef.value.validate()
    if (!isValid) return

    saving.value = true

    // 准备提交的用户数据
    const userData = { ...currentUser.value }

    let saveResult = null

    if (isEdit.value) {
      saveResult = await userStore.updateUser(userData.id, userData)
      // 分配角色
      if (saveResult && userData.roleId) {
        await assignUserRoles(userData.id, [userData.roleId])
      }
      ElMessage.success('用户信息更新成功')
    } else {
      saveResult = await userStore.createUser(userData)
      // 分配角色（如果是新创建的用户）
      if (saveResult && saveResult.id && userData.roleId) {
        await assignUserRoles(saveResult.id, [userData.roleId])
      }
      ElMessage.success('用户创建成功')
    }

    formDialogVisible.value = false
    handleSearch()
  } catch (error) {
    ElMessage.error(isEdit.value ? '用户更新失败' : '用户创建失败')
  } finally {
    saving.value = false
  }
}

// 删除单个用户
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户"${row.username}"吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await userStore.deleteUser(row.id)
    ElMessage.success('用户删除成功')
    handleSearch()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('用户删除失败')
    }
  }
}



/**
 * 角色分配相关方法
 * -------------------------------------------------------
 */
// 角色选择变更处理
const handleRoleSelectionChange = (selection) => {
  selectedRoles.value = selection
}

// 角色是否可选
const isRoleSelectable = (row) => {
  // 只有启用状态的角色才能被选择
  return row.isActive !== false
}

// 角色分配确认
const handleAssignRoleConfirm = async () => {
  try {
    assignRoleLoading.value = true

    // 只获取第一个选中的角色ID，如果没有则设为null
    const roleId = selectedRoles.value.length > 0 ? selectedRoles.value[0].id : null

    if (!roleId) {
      ElMessage.warning('请选择一个角色')
      assignRoleLoading.value = false
      return
    }

    // 这里应该有一个当前用户ID的变量，但原始代码中不完整
    // 实际使用时应从当前选中的用户记录中获取ID
    ElMessage.warning('用户角色分配功能需要完善')
    handleSearch()
    handleRoleDialogClose()
  } catch (error) {
    console.error('分配角色失败:', error)
    ElMessage.error('分配角色失败')
  } finally {
    assignRoleLoading.value = false
  }
}

// 关闭角色分配弹窗
const handleRoleDialogClose = () => {
  roleDialogVisible.value = false
  // 重置数据
  selectedRoles.value = []
  roleList.value = []
  currentUserName.value = ''
  currentUserDepartment.value = ''
}

/**
 * 字典与选项加载方法
 * -------------------------------------------------------
 */

// 加载角色选项数据
const loadRoleOptions = async () => {
  try {
    const response = await getRoleOptions()
    roleOptions.value = response || []
  } catch (error) {
    console.error('加载角色选项失败:', error)
    roleOptions.value = []
  }
}

// 加载用户状态字典数据
const loadUserStatusOptions = async () => {
  try {
    const dictData = await getDictData(DICT_TYPES.USER_STATUS)
    userStatusOptions.value = dictData.map(item => ({
      label: item.label,
      value: item.value === '1' // 转换为布尔值以兼容现有逻辑
    }))
  } catch (error) {
    // 使用兜底数据
    userStatusOptions.value = [
      { label: '启用', value: true },
      { label: '禁用', value: false }
    ]
  }
}

/**
 * 辅助与格式化方法
 * -------------------------------------------------------
 */
// 获取用户状态显示标签
const getUserStatusLabel = (isActive) => {
  const option = userStatusOptions.value.find(opt => opt.value === isActive)
  return option ? option.label : (isActive ? '启用' : '禁用')
}

/**
 * 生命周期钩子
 * -------------------------------------------------------
 */
// 页面初始化
onMounted(async () => {
  // 1. 首先加载角色和状态选项
  await Promise.all([
    loadRoleOptions(),
    loadUserStatusOptions()
  ])

  // 2. 然后加载用户列表
  handleSearch()
})
</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;

/**
 * 页面主布局样式
 * -------------------------------------------------------
 */
.user-management {
  padding: var(--spacing-large);
  background: var(--el-bg-color-page);
  height: calc(100vh - var(--header-height));
  display: flex;
  flex-direction: column;
  overflow: hidden;

  // 页面标题区域样式已移至PageHeader组件

  // 表格区域样式
  .table-section {
    background: var(--bg-primary);
    border-radius: var(--border-radius-large);
    overflow: hidden;
    flex: 1;
    display: flex;
    flex-direction: column;
  }

  // 操作按钮样式
  .action-buttons {
    display: flex;
    gap: var(--spacing-xs);
    flex-wrap: nowrap;
    justify-content: center; // 居中对齐，更美观
    align-items: center;
    width: 100%; // 占满操作列宽度
    flex-direction: row;

    .custom-button {
      min-width: 56px; // 稍微减小最小宽度
      padding: 5px 10px; // 减小内边距
      font-size: var(--font-size-extra-small); // 稍微减小字体
      flex: 0 0 auto; // 不允许伸缩，保持固定尺寸
      border-radius: var(--border-radius-base); // 圆角

      &.custom-button--small {
        height: var(--button-size-small);
        line-height: 1;
      }
    }
  }

  // 用户表单样式
  .el-form {
    .el-form-item {
      margin-bottom: var(--form-item-margin-bottom-large);
    }

    .el-input,
    .el-select {
      width: 100%;
    }

    .el-radio-group {
      .el-radio {
        margin-right: var(--spacing-large);
      }
    }
  }

  // 角色分配样式
  .role-assignment {
    .assignment-header {
      @include flex-between;
      padding: var(--spacing-base) 0;
      border-bottom: 1px solid var(--el-border-color-light);
      margin-bottom: var(--spacing-large);

      .user-info {
        @include flex-center-y;
        gap: var(--spacing-medium);

        .user-desc {
          font-size: var(--font-size-base);
          color: var(--el-text-color-secondary);
        }
      }

      .role-stats {
        font-size: var(--font-size-base);
        color: var(--el-text-color-secondary);
      }
    }

    .assignment-content {
      margin-bottom: var(--spacing-large);

      .role-name {
        @include flex-center-y;
        gap: var(--spacing-small);

        .status-tag {
          margin-left: var(--spacing-small);
        }
      }
    }
  }
}

/**
 * 响应式布局适配
 * -------------------------------------------------------
 */
// 移动端适配
@media (max-width: 768px) {
  .user-management {
    .action-buttons {
      flex-direction: column;
      gap: 6px;

      .custom-button {
        width: 100%;
        min-width: 60px;
        font-size: 12px;
        padding: 6px 12px;
      }
    }

    // 表格容器适配
    .table-section {
      margin: 0 -10px; // 扩展到屏幕边缘
      border-radius: 0;
    }
  }
}

// 超小屏幕适配
@media (max-width: 480px) {
  .user-management {
    .action-buttons {
      .custom-button {
        font-size: 11px;
        padding: 4px 8px;
        height: 28px;
      }
    }
  }
}
</style>
