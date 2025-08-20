<template>
    <div class="inspection-tasks">
        <!-- 任务搜索区域 -->
        <CommonSearch v-model="taskSearchForm" :items="taskSearchFields" :single-row="true" @search="handleTaskSearch"
            @reset="handleTaskReset">
            <template #actions>
                <CustomButton type="primary" @click="handleAddTask" v-permission="'business:manage'">
                    <i class="fa fa-plus"></i>
                    新增
                </CustomButton>
            </template>
        </CommonSearch>

        <!-- 任务表格区域 -->
        <div class="table-section">
            <CommonTable :data="taskTableData" :columns="taskColumns" :loading="taskLoading" :show-selection="false"
                :show-index="true" :show-actions="true" :actions-width="180" @row-click="handleTaskRowClick">
                <!-- 自定义列渲染 -->
                <template #facility="{ row }">
                    {{ getFacilityDisplay(row.facilityType, row.facilityId) }}
                </template>
                <template #frequency="{ row }">
                    {{ getDictLabelSync('inspection_frequency', row.frequency) }}
                </template>
                <template #status="{ row }">
                    <el-tag :type="getStatusTagType(row.status)" size="small">
                        {{ getDictLabelSync('inspection_status', row.status) }}
                    </el-tag>
                </template>
                <template #startTime="{ row }">
                    {{ formatDateTime(row.startTime, 'YYYY-MM-DD HH:mm') }}
                </template>
                <template #endTime="{ row }">
                    {{ formatDateTime(row.endTime, 'YYYY-MM-DD HH:mm') }}
                </template>
                <template #facilityName="{ row }">
                    {{ row.facilityName || getFacilityDisplay(row.facilityType, row.facilityId) }}
                </template>

                <!-- 操作列 -->
                <template #actions="{ row }">
                    <CustomButton type="text" text-type="primary" @click="handleEditTask(row)"
                        v-permission="'business:manage'">
                        编辑
                    </CustomButton>
                    <el-dropdown @command="(cmd) => handleTaskStatusChange(row, cmd)" v-permission="'business:manage'">
                        <CustomButton type="text" text-type="info">
                            状态 <i class="fa fa-caret-down"></i>
                        </CustomButton>
                        <template #dropdown>
                            <el-dropdown-menu>
                                <el-dropdown-item
                                    v-for="option in props.dictMaps.inspection_status"
                                    :key="option.value"
                                    :command="option.value"
                                    :disabled="row.status === option.value">
                                    {{ option.label }}
                                </el-dropdown-item>
                            </el-dropdown-menu>
                        </template>
                    </el-dropdown>
                    <CustomButton type="text" text-type="danger" @click="handleDeleteTask(row)"
                        v-permission="'business:manage'">
                        删除
                    </CustomButton>
                </template>
            </CommonTable>

            <CustomPagination v-model:current-page="taskPagination.currentPage"
                v-model:page-size="taskPagination.pageSize" :total="taskPagination.total"
                :page-sizes="[10, 20, 50, 100]" @size-change="handleTaskSizeChange"
                @current-change="handleTaskCurrentChange" />
        </div>

        <!-- 任务新增/编辑弹窗 -->
        <CustomDialog v-model:visible="taskDialogVisible" :title="taskDialogTitle" width="60%"
            :loading="taskFormLoading" @confirm="handleTaskSubmit" @cancel="taskDialogVisible = false">
            <CommonForm ref="taskFormRef" v-model="taskFormData" :items="taskFormFields" :rules="taskFormRules"
                :show-actions="false" :label-width="'120px'" />
        </CustomDialog>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import CommonSearch from '@/components/Common/CommonSearch.vue'
import CommonTable from '@/components/Common/CommonTable.vue'
import CommonForm from '@/components/Common/CommonForm.vue'
import CustomButton from '@/components/Common/CustomButton.vue'
import CustomDialog from '@/components/Common/CustomDialog.vue'
import CustomPagination from '@/components/Common/CustomPagination.vue'
import { formatDateTime } from '@/utils/shared/common'
import {
    listTasks, createTask, updateTask, deleteTask, updateTaskStatus
} from '@/api/inspection'

// ================= Props =================
const props = defineProps({
    dictMaps: {
        type: Object,
        required: true
    },
    facilityTypeOptions: {
        type: Array,
        required: true
    },
    facilityOptions: {
        type: Object,
        required: true
    },
    personnelOptions: {
        type: Array,
        required: true
    },
    loadFacilityOptions: {
        type: Function,
        required: true
    },
    getDictLabelSync: {
        type: Function,
        required: true
    },
    getFacilityDisplay: {
        type: Function,
        required: true
    },
    exporting: {
        type: Boolean,
        default: false
    }
})

// ================= Emits =================
const emit = defineEmits(['task-updated', 'export-tasks'])

// ================= 任务相关 =================
const taskSearchForm = ref({
    keyword: '',
    status: '',
    frequency: '',
    facilityType: ''
})

const taskSearchFields = ref([
    {
        prop: 'keyword',
        label: '工程/人员名称',
        type: 'input',
        placeholder: '请输入工程名称或人员姓名',
        width: '285px',
        labelWidth: '90px',
        clearable: true
    },
    {
        prop: 'status',
        label: '计划状态',
        type: 'select',
        options: [],
        placeholder: '请选择计划状态',
        width: '220px',
        labelWidth: 'var(--form-label-width-standard)',
        clearable: true
    },
    {
        prop: 'frequency',
        label: '巡检频率',
        type: 'select',
        options: [],
        placeholder: '请选择巡检频率',
        width: '220px',
        labelWidth: 'var(--form-label-width-standard)',
        clearable: true
    },
    {
        prop: 'facilityType',
        label: '巡检类型',
        type: 'select',
        options: [],
        placeholder: '请选择巡检类型',
        width: '220px',
        labelWidth: 'var(--form-label-width-standard)',
        clearable: true
    }
])

const taskTableData = ref([])
const taskLoading = ref(false)
const taskPagination = reactive({
    currentPage: 1,
    pageSize: 10,
    total: 0
})

const taskColumns = [
    { prop: 'title', label: '计划名称', width: 200, showOverflowTooltip: true },
    { prop: 'facilityName', label: '工程名称', width: 200, showOverflowTooltip: true },
    { prop: 'frequency', label: '巡检频率', width: 120 },
    { prop: 'assigneeName', label: '巡检人员', width: 120 },
    { prop: 'startTime', label: '开始时间', width: 160 },
    { prop: 'endTime', label: '结束时间', width: 160 },
    { prop: 'taskCount', label: '任务数', width: 100 },
    { prop: 'completedCount', label: '完成数', width: 100 }
]

// 任务表单相关
const taskDialogVisible = ref(false)
const taskDialogTitle = ref('')
const taskFormLoading = ref(false)
const taskFormRef = ref(null)
const taskFormData = ref({})
const isEditTask = ref(false)
const editTaskId = ref(null)

const taskFormFields = ref([
    {
        prop: 'title',
        label: '任务标题',
        type: 'input',
        required: true,
        placeholder: '请输入任务标题',
        span: 24
    },
    {
        prop: 'facilityType',
        label: '设施类型',
        type: 'select',
        required: true,
        placeholder: '请选择设施类型',
        options: [],
        span: 12
    },
    {
        prop: 'facilityId',
        label: '设施',
        type: 'select',
        required: true,
        placeholder: '请先选择设施类型',
        options: [],
        span: 12,
        disabled: true
    },
    {
        prop: 'frequency',
        label: '巡检频次',
        type: 'select',
        required: true,
        placeholder: '请选择巡检频次',
        options: [],
        span: 12
    },
    {
        prop: 'assigneeId',
        label: '巡检人员',
        type: 'select',
        required: true,
        placeholder: '请选择巡检人员',
        options: [],
        span: 12
    },
    {
        prop: 'scheduledDate',
        label: '计划日期',
        type: 'date',
        required: true,
        placeholder: '请选择计划日期',
        span: 12
    },
    {
        prop: 'content',
        label: '巡检内容',
        type: 'textarea',
        placeholder: '请输入巡检内容',
        span: 24,
        rows: 3
    }
])

const taskFormRules = reactive({
    title: [{ required: true, message: '请输入任务标题', trigger: 'blur' }],
    facilityType: [{ required: true, message: '请选择设施类型', trigger: 'change' }],
    facilityId: [{ required: true, message: '请选择设施', trigger: 'change' }],
    frequency: [{ required: true, message: '请选择巡检频次', trigger: 'change' }],
    assigneeId: [{ required: true, message: '请选择巡检人员', trigger: 'change' }],
    scheduledDate: [{ required: true, message: '请选择计划日期', trigger: 'change' }]
})

// ================= 计算属性 =================
const getStatusTagType = (status) => {
    const typeMap = {
        'PENDING': '',
        'IN_PROGRESS': 'info',
        'COMPLETED': 'success',
        'EXCEPTION': 'danger'
    }
    return typeMap[status] || ''
}

// ================= 生命周期 =================
onMounted(() => {
    updateSearchFieldOptions()
    updateFormFieldOptions()
    loadTaskData()
})

// ================= 选项更新 =================
const updateSearchFieldOptions = () => {
    taskSearchFields.value.find(f => f.prop === 'status').options = props.dictMaps.inspection_status
    taskSearchFields.value.find(f => f.prop === 'frequency').options = props.dictMaps.inspection_frequency
    taskSearchFields.value.find(f => f.prop === 'facilityType').options = props.facilityTypeOptions
}

const updateFormFieldOptions = () => {
    taskFormFields.value.find(f => f.prop === 'facilityType').options = props.facilityTypeOptions
    taskFormFields.value.find(f => f.prop === 'frequency').options = props.dictMaps.inspection_frequency
    taskFormFields.value.find(f => f.prop === 'assigneeId').options = props.personnelOptions
}

// ================= 监听器 =================
watch(() => taskFormData.value.facilityType, async (newType, oldType) => {
    if (newType !== oldType) {
        taskFormData.value.facilityId = null
    }

    const facilityField = taskFormFields.value.find(f => f.prop === 'facilityId')

    if (newType) {
        await props.loadFacilityOptions(newType)
        facilityField.options = props.facilityOptions[newType] || []
        facilityField.disabled = false
        facilityField.placeholder = '请选择设施'
    } else {
        facilityField.options = []
        facilityField.disabled = true
        facilityField.placeholder = '请先选择设施类型'
    }
})

// ================= 任务方法 =================
const loadTaskData = async () => {
    taskLoading.value = true
    try {
        const params = {
            page: taskPagination.currentPage,
            size: taskPagination.pageSize,
            ...taskSearchForm.value
        }
        const response = await listTasks(params)
        taskTableData.value = response.items || []
        taskPagination.total = response.total || 0
    } catch (error) {
        console.error('加载任务数据失败:', error)
        ElMessage.error('加载任务数据失败')
    } finally {
        taskLoading.value = false
    }
}

const handleTaskSearch = () => {
    taskPagination.currentPage = 1
    loadTaskData()
}

const handleTaskReset = () => {
    taskSearchForm.value = {
        keyword: '',
        status: '',
        frequency: '',
        facilityType: ''
    }
    taskPagination.currentPage = 1
    loadTaskData()
}

const handleTaskSizeChange = (size) => {
    taskPagination.pageSize = size
    taskPagination.currentPage = 1
    loadTaskData()
}

const handleTaskCurrentChange = (page) => {
    taskPagination.currentPage = page
    loadTaskData()
}

const handleTaskRowClick = (row) => {
    // 可以在这里添加行点击逻辑
}

const handleAddTask = () => {
    isEditTask.value = false
    editTaskId.value = null
    taskDialogTitle.value = '新增巡检任务'
    taskFormData.value = {
        title: '',
        facilityType: '',
        facilityId: null,
        frequency: '',
        assigneeId: null,
        scheduledDate: '',
        content: ''
    }
    taskDialogVisible.value = true
}

const handleEditTask = (row) => {
    isEditTask.value = true
    editTaskId.value = row.id
    taskDialogTitle.value = '编辑巡检任务'
    taskFormData.value = { ...row }
    taskDialogVisible.value = true
}

const handleTaskSubmit = async () => {
    if (!taskFormRef.value) return

    try {
        const isValid = await taskFormRef.value.validate()
        if (!isValid) return

        const formData = taskFormRef.value.formData
        taskFormLoading.value = true

        if (isEditTask.value) {
            await updateTask(editTaskId.value, formData)
            ElMessage.success('更新任务成功')
        } else {
            await createTask(formData)
            ElMessage.success('创建任务成功')
        }

        taskDialogVisible.value = false
        loadTaskData()
        emit('task-updated')
    } catch (error) {
        console.error('提交任务失败:', error)
        ElMessage.error('提交任务失败')
    } finally {
        taskFormLoading.value = false
    }
}

const handleTaskStatusChange = async (row, newStatus) => {
    try {
        await ElMessageBox.confirm(
            `确定要将任务状态更改为"${props.getDictLabelSync('inspection_status', newStatus)}"吗？`,
            '状态更改确认',
            {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }
        )

        await updateTaskStatus(row.id, newStatus)
        ElMessage.success('状态更新成功')
        loadTaskData()
    } catch (error) {
        if (error !== 'cancel') {
            console.error('更新任务状态失败:', error)
            ElMessage.error('更新任务状态失败')
        }
    }
}

const handleDeleteTask = async (row) => {
    try {
        await ElMessageBox.confirm(
            `确定要删除任务"${row.title}"吗？删除后无法恢复！`,
            '删除确认',
            {
                confirmButtonText: '确定删除',
                cancelButtonText: '取消',
                type: 'warning'
            }
        )

        await deleteTask(row.id)
        ElMessage.success('删除任务成功')
        loadTaskData()
    } catch (error) {
        if (error !== 'cancel') {
            console.error('删除任务失败:', error)
            ElMessage.error('删除任务失败')
        }
    }
}

// 导出
const handleExport = () => {
    emit('export-tasks', taskSearchForm.value);
}

// Expose methods to parent
defineExpose({
    handleExport,
    loadTaskData
});

</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;

.inspection-tasks {
    padding: var(--spacing-large);

    .table-section {
        margin-top: var(--spacing-large);
    }

    @include respond-to(sm) {
        padding: var(--spacing-sm);
    }
}
</style>