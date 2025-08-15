<template>
    <div class="record-management">
        <!-- 搜索区域 -->
        <div class="record-management__search-section">
            <CommonSearch v-model="searchForm" :items="enhancedSearchFields" :single-row="true" @search="handleSearch"
                @reset="handleResetSearch">
                <template #actions>
                    <CustomButton type="primary" @click="handleAdd" v-permission="'business:operate'">
                        <i class="fa fa-plus"></i>
                        新增
                    </CustomButton>
                </template>
            </CommonSearch>
        </div>

        <!-- 表格区域 -->
        <div class="record-management__table">
            <CommonTable :data="dataList" :columns="enhancedTableColumns" :loading="loading" :total="pagination.total"
                :current-page="pagination.currentPage" :page-size="pagination.pageSize" :show-index="true"
                :show-actions="true" :actions-width="140" :show-toolbar="false" :show-pagination="false"
                @size-change="handleSizeChange" @current-change="handleCurrentChange">
                <!-- 预警等级列 -->
                <template #warningLevel="{ row }">
                    <span>{{ getWarningLevelName(row.warningLevel) }}</span>
                </template>

                <!-- 预警状态列 -->
                <template #warningStatus="{ row }">
                    <span>{{ row.warningStatus }}</span>
                </template>

                <!-- 预警内容列 -->
                <template #warningContent="{ row }">
                    <el-tooltip v-if="row.warningContent && row.warningContent.length > 50"
                        :content="row.warningContent" placement="top" class="record-management__tooltip-wrapper">
                        <span class="record-management__content-text">{{ row.warningContent.substring(0, 50)
                            }}...</span>
                    </el-tooltip>
                    <span v-else class="record-management__content-text">{{ row.warningContent || '-' }}</span>
                </template>

                <!-- 发生时间列 -->
                <template #occurredAt="{ row }">
                    <span>{{ formatDateTimeUtil(row.occurredAt) }}</span>
                </template>

                <!-- 持续时长列 -->
                <template #duration="{ row }">
                    <span>{{ formatDurationUtil(row.occurredAt, row.resolvedAt) }}</span>
                </template>

                <!-- 操作列 -->
                <template #actions="{ row }">
                    <div class="record-management__action-buttons">
                        <CustomButton v-if="row.warningStatus === '进行中'" type="primary" size="small"
                            @click="handleResolve(row)" v-permission="'business:operate'">
                            <i class="fa fa-check"></i>
                            解除
                        </CustomButton>
                        <CustomButton v-else type="text" size="small" disabled>
                            已解除
                        </CustomButton>
                    </div>
                </template>
            </CommonTable>

            <!-- 自定义分页 -->
            <CustomPagination :current-page="pagination.currentPage" :page-size="pagination.pageSize"
                :total="pagination.total" :page-sizes="[10, 20, 50, 100]" @size-change="handleSizeChange"
                @current-change="handleCurrentChange" />
        </div>

        <!-- 解除对话框 -->
        <CustomDialog :visible="resolveDialogVisible" @update:visible="resolveDialogVisible = $event" title="解除预警"
            width="550px" :loading="resolveLoading" confirm-button-text="确认解除" @confirm="handleResolveSubmit"
            @cancel="handleCloseResolveDialog" custom-class="resolve-dialog">
            <!-- 预警信息概览 -->
            <div class="resolve-dialog__overview" v-if="currentRecord && Object.keys(currentRecord).length > 0">
                <div class="overview-grid">
                    <div class="overview-item">
                        <span class="overview-label overview-label--with-icon"><i
                                class="overview-label__icon fa fa-map-marker"></i>
                            预警地点</span>
                        <span class="overview-value">{{ currentRecord.warningLocation || '-' }}</span>
                    </div>
                    <div class="overview-item">
                        <span class="overview-label overview-label--with-icon"><i
                                class="overview-label__icon fa fa-tags"></i>
                            预警类型</span>
                        <span class="overview-value">{{ currentRecord.warningType || '-' }}</span>
                    </div>
                    <div class="overview-item">
                        <span class="overview-label overview-label--with-icon"><i
                                class="overview-label__icon fa fa-line-chart"></i>
                            预警等级</span>
                        <span class="overview-value">
                            <el-tag v-if="currentRecord.warningLevel"
                                :type="getWarningLevelType(currentRecord.warningLevel)" size="small">
                                {{ getWarningLevelName(currentRecord.warningLevel) }}
                            </el-tag>
                            <span v-else>-</span>
                        </span>
                    </div>
                    <div class="overview-item">
                        <span class="overview-label overview-label--with-icon"><i
                                class="overview-label__icon fa fa-clock-o"></i>
                            发生时间</span>
                        <span class="overview-value">{{ formatDateTimeUtil(currentRecord.occurredAt) || '-' }}</span>
                    </div>
                    <div class="overview-item overview-item--full-width">
                        <span class="overview-label"><i class="fa fa-hourglass-half"></i> 持续时长</span>
                        <span class="overview-value">{{ formatDurationUtil(currentRecord.occurredAt,
                            currentRecord.resolvedAt)
                        }}</span>
                    </div>
                </div>
            </div>

            <!-- 解除表单 -->
            <CommonForm ref="resolveFormRef" v-model="resolveFormData" :items="resolveFormItems"
                :rules="resolveFormRules" label-width="var(--form-label-width-form)" label-position="top"
                :show-actions="false" class="record-management__resolve-form">

                <template #resolveTime>
                    <div class="resolve-form__time-display">
                        <div class="resolve-form__time-content">
                            <span class="resolve-form__time-label">解除时间</span>
                            <div class="resolve-form__time-value">
                                <i class="fa fa-calendar-check-o"></i>
                                <span>{{ resolveFormData.resolveTime }}</span>
                            </div>
                        </div>
                        <small class="resolve-form__time-note">（系统自动更新，无需手动输入）</small>
                    </div>
                </template>

                <template #confirmation>
                    <div class="resolve-form__confirmation">
                        <el-checkbox v-model="resolveFormData.confirmed" size="large">
                            我已确认预警情况，并执行解除操作。
                        </el-checkbox>
                        <p class="resolve-form__note">
                            <i class="fa fa-info-circle resolve-form__note-icon"></i>
                            请注意：预警解除后，状态将变更为"已解除"且无法撤销。
                        </p>
                    </div>
                </template>
            </CommonForm>
        </CustomDialog>

        <!-- 新增/编辑对话框 -->
        <!-- RecordDialog组件内联 -->
        <CustomDialog :visible="dialogVisible" @update:visible="dialogVisible = $event" title="新增预警记录"
            width="var(--panel-height-default)" :loading="submitLoading" confirmButtonText="创建" @confirm="handleSubmit"
            @cancel="handleCloseRecordDialog">
            <CommonForm ref="recordFormRef" v-model="formData" :items="recordFormItems" :rules="recordFormRules"
                label-width="var(--form-label-width-detail)" :show-actions="false">
                <!-- 预警类型选择器插槽 -->
                <template #warningType="{ item, formData, disabled }">
                    <CustomSelect v-model="formData.warningType" :options="warningTypeOptions" value-key="value"
                        label-key="label" placeholder="请选择预警类型" :disabled="disabled" clearable />
                </template>

                <!-- 预警等级选择器插槽 -->
                <template #warningLevel="{ item, formData, disabled }">
                    <CustomSelect v-model="formData.warningLevel" :options="warningLevelOptions" value-key="value"
                        label-key="label" placeholder="请选择预警等级" :disabled="disabled" clearable />
                </template>

                <!-- 发生时间日期时间选择器插槽 -->
                <template #occurredAt="{ item, formData, disabled }">
                    <el-date-picker v-model="formData.occurredAt" type="datetime" placeholder="请选择发生时间"
                        format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" :disabled="disabled"
                        :disabled-date="disabledDate" style="width: 100%" />
                </template>
            </CommonForm>
        </CustomDialog>
    </div>
</template>

<!-- 预警记录管理组件 - 水利工程预警记录管理平台 -->
<script setup>
import { ref, reactive, computed, onMounted, watch, nextTick } from "vue";
import { ElMessage } from "element-plus";
import CommonSearch from '@/components/Common/CommonSearch.vue';
import CommonTable from '@/components/Common/CommonTable.vue';
import CustomButton from '@/components/Common/CustomButton.vue';
import CustomPagination from '@/components/Common/CustomPagination.vue';
import CommonForm from '@/components/Common/CommonForm.vue';
import CustomSelect from '@/components/Common/CustomSelect.vue';
import CustomDialog from '@/components/Common/CustomDialog.vue';
import {
    getRecordList,
    createRecord,
    resolveWarning,
    getWarningLocationOptions,
} from "@/api/warning/records";
import { useDictionary } from "@/composables/useDictionary";
import { formatDateTime as formatDateTimeUtil, formatLocalTimeForAPI, formatDuration as formatDurationUtil } from '@/utils/shared/common';

// 组合式函数
const { getDictData } = useDictionary();

// 响应式状态管理
// 页面加载状态
const loading = ref(false);

// 数据列表
const dataList = ref([]);

// 解除预警对话框状态 - 来自ResolveDialog组件
const resolveDialogVisible = ref(false);
const currentRecord = ref({});
const resolveLoading = ref(false);
const resolveTimeInterval = ref(null); // 用于存储定时器

// 新增/编辑对话框状态 - 来自RecordDialog组件
const dialogVisible = ref(false);
const submitLoading = ref(false);

// RecordDialog表单引用
const recordFormRef = ref(null);

// ResolveDialog表单引用和数据
const resolveFormRef = ref(null);
const resolveFormData = reactive({
    resolveTime: '',
    resolveRemark: '',
    confirmed: false,
});

// 分页配置
const pagination = reactive({
    currentPage: 1,
    pageSize: 10,
    total: 0,
});

// 搜索表单数据
const searchForm = reactive({
    occurredTimeRange: [],
    warningLocation: "",
    warningType: "",
    warningLevel: "",
    warningStatus: "",
});

// 新增/编辑表单数据
const formData = reactive({
    id: null,
    warningLocation: "",
    warningType: "",
    warningLevel: "",
    warningContent: "",
    projectName: "",
    occurredAt: "",
    thresholdId: null,
});



// 下拉选项数据
const warningLocationOptions = ref([]);
const warningTypeOptions = ref([]);
const warningLevelOptions = ref([]);
const warningStatusOptions = ref([]);

// 计算属性配置
const enhancedTableColumns = computed(() => [
    { prop: 'warningLocation', label: '预警地点', minWidth: 120 },
    { prop: 'warningType', label: '预警类型', width: 100, align: 'center' },
    {
        prop: 'warningLevel',
        label: '预警等级',
        width: 100,
        align: 'center',
        slot: 'warningLevel',
    },
    {
        prop: 'warningContent',
        label: '预警内容',
        minWidth: 200,
        align: 'center',
        slot: 'warningContent',
    },
    {
        prop: 'warningStatus',
        label: '预警状态',
        width: 100,
        align: 'center',
        slot: 'warningStatus',
    },
    { prop: 'projectName', label: '所属工程', width: 150 },
    {
        prop: 'occurredAt',
        label: '发生时间',
        width: 160,
        align: 'center',
        slot: 'occurredAt',
    },
    {
        prop: 'duration',
        label: '持续时长',
        width: 120,
        align: 'center',
        slot: 'duration',
    },
]);

// 搜索字段配置
const enhancedSearchFields = computed(() => {
    return [
        {
            type: 'datetimerange',
            prop: 'occurredTimeRange',
            label: '发生时间',
            startPlaceholder: '开始时间',
            endPlaceholder: '结束时间',
            labelWidth: 'var(--form-label-width-standard)',
            showDuration: true,
            format: 'YYYY-MM-DD HH:mm:ss',
            span: 8
        },
        {
            type: 'select',
            prop: 'warningLocation',
            label: '预警地点',
            placeholder: '请选择',
            labelWidth: 'var(--form-label-width-standard)',
            options: warningLocationOptions.value,
            clearable: true,
            filterable: false,
        },
        {
            type: 'select',
            prop: 'warningType',
            label: '预警类型',
            placeholder: '请选择',
            labelWidth: 'var(--form-label-width-standard)',
            options: warningTypeOptions.value,
            clearable: true,
            filterable: false,
        },
        {
            type: 'select',
            prop: 'warningLevel',
            label: '预警等级',
            placeholder: '请选择',
            labelWidth: 'var(--form-label-width-standard)',
            options: warningLevelOptions.value,
            clearable: true,
            filterable: false,
        },
        {
            type: 'select',
            prop: 'warningStatus',
            label: '预警状态',
            placeholder: '请选择',
            labelWidth: 'var(--form-label-width-standard)',
            options: warningStatusOptions.value,
            clearable: true,
            filterable: false,
        }
    ];
});

// 新增表单配置
const recordFormItems = computed(() => [
    {
        type: 'input',
        prop: 'warningLocation',
        label: '预警地点',
        placeholder: '请输入预警地点',
        maxlength: 255,
        showWordLimit: true,
        clearable: true,
        span: 24,
        required: true
    },
    {
        type: 'slot',
        prop: 'warningType',
        label: '预警类型',
        span: 12,
        required: true
    },
    {
        type: 'slot',
        prop: 'warningLevel',
        label: '预警等级',
        span: 12,
        required: true
    },
    {
        type: 'textarea',
        prop: 'warningContent',
        label: '预警内容',
        placeholder: '请输入预警内容',
        maxlength: 1000,
        showWordLimit: true,
        rows: 3,
        span: 24,
        required: true
    },
    {
        type: 'input',
        prop: 'projectName',
        label: '所属工程',
        placeholder: '请输入所属工程',
        maxlength: 255,
        showWordLimit: true,
        clearable: true,
        span: 24
    },
    {
        type: 'slot',
        prop: 'occurredAt',
        label: '发生时间',
        span: 24,
        required: true
    },
    {
        type: 'input-number',
        prop: 'thresholdId',
        label: '关联预警指标ID',
        placeholder: '请输入关联预警指标ID',
        min: 1,
        controlsPosition: 'right',
        span: 24
    }
]);

// 表单验证规则
const recordFormRules = computed(() => ({
    warningLocation: [
        { required: true, message: '请输入预警地点', trigger: 'blur' },
        { max: 255, message: '预警地点长度不能超过255个字符', trigger: 'blur' },
    ],
    warningType: [
        { required: true, message: '请选择预警类型', trigger: 'change' },
    ],
    warningLevel: [
        { required: true, message: '请选择预警等级', trigger: 'change' },
    ],
    warningContent: [
        { required: true, message: '请输入预警内容', trigger: 'blur' },
        { max: 1000, message: '预警内容长度不能超过1000个字符', trigger: 'blur' },
    ],
    projectName: [
        { max: 255, message: '所属工程长度不能超过255个字符', trigger: 'blur' },
    ],
    occurredAt: [
        {
            required: true,
            message: '请选择发生时间',
            trigger: 'change',
            validator: (rule, value, callback) => {
                if (!value) {
                    callback(new Error('请选择发生时间'));
                } else if (typeof value === 'string' && value.trim() === '') {
                    callback(new Error('请选择发生时间'));
                } else {
                    // 验证通过
                    callback();
                }
            }
        },
    ],
    thresholdId: [
        { type: 'number', message: '关联预警指标ID必须为数字', trigger: 'blur' },
    ],
}));

// 解除表单配置
const resolveFormItems = computed(() => [
    {
        type: 'slot',
        prop: 'resolveTime',
        label: '', // 标签由插槽内部管理
        span: 24,
    },
    {
        type: 'textarea',
        prop: 'resolveRemark',
        label: '解除备注',
        placeholder: '请输入解除备注（可选）',
        rows: 3,
        maxlength: 500,
        showWordLimit: true,
        resize: 'none',
        span: 24,
    },
    {
        type: 'slot',
        prop: 'confirmation',
        label: '', // 标签由插槽内部管理
        span: 24,
    },
]);

// 解除表单验证规则
const resolveFormRules = computed(() => ({
    resolveRemark: [
        { max: 500, message: '解除备注长度不能超过500个字符', trigger: 'blur' },
    ],
    confirmed: [
        {
            required: true,
            message: '请确认要解除此预警',
            trigger: 'change',
            validator: (rule, value, callback) => {
                if (value) {
                    callback();
                } else {
                    callback(new Error('请勾选以确认解除预警'));
                }
            },
        },
    ],
}));

// 数据获取方法
const fetchData = async () => {
    loading.value = true;
    try {
        // 处理搜索参数，将时间范围转换为startTime和endTime
        const { occurredTimeRange, ...otherParams } = searchForm;
        const params = {
            page: pagination.currentPage,
            size: pagination.pageSize,
            ...otherParams,
        };

        // 如果选择了时间范围，转换为startTime和endTime
        if (occurredTimeRange && occurredTimeRange.length === 2) {
            // 使用formatLocalTimeForAPI保持本地时区
            params.startTime = formatLocalTimeForAPI(occurredTimeRange[0]);
            params.endTime = formatLocalTimeForAPI(occurredTimeRange[1]);
        }

        const response = await getRecordList(params);

        if (response) {
            // 兼容不同的响应格式
            dataList.value = response.items || response.records || response || [];
            pagination.total = response.total || 0;
        }
    } catch (err) {
        ElMessage.error(err.message || "获取数据失败");
    } finally {
        loading.value = false;
    }
};

/**
 * 初始化所有选项数据
 */
const initOptions = async () => {
    const fetchTasks = [
        // 获取预警地点选项
        getWarningLocationOptions().then(response => {
            if (response && Array.isArray(response)) {
                warningLocationOptions.value = response.map(item => ({
                    label: item,
                    value: item,
                }));
            }
        }).catch(err => {
            console.warn('获取预警地点选项失败:', err);
            warningLocationOptions.value = [];
        }),

        // 获取预警类型选项
        getDictData("warning_type").then(data => {
            warningTypeOptions.value = data || [];
        }).catch(err => {
            console.warn('获取预警类型失败:', err);
            warningTypeOptions.value = [];
        }),

        // 获取预警等级选项
        getDictData("warning_level").then(data => {
            warningLevelOptions.value = data || [];
        }).catch(err => {
            console.warn('获取预警等级失败:', err);
            warningLevelOptions.value = [];
        }),

        // 获取预警状态选项
        getDictData("warning_status").then(data => {
            warningStatusOptions.value = data || [];
        }).catch(err => {
            console.warn('获取预警状态失败:', err);
            warningStatusOptions.value = [];
        })
    ];

    await Promise.allSettled(fetchTasks);
};

// 搜索处理
const handleSearch = async (searchData) => {
    // 如果有传入搜索数据，则更新 searchForm
    if (searchData) {
        Object.keys(searchData).forEach(key => {
            if (searchForm.hasOwnProperty(key)) {
                searchForm[key] = searchData[key];
            }
        });
    }

    pagination.currentPage = 1;
    await fetchData();
};

// 重置搜索
const handleResetSearch = async () => {
    Object.keys(searchForm).forEach((key) => {
        if (key === "occurredTimeRange") {
            searchForm[key] = [];
        } else {
            searchForm[key] = "";
        }
    });
    await handleSearch();
};

// 新增操作
const handleAdd = () => {
    // 重置表单数据，确保新增时表单是干净的
    resetFormData();
    // 打开对话框
    dialogVisible.value = true;
};

// 表单提交处理
const handleSubmit = async () => {
    // 检查表单引用是否存在
    if (!recordFormRef.value) {
        ElMessage.error('表单未初始化，请稍后重试');
        return;
    }

    // 执行表单验证
    try {
        const isValid = await recordFormRef.value.validate();
        if (!isValid) {
            // 验证失败时，错误信息已由表单组件显示，无需重复提示
            return;
        }
    } catch (validationError) {
        console.error('表单验证时发生错误:', validationError);
        return;
    }

    // 获取表单数据并提交
    const currentFormData = recordFormRef.value.formData;
    const submitData = { ...currentFormData };

    submitLoading.value = true;
    try {
        await createRecord(submitData);
        ElMessage.success("创建成功");
        dialogVisible.value = false;
        await fetchData();
    } catch (err) {
        console.error('创建预警记录失败:', err);
        if (err.message) {
            ElMessage.error(err.message);
        } else {
            ElMessage.error("创建失败，请重试");
        }
    } finally {
        submitLoading.value = false;
    }
};

/**
 * 通用表单重置方法
 */
const resetFormData = () => {
    Object.assign(formData, {
        id: null,
        warningLocation: "",
        warningType: "",
        warningLevel: "",
        warningContent: "",
        projectName: "",
        occurredAt: "",
        thresholdId: null,
    });
};

// 预警解除操作
const handleResolve = (row) => {
    if (row.warningStatus === "已解除") {
        ElMessage.warning("该预警已解除");
        return;
    }

    currentRecord.value = { ...row };
    resolveDialogVisible.value = true;
};

// 关闭解除对话框
const handleCloseResolveDialog = () => {
    resolveDialogVisible.value = false;
};

/**
 * 重置解除表单数据
 */
const resetResolveFormData = () => {
    Object.assign(resolveFormData, {
        resolveTime: formatDateTimeUtil(new Date(), 'YYYY-MM-DD HH:mm:ss'),
        resolveRemark: "",
        confirmed: false,
    });
};

// 解除表单提交处理
const handleResolveSubmit = async () => {
    if (!resolveFormRef.value) return;

    try {
        await resolveFormRef.value.validate();

        if (!resolveFormData.confirmed) {
            ElMessage.warning('请确认要解除此预警');
            return;
        }

        // 验证ID是否存在
        if (!currentRecord.value.id) {
            ElMessage.error('预警记录ID不存在，无法解除预警');
            return;
        }

        resolveLoading.value = true;
        await resolveWarning(currentRecord.value.id, {
            resolveRemark: resolveFormData.resolveRemark,
        });
        ElMessage.success("预警解除成功");
        resolveDialogVisible.value = false;
        fetchData();
    } catch (err) {
        if (err.message) {
            ElMessage.error(err.message);
        } else {
            ElMessage.error('预警解除失败，请重试');
        }
    } finally {
        resolveLoading.value = false;
    }
};

// 获取预警等级名称
const getWarningLevelName = (level) => {
    if (!level) return '-';
    const levelItem = warningLevelOptions.value.find(item => item.value === level.toString());
    return levelItem ? levelItem.label : `${level}级预警`;
};

// 获取预警等级类型
const getWarningLevelType = (level) => {
    if (!level) return '';
    const levelItem = warningLevelOptions.value.find(item => item.value === level.toString());
    // 根据预警等级值返回对应的Element Plus标签类型
    const typeMap = { '1': 'danger', '2': 'warning', '3': 'info', '4': 'success' };
    return typeMap[level] || '';
};

// 分页处理
const handleSizeChange = (size) => {
    pagination.pageSize = size;
    pagination.currentPage = 1;
    fetchData();
};

// 当前页变化处理
const handleCurrentChange = (page) => {
    pagination.currentPage = page;
    fetchData();
};

// 工具方法
const disabledDate = (time) => {
    return time.getTime() < Date.now() - 8.64e7; // 禁用当前时间之前的日期
};

// 生命周期
onMounted(async () => {
    await initOptions();
    await fetchData();
});

// 监听器
watch(
    () => resolveDialogVisible.value,
    (visible) => {
        if (visible) {
            resetResolveFormData();
            // 启动实时时间更新
            resolveTimeInterval.value = setInterval(() => {
                resolveFormData.resolveTime = formatDateTimeUtil(new Date(), 'YYYY-MM-DD HH:mm:ss');
            }, 1000);
            nextTick(() => resolveFormRef.value?.clearValidate());
        } else {
            // 清理定时器
            if (resolveTimeInterval.value) {
                clearInterval(resolveTimeInterval.value);
                resolveTimeInterval.value = null;
            }
        }
    }
);

// 监听新增对话框状态
watch(
    () => dialogVisible.value,
    (visible) => {
        if (visible) {
            nextTick(() => {
                initializeDefaultTime();
                recordFormRef.value?.clearValidate();
            });
        } else if (!submitLoading.value) {
            resetFormData();
        }
    }
);

// 初始化默认时间
const initializeDefaultTime = () => {
    if (!formData.occurredAt) {
        const now = new Date();
        // 使用formatDateTime确保格式一致性，避免时区问题
        formData.occurredAt = formatDateTimeUtil(now, 'YYYY-MM-DD HH:mm:ss');
    }
};

// 关闭新增对话框
const handleCloseRecordDialog = () => {
    dialogVisible.value = false;
    // 表单重置由对话框监听器处理，无需重复调用
};
</script>

<style scoped lang="scss">
@use "../../assets/styles/index.scss" as *;

/* 预警记录管理组件样式 */
.record-management {
    background: var(--bg-primary);
    min-height: calc(100vh - var(--page-min-height-offset));
    padding: var(--spacing-large);

    &__search-section {
        margin-bottom: var(--spacing-large);
    }

    // ==================== 表格容器样式 ====================
    /**
     * 表格容器 - 卡片风格设计
     * 包含表格内容和分页组件
     */
    &__table {
        background: var(--bg-primary);
        border-radius: var(--border-radius-large);
        box-shadow: var(--shadow-card);
        border: 1px solid var(--border-color);
        overflow: hidden;

        /* 自定义分页样式 */
        :deep(.custom-pagination) {
            padding: var(--spacing-base);
            justify-content: center;
            border-top: 1px solid var(--border-color);
        }

        // 表格加载状态修饰符
        &--loading {
            position: relative;

            &::after {
                content: '';
                position: absolute;
                top: 0;
                left: 0;
                right: 0;
                bottom: 0;
                background: rgba(255, 255, 255, 0.7);
                z-index: var(--z-index-dropdown);
            }
        }
    }

    /* 内容显示样式 */
    &__tooltip-wrapper,
    &__content-text {
        @include flex-center;
        flex-direction: column;
        width: 100%;
        max-width: var(--sidebar-width);
        margin: 0 auto;
        word-break: break-word;
    }

    &__action-buttons {
        display: flex;
        align-items: center;
        justify-content: center;
    }
}

/* 解除对话框样式 */
:deep(.resolve-dialog .el-dialog__body) {
    padding: var(--spacing-large) var(--spacing-extra-large) !important;
}

/* 预警概览区域 */
.resolve-dialog__overview {
    margin-bottom: var(--spacing-large);
    background: var(--bg-secondary);
    border: 1px solid var(--border-color);
    border-radius: var(--border-radius-large);
    box-shadow: var(--shadow-card);
    overflow: hidden;
    padding: var(--spacing-large);

    .overview-grid {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: var(--spacing-base) var(--spacing-large);
    }

    .overview-item {
        display: flex;
        flex-direction: column;
        gap: var(--spacing-mini);

        &--full-width {
            grid-column: 1 / -1;
        }
    }

    .overview-label {
        @include flex-start;
        color: var(--text-secondary);
        font-size: var(--font-size-small);

        &--with-icon {
            display: inline-flex;
            align-items: center;
            gap: var(--spacing-small);
        }
    }

    .overview-label__icon {
        margin-right: var(--spacing-small);
        color: var(--info-color);
        width: var(--font-size-base);
        @include flex-center;
    }

    .overview-value {
        color: var(--text-primary);
        font-size: var(--font-size-base);
        font-weight: var(--font-weight-medium);
        padding-left: calc(var(--font-size-base) + var(--spacing-small));

        .el-tag {
            font-weight: var(--font-weight-bold);
        }
    }
}

/* 响应式样式 */
@include respond-to(lg) {
    .record-management {
        &__search-section {
            padding: var(--spacing-medium);
        }

        &__table {
            margin: 0 -#{var(--spacing-small)};
        }
    }
}

@include respond-to(md) {
    .record-management {
        padding: var(--spacing-medium);

        &__table {
            border-radius: var(--border-radius-base);
        }
    }
}

@include respond-to(sm) {
    .record-management {
        padding: var(--spacing-base);

        &__search-section {
            padding: var(--spacing-medium);
        }

        &__table {
            border-radius: var(--border-radius-base);
        }
    }

    .resolve-dialog__overview .overview-grid {
        grid-template-columns: 1fr;
    }
}

/* 解除表单相关样式 */
.record-management__resolve-form {
    :deep(.el-form-item) {
        margin-bottom: var(--spacing-base);

        .el-form-item__label {
            color: var(--text-primary);
            font-weight: var(--font-weight-bold);
            line-height: normal !important;
            padding-bottom: var(--spacing-small) !important;
        }

        .el-form-item__content {
            line-height: normal;
        }
    }
}

.resolve-form__time-display {
    background: var(--bg-secondary);
    border-radius: var(--border-radius-base);
    padding: var(--spacing-medium) var(--spacing-base);
    border: 1px solid var(--border-color);
}

.resolve-form__time-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.resolve-form__time-label {
    color: var(--text-secondary);
    font-size: var(--font-size-base);
}

.resolve-form__time-value {
    display: inline-flex;
    align-items: center;
    gap: var(--spacing-small);
    color: var(--primary-color);
    font-size: var(--font-size-large);
    font-weight: var(--font-weight-bold);
    font-family: var(--font-family-number);
}

.resolve-form__time-note {
    margin-top: var(--spacing-mini);
    color: var(--text-tertiary);
    font-size: var(--font-size-extra-small);
    text-align: right;
}

.resolve-form__confirmation {
    border: 1px solid var(--warning-light-color);
    background-color: var(--warning-bg-color);
    border-radius: var(--border-radius-base);
    padding: var(--spacing-medium) var(--spacing-base);
    margin-top: var(--spacing-small);

    :deep(.el-checkbox) {
        --el-checkbox-checked-text-color: var(--warning-dark-color);
        --el-checkbox-checked-input-border-color: var(--warning-dark-color);
        --el-checkbox-checked-bg-color: var(--warning-dark-color);

        .el-checkbox__label {
            font-weight: var(--font-weight-bold);
        }
    }
}

.resolve-form__note {
    display: flex;
    align-items: flex-start;
    color: var(--text-secondary);
    font-size: var(--font-size-small);
    margin-top: var(--spacing-medium);
    padding-left: var(--spacing-small);
}

.resolve-form__note-icon {
    margin-right: var(--spacing-small);
    color: var(--info-color);
}

@include respond-to(xs) {
    .record-management {
        padding: var(--spacing-small);

        &__table {
            border-radius: 0;
            margin: 0 -#{var(--spacing-small)};
        }
    }
}
</style>