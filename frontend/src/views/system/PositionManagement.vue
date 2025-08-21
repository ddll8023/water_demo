<!-- 岗位管理页面 -->
<template>
  <div class="position-management">
    <!-- 使用页面头部组件 -->
    <PageHeader title="岗位管理" icon="fa-briefcase" description="管理系统岗位信息和职责定义" />

    <div class="content-wrapper">
      <div class="position-section">
        <!-- 搜索区域 -->
        <CommonSearch v-model="searchForm" :items="searchFields" :single-row="true" @search="handleSearch"
          @reset="handleResetSearch">
          <template #actions>
            <CustomButton type="primary" @click="handleAdd" v-permission="'system:manage'">
              <i class="fa fa-plus"></i>
              新增岗位
            </CustomButton>
          </template>
        </CommonSearch>

        <!-- 岗位列表表格 -->
        <div class="table-section">
          <CommonTable :data="positionList" :columns="tableColumns" :loading="loading" :show-selection="false"
            :show-index="true" :show-actions="true" :actions-width="150">


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

          <!-- 独立分页组件 -->
          <CustomPagination v-model:current-page="pagination.currentPage" v-model:page-size="pagination.pageSize"
            :total="pagination.total" :page-sizes="[10, 20, 50, 100]" @size-change="handleSizeChange"
            @current-change="handleCurrentChange" />
        </div>
      </div>
    </div>

    <!-- 合并 PositionForm 组件 -->
    <CustomDialog v-model:visible="formDialogVisible" :title="isEdit ? '编辑岗位' : '新增岗位'"
      width="var(--panel-height-default)" :close-on-click-modal="false" @cancel="handleFormDialogClose"
      @confirm="handlePositionSubmit" :loading="submitLoading">
      <CommonForm ref="positionFormRef" v-model="positionForm" :items="formItems" :rules="positionFormRules"
        label-width="100px" label-position="right" :show-actions="false"
        @update:modelValue="(value) => Object.assign(positionForm, value)" />
    </CustomDialog>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import CommonSearch from '@/components/Common/CommonSearch.vue'
import CommonTable from '@/components/Common/CommonTable.vue'
import CustomButton from '@/components/Common/CustomButton.vue'
import CustomDialog from '@/components/Common/CustomDialog.vue'
import CommonForm from '@/components/Common/CommonForm.vue'
import CustomPagination from '@/components/Common/CustomPagination.vue'
import {
  getPositionList,
  deletePosition,
  createPosition,
  updatePosition,
  checkPositionNameAvailable
} from '@/api/position'
import { formatDateTime } from '@/utils/shared/common'
import PageHeader from '@/components/Common/PageHeader.vue'

// =============================================
// 状态与数据定义
// =============================================

// 响应式数据
const loading = ref(false)
const positionList = ref([])
const formDialogVisible = ref(false)
const currentPosition = ref({})
const isEdit = ref(false)

// 搜索表单 - 符合设计规范
const searchForm = reactive({
  keyword: ''
})

// 分页信息
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// =============================================
// 配置项定义
// =============================================

// 搜索字段配置 - 符合设计规范
const searchFields = [
  {
    prop: 'keyword',
    label: '岗位名称',
    type: 'input',
    placeholder: '请输入岗位名称',
    width: '240px',
    labelWidth: 'var(--form-label-width-standard)'
  }
]

// 处理岗位名称输入框失去焦点事件
const handleNameBlur = async (event) => {
  const value = event.target.value
  if (!value) return

  try {
    const excludeId = isEdit.value ? currentPosition.value.id : null
    const result = await checkPositionNameAvailable(value, excludeId)
    if (!result.available) {
      // 如果名称不可用，设置表单项的错误状态
      positionFormRef.value?.validateField('name')
    }
  } catch (error) {
    console.error('检查岗位名称可用性失败', error)
  }
}

// 表格列配置 - 符合设计规范
const tableColumns = [
  { prop: 'id', label: 'ID', width: 80, sortable: true },
  { prop: 'name', label: '岗位名称', width: 150, sortable: true },
  { prop: 'description', label: '岗位描述', minWidth: 200, showOverflowTooltip: true },
  { prop: 'responsibilities', label: '岗位职责', minWidth: 200, showOverflowTooltip: true },
  { prop: 'level', label: '岗位级别', width: 120 },
  { prop: 'createdAt', label: '创建时间', width: 160, slot: 'createdAt', sortable: true }
]

// =============================================
// 数据加载函数
// =============================================

// 加载岗位列表
const loadPositionList = async () => {
  try {
    loading.value = true
    const params = {
      page: pagination.currentPage,
      size: pagination.pageSize,
      ...searchForm
    }

    const response = await getPositionList(params)
    positionList.value = response.items
    pagination.total = response.total
  } catch (error) {
    ElMessage.error('加载岗位列表失败')
  } finally {
    loading.value = false
  }
}



// 表单配置项
const formItems = ref([
  {
    prop: 'name',
    label: '岗位名称',
    type: 'input',
    placeholder: '请输入岗位名称',
    maxlength: 50,
    showWordLimit: true,
    clearable: true,
    span: 16,
    events: {
      blur: handleNameBlur
    }
  },
  {
    prop: 'description',
    label: '岗位描述',
    type: 'textarea',
    rows: 4,
    placeholder: '请输入岗位描述',
    maxlength: 500,
    showWordLimit: true,
    span: 16
  },
  {
    prop: 'responsibilities',
    label: '岗位职责',
    type: 'textarea',
    rows: 4,
    placeholder: '请输入岗位职责',
    maxlength: 500,
    showWordLimit: true,
    span: 16
  },
  {
    prop: 'level',
    label: '岗位级别',
    type: 'input', // 改为input类型
    placeholder: '请输入岗位级别', // 修改占位符提示
    maxlength: 50, // 添加最大长度限制
    clearable: true,
    span: 16
  }
])

// =============================================
// 生命周期钩子
// =============================================

onMounted(() => {
  loadPositionList()
})

// =============================================
// 搜索与过滤函数
// =============================================

// 搜索处理
const handleSearch = () => {
  pagination.currentPage = 1
  loadPositionList()
}

// 重置搜索
const handleResetSearch = () => {
  searchForm.keyword = ''
  pagination.currentPage = 1
  loadPositionList()
}

// =============================================
// 岗位管理操作函数
// =============================================

// 新增岗位
const handleAdd = () => {
  currentPosition.value = {}
  isEdit.value = false
  formDialogVisible.value = true
}

// 编辑岗位
const handleEdit = (row) => {
  currentPosition.value = { ...row }
  isEdit.value = true
  formDialogVisible.value = true
}

// 删除岗位
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除岗位"${row.name}"吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await deletePosition(row.id)
    ElMessage.success('删除成功')
    loadPositionList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// =============================================
// 分页处理函数
// =============================================

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.currentPage = 1
  loadPositionList()
}

// 当前页变化
const handleCurrentChange = (page) => {
  pagination.currentPage = page
  loadPositionList()
}

// =============================================
// 岗位表单相关 (合并 PositionForm)
// =============================================

// =============================================
// 岗位表单相关 (合并 PositionForm)
// =============================================
const positionFormRef = ref()
const submitLoading = ref(false)

// 表单数据
const positionForm = reactive({
  name: '',
  description: '',
  responsibilities: '',
  level: ''
})



// 表单验证规则
const positionFormRules = {
  name: [
    { required: true, message: '请输入岗位名称', trigger: 'blur' }
  ],
  description: [
    { max: 500, message: '岗位描述不能超过500个字符', trigger: 'blur' }
  ],
  responsibilities: [
    { max: 500, message: '岗位职责不能超过500个字符', trigger: 'blur' }
  ]
}

// 监听表单数据变化
watch(
  () => currentPosition.value,
  (newData) => {
    if (newData && Object.keys(newData).length > 0) {
      // 使用深拷贝确保数据不共享引用
      const newFormData = {
        name: newData.name || '',
        description: newData.description || '',
        responsibilities: newData.responsibilities || '',
        level: newData.level || ''
      };

      // 确保完全替换positionForm的内容而不是合并
      Object.keys(positionForm).forEach(key => {
        delete positionForm[key];
      });
      Object.assign(positionForm, newFormData);
    }
  },
  { immediate: true, deep: true }
)

// 监听表单对话框显示状态
watch(
  () => formDialogVisible.value,
  (visible) => {
    if (visible) {
      nextTick(() => {
        positionFormRef.value?.clearValidate()
      })
    } else {
      // 重置表单
      Object.assign(positionForm, {
        name: '',
        description: '',
        responsibilities: '',
        level: ''
      })
      // 确保清除验证状态
      nextTick(() => {
        positionFormRef.value?.clearValidate()
      })
    }
  }
)

// =============================================
// 表单相关函数
// =============================================





// 关闭表单对话框
const handleFormDialogClose = () => {
  formDialogVisible.value = false
}

// 提交表单
const handlePositionSubmit = async () => {
  try {
    const valid = await positionFormRef.value.validate();
    if (!valid) return;

    submitLoading.value = true;

    // 构建一个新的数据对象，避免引用问题
    const formData = {
      name: positionForm.name,
      description: positionForm.description,
      responsibilities: positionForm.responsibilities,
      level: positionForm.level
    };

    if (isEdit.value) {
      await updatePosition(currentPosition.value.id, formData);
      ElMessage.success('更新岗位成功');
    } else {
      await createPosition(formData);
      ElMessage.success('创建岗位成功');
    }

    handleFormSuccess();
  } catch (error) {
    console.error('API提交错误:', error);
    ElMessage.error(isEdit.value ? '更新岗位失败' : '创建岗位失败');
  } finally {
    submitLoading.value = false;
  }
};

// 表单提交成功
const handleFormSuccess = () => {
  formDialogVisible.value = false
  loadPositionList()
}





</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;

.position-management {
  background-color: var(--bg-secondary);
  min-height: calc(100vh - var(--header-height));

  .content-wrapper {
    background: var(--bg-primary);
    border-radius: var(--border-radius-large);
    box-shadow: var(--shadow-light);
    border: 1px solid var(--border-color-light);
    overflow: hidden;
  }

  .position-section {
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

// 响应式适配
@include respond-to(sm) {
  .position-management {
    padding: var(--spacing-sm);

    .position-section {
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

/* 深度选择器样式 */
:deep(.el-form-item__label) {
  font-weight: var(--font-weight-medium);
}

:deep(.el-input__count) {
  color: var(--text-tertiary);
}
</style>
