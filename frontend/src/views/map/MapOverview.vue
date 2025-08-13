<template>
    <div class="map-overview">
        <!-- 主要内容区域 -->
        <div class="map-content">
            <!-- 地图区域占用全屏 -->
            <div class="map-area">
                <AMapContainer ref="mapContainer" :facilities="filteredFacilities"
                    :monitoring-stations="filteredMonitoringStations" :center="mapCenter" :zoom="mapZoom"
                    :map-style="mapStyle" :show-road-net="showRoadNet" :show-traffic="showTraffic"
                    :pipelines="mockPipelineData" :warning-stations="mockWarningData" @map-click="handleMapClick"
                    @device-popup-show="handleDevicePopupShow" @device-popup-hide="handleDevicePopupHide" />

                <!-- 图层控制面板 -->
                <div class="layer-control-panel" :class="{ collapsed: !layerPanelVisible }">
                    <div class="panel-header" @click="toggleLayerPanel">
                        <!-- 展开状态显示 -->
                        <template v-if="layerPanelVisible">
                            <i class="fa fa-layers"></i>
                            <span>图层控制</span>
                            <i class="fa fa-angle-down toggle-icon expanded"></i>
                        </template>
                        <!-- 收缩状态显示 -->
                        <template v-else>
                            <i class="fa fa-map-o collapsed-icon" title="点击展开图层控制"></i>
                        </template>
                    </div>

                    <div class="panel-content" :class="{ 'content-hidden': !layerPanelVisible }">
                        <!-- 地图样式选择 -->
                        <div class="layer-group">
                            <div class="group-title">地图样式</div>
                            <div class="layer-options">
                                <div class="layer-button" :class="{ active: mapStyle === 'standard' }"
                                    @click="handleMapStyleChange('standard')">
                                    <i class="fa fa-map"></i>
                                    <span>标准地图</span>
                                </div>
                                <div class="layer-button" :class="{ active: mapStyle === 'satellite' }"
                                    @click="handleMapStyleChange('satellite')">
                                    <i class="fa fa-globe"></i>
                                    <span>卫星图</span>
                                </div>
                            </div>
                        </div>

                        <!-- 图层叠加 -->
                        <div class="layer-group">
                            <div class="group-title">图层叠加</div>
                            <div class="layer-options">
                                <div class="layer-button" :class="{ active: showRoadNet }" @click="toggleRoadNet">
                                    <i class="fa fa-road"></i>
                                    <span>路网</span>
                                </div>
                                <div class="layer-button" :class="{ active: showTraffic }" @click="toggleTraffic">
                                    <i class="fa fa-car"></i>
                                    <span>路况</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 悬浮设备管理面板 -->
                <FacilityPanel :facilities="filteredFacilities" :monitoring-stations="filteredMonitoringStations"
                    :selected-device="selectedFacility" :enable-floating="true" @device-select="handleDeviceSelect"
                    @device-locate="handleDeviceLocate" @device-type-change="handleDeviceTypeChange" />

                <!-- 右侧图例说明面板 -->
                <RightControlPanel :initial-visible="rightPanelVisible" @panel-toggle="handleRightPanelToggle" />

                <!-- 设备信息弹窗 -->
                <DeviceInfoPopup :visible="popupVisible" :device="popupDevice" :position="popupPosition"
                    :container-size="mapContainerSize" :marker-size="popupMarkerSize"
                    :marker-border-width="popupMarkerBorder" @close="handlePopupClose"
                    @view-detail="handlePopupViewDetail" />
            </div>
        </div>

        <!-- 监测点详情对话框 -->
        <CustomDialog v-model:visible="detailDialogVisible"
            :title="`${detailDevice?.stationName || detailDevice?.name || '设备'} - 详细信息`"
            width="var(--panel-height-default)" @confirm="detailDialogVisible = false"
            @cancel="detailDialogVisible = false">
            <div class="device-detail" v-if="detailDevice">
                <!-- 基础信息 -->
                <div class="detail-section">
                    <h4 class="section-title">基础信息</h4>
                    <div class="info-grid">
                        <div class="info-item">
                            <span class="info-label">名称</span>
                            <span class="info-value">{{ detailDevice.stationName || detailDevice.name || '未知' }}</span>
                        </div>
                        <div class="info-item" v-if="detailDevice.stationCode">
                            <span class="info-label">站码</span>
                            <span class="info-value">{{ detailDevice.stationCode }}</span>
                        </div>
                        <div class="info-item" v-if="detailDevice.monitoringItem">
                            <span class="info-label">监测项目</span>
                            <span class="info-value">{{ detailDevice.monitoringItem }}</span>
                        </div>
                        <div class="info-item" v-if="detailDevice.type">
                            <span class="info-label">设备类型</span>
                            <span class="info-value">{{ detailDevice.type }}</span>
                        </div>
                        <div class="info-item" v-if="detailDevice.status">
                            <span class="info-label">运行状态</span>
                            <span class="info-value">{{ detailDevice.status }}</span>
                        </div>
                        <div class="info-item" v-if="detailDevice.capacity">
                            <span class="info-label">容量</span>
                            <span class="info-value">{{ detailDevice.capacity }}</span>
                        </div>
                        <div class="info-item" v-if="detailDevice.longitude && detailDevice.latitude">
                            <span class="info-label">位置</span>
                            <span class="info-value">{{ detailDevice.longitude.toFixed(4) }}, {{
                                detailDevice.latitude.toFixed(4) }}</span>
                        </div>
                        <div class="info-item" v-if="detailDevice.lastMonitoringTime || detailDevice.updateTime">
                            <span class="info-label">更新时间</span>
                            <span class="info-value">{{ formatDateTime(detailDevice.lastMonitoringTime ||
                                detailDevice.updateTime) }}</span>
                        </div>
                    </div>
                </div>

                <!-- 监测数据 (仅监测站显示) -->
                <div class="detail-section"
                    v-if="isMonitoringStation && detailParsedData && Object.keys(detailParsedData).length > 0">
                    <h4 class="section-title">最新监测数据</h4>
                    <div class="monitoring-data">
                        <div class="data-item" v-for="(value, key) in detailParsedData" :key="key">
                            <span class="data-label">{{ getIndicatorLabel(key) }}</span>
                            <span class="data-value">
                                <span class="value">{{ value }}</span>
                                <span class="unit" v-if="getIndicatorUnit(key)">{{ getIndicatorUnit(key) }}</span>
                            </span>
                        </div>
                    </div>
                </div>

                <!-- 无数据提示 (仅监测站且无数据时显示) -->
                <div class="detail-section" v-else-if="isMonitoringStation">
                    <h4 class="section-title">最新监测数据</h4>
                    <div class="no-data">
                        <i class="fa fa-exclamation-triangle"></i>
                        <span>暂无最新监测数据</span>
                    </div>
                </div>

                <!-- 历史趋势简览（仅水质站点显示，纯数值简要信息） -->
                <div class="detail-section" v-if="isWaterQualityStation">
                    <h4 class="section-title">历史趋势简览（最近记录）</h4>
                    <div class="trend-summary" :class="{ loading: trendLoading }">
                        <div class="trend-card" v-for="item in trendItems" :key="item.code">
                            <div class="trend-card__header">
                                <span class="trend-card__title">{{ item.name }}</span>
                                <span class="trend-card__unit">{{ item.unit }}</span>
                            </div>
                            <div class="trend-card__body" v-if="trendSummaries[item.code]">
                                <div class="trend-row">
                                    <span class="label">最新值</span>
                                    <span class="value">{{ trendSummaries[item.code].latestDisplay }}</span>
                                </div>
                                <div class="trend-row">
                                    <span class="label">较前值</span>
                                    <span class="value" :class="trendSummaries[item.code].deltaClass">{{
                                        trendSummaries[item.code].deltaDisplay }}</span>
                                </div>
                                <div class="trend-row">
                                    <span class="label">统计</span>
                                    <span class="value">最小 {{ trendSummaries[item.code].minDisplay }} / 均值 {{
                                        trendSummaries[item.code].avgDisplay }} / 最大 {{
                                            trendSummaries[item.code].maxDisplay
                                        }}</span>
                                </div>
                                <div class="trend-row">
                                    <span class="label">趋势</span>
                                    <span class="value">{{ trendSummaries[item.code].trendText }}</span>
                                </div>
                            </div>
                            <div class="trend-card__empty" v-else>
                                暂无数据
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <template #footer>
                <div class="dialog-footer">
                    <button class="dialog-btn dialog-btn--primary" @click="detailDialogVisible = false">
                        确定
                    </button>
                </div>
            </template>
        </CustomDialog>
    </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from "vue";
import { ElMessage } from "element-plus";
import AMapContainer from "@/components/Map/AMapContainer.vue";
import FacilityPanel from "@/components/Map/FacilityPanel.vue";
import RightControlPanel from "@/components/Map/RightControlPanel.vue";
import DeviceInfoPopup from "@/components/Map/DeviceInfoPopup.vue";
import CustomDialog from "@/components/Common/CustomDialog.vue";
import {
    getAllFacilityLocations,
    getMonitoringStationsWithLatestData
} from "@/api/map";
import {
    getUnifiedDeviceType
} from "@/utils/map/deviceType";
import { getWaterQualityMonitoringData } from "@/api/monitoring";
// 指标字段映射：按最近记录统计
const WQ_FIELD_MAP = {
    WT: 'waterTemperature',
    TU: 'turbidity',
    PH: 'phValue',
    EC: 'conductivity',
    DO: 'dissolvedOxygen',
    AN: 'ammoniaNitrogen',
    COD: 'codValue',
    RC: 'residualChlorine'
};

// 响应式数据
const loading = ref(false);
const mapContainer = ref(null);
const selectedFacility = ref(null);

// 数据存储
const facilities = ref([]);
const monitoringStations = ref([]);

// 设备类型选择配置
const selectedDeviceType = ref('all');

// 地图配置
const mapCenter = ref([114.3, 30.6]); // 湖北省中心坐标
const mapZoom = ref(8);

// 图层控制状态
const mapStyle = ref('standard'); // 地图样式：standard | satellite
const showRoadNet = ref(false); // 显示路网
const showTraffic = ref(false); // 显示路况
const layerPanelVisible = ref(true); // 图层面板显示状态

// 右侧图例面板状态
const rightPanelVisible = ref(true); // 右侧面板显示状态

// 设备信息弹窗状态
const popupVisible = ref(false); // 弹窗显示状态
const popupDevice = ref(null); // 弹窗设备数据
const popupPosition = ref({ x: 0, y: 0 }); // 弹窗位置
const popupMarkerSize = ref(16); // 弹窗对应的marker尺寸
const popupMarkerBorder = ref(1); // 弹窗对应的marker边框宽度
const mapContainerSize = ref({ width: 0, height: 0 }); // 地图容器尺寸

// 监测点详情对话框状态
const detailDialogVisible = ref(false); // 详情对话框显示状态
const detailDevice = ref(null); // 详情对话框中的设备数据
const detailParsedData = ref(null); // 详情对话框中的解析后数据

// 历史趋势简览（仅水质站点）
const isWaterQualityStation = computed(() => {
    return !!(detailDevice.value && detailDevice.value.stationId && detailDevice.value.monitoringItem === 'WQ');
});

const trendLoading = ref(false);
const trendItems = ref([
    { code: 'WT', name: '水温', unit: '℃' },
    { code: 'TU', name: '浊度', unit: 'NTU' },
    { code: 'PH', name: 'pH值', unit: '' },
    { code: 'EC', name: '电导率', unit: 'μS/cm' },
    { code: 'DO', name: '溶解氧', unit: 'mg/L' },
    { code: 'AN', name: '氨氮', unit: 'mg/L' },
    { code: 'COD', name: '化学需氧量', unit: 'mg/L' },
    { code: 'RC', name: '余氯', unit: 'mg/L' }
]);
const trendSummaries = ref({});

const formatNumber = (val) => {
    if (val === null || val === undefined || isNaN(val)) return '-';
    const num = Number(val);
    if (Math.abs(num) >= 100) return num.toFixed(1);
    if (Math.abs(num) >= 10) return num.toFixed(2);
    return num.toFixed(3);
};

const buildSummary = (labels = [], values = [], unit = '') => {
    if (!Array.isArray(values) || values.length === 0) return null;
    const numeric = values.map(v => (v === null || v === undefined) ? null : Number(v)).filter(v => v !== null && !isNaN(v));
    if (numeric.length === 0) return null;
    const latest = numeric[numeric.length - 1];
    const prev = numeric.length >= 2 ? numeric[numeric.length - 2] : null;
    const delta = (prev !== null && prev !== undefined) ? (latest - prev) : null;
    const min = Math.min(...numeric);
    const max = Math.max(...numeric);
    const avg = numeric.reduce((a, b) => a + b, 0) / numeric.length;
    const first = numeric[0];
    const trendText = (first !== undefined && first !== null)
        ? (latest > first ? '整体上升' : latest < first ? '整体下降' : '整体平稳')
        : '—';
    const deltaClass = delta === null ? '' : (delta > 0 ? 'up' : (delta < 0 ? 'down' : 'flat'));
    return {
        latestDisplay: `${formatNumber(latest)}${unit ? ' ' + unit : ''}`,
        deltaDisplay: delta === null ? '—' : `${delta > 0 ? '↑ ' : delta < 0 ? '↓ ' : '— '}${formatNumber(Math.abs(delta))}${unit ? ' ' + unit : ''}`,
        deltaClass,
        minDisplay: `${formatNumber(min)}${unit ? ' ' + unit : ''}`,
        maxDisplay: `${formatNumber(max)}${unit ? ' ' + unit : ''}`,
        avgDisplay: `${formatNumber(avg)}${unit ? ' ' + unit : ''}`,
        trendText
    };
};

const loadTrendSummaries = async () => {
    if (!isWaterQualityStation.value) {
        trendSummaries.value = {};
        return;
    }
    try {
        trendLoading.value = true;
        const stationId = detailDevice.value.stationId;
        // 获取最近记录（按监测时间倒序）
        const resp = await getWaterQualityMonitoringData({
            stationId,
            page: 1,
            size: 30,
            sort: 'monitoring_time,desc',
            dataQuality: 1
        });
        const items = (resp && resp.items) ? resp.items : [];
        const next = {};
        trendItems.value.forEach((item) => {
            const field = WQ_FIELD_MAP[item.code];
            const valuesDesc = items.map(row => row && row[field]);
            const values = valuesDesc.filter(v => v !== null && v !== undefined).reverse(); // 按时间正序
            const summary = buildSummary([], values, item.unit);
            if (summary) next[item.code] = summary;
        });
        trendSummaries.value = next;
    } catch (e) {
        console.error('加载历史趋势简览失败:', e);
        ElMessage.error('加载历史趋势简览失败');
    } finally {
        trendLoading.value = false;
    }
};

// 模拟管线数据 (临时用于测试)
const mockPipelineData = ref([
    {
        id: 1,
        name: '主供水干管-1',
        type: 'main',
        coordinates: [
            { lng: 114.25, lat: 30.55 },
            { lng: 114.30, lat: 30.60 },
            { lng: 114.35, lat: 30.62 }
        ]
    },
    {
        id: 2,
        name: '支管线-1',
        type: 'branch',
        coordinates: [
            { lng: 114.30, lat: 30.60 },
            { lng: 114.32, lat: 30.58 },
            { lng: 114.34, lat: 30.56 }
        ]
    },
    {
        id: 3,
        name: '主供水干管-2',
        type: 'main',
        coordinates: [
            { lng: 114.20, lat: 30.65 },
            { lng: 114.25, lat: 30.68 },
            { lng: 114.30, lat: 30.70 }
        ]
    }
]);

// 模拟预警数据 (临时用于测试)
const mockWarningData = ref([
    {
        id: 1,
        name: '水位预警点-1',
        level: 'general',
        longitude: 114.28,
        latitude: 30.57,
        warningType: '水位',
        warningContent: '水位超过预警线'
    },
    {
        id: 2,
        name: '流量预警点-1',
        level: 'serious',
        longitude: 114.32,
        latitude: 30.65,
        warningType: '流量',
        warningContent: '流量异常偏低'
    },
    {
        id: 3,
        name: '水质预警点-1',
        level: 'general',
        longitude: 114.35,
        latitude: 30.58,
        warningType: '水质',
        warningContent: '水质指标异常'
    }
]);

// 筛选后的设施数据（根据选择的设备类型）
const filteredFacilities = computed(() => {
    let result = [];

    // 如果选择全部设备，返回所有设施
    if (selectedDeviceType.value === 'all') {
        result = facilities.value;
    }
    // 如果选择监测站点，不显示设施
    else if (selectedDeviceType.value === 'monitoring_station') {
        result = [];
    }
    // 根据选择的设备类型进行筛选
    else {
        result = facilities.value.filter(facility => {
            const deviceType = getUnifiedDeviceType(facility);
            return deviceType === selectedDeviceType.value;
        });
    }

    return result;
});

// 筛选后的监测站数据（根据选择的设备类型）
const filteredMonitoringStations = computed(() => {
    // 只有选择监测站点时才显示监测站
    if (selectedDeviceType.value === 'monitoring_station') {
        return monitoringStations.value;
    }

    // 其他情况均不显示监测站（包括全部设备）
    return [];
});

// 判断当前详情对话框中的设备是否是监测站
const isMonitoringStation = computed(() => {
    return !!(detailDevice.value && (detailDevice.value.stationName || detailDevice.value.stationId || detailDevice.value.monitoringItem));
});

// 生命周期 - 组件挂载时加载数据
onMounted(async () => {
    await loadMapData();
    // 延迟初始化地图容器尺寸，确保DOM完全渲染
    setTimeout(() => {
        updateMapContainerSize();
    }, 100);
    // 监听窗口大小变化
    window.addEventListener('resize', updateMapContainerSize);
});

// 生命周期 - 组件卸载时清理监听器
onUnmounted(() => {
    window.removeEventListener('resize', updateMapContainerSize);
});

/**
 * 数据预处理：去重和清洗
 */
const preprocessData = (facilities, stations) => {
    // 1. 去重函数
    const removeDuplicates = (data, keyFn) => {
        const seen = new Set();
        return data.filter(item => {
            const key = keyFn(item);
            if (seen.has(key)) {
                return false;
            }
            seen.add(key);
            return true;
        });
    };

    // 2. 设施数据去重（基于名称+坐标+类型）
    const cleanFacilities = removeDuplicates(facilities, item =>
        `${item.name}-${item.type}-${item.longitude}-${item.latitude}`
    );

    // 3. 监测站数据去重（基于名称+坐标+监测类型）
    const cleanStations = removeDuplicates(stations, item =>
        `${item.stationName || item.name}-${item.longitude}-${item.latitude}-${item.monitoringItem}`
    );

    // 4. 移除真正重复的数据（完全相同的名称、坐标和类型）
    const realStationLocations = new Set(cleanStations.map(s =>
        `${s.stationName || s.name}-${s.longitude}-${s.latitude}`
    ));

    const finalFacilities = cleanFacilities.filter(facility => {
        // 只移除类型为monitoring_station但没有实际监测项目的设施，且存在相同位置的真实监测站
        if (facility.type === 'monitoring_station' && !facility.monitoringItem) {
            const facilityKey = `${facility.name}-${facility.longitude}-${facility.latitude}`;
            return !realStationLocations.has(facilityKey);
        }
        // 保留所有其他设施
        return true;
    });

    return {
        facilities: finalFacilities,
        stations: cleanStations
    };
};

/**
 * 加载地图数据
 */
const loadMapData = async () => {
    try {
        loading.value = true;

        // 并行加载所有数据
        const [facilitiesRes, stationsRes] = await Promise.all([
            getAllFacilityLocations(),
            getMonitoringStationsWithLatestData()
        ]);

        const rawFacilities = facilitiesRes || [];
        const rawStations = stationsRes || [];

        // 数据预处理
        const { facilities: cleanFacilities, stations: cleanStations } = preprocessData(rawFacilities, rawStations);

        facilities.value = cleanFacilities;
        monitoringStations.value = cleanStations;

        // 检查数据有效性
        const validFacilities = facilities.value.filter(f => f.longitude && f.latitude);
        const validStations = monitoringStations.value.filter(s => s.longitude && s.latitude);



        if (validFacilities.length === 0 && validStations.length === 0) {
            ElMessage.warning("数据加载成功，但未发现有效的地理位置信息");
        } else {
            const totalValid = validFacilities.length + validStations.length;
            ElMessage.success(`数据加载成功！共 ${totalValid} 个可定位设备 (设施 ${validFacilities.length} + 监测站 ${validStations.length})，支持按类型筛选显示`);
        }

    } catch (error) {
        console.error('❌ 地图数据加载失败:', error);
        ElMessage.error(`数据加载失败: ${error.message || '网络请求异常'}`);
    } finally {
        loading.value = false;
    }
};

/**
 * 处理设备选择（来自FacilityPanel）
 */
const handleDeviceSelect = (device) => {
    selectedFacility.value = device;
};

/**
 * 处理设备定位（来自FacilityPanel）
 */
const handleDeviceLocate = (location) => {
    if (mapContainer.value) {
        mapContainer.value.setCenter([location.longitude, location.latitude]);
        mapContainer.value.setZoom(17);
    }
};

/**
 * 处理设备类型选择变化（来自FacilityPanel）
 */
const handleDeviceTypeChange = (type) => {
    selectedDeviceType.value = type;

    // 如果当前选中的设备不在新的筛选结果中，清除选中状态
    if (selectedFacility.value) {
        const deviceType = getUnifiedDeviceType(selectedFacility.value);
        if (type !== 'all' && deviceType !== type) {
            selectedFacility.value = null;
        }
    }
};

/**
 * 处理地图点击
 */
const handleMapClick = (location) => {
    selectedFacility.value = null;
    // 地图点击时隐藏弹窗（由AMapContainer的handleMapClick已处理）
};

/**
 * 切换图层面板显示状态
 */
const toggleLayerPanel = () => {
    layerPanelVisible.value = !layerPanelVisible.value;
};

/**
 * 处理地图样式切换
 */
const handleMapStyleChange = (style) => {
    mapStyle.value = style;
};

/**
 * 切换路网图层
 */
const toggleRoadNet = () => {
    showRoadNet.value = !showRoadNet.value;
};

/**
 * 切换路况图层
 */
const toggleTraffic = () => {
    showTraffic.value = !showTraffic.value;
};

/**
 * 处理右侧面板切换
 */
const handleRightPanelToggle = (visible) => {
    rightPanelVisible.value = visible;
    console.log('Legend panel toggled:', visible);
};

/**
 * 更新地图容器尺寸
 */
const updateMapContainerSize = () => {
    const mapElement = document.querySelector('.map-area');
    if (mapElement) {
        const rect = mapElement.getBoundingClientRect();
        // 确保获取到有效的尺寸数据
        if (rect.width > 0 && rect.height > 0) {
            mapContainerSize.value = {
                width: rect.width,
                height: rect.height
            };

        } else {
            // 如果尺寸为0，延迟重试
            console.warn('地图容器尺寸为0，将在100ms后重试');
            setTimeout(updateMapContainerSize, 100);
        }
    } else {
        console.warn('未找到地图容器元素，将在100ms后重试');
        setTimeout(updateMapContainerSize, 100);
    }
};

/**
 * 处理设备弹窗显示
 */
const handleDevicePopupShow = (data) => {
    popupDevice.value = data.device;
    popupPosition.value = data.position;
    popupMarkerSize.value = data.markerSize || 16; // 设置marker尺寸
    popupMarkerBorder.value = data.markerBorderWidth || 1; // 设置marker边框宽度
    popupVisible.value = true;

    // 同时更新selectedFacility用于其他功能
    selectedFacility.value = data.device;
};

/**
 * 处理设备弹窗隐藏
 */
const handleDevicePopupHide = () => {
    popupVisible.value = false;
    popupDevice.value = null;
};

/**
 * 处理弹窗关闭
 */
const handlePopupClose = () => {
    handleDevicePopupHide();
};

/**
 * 处理弹窗查看详情
 */
const handlePopupViewDetail = (device) => {
    console.log('查看设备详情:', device);

    // 关闭地图上的气泡弹窗
    handleDevicePopupHide();

    // 设置详情对话框数据
    detailDevice.value = device;

    // 如果是监测站，解析监测数据
    if (device.stationName || device.stationId || device.monitoringItem) {
        let parsedData = null;
        const data = device.latestData;
        try {
            if (data && typeof data === 'string') {
                parsedData = JSON.parse(data);
            } else if (data && typeof data === 'object') {
                parsedData = data;
            }
        } catch (error) {
            console.warn('解析监测数据失败:', device.latestData, error);
        }

        if (parsedData) {
            // 过滤掉空值
            parsedData = Object.fromEntries(
                Object.entries(parsedData).filter(([key, value]) =>
                    value !== null && value !== undefined && value !== ''
                )
            );
        }

        detailParsedData.value = parsedData;
    } else {
        detailParsedData.value = null;
    }

    // 若为水质监测站，加载历史趋势简览
    if (isWaterQualityStation.value) {
        loadTrendSummaries();
    } else {
        trendSummaries.value = {};
    }

    // 打开详情对话框
    detailDialogVisible.value = true;
};

/**
 * 格式化日期时间
 */
const formatDateTime = (dateString) => {
    if (!dateString) return '';
    try {
        const date = new Date(dateString);
        return date.toLocaleString('zh-CN', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
    } catch (error) {
        return dateString;
    }
};

/**
 * 获取指标标签
 */
const getIndicatorLabel = (key) => {
    const labels = {
        waterTemperature: '水温',
        turbidity: '浊度',
        phValue: 'pH值',
        conductivity: '电导率',
        dissolvedOxygen: '溶解氧',
        ammoniaNitrogen: '氨氮',
        codValue: 'COD',
        residualChlorine: '余氯'
    };
    return labels[key] || key;
};

/**
 * 获取指标单位
 */
const getIndicatorUnit = (key) => {
    const units = {
        waterTemperature: '℃',
        turbidity: 'NTU',
        phValue: '',
        conductivity: 'μS/cm',
        dissolvedOxygen: 'mg/L',
        ammoniaNitrogen: 'mg/L',
        codValue: 'mg/L',
        residualChlorine: 'mg/L'
    };
    return units[key] || '';
};


</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;

.map-overview {
    min-height: 100vh;
    display: flex;
    flex-direction: column;

    .map-content {
        flex: 1;
        padding: var(--spacing-small);

        .map-area {
            width: 100%;
            height: calc(100vh - 100px);
            position: relative;
            background: var(--bg-primary);
            border-radius: var(--border-radius-large);
            box-shadow: var(--box-shadow-light);
            overflow: visible;

            .layer-control-panel {
                position: absolute;
                top: 16px;
                left: 16px;
                background: var(--bg-primary);
                border-radius: var(--border-radius-large);
                box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
                z-index: 1000;
                min-width: 160px;
                max-width: 220px;
                overflow: hidden;
                transition: all var(--map-panel-transition-duration) var(--map-panel-transition-ease);
                transform-origin: top left;

                &.collapsed {
                    min-width: auto;
                    max-width: 48px;
                    width: 48px;
                    height: 48px;
                    transform: scale(var(--map-panel-hidden-scale));

                    .panel-header {
                        justify-content: center;

                        .collapsed-icon {
                            margin: 0;
                            font-size: 18px;
                            color: var(--primary-color);
                            transition: all var(--map-panel-transition-duration) var(--map-panel-transition-ease);

                            &:hover {
                                color: var(--primary-light);
                                transform: scale(1.15) rotate(5deg);
                            }
                        }
                    }

                    .panel-content {
                        opacity: 0;
                        transform: translateY(var(--map-panel-hidden-translate-y)) scale(var(--map-panel-hidden-scale));
                        pointer-events: none;
                        max-height: 0;
                    }
                }

                .panel-header {
                    padding: 12px 16px;
                    background: var(--bg-secondary);
                    border-bottom: 1px solid var(--border-color-lighter);
                    display: flex;
                    align-items: center;
                    cursor: pointer;
                    transition: all var(--map-panel-transition-duration) var(--map-panel-transition-ease);
                    position: relative;
                    overflow: hidden;

                    &:hover {
                        background: var(--bg-tertiary);
                        transform: translateY(-1px);
                        box-shadow: var(--shadow-card);
                    }

                    &:active {
                        transform: translateY(0);
                    }

                    i:first-child {
                        color: var(--primary-color);
                        font-size: 16px;
                        margin-right: var(--spacing-small);
                        transition: all var(--map-panel-transition-duration) var(--map-panel-transition-ease);

                        &:hover {
                            transform: scale(1.1);
                        }
                    }

                    span {
                        flex: 1;
                        font-weight: var(--font-weight-medium);
                        color: var(--text-primary);
                        font-size: var(--font-size-base);
                        transition: all var(--map-panel-transition-duration) var(--map-panel-transition-ease);
                    }

                    .toggle-icon {
                        color: var(--text-secondary);
                        font-size: 12px;
                        transition: all var(--map-panel-transition-duration) var(--map-panel-transition-ease);
                        margin-left: var(--spacing-small);
                        transform-origin: center center;

                        &.expanded {
                            transform: rotate(180deg);
                        }

                        &:not(.expanded) {
                            transform: rotate(0deg);
                        }

                        &:hover {
                            color: var(--primary-color);

                            &.expanded {
                                transform: rotate(180deg) scale(1.2);
                            }

                            &:not(.expanded) {
                                transform: rotate(0deg) scale(1.2);
                            }
                        }
                    }
                }

                .panel-content {
                    padding: var(--spacing-base);
                    transition: all var(--map-panel-transition-duration) var(--map-panel-transition-ease);
                    transform-origin: top center;
                    max-height: var(--map-panel-max-height);
                    opacity: 1;
                    transform: translateY(0) scale(1);

                    .layer-group {
                        margin-bottom: var(--spacing-large);
                        opacity: 1;
                        transform: none;

                        &:last-child {
                            margin-bottom: 0;
                        }

                        .group-title {
                            font-size: var(--font-size-small);
                            color: var(--text-secondary);
                            font-weight: var(--font-weight-medium);
                            text-transform: uppercase;
                            letter-spacing: 0.5px;
                            margin-bottom: var(--spacing-medium);
                        }

                        .layer-options {
                            display: flex;
                            flex-direction: column;
                            gap: var(--spacing-small);

                            .layer-button {
                                display: flex;
                                align-items: center;
                                padding: 10px 12px;
                                background: var(--bg-secondary);
                                border: 1px solid var(--border-color-light);
                                border-radius: var(--border-radius-base);
                                cursor: pointer;
                                transition: all var(--map-panel-transition-duration) var(--map-panel-transition-ease);
                                font-size: var(--font-size-small);
                                position: relative;
                                overflow: hidden;
                                transform: translateZ(0);

                                &::before {
                                    content: '';
                                    position: absolute;
                                    top: 0;
                                    left: -100%;
                                    width: 100%;
                                    height: 100%;
                                    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
                                    transition: left var(--map-panel-transition-duration) var(--map-panel-transition-ease);
                                }

                                &:hover {
                                    background: var(--bg-tertiary);
                                    border-color: var(--primary-color);
                                    transform: translateY(-2px) scale(1.02);
                                    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);

                                    &::before {
                                        left: 100%;
                                    }

                                    i {
                                        transform: scale(1.1) rotate(5deg);
                                    }

                                    span {
                                        transform: translateX(2px);
                                    }
                                }

                                &:active {
                                    transform: translateY(-1px) scale(0.98);
                                }

                                &.active {
                                    background: #ecf5ff;
                                    border-color: var(--primary-color);
                                    color: var(--primary-color);
                                    transform: translateY(-1px);
                                    box-shadow: var(--shadow-card-strong);

                                    i {
                                        color: var(--primary-color);
                                    }

                                    span {
                                        font-weight: var(--font-weight-medium);
                                    }
                                }

                                i {
                                    font-size: 14px;
                                    margin-right: var(--spacing-small);
                                    color: var(--text-secondary);
                                    width: 16px;
                                    text-align: center;
                                    transition: all var(--map-panel-transition-duration) var(--map-panel-transition-ease);
                                }

                                span {
                                    color: var(--text-primary);
                                    font-weight: var(--font-weight-normal);
                                    transition: all var(--map-panel-transition-duration) var(--map-panel-transition-ease);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

.device-detail {
    .detail-section {
        margin-bottom: var(--spacing-large);

        &:last-child {
            margin-bottom: 0;
        }

        .section-title {
            font-size: var(--font-size-medium);
            font-weight: var(--font-weight-bold);
            color: var(--text-primary);
            margin: 0 0 var(--spacing-base) 0;
            padding-bottom: var(--spacing-small);
            border-bottom: 1px solid var(--border-color-lighter);
        }

        .info-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: var(--spacing-base);

            .info-item {
                display: flex;
                justify-content: space-between;
                align-items: center;
                padding: var(--spacing-small);
                background: var(--bg-tertiary);
                border-radius: var(--border-radius-base);
                border: 1px solid var(--border-color-extra-light);

                .info-label {
                    font-size: var(--font-size-small);
                    color: var(--text-secondary);
                    font-weight: var(--font-weight-medium);
                    margin-right: var(--spacing-base);
                    flex-shrink: 0;
                }

                .info-value {
                    font-size: var(--font-size-small);
                    color: var(--text-primary);
                    text-align: right;
                    word-break: break-word;
                }
            }
        }

        .monitoring-data {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: var(--spacing-base);

            .data-item {
                display: flex;
                justify-content: space-between;
                align-items: center;
                padding: var(--spacing-small);
                background: var(--bg-primary);
                border: 1px solid var(--border-color-light);
                border-radius: var(--border-radius-base);
                transition: all 0.2s ease;

                &:hover {
                    border-color: var(--primary-light);
                    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
                }

                .data-label {
                    font-size: var(--font-size-small);
                    color: var(--text-secondary);
                    font-weight: var(--font-weight-medium);
                    margin-right: var(--spacing-base);
                }

                .data-value {
                    display: flex;
                    align-items: baseline;
                    gap: var(--spacing-mini);

                    .value {
                        font-size: var(--font-size-base);
                        font-weight: var(--font-weight-bold);
                        color: var(--primary-color);
                    }

                    .unit {
                        font-size: var(--font-size-extra-small);
                        color: var(--text-tertiary);
                    }
                }
            }
        }

        .no-data {
            display: flex;
            align-items: center;
            justify-content: center;
            padding: var(--spacing-large);
            color: var(--text-tertiary);
            font-size: var(--font-size-small);
            background: var(--bg-tertiary);
            border-radius: var(--border-radius-base);
            border: 1px dashed var(--border-color-light);

            i {
                margin-right: var(--spacing-small);
                color: var(--warning-color);
                font-size: var(--font-size-base);
            }
        }

        .trend-summary {
            display: flex;
            flex-direction: column;
            gap: var(--spacing-large);
            max-width: 100%;

            &.loading {
                opacity: var(--opacity-medium);
            }

            .trend-card {
                border: 1px solid var(--border-color-extra-light);
                border-radius: var(--border-radius-base);
                background: var(--bg-primary);
                padding: var(--spacing-large);
                max-width: 100%;
                box-shadow: var(--shadow-light);
                transition: box-shadow 0.2s ease;
                display: flex;
                flex-direction: column;
                gap: var(--spacing-base);

                &:hover {
                    box-shadow: var(--shadow-base);
                }

                &__header {
                    display: flex;
                    align-items: baseline;
                    justify-content: space-between;
                    border-bottom: 1px solid var(--border-color-lighter);
                    padding-bottom: var(--spacing-small);
                    margin-bottom: var(--spacing-small);

                    .trend-card__title {
                        font-size: var(--font-size-large);
                        font-weight: var(--font-weight-semibold);
                        color: var(--text-primary);
                    }

                    .trend-card__unit {
                        color: var(--text-tertiary);
                        font-size: var(--font-size-small);
                        font-weight: var(--font-weight-medium);
                    }
                }

                &__body {
                    display: flex;
                    flex-direction: column;
                    gap: var(--spacing-small);
                }

                &__empty {
                    color: var(--text-tertiary);
                    font-size: var(--font-size-small);
                    text-align: center;
                    padding: var(--spacing-large) 0;
                }

                .trend-row {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    color: var(--text-secondary);
                    padding: var(--spacing-xs) 0;
                    border-bottom: 1px solid var(--border-color-extra-light);

                    &:last-child {
                        border-bottom: none;
                    }
                }

                .trend-row .label {
                    font-weight: var(--font-weight-medium);
                    font-size: var(--font-size-small);
                    min-width: 60px;
                }

                .trend-row .value {
                    color: var(--text-primary);
                    font-weight: var(--font-weight-medium);
                    text-align: right;
                }

                .trend-row .value.up {
                    color: var(--danger-color);
                }

                .trend-row .value.down {
                    color: var(--success-color);
                }

                .trend-row .value.flat {
                    color: var(--text-secondary);
                }
            }
        }
    }
}

.dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: var(--spacing-base);

    .dialog-btn {
        padding: var(--spacing-small) var(--spacing-large);
        border: none;
        border-radius: var(--border-radius-base);
        font-size: var(--font-size-small);
        font-weight: var(--font-weight-medium);
        cursor: pointer;
        transition: all 0.2s ease;

        &--primary {
            background: var(--primary-color);
            color: white;

            &:hover {
                background: var(--primary-dark);
                transform: translateY(-1px);
                box-shadow: 0 4px 12px rgba(22, 93, 255, 0.3);
            }
        }
    }
}
</style>