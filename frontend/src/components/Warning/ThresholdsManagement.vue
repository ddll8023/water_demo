<template>
    <div class="threshold-management" :class="{ 'threshold-management--loading': loading }">
        <!-- 搜索区域 -->
        <div class="threshold-management__search">
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
        <div class="threshold-management__table" :class="{ 'threshold-management__table--loading': loading }">
            <CommonTable :data="dataList" :columns="enhancedTableColumns" :loading="loading" :show-index="true"
                :show-actions="false" :show-pagination="false" :show-toolbar="false">

                <!-- 监测项列 -->
                <template #monitoringItem="{ row }">
                    <span>{{ getMonitoringItemLabel(row.monitoringItem) }}</span>
                </template>

                <!-- 操作列 -->
                <template #actions="{ row }">
                    <div class="threshold-management__actions">
                        <CustomButton type="text" text-type="primary" size="small" @click="handleEdit(row)"
                            v-permission="'business:operate'">
                            编辑
                        </CustomButton>
                        <CustomButton type="text" text-type="danger" size="small" @click="handleDelete(row)"
                            v-permission="'business:operate'">
                            删除
                        </CustomButton>
                    </div>
                </template>
            </CommonTable>

            <!-- 分页区域 -->
            <CustomPagination class="custom-pagination" :current-page="pagination.currentPage"
                :page-size="pagination.pageSize" :total="pagination.total" :page-sizes="[10, 20, 50, 100]"
                @size-change="handleSizeChange" @current-change="handleCurrentChange" />
        </div>

        <!-- 编辑对话框 -->
        <CustomDialog v-model:visible="dialogVisible" :title="isEdit ? '编辑预警指标' : '新增预警指标'"
            width="var(--panel-height-default)" :close-on-click-modal="false" :loading="submitLoading"
            @close="handleDialogClose" @cancel="handleDialogClose" @confirm="handleDialogSubmit">
            <CommonForm ref="dialogFormRef" v-model="dialogFormData" :items="formItems" :rules="formRules"
                label-width="120px" :show-actions="false">
                <!-- 监测项选择器插槽 -->
                <template #monitoringItem="{ item, formData, disabled }">
                    <CustomSelect v-model="formData.monitoringItem" :options="monitoringItemOptions" value-key="value"
                        label-key="label" placeholder="请选择监测项" :disabled="disabled" clearable />
                </template>


            </CommonForm>

            <!-- 阈值关系提示 -->
            <div class="threshold-management__tips">
                <el-alert title="阈值设置说明" type="info" :closable="false" show-icon>
                    <template #default>
                        <div class="threshold-management__tips-content">
                            <p>• 阈值关系：下下限 ≤ 下限 ≤ 上限 ≤ 上上限</p>
                            <p>• 超出上上限或低于下下限时触发一级预警</p>
                            <p>• 超出上限或低于下限时触发二级预警</p>
                            <p>• 至少需要设置一个阈值才能生效</p>
                        </div>
                    </template>
                </el-alert>
            </div>
        </CustomDialog>
    </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick, watch } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import CommonSearch from '@/components/Common/CommonSearch.vue';
import CommonTable from '@/components/Common/CommonTable.vue';
import CustomButton from '@/components/Common/CustomButton.vue';
import CustomSelect from '@/components/Common/CustomSelect.vue';
import CommonForm from '@/components/Common/CommonForm.vue';
import CustomDialog from '@/components/Common/CustomDialog.vue';
import CustomPagination from '@/components/Common/CustomPagination.vue';

import {
    getThresholdList,
    getThresholdDetail,
    createThreshold,
    updateThreshold,
    deleteThreshold,
    checkThresholdDuplicate,
} from "@/api/warning/thresholds";
import { useDictionary, DICT_TYPES } from "@/composables/useDictionary";

/**
 * ----------------------------------------
 * 组合式函数和工具
 * ----------------------------------------
 */
const { getDictData } = useDictionary();

/**
 * ----------------------------------------
 * 响应式状态管理
 * ----------------------------------------
 */
// 页面加载状态
const loading = ref(false);
const submitLoading = ref(false);

// 数据列表和选择状态
const dataList = ref([]);

// 对话框状态
const dialogVisible = ref(false);
const isEdit = ref(false);
const currentRow = ref({});
const dialogFormRef = ref(null);

// 分页信息
const pagination = reactive({
    currentPage: 1,
    pageSize: 10,
    total: 0,
});

// 搜索表单 - 只包含测点名称和监测项搜索
const searchForm = reactive({
    stationName: "",
    monitoringItem: "",
});

// 表单数据
const dialogFormData = reactive({
    id: null,
    stationName: "",
    monitoringItem: "",
    upperUpperLimit: null,
    upperLimit: null,
    lowerLimit: null,
    lowerLowerLimit: null,
    unit: "",
});

// 监测项选项列表
const monitoringItemOptions = ref([]);

/**
 * ----------------------------------------
 * 计算属性配置
 * ----------------------------------------
 */

/**
 * 增强的表格列配置
 */
const enhancedTableColumns = computed(() => [
    { prop: 'stationName', label: '测点名称', minWidth: 120 },
    {
        prop: 'monitoringItem',
        label: '监测项',
        width: 100,
        align: 'center',
        slot: 'monitoringItem',
    },
    { prop: 'upperUpperLimit', label: '上上限', width: 80, align: 'center' },
    { prop: 'upperLimit', label: '上限', width: 80, align: 'center' },
    { prop: 'lowerLimit', label: '下限', width: 80, align: 'center' },
    { prop: 'lowerLowerLimit', label: '下下限', width: 80, align: 'center' },
    { prop: 'unit', label: '单位', width: 80, align: 'center' },
    { prop: 'actions', label: '操作', width: 160, align: 'center', slot: 'actions' },
]);

/**
 * 增强的搜索字段配置 - 测点名称输入框搜索和监测项下拉选择搜索
 */
const enhancedSearchFields = computed(() => [
    {
        type: 'input',
        prop: 'stationName',
        label: '测点名称',
        placeholder: '请输入测点名称',
        labelWidth: 'var(--form-label-width-standard)',
        span: 4,
    },
    {
        type: 'select',
        prop: 'monitoringItem',
        label: '监测项',
        placeholder: '请选择监测项',
        labelWidth: 'var(--form-label-width-search)',
        options: monitoringItemOptions.value,
        span: 4,
    },
]);

/**
 * 表单验证规则
 */
const formRules = computed(() => ({
    stationName: [
        { required: true, message: '请输入测点名称', trigger: 'blur' },
        { max: 255, message: '测点名称长度不能超过255个字符', trigger: 'blur' },
    ],
    monitoringItem: [
        { required: true, message: '请选择监测项', trigger: 'change' },
    ],
    upperUpperLimit: [
        {
            validator: (_, value, callback) => {
                if (value !== null && value !== undefined) {
                    if (dialogFormData.upperLimit !== null && value < dialogFormData.upperLimit) {
                        callback(new Error('上上限不能小于上限'))
                    }
                }
                callback()
            },
            trigger: 'blur',
        },
    ],
    upperLimit: [
        {
            validator: (_, value, callback) => {
                if (value !== null && value !== undefined) {
                    if (dialogFormData.upperUpperLimit !== null && value > dialogFormData.upperUpperLimit) {
                        callback(new Error('上限不能大于上上限'))
                    }
                    if (dialogFormData.lowerLimit !== null && value < dialogFormData.lowerLimit) {
                        callback(new Error('上限不能小于下限'))
                    }
                }
                callback()
            },
            trigger: 'blur',
        },
    ],
    lowerLimit: [
        {
            validator: (_, value, callback) => {
                if (value !== null && value !== undefined) {
                    if (dialogFormData.upperLimit !== null && value > dialogFormData.upperLimit) {
                        callback(new Error('下限不能大于上限'))
                    }
                    if (dialogFormData.lowerLowerLimit !== null && value < dialogFormData.lowerLowerLimit) {
                        callback(new Error('下限不能小于下下限'))
                    }
                }
                callback()
            },
            trigger: 'blur',
        },
    ],
    lowerLowerLimit: [
        {
            validator: (_, value, callback) => {
                if (value !== null && value !== undefined) {
                    if (dialogFormData.lowerLimit !== null && value > dialogFormData.lowerLimit) {
                        callback(new Error('下下限不能大于下限'))
                    }
                }
                callback()
            },
            trigger: 'blur',
        },
    ],
    unit: [
        { max: 20, message: '单位长度不能超过20个字符', trigger: 'blur' },
    ],
}))

/**
 * 表单项配置
 */
const formItems = computed(() => [
    {
        type: 'input',
        prop: 'stationName',
        label: '测点名称',
        placeholder: '请输入测点名称',
        maxlength: 255,
        showWordLimit: true,
        clearable: true,
        span: 24,
        required: true
    },
    {
        type: 'slot',
        prop: 'monitoringItem',
        label: '监测项',
        span: 12,
        required: true
    },
    {
        type: 'input',
        prop: 'unit',
        label: '单位',
        placeholder: '请输入单位',
        maxlength: 20,
        showWordLimit: true,
        clearable: true,
        span: 12
    },
    {
        type: 'input-number',
        prop: 'upperUpperLimit',
        label: '上上限',
        placeholder: '请输入上上限',
        precision: 2,
        step: 0.01,
        controlsPosition: 'right',
        span: 12,
        defaultValue: null
    },
    {
        type: 'input-number',
        prop: 'upperLimit',
        label: '上限',
        placeholder: '请输入上限',
        precision: 2,
        step: 0.01,
        controlsPosition: 'right',
        span: 12,
        defaultValue: null
    },
    {
        type: 'input-number',
        prop: 'lowerLimit',
        label: '下限',
        placeholder: '请输入下限',
        precision: 2,
        step: 0.01,
        controlsPosition: 'right',
        span: 12,
        defaultValue: null
    },
    {
        type: 'input-number',
        prop: 'lowerLowerLimit',
        label: '下下限',
        placeholder: '请输入下下限',
        precision: 2,
        step: 0.01,
        controlsPosition: 'right',
        span: 12,
        defaultValue: null
    }
]);

/**
 * ----------------------------------------
 * 数据获取和处理方法
 * ----------------------------------------
 */

/**
 * 获取预警指标列表
 */
const fetchData = async () => {
    loading.value = true;
    try {
        const params = {
            page: pagination.currentPage,
            size: pagination.pageSize,
            ...searchForm,
        };

        const response = await getThresholdList(params);

        if (response) {
            dataList.value = response.items || [];
            pagination.total = response.total || 0;
        }
    } catch (err) {
        ElMessage.error(err.message || "获取数据失败");
    } finally {
        loading.value = false;
    }
};

/**
 * 获取预警指标详情
 */
const fetchDetail = async (id) => {
    try {
        const response = await getThresholdDetail(id);
        return response;
    } catch (err) {
        ElMessage.error("获取详情失败");
        throw err;
    }
};

/**
 * 获取监测项选项列表
 */
const fetchMonitoringItemOptions = async () => {
    try {
        const monitoringItems = await getDictData(DICT_TYPES.MONITORING_ITEM);
        monitoringItemOptions.value = monitoringItems.map(item => ({
            label: item.label,
            value: item.value
        }));
    } catch (error) {
        console.error('获取监测项选项失败:', error);
    }
};

/**
 * 同步获取监测项标签（用于删除确认对话框等场景）
 */
const getMonitoringItemLabel = (value) => {
    if (!value || !monitoringItemOptions.value.length) {
        return value || '';
    }

    const item = monitoringItemOptions.value.find(option =>
        String(option.value) === String(value)
    );

    return item ? item.label : value;
};

/**
 * ----------------------------------------
 * 搜索和筛选操作
 * ----------------------------------------
 */

/**
 * 搜索处理
 */
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

/**
 * 重置搜索
 */
const handleResetSearch = async () => {
    searchForm.stationName = "";
    searchForm.monitoringItem = "";
    await handleSearch();
};

/**
 * 对话框关闭处理
 */
const handleDialogClose = () => {
    dialogVisible.value = false;

    // 清除验证状态
    nextTick(() => {
        dialogFormRef.value?.clearValidate();
        resetFormData();
    });
};

/**
 * 对话框提交处理
 */
const handleDialogSubmit = async () => {
    if (!dialogFormRef.value) return;

    try {
        const isValid = await dialogFormRef.value.validate();
        if (!isValid) return;

        // 获取表单的当前数据
        const currentFormData = dialogFormRef.value.formData || dialogFormData;

        // 在编辑模式下，确保ID存在且有效
        if (isEdit.value) {
            if (!currentFormData.id || currentFormData.id === null) {
                ElMessage.error('编辑数据异常，请重新打开编辑对话框');
                return;
            }
        }

        // 检查是否至少设置了一个阈值
        const hasThreshold = [
            currentFormData.upperUpperLimit,
            currentFormData.upperLimit,
            currentFormData.lowerLimit,
            currentFormData.lowerLowerLimit,
        ].some(value => {
            // 检查是否为有效数字（包括0）
            return value !== null &&
                value !== undefined &&
                value !== '' &&
                typeof value === 'number' &&
                !isNaN(value);
        });

        if (!hasThreshold) {
            ElMessage.warning('请至少设置一个阈值');
            return;
        }

        // 使用当前最新数据进行提交
        await handleSubmit({ ...currentFormData });

    } catch (error) {
        console.error('表单验证失败:', error);
    }
};

/**
 * ----------------------------------------
 * 数据操作方法
 * ----------------------------------------
 */

/**
 * 新增处理
 */
const handleAdd = () => {
    isEdit.value = false;
    resetFormData();
    currentRow.value = {};
    dialogVisible.value = true;

    // 确保在对话框打开后重置表单验证状态
    nextTick(() => {
        dialogFormRef.value?.clearValidate();
    });
};

/**
 * 编辑处理
 */
const handleEdit = async (row) => {
    isEdit.value = true;
    try {
        const detail = await fetchDetail(row.id);

        // 重置表单数据，然后赋值详情数据
        resetFormData();
        Object.assign(dialogFormData, detail);

        currentRow.value = { ...row };
        dialogVisible.value = true;

        // 确保在对话框打开后重置表单验证状态
        nextTick(() => {
            dialogFormRef.value?.clearValidate();
        });
    } catch (err) {
        // 错误已在 fetchDetail 中处理
    }
};

/**
 * 删除处理
 */
const handleDelete = async (row) => {
    try {
        const monitoringItemName = getMonitoringItemLabel(row.monitoringItem);

        await ElMessageBox.confirm(
            `确定要删除测点"${row.stationName}"的"${monitoringItemName}"预警指标吗？`,
            "删除确认",
            {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning",
            }
        );

        await deleteThreshold(row.id);
        ElMessage.success("删除成功");
        fetchData();
    } catch (err) {
        if (err !== "cancel") {
            ElMessage.error("删除失败");
        }
    }
};

/**
 * 表单提交处理
 */
const handleSubmit = async (submitData) => {
    submitLoading.value = true;
    try {
        // 检查重复
        const isDuplicate = await checkThresholdDuplicate(
            submitData.stationName,
            submitData.monitoringItem,
            isEdit.value ? submitData.id : null
        );

        if (isDuplicate.data) {
            ElMessage.error("该测点的监测项已存在预警指标配置");
            return;
        }

        if (isEdit.value) {
            if (!submitData.id || submitData.id === null) {
                ElMessage.error('更新失败：缺少有效的记录ID');
                return;
            }
            await updateThreshold(submitData.id, submitData);
            ElMessage.success("更新成功");
        } else {
            await createThreshold(submitData);
            ElMessage.success("创建成功");
        }

        dialogVisible.value = false;
        resetFormData();
        await fetchData();
    } catch (err) {
        if (err.message) {
            ElMessage.error(err.message);
        } else {
            ElMessage.error(isEdit.value ? "更新失败" : "创建失败");
        }
    } finally {
        submitLoading.value = false;
    }
};

/**
 * ----------------------------------------
 * 表格和分页事件处理
 * ----------------------------------------
 */

/**
 * 分页大小变化处理
 */
const handleSizeChange = (size) => {
    pagination.pageSize = size;
    pagination.currentPage = 1;
    fetchData();
};

/**
 * 当前页变化处理
 */
const handleCurrentChange = (page) => {
    pagination.currentPage = page;
    fetchData();
};

/**
 * ----------------------------------------
 * 辅助工具方法
 * ----------------------------------------
 */

/**
 * 重置表单数据
 */
const resetFormData = () => {
    Object.assign(dialogFormData, {
        id: null,
        stationName: "",
        monitoringItem: "",
        upperUpperLimit: null,
        upperLimit: null,
        lowerLimit: null,
        lowerLowerLimit: null,
        unit: "",
    });
};

/**
 * 初始化搜索字段选项
 */
const initSearchOptions = async () => {
    try {
        // 获取监测项选项
        await fetchMonitoringItemOptions();
    } catch (error) {
        console.error('初始化搜索选项失败:', error);
    }
};

/**
 * ----------------------------------------
 * 数据监听器
 * ----------------------------------------
 */

/**
 * 监听对话框显示状态，确保表单在显示时正确处理
 */
watch(
    () => dialogVisible.value,
    (visible) => {
        if (visible) {
            nextTick(() => {
                dialogFormRef.value?.clearValidate();
            });
        }
    }
);

/**
 * ----------------------------------------
 * 生命周期钩子和初始化
 * ----------------------------------------
 */

/**
 * 组件挂载后执行初始化
 */
onMounted(async () => {
    await initSearchOptions();
    await fetchData();
});
</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;

.threshold-management {
    padding: var(--spacing-large);
    background: var(--bg-primary);
    min-height: calc(100vh - var(--page-min-height-offset));

    &__search {
        margin-bottom: var(--spacing-large);

        .fa {
            margin-right: var(--spacing-small);
        }
    }

    // 加载状态修饰符
    &--loading {
        pointer-events: none;
        opacity: var(--opacity-high);
    }

    // 表格容器
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

    // 操作按钮容器
    &__actions {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: var(--spacing-small);
    }

    // 提示区容器
    &__tips {
        margin-top: var(--spacing-large);
    }

    // 提示内容文本
    &__tips-content {
        line-height: var(--line-height-large);
        font-size: var(--font-size-small);

        p {
            margin: var(--spacing-mini) 0;
        }
    }

    // 表单组件定制
    :deep(.el-form-item__label) {
        font-weight: var(--font-weight-medium);
    }

    :deep(.el-input-number) {
        width: 100%;
    }

    // 警告提示组件定制
    :deep(.el-alert__content) {
        padding-right: 0;
    }
}

// 响应式适配
.threshold-management {

    // 大屏幕适配
    @include respond-to(lg) {
        &__search {
            padding: var(--spacing-medium);
        }

        &__table {
            margin: 0 -#{var(--spacing-small)};
        }
    }

    // 中等屏幕适配
    @include respond-to(md) {
        padding: var(--spacing-medium);

        &__table {
            border-radius: var(--border-radius-base);
        }
    }

    // 小屏幕适配
    @include respond-to(sm) {
        padding: var(--spacing-base);

        &__actions {
            flex-direction: column;
            gap: var(--spacing-mini);
        }

        &__tips-content {
            line-height: var(--line-height-base);
        }
    }

    // 超小屏幕适配
    @include respond-to(xs) {
        padding: var(--spacing-small);

        &__table {
            border-radius: 0;
            margin: 0 -#{var(--spacing-small)};
        }
    }
}
</style>