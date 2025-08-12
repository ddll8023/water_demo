<template>
  <div class="dictionary-management">
    <!-- 使用页面头部组件 -->
    <PageHeader title="字典管理" icon="fa-book" description="管理系统数据字典、枚举值和配置参数" />

    <!-- 主体内容区域 -->
    <div class="main-content">
      <el-row :gutter="20">
        <!-- 字典类型列表 -->
        <el-col :span="8">
          <el-card class="type-card">
            <template #header>
              <div class="card-header">
                <span>字典类型</span>
                <CustomButton type="primary" size="small" @click="handleAddType">
                  <i class="fa fa-plus"></i>
                  新增
                </CustomButton>
              </div>
            </template>

            <div class="type-list">
              <div v-for="type in dictionaryTypes" :key="type.id" class="type-item"
                :class="{ active: selectedType?.id === type.id }" @click="handleTypeClick(type)">
                <div class="type-info">
                  <div class="type-name">{{ type.typeName }}</div>
                  <div class="type-code">{{ type.typeCode }}</div>
                  <div class="type-count">{{ type.dataCount }}项</div>
                </div>
                <div class="type-actions">
                  <CustomButton type="text" text-type="primary" size="small" @click.stop="handleEditType(type)">
                    <i class="fa fa-edit"></i>
                  </CustomButton>
                  <CustomButton type="text" text-type="danger" size="small" @click.stop="handleDeleteType(type)">
                    <i class="fa fa-trash"></i>
                  </CustomButton>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>

        <!-- 字典项列表 -->
        <el-col :span="16">
          <el-card class="item-card">
            <template #header>
              <div class="card-header">
                <span>字典项管理</span>
                <div class="header-actions">
                  <CustomButton type="primary" size="small" :disabled="!selectedType" @click="handleAddItem">
                    <i class="fa fa-plus"></i>
                    新增字典项
                  </CustomButton>
                </div>
              </div>
            </template>

            <div v-if="selectedType" class="item-content">
              <!-- 字典项表格 -->
              <div class="table-container">
                <CommonTable :data="paginatedItems" :columns="tableColumns" :show-toolbar="false"
                  :show-selection="false" :show-index="false" :show-actions="true" :actions-width="150"
                  :actions-fixed="true">
                  <template #isActive="{ row }">
                    <el-tag :type="row.isActive ? 'success' : 'danger'">
                      {{ row.isActive ? '启用' : '禁用' }}
                    </el-tag>
                  </template>

                  <template #actions="{ row }">
                    <div class="action-buttons">
                      <CustomButton type="text" text-type="primary" size="small" @click="handleEditItem(row)">
                        <i class="fa fa-edit"></i>
                        编辑
                      </CustomButton>
                      <CustomButton type="text" text-type="danger" size="small" @click="handleDeleteItem(row)">
                        <i class="fa fa-trash"></i>
                        删除
                      </CustomButton>
                    </div>
                  </template>
                </CommonTable>
              </div>

              <!-- 分页 -->
              <div class="pagination-container">
                <CustomPagination :current-page="pagination.page" :page-size="pagination.size" :total="pagination.total"
                  @update:currentPage="pagination.page = $event" @update:pageSize="pagination.size = $event"
                  @current-change="handleCurrentChange" @size-change="handleSizeChange" />
              </div>
            </div>

            <div v-else class="no-selection">
              <el-empty description="请从左侧选择一个字典类型" />
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 字典类型编辑对话框 -->
    <CustomDialog v-model:visible="typeDialogVisible" :title="typeDialogTitle" width="500px" :loading="typeLoading"
      @confirm="handleTypeSubmit" @cancel="handleTypeDialogClose" @close="handleTypeDialogClose">
      <el-form ref="typeFormRef" :model="typeForm" :rules="typeRules" label-width="100px">
        <el-form-item label="类型名称" prop="typeName">
          <CustomInput v-model="typeForm.typeName" placeholder="请输入类型名称" />
        </el-form-item>
        <el-form-item label="类型编码" prop="typeCode">
          <CustomInput v-model="typeForm.typeCode" placeholder="请输入类型编码" />
        </el-form-item>
        <el-form-item label="排序值">
          <CustomInputNumber v-model="typeForm.sortOrder" :min="0" :max="9999" />
        </el-form-item>
        <el-form-item label="类型描述">
          <CustomTextarea v-model="typeForm.description" :rows="3" placeholder="请输入类型描述" />
        </el-form-item>
        <el-form-item label="状态">
          <CustomRadioGroup v-model="typeForm.isActive" :options="statusOptions" value-key="value" label-key="label" />
        </el-form-item>
      </el-form>
    </CustomDialog>

    <!-- 字典项编辑对话框 -->
    <CustomDialog v-model:visible="itemDialogVisible" :title="itemDialogTitle" width="500px" :loading="itemLoading"
      @confirm="handleItemSubmit" @cancel="handleItemDialogClose" @close="handleItemDialogClose">
      <el-form ref="itemFormRef" :model="itemForm" :rules="itemRules" label-width="100px">
        <el-form-item label="字典标签" prop="dataLabel">
          <CustomInput v-model="itemForm.dataLabel" placeholder="请输入字典标签" />
        </el-form-item>
        <el-form-item label="字典值" prop="dataValue">
          <CustomInput v-model="itemForm.dataValue" placeholder="请输入字典值" />
        </el-form-item>
        <el-form-item label="排序值">
          <CustomInputNumber v-model="itemForm.sortOrder" :min="0" :max="9999" />
        </el-form-item>
        <el-form-item label="状态">
          <CustomRadioGroup v-model="itemForm.isActive" :options="statusOptions" value-key="value" label-key="label" />
        </el-form-item>
        <el-form-item label="备注">
          <CustomTextarea v-model="itemForm.description" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
    </CustomDialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 导入组件
import CommonTable from '@/components/Common/CommonTable.vue'
import CustomInput from '@/components/Common/CustomInput.vue'
import CustomTextarea from '@/components/Common/CustomTextarea.vue'
import CustomInputNumber from '@/components/Common/CustomInputNumber.vue'
import CustomRadioGroup from '@/components/Common/CustomRadioGroup.vue'
import CustomButton from '@/components/Common/CustomButton.vue'
import PageHeader from '@/components/Common/PageHeader.vue'
import CustomPagination from '@/components/Common/CustomPagination.vue'
import CustomDialog from '@/components/Common/CustomDialog.vue'

// 导入API
import {
  getDictTypeList,
  createDictType,
  updateDictType,
  deleteDictType,
  getDictDataByTypeId,
  createDictData,
  updateDictData,
  deleteDictData,
  checkTypeCodeExists
} from '@/api/dictionary'

// ==============================================
// 数据定义
// ==============================================

// 页面状态数据

// 表单引用
const typeFormRef = ref(null)
// 字典项表单引用
const itemFormRef = ref(null)
// 字典类型对话框可见性
const typeDialogVisible = ref(false)
// 字典项对话框可见性
const itemDialogVisible = ref(false)
// 字典类型对话框标题
const typeDialogTitle = ref('')
// 字典项对话框标题
const itemDialogTitle = ref('')
// 选中的字典类型
const selectedType = ref(null)

// 加载状态
const loading = ref(false)
// 字典类型加载状态
const typeLoading = ref(false)
// 字典项加载状态
const itemLoading = ref(false)

// 表格配置
const tableColumns = [
  { prop: 'dataLabel', label: '字典标签', minWidth: 120 },
  { prop: 'dataValue', label: '字典值', minWidth: 100 },
  { prop: 'isActive', label: '状态', width: 80 },
  { prop: 'description', label: '备注', minWidth: 150, showOverflowTooltip: true }
]

// 字典类型表单数据
const typeForm = reactive({
  id: null,
  typeName: '',
  typeCode: '',
  description: '',
  sortOrder: 0,
  isActive: true
})

// 字典项表单数据
const itemForm = reactive({
  id: null,
  typeId: null,
  dataLabel: '',
  dataValue: '',
  sortOrder: 0,
  isActive: true,
  description: ''
})

// 分页数据
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 业务数据
// 字典类型列表
const dictionaryTypes = ref([])
// 字典项列表
const dictionaryItems = ref([])

// 计算属性，用于前端分页
const paginatedItems = computed(() => {
  const start = (pagination.page - 1) * pagination.size
  const end = start + pagination.size
  return dictionaryItems.value.slice(start, end)
})

const statusOptions = [
  { label: '启用', value: true },
  { label: '禁用', value: false }
]

// 表单验证规则
const typeRules = {
  typeName: [{ required: true, message: '请输入字典类型名称', trigger: 'blur' }],
  typeCode: [
    { required: true, message: '请输入字典类型编码', trigger: 'blur' },
    {
      validator: async (rule, value, callback) => {
        if (!value) return callback() // 如果为空，交给required规则处理

        if (typeForm.id !== null) {
          try {
            const response = await checkTypeCodeExists(value, typeForm.id)
            if (response.exists) {
              callback(new Error('字典类型编码已存在'))
            } else {
              callback()
            }
          } catch (error) {
            console.error('验证字典类型编码失败:', error)
            callback() // 验证失败时不阻止表单提交
          }
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const itemRules = {
  dataLabel: [{ required: true, message: '请输入字典标签', trigger: 'blur' }],
  dataValue: [
    { required: true, message: '请输入字典值', trigger: 'blur' }
  ]
}

// ==============================================
// 生命周期钩子
// ==============================================

onMounted(async () => {
  await loadDictionaryTypes()
  // 默认选择第一个字典类型
  if (dictionaryTypes.value.length > 0) {
    handleTypeClick(dictionaryTypes.value[0])
  }
})

// ==============================================
// 数据加载方法
// ==============================================

/**
 * 加载字典类型列表
 */
const loadDictionaryTypes = async () => {
  try {
    loading.value = true
    const response = await getDictTypeList({
      page: 1,
      size: 100, // 获取所有类型
      isActive: true
    })

    // response 就是 PagedResponseDTO 对象，直接访问 items 字段
    const items = response.items || []

    dictionaryTypes.value = items.map(item => ({
      id: item.id,
      typeName: item.typeName,
      typeCode: item.typeCode,
      description: item.description,
      sortOrder: item.sortOrder,
      isActive: item.isActive,
      dataCount: item.dataCount || 0
    }))
  } catch (error) {
    ElMessage.error('加载字典类型失败：' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

/**
 * 加载字典数据列表
 */
const loadDictionaryItems = async () => {
  if (!selectedType.value) {
    dictionaryItems.value = []
    return
  }

  try {
    itemLoading.value = true
    const response = await getDictDataByTypeId(selectedType.value.id)

    // response 直接是 List<DictDataResponseDTO>，是一个数组
    const items = Array.isArray(response) ? response : []
    dictionaryItems.value = items.map(item => ({
      id: item.id,
      typeId: item.typeId,
      dataLabel: item.dataLabel,
      dataValue: item.dataValue,
      description: item.description,
      sortOrder: item.sortOrder,
      isActive: item.isActive
    }))
    pagination.total = items.length // 临时修复：使用返回的数组长度作为总数
  } catch (error) {
    ElMessage.error('加载字典数据失败：' + (error.message || '未知错误'))
  } finally {
    itemLoading.value = false
  }
}

// ==============================================
// 字典类型处理方法
// ==============================================

/**
 * 处理类型点击事件
 */
const handleTypeClick = (type) => {
  selectedType.value = type
  loadDictionaryItems()
}

/**
 * 处理新增类型事件
 */
const handleAddType = () => {
  typeDialogTitle.value = '新增字典类型'
  resetTypeForm()
  typeDialogVisible.value = true
}

/**
 * 处理编辑类型事件
 */
const handleEditType = (type) => {
  typeDialogTitle.value = '编辑字典类型'
  Object.assign(typeForm, type)
  typeDialogVisible.value = true
}

/**
 * 处理删除类型事件
 */
const handleDeleteType = async (type) => {
  try {
    await ElMessageBox.confirm(`确定要删除字典类型"${type.typeName}"吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    typeLoading.value = true
    await deleteDictType(type.id)
    ElMessage.success('删除成功')
    await loadDictionaryTypes()

    // 如果删除的是当前选中的类型，清空选中状态
    if (selectedType.value && selectedType.value.id === type.id) {
      selectedType.value = null
      dictionaryItems.value = []
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败：' + (error.message || '未知错误'))
    } else {
      ElMessage.info('已取消删除')
    }
  } finally {
    typeLoading.value = false
  }
}

/**
 * 处理类型对话框关闭事件
 */
const handleTypeDialogClose = () => {
  resetTypeForm()
}

/**
 * 处理类型表单提交事件
 */
const handleTypeSubmit = async () => {
  try {
    // 在验证前设置loading状态
    typeLoading.value = true
    await typeFormRef.value?.validate()

    if (typeForm.id) {
      // 更新字典类型
      await updateDictType(typeForm.id, typeForm)
      ElMessage.success('更新成功')
    } else {
      // 创建字典类型
      await createDictType(typeForm)
      ElMessage.success('创建成功')
    }

    typeDialogVisible.value = false
    await loadDictionaryTypes()
  } catch (error) {
    console.error('字典类型表单提交错误:', error)
    // 打印完整的错误对象以查看所有信息
    console.dir(error)

    if (error.name !== 'ValidationError') {
      ElMessage.error('操作失败：' + (error.message || '未知错误'))
    }
  } finally {
    typeLoading.value = false
  }
}

/**
 * 重置类型表单
 */
const resetTypeForm = () => {
  Object.assign(typeForm, {
    id: null,
    typeName: '',
    typeCode: '',
    description: '',
    sortOrder: 0,
    isActive: true
  })
  typeFormRef.value?.clearValidate()
}

// ==============================================
// 字典项处理方法
// ==============================================

/**
 * 处理新增字典项事件
 */
const handleAddItem = () => {
  if (!selectedType.value) {
    ElMessage.warning('请先选择字典类型')
    return
  }

  itemDialogTitle.value = '新增字典项'
  resetItemForm()
  itemForm.typeId = selectedType.value.id
  itemDialogVisible.value = true
}

/**
 * 处理编辑字典项事件
 */
const handleEditItem = (item) => {
  itemDialogTitle.value = '编辑字典项'
  Object.assign(itemForm, item)
  itemDialogVisible.value = true
}

/**
 * 处理删除字典项事件
 */
const handleDeleteItem = async (item) => {
  try {
    await ElMessageBox.confirm(`确定要删除字典项"${item.dataLabel}"吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    itemLoading.value = true
    await deleteDictData(item.id)
    ElMessage.success('删除成功')
    await loadDictionaryItems()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败：' + (error.message || '未知错误'))
    } else {
      ElMessage.info('已取消删除')
    }
  } finally {
    itemLoading.value = false
  }
}

/**
 * 处理字典项对话框关闭事件
 */
const handleItemDialogClose = () => {
  resetItemForm()
}

/**
 * 处理字典项表单提交事件
 */
const handleItemSubmit = async () => {
  try {
    // 在验证前设置loading状态
    itemLoading.value = true
    await itemFormRef.value?.validate()

    if (itemForm.id) {
      // 更新字典数据
      await updateDictData(itemForm.id, itemForm)
      ElMessage.success('更新成功')
    } else {
      // 创建字典数据
      await createDictData(itemForm)
      ElMessage.success('创建成功')
    }

    itemDialogVisible.value = false
    await loadDictionaryItems()
  } catch (error) {
    console.error('字典项表单提交错误:', error)
    // 打印完整的错误对象以查看所有信息
    console.dir(error)

    if (error.name !== 'ValidationError') {
      ElMessage.error('操作失败：' + (error.message || '未知错误'))
    }
  } finally {
    itemLoading.value = false
  }
}

/**
 * 重置字典项表单
 */
const resetItemForm = () => {
  Object.assign(itemForm, {
    id: null,
    typeId: null,
    dataLabel: '',
    dataValue: '',
    sortOrder: 0,
    isActive: true,
    description: ''
  })
  itemFormRef.value?.clearValidate()
}

// ==============================================
// 其他功能方法
// ==============================================



// ==============================================
// 分页处理方法
// ==============================================

/**
 * 处理每页数量变化
 */
const handleSizeChange = (size) => {
  pagination.size = size
  loadDictionaryItems()
}

/**
 * 处理页码变化
 */
const handleCurrentChange = (page) => {
  pagination.page = page
  loadDictionaryItems()
}
</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;

.dictionary-management {

  /* 主内容区样式 */
  .main-content {

    .type-card,
    .item-card {
      // 使用CSS变量计算高度，考虑页面头部和间距
      height: calc(100vh - var(--page-min-height-offset));
      min-height: 600px;
      display: flex;
      flex-direction: column;

      :deep(.el-card__body) {
        display: flex;
        flex-direction: column;
        flex: 1;
        min-height: 0; // 关键：允许内部出现滚动
      }

      .card-header {
        @include flex-between;

        .header-actions {
          display: flex;
          gap: var(--spacing-medium);
        }
      }
    }

    /* 字典类型列表样式 */
    .type-list {
      // 计算可用高度：总高度 - 卡片头部 - 内边距
      max-height: calc(100vh - var(--page-min-height-offset) - 80px);
      min-height: calc(600px - 80px);
      overflow-y: auto;
      @include custom-scrollbar();

      .type-item {
        @include flex-between;
        padding: var(--spacing-medium);
        border: 1px solid var(--el-border-color-light);
        border-radius: var(--border-radius-base);
        margin-bottom: var(--spacing-small);
        cursor: pointer;
        transition: var(--transition-base);

        &:hover {
          border-color: var(--primary-color);
          background-color: #f0f9ff;
        }

        &.active {
          border-color: var(--primary-color);
          background-color: #e6f7ff;
        }

        .type-info {
          .type-name {
            font-size: var(--font-size-base);
            font-weight: var(--font-weight-medium);
            color: var(--text-primary);
            margin-bottom: var(--spacing-mini);
          }

          .type-code {
            font-size: var(--font-size-extra-small);
            color: var(--text-tertiary);
            margin-bottom: var(--spacing-micro);
          }

          .type-count {
            font-size: var(--font-size-extra-small);
            color: var(--primary-color);
          }
        }

        .type-actions {
          display: flex;
          gap: var(--spacing-mini);
        }
      }
    }

    /* 字典项内容区样式 */
    .item-content {
      flex: 1;
      min-height: 0; // 关键：允许内部出现滚动
      display: flex;
      flex-direction: column;

      .table-container {
        // 作为可收缩区域承载表格滚动
        flex: 1 1 0%;
        min-height: 0; // 允许内部滚动
        height: 0; // 为子元素的100%高度提供确定的参考
        overflow: hidden; // 由内部表格容器负责滚动
      }

      .pagination-container {
        // 为分页组件分配固定高度空间
        flex-shrink: 0;
        height: var(--pagination-container-height);
        @include flex-center;
        padding: var(--spacing-small) 0;
        border-top: 1px solid var(--el-border-color-lighter);
        background-color: var(--bg-tertiary);
      }
    }

    /* 无选择状态样式 */
    .no-selection {
      @include flex-center;
      height: calc(100vh - var(--page-min-height-offset) - 60px);
      min-height: calc(600px - 60px);
    }
  }
}
</style>
