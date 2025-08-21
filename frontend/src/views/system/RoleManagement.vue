<!-- 角色管理页面 -->
<template>
  <div class="role-management">
    <!-- 使用页面头部组件 -->
    <PageHeader title="角色管理" icon="fa-users" description="管理系统角色信息和权限分配" />

    <!-- 内容包装器 -->
    <div class="content-wrapper">
      <div class="role-section">
        <!-- 搜索区域 -->
        <CommonSearch v-model="searchForm" :items="searchFields" :single-row="true" @search="handleSearch"
          @reset="handleResetSearch">
          <template #actions>
            <CustomButton type="primary" @click="handleAdd" v-permission="'system:manage'">
              <i class="fa fa-plus"></i>
              新增角色
            </CustomButton>
          </template>
        </CommonSearch>

        <!-- 角色列表表格 -->
        <div class="table-section">
          <CommonTable :data="roleList" :columns="tableColumns" :loading="loading" :show-selection="false"
            :show-index="true" :show-actions="true" :show-toolbar="false" :actions-width="150" :actions-fixed="false">


            <template #actions="{ row }">
              <div class="action-buttons">
                <CustomButton type="text" text-type="primary" size="small" @click="handleEdit(row)">
                  编辑
                </CustomButton>
                <CustomButton type="text" text-type="danger" size="small" @click="handleDelete(row)">
                  删除
                </CustomButton>
              </div>
            </template>
          </CommonTable>

          <!-- 独立分页组件 -->
          <CustomPagination v-model:current-page="pagination.currentPage" v-model:page-size="pagination.pageSize"
            :total="pagination.total" :page-sizes="[10, 20, 50, 100]" @size-change="handleSizeChange"
            @current-change="handleCurrentChange" />
        </div>
      </div>
    </div>

    <!-- 角色表单弹窗 -->
    <CustomDialog v-model:visible="formDialogVisible" :title="isEdit ? '编辑角色' : '新增角色'" width="550px"
      :close-on-click-modal="false" :loading="saving" @confirm="handleSaveRole">

      <CommonForm ref="roleFormRef" v-model="currentRole" :items="formItems" :rules="formRules" label-width="120px"
        label-position="right" size="default" :show-actions="false">

        <!-- 权限树插槽 -->
        <template #permissions>
          <div class="permission-section">
            <div class="permission-tree">
              <el-tree ref="permissionTreeRef" :data="permissionTree" :props="treeProps" show-checkbox node-key="id"
                :default-checked-keys="selectedPermissions" @check="handlePermissionCheck">
                <template #default="{ node, data }">
                  <div class="tree-node">
                    <i :class="getPermissionIcon(data.type)"></i>
                    <span class="node-label">{{ data.name }}</span>
                    <el-tag size="small" :type="getPermissionTypeColor(data.type)">
                      {{ getPermissionTypeText(data.type) }}
                    </el-tag>
                  </div>
                </template>
              </el-tree>
            </div>
          </div>
        </template>
      </CommonForm>
    </CustomDialog>


  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import CommonSearch from '@/components/Common/CommonSearch.vue'
import CommonTable from '@/components/Common/CommonTable.vue'
import CustomButton from '@/components/Common/CustomButton.vue'
import CustomDialog from '@/components/Common/CustomDialog.vue'
import CommonForm from '@/components/Common/CommonForm.vue'
import CustomPagination from '@/components/Common/CustomPagination.vue'
import { getRoleList, getRoleDetail, createRole, updateRole, deleteRole, checkRoleNameAvailable, getRolePermissions, assignRolePermissions } from '@/api/role'
import { getPermissionTree } from '@/api/permission'
import PageHeader from '@/components/Common/PageHeader.vue'

/**
 * ===========================
 * 状态与响应式数据
 * ===========================
 */
// 页面状态
const loading = ref(false)
const saving = ref(false)
const formDialogVisible = ref(false)
const isEdit = ref(false)

// 数据存储
const roleList = ref([])
const roleFormRef = ref()
const permissionTreeRef = ref(null)
const currentRole = ref({
  name: '',
  description: '',
  sortOrder: 0,
  remark: ''
})

// 权限相关数据
const permissionTree = ref([])
const selectedPermissions = ref([])

// 树形组件配置
const treeProps = {
  children: 'children',
  label: 'name'
}

// 角色表单项配置
const formItems = computed(() => {
  const items = [
    {
      prop: 'name',
      label: '角色名称',
      type: 'input',
      placeholder: '请输入角色名称',
      clearable: true,
      maxlength: 50,
      showWordLimit: true,
      required: true,
      span: 18
    },
    {
      prop: 'sortOrder',
      label: '排序顺序',
      type: 'input-number',
      placeholder: '请输入排序顺序',
      min: 0,
      max: 9999,
      help: '数值越小排序越靠前',
      span: 18
    },
    {
      prop: 'description',
      label: '角色描述',
      type: 'textarea',
      placeholder: '请输入角色描述',
      rows: 4,
      maxlength: 200,
      showWordLimit: true,
      span: 18
    },
    {
      prop: 'remark',
      label: '备注',
      type: 'textarea',
      placeholder: '请输入备注信息',
      rows: 3,
      maxlength: 200,
      showWordLimit: true,
      span: 18
    },
    {
      prop: 'permissions',
      label: '权限分配',
      type: 'slot',
      slotName: 'permissions',
      span: 18
    }
  ]

  return items
})

/**
 * ===========================
 * 表单与表格配置
 * ===========================
 */
// 搜索表单
const searchForm = reactive({
  name: '' // 角色名称搜索字段
})

// 搜索字段配置
const searchFields = [
  {
    prop: 'name',
    label: '角色名称',
    type: 'input',
    placeholder: '请输入角色名称',
    width: '240px',
    labelWidth: 'var(--form-label-width-standard)'
  }
]

// 分页信息
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 表格列配置
const tableColumns = [
  { prop: 'id', label: 'ID', width: 80, align: 'center' },
  { prop: 'name', label: '角色名称', width: 150 },
  { prop: 'description', label: '描述', minWidth: 200 }
]

// 自定义验证器
const validateRoleName = async (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入角色名称'))
    return
  }

  try {
    const excludeId = isEdit.value ? currentRole.value.id : null
    const response = await checkRoleNameAvailable(value, excludeId)
    if (!response.available) {
      callback(new Error('角色名称已存在'))
    } else {
      callback()
    }
  } catch (error) {
    console.error('验证角色名称失败:', error)
    callback()
  }
}

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入角色名称', trigger: 'blur' },
    { min: 2, max: 50, message: '角色名称长度在 2 到 50 个字符', trigger: 'blur' },
    { validator: validateRoleName, trigger: 'blur' }
  ],
  sortOrder: [
    { required: true, message: '请输入排序顺序', trigger: 'blur' },
    { type: 'number', min: 0, max: 9999, message: '排序顺序必须在 0 到 9999 之间', trigger: 'blur' }
  ],
  description: [
    { max: 200, message: '描述长度不能超过 200 个字符', trigger: 'blur' }
  ],
  remark: [
    { max: 200, message: '备注长度不能超过 200 个字符', trigger: 'blur' }
  ]
}

/**
 * ===========================
 * 搜索与筛选方法
 * ===========================
 */
// 执行搜索
const handleSearch = async (searchData) => {
  loading.value = true
  try {
    // 如果传入了搜索数据，则更新searchForm
    if (searchData && Object.keys(searchData).length > 0) {
      Object.assign(searchForm, searchData)
    }

    const params = {
      page: pagination.currentPage,
      size: pagination.pageSize,
      name: searchForm.name
    }

    const response = await getRoleList(params)
    roleList.value = response.items || []
    pagination.total = response.total || 0
  } catch (error) {
    ElMessage.error('查询角色列表失败')
  } finally {
    loading.value = false
  }
}

// 重置搜索
const handleResetSearch = (resetData) => {
  searchForm.name = ''
  pagination.currentPage = 1
  handleSearch(resetData)
}

/**
 * ===========================
 * 权限相关方法
 * ===========================
 */
// 加载权限树
const loadPermissionTree = async () => {
  try {
    const response = await getPermissionTree()
    permissionTree.value = response || []

    // 等待DOM更新后自动展开所有节点
    await nextTick()
    if (permissionTreeRef.value) {
      expandAll()
    }
  } catch (error) {
    console.error('加载权限树失败:', error)
    ElMessage.error('加载权限树失败')
  }
}

// 加载角色权限
const loadRolePermissions = async (roleId) => {
  if (!roleId) return
  try {
    const response = await getRolePermissions(roleId)
    selectedPermissions.value = (response || []).map(item => item.id)
    // 设置树形组件的选中状态
    if (permissionTreeRef.value) {
      permissionTreeRef.value.setCheckedKeys(selectedPermissions.value)
    }
  } catch (error) {
    console.error('加载角色权限失败:', error)
    ElMessage.error('加载角色权限失败')
  }
}

const handlePermissionCheck = () => {
  // 获取所有选中的节点key
  const checkedKeys = permissionTreeRef.value.getCheckedKeys()
  // 获取半选中的节点key（父节点部分选中）
  const halfCheckedKeys = permissionTreeRef.value.getHalfCheckedKeys()
  // 合并所有需要保存的权限ID
  selectedPermissions.value = [...checkedKeys, ...halfCheckedKeys]
}

const expandAll = () => {
  const allNodes = permissionTreeRef.value.store.nodesMap
  for (let nodeId in allNodes) {
    allNodes[nodeId].expanded = true
  }
}

const getPermissionTypeColor = (type) => {
  const colorMap = {
    'MENU': 'primary',
    'BUTTON': 'success',
    'API': 'warning'
  }
  return colorMap[type] || 'info'
}

const getPermissionTypeText = (type) => {
  const textMap = {
    'MENU': '菜单',
    'BUTTON': '按钮',
    'API': '接口'
  }
  return textMap[type] || type
}

const getPermissionIcon = (type) => {
  if (type === 'MENU') {
    return 'fa fa-bars'
  } else if (type === 'BUTTON') {
    return 'fa fa-square-o'
  } else {
    return 'fa fa-key'
  }
}

// 保存权限分配
const savePermissions = async (roleId) => {
  if (!roleId) return

  try {
    const checkedKeys = permissionTreeRef.value.getCheckedKeys()
    await assignRolePermissions(roleId, checkedKeys)
    return true
  } catch (error) {
    console.error('权限分配失败:', error)
    ElMessage.error('权限分配失败')
    return false
  }
}

/**
 * ===========================
 * 角色增删改操作
 * ===========================
 */
// 添加角色
const handleAdd = async () => {
  isEdit.value = false
  currentRole.value = {
    name: '',
    description: '',
    sortOrder: 0,
    remark: ''
  }
  formDialogVisible.value = true

  // 加载权限树
  await loadPermissionTree()
  // 清空已选择的权限
  selectedPermissions.value = []
}

// 编辑角色
const handleEdit = async (row) => {
  try {
    isEdit.value = true
    const roleDetail = await getRoleDetail(row.id)
    currentRole.value = { ...roleDetail }
    formDialogVisible.value = true

    // 加载权限树
    await loadPermissionTree()
    // 加载角色权限
    await loadRolePermissions(currentRole.value.id)
  } catch (error) {
    ElMessage.error('获取角色详情失败')
  }
}

// 保存角色
const handleSaveRole = async () => {
  if (!roleFormRef.value) return

  try {
    // 使用CommonForm提供的validate方法进行表单验证
    const isValid = await roleFormRef.value.validate()
    if (!isValid) return

    saving.value = true

    if (isEdit.value) {
      await updateRole(currentRole.value.id, currentRole.value)
      // 如果是编辑模式，同时保存权限
      await savePermissions(currentRole.value.id)
      ElMessage.success('角色更新成功')
    } else {
      await createRole(currentRole.value)
      ElMessage.success('角色创建成功')
    }

    formDialogVisible.value = false
    handleSearch()
  } catch (error) {
    console.error('保存角色失败:', error)
    ElMessage.error(isEdit.value ? '角色更新失败' : '角色创建失败')
  } finally {
    saving.value = false
  }
}

// 删除角色
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除角色"${row.name}"吗？`,
      '确认删除',
      { type: 'warning' }
    )

    await deleteRole(row.id)
    ElMessage.success('角色删除成功')
    handleSearch()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('角色删除失败')
    }
  }
}



/**
 * ===========================
 * 分页处理
 * ===========================
 */
// 每页数量变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.currentPage = 1
  handleSearch()
}

// 当前页变化
const handleCurrentChange = (page) => {
  pagination.currentPage = page
  handleSearch()
}

/**
 * ===========================
 * 生命周期钩子
 * ===========================
 */
// 页面初始化
onMounted(() => {
  handleSearch()
})
</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;

/**
 * ===========================
 * 页面布局样式
 * ===========================
 */
.role-management {
  background-color: var(--bg-secondary);
  min-height: calc(100vh - var(--header-height));

  .content-wrapper {
    background: var(--bg-primary);
    border-radius: var(--border-radius-large);
    box-shadow: var(--shadow-light);
    border: 1px solid var(--border-color-light);
    overflow: hidden;
  }

  .role-section {
    padding: var(--spacing-large);

    .table-section {
      margin-top: var(--spacing-large);
    }
  }

  .action-buttons {
    @include flex-center;
    gap: var(--spacing-small);
    flex-wrap: nowrap;
  }

  .dialog-footer {
    @include flex-end;
    gap: var(--spacing-medium);
  }
}

/**
 * ===========================
 * 响应式适配
 * ===========================
 */
@include respond-to(sm) {
  .role-management {
    padding: var(--spacing-sm);

    .role-section {
      padding: var(--spacing-sm);
    }

    .action-buttons {
      flex-direction: column;
      gap: var(--spacing-mini);

      .custom-button {
        width: 100%;
        font-size: var(--font-size-sm);
      }
    }
  }
}

/**
 * ===========================
 * 角色表单样式（从RoleForm合并）
 * ===========================
 */
.el-form {
  .el-form-item {
    margin-bottom: var(--form-item-margin-bottom-large);
  }

  .form-tip {
    margin-left: var(--spacing-sm);
    font-size: var(--font-size-sm);
    color: var(--text-tertiary);
  }

  .el-input-number {
    width: 200px;
  }

  .el-radio-group {
    .el-radio {
      margin-right: var(--spacing-large);
    }
  }

  .permission-section {
    width: 100%;
    max-width: 800px;
    border: var(--border-width-thin) solid var(--border-color-light);
    border-radius: var(--border-radius-base);
    padding: var(--spacing-base);
    background-color: var(--bg-tertiary);

    .permission-tree {
      max-height: var(--panel-content-min-height);
      overflow-y: auto;
      border: var(--border-width-thin) solid var(--border-color-light);
      border-radius: var(--border-radius-base);
      padding: var(--spacing-small);
      background-color: var(--bg-primary);

      .tree-node {
        display: flex;
        align-items: center;
        gap: var(--spacing-small);
        width: 100%;
        padding: 2px 0;

        i {
          color: #909399;
          width: var(--icon-size-md);
          text-align: center;
        }

        .node-label {
          flex: 1;
          margin-right: var(--spacing-small);
        }

        .el-tag {
          margin-left: auto;
          text-transform: capitalize;
          min-width: 40px;
          text-align: center;
        }
      }
    }
  }
}
</style>
