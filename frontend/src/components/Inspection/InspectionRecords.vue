<template>
    <div class="inspection-records">
        <!-- 记录搜索区域 -->
        <CommonSearch v-model="recordSearchForm" :items="recordSearchFields" :single-row="true"
            @search="handleRecordSearch" @reset="handleRecordReset">
            <template #actions>
                <CustomButton type="primary" @click="handleAddRecord" v-permission="'business:operate'">
                    <i class="fa fa-plus"></i>
                    新增
                </CustomButton>
            </template>
        </CommonSearch>

        <!-- 记录表格区域 -->
        <div class="table-section">
            <CommonTable :data="recordTableData" :columns="recordColumns" :loading="recordLoading"
                :show-selection="false" :show-index="true" :show-actions="true" :actions-width="160"
                @row-click="handleRecordRowClick">
                <!-- 自定义列渲染 -->
                <template #facilityDisplay="{ row }">
                    {{ getFacilityDisplay(row.facilityType, row.facilityId) }}
                </template>
                <template #facilityTypeDisplay="{ row }">
                    {{ getDictLabelSync('facility_type', row.facilityType) }}
                </template>
                <template #issueFlag="{ row }">
                    <el-tag :type="row.issueFlag ? 'danger' : 'success'" size="small">
                        {{ row.issueFlag ? '异常' : '正常' }}
                    </el-tag>
                </template>
                <template #issueDescription="{ row }">
                    <span :title="row.issueDescription">
                        {{ row.issueDescription ? (row.issueDescription.length > 20 ?
                            row.issueDescription.substring(0, 20) + '...' : row.issueDescription) : '-' }}
                    </span>
                </template>
                <template #processed="{ row }">
                    <el-tag :type="row.resolvedAt ? 'success' : 'info'" size="small">
                        {{ row.resolvedAt ? '已处理' : '未处理' }}
                    </el-tag>
                </template>
                <template #coverImage="{ row }">
                    <img v-if="row.coverImage" :src="row.coverImage" alt="图片" class="cover-image" />
                    <span v-else>-</span>
                </template>
                <template #recordTime="{ row }">
                    {{ formatDateTime(row.recordTime, 'YYYY-MM-DD HH:mm:ss') }}
                </template>

                <!-- 操作列 -->
                <template #actions="{ row }">
                    <CustomButton type="text" text-type="primary" @click="handleViewRecord(row)">
                        详情
                    </CustomButton>
                    <CustomButton v-if="!row.resolvedAt" type="text" text-type="warning" @click="openResolveDialog(row)"
                        v-permission="'business:operate'">
                        再次处理
                    </CustomButton>
                </template>
            </CommonTable>

            <CustomPagination v-model:current-page="recordPagination.currentPage"
                v-model:page-size="recordPagination.pageSize" :total="recordPagination.total"
                :page-sizes="[10, 20, 50, 100]" @size-change="handleRecordSizeChange"
                @current-change="handleRecordCurrentChange" />
        </div>

        <!-- 记录新增弹窗 -->
        <CustomDialog v-model:visible="recordDialogVisible" :title="'新增巡检记录'" width="70%" :loading="recordFormLoading"
            @confirm="handleRecordSubmit" @cancel="recordDialogVisible = false">
            <CommonForm ref="recordFormRef" v-model="recordFormData" :items="recordFormFields" :rules="recordFormRules"
                :show-actions="false" :label-width="'120px'">
                <!-- 记录时间插槽 -->
                <template #recordTime="{ item, formData, disabled }">
                    <el-date-picker v-model="formData.recordTime" type="datetime" placeholder="请选择记录时间"
                        format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" :disabled="disabled"
                        style="width: 100%" />
                </template>
                <!-- 文件上传插槽 -->
                <template #files>
                    <el-upload ref="uploadRef" v-model:file-list="uploadFileList" :auto-upload="false"
                        :show-file-list="true" :list-type="'picture-card'" :accept="'image/*'" :multiple="true"
                        :limit="10" :on-change="handleFileChange" :on-remove="handleFileRemove"
                        :on-preview="handleFilePreview">
                        <i class="fa fa-plus"></i>
                    </el-upload>
                </template>
            </CommonForm>
        </CustomDialog>

        <!-- 记录详情弹窗 -->
        <CustomDialog v-model:visible="recordDetailVisible" :title="'巡检记录详情'" width="70%" :show-confirm-button="false"
            :show-cancel-button="false">
            <div v-if="currentRecord" class="record-detail">
                <div class="detail-grid">
                    <div class="detail-item">
                        <label>记录时间：</label>
                        <span>{{ formatDateTime(currentRecord.recordTime, 'YYYY-MM-DD HH:mm:ss') }}</span>
                    </div>
                    <div class="detail-item">
                        <label>设施信息：</label>
                        <span>{{ getFacilityDisplay(currentRecord.facilityType, currentRecord.facilityId) }}</span>
                    </div>
                    <div class="detail-item">
                        <label>设备状态：</label>
                        <span>{{ getDictLabelSync('device_status', currentRecord.deviceStatus) }}</span>
                    </div>
                    <div class="detail-item">
                        <label>巡检人员：</label>
                        <span>{{ currentRecord.inspectorName || '-' }}</span>
                    </div>
                    <div class="detail-item">
                        <label>是否异常：</label>
                        <el-tag :type="currentRecord.issueFlag ? 'danger' : 'success'" size="small">
                            {{ currentRecord.issueFlag ? '异常' : '正常' }}
                        </el-tag>
                    </div>
                    <div class="detail-item full-width" v-if="currentRecord.issueDescription">
                        <label>异常描述：</label>
                        <span>{{ currentRecord.issueDescription }}</span>
                    </div>
                    <div class="detail-item full-width" v-if="currentRecord.resolution">
                        <label>解决方案：</label>
                        <span>{{ currentRecord.resolution }}</span>
                    </div>
                    <div class="detail-item" v-if="currentRecord.resolvedAt">
                        <label>解决时间：</label>
                        <span>{{ formatDateTime(currentRecord.resolvedAt, 'YYYY-MM-DD HH:mm:ss') }}</span>
                    </div>
                </div>

                <!-- 附件展示 -->
                <div v-if="recordAttachments.length > 0" class="attachments-section">
                    <h4>相关附件</h4>
                    <div class="attachment-grid">
                        <div v-for="attachment in recordAttachments" :key="attachment.id" class="attachment-item"
                            @click="handleAttachmentPreview(attachment)">
                            <img :src="attachment.filePath" :alt="attachment.fileName" />
                            <div class="attachment-name">{{ attachment.fileName }}</div>
                        </div>
                    </div>
                </div>
            </div>
        </CustomDialog>

        <!-- 再次处理弹窗 -->
        <CustomDialog v-model:visible="resolveDialogVisible" :title="'再次处理'" width="40%" :loading="resolveLoading"
            @confirm="handleResolveSubmit" @cancel="() => resolveDialogVisible = false">
            <CommonForm ref="resolveFormRef" v-model="resolveFormData" :items="resolveFormFields"
                :rules="resolveFormRules" :show-actions="false" :label-width="'100px'" />
        </CustomDialog>

        <!-- 图片预览弹窗 -->
        <el-dialog v-model="imagePreviewVisible" title="图片预览" width="80%" center>
            <div class="image-preview-container">
                <img :src="previewImageUrl" alt="预览图片" />
            </div>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import CommonSearch from '@/components/Common/CommonSearch.vue'
import CommonTable from '@/components/Common/CommonTable.vue'
import CommonForm from '@/components/Common/CommonForm.vue'
import CustomButton from '@/components/Common/CustomButton.vue'
import CustomDialog from '@/components/Common/CustomDialog.vue'
import CustomPagination from '@/components/Common/CustomPagination.vue'
import { formatDateTime, formatLocalTimeForAPI } from '@/utils/shared/common'
import {
    listTasks, listRecords, getRecordDetail, getRecordAttachments, createRecord, resolveRecord
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
const emit = defineEmits(['record-created', 'export-records'])

// ================= 记录相关 =================
const recordSearchForm = ref({
    facilityType: '',
    facilityId: null,
    recordTime: [],
    issueFlag: '',
    processed: '',
    inspectorId: null
})

const recordSearchFields = ref([
    {
        prop: 'facilityType',
        label: '设施类型',
        type: 'select',
        options: [],
        placeholder: '请选择设施类型',
        width: '220px',
        labelWidth: 'var(--form-label-width-standard)',
        clearable: true
    },
    {
        prop: 'facilityId',
        label: '巡检设施',
        type: 'select',
        options: [],
        placeholder: '请选择巡检设施',
        width: '220px',
        labelWidth: 'var(--form-label-width-standard)',
        clearable: true
    },
    {
        prop: 'recordTime',
        label: '巡检时间',
        type: 'datetimerange',
        startPlaceholder: '请选择开始时间',
        endPlaceholder: '请选择结束时间',
        showDuration: true,
        width: '405px',
        labelWidth: 'var(--form-label-width-standard)'
    },
    {
        prop: 'issueFlag',
        label: '异常情况',
        type: 'select',
        options: [
            { label: '正常', value: 0 },
            { label: '异常', value: 1 }
        ],
        placeholder: '请选择',
        width: '220px',
        labelWidth: 'var(--form-label-width-standard)',
        clearable: true
    },
    {
        prop: 'processed',
        label: '处理状态',
        type: 'select',
        options: [
            { label: '未处理', value: 0 },
            { label: '已处理', value: 1 }
        ],
        placeholder: '请选择',
        width: '220px',
        labelWidth: 'var(--form-label-width-standard)',
        clearable: true
    },
    {
        prop: 'inspectorId',
        label: '负责人',
        type: 'select',
        options: [],
        placeholder: '请选择负责人',
        width: '220px',
        labelWidth: 'var(--form-label-width-standard)',
        clearable: true
    }
])

const recordTableData = ref([])
const recordLoading = ref(false)
const recordPagination = reactive({
    currentPage: 1,
    pageSize: 10,
    total: 0
})

const recordColumns = [
    { prop: 'facilityDisplay', label: '巡检站点', width: 200 },
    { prop: 'facilityTypeDisplay', label: '巡检类型', width: 120 },
    { prop: 'issueFlag', label: '异常情况', width: 100 },
    { prop: 'issueDescription', label: '巡检情况', width: 200 },
    { prop: 'processed', label: '处理状态', width: 100 },
    { prop: 'coverImage', label: '图片', width: 90 },
    { prop: 'inspectorName', label: '负责人', width: 100 },
    { prop: 'recordTime', label: '日期', width: 150 }
]

// 记录表单相关
const recordDialogVisible = ref(false)
const recordFormLoading = ref(false)
const recordFormRef = ref(null)
const recordFormData = ref({})
const uploadRef = ref(null)
const uploadFileList = ref([])

const recordFormFields = ref([
    {
        prop: 'taskId',
        label: '关联任务',
        type: 'select',
        options: [],
        placeholder: '可选择关联任务',
        span: 12,
        clearable: true
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
        prop: 'recordTime',
        label: '记录时间',
        type: 'slot',
        required: true,
        span: 12
    },
    {
        prop: 'inspectorId',
        label: '负责人',
        type: 'select',
        required: true,
        placeholder: '请选择负责人',
        options: [],
        span: 12
    },
    {
        prop: 'deviceStatus',
        label: '设备状态',
        type: 'select',
        required: true,
        placeholder: '请选择设备状态',
        options: [],
        span: 12
    },
    {
        prop: 'issueFlag',
        label: '是否异常',
        type: 'select',
        required: true,
        placeholder: '请选择',
        options: [
            { label: '正常', value: 0 },
            { label: '异常', value: 1 }
        ],
        span: 12
    },
    {
        prop: 'issueDescription',
        label: '异常描述',
        type: 'textarea',
        placeholder: '请输入异常描述（异常时必填）',
        span: 24,
        rows: 3
    },
    {
        prop: 'resolution',
        label: '解决方案',
        type: 'textarea',
        placeholder: '请输入解决方案（可选）',
        span: 24,
        rows: 3
    },
    {
        prop: 'files',
        label: '相关图片',
        type: 'slot',
        span: 24
    }
])

const recordFormRules = reactive({
    facilityType: [{ required: true, message: '请选择设施类型', trigger: 'change' }],
    facilityId: [{ required: true, message: '请选择设施', trigger: 'change' }],
    recordTime: [{ required: true, message: '请选择记录时间', trigger: 'change' }],
    inspectorId: [{ required: true, message: '请选择负责人', trigger: 'change' }],
    deviceStatus: [{ required: true, message: '请选择设备状态', trigger: 'change' }],
    issueFlag: [{ required: true, message: '请选择是否异常', trigger: 'change' }],
    issueDescription: [
        {
            validator: (rule, value, callback) => {
                if (recordFormData.value.issueFlag === 1 && (!value || value.trim() === '')) {
                    callback(new Error('异常时必须填写异常描述'))
                } else {
                    callback()
                }
            },
            trigger: 'blur'
        }
    ]
})

// 再次处理相关
const resolveDialogVisible = ref(false)
const resolveFormRef = ref(null)
const resolveLoading = ref(false)
const resolveFormData = ref({ resolution: '' })
const resolveRecordId = ref(null)

const resolveFormFields = ref([
    { prop: 'resolution', label: '处理方案', type: 'textarea', required: true, placeholder: '请输入处理方案', span: 24, rows: 4 }
])
const resolveFormRules = reactive({
    resolution: [{ required: true, message: '请输入处理方案', trigger: 'blur' }]
})

// 记录详情相关
const recordDetailVisible = ref(false)
const currentRecord = ref(null)
const recordAttachments = ref([])

// 图片预览相关
const imagePreviewVisible = ref(false)
const previewImageUrl = ref('')

// ================= 生命周期 =================
onMounted(() => {
    updateSearchFieldOptions()
    updateFormFieldOptions()
    loadRecordData()
})

// ================= 选项更新 =================
const updateSearchFieldOptions = () => {
    const recordFacilityTypeField = recordSearchFields.value.find(f => f.prop === 'facilityType')
    if (recordFacilityTypeField) {
        recordFacilityTypeField.options = props.facilityTypeOptions
    }
    const inspectorField = recordSearchFields.value.find(f => f.prop === 'inspectorId')
    if (inspectorField) {
        inspectorField.options = props.personnelOptions
    }
}

const updateFormFieldOptions = () => {
    recordFormFields.value.find(f => f.prop === 'facilityType').options = props.facilityTypeOptions
    recordFormFields.value.find(f => f.prop === 'inspectorId').options = props.personnelOptions
    recordFormFields.value.find(f => f.prop === 'deviceStatus').options = props.dictMaps.device_status
}

// ================= 监听器 =================
watch(() => recordFormData.value.facilityType, async (newType, oldType) => {
    if (newType !== oldType) {
        recordFormData.value.facilityId = null
        recordFormData.value.taskId = null
    }

    const facilityField = recordFormFields.value.find(f => f.prop === 'facilityId')
    const taskField = recordFormFields.value.find(f => f.prop === 'taskId')

    if (newType) {
        await props.loadFacilityOptions(newType)
        facilityField.options = props.facilityOptions[newType] || []
        facilityField.disabled = false
        facilityField.placeholder = '请选择设施'

        await loadTaskOptions(newType, recordFormData.value.facilityId)
    } else {
        facilityField.options = []
        facilityField.disabled = true
        facilityField.placeholder = '请先选择设施类型'
        taskField.options = []
    }
})

watch(() => recordFormData.value.facilityId, async (newId) => {
    recordFormData.value.taskId = null
    if (newId && recordFormData.value.facilityType) {
        await loadTaskOptions(recordFormData.value.facilityType, newId)
    } else {
        const taskField = recordFormFields.value.find(f => f.prop === 'taskId')
        taskField.options = []
    }
})

watch(() => recordSearchForm.value.facilityType, async (newType, oldType) => {
    if (newType !== oldType) {
        recordSearchForm.value.facilityId = null
    }

    const facilityField = recordSearchFields.value.find(f => f.prop === 'facilityId')

    if (newType) {
        await props.loadFacilityOptions(newType)
        if (facilityField) {
            facilityField.options = props.facilityOptions[newType] || []
            facilityField.placeholder = '请选择设施'
        }
    } else {
        if (facilityField) {
            const allFacilities = []
            Object.keys(props.facilityOptions).forEach(type => {
                allFacilities.push(...props.facilityOptions[type])
            })
            facilityField.options = allFacilities
            facilityField.placeholder = '请选择巡检设施'
        }
    }
})

const loadTaskOptions = async (facilityType, facilityId) => {
    try {
        const response = await listTasks({
            facilityType,
            facilityId,
            status: 'PENDING,IN_PROGRESS',
            size: 100
        })

        const taskField = recordFormFields.value.find(f => f.prop === 'taskId')
        taskField.options = (response.items || []).map(task => ({
            label: task.title,
            value: task.id
        }))
    } catch (error) {
        console.error('加载任务选项失败:', error)
    }
}

// ================= 记录方法 =================
const loadRecordData = async () => {
    recordLoading.value = true
    try {
        const { recordTime, ...otherParams } = recordSearchForm.value

        const params = {
            page: recordPagination.currentPage,
            size: recordPagination.pageSize,
            ...filterValidParams(otherParams),
            ...processTimeRangeParams(recordTime)
        }

        const response = await listRecords(params)
        recordTableData.value = response.items || []
        recordPagination.total = response.total || 0
    } catch (error) {
        console.error('加载记录数据失败:', error)
        ElMessage.error('加载记录数据失败')
    } finally {
        recordLoading.value = false
    }
}

const handleRecordSearch = () => {
    recordPagination.currentPage = 1
    loadRecordData()
}

const handleRecordReset = () => {
    recordSearchForm.value = {
        facilityType: '',
        facilityId: null,
        recordTime: [],
        issueFlag: '',
        processed: '',
        inspectorId: null
    }
    recordPagination.currentPage = 1
    loadRecordData()
}

const handleRecordSizeChange = (size) => {
    recordPagination.pageSize = size
    recordPagination.currentPage = 1
    loadRecordData()
}

const handleRecordCurrentChange = (page) => {
    recordPagination.currentPage = page
    loadRecordData()
}

const handleRecordRowClick = (row) => {
    // 可以在这里添加行点击逻辑
}

const handleAddRecord = () => {
    recordFormData.value = {
        taskId: null,
        facilityType: '',
        facilityId: null,
        recordTime: '',
        inspectorId: null,
        deviceStatus: '',
        issueFlag: 0,
        issueDescription: '',
        resolution: ''
    }
    uploadFileList.value = []
    recordDialogVisible.value = true
}

const handleRecordSubmit = async () => {
    if (!recordFormRef.value) return

    try {
        const isValid = await recordFormRef.value.validate()
        if (!isValid) return

        const formData = recordFormRef.value.formData
        recordFormLoading.value = true

        const submitData = new FormData()
        submitData.append('data', new Blob([JSON.stringify(formData)], { type: 'application/json' }))

        uploadFileList.value.forEach(file => {
            if (file.raw) {
                submitData.append('files', file.raw)
            }
        })

        await createRecord(submitData)
        ElMessage.success('创建记录成功')
        recordDialogVisible.value = false
        loadRecordData()

        if (formData.taskId && formData.issueFlag === 0) {
            emit('record-created')
        }
    } catch (error) {
        console.error('提交记录失败:', error)
        ElMessage.error('提交记录失败')
    } finally {
        recordFormLoading.value = false
    }
}

const handleViewRecord = async (row) => {
    try {
        currentRecord.value = await getRecordDetail(row.id)
        recordAttachments.value = await getRecordAttachments(row.id)
        recordDetailVisible.value = true
    } catch (error) {
        console.error('加载记录详情失败:', error)
        ElMessage.error('加载记录详情失败')
    }
}

const openResolveDialog = (row) => {
    resolveRecordId.value = row.id
    resolveFormData.value = { resolution: '' }
    resolveDialogVisible.value = true
}

const handleResolveSubmit = async () => {
    if (!resolveFormRef.value) return
    const isValid = await resolveFormRef.value.validate()
    if (!isValid) return

    try {
        resolveLoading.value = true
        await resolveRecord(resolveRecordId.value, resolveFormData.value.resolution)
        ElMessage.success('处理完成')
        resolveDialogVisible.value = false
        loadRecordData()
    } catch (error) {
        console.error('再次处理失败:', error)
        ElMessage.error('再次处理失败')
    } finally {
        resolveLoading.value = false
    }
}

const handleExport = () => {
    const { recordTime, ...otherParams } = recordSearchForm.value;
    const params = {
        ...filterValidParams(otherParams),
        ...processTimeRangeParams(recordTime),
    };
    emit('export-records', params);
};


// ================= 文件上传 =================
const handleFileChange = (file, fileList) => {
    uploadFileList.value = fileList
}

const handleFileRemove = (file, fileList) => {
    uploadFileList.value = fileList
}

const handleFilePreview = (file) => {
    const url = file.url || URL.createObjectURL(file.raw)
    previewImageUrl.value = url
    imagePreviewVisible.value = true
}

const handleAttachmentPreview = (attachment) => {
    previewImageUrl.value = attachment.filePath
    imagePreviewVisible.value = true
}

// ================= 工具函数 =================
const filterValidParams = (params) => {
    const filtered = {}
    Object.keys(params).forEach(key => {
        const value = params[key]
        if (value !== null && value !== '' && value !== undefined) {
            filtered[key] = value
        }
    })
    return filtered
}

const processTimeRangeParams = (timeRange) => {
    if (!timeRange || !Array.isArray(timeRange) || timeRange.length !== 2) {
        return {}
    }

    return {
        startTime: formatLocalTimeForAPI(timeRange[0]),
        endTime: formatLocalTimeForAPI(timeRange[1])
    }
}

// Expose methods to parent
defineExpose({
    handleExport,
    loadRecordData
});
</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;

.inspection-records {
    padding: var(--spacing-medium);

    .table-section {
        margin-top: var(--spacing-medium);
    }

    // 封面图片样式
    .cover-image {
        width: var(--thumbnail-width);
        height: var(--thumbnail-height);
        object-fit: cover;
    }

    // 记录详情样式
    .record-detail {
        .detail-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: var(--spacing-medium);
            margin-bottom: var(--spacing-large);

            .detail-item {
                display: flex;
                align-items: flex-start;
                gap: var(--spacing-sm);

                &.full-width {
                    grid-column: 1 / -1;
                    flex-direction: column;
                    gap: var(--spacing-xs);
                }

                label {
                    font-weight: 500;
                    color: var(--text-primary);
                    white-space: nowrap;
                    min-width: var(--button-min-width);
                }

                span {
                    color: var(--text-secondary);
                    word-break: break-word;
                }
            }
        }

        .attachments-section {
            h4 {
                margin: 0 0 var(--spacing-medium) 0;
                color: var(--text-primary);
                font-size: 16px;
                font-weight: 500;
            }

            .attachment-grid {
                display: grid;
                grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
                gap: var(--spacing-medium);

                .attachment-item {
                    border: 1px solid var(--border-light);
                    border-radius: var(--border-radius-small);
                    overflow: hidden;
                    cursor: pointer;
                    transition: var(--transition-fast);

                    &:hover {
                        border-color: var(--primary-color);
                        box-shadow: var(--shadow-light);
                    }

                    img {
                        width: 100%;
                        height: 100px;
                        object-fit: cover;
                        display: block;
                    }

                    .attachment-name {
                        padding: var(--spacing-xs);
                        font-size: 12px;
                        color: var(--text-secondary);
                        text-align: center;
                        background: var(--bg-tertiary);
                        overflow: hidden;
                        text-overflow: ellipsis;
                        white-space: nowrap;
                    }
                }
            }
        }
    }

    // 图片预览样式
    .image-preview-container {
        text-align: center;

        img {
            max-width: 100%;
            max-height: 70vh;
            object-fit: contain;
        }
    }

    @include respond-to(md) {
        .record-detail .detail-grid {
            grid-template-columns: 1fr;

            .detail-item.full-width {
                grid-column: 1;
            }
        }
    }

    @include respond-to(sm) {
        padding: var(--spacing-sm);

        .record-detail .attachments-section .attachment-grid {
            grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
        }
    }
}
</style>