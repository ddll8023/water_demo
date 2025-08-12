<template>
  <div class="management-service">
    <!-- 使用页面头部组件 -->
    <PageHeader title="管理信息服务" icon="fa-sitemap" description="业务层面的部门信息和人员信息档案管理" />

    <!-- 功能选项卡 -->
    <TabSection v-model="activeTab" :tabs="tabConfig" />

    <!-- 组织架构管理视图 -->
    <div v-if="activeTab === 'organization'" class="organization-view">
      <el-row :gutter="20">
        <!-- 左侧部门树 -->
        <el-col :span="8">
          <el-card class="tree-card">
            <!-- 部门搜索和控制区域 -->
            <div class="tree-controls">
              <CustomInput v-model="departmentSearchText" placeholder="搜索部门..." clearable
                @input="handleDepartmentSearch" prefix-icon="fa-search" class="search-input" />
              <div class="control-buttons">
                <CustomButton type="primary" size="small" @click="handleAddDepartment" v-permission="'business:manage'">
                  新增
                </CustomButton>
                <CustomButton type="secondary" size="small" @click="handleToggleAll" :disabled="!departmentTree.length">
                  <i class="fa fa-compress"></i>
                  展开/折叠
                </CustomButton>
              </div>
            </div>

            <div class="tree-container">
              <el-tree ref="departmentTreeRef" :data="filteredDepartmentTree" :props="treeProps" node-key="id"
                :expand-on-click-node="false" :highlight-current="true" @node-click="handleDepartmentClick"
                :expanded-keys="expandedKeys" :default-expand-all="true" class="department-tree">
                <template #default="{ node, data }">
                  <span class="tree-node" :class="{ 'tree-node-selected': selectedDepartment.id === data.id }">
                    <span class="node-content">
                      <i class="node-icon fa" :class="{
                        'fa-sitemap': node.level === 1,
                        'fa-building-o': node.level === 2,
                        'fa-users': node.level > 2
                      }"></i>
                      <span class="node-label">{{ node.label }}</span>
                    </span>
                    <span class="node-actions">
                      <CustomButton type="text" text-type="primary" size="small"
                        @click.stop="handleEditDepartment(data)" v-permission="'business:manage'">
                        编辑
                      </CustomButton>
                      <CustomButton type="text" text-type="danger" size="small"
                        @click.stop="handleDeleteDepartment(data)" v-permission="'business:manage'">
                        删除
                      </CustomButton>
                    </span>
                  </span>
                </template>
              </el-tree>
            </div>
          </el-card>
        </el-col>

        <!-- 右侧部门详情面板 -->
        <el-col :span="16">
          <el-card class="detail-card">
            <!-- 部门详情标题 -->
            <div class="detail-header">
              <h3 class="detail-title">
                部门详情：{{ selectedDepartment.name || '请选择部门' }}
              </h3>
            </div>

            <!-- 部门详情标签页 -->
            <div v-if="selectedDepartment.id" class="department-details">
              <el-tabs v-model="activeDetailTab" class="detail-tabs">
                <!-- 基本信息标签页 -->
                <el-tab-pane label="基本信息" name="basic">
                  <template #label>
                    <span class="tab-label">
                      <i class="fa fa-info-circle"></i>
                      基本信息
                    </span>
                  </template>
                  <div class="basic-info-content">
                    <!-- 部门基本信息卡片 -->
                    <div class="department-info-card">
                      <div class="info-header">
                        <div class="info-title-section">
                          <h4 class="department-name">{{ selectedDepartment.name }}</h4>
                          <p class="department-description">{{ selectedDepartment.duty }}</p>
                        </div>
                        <div class="info-actions">
                          <CustomButton type="primary" @click="handleEditDepartment(selectedDepartment)"
                            v-permission="'business:manage'">
                            <i class="fa fa-edit"></i>
                            编辑信息
                          </CustomButton>
                        </div>
                      </div>

                      <div class="info-grid">
                        <div class="info-item">
                          <div class="info-icon">
                            <i class="fa fa-users"></i>
                          </div>
                          <div class="info-content">
                            <p class="info-label">部门人数</p>
                            <p class="info-value">{{ departmentPersonnel.length }}人</p>
                          </div>
                        </div>

                        <div class="info-item">
                          <div class="info-icon">
                            <i class="fa fa-calendar-o"></i>
                          </div>
                          <div class="info-content">
                            <p class="info-label">成立日期</p>
                            <p class="info-value">{{ formatDateTime(selectedDepartment.createdAt, 'YYYY-MM-DD') }}</p>
                          </div>
                        </div>
                      </div>
                    </div>

                    <!-- 部门职责描述 -->
                    <div class="department-duty-section">
                      <h4 class="section-title">部门职责</h4>
                      <p class="duty-description">
                        {{ selectedDepartment.duty || "无" }}
                      </p>
                    </div>
                  </div>
                </el-tab-pane>

                <!-- 下级部门标签页 -->
                <el-tab-pane label="下级部门" name="subdepartments">
                  <template #label>
                    <span class="tab-label">
                      <i class="fa fa-building-o"></i>
                      下级部门
                    </span>
                  </template>
                  <div class="subdepartments-content">
                    <!-- 下级部门操作区域 -->
                    <div class="subdepartments-header">
                      <CustomButton type="primary" @click="handleAddSubDepartment" v-permission="'business:manage'">
                        <i class="fa fa-plus"></i>
                        新增下级部门
                      </CustomButton>
                    </div>

                    <CommonTable :data="subDepartments" :columns="subDepartmentColumns" :loading="subDepartmentsLoading"
                      :show-selection="true" :show-index="true" @edit="handleEditSubDepartment"
                      @delete="handleDeleteSubDepartment">
                      <template #isActive="{ row }">
                        <el-tag :type="row.isActive ? 'success' : 'danger'" size="small">
                          {{ row.isActive ? '启用' : '禁用' }}
                        </el-tag>
                      </template>
                      <template #actions="{ row, index }">
                        <CustomButton type="text" text-type="primary" size="small"
                          @click.stop="handleEditSubDepartment(row)">
                          编辑
                        </CustomButton>
                        <CustomButton type="text" text-type="danger" size="small"
                          @click.stop="handleDeleteSubDepartment(row)">
                          删除
                        </CustomButton>

                      </template>
                    </CommonTable>
                  </div>
                </el-tab-pane>

                <!-- 部门人员标签页 -->
                <el-tab-pane label="部门人员" name="personnel">
                  <template #label>
                    <span class="tab-label">
                      <i class="fa fa-users"></i>
                      部门人员
                    </span>
                  </template>
                  <div class="personnel-content">
                    <CommonTable :data="departmentPersonnel" :columns="personnelColumns" :loading="personnelLoading"
                      :show-pagination="false" :show-index="true" @selection-change="handlePersonnelSelectionChange">
                      <template #actions="{ row, index }">
                        <CustomButton type="text" text-type="primary" size="small"
                          @click.stop="handleEditPersonnel(row)">
                          编辑
                        </CustomButton>
                        <CustomButton type="text" text-type="danger" size="small"
                          @click.stop="handleDeletePersonnel(row)">
                          删除
                        </CustomButton>
                      </template>
                    </CommonTable>

                    <!-- 部门人员分页器 -->
                    <div class="table-pagination">
                      <CustomPagination :currentPage="departmentPersonnelPage" :pageSize="departmentPersonnelSize"
                        :total="departmentPersonnelTotal" @size-change="handleDepartmentPersonnelSizeChange"
                        @current-change="handleDepartmentPersonnelCurrentChange" />
                    </div>
                  </div>
                </el-tab-pane>
              </el-tabs>
            </div>

            <!-- 空状态 -->
            <div v-else class="empty-state">
              <el-empty description="请从左侧选择部门查看详情" />
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 全局人员列表视图 -->
    <div v-if="activeTab === 'personnel'" class="personnel-view">
      <!-- 搜索区域 -->
      <CommonSearch v-model="personnelSearchForm" :items="personnelSearchFields" :single-row="true"
        @search="handlePersonnelSearch" @reset="handlePersonnelResetSearch">
        <template #actions>
          <CustomButton type="primary" @click="handleAddPersonnel" v-permission="'business:manage'">
            新增
          </CustomButton>
          <CustomButton type="danger" @click="handleBatchDeletePersonnel" :disabled="selectedPersonnel.length === 0"
            v-permission="'business:manage'">
            批量删除
          </CustomButton>
        </template>
      </CommonSearch>

      <!-- 人员表格 -->
      <CommonTable :data="allPersonnelList" :columns="allPersonnelColumns" :loading="personnelListLoading"
        :show-selection="true" :show-index="true" :show-pagination="false" :current-page="personnelCurrentPage"
        :page-size="personnelPageSize" :total="personnelTotal" :show-toolbar="false" @edit="handleEditPersonnel"
        @delete="handleDeletePersonnel" @selection-change="handlePersonnelSelectionChange">
      </CommonTable>

      <!-- 全局人员列表分页器 -->
      <div class="table-pagination">
        <CustomPagination :currentPage="personnelCurrentPage" :pageSize="personnelPageSize" :total="personnelTotal"
          @size-change="handlePersonnelSizeChange" @current-change="handlePersonnelCurrentChange" />
      </div>
    </div>

    <!-- 人员表单弹窗 -->
    <CustomDialog v-model:visible="personnelFormVisible" :title="isEditPersonnel ? '编辑人员信息' : '新增人员'" width="800px"
      :close-on-click-modal="false" :loading="saving" @confirm="handleSavePersonnel"
      @cancel="personnelFormVisible = false">
      <CommonForm ref="personnelFormRef" v-model="currentPersonnel" :items="personnelFormItems"
        :rules="personnelFormRules" :label-width="120" :show-actions="false" />
    </CustomDialog>

    <!-- 部门表单对话框 -->
    <CustomDialog v-model:visible="departmentFormVisible" :title="isEditDepartment ? '编辑部门' : '新增部门'"
      :loading="departmentFormLoading" width="500px" @confirm="handleDepartmentFormSubmit"
      @cancel="departmentFormVisible = false">
      <CommonForm ref="departmentFormRef" v-model="currentDepartmentForm" :items="departmentFormItems"
        :rules="departmentFormRules" :label-width="100" :show-actions="false"> <!-- 修改:model-value为v-model -->
      </CommonForm>
    </CustomDialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import CustomInput from '@/components/Common/CustomInput.vue'
import CommonSearch from '@/components/Common/CommonSearch.vue'
import CommonTable from '@/components/Common/CommonTable.vue'
import CommonForm from '@/components/Common/CommonForm.vue'
import CustomButton from '@/components/Common/CustomButton.vue'
import CustomDialog from '@/components/Common/CustomDialog.vue'
import PageHeader from '@/components/Common/PageHeader.vue'
import TabSection from '@/components/Common/TabSection.vue'
import CustomPagination from '@/components/Common/CustomPagination.vue'


// API接口导入
import {
  getDepartmentList,
  deleteDepartment,
  createDepartment
} from '@/api/department'
import {
  getPersonnelList,
  createPersonnel,
  updatePersonnel,
  deletePersonnel,
  batchDeletePersonnel,
  getDepartmentTree,
  updateDepartment
} from '@/api/management-info'
import { getPositionList } from '@/api/position'
import { formatDateTime, formatLocalTimeForAPI } from '@/utils/shared/common'

/**
 * ----------------------------------------
 * 组件状态和引用
 * ----------------------------------------
 */
// 当前激活的标签页
const activeTab = ref('organization')

// 选项卡配置
const tabConfig = [
  { name: 'organization', label: '组织架构管理', icon: 'fa-sitemap' },
  { name: 'personnel', label: '全局人员列表', icon: 'fa-users' }
]

// 组件引用
const personnelFormRef = ref()
const departmentFormRef = ref() // 新增部门表单引用

/**
 * ----------------------------------------
 * 组织架构管理配置
 * ----------------------------------------
 */
// 部门树相关数据
const departmentTree = ref([])
const filteredDepartmentTree = ref([])
const selectedDepartment = ref({})
const expandedKeys = ref([])
const departmentSearchText = ref('')

// 部门详情标签页
const activeDetailTab = ref('basic')

// 下级部门相关数据
const subDepartments = ref([])
const subDepartmentsLoading = ref(false)

// 部门表单相关数据
const departmentFormVisible = ref(false)
const currentDepartmentForm = ref({})
const isEditDepartment = ref(false)
const parentDepartmentForForm = ref({})
// 部门表单提交状态
const departmentFormLoading = ref(false)

// 部门表单配置
const departmentFormItems = computed(() => [
  {
    prop: 'name',
    label: '部门名称',
    type: 'input',
    placeholder: '请输入部门名称',
    span: 24, // 设置为占满整行
    rules: [
      { required: true, message: '请输入部门名称', trigger: 'blur' },
      { min: 2, max: 50, message: '长度在2到50个字符之间', trigger: 'blur' }
    ]
  },
  {
    prop: 'parentId',
    label: '上级部门',
    type: 'select',
    placeholder: '请选择上级部门',
    span: 24, // 设置为占满整行
    disabled: computed(() => {
      // 只有在新增子部门时才禁用上级部门选择
      return !!parentDepartmentForForm.value.id && !isEditDepartment.value
    }),
    options: computed(() => {
      // 获取所有部门选项
      let options = departmentOptions.value.map(item => ({
        label: item.name,
        value: item.id
      }))

      // 如果是编辑状态，过滤掉自身和其子部门，防止循环引用
      if (isEditDepartment.value && currentDepartmentForm.value.id) {
        // 获取当前部门及其所有子部门的ID
        const excludeIds = getDepartmentAndChildrenIds(
          currentDepartmentForm.value.id,
          departmentOptions.value
        )

        // 过滤掉不能选择的部门
        options = options.filter(option => !excludeIds.includes(option.value))
      }

      // 添加空选项（表示顶级部门）
      options.unshift({ label: '无 (顶级部门)', value: null })

      return options
    })
  },
  {
    prop: 'createdAt',
    label: '创建日期',
    type: 'date',
    placeholder: '请选择创建日期',
    span: 24, // 设置为占满整行
    format: 'YYYY-MM-DD',
    valueFormat: 'YYYY-MM-DD'
  },
  {
    prop: 'duty',
    label: '部门职责',
    type: 'textarea',
    placeholder: '请输入部门职责',
    span: 24, // 设置为占满整行
    rows: 4
  },
  {
    prop: 'contact',
    label: '联系方式',
    type: 'input',
    placeholder: '请输入联系方式',
    span: 24 // 设置为占满整行
  }
])

// 部门表单验证规则
const departmentFormRules = {
  name: [
    { required: true, message: '请输入部门名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在2到50个字符之间', trigger: 'blur' }
  ]
}

// 部门表单提交处理
const handleDepartmentFormSubmit = async () => {
  try {
    // 表单验证
    const valid = await departmentFormRef.value?.validate()
    if (!valid) return

    departmentFormLoading.value = true

    // 获取最新表单数据
    const formData = { ...currentDepartmentForm.value }

    // 获取表单组件内部最新数据（如果有差异）
    if (departmentFormRef.value?.formData) {
      Object.assign(formData, departmentFormRef.value.formData)
    }

    // 日期处理：如果有日期，添加时间部分并转换为后端接受的格式
    if (formData.createdAt) {
      // 添加时间部分，然后转换成后端接受的格式
      formData.createdAt = formatLocalTimeForAPI(`${formData.createdAt} 00:00:00`)
    }

    // 如果有父部门指定，设置parentId
    if (parentDepartmentForForm.value.id) {
      formData.parentId = parentDepartmentForForm.value.id
    }

    // 根据isEditDepartment状态决定是创建还是更新
    if (isEditDepartment.value) {
      await updateDepartment(formData.id, formData)
    } else {
      await createDepartment(formData)
    }

    // 关闭表单弹窗
    departmentFormVisible.value = false

    // 成功提示
    ElMessage.success(isEditDepartment.value ? '部门更新成功' : '部门创建成功')

    // 重新加载数据
    await handleDepartmentFormSuccess()
  } catch (error) {
    console.error('保存部门失败:', error)
    ElMessage.error('保存失败: ' + (error.message || '未知错误'))
  } finally {
    departmentFormLoading.value = false
  }
}

// 树形组件配置
const treeProps = {
  children: 'children',
  label: 'name'
}

/**
 * ----------------------------------------
 * 人员管理配置
 * ----------------------------------------
 */
// 部门人员相关数据
const departmentPersonnel = ref([])
const personnelLoading = ref(false)
const selectedPersonnel = ref([])
const departmentPersonnelPage = ref(1)
const departmentPersonnelSize = ref(10)
const departmentPersonnelTotal = ref(0)

// 全局人员列表相关数据
const allPersonnelList = ref([])
const personnelListLoading = ref(false)
const personnelCurrentPage = ref(1)
const personnelPageSize = ref(10)
const personnelTotal = ref(0)

// 人员表单相关数据
const personnelFormVisible = ref(false)
const currentPersonnel = ref({})
const isEditPersonnel = ref(false)
const saving = ref(false)

// 选项数据
const departmentOptions = ref([])
const positionOptions = ref([])

// 搜索表单
const personnelSearchForm = ref({
  name: '', // 姓名搜索
  positionId: '' // 职责搜索（岗位ID）
})

// 搜索字段配置
const personnelSearchFields = [
  {
    prop: 'name', // 修改为name以匹配后端API参数
    label: '姓名',
    type: 'input',
    placeholder: '请输入姓名',
    labelWidth: '40px'
  },
  {
    prop: 'positionId',
    label: '职责',
    type: 'select',
    placeholder: '请选择职责',
    labelWidth: '40px',
    options: [] // 将通过API动态加载
  }
]

// 部门人员表格列配置
const personnelColumns = [
  { prop: 'fullName', label: '姓名', width: 120 },
  { prop: 'positionName', label: '职责', width: 120 },
  { prop: 'phone', label: '联系方式', width: 140 },
  { prop: 'hireDate', label: '入职日期', width: 120 },
  { prop: 'workResponsibilities', label: '工作职责', width: 200 } // 修正字段名为workResponsibilities
]

// 下级部门表格列配置
const subDepartmentColumns = [
  { prop: 'name', label: '部门名称', width: 150 },
  { prop: 'duty', label: '部门职责', minWidth: 200 },
  { prop: 'contact', label: '联系方式', width: 140 },
  { prop: 'personnelCount', label: '人员数量', width: 100, formatter: (row) => `${row.personnelCount || 0}人` },
  {
    prop: 'isActive',
    label: '状态',
    width: 80,
    slot: true
  }
]

// 全局人员表格列配置
const allPersonnelColumns = [
  { prop: 'fullName', label: '姓名', width: 120 },
  { prop: 'positionName', label: '职责', width: 120 },
  { prop: 'phone', label: '联系方式', width: 140 },
  { prop: 'hireDate', label: '入职日期', width: 120 },
  { prop: 'workResponsibilities', label: '工作职责', width: 200 } // 修正字段名为workResponsibilities
]

// 人员表单配置项
const personnelFormItems = [
  {
    prop: 'fullName',
    label: '姓名',
    type: 'input',
    placeholder: '请输入姓名',
    maxlength: 100,
    showWordLimit: true,
    clearable: true,
    span: 12
  },
  {
    prop: 'phone',
    label: '联系电话',
    type: 'input',
    placeholder: '请输入联系电话',
    maxlength: 50,
    clearable: true,
    span: 12
  },
  {
    prop: 'hireDate',
    label: '入职日期',
    type: 'date',
    placeholder: '请选择入职日期',
    format: 'YYYY-MM-DD',
    valueFormat: 'YYYY-MM-DD',
    clearable: true,
    span: 12
  },
  {
    prop: 'status',
    label: '人员状态',
    type: 'select',
    placeholder: '请选择人员状态',
    options: [
      { label: '在职', value: '在职' },
      { label: '离职', value: '离职' }
    ],
    span: 12
  },
  {
    prop: 'workResponsibilities',
    label: '工作职责',
    type: 'textarea',
    placeholder: '请输入工作职责描述...',
    rows: 4,
    maxlength: 500,
    showWordLimit: true,
    clearable: true,
    span: 18
  },
  {
    prop: 'departmentId',
    label: '所属部门',
    type: 'select',
    placeholder: '请选择所属部门',
    options: computed(() => departmentOptions.value.map(item => ({
      label: item.name,
      value: item.id
    }))),
    clearable: true,
    span: 12
  },
  {
    prop: 'positionId',
    label: '岗位',
    type: 'select',
    placeholder: '请选择岗位',
    options: computed(() => positionOptions.value.map(item => ({
      label: item.name,
      value: item.id
    }))),
    clearable: true,
    span: 12
  }
]

// 自定义电话号码验证规则
const validatePhone = (rule, value, callback) => {
  if (value && !/^[\d\-\s\+\(\)]+$/.test(value)) {
    callback(new Error('请输入正确的电话号码格式'))
    return
  }
  callback()
}

// 人员表单验证规则
const personnelFormRules = {
  fullName: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { max: 100, message: '姓名长度不能超过100个字符', trigger: 'blur' }
  ],
  phone: [
    { validator: validatePhone, trigger: 'blur' },
    { max: 50, message: '联系电话长度不能超过50个字符', trigger: 'blur' }
  ],
  workResponsibilities: [
    { max: 500, message: '工作职责描述不能超过500个字符', trigger: 'blur' }
  ],
  // 部门和岗位字段允许为空值（非必填字段）
  departmentId: [],
  positionId: []
}

/**
 * ----------------------------------------
 * 部门管理事件处理
 * ----------------------------------------
 */

// 部门搜索和控制功能
const handleDepartmentSearch = () => {
  if (!departmentSearchText.value.trim()) {
    filteredDepartmentTree.value = departmentTree.value
    return
  }

  const filterTree = (nodes) => {
    return nodes.filter(node => {
      const matchesSearch = node.name.toLowerCase().includes(departmentSearchText.value.toLowerCase())
      const hasMatchingChildren = node.children && filterTree(node.children).length > 0

      if (hasMatchingChildren) {
        node.children = filterTree(node.children)
      }

      return matchesSearch || hasMatchingChildren
    })
  }

  filteredDepartmentTree.value = filterTree([...departmentTree.value])
}

// 切换展开/折叠状态
const isAllExpanded = ref(false)

const handleToggleAll = () => {
  if (isAllExpanded.value) {
    // 当前是展开状态，执行折叠
    expandedKeys.value = []
    nextTick(() => {
      const tree = document.querySelector('.department-tree')
      if (tree) {
        const allKeys = getAllNodeKeys(filteredDepartmentTree.value)
        allKeys.forEach(key => {
          const node = tree.__vueParentInstance?.exposed?.getNode(key)
          if (node) {
            node.expanded = false
          }
        })
      }
    })
    isAllExpanded.value = false
  } else {
    // 当前是折叠状态，执行展开
    const allKeys = getAllNodeKeys(filteredDepartmentTree.value)
    expandedKeys.value = allKeys
    nextTick(() => {
      const tree = document.querySelector('.department-tree')
      if (tree) {
        allKeys.forEach(key => {
          const node = tree.__vueParentInstance?.exposed?.getNode(key)
          if (node) {
            node.expanded = true
          }
        })
      }
    })
    isAllExpanded.value = true
  }

  // 树形组件会自动更新
}

// 部门点击和选择处理
const handleDepartmentClick = async (data) => {
  selectedDepartment.value = data
  activeDetailTab.value = 'basic'

  // 加载部门人员和下级部门
  await Promise.all([
    loadDepartmentPersonnel(data.id),
    loadSubDepartments(data.id)
  ])
}

// 部门新增和编辑处理
const handleAddDepartment = () => {
  currentDepartmentForm.value = {}
  parentDepartmentForForm.value = {}
  isEditDepartment.value = false
  departmentFormVisible.value = true
}

const handleAddSubDepartment = () => {
  if (!selectedDepartment.value.id) {
    ElMessage.warning('请先选择一个部门')
    return
  }

  // 确保先加载部门选项
  loadDepartmentOptions().then(() => {
    currentDepartmentForm.value = {
      parentId: selectedDepartment.value.id, // 直接设置父部门ID
      parentName: selectedDepartment.value.name // 显示父部门名称
    }
    parentDepartmentForForm.value = selectedDepartment.value
    isEditDepartment.value = false
    departmentFormVisible.value = true
  })
}

const handleEditDepartment = async (data) => {
  // 首先加载部门选项数据
  await loadDepartmentOptions()

  currentDepartmentForm.value = { ...data }
  parentDepartmentForForm.value = {}
  isEditDepartment.value = true
  departmentFormVisible.value = true
}

const handleEditSubDepartment = async (data) => {
  // 首先加载部门选项数据
  await loadDepartmentOptions()

  currentDepartmentForm.value = { ...data }
  parentDepartmentForForm.value = {}
  isEditDepartment.value = true
  departmentFormVisible.value = true
}

// 部门删除处理
const handleDeleteDepartment = async (data) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除部门"${data.name}"吗？删除后该部门下的所有人员将需要重新分配。`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await deleteDepartment(data.id)
    ElMessage.success('删除成功')

    // 重新加载部门树
    await loadDepartmentTree()

    // 如果删除的是当前选中的部门，清空选中状态
    if (selectedDepartment.value.id === data.id) {
      selectedDepartment.value = {}
      departmentPersonnel.value = []
      subDepartments.value = []
    }
  } catch (error) {
    if (error.message !== 'cancel') {
      console.error('删除部门失败:', error)
      ElMessage.error('删除部门失败')
    }
  }
}

// 部门表单提交成功回调
const handleDepartmentFormSuccess = async () => {
  departmentFormVisible.value = false

  // 保存当前选中的部门ID
  const currentSelectedId = selectedDepartment.value.id

  // 重新加载部门树
  await loadDepartmentTree()

  // 如果之前有选中的部门，尝试重新选中
  if (currentSelectedId) {
    // 在新的树结构中查找对应的部门
    const findDepartmentInTree = (tree, targetId) => {
      for (const dept of tree) {
        if (dept.id === targetId) {
          return dept
        }
        if (dept.children && dept.children.length > 0) {
          const found = findDepartmentInTree(dept.children, targetId)
          if (found) return found
        }
      }
      return null
    }

    const foundDepartment = findDepartmentInTree(departmentTree.value, currentSelectedId)
    if (foundDepartment) {
      selectedDepartment.value = foundDepartment
      await Promise.all([
        loadDepartmentPersonnel(foundDepartment.id),
        loadSubDepartments(foundDepartment.id)
      ])
    }
  }

  ElMessage.success(isEditDepartment.value ? '部门更新成功' : '部门创建成功')
}

// 下级部门管理处理
const handleDeleteSubDepartment = async (data) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除下级部门"${data.name}"吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await deleteDepartment(data.id)
    ElMessage.success('删除成功')

    // 重新加载下级部门列表和部门树
    await Promise.all([
      loadSubDepartments(selectedDepartment.value.id),
      loadDepartmentTree()
    ])
  } catch (error) {
    if (error.message !== 'cancel') {
      console.error('删除下级部门失败:', error)
      ElMessage.error('删除失败')
    }
  }
}


/**
 * ----------------------------------------
 * 人员管理事件处理
 * ----------------------------------------
 */

// 人员新增和编辑处理
const handleAddPersonnel = async () => {
  // 确保选项数据已加载
  if (departmentOptions.value.length === 0 || positionOptions.value.length === 0) {
    await loadAllOptions()
  }

  isEditPersonnel.value = false
  currentPersonnel.value = {
    fullName: '',
    phone: '',
    departmentId: null,
    positionId: null,
    hireDate: null, // 入职日期字段
    status: '在职',
    workResponsibilities: '' // 新增工作职责字段
  }
  personnelFormVisible.value = true
}

const handleEditPersonnel = async (row) => {
  // 确保选项数据已加载
  if (departmentOptions.value.length === 0 || positionOptions.value.length === 0) {
    await loadAllOptions()
  }

  isEditPersonnel.value = true
  currentPersonnel.value = {
    id: row.id,
    fullName: row.fullName,
    phone: row.phone,
    departmentId: row.departmentId,
    positionId: row.positionId,
    hireDate: row.hireDate || null, // 入职日期字段
    status: row.status,
    workResponsibilities: row.workResponsibilities || '' // 新增工作职责字段
  }
  personnelFormVisible.value = true
}

// 人员保存处理
const handleSavePersonnel = async () => {
  try {
    // 验证表单
    const valid = await personnelFormRef.value?.validate()
    if (!valid) return

    saving.value = true

    if (isEditPersonnel.value) {
      // 编辑人员
      await updatePersonnel(currentPersonnel.value.id, {
        fullName: currentPersonnel.value.fullName,
        phone: currentPersonnel.value.phone,
        departmentId: currentPersonnel.value.departmentId,
        positionId: currentPersonnel.value.positionId,
        hireDate: currentPersonnel.value.hireDate, // 入职日期字段
        status: currentPersonnel.value.status,
        workResponsibilities: currentPersonnel.value.workResponsibilities // 新增工作职责字段
      })
      ElMessage.success('更新人员信息成功')
    } else {
      // 新增人员
      await createPersonnel({
        fullName: currentPersonnel.value.fullName,
        phone: currentPersonnel.value.phone,
        departmentId: currentPersonnel.value.departmentId,
        positionId: currentPersonnel.value.positionId,
        hireDate: currentPersonnel.value.hireDate, // 入职日期字段
        status: currentPersonnel.value.status,
        workResponsibilities: currentPersonnel.value.workResponsibilities // 新增工作职责字段
      })
      ElMessage.success('新增人员成功')
    }

    personnelFormVisible.value = false

    // 刷新人员列表
    if (activeTab.value === 'personnel') {
      await loadAllPersonnelList()
    } else {
      // 如果在组织架构视图，刷新当前部门的人员列表
      if (selectedDepartment.value.id) {
        await loadDepartmentPersonnel(selectedDepartment.value.id)
      }
    }
  } catch (error) {
    console.error('保存人员信息失败:', error)
    ElMessage.error(isEditPersonnel.value ? '更新人员信息失败' : '新增人员失败')
  } finally {
    saving.value = false
  }
}

// 人员删除处理
const handleDeletePersonnel = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除人员"${row.fullName}"吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await deletePersonnel(row.id)
    ElMessage.success('删除成功')

    // 重新加载当前部门人员列表或全局人员列表
    if (activeTab.value === 'organization' && selectedDepartment.value.id) {
      await loadDepartmentPersonnel(selectedDepartment.value.id)
    } else if (activeTab.value === 'personnel') {
      await loadAllPersonnelList()
    }
  } catch (error) {
    if (error.message !== 'cancel') {
      console.error('删除人员失败:', error)
      ElMessage.error('删除人员失败')
    }
  }
}

const handleBatchDeletePersonnel = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedPersonnel.value.length} 个人员吗？`,
      '批量删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const userIds = selectedPersonnel.value.map(user => user.id)
    await batchDeletePersonnel(userIds)
    ElMessage.success('批量删除成功')

    // 清空选中状态
    selectedPersonnel.value = []

    // 重新加载当前部门人员列表或全局人员列表
    if (activeTab.value === 'organization' && selectedDepartment.value.id) {
      await loadDepartmentPersonnel(selectedDepartment.value.id)
    } else if (activeTab.value === 'personnel') {
      await loadAllPersonnelList()
    }
  } catch (error) {
    if (error.message !== 'cancel') {
      console.error('批量删除人员失败:', error)
      ElMessage.error('批量删除人员失败')
    }
  }
}

// 人员选择变化处理
const handlePersonnelSelectionChange = (selection) => {
  selectedPersonnel.value = selection
}

// 部门人员分页处理
const handleDepartmentPersonnelSizeChange = async (size) => {
  departmentPersonnelSize.value = size
  departmentPersonnelPage.value = 1 // 改变每页条数时重置到第一页
  await loadDepartmentPersonnel(selectedDepartment.value.id)
}

const handleDepartmentPersonnelCurrentChange = async (page) => {
  departmentPersonnelPage.value = page
  await loadDepartmentPersonnel(selectedDepartment.value.id)
}

// 全局人员列表搜索和分页处理
const handlePersonnelSearch = async () => {
  personnelCurrentPage.value = 1 // 搜索时重置到第一页
  await loadAllPersonnelList()
}

const handlePersonnelResetSearch = async () => {
  // 确保使用与API参数匹配的字段名
  Object.assign(personnelSearchForm.value, {
    name: '',
    positionId: ''
  })
  personnelCurrentPage.value = 1 // 重置时重置到第一页
  // 移除重复的数据加载调用，避免发送两次相同请求
  // 因为CommonSearch组件在reset事件后会自动触发search事件
}

const handlePersonnelSizeChange = async (size) => {
  personnelPageSize.value = size
  personnelCurrentPage.value = 1 // 改变页大小时重置到第一页
  await loadAllPersonnelList()
}

const handlePersonnelCurrentChange = async (page) => {
  personnelCurrentPage.value = page
  await loadAllPersonnelList()
}

/**
 * ----------------------------------------
 * 数据加载函数
 * ----------------------------------------
 */
// 获取所有节点的ID（递归）
const getAllNodeKeys = (nodes) => {
  let keys = []
  nodes.forEach(node => {
    keys.push(node.id)
    if (node.children && node.children.length > 0) {
      keys = keys.concat(getAllNodeKeys(node.children))
    }
  })
  return keys
}

// 部门树数据加载
const loadDepartmentTree = async () => {
  try {
    // loading.value = true // 移除未使用的loading变量
    const data = await getDepartmentTree()
    departmentTree.value = data || []
    filteredDepartmentTree.value = data || []

    // 设置默认展开所有节点
    if (data && data.length > 0) {
      const allKeys = getAllNodeKeys(data)
      expandedKeys.value = allKeys
    }
  } catch (error) {
    console.error('加载部门树失败:', error)
    ElMessage.error('加载部门树失败')
    departmentTree.value = []
    filteredDepartmentTree.value = []
  } finally {
    // loading.value = false // 移除未使用的loading变量
  }
}

// 部门人员数据加载
const loadDepartmentPersonnel = async (departmentId) => {
  if (!departmentId) {
    departmentPersonnel.value = []
    departmentPersonnelTotal.value = 0
    return
  }

  try {
    personnelLoading.value = true
    const data = await getPersonnelList({
      page: departmentPersonnelPage.value,
      size: departmentPersonnelSize.value,
      departmentId: departmentId
    })
    departmentPersonnel.value = data.items || []
    departmentPersonnelTotal.value = data.total || 0
  } catch (error) {
    console.error('加载部门人员失败:', error)
    ElMessage.error('加载部门人员失败')
    departmentPersonnel.value = []
    departmentPersonnelTotal.value = 0
  } finally {
    personnelLoading.value = false
  }
}

// 下级部门数据加载
const loadSubDepartments = async (parentId) => {
  try {
    subDepartmentsLoading.value = true
    // 从部门树中筛选出当前部门的直接下级部门
    const findSubDepartments = (nodes, targetId) => {
      for (const node of nodes) {
        if (node.id === targetId) {
          return node.children || []
        }
        if (node.children && node.children.length > 0) {
          const result = findSubDepartments(node.children, targetId)
          if (result) return result
        }
      }
      return []
    }

    const subDepts = findSubDepartments(departmentTree.value, parentId)

    // 为每个下级部门添加人员数量信息
    const subDepartmentsWithCount = await Promise.all(
      subDepts.map(async (dept) => {
        try {
          const personnel = await getPersonnelList({
            page: 1,
            size: 1, // 只需要获取数量，不需要具体数据
            departmentId: dept.id
          })
          return {
            ...dept,
            personnelCount: personnel.total || 0
          }
        } catch (error) {
          console.error(`获取部门 ${dept.name} 人员数量失败:`, error)
          return {
            ...dept,
            personnelCount: 0
          }
        }
      })
    )

    subDepartments.value = subDepartmentsWithCount
  } catch (error) {
    console.error('加载下级部门失败:', error)
    ElMessage.error('加载下级部门失败')
    subDepartments.value = []
  } finally {
    subDepartmentsLoading.value = false
  }
}

// 选项数据加载
const loadDepartmentOptions = async () => {
  try {
    const response = await getDepartmentList({ size: 100 })
    departmentOptions.value = response.items || []
  } catch (error) {
    console.error('❌ 加载部门选项失败:', error)
    ElMessage.error('加载部门选项失败')
    departmentOptions.value = []
  }
}

const loadPositionOptions = async () => {
  try {
    const response = await getPositionList({ size: 100 })
    positionOptions.value = response.items || []

    // 构建选项数组
    const options = [
      { label: '全部', value: '' },
      ...positionOptions.value.map(item => ({
        label: item.name,
        value: item.id
      }))
    ]

    // 更新搜索字段配置中的岗位选项
    for (let i = 0; i < personnelSearchFields.length; i++) {
      if (personnelSearchFields[i].prop === 'positionId') {
        // 强制创建新数组，确保触发响应式更新
        personnelSearchFields[i] = {
          ...personnelSearchFields[i],
          options: [...options]
        }
      }
    }

    // 手动触发搜索组件更新
    nextTick(() => {
      // 强制更新视图
      personnelSearchForm.value = { ...personnelSearchForm.value }
    })
  } catch (error) {
    console.error('❌ 加载岗位选项失败:', error)
    ElMessage.error('加载岗位选项失败')
    positionOptions.value = []
  }
}

const loadAllOptions = async () => {
  await Promise.all([
    loadDepartmentOptions(),
    loadPositionOptions()
  ])
}

// 全局人员列表数据加载
const loadAllPersonnelList = async () => {
  try {
    personnelListLoading.value = true

    // 构建搜索参数
    const params = {
      page: personnelCurrentPage.value,
      size: personnelPageSize.value,
      name: personnelSearchForm.value.name, // 搜索姓名
      positionId: personnelSearchForm.value.positionId // 职责搜索
    }

    // 过滤空值参数
    Object.keys(params).forEach(key => {
      if (params[key] === '' || params[key] === null || params[key] === undefined) {
        delete params[key]
      }
    })

    const data = await getPersonnelList(params)
    allPersonnelList.value = data.items || []
    personnelTotal.value = data.total || 0
  } catch (error) {
    console.error('加载人员列表失败:', error)
    ElMessage.error('加载人员列表失败')
    allPersonnelList.value = []
    personnelTotal.value = 0
  } finally {
    personnelListLoading.value = false
  }
}

/**
 * ----------------------------------------
 * 工具方法
 * ----------------------------------------
 */
// 获取指定部门的所有子部门ID（包含自身）
const getDepartmentAndChildrenIds = (departmentId, departments) => {
  const ids = [departmentId]
  const findChildren = (parentId) => {
    departments.forEach(dept => {
      if (dept.parentId === parentId) {
        ids.push(dept.id)
        findChildren(dept.id)
      }
    })
  }
  findChildren(departmentId)
  return ids
}

// 监听选项卡变化
watch(activeTab, async (newTab) => {
  if (newTab === 'organization') {
    await loadDepartmentTree()
    selectedDepartment.value = {}
    departmentPersonnel.value = []
  } else if (newTab === 'personnel') {
    // 先加载所有选项，再加载人员列表，确保下拉框数据已准备好
    await loadAllOptions() // 先加载所有选项数据
    await loadAllPersonnelList() // 然后加载人员列表
  }
})

/**
 * ----------------------------------------
 * 生命周期钩子和初始化
 * ----------------------------------------
 */
// 组件挂载时初始化
onMounted(async () => {
  try {
    // 加载部门树和所有选项
    await loadDepartmentTree()
    await loadAllOptions() // 确保加载所有选项数据
    ElMessage.success('管理信息服务页面加载完成')
  } catch (error) {
    console.error('页面初始化失败:', error)
    ElMessage.error('页面初始化失败')
  }
})
</script>

<style scoped lang="scss">
/**
 * ----------------------------------------
 * 页面基本样式
 * ----------------------------------------
 */
.management-service {
  padding: 20px;
  background-color: var(--bg-secondary);
  min-height: calc(100vh - 60px);

  /**
 * ----------------------------------------
 * 组织架构管理视图样式
 * ----------------------------------------
 */
  .organization-view {

    /**
     * 部门树卡片样式
     */
    .tree-card {
      height: 600px;
      border: 1px solid var(--neutral-dark);
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, .1);
      background: var(--bg-primary);

      // 部门树控制区域
      .tree-controls {
        padding: 16px;
        border-bottom: 1px solid var(--neutral-dark);
        background: var(--neutral-color);

        .search-input {
          margin-bottom: 12px;

          :deep(.el-input__wrapper) {
            border-radius: 6px;
            border-color: var(--neutral-dark);

            &:hover {
              border-color: var(--primary-color);
            }

            &.is-focus {
              border-color: var(--primary-color);
              box-shadow: 0 0 0 2px rgba(22, 93, 255, 0.1);
            }
          }

          :deep(.el-input__inner) {
            color: var(--text-primary);

            &::placeholder {
              color: var(--text-tertiary);
            }
          }
        }

        .control-buttons {
          display: flex;
          gap: 8px;

          .custom-button {
            border-radius: 6px;
            font-weight: 500;
            transition: all 0.15s ease;

            .fa {
              margin-right: 4px;
            }
          }
        }
      }

      /**
       * 部门树容器样式
       */
      .tree-container {
        height: calc(100% - 120px);
        overflow-y: auto;
        padding: 8px;

        .department-tree {

          // 部门树节点样式
          :deep(.el-tree-node) {
            position: relative;

            // 垂直连接线
            &:not(:last-child) {
              &::before {
                content: '';
                position: absolute;
                left: 9px;
                top: 36px;
                bottom: -2px;
                width: 1px;
                background-color: var(--el-border-color-light);
              }
            }

            // 水平连接线
            &::after {
              content: '';
              position: absolute;
              left: 9px;
              top: 18px;
              width: 20px;
              height: 1px;
              background-color: var(--el-border-color-light);
            }

            // 根节点不显示水平连接线
            &.is-root::after {
              display: none;
            }
          }

          :deep(.el-tree-node__content) {
            height: 36px;
            border-radius: 6px;
            margin-bottom: 2px;
            transition: all 0.3s ease;

            &:hover {
              background-color: var(--el-fill-color-light);
            }
          }

          :deep(.el-tree-node__expand-icon) {
            color: var(--el-text-color-secondary);
            font-size: 12px;
          }
        }

        /**
         * 树节点样式
         */
        .tree-node {
          display: flex;
          align-items: center;
          justify-content: space-between;
          width: 100%;
          padding-right: 8px;

          .node-content {
            display: flex;
            align-items: center;
            gap: 8px;
            flex: 1;

            .node-icon {
              color: var(--primary-color);
              font-size: 16px;
              width: 16px;
              text-align: center;
            }

            .node-label {
              font-size: 14px;
              color: var(--text-primary);
              font-weight: 500;
            }
          }

          .node-actions {
            opacity: 0;
            transition: opacity 0.15s ease;
            display: flex;
            gap: 4px;
          }

          &:hover .node-actions {
            opacity: 1;
          }

          // 选中状态样式 - 参考index.html的tree-node-active样式
          &.tree-node-selected {
            :deep(.el-tree-node__content) {
              background-color: rgba(22, 93, 255, 0.1);
              border-left: 4px solid var(--primary-color);
              padding-left: 12px;
            }

            .node-content .node-label {
              color: var(--primary-color);
              font-weight: 600;
            }
          }
        }
      }
    }

    /**
     * 部门详情卡片样式
     */
    .detail-card {
      height: 600px;
      border: 1px solid var(--neutral-dark);
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, .1);
      background: var(--bg-primary);

      // 详情标题 - 参考index.html的详情标题区样式
      .detail-header {
        padding: 16px 20px;
        border-bottom: 1px solid var(--neutral-dark);
        background: var(--neutral-color);

        .detail-title {
          margin: 0;
          font-size: 18px;
          font-weight: 600;
          color: var(--text-primary);
        }
      }

      /**
       * 部门详情标签页样式
       */
      .department-details {
        height: calc(100% - 60px);

        .detail-tabs {
          height: 100%;
          padding: 0 20px;

          :deep(.el-tabs__header) {
            margin-bottom: 16px;
          }

          :deep(.el-tabs__content) {
            height: calc(100% - 56px);
            overflow-y: auto;
          }

          :deep(.el-tabs__item) {
            font-weight: 500;
          }
        }

        /**
         * 基本信息内容样式
         */
        .basic-info-content {
          height: 100%;

          // 部门信息卡片
          .department-info-card {
            background: var(--neutral-color);
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 20px;
            border: 1px solid var(--neutral-dark);

            .info-header {
              display: flex;
              justify-content: space-between;
              align-items: flex-start;
              margin-bottom: 20px;

              .info-title-section {
                flex: 1;

                .department-name {
                  margin: 0 0 8px 0;
                  font-size: 20px;
                  font-weight: 600;
                  color: var(--text-primary);
                }

                .department-description {
                  margin: 0;
                  font-size: 14px;
                  color: var(--text-secondary);
                  line-height: 1.5;
                }
              }

              .info-actions {
                .custom-button {
                  border-radius: 6px;
                  transition: all 0.15s ease;
                }
              }
            }

            /**
             * 信息网格样式
             */
            .info-grid {
              display: grid;
              grid-template-columns: repeat(2, 1fr);
              gap: 20px;

              .info-item {
                display: flex;
                align-items: flex-start;
                gap: 12px;

                .info-icon {
                  width: 40px;
                  height: 40px;
                  border-radius: 8px;
                  background: rgba(22, 93, 255, 0.1);
                  display: flex;
                  align-items: center;
                  justify-content: center;
                  color: var(--primary-color);
                  font-size: 18px;

                  .fa {
                    font-size: 18px;
                  }
                }

                .info-content {
                  flex: 1;

                  .info-label {
                    margin: 0 0 4px 0;
                    font-size: 12px;
                    color: var(--text-secondary);
                  }

                  .info-value {
                    margin: 0;
                    font-size: 14px;
                    color: var(--text-primary);
                    font-weight: 500;
                  }
                }
              }
            }
          }

          /**
           * 部门职责部分样式
           */
          .department-duty-section {
            .section-title {
              margin: 0 0 12px 0;
              font-size: 16px;
              font-weight: 600;
              color: var(--text-primary);
            }

            .duty-description {
              margin: 0;
              font-size: 14px;
              color: var(--text-secondary);
              line-height: 1.6;
            }
          }
        }

        /**
         * 下级部门和人员内容样式
         */
        .subdepartments-content,
        .personnel-content {
          height: 100%;
          display: flex;
          flex-direction: column;

          .subdepartments-header {
            margin-bottom: 16px;
            display: flex;
            justify-content: flex-start;
            align-items: center;
          }

          .table-pagination {
            margin-top: 16px;
            display: flex;
            justify-content: center;
          }
        }


      }

      /**
       * 空状态样式
       */
      .empty-state {
        display: flex;
        align-items: center;
        justify-content: center;
        height: 400px;
      }
    }
  }

  /**
   * ----------------------------------------
   * 全局人员列表视图样式
   * ----------------------------------------
   */
  .personnel-view {
    background: var(--bg-primary);
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, .1);
    border: 1px solid var(--neutral-dark);
  }
}

/**
 * ----------------------------------------
 * 响应式设计样式
 * ----------------------------------------
 */
@media (max-width: 1200px) {
  .management-service {
    .organization-view {
      .el-row {
        flex-direction: column;
      }

      .el-col {
        width: 100% !important;
        max-width: 100% !important;
      }

      .tree-card {
        height: 350px;
        margin-bottom: 20px;

        .tree-controls {
          .control-buttons {
            flex-wrap: wrap;
            gap: 6px;

            .custom-button {
              flex: 1;
              min-width: 120px;
            }
          }
        }
      }

      .detail-card {
        height: auto;

        .department-details {
          .basic-info-content {
            .department-info-card {
              .info-grid {
                grid-template-columns: 1fr;
                gap: 16px;
              }
            }
          }
        }
      }
    }
  }
}

/**
 * 移动端响应式样式
 */
@media (max-width: 768px) {
  .management-service {
    padding: 10px;

    .organization-view {
      .tree-card {
        height: auto;
        min-height: 300px;

        .tree-controls {
          padding: 12px;

          .control-buttons {
            flex-direction: column;
            gap: 8px;

            .custom-button {
              width: 100%;
            }
          }
        }
      }

      .detail-card {
        height: auto;

        .detail-header {
          padding: 12px 15px;

          .detail-title {
            font-size: 16px;
          }
        }

        .department-details {
          .detail-tabs {
            padding: 0 15px;
          }

          .basic-info-content {
            .department-info-card {
              padding: 15px;

              .info-header {
                flex-direction: column;
                align-items: flex-start;
                gap: 12px;

                .info-actions {
                  width: 100%;

                  .custom-button {
                    width: 100%;
                  }
                }
              }

              .info-grid {
                grid-template-columns: 1fr;
                gap: 12px;

                .info-item {
                  .info-icon {
                    width: 32px;
                    height: 32px;
                    font-size: 16px;
                  }
                }
              }
            }
          }


        }
      }
    }

    .personnel-view {
      padding: 15px;
    }
  }
}

/**
 * ----------------------------------------
 * 弹窗样式
 * ----------------------------------------
 */
.dialog-footer {
  text-align: right;

  .custom-button {
    margin-left: 10px;
  }
}
</style>