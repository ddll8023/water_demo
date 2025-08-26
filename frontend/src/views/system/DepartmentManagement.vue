<!-- 部门管理页面 -->
<template>
  <div class="department-management">
    <!-- 使用页面头部组件 -->
    <PageHeader title="部门管理" icon="fa-building-o" description="管理组织架构、部门层级和人员分配" />

    <div class="content-wrapper">
      <div class="department-section">
        <!-- 搜索区域 -->
        <CommonSearch v-model="searchForm" :items="searchFields" :single-row="true" @search="handleSearch"
          @reset="handleResetSearch">
          <template #actions>
            <CustomButton type="primary" @click="handleAdd" v-permission="'system:manage'">
              <i class="fa fa-plus"></i>
              新增部门
            </CustomButton>

          </template>
        </CommonSearch>

        <!-- 表格视图 -->
        <div class="table-section">
          <CommonTable :data="departmentList" :columns="tableColumns" :loading="loading" :show-selection="false"
            :show-index="true" :show-actions="true" :show-toolbar="false" :actions-width="150" :actions-fixed="false"
            @row-click="handleRowClick">
            <template #level="{ row }">
              <el-tag :type="({ 1: 'danger', 2: 'warning', 3: 'success', 4: 'info' }[calculateLevel(row)] || 'info')"
                size="small">
                第{{ calculateLevel(row) }}级
              </el-tag>
            </template>

            <template #parentName="{ row }">
              <span v-if="row.parentName">{{ row.parentName }}</span>
              <el-tag v-else type="info" size="small">顶级部门</el-tag>
            </template>

            <template #isActive="{ row }">
              <el-tag :type="row.isActive === '1' ? 'success' : 'danger'" size="small">
                {{ getDictLabelSync('department_status', row.isActive) }}
              </el-tag>
            </template>

            <template #personnelCount="{ row }">
              <span>{{ row.personnelCount || 0 }}人</span>
            </template>

            <template #createdAt="{ row }">
              {{ formatDateTime(row.createdAt) }}
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
            :total="pagination.total" :page-sizes="[10, 20, 50, 100]" @size-change="handleSizeChange"
            @current-change="handleCurrentChange" />
        </div>
      </div>
    </div>

    <!-- 部门表单对话框 -->
    <CustomDialog :visible="formDialogVisible" :title="isEdit ? '编辑部门' : '新增部门'" :width="'var(--dialog-width-large)'"
      :close-on-click-modal="false" @update:visible="handleFormDialogClose">
      <CommonForm ref="formRef" v-model="form" :items="formItems" :rules="formRules"
        :label-width="'var(--form-label-width-detail)'" label-position="right" :show-actions="false"
        @submit="handleFormSubmit">
        <template #parentId>
          <el-tree-select v-model="form.parentId" :data="departmentTreeOptions" :props="treeSelectProps"
            placeholder="请选择上级部门" style="width: 100%" clearable check-strictly :default-expand-all="false"
            node-key="id" />
        </template>

        <template #parentDepartmentInfo>
          <div class="parent-department-info">
            <div class="parent-info-header">
              <i class="fa fa-building-o parent-info-icon"></i>
              <span class="parent-info-title">父部门信息</span>
            </div>
            <div class="parent-info-content">
              <div class="parent-info-item">
                <span class="info-label">部门名称：</span>
                <span class="info-value">{{ parentDepartment.name || '未知' }}</span>
              </div>
              <div class="parent-info-item">
                <span class="info-label">部门职责：</span>
                <span class="info-value">{{ parentDepartment.duty || '暂无描述' }}</span>
              </div>
              <div class="parent-info-item" v-if="parentDepartment.contact">
                <span class="info-label">联系方式：</span>
                <span class="info-value">{{ parentDepartment.contact }}</span>
              </div>
              <div class="parent-info-item" v-if="parentDepartment.personnelCount !== undefined">
                <span class="info-label">人员数量：</span>
                <span class="info-value">{{ parentDepartment.personnelCount || 0 }}人</span>
              </div>
            </div>
          </div>
        </template>
      </CommonForm>

      <template #footer>
        <div class="dialog-footer">
          <CustomButton type="secondary" @click="handleFormDialogClose">取消</CustomButton>
          <CustomButton type="primary" :loading="submitLoading"
            @click="formRef.value?.validate().then(valid => { if (valid) handleFormSubmit(form) })">
            确定
          </CustomButton>
        </div>
      </template>
    </CustomDialog>


  </div>
</template>

<script setup>
/**
 * ==============================
 * 导入和基本配置
 * ==============================
 */
import { ref, reactive, onMounted, watch, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import CommonSearch from '@/components/Common/CommonSearch.vue'
import CommonTable from '@/components/Common/CommonTable.vue'
import CommonForm from '@/components/Common/CommonForm.vue'
import CustomButton from '@/components/Common/CustomButton.vue'
import CustomDialog from '@/components/Common/CustomDialog.vue'
import CustomPagination from '@/components/Common/CustomPagination.vue'
import PageHeader from '@/components/Common/PageHeader.vue'

import {
  getDepartmentList,
  deleteDepartment,
  createDepartment,
  updateDepartment,
  checkDepartmentNameAvailable
} from '@/api/department'
import { getDepartmentTree } from '@/api/management-info'
import { formatDateTime } from '@/utils/shared/common'
import { useDictionary, DICT_TYPES } from '@/composables/useDictionary'

/**
 * ==============================
 * 响应式数据和状态管理
 * ==============================
 */
// 列表数据和加载状态
const loading = ref(false)
const departmentList = ref([])

// 字典相关
const { getDictData } = useDictionary()
const departmentStatusOptions = ref([])

// 字典数据缓存
const dictMaps = reactive({
  department_status: []
})

// 字典加载状态
const dictLoading = ref(true)

// 对话框状态
const formDialogVisible = ref(false)

// 表单数据
const currentDepartment = ref({})
const parentDepartment = ref({})
const isEdit = ref(false)
const formRef = ref()
const submitLoading = ref(false)
const departmentTreeOptions = ref([])

// 表单数据 - 严格按照后端API标准
const form = reactive({
  name: '',           // 部门名称
  parentId: null,     // 父部门ID
  duty: '',           // 部门职责
  contact: '',        // 联系方式
  regionId: 1,        // 所属行政区域ID (默认值1，regions API暂未实现)
  isActive: '1'       // 启用状态
})

// 树形选择器配置
const treeSelectProps = {
  children: 'children',
  label: 'name',
  value: 'id'
}

// 搜索表单
const searchForm = reactive({
  name: ''
})

// 分页信息
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})



// 表单配置项
const formItems = ref([
  {
    prop: 'name',
    label: '部门名称',
    type: 'input',
    placeholder: '请输入部门名称',
    maxlength: 50,
    showWordLimit: true,
    clearable: true,
    required: true,
    span: 12
  },
  {
    prop: 'parentId',
    label: '上级部门',
    type: 'slot',
    span: 12
  },
  {
    prop: 'isActive',
    label: '启用状态',
    type: 'radio',
    options: departmentStatusOptions,
    defaultValue: '1',
    required: true
  },
  {
    prop: 'contact',
    label: '联系方式',
    type: 'input',
    placeholder: '请输入联系方式',
    maxlength: 255,
    showWordLimit: true,
    clearable: true,
    span: 12
  },
  {
    prop: 'regionId',
    label: '所属区域',
    type: 'select',
    options: [{ value: 1, label: '默认区域' }],
    placeholder: '请选择所属区域',
    disabled: true,
    span: 12,
    tip: '注：行政区划功能暂未开放，当前使用默认区域'
  },
  {
    prop: 'duty',
    label: '部门职责',
    type: 'textarea',
    placeholder: '请输入部门职责',
    maxlength: 500,
    showWordLimit: true,
    rows: 4,
    span: 24
  },
  {
    prop: 'parentDepartmentInfo',
    label: '父部门信息',
    type: 'slot',
    span: 24,
    hidden: () => !parentDepartment.value || !parentDepartment.value.id
  }
])

/**
 * ==============================
 * 表单验证规则                        
 * ==============================
 */
// 自定义验证规则
const validateName = async (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入部门名称'))
    return
  }

  try {
    const excludeId = isEdit.value ? currentDepartment.value.id : null
    const parentId = form.parentId || (parentDepartment.value && parentDepartment.value.id)
    const result = await checkDepartmentNameAvailable(value, parentId, excludeId)
    if (!result.available) {
      callback(new Error('同级部门中已存在相同名称'))
    } else {
      callback()
    }
  } catch (error) {
    callback()
  }
}

const validateParentId = (rule, value, callback) => {
  if (isEdit.value && value === currentDepartment.value.id) {
    callback(new Error('不能选择自己作为上级部门'))
    return
  }
  callback()
}

// 表单验证规则 - 按照后端API标准
const formRules = {
  name: [
    { required: true, validator: validateName, trigger: 'blur' }
  ],
  parentId: [
    { validator: validateParentId, trigger: 'change' }
  ],
  duty: [
    { max: 500, message: '部门职责不能超过500个字符', trigger: 'blur' }
  ],
  contact: [
    { required: true, message: '请输入联系方式', trigger: 'blur' },
    { max: 255, message: '联系方式不能超过255个字符', trigger: 'blur' }
  ]
}

/**
 * ==============================
 * 表格和搜索配置
 * ==============================
 */
// 搜索字段配置
const searchFields = [
  {
    prop: 'name',
    label: '部门名称',
    type: 'input',
    placeholder: '请输入部门名称',
    width: '200px',
    labelWidth: 'var(--form-label-width-standard)'
  }
]

// 表格列配置
const tableColumns = [
  { prop: 'name', label: '部门名称', width: 150, sortable: true },
  { prop: 'level', label: '部门级别', width: 100, slot: 'level' },
  { prop: 'parentName', label: '上级部门', width: 150, slot: 'parentName' },
  { prop: 'duty', label: '部门职责', minWidth: 200, showOverflowTooltip: true },
  { prop: 'contact', label: '联系方式', width: 150, showOverflowTooltip: true },
  { prop: 'personnelCount', label: '人员数量', width: 100, slot: 'personnelCount' },
  { prop: 'isActive', label: '状态', width: 80, slot: 'isActive' },
  { prop: 'createdAt', label: '创建时间', width: 160, slot: 'createdAt', sortable: true }
]

/**
 * ==============================
 * 表单操作函数
 * ==============================
 */
// 重置表单 - 按照后端标准字段
const resetForm = () => {
  form.name = ''
  form.parentId = null
  form.duty = ''
  form.contact = ''
  form.regionId = 1
  form.isActive = '1'
}

/**
 * ==============================
 * 监听器和生命周期                    
 * ==============================
 */
// 监听表单数据变化 - 按照后端标准字段
watch(
  () => currentDepartment.value,
  (newData) => {
    if (newData && Object.keys(newData).length > 0) {
      form.name = newData.name || ''
      form.parentId = newData.parentId || null
      form.duty = newData.duty || ''
      form.contact = newData.contact || ''
      form.regionId = newData.regionId || 1
      form.isActive = newData.isActive !== undefined ? newData.isActive : '1'
    } else {
      resetForm()
    }
  },
  { immediate: true, deep: true }
)

// 监听父部门变化
watch(
  () => parentDepartment.value,
  (newParent) => {
    if (newParent && newParent.id) {
      form.parentId = newParent.id
    }
  },
  { immediate: true, deep: true }
)

// 监听form.parentId变化，加载父部门详细信息
watch(
  () => form.parentId,
  async (newParentId) => {
    if (newParentId && departmentTreeOptions.value.length > 0) {
      const findDepartmentInTree = (tree, targetId) => {
        for (const node of tree) {
          if (node.id === targetId) {
            return node
          }
          if (node.children && node.children.length > 0) {
            const found = findDepartmentInTree(node.children, targetId)
            if (found) return found
          }
        }
        return null
      }

      const foundParent = findDepartmentInTree(departmentTreeOptions.value, newParentId)
      if (foundParent) {
        parentDepartment.value = { ...foundParent }
      } else {
        parentDepartment.value = {}
      }
    } else {
      parentDepartment.value = {}
    }
  },
  { immediate: true }
)

// 监听对话框显示状态
watch(
  () => formDialogVisible.value,
  (visible) => {
    if (visible) {
      nextTick(() => {
        formRef.value?.clearValidate()
        // 在对话框打开时重新加载部门树
        loadDepartmentTree()
      })
    } else {
      resetForm()
    }
  }
)

/**
 * ==============================
 * 字典数据处理函数
 * ==============================
 */
// 字典预加载函数
const loadDictionaries = async () => {
  try {
    dictLoading.value = true
    const departmentStatus = await getDictData('department_status')
    dictMaps.department_status = departmentStatus

    // 同时更新表单选项
    departmentStatusOptions.value = departmentStatus.map(item => ({
      label: item.label,
      value: item.value
    }))
  } catch (error) {
    console.error('加载字典数据失败:', error)
    // 使用兜底数据
    const fallbackData = [
      { label: '启用', value: '1' },
      { label: '禁用', value: '0' }
    ]
    dictMaps.department_status = fallbackData
    departmentStatusOptions.value = fallbackData
  } finally {
    dictLoading.value = false
  }
}

// 同步获取字典标签
const getDictLabelSync = (dictType, value, defaultLabel = '') => {
  if (!dictType || value === undefined || value === null) {
    return defaultLabel || '-'
  }

  // 如果字典数据正在加载中，根据字典类型提供兜底显示
  if (dictLoading.value && dictType === 'department_status') {
    return value === '1' ? '启用' : value === '0' ? '禁用' : (defaultLabel || String(value) || '-')
  }

  const dict = dictMaps[dictType] || []
  const item = dict.find(d => String(d.value) === String(value))

  // 如果找到匹配项，返回标签
  if (item) {
    return item.label
  }

  // 如果字典数据未加载或未找到匹配项，根据具体字典类型提供兜底显示
  if (dictType === 'department_status') {
    return value === '1' ? '启用' : value === '0' ? '禁用' : (defaultLabel || String(value) || '-')
  }

  return defaultLabel || String(value) || '-'
}

/**
 * ==============================
 * 辅助计算函数
 * ==============================
 */
// 计算部门级别 - 基于parent_id关系动态计算
const calculateLevel = (department) => {
  if (!department.parentId) {
    return 1 // 顶级部门
  }

  // 在部门列表中查找父部门并递归计算级别
  const findParentLevel = (list, parentId) => {
    const parent = list.find(item => item.id === parentId)
    if (parent) {
      return calculateLevel(parent)
    }
    return 0
  }

  const parentLevel = findParentLevel(departmentList.value, department.parentId)
  return parentLevel > 0 ? parentLevel + 1 : 1
}





/**
 * ==============================
 * 数据加载和处理方法                  
 * ==============================
 */
// 生命周期钩子
onMounted(async () => {
  try {
    // 先加载字典数据，确保状态显示正确
    await loadDictionaries()

    // 再并行加载其他数据
    await Promise.all([
      loadDepartmentList(),
      loadDepartmentTree()
    ])
  } catch (error) {
    console.error('页面初始化失败:', error)
  }
})

// 加载部门树数据
const loadDepartmentTree = async () => {
  try {
    const response = await getDepartmentTree()
    if (response && Array.isArray(response)) {
      // 如果是编辑模式，需要过滤掉当前部门及其子部门，防止循环引用
      if (isEdit.value && currentDepartment.value && currentDepartment.value.id) {
        const currentId = currentDepartment.value.id
        const filterTree = (tree) => tree.filter(node => {
          if (node.id === currentId) {
            return false
          }
          if (node.children && node.children.length > 0) {
            node.children = filterTree(node.children)
          }
          return true
        })
        departmentTreeOptions.value = filterTree(response)
      } else {
        departmentTreeOptions.value = response
      }
    }
  } catch (error) {
    console.error('加载部门树失败', error)
    ElMessage.error('加载部门树失败')
  }
}



// 加载部门列表数据
const loadDepartmentList = async () => {
  try {
    loading.value = true
    const params = {
      page: pagination.currentPage,
      size: pagination.pageSize,
      ...searchForm
    }

    // 过滤掉空值和null值，但保留page和size参数
    const filteredParams = Object.fromEntries(
      Object.entries(params).filter(([key, value]) => {
        // 保留page和size参数
        if (key === 'page' || key === 'size') {
          return true
        }
        // 过滤掉空字符串、null和undefined
        return value !== '' && value !== null && value !== undefined
      })
    )

    const response = await getDepartmentList(filteredParams)
    departmentList.value = response.items
    pagination.total = response.total
  } catch (error) {
    ElMessage.error('加载部门列表失败')
  } finally {
    loading.value = false
  }
}



/**
 * ==============================
 * CRUD操作函数
 * ==============================
 */
// 新增部门
const handleAdd = () => {
  currentDepartment.value = {}
  parentDepartment.value = {}
  isEdit.value = false
  formDialogVisible.value = true
}

// 编辑部门
const handleEdit = (row) => {
  currentDepartment.value = { ...row }
  parentDepartment.value = {}
  isEdit.value = true
  formDialogVisible.value = true
}

// 删除部门
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除部门"${row.name}"吗？删除后其子部门也将被删除。`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await deleteDepartment(row.id)
    ElMessage.success('删除成功')
    loadDepartmentList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}



// 关闭表单对话框
const handleFormDialogClose = () => {
  formDialogVisible.value = false
}

// 提交表单 - 严格按照后端API标准
const handleFormSubmit = async (formData) => {
  try {
    submitLoading.value = true

    if (isEdit.value) {
      await updateDepartment(currentDepartment.value.id, formData)
      ElMessage.success('更新部门成功')
    } else {
      await createDepartment(formData)
      ElMessage.success('创建部门成功')
    }

    handleFormSuccess()
  } catch (error) {
    ElMessage.error(isEdit.value ? '更新部门失败' : '创建部门失败')
  } finally {
    submitLoading.value = false
  }
}

/**
 * ==============================
 * 事件处理函数
 * ==============================
 */
// 搜索处理
const handleSearch = (searchData) => {
  // 如果传递了搜索数据，则更新searchForm
  if (searchData) {
    Object.assign(searchForm, searchData)
  }
  pagination.currentPage = 1
  loadDepartmentList()
}

// 重置搜索
const handleResetSearch = () => {
  searchForm.name = ''
  pagination.currentPage = 1
  // 移除直接调用loadDepartmentList()，避免重复请求
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.currentPage = 1
  loadDepartmentList()
}

// 当前页变化
const handleCurrentChange = (page) => {
  pagination.currentPage = page
  loadDepartmentList()
}

// 表格行点击事件 - 不再触发查看人员功能
const handleRowClick = (row, column, event) => {
  // 检查点击的目标是否为按钮或按钮内的元素
  const target = event.target
  const isButton = target.tagName === 'BUTTON' ||
    target.closest('button') ||
    target.classList.contains('custom-button') ||
    target.closest('.custom-button') ||
    target.closest('.action-buttons')

  // 如果点击的是按钮，不执行任何额外功能
  if (isButton) {
    return
  }

  // 行点击不执行额外操作
}

// 表单提交成功
const handleFormSuccess = () => {
  formDialogVisible.value = false
  loadDepartmentList()
  loadDepartmentTree() // 重新加载部门树数据
}
</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;

/**
 * ==============================
 * 页面基础样式
 * ==============================
 */
.department-management {
  background-color: var(--bg-secondary);
  min-height: calc(100vh - var(--header-height));

  .content-wrapper {
    background: var(--bg-primary);
    border-radius: var(--border-radius-large);
    box-shadow: var(--shadow-light);
    border: 1px solid var(--border-color-light);
    overflow: hidden;
  }

  .department-section {
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

.form-tip {
  font-size: var(--font-size-sm);
  color: var(--text-tertiary);
  margin-top: var(--spacing-mini);
}

/**
 * ==============================
 * 响应式适配
 * ==============================
 */
@include respond-to(md) {
  .department-management {
    .content-wrapper {
      margin: var(--spacing-medium);
    }

    .department-section {
      padding: var(--spacing-medium);
    }
  }
}

@include respond-to(sm) {
  .department-management {
    padding: var(--spacing-sm);

    .content-wrapper {
      margin: var(--spacing-sm);
      border-radius: var(--border-radius-base);
    }

    .department-section {
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

/* 父部门信息样式 */
.parent-department-info {
  background: var(--bg-secondary);
  border: 1px solid var(--border-color-light);
  border-radius: var(--border-radius-md);
  padding: var(--spacing-medium);
  margin-top: var(--spacing-small);

  .parent-info-header {
    @include flex-start;
    margin-bottom: var(--spacing-medium);
    padding-bottom: var(--spacing-small);
    border-bottom: 1px solid var(--border-color-light);

    .parent-info-icon {
      color: var(--primary-color);
      font-size: var(--font-size-medium);
      margin-right: var(--spacing-small);
    }

    .parent-info-title {
      font-size: var(--font-size-base);
      font-weight: var(--font-weight-medium);
      color: var(--text-primary);
    }
  }

  .parent-info-content {
    .parent-info-item {
      @include flex-start;
      margin-bottom: var(--spacing-small);

      &:last-child {
        margin-bottom: 0;
      }

      .info-label {
        font-size: var(--font-size-sm);
        color: var(--text-secondary);
        font-weight: var(--font-weight-medium);
        min-width: 80px;
        flex-shrink: 0;
      }

      .info-value {
        font-size: var(--font-size-sm);
        color: var(--text-primary);
        word-break: break-word;
        flex: 1;
      }
    }
  }
}

/* 深度选择器样式 */
:deep(.el-form-item__label) {
  font-weight: var(--font-weight-medium);
}

:deep(.el-input__count) {
  color: var(--text-tertiary);
}

:deep(.el-tree-select) {
  width: 100%;
}
</style>
