<template>
    <div class="inspection-management">
        <PageHeader title="工程巡检" icon="fa-search" description="巡检任务管理与巡检记录采集">
            <template #actions>
                <CustomButton type="success" :icon="'fa-upload'" :loading="exporting" @click="handleExport">
                    {{ exporting ? '导出中...' : '导出' }}
                </CustomButton>
            </template>
        </PageHeader>
        <TabSection v-model="activeTab" :tabs="tabs" @tab-change="handleTabChange" />

        <div class="tab-content-wrapper">
            <!-- 巡检任务 Tab -->
            <div v-if="activeTab === 'tasks'" class="task-section">
                <InspectionTasks ref="tasksRef" :dict-maps="dictMaps" :facility-options="facilityOptions"
                    :personnel-options="personnelOptions" :load-facility-options="loadFacilityOptions"
                    :get-dict-label-sync="getDictLabelSync" :get-facility-display="getFacilityDisplay"
                    :exporting="exporting" @task-updated="handleTaskUpdated" @export-tasks="handleExportTasks" />
            </div>

            <!-- 巡检记录 Tab -->
            <div v-else-if="activeTab === 'records'" class="record-section">
                <InspectionRecords ref="recordsRef" :dict-maps="dictMaps" :facility-options="facilityOptions"
                    :personnel-options="personnelOptions" :load-facility-options="loadFacilityOptions"
                    :get-dict-label-sync="getDictLabelSync" :get-facility-display="getFacilityDisplay"
                    :exporting="exporting" @record-created="handleRecordCreated"
                    @export-records="handleExportRecords" />
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/Common/PageHeader.vue'
import TabSection from '@/components/Common/TabSection.vue'
import CustomButton from '@/components/Common/CustomButton.vue'
import InspectionTasks from '@/components/Inspection/InspectionTasks.vue'
import InspectionRecords from '@/components/Inspection/InspectionRecords.vue'
import { useDictionary } from '@/composables/useDictionary'
import { formatDateTime, formatLocalTimeForAPI } from '@/utils/shared/common'
import { listTasks, listRecords } from '@/api/inspection'
import {
    getAvailablePumpingStations, getAvailableWaterPlants, getAvailableReservoirs
} from '@/api/engineering-service'
import { getAvailableMonitoringStations } from '@/api/monitoring'
import { getPersonnelList } from '@/api/management-info'

// ================= Refs for child components =================
const tasksRef = ref(null)
const recordsRef = ref(null)

// ================= 组合式函数 =================
const { getDictData } = useDictionary()

// ================= 响应式数据 =================
const activeTab = ref('tasks')
const tabs = [
    { name: 'tasks', label: '巡检任务', icon: 'fa-tasks' },
    { name: 'records', label: '巡检记录', icon: 'fa-file-image-o' }
]

const exporting = ref(false)

// 字典数据
const dictMaps = reactive({
    inspection_status: [],
    inspection_frequency: [],
    facility_type: [],
    device_status: []
})

// 设施选项数据
const facilityOptions = reactive({
    pumping_station: [],
    water_plant: [],
    reservoir: [],
    monitoring_station: []
})

// 人员选项数据
const personnelOptions = ref([])


// ================= 生命周期 =================
onMounted(async () => {
    await loadDictionaries()
    await loadPersonnelOptions()
    await loadAllFacilityOptions()
})

// ================= 计算属性/工具函数 =================
const getDictLabelSync = (dictType, value) => {
    const dict = dictMaps[dictType] || []
    const item = dict.find(d => String(d.value) === String(value))
    return item ? item.label : value || '-'
}

const getFacilityDisplay = (facilityType, facilityId) => {
    if (!facilityType || !facilityId) return '-'

    const facilityTypeLabel = getDictLabelSync('facility_type', facilityType)
    const facilityList = facilityOptions[facilityType] || []
    const facility = facilityList.find(f => f.value === facilityId)
    const facilityName = facility ? facility.label : `ID:${facilityId}`

    return `${facilityTypeLabel} - ${facilityName}`
}

// ================= 字典与选项加载 =================
const loadDictionaries = async () => {
    try {
        const [inspectionStatus, inspectionFrequency, facilityType, deviceStatus] = await Promise.all([
            getDictData('inspection_status'),
            getDictData('inspection_frequency'),
            getDictData('facility_type'),
            getDictData('device_status')
        ])

        dictMaps.inspection_status = inspectionStatus
        dictMaps.inspection_frequency = inspectionFrequency
        dictMaps.facility_type = facilityType
        dictMaps.device_status = deviceStatus
    } catch (error) {
        console.error('加载字典数据失败:', error)
    }
}

const loadPersonnelOptions = async () => {
    try {
        const response = await getPersonnelList({ page: 1, size: 1000 })
        personnelOptions.value = response.list.map(p => ({
            label: p.fullName,
            value: p.id
        }))
    } catch (error) {
        console.error('加载人员选项失败:', error)
    }
}

const loadAllFacilityOptions = async () => {
    try {
        await Promise.all([
            loadFacilityOptions('pumping_station'),
            loadFacilityOptions('water_plant'),
            loadFacilityOptions('reservoir'),
            loadFacilityOptions('monitoring_station')
        ])
    } catch (error) {
        console.error('加载设施选项失败:', error)
    }
}

const loadFacilityOptions = async (facilityType) => {
    if (!facilityType || facilityOptions[facilityType].length > 0) return

    try {
        let response = null
        switch (facilityType) {
            case 'pumping_station':
                response = await getAvailablePumpingStations()
                break
            case 'water_plant':
                response = await getAvailableWaterPlants()
                break
            case 'reservoir':
                response = await getAvailableReservoirs()
                break
            case 'monitoring_station':
                response = await getAvailableMonitoringStations()
                break
        }

        if (response) {
            facilityOptions[facilityType] = response.map(item => ({
                label: item.name,
                value: item.id
            }))
        }
    } catch (error) {
        console.error(`加载${facilityType}选项失败:`, error)
    }
}

// ================= Tab 切换 =================
const handleTabChange = (tabName) => {
    // Tab change logic can be handled here if needed
}

// ================= 子组件事件处理 =================
const handleTaskUpdated = () => {
    if (recordsRef.value) {
        recordsRef.value.loadRecordData(); // Assuming records component exposes this method
    }
}

const handleRecordCreated = () => {
    if (tasksRef.value) {
        tasksRef.value.loadTaskData();
    }
}

// ================= 导出 =================
const handleExport = async () => {
    if (activeTab.value === 'tasks' && tasksRef.value) {
        tasksRef.value.handleExport();
    } else if (activeTab.value === 'records' && recordsRef.value) {
        recordsRef.value.handleExport();
    }
}

const fetchAllPages = async (fetchFn, baseParams) => {
    const pageSize = 200
    let page = 1
    const all = []
    while (true) {
        const data = await fetchFn({ ...baseParams, page, size: pageSize })
        const items = data.items || []
        all.push(...items)
        if (!data.total || page * pageSize >= data.total) break
        page++
    }
    return all
}

const exportToCsv = (rows, headers, fileName) => {
    const csv = [headers.map(h => '"' + h.label + '"').join(',')]
    rows.forEach(row => {
        const line = headers.map(h => {
            const v = typeof h.get === 'function' ? h.get(row) : row[h.key]
            return '"' + (v !== undefined && v !== null ? String(v).replace(/"/g, '""') : '') + '"'
        }).join(',')
        csv.push(line)
    })
    const blob = new Blob(["\ufeff" + csv.join('\n')], { type: 'text/csv;charset=utf-8;' })
    const link = document.createElement('a')
    link.href = URL.createObjectURL(blob)
    link.download = `${fileName}-${formatDateTime(new Date(), 'YYYYMMDDHHmmss')}.csv`
    link.click()
    URL.revokeObjectURL(link.href)
}

const handleExportTasks = async (searchParams) => {
    exporting.value = true;
    try {
        const rows = await fetchAllPages(listTasks, searchParams);
        const headers = [
            { key: 'title', label: '计划名称' },
            { key: 'facilityName', label: '工程名称', get: (r) => r.facilityName || getFacilityDisplay(r.facilityType, r.facilityId) },
            { key: 'frequency', label: '巡检频率', get: (r) => getDictLabelSync('inspection_frequency', r.frequency) },
            { key: 'assigneeName', label: '巡检人员' },
            { key: 'startTime', label: '开始时间', get: (r) => r.startTime ? formatDateTime(r.startTime, 'YYYY-MM-DD HH:mm') : '' },
            { key: 'endTime', label: '结束时间', get: (r) => r.endTime ? formatDateTime(r.endTime, 'YYYY-MM-DD HH:mm') : '' },
            { key: 'taskCount', label: '任务数' },
            { key: 'completedCount', label: '完成数' }
        ];
        exportToCsv(rows, headers, '巡检任务');
    } catch (e) {
        ElMessage.error('导出失败');
    } finally {
        exporting.value = false;
    }
}

const handleExportRecords = async (searchParams) => {
    exporting.value = true;
    try {
        const rows = await fetchAllPages(listRecords, searchParams);
        const headers = [
            { key: 'facilityDisplay', label: '巡检站点', get: (r) => getFacilityDisplay(r.facilityType, r.facilityId) },
            { key: 'facilityTypeDisplay', label: '巡检类型', get: (r) => getDictLabelSync('facility_type', r.facilityType) },
            { key: 'issueFlag', label: '异常情况', get: (r) => (r.issueFlag ? '异常' : '正常') },
            { key: 'issueDescription', label: '巡检情况' },
            { key: 'processed', label: '处理状态', get: (r) => (r.resolvedAt ? '已处理' : '未处理') },
            { key: 'inspectorName', label: '负责人' },
            { key: 'recordTime', label: '日期', get: (r) => formatDateTime(r.recordTime, 'YYYY-MM-DD HH:mm:ss') }
        ];
        exportToCsv(rows, headers, '巡检记录');
    } catch (e) {
        ElMessage.error('导出失败');
    } finally {
        exporting.value = false;
    }
}
</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;

.inspection-management {
    padding: var(--spacing-lg);
    background-color: var(--bg-secondary);
    min-height: calc(100vh - var(--header-height));

    .tab-content-wrapper {
        background: var(--bg-primary);
        border-radius: var(--border-radius-md);
        box-shadow: var(--shadow-light);
        border: 1px solid var(--border-color-light);
        overflow: hidden;
    }

    .task-section,
    .record-section {
        padding: var(--spacing-md);
    }

    @include respond-to(md) {
        padding: var(--spacing-md);
    }

    @include respond-to(sm) {
        padding: var(--spacing-sm);

        .task-section,
        .record-section {
            padding: var(--spacing-sm);
        }
    }
}
</style>