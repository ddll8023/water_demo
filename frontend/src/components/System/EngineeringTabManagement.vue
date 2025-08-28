<template>
    <div class="engineering-tab-management">
        <!-- 操作栏 -->
        <div class="operation-bar">
            <div class="operation-left">
                <CustomButton type="primary" icon="fa-save" @click="handleBatchSave" class="save-sort-btn">
                    保存排序
                </CustomButton>
            </div>
        </div>

        <!-- Tab配置列表 -->
        <div class="tab-list-container">
            <CustomCard shadow="hover" bordered>
                <template #header>
                    <span class="card-title">
                        <i class="fa fa-list"></i>
                        Tab配置列表
                    </span>
                </template>
                <template #extra>
                    <span class="total-count">共 {{ total }} 个配置</span>
                </template>

                <!-- 拖拽排序列表 -->
                <draggable v-model="tabList" handle=".drag-handle" @change="handleSortChange" item-key="id">
                    <template #item="{ element }">
                        <div class="tab-item" :class="{ disabled: element.isVisible !== '1' }">
                            <div class="tab-content">
                                <!-- 拖拽手柄 -->
                                <div class="drag-handle">
                                    <i class="fa fa-bars"></i>
                                </div>

                                <!-- Tab图标和信息 -->
                                <div class="tab-info">
                                    <div class="tab-icon">
                                        <i class="fa" :class="element.tabIcon"></i>
                                    </div>
                                    <div class="tab-details">
                                        <div class="tab-name">{{ element.tabName }}</div>
                                        <div class="tab-key">{{ element.tabKey }}</div>
                                    </div>
                                </div>

                                <!-- 排序顺序 -->
                                <div class="tab-order">
                                    <span class="order-label">排序:</span>
                                    <span class="order-value">{{ element.sortOrder }}</span>
                                </div>

                                <!-- 显示状态 -->
                                <div class="tab-status">
                                    <CustomSwitch v-model="element.isVisible" active-value="1" inactive-value="0"
                                        @change="handleVisibilityChange(element)" />
                                </div>

                                <!-- 操作按钮 -->
                                <div class="tab-actions">
                                    <CustomButton type="primary" size="small" icon="fa-edit"
                                        @click="handleEdit(element)">
                                        编辑
                                    </CustomButton>
                                    <CustomButton type="danger" size="small" icon="fa-trash"
                                        @click="handleDelete(element)">
                                        删除
                                    </CustomButton>
                                </div>
                            </div>
                        </div>
                    </template>
                </draggable>

                <!-- 空状态 -->
                <div v-if="tabList.length === 0" class="empty-state">
                    <i class="fa fa-inbox"></i>
                    <p>暂无Tab配置数据</p>
                </div>
            </CustomCard>
        </div>

        <!-- 分页组件 -->
        <div class="pagination-container">
            <CustomPagination v-model:current-page="queryForm.page" v-model:page-size="queryForm.size" :total="total"
                @change="loadTabList" />
        </div>

        <!-- Tab配置对话框 -->
        <CustomDialog v-model="dialogVisible" :title="dialogTitle" width="600px" @confirm="handleSubmit"
            @cancel="handleCancel">
            <CommonForm ref="tabFormRef" v-model="tabForm" :items="tabFormFields" :rules="tabFormRules"
                label-width="120px" :show-actions="false" />
        </CustomDialog>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import draggable from 'vuedraggable'
import CustomButton from '@/components/Common/CustomButton.vue'
import CustomSwitch from '@/components/Common/CustomSwitch.vue'
import CustomDialog from '@/components/Common/CustomDialog.vue'
import CustomPagination from '@/components/Common/CustomPagination.vue'
import CustomCard from '@/components/Common/CustomCard.vue'
import CommonForm from '@/components/Common/CommonForm.vue'
import {
    getEngineeringServiceTabList,
    createEngineeringServiceTab,
    updateEngineeringServiceTab,
    deleteEngineeringServiceTab,
    batchUpdateTabSortOrder,
    updateTabVisibility
} from '@/api/engineering-service-tab'

// ================================
// 响应式数据
// ================================
const loading = ref(false)
const total = ref(0)
const hasChanges = ref(false)
const tabList = ref([])
const originalSortOrder = ref([])

// 查询表单
const queryForm = reactive({
    page: 1,
    size: 10
})

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('create')
const currentTabId = ref(null)

const dialogTitle = computed(() => {
    return '编辑Tab配置'
})

// Tab表单
const tabForm = reactive({
    tabName: '',
    tabIcon: ''
})

const tabFormRef = ref(null)

// Tab表单字段配置
const tabFormFields = [
    {
        prop: 'tabName',
        label: 'Tab名称',
        type: 'input',
        required: true,
        placeholder: '请输入Tab显示名称',
        span: 24
    },
    {
        prop: 'tabIcon',
        label: 'Tab图标',
        type: 'input',
        required: true,
        placeholder: '请输入图标类名，如 fa-map-marker',
        span: 24,
        disabled: true
    }
]

// 表单验证规则
const tabFormRules = {
    tabName: [
        { required: true, message: 'Tab名称不能为空', trigger: 'blur' },
        { min: 1, max: 100, message: 'Tab名称长度在1-100个字符', trigger: 'blur' }
    ],
    tabIcon: [
        { required: true, message: 'Tab图标不能为空', trigger: 'blur' }
    ]
}

// ================================
// 生命周期
// ================================
onMounted(() => {
    loadTabList()
})

// ================================
// 方法定义
// ================================

/**
 * 加载Tab配置列表
 */
const loadTabList = async () => {
    try {
        loading.value = true
        const response = await getEngineeringServiceTabList(queryForm)

        // 响应拦截器已经处理了成功响应，直接使用response即为PageResult数据
        tabList.value = response.items || []
        total.value = response.total || 0

        // 保存原始排序顺序用于检测变化
        originalSortOrder.value = tabList.value.map((item, index) => ({
            id: item.id,
            sortOrder: index + 1
        }))
        hasChanges.value = false
    } catch (error) {
        console.error('加载Tab配置列表失败:', error)
        ElMessage.error('加载Tab配置列表失败')
    } finally {
        loading.value = false
    }
}



/**
 * 新增Tab配置
 */
const handleCreate = () => {
    dialogType.value = 'create'
    currentTabId.value = null
    resetTabForm()
    dialogVisible.value = true
}

/**
 * 编辑Tab配置
 */
const handleEdit = (tab) => {
    dialogType.value = 'edit'
    currentTabId.value = tab.id
    Object.assign(tabForm, {
        tabName: tab.tabName,
        tabIcon: tab.tabIcon
    })
    dialogVisible.value = true
}

/**
 * 删除Tab配置
 */
const handleDelete = async (tab) => {
    try {
        await ElMessageBox.confirm(
            `确定要删除Tab配置"${tab.tabName}"吗？删除后将不能恢复。`,
            '确认删除',
            {
                confirmButtonText: '确定删除',
                cancelButtonText: '取消',
                type: 'warning'
            }
        )

        await deleteEngineeringServiceTab(tab.id)
        ElMessage.success('Tab配置删除成功')
        loadTabList()

    } catch (error) {
        if (error !== 'cancel') {
            console.error('删除Tab配置失败:', error)
            ElMessage.error('删除Tab配置失败')
        }
    }
}

/**
 * 排序变化处理
 */
const handleSortChange = () => {
    // 更新排序顺序
    tabList.value.forEach((item, index) => {
        item.sortOrder = index + 1
    })

    // 检查是否有变化
    const currentOrder = tabList.value.map((item, index) => ({
        id: item.id,
        sortOrder: index + 1
    }))

    hasChanges.value = JSON.stringify(currentOrder) !== JSON.stringify(originalSortOrder.value)
}

/**
 * 批量保存排序
 */
const handleBatchSave = async () => {
    try {
        // 检查是否有实际变化
        const currentOrder = tabList.value.map((item, index) => ({
            id: item.id,
            sortOrder: index + 1
        }))

        const hasActualChanges = JSON.stringify(currentOrder) !== JSON.stringify(originalSortOrder.value)

        if (!hasActualChanges) {
            ElMessage.info('排序未发生变化，无需保存')
            return
        }

        const updateData = tabList.value.map(item => ({
            id: item.id,
            sortOrder: item.sortOrder
        }))

        await batchUpdateTabSortOrder(updateData)
        ElMessage.success('Tab配置排序保存成功')

        // 更新原始排序顺序
        originalSortOrder.value = [...updateData]
        hasChanges.value = false

    } catch (error) {
        console.error('保存排序失败:', error)
        ElMessage.error('保存排序失败')
    }
}

/**
 * 显示状态变化处理
 */
const handleVisibilityChange = async (tab) => {
    try {
        await updateTabVisibility(tab.tabKey, tab.isVisible)
        ElMessage.success(`Tab配置${tab.isVisible === '1' ? '显示' : '隐藏'}成功`)

    } catch (error) {
        console.error('更新显示状态失败:', error)
        ElMessage.error('更新显示状态失败')
        // 回滚状态
        tab.isVisible = tab.isVisible === '1' ? '0' : '1'
    }
}

/**
 * 提交表单
 */
const handleSubmit = async () => {
    try {
        const valid = await tabFormRef.value.validate()
        if (!valid) return

        if (dialogType.value === 'create') {
            await createEngineeringServiceTab(tabForm)
            ElMessage.success('Tab配置创建成功')
        } else {
            await updateEngineeringServiceTab(currentTabId.value, tabForm)
            ElMessage.success('Tab配置更新成功')
        }

        dialogVisible.value = false
        loadTabList()

    } catch (error) {
        console.error('提交失败:', error)
        ElMessage.error('操作失败')
    }
}

/**
 * 取消对话框
 */
const handleCancel = () => {
    dialogVisible.value = false
}

/**
 * 重置表单
 */
const resetTabForm = () => {
    Object.assign(tabForm, {
        tabName: '',
        tabIcon: ''
    })
    tabFormRef.value?.clearValidate()
}
</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;

.engineering-tab-management {
    background-color: var(--bg-primary);

    .operation-bar {
        padding: var(--spacing-medium) 0;

        .operation-left {
            @include flex-start;
            gap: var(--spacing-sm);

            .save-sort-btn {
                min-width: 100px;
                font-weight: var(--font-weight-medium);
                margin-left: var(--spacing-medium);
            }
        }
    }

    .tab-list-container {

        .card-title {
            @include flex-start;
            gap: var(--spacing-xs);
            font-weight: var(--font-weight-medium);
            color: var(--text-primary);
        }

        .total-count {
            color: var(--text-secondary);
            font-size: var(--font-size-sm);
        }
    }

    .tab-item {
        border: 1px solid var(--border-light);
        border-radius: var(--border-radius-md);
        margin-bottom: var(--spacing-sm);
        background: var(--bg-primary);
        transition: all var(--transition-base);

        &:hover {
            box-shadow: var(--shadow-hover-button);
            border-color: var(--primary-color);
        }

        &.disabled {
            opacity: 0.6;
            background: var(--bg-disabled);
        }

        .tab-content {
            @include flex-start;
            align-items: center;
            padding: var(--spacing-medium);
            gap: var(--spacing-medium);

            .drag-handle {
                cursor: move;
                color: var(--text-secondary);
                padding: var(--spacing-xs);

                &:hover {
                    color: var(--primary-color);
                }
            }

            .tab-info {
                @include flex-start;
                gap: var(--spacing-sm);
                flex: 1;

                .tab-icon {
                    @include flex-center;
                    width: 40px;
                    height: 40px;
                    background: var(--primary-transparent-light);
                    border-radius: var(--border-radius-round);
                    color: var(--primary-color);
                    font-size: var(--font-size-lg);
                }

                .tab-details {
                    .tab-name {
                        font-weight: var(--font-weight-medium);
                        color: var(--text-primary);
                        margin-bottom: var(--spacing-micro);
                    }

                    .tab-key {
                        font-size: var(--font-size-sm);
                        color: var(--text-secondary);
                        font-family: var(--font-family-number);
                    }
                }
            }

            .tab-order {
                @include flex-start;
                gap: var(--spacing-xs);

                .order-label {
                    color: var(--text-secondary);
                    font-size: var(--font-size-sm);
                }

                .order-value {
                    color: var(--primary-color);
                    font-weight: var(--font-weight-medium);
                    background: var(--primary-transparent-light);
                    padding: 2px 8px;
                    border-radius: var(--border-radius-small);
                }
            }

            .tab-status {
                @include flex-center;
                min-width: 80px;
            }

            .tab-actions {
                @include flex-start;
                gap: var(--spacing-xs);
            }
        }
    }

    .empty-state {
        @include flex-center;
        flex-direction: column;
        padding: var(--spacing-extra-large);
        color: var(--text-secondary);

        .fa {
            font-size: var(--font-size-xxl);
            margin-bottom: var(--spacing-medium);
            opacity: 0.6;
        }

        p {
            font-size: var(--font-size-base);
            margin: 0;
        }
    }

    .pagination-container {
        @include flex-center;
        margin: var(--spacing-large) 0;
    }

    @include respond-to(md) {
        .tab-item .tab-content {
            flex-wrap: wrap;
            gap: var(--spacing-sm);
        }
    }

    @include respond-to(sm) {
        .operation-bar {
            flex-direction: column;
            gap: var(--spacing-sm);
            align-items: stretch;
        }

        .tab-item .tab-content {
            padding: var(--spacing-sm);

            .tab-info {
                flex: none;
                width: 100%;
            }

            .tab-actions {
                width: 100%;
                justify-content: flex-end;
            }
        }
    }
}
</style>