<!-- 设施管理通用组件 - 支持所有工程信息服务模块的CRUD操作 -->
<template>
  <div class="facility-management">
    <!-- 搜索区域 -->
    <CommonSearch v-model="searchForm" :items="searchFields" :single-row="true" @search="handleSearch"
      @reset="handleResetSearch">
      <template #actions>
        <CustomButton type="primary" @click="handleAdd" v-permission="'business:manage'">
          <i class="fa fa-plus"></i>
          新增
        </CustomButton>
      </template>
    </CommonSearch>

    <!-- 表格区域 -->
    <div class="table-section">
      <CommonTable :data="tableData" :columns="enhancedColumns" :loading="loading" :total="pagination.total"
        :current-page="pagination.currentPage" :page-size="pagination.pageSize" :show-index="true" :show-actions="true"
        :actions-width="120" :actions-fixed="false" @size-change="handleSizeChange"
        @current-change="handleCurrentChange" @row-click="handleRowClick">
        <!-- 自定义列渲染 -->
        <template v-for="column in tableColumns" :key="column.prop" #[column.prop]="{ row }">
          <span v-if="column.type === 'tag'">
            <el-tag :type="getTagType(row[column.prop], column.tagMap)" size="small">
              {{ getTagText(row[column.prop], column.tagMap) }}
            </el-tag>
          </span>
          <span v-else-if="column.type === 'date' || column.type === 'datetime'">
            {{ formatDateTime(row[column.prop], "YYYY-MM-DD") }}
          </span>
          <span v-else-if="column.type === 'dict'">
            {{ getDictLabelSync(column.dictType, row[column.prop]) }}
          </span>
          <span v-else>
            {{ row[column.prop] }}
          </span>
        </template>

        <!-- 操作列 -->
        <template #actions="{ row }">
          <CustomButton type="text" text-type="primary" @click="handleEdit(row)" v-permission="'business:manage'">
            编辑
          </CustomButton>
          <CustomButton type="text" text-type="danger" @click="handleDelete(row)" v-permission="'business:manage'">
            删除
          </CustomButton>
        </template>
      </CommonTable>

      <!-- 添加分页组件 -->
      <CustomPagination v-model:currentPage="pagination.currentPage" v-model:pageSize="pagination.pageSize"
        :total="pagination.total" :pageSizes="[10, 20, 50, 100]" @size-change="handleSizeChange"
        @current-change="handleCurrentChange" />
    </div>

    <!-- 新增/编辑弹窗 -->
    <CustomDialog v-model:visible="dialogVisible" :title="dialogTitle" width="60%" :close-on-click-modal="false"
      :close-on-press-escape="false" :loading="formLoading" @cancel="dialogVisible = false" @confirm="handleSubmit">
      <CommonForm ref="formRef" v-model="formData" :items="formFields" :rules="formRules" :submit-loading="formLoading"
        :label-width="'var(--form-label-width-detail)'" :show-actions="false" />
    </CustomDialog>

  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import CommonSearch from '@/components/Common/CommonSearch.vue'
import CommonTable from '@/components/Common/CommonTable.vue'
import CommonForm from '@/components/Common/CommonForm.vue'
import CustomButton from '@/components/Common/CustomButton.vue'
import CustomDialog from '@/components/Common/CustomDialog.vue'
import CustomPagination from '@/components/Common/CustomPagination.vue'
import { formatDateTime } from '@/utils/shared/common'
import { useDictionary } from '@/composables/useDictionary'

// ===========================
// 组件属性与初始化
// ===========================
// Props
const props = defineProps({
  facilityType: {
    type: String,
    required: true
  },
  facilityName: {
    type: String,
    required: true
  },
  searchFields: {
    type: Array,
    required: true
  },
  tableColumns: {
    type: Array,
    required: true
  },
  formFields: {
    type: Array,
    required: true
  },
  apiFunctions: {
    type: Object,
    required: true
  },
  validationRules: {
    type: Object,
    default: () => ({})
  }
})

// Emits
const emit = defineEmits(['refresh'])

// 字典功能
const { getDictData } = useDictionary()
const dictMaps = ref({}) // 存储字典映射

// 响应式数据
const loading = ref(false)
const tableData = ref([])
const searchForm = ref({})
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 弹窗相关
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formData = ref({})
const formLoading = ref(false)
const formRef = ref(null)
const isEdit = ref(false)
const editId = ref(null)

// 生命周期钩子
onMounted(async () => {
  await loadDictMaps() // 先加载字典数据
  loadData() // 再加载表格数据
})

// 监听搜索字段变化
watch(() => props.searchFields, () => {
  searchForm.value = {}
}, { immediate: true })

// ===========================
// 计算属性
// ===========================
const enhancedColumns = computed(() => {
  return props.tableColumns.map(column => {
    if (column.prop === 'actions') {
      return {
        ...column,
        type: 'actions'
      }
    }
    return column
  })
})

// 表单验证规则
const formRules = computed(() => {
  const rules = {}

  // 获取验证规则配置
  const validationRules = props.validationRules || {}

  props.formFields.forEach(field => {
    const fieldRules = []

    // 必填验证
    if (field.required) {
      fieldRules.push({
        required: true,
        message: `请${field.type === 'select' ? '选择' : '输入'}${field.label}`,
        trigger: field.type === 'select' ? 'change' : 'blur'
      })
    }

    // 添加自定义验证规则
    if (field.rules && validationRules[field.rules]) {
      fieldRules.push(...validationRules[field.rules])
    }

    // 如果有验证规则，添加到rules对象
    if (fieldRules.length > 0) {
      rules[field.prop] = fieldRules
    }
  })

  return rules
})

// ===========================
// 数据加载与管理模块
// ===========================
/**
 * 加载表格数据
 */
const loadData = async () => {
  if (!props.apiFunctions.getList) {
    if (import.meta.env.MODE === 'development') {
      console.warn('未提供getList API函数')
    }
    return
  }

  loading.value = true
  try {
    const params = {
      page: pagination.currentPage,
      size: pagination.pageSize,
      ...searchForm.value
    }

    // 过滤空值参数，优化请求
    const filteredParams = Object.fromEntries(
      Object.entries(params).filter(([key, value]) => {
        // 保留分页参数
        if (key === 'page' || key === 'size') return true
        // 过滤空值
        return value !== '' && value !== null && value !== undefined
      })
    )

    const response = await props.apiFunctions.getList(filteredParams)

    // 由于响应拦截器已经处理了错误，这里直接使用数据
    tableData.value = response.items || []
    pagination.total = response.total || 0
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

/**
 * 处理搜索
 */
const handleSearch = () => {
  pagination.currentPage = 1
  loadData()
}

/**
 * 重置搜索条件
 */
const handleResetSearch = () => {
  searchForm.value = {}
  pagination.currentPage = 1
  // loadData() - 移除这行，防止重复请求
}

/**
 * 刷新数据 - 暴露给父组件使用
 */
const refreshData = () => {
  loadData()
}

// 暴露给父组件的方法
defineExpose({
  refreshData
})

// ===========================
// 表格操作模块
// ===========================
/**
 * 处理每页数量变化
 */
const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.currentPage = 1
  loadData()
}

/**
 * 处理页码变化
 */
const handleCurrentChange = (page) => {
  pagination.currentPage = page
  loadData()
}

/**
 * 处理行点击事件
 */
const handleRowClick = (row) => {
  // 行点击事件
}

// ===========================
// CRUD操作模块
// ===========================
/**
 * 处理添加操作
 */
const handleAdd = () => {
  isEdit.value = false
  editId.value = null
  dialogTitle.value = `新增${props.facilityName}`

  // 完全清空表单数据
  const emptyData = {}
  props.formFields.forEach(field => {
    if (field.prop) {
      // 根据字段类型设置默认值
      switch (field.type) {
        case 'number':
        case 'input-number':
          emptyData[field.prop] = null
          break
        case 'switch':
          emptyData[field.prop] = false
          break
        case 'checkbox':
        case 'checkbox-group':
          emptyData[field.prop] = []
          break
        case 'date':
        case 'datetime':
        case 'time':
          emptyData[field.prop] = null
          break
        case 'select':
          emptyData[field.prop] = null
          break
        default:
          emptyData[field.prop] = ''
      }
    }
  })

  formData.value = emptyData
  dialogVisible.value = true

  // 在下一个tick清空表单验证
  nextTick(() => {
    formRef.value?.clearValidate()
  })
}

/**
 * 处理编辑操作
 */
const handleEdit = (row) => {
  isEdit.value = true
  editId.value = row.id
  dialogTitle.value = `编辑${props.facilityName}`
  formData.value = { ...row }
  dialogVisible.value = true
}

/**
 * 处理删除操作
 */
const handleDelete = async (row) => {
  if (!props.apiFunctions.delete) {
    ElMessage.warning('未提供删除API函数')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要删除${props.facilityName}"${row.name || row.title}"吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await props.apiFunctions.delete(row.id)

    // 删除成功后刷新数据，并通知父组件刷新相关数据
    ElMessage.success('删除成功')
    loadData()
    emit('refresh')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// ===========================
// 表单处理模块
// ===========================
/**
 * 处理表单提交
 */
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
  } catch (error) {
    return
  }

  const apiFunction = isEdit.value ? props.apiFunctions.update : props.apiFunctions.create

  if (!apiFunction) {
    ElMessage.warning(`未提供${isEdit.value ? '更新' : '创建'}API函数`)
    return
  }

  formLoading.value = true
  try {
    // 为消毒药材自动添加waterPlantId字段
    const submitData = { ...formData.value }
    if (props.facilityType === 'disinfection-materials' && !submitData.waterPlantId) {
      submitData.waterPlantId = 1 // 设置默认水厂ID
    }

    if (isEdit.value) {
      await apiFunction(editId.value, submitData)
    } else {
      await apiFunction(submitData)
    }

    // 操作成功后关闭弹窗，刷新数据，并通知父组件刷新相关数据
    ElMessage.success(`${isEdit.value ? '更新' : '创建'}成功`)
    dialogVisible.value = false
    loadData()
    emit('refresh')
  } catch (error) {
    console.error(`${isEdit.value ? '更新' : '创建'}失败:`, error)
    ElMessage.error(`${isEdit.value ? '更新' : '创建'}失败`)
  } finally {
    formLoading.value = false
  }
}

// ===========================
// 辅助工具模块
// ===========================
/**
 * 获取标签类型
 */
const getTagType = (value, tagMap) => {
  if (!tagMap) return 'info'
  const item = tagMap.find(item => item.value === value)
  return item ? item.type : 'info'
}

/**
 * 获取标签文本
 */
const getTagText = (value, tagMap) => {
  if (!tagMap) return value
  const item = tagMap.find(item => item.value === value)
  return item ? item.label : value
}

/**
 * 获取字典标签
 */
const getDictLabelSync = (dictType, value) => {
  if (!value || !dictType || !dictMaps.value[dictType]) {
    return value || ''
  }

  const dictMap = dictMaps.value[dictType]
  const item = dictMap.find(item => String(item.value) === String(value))
  return item ? item.label : value
}

/**
 * 加载字典映射
 */
const loadDictMaps = async () => {
  try {
    // 收集所有需要的字典类型
    const dictTypes = new Set()
    props.tableColumns.forEach(column => {
      if (column.type === 'dict' && column.dictType) {
        dictTypes.add(column.dictType)
      }
    })

    // 加载字典数据
    for (const dictType of dictTypes) {
      try {
        const dictData = await getDictData(dictType)
        dictMaps.value[dictType] = dictData
      } catch (error) {
        console.error(`加载字典失败: ${dictType}`, error)
        dictMaps.value[dictType] = []
      }
    }
  } catch (error) {
    console.error('加载字典数据失败:', error)
  }
}
</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;

.facility-management {
  padding: var(--spacing-large);
}

.table-section {
  margin-top: var(--spacing-base);
}
</style>
