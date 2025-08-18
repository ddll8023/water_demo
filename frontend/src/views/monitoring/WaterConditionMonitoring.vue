<template>
    <div class="water-condition-monitoring">
        <!-- 使用页面头部组件 -->
        <PageHeader title="水情监测" icon="fa-database" description="查询和管理水情水位、蓄水量、超汛限、入库流量、出库流量等监测数据" />

        <!-- 筛选区域 -->
        <div class="filter-section">
            <CommonSearch v-model="searchForm" :items="searchFields" :single-row="true" @search="handleSearch"
                @reset="handleReset">
                <template #actions>
                    <!-- 时间范围快捷选择按钮组 -->
                    <div class="time-range-buttons">
                        <CustomButton :type="activeTimeRange === '7days' ? 'primary' : 'secondary'" size="small"
                            @click="handleTimeRangeChange('7days')">
                            7天
                        </CustomButton>
                        <CustomButton :type="activeTimeRange === '30days' ? 'primary' : 'secondary'" size="small"
                            @click="handleTimeRangeChange('30days')">
                            30天
                        </CustomButton>
                        <CustomButton :type="activeTimeRange === 'all' ? 'primary' : 'secondary'" size="small"
                            @click="handleTimeRangeChange('all')">
                            全部
                        </CustomButton>
                    </div>
                    <CustomButton type="secondary" @click="showImportDialog = true" v-permission="'business:operate'">
                        <i class="fa fa-upload"></i>
                        导入数据
                    </CustomButton>
                    <CustomButton type="primary" @click="handleExport" :loading="exportLoading">
                        <i class="fa fa-download"></i>
                        导出数据
                    </CustomButton>
                </template>
            </CommonSearch>
        </div>

        <!-- 图表轮播区域 -->
        <div class="chart-carousel-section">
            <MonitoringChart :chart-data="chartData" :chart-items="waterConditionMonitoringItems"
                :active-index="currentChartIndex" :loading="chartLoading" :has-searched="hasSearched"
                :has-station="!!searchForm.stationId && chartSearchTriggered" title="水情监测数据"
                @update:active-index="currentChartIndex = $event" @chart-initialized="handleChartInitialized" />
        </div>

        <!-- 数据表格区域 -->
        <div class="table-section">
            <div class="table-content">
                <CommonTable :data="tableData" :columns="tableColumns" :loading="tableLoading" :show-selection="false"
                    :show-index="true" :show-actions="false" />

                <!-- 使用自定义分页组件 -->
                <CustomPagination :current-page="pagination.currentPage" :page-size="pagination.pageSize"
                    :total="pagination.total" :page-sizes="[10, 20, 50, 100]" @size-change="handleSizeChange"
                    @current-change="handleCurrentChange" />
            </div>
        </div>

        <!-- Excel导入组件 -->
        <ExcelImporter v-model:visible="showImportDialog" import-type="water-condition"
            :template-columns="waterConditionImportColumns" @import-success="handleImportSuccess" />
    </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import CommonSearch from '@/components/Common/CommonSearch.vue'
import CommonTable from '@/components/Common/CommonTable.vue'
import CustomButton from '@/components/Common/CustomButton.vue'
import ExcelImporter from '@/components/Common/ExcelImporter.vue'
import PageHeader from '@/components/Common/PageHeader.vue'
import MonitoringChart from '@/components/Chart/MonitoringChart.vue'
import CustomPagination from '@/components/Common/CustomPagination.vue'
import {
    getWaterConditionMonitoringData,
    getAvailableMonitoringStations,
    getWaterConditionChartData,
    exportWaterConditionData
} from '@/api/monitoring'
import { formatLocalTimeForAPI, formatDateTime } from '@/utils/shared/common'

// ===================================
// 常量定义
// ===================================

// 水情监测项目配置
const waterConditionMonitoringItems = [
    {
        code: 'WATER_LEVEL',
        name: '水位',
        unit: 'm',
        color: '#409EFF',
        icon: 'fa-tint',
        description: '水位监测数据'
    },
    {
        code: 'STORAGE_CAPACITY',
        name: '蓄水量',
        unit: '10⁴m³',
        color: '#67C23A',
        icon: 'fa-database',
        description: '蓄水量监测数据'
    },
    {
        code: 'INFLOW',
        name: '入库流量',
        unit: 'm³/s',
        color: '#E6A23C',
        icon: 'fa-sign-in',
        description: '入库流量监测数据'
    },
    {
        code: 'OUTFLOW',
        name: '出库流量',
        unit: 'm³/s',
        color: '#F56C6C',
        icon: 'fa-sign-out',
        description: '出库流量监测数据'
    }
]

// 水情导入列配置
const waterConditionImportColumns = [
    { prop: 'rowNumber', label: '序号', width: 80 },
    { prop: 'monitoringTime', label: '时间', width: 180 },
    { prop: 'waterLevel', label: '水位(m)', width: 120 },
    { prop: 'storageCapacity', label: '蓄水量(10⁴m³)', width: 140 },
    { prop: 'floodLimitDiff', label: '超汛限(m)', width: 120 },
    { prop: 'inflow', label: '入库流量(m³/s)', width: 140 },
    { prop: 'outflow', label: '出库流量(m³/s)', width: 140 }
]

// 表格列配置
const tableColumns = [
    {
        prop: 'monitoringTime',
        label: '时间',
        width: 180,
        sortable: true,
        formatter: (row) => formatDateTime(row.monitoringTime)
    },
    {
        prop: 'waterLevel',
        label: '水位',
        width: 120,
        formatter: (row) => {
            const value = row.waterLevel
            return value !== null && value !== undefined ? `${Number(value).toFixed(3)}` : '-'
        }
    },
    {
        prop: 'storageCapacity',
        label: '蓄水量',
        width: 140,
        formatter: (row) => {
            const value = row.storageCapacity
            return value !== null && value !== undefined ? `${Number(value).toFixed(2)}` : '-'
        }
    },
    {
        prop: 'floodLimitDiff',
        label: '超汛限',
        width: 120,
        formatter: (row) => {
            const value = row.floodLimitDiff
            return value !== null && value !== undefined ? `${Number(value).toFixed(3)}` : '-'
        }
    },
    {
        prop: 'inflow',
        label: '入库流量',
        width: 140,
        formatter: (row) => {
            const value = row.inflow
            return value !== null && value !== undefined ? `${Number(value).toFixed(3)}` : '-'
        }
    },
    {
        prop: 'outflow',
        label: '出库流量',
        width: 140,
        formatter: (row) => {
            const value = row.outflow
            return value !== null && value !== undefined ? `${Number(value).toFixed(3)}` : '-'
        }
    }
]

// ===================================
// 响应式数据与状态管理
// ===================================

// 搜索表单状态
const searchForm = ref({
    stationId: null,
    timeRange: []
})

// 搜索字段配置
const searchFields = ref([
    {
        prop: 'stationId',
        label: '监测站点',
        type: 'select',
        options: [],
        placeholder: '请选择监测站点',
        width: '240px',
        labelWidth: 'var(--form-label-width-standard)'
    },
    {
        prop: 'timeRange',
        label: '监测时间',
        type: 'datetimerange',
        startPlaceholder: '请选择开始时间',
        endPlaceholder: '请选择结束时间',
        showDuration: true,
        width: '405px',
        labelWidth: 'var(--form-label-width-standard)'
    }
])

// 搜索状态管理
const hasSearched = ref(false) // 是否已执行过搜索
const chartSearchTriggered = ref(false) // 是否已触发图表搜索
const activeTimeRange = ref('7days') // 当前活动的时间范围类型，默认选中7天
const quickSearchTimeRange = ref(null) // 快捷按钮搜索时使用的时间范围

// 数据状态管理
const tableData = ref([])
const stationOptions = ref([])

// 图表数据
const chartData = ref({ labels: [], values: [] })
const chartLoading = ref(false)
const currentChartIndex = ref(0)

// 加载状态管理
const tableLoading = ref(false)
const exportLoading = ref(false)
const showImportDialog = ref(false)

// 分页配置
const pagination = reactive({
    currentPage: 1,
    pageSize: 20,
    total: 0
})

// ===================================
// 监听器
// ===================================

// 监听图表类型变化
watch(() => currentChartIndex.value, (newIndex) => {
    // 如果已经搜索并选择了站点，切换图表类型时重新加载数据
    if (searchForm.value.stationId && hasSearched.value && chartSearchTriggered.value) {
        loadChartData()
    }
})

// 监听时间选择器变化，重置快捷按钮状态
watch(() => searchForm.value.timeRange, (newTimeRange) => {
    // 如果时间选择器有值且不是通过快捷按钮设置的，则重置快捷按钮状态
    if (newTimeRange && newTimeRange.length === 2 && !quickSearchTimeRange.value) {
        activeTimeRange.value = null
    }
    // 清除快捷搜索标记
    quickSearchTimeRange.value = null
}, { deep: true })

// ===================================
// 工具函数
// ===================================

// 通用的时间范围参数处理函数
const processTimeRangeParams = (timeRange) => {
    if (!timeRange || !Array.isArray(timeRange) || timeRange.length !== 2) {
        return {}
    }

    return {
        startTime: formatLocalTimeForAPI(timeRange[0]),
        endTime: formatLocalTimeForAPI(timeRange[1])
    }
}

// 计算快捷时间范围
const calculateQuickTimeRange = (timeRangeType) => {
    const now = new Date()
    let startTime, endTime

    switch (timeRangeType) {
        case '7days':
            // 最近7天
            startTime = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000)
            endTime = now
            break
        case '30days':
            // 最近30天
            startTime = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000)
            endTime = now
            break
        case 'all':
            // 全部数据，返回空数组
            return []
        default:
            return []
    }

    return [startTime, endTime]
}

// 获取当前有效的时间范围
const getCurrentTimeRange = () => {
    // 如果是快捷搜索，使用快捷搜索的时间范围
    if (quickSearchTimeRange.value) {
        return quickSearchTimeRange.value
    }
    // 否则使用时间选择器的值
    return searchForm.value.timeRange
}

// ===================================
// 数据加载函数
// ===================================

// 加载监测站点
const loadStations = async () => {
    try {
        // 获取水情监测站点
        const response = await getAvailableMonitoringStations({ monitoringItemCode: 'Z' })

        if (response && Array.isArray(response)) {
            stationOptions.value = response.map(station => ({
                label: station.name,
                value: station.id
            }))

            // 更新搜索字段的选项
            const stationField = searchFields.value.find(field => field.prop === 'stationId')
            if (stationField) {
                stationField.options = stationOptions.value
            }

            // 默认选择第一个监测站点
            if (stationOptions.value.length > 0) {
                searchForm.value.stationId = stationOptions.value[0].value
            } else {
                ElMessage.warning('没有找到可用的水情监测站点')
            }
        } else {
            ElMessage.warning('监测站点数据格式不正确')
        }
    } catch (error) {
        console.error('加载监测站点失败:', error)
        ElMessage.error(`加载监测站点失败: ${error.message || '未知错误'}`)
    }
}

// 加载表格数据
const loadTableData = async () => {
    tableLoading.value = true
    try {
        // 获取当前有效的时间范围
        const currentTimeRange = getCurrentTimeRange()
        const params = {
            page: pagination.currentPage,
            size: pagination.pageSize,
            stationId: searchForm.value.stationId,
            // 使用当前有效的时间范围参数
            ...processTimeRangeParams(currentTimeRange)
        }

        const response = await getWaterConditionMonitoringData(params)
        if (response) {
            // 直接使用API返回的数据
            tableData.value = response.items || []
            pagination.total = response.total || 0
        }
    } catch (error) {
        console.error('加载表格数据失败:', error)
        ElMessage.error('加载表格数据失败')
    } finally {
        tableLoading.value = false
    }
}

// 加载图表数据
const loadChartData = async () => {
    if (!searchForm.value.stationId || !hasSearched.value || !chartSearchTriggered.value) {
        return;
    }

    chartLoading.value = true;

    try {
        // 获取当前有效的时间范围
        const currentTimeRange = getCurrentTimeRange()
        // 构建API参数
        const item = waterConditionMonitoringItems[currentChartIndex.value];
        const params = {
            stationId: searchForm.value.stationId,
            dataType: item.code.toLowerCase(),
            // 使用当前有效的时间范围参数
            ...processTimeRangeParams(currentTimeRange)
        };

        // 调用API获取数据
        const response = await getWaterConditionChartData(params);
        if (!response) {
            chartLoading.value = false;
            return;
        }

        // 更新图表数据
        chartData.value = {
            labels: response.labels || [],
            values: response.values || []
        };
    } catch (error) {
        console.error('加载图表数据失败:', error);
        ElMessage.error('加载图表数据失败');
    } finally {
        // 重置加载状态
        chartLoading.value = false;
    }
}

// ===================================
// 事件处理函数
// ===================================

// 搜索相关事件处理
const handleSearch = () => {
    pagination.currentPage = 1
    loadTableData()

    // 标记已执行搜索
    hasSearched.value = true

    // 只有在点击搜索按钮并选择了站点时才触发图表搜索
    if (searchForm.value.stationId) {
        chartSearchTriggered.value = true
        loadChartData()
    } else {
        chartSearchTriggered.value = false
    }
}

const handleReset = () => {
    searchForm.value = {
        stationId: null,
        timeRange: []
    }
    // 重置搜索状态
    hasSearched.value = false
    chartSearchTriggered.value = false
    // 重置时间范围状态为7天激活，但不设置时间选择器值
    activeTimeRange.value = '7days'
    quickSearchTimeRange.value = null
    pagination.currentPage = 1
    loadTableData()
    // 重置图表数据
    chartData.value = { labels: [], values: [] }
}

// 处理快捷按钮搜索
const handleQuickSearch = (timeRange) => {
    // 设置快捷搜索标记，防止监听器重置按钮状态
    quickSearchTimeRange.value = timeRange

    pagination.currentPage = 1
    loadTableData()

    // 标记已执行搜索
    hasSearched.value = true

    // 只有在选择了站点时才触发图表搜索
    if (searchForm.value.stationId) {
        chartSearchTriggered.value = true
        loadChartData()
    } else {
        chartSearchTriggered.value = false
    }
}

// 时间范围切换处理
const handleTimeRangeChange = (timeRangeType) => {
    activeTimeRange.value = timeRangeType

    // 计算时间范围但不设置到时间选择器
    const timeRange = calculateQuickTimeRange(timeRangeType)

    // 自动触发快捷搜索
    handleQuickSearch(timeRange)
}

// 分页相关事件处理
const handleSizeChange = (size) => {
    pagination.pageSize = size
    pagination.currentPage = 1
    loadTableData()
}

const handleCurrentChange = (page) => {
    pagination.currentPage = page
    loadTableData()
}

// 图表事件处理
const handleChartInitialized = () => {
    // 仅在已明确触发图表搜索时加载图表数据
    if (searchForm.value.stationId && hasSearched.value && chartSearchTriggered.value) {
        loadChartData()
    }
}

// 导出数据
const handleExport = async () => {
    exportLoading.value = true
    try {
        // 获取当前有效的时间范围
        const currentTimeRange = getCurrentTimeRange()
        const params = {
            stationId: searchForm.value.stationId,
            ...processTimeRangeParams(currentTimeRange),
            format: 'excel'
        }

        const response = await exportWaterConditionData(params)

        // 根据响应类型处理
        if (response instanceof Blob) {
            // 获取文件名
            const fileName = `水情监测数据_${new Date().toISOString().slice(0, 10)}.xlsx`;

            // 创建下载链接
            const blob = new Blob([response], {
                type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
            })
            const url = window.URL.createObjectURL(blob)
            const link = document.createElement('a')
            link.href = url
            link.download = fileName
            document.body.appendChild(link)
            link.click()
            document.body.removeChild(link)
            window.URL.revokeObjectURL(url)

            ElMessage.success('数据导出成功')
        } else {
            ElMessage.error('导出失败：服务器返回格式错误')
        }
    } catch (error) {
        // 捕获并显示具体错误
        let errorMsg = '导出数据失败';
        if (error.response && error.response.data) {
            // 尝试读取服务器返回的错误信息
            const reader = new FileReader();
            reader.onload = () => {
                try {
                    const errorData = JSON.parse(reader.result);
                    errorMsg = errorData.message || errorMsg;
                    ElMessage.error(errorMsg);
                } catch (e) {
                    ElMessage.error(errorMsg);
                }
            };
            reader.onerror = () => ElMessage.error(errorMsg);
            reader.readAsText(error.response.data);
        } else {
            ElMessage.error(`${errorMsg}: ${error.message || '未知错误'}`);
        }
    } finally {
        exportLoading.value = false
    }
}

// 导入成功处理
const handleImportSuccess = () => {
    ElMessage.success('数据导入成功！')
    // 刷新表格数据
    loadTableData()
    // 刷新图表数据
    if (searchForm.value.stationId && hasSearched.value && chartSearchTriggered.value) {
        loadChartData()
    }
}

// ===================================
// 生命周期钩子
// ===================================

// 页面初始化
onMounted(async () => {
    try {
        // 加载站点数据
        await loadStations()

        // 设置默认7天时间范围并加载对应数据
        handleTimeRangeChange('7days')
    } catch (error) {
        console.error('页面初始化失败:', error)
        ElMessage.error('页面初始化失败，请刷新重试')
    }
})
</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;

// ============================================
// 页面布局样式
// ============================================
.water-condition-monitoring {

    // ============================================
    // 页面内容区域样式
    // ============================================
    .filter-section,
    .chart-carousel-section,
    .table-section {
        margin-bottom: var(--spacing-large);
    }

    // 表格区域样式
    .table-section {
        background: var(--bg-primary);
        border-radius: var(--border-radius-md);
        box-shadow: var(--shadow-light);
        border: 1px solid var(--border-color-light);
        overflow: hidden;

        .table-content {
            padding: var(--spacing-md);
            padding-bottom: 20px;
        }

        // 响应式适配
        @include respond-to(md) {
            .table-content {
                padding: var(--spacing-md);
                padding-bottom: 20px;
            }
        }

        @include respond-to(sm) {
            .table-content {
                padding: var(--spacing-sm);
                padding-bottom: 20px;
            }
        }
    }

    // 时间范围按钮组样式
    .time-range-buttons {
        @include flex-center-y;
        gap: var(--spacing-small);
        margin-right: var(--spacing-medium);

        .custom-button {
            min-width: var(--button-min-width);
            transition: var(--transition-base);

            &:hover {
                transform: translateY(-1px);
                box-shadow: var(--shadow-hover-button);
            }
        }
    }


}
</style>
