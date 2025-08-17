<template>
    <div class="amap-container">
        <div ref="mapContainer" class="amap-container__map" :data-canvas-optimize="true"></div>
        <div v-if="loading" class="amap-container__loading">
            <span class="amap-container__loading-icon">🔄</span>
            <span class="amap-container__loading-text">地图加载中...</span>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from "vue";
import AMapLoader from "@amap/amap-jsapi-loader";
import { useDictionary } from "@/composables/useDictionary";
import { useFacilityTypes } from "@/composables/useFacilityTypes";
import {
    FACILITY_TYPE_CONFIG,
    MONITORING_ITEM_CONFIG,
    DEFAULT_DEVICE_ICON,
    getPipelineTypeConfig,
    getWarningIconConfig
} from "@/utils/map/deviceIcon";

// Props定义
const props = defineProps({
    // 设施点位数据
    facilities: {
        type: Array,
        default: () => [],
    },
    // 监测站点数据
    monitoringStations: {
        type: Array,
        default: () => [],
    },
    // 地图中心点
    center: {
        type: Array,
        default: () => [114.3, 30.6], // 湖北省中心坐标
    },
    // 地图缩放级别
    zoom: {
        type: Number,
        default: 8,
    },
    // 地图样式
    mapStyle: {
        type: String,
        default: 'standard', // standard | satellite
    },
    // 显示路网
    showRoadNet: {
        type: Boolean,
        default: false,
    },
    // 显示路况
    showTraffic: {
        type: Boolean,
        default: false,
    },
    // 供水管线数据
    pipelines: {
        type: Array,
        default: () => [],
    },
    // 预警站点数据
    warningStations: {
        type: Array,
        default: () => [],
    },

});

// Events定义
const emit = defineEmits([
    "map-click",
    "map-ready",
    "station-click",
    "device-popup-show",
    "device-popup-hide"
]);

// 常量定义已整合到 @/utils/deviceIconConfig.js 统一管理

// 响应式数据
const mapContainer = ref(null);
const loading = ref(true);
const map = ref(null);
const facilityMarkers = ref([]);
const stationMarkers = ref([]);
const roadNetLayer = ref(null);
const trafficLayer = ref(null);
const satelliteLayer = ref(null);

// 新增：管线和预警站点相关
const pipelinePolylines = ref([]); // 管线多段线对象数组
const warningMarkers = ref([]); // 预警站点标记数组

// 动态缩放相关状态
const currentZoom = ref(8);

// 字典数据
const { getDictData } = useDictionary();
const { loadFacilityTypeMap, getFacilityTypeLabelSync } = useFacilityTypes();
const deviceStatusOptions = ref([]);
const facilityTypeMap = ref({});

// 生命周期 - 挂载时初始化地图
onMounted(async () => {
    await initMap();
    await loadDictionaries();
});

// 生命周期 - 卸载时销毁地图
onUnmounted(() => {
    if (map.value) {
        map.value.destroy();
    }
});

// 监听设施数据变化
watch(
    () => props.facilities,
    (newFacilities) => {
        if (map.value && newFacilities && Array.isArray(newFacilities)) {
            updateFacilityMarkers(newFacilities);
        }
    },
    { deep: true }
);

// 监听监测站点数据变化
watch(
    () => props.monitoringStations,
    (newStations) => {
        if (map.value && newStations && Array.isArray(newStations)) {
            updateStationMarkers(newStations);
        }
    },
    { deep: true }
);

// 监听地图样式变化
watch(
    () => props.mapStyle,
    (newStyle) => {
        if (map.value) {
            updateMapStyle(newStyle);
        }
    }
);

// 监听路网显示状态变化
watch(
    () => props.showRoadNet,
    (show) => {
        if (map.value) {
            toggleRoadNetLayer(show);
        }
    }
);

// 监听路况显示状态变化
watch(
    () => props.showTraffic,
    (show) => {
        if (map.value) {
            toggleTrafficLayer(show);
        }
    }
);

// 监听管线数据变化
watch(
    () => props.pipelines,
    (newPipelines) => {
        if (map.value && newPipelines) {
            createPipelinePolylines(newPipelines);
        }
    },
    { deep: true }
);

// 监听预警站点数据变化
watch(
    () => props.warningStations,
    (newWarnings) => {
        if (map.value && newWarnings) {
            createWarningMarkers(newWarnings);
        }
    },
    { deep: true }
);

/**
 * 初始化高德地图
 */
const initMap = async () => {
    try {
        loading.value = true;

        // 加载高德地图JS API
        const AMap = await AMapLoader.load({
            key: import.meta.env.VITE_AMAP_KEY, // 需要在.env文件中配置
            version: "2.0",
            plugins: ["AMap.Scale", "AMap.ToolBar", "AMap.MapType", "AMap.TileLayer"]
        });

        await nextTick();

        // 创建地图实例
        map.value = new AMap.Map(mapContainer.value, {
            zoom: props.zoom,
            center: props.center,
            mapStyle: "amap://styles/normal", // 始终使用标准样式作为底图
            // Canvas性能优化配置
            resizeEnable: true,
            rotateEnable: false,
            pitchEnable: false,
            dragEnable: true,
            zoomEnable: true,
            doubleClickZoom: false,
            keyboardEnable: false,
            jogEnable: false,
            animateEnable: true,
            // 图层渲染优化
            layers: [],
            features: ['bg', 'road', 'building', 'point'],
            // Canvas willReadFrequently优化
            canvas: {
                willReadFrequently: true
            }
        });

        // 添加地图控件
        map.value.addControl(new AMap.Scale()); // 比例尺
        map.value.addControl(
            new AMap.ToolBar({
                position: "RT", // 右上角
            })
        ); // 工具条

        // 绑定地图事件
        map.value.on("click", handleMapClick);
        map.value.on("complete", handleMapComplete);
        map.value.on("zoomchange", handleZoomChange);
        map.value.on("moveend", handleMapMoveEnd);
        map.value.on("zoomend", handleMapZoomEnd);

        // 全局点击监听器 - 处理marker点击事件
        document.addEventListener('click', (e) => {
            const markerElement = e.target.closest('.amap-marker');
            if (markerElement) {
                const markerId = markerElement.getAttribute('data-marker-id');

                // 查找对应的marker和数据
                const allMarkers = [...facilityMarkers.value, ...stationMarkers.value];
                const targetMarker = allMarkers.find(marker => {
                    const extData = marker.getExtData();
                    return extData?.markerId === markerId;
                });

                if (targetMarker) {
                    const extData = targetMarker.getExtData();
                    const item = extData?.data;
                    const type = extData?.type;

                    if (item && type) {
                        // 获取marker在地图上的像素坐标
                        const containerPosition = map.value.lngLatToContainer(targetMarker.getPosition());
                        if (containerPosition && containerPosition.x >= 0 && containerPosition.y >= 0) {
                            // 获取当前marker的尺寸
                            const markerSize = calculateIconSize(currentZoom.value, type);
                            // 计算当前marker边框宽度（与图标渲染一致）
                            const markerBorderWidth = markerSize <= 15 ? 1 : (markerSize <= 30 ? 2 : 3);

                            // 触发弹窗显示事件
                            emit('device-popup-show', {
                                device: item,
                                position: {
                                    x: containerPosition.x,
                                    y: containerPosition.y
                                },
                                type,
                                markerSize: markerSize,
                                markerBorderWidth: markerBorderWidth,
                                deviceStatusOptions: deviceStatusOptions.value
                            });

                            // 阻止事件冒泡
                            e.stopPropagation();
                        }
                    }
                }
            }
        });

        // 获取初始缩放级别
        currentZoom.value = map.value.getZoom();

        // 初始化图层
        await initMapLayers();

        // 初始化特殊图层
        await initSpecialLayers();

        loading.value = false;
        emit("map-ready", map.value);
    } catch (error) {
        console.error("地图初始化失败:", error);
        loading.value = false;
    }
};

/**
 * 加载字典数据
 */
const loadDictionaries = async () => {
    try {
        deviceStatusOptions.value = await getDictData('device_status');
        // 加载设施类型映射
        facilityTypeMap.value = await loadFacilityTypeMap();
    } catch (error) {
        console.error('加载字典数据失败:', error);
        deviceStatusOptions.value = [];
        facilityTypeMap.value = {};
    }
};

/**
 * 初始化地图图层
 */
const initMapLayers = async () => {
    await nextTick();

    // 渲染初始数据
    if (props.facilities.length > 0) {
        updateFacilityMarkers(props.facilities);
    }

    if (props.monitoringStations.length > 0) {
        updateStationMarkers(props.monitoringStations);
    }
};

/**
 * 初始化特殊图层（卫星、路网、路况）
 */
const initSpecialLayers = async () => {
    // 基础图层配置
    const baseTileLayerConfig = {
        tileSize: 256,
        opacity: 1,
        retinaFlag: true
    };

    // 图层配置定义
    const layerConfigs = [
        {
            type: AMap.TileLayer.Satellite,
            ref: satelliteLayer,
            config: { zIndex: 1, ...baseTileLayerConfig },
            showCondition: () => props.mapStyle === 'satellite'
        },
        {
            type: AMap.TileLayer.RoadNet,
            ref: roadNetLayer,
            config: { zIndex: 10, ...baseTileLayerConfig },
            showCondition: () => props.showRoadNet
        },
        {
            type: AMap.TileLayer.Traffic,
            ref: trafficLayer,
            config: {
                zIndex: 11,
                ...baseTileLayerConfig,
                autoRefresh: true,
                interval: 180 // 3分钟刷新一次路况
            },
            showCondition: () => props.showTraffic
        }
    ];

    // 批量创建和显示图层
    layerConfigs.forEach(({ type, ref, config, showCondition }) => {
        ref.value = new type(config);
        if (showCondition()) {
            map.value.add(ref.value);
        }
    });
};

/**
 * 通用标记更新函数
 */
const updateMarkers = (items, type) => {
    clearMarkers(type);

    const markersArray = type === 'facility' ? facilityMarkers : stationMarkers;

    items.forEach((item) => {
        if (item.longitude && item.latitude) {
            // 先创建marker（不绑定事件）
            const marker = createMarkerWithoutEvents(item, type);
            markersArray.value.push(marker);

            try {
                // 先添加到地图
                map.value.add(marker);

                // 添加到地图后再绑定事件
                bindMarkerEvents(marker, item, type);

            } catch (error) {
                console.error(`添加Marker到地图失败:`, error);
            }
        }
    });
};

/**
 * 创建不带事件的标记
 */
const createMarkerWithoutEvents = (item, type) => {
    const iconConfig = getItemIconConfig(item);
    iconConfig.type = type;

    // 获取名称和ID的通用逻辑
    const getName = () => {
        if (type === 'facility') return item.name;
        return item.stationName || item.name;
    };

    const getId = () => {
        if (type === 'facility') return item.id || item.name;
        return item.stationId || item.stationName || item.name;
    };

    const name = getName();
    const markerId = `${type}_${getId()}_${item.longitude}_${item.latitude}`;

    // 根据当前缩放级别计算图标尺寸
    const iconSize = calculateIconSize(currentZoom.value, type);
    const htmlContent = createIconHtml(iconConfig, iconSize, name, markerId);

    const marker = new AMap.Marker({
        position: [item.longitude, item.latitude],
        content: htmlContent,
        title: name,
        offset: new AMap.Size(-iconSize / 2, -iconSize / 2),
        zIndex: 100,
        extData: { type: type, data: item, markerId: markerId },
    });

    return marker;
};

/**
 * 绑定标记事件
 */
const bindMarkerEvents = (marker, item, type) => {
    const extData = marker.getExtData();
    const markerId = extData?.markerId;
    const name = type === 'facility' ? item.name : (item.stationName || item.name);

    // 绑定点击事件（添加去重逻辑）
    let lastClickTime = 0;

    // 事件处理函数
    const handleClick = (e, eventType = 'AMap') => {
        const currentTime = Date.now();
        if (currentTime - lastClickTime < 200) {
            return;
        }
        lastClickTime = currentTime;

        // 计算标记在容器中的像素坐标
        const markerPosition = marker.getPosition();
        const containerPosition = map.value.lngLatToContainer(markerPosition);

        // 验证坐标转换结果
        if (!containerPosition || containerPosition.x === undefined || containerPosition.y === undefined) {
            console.warn('坐标转换失败，无法显示弹窗');
            return;
        }

        // 获取当前marker的尺寸
        const markerSize = calculateIconSize(currentZoom.value, type);
        // 计算当前marker边框宽度（与图标渲染一致）
        const markerBorderWidth = markerSize <= 15 ? 1 : (markerSize <= 30 ? 2 : 3);

        // 发送弹窗显示事件
        emit("device-popup-show", {
            device: item,
            position: {
                x: containerPosition.x,
                y: containerPosition.y
            },
            markerType: type,
            markerSize: markerSize,
            markerBorderWidth: markerBorderWidth,
            deviceStatusOptions: deviceStatusOptions.value
        });
    };

    try {
        // 高德地图API事件
        marker.on("click", (e) => handleClick(e, 'AMap'));

        // DOM事件作为备用（延迟绑定确保DOM已渲染）
        setTimeout(() => {
            try {
                const markerElement = document.querySelector(`[data-marker-id="${markerId}"]`);
                if (markerElement) {
                    markerElement.addEventListener('click', (e) => {
                        e.stopPropagation(); // 防止冒泡到地图
                        handleClick(e, 'DOM');
                    });
                }
            } catch (domError) {
                console.error(`DOM事件绑定失败:`, markerId, domError);
            }
        }, 500);

    } catch (error) {
        console.error(`事件绑定失败:`, markerId, error);
    }
};

/**
 * 更新设施标记点 - 使用通用函数
 */
const updateFacilityMarkers = (facilities) => {
    updateMarkers(facilities, 'facility');
};

/**
 * 更新监测站点标记 - 使用通用函数
 */
const updateStationMarkers = (stations) => {
    updateMarkers(stations, 'station');
};

/**
 * 统一的图标配置获取函数（精确分类版本）
 * 现在使用 @/utils/deviceIconConfig.js 中的统一配置
 */
const getItemIconConfig = (item) => {
    // 1. 优先检查是否为真正的监测站（有监测站特有字段）
    if ((item.stationName || item.stationId) && item.monitoringItem) {
        return MONITORING_ITEM_CONFIG[item.monitoringItem] || DEFAULT_DEVICE_ICON;
    }

    // 2. 检查是否为设施（有type字段且不是monitoring_station类型，或者是但没有监测项）
    if (item.type && item.type !== 'monitoring_station') {
        return FACILITY_TYPE_CONFIG[item.type] || DEFAULT_DEVICE_ICON;
    }

    // 3. 对于type为monitoring_station但没有实际监测项的数据，当作设施处理
    if (item.type === 'monitoring_station' && !item.monitoringItem) {
        // 根据名称特征判断实际类型
        const name = (item.name || '').toLowerCase();
        if (name.includes('水库')) {
            return FACILITY_TYPE_CONFIG['reservoir'] || DEFAULT_DEVICE_ICON;
        } else if (name.includes('水厂')) {
            return FACILITY_TYPE_CONFIG['water_plant'] || DEFAULT_DEVICE_ICON;
        } else if (name.includes('流量')) {
            return FACILITY_TYPE_CONFIG['pipeline'] || DEFAULT_DEVICE_ICON;
        }
        // 默认作为监测站设施处理
        return FACILITY_TYPE_CONFIG['monitoring_station'] || DEFAULT_DEVICE_ICON;
    }

    // 4. 兜底逻辑
    return DEFAULT_DEVICE_ICON;
};

/**
 * 创建图标HTML内容 - 使用内联样式确保尺寸正确应用
 */
const createIconHtml = (iconConfig, size, title, markerId = '') => {
    // 从CSS变量获取基础配置值，确保与样式系统统一
    const rootStyles = getComputedStyle(document.documentElement);
    const fontSizeRatio = parseFloat(rootStyles.getPropertyValue('--marker-font-size-ratio').trim()) || 0.65;

    // 精确的尺寸计算
    const borderWidth = size <= 15 ? 1 : size <= 30 ? 2 : 3;
    const fontSize = Math.max(Math.round(size * fontSizeRatio), 10);

    // 构建CSS类名
    const markerType = iconConfig.type || 'facility';
    const cssClasses = [
        'amap-marker',
        `amap-marker--${markerType}`,
    ].filter(Boolean).join(' ');

    // 动态样式通过CSS自定义属性传递
    const dynamicStyle = [
        `--marker-size: ${size}px`,
        `--marker-font-size: ${fontSize}px`,
        `--marker-border-width: ${borderWidth}px`,
        `--marker-border-color: ${iconConfig.color}`,
        `--marker-bg-color: ${iconConfig.bgColor}`,
        `--marker-color: ${iconConfig.color}`,
        // pointer-events 必须在此处设置以确保事件可被捕获
        `pointer-events: auto`
    ].join('; ');

    const htmlContent = `
        <div class="${cssClasses}" 
             data-marker-id="${markerId}" 
             data-marker-type="${markerType}"
             title="${title}"
             style="${dynamicStyle}">
            <div class="amap-marker__icon">
                ${iconConfig.symbol}
            </div>
        </div>
    `;

    return htmlContent;
};

/**
 * 通用清除标记函数
 */
const clearMarkers = (type) => {
    const markersArray = type === 'facility' ? facilityMarkers : stationMarkers;

    markersArray.value.forEach((marker) => {
        map.value.remove(marker);
    });
    markersArray.value = [];
};

/**
 * 清除设施标记 - 使用通用函数
 */
const clearFacilityMarkers = () => {
    clearMarkers('facility');
};

/**
 * 清除监测站点标记 - 使用通用函数
 */
const clearStationMarkers = () => {
    clearMarkers('station');
};

/**
 * 地图点击事件处理
 */
const handleMapClick = (e) => {
    // 发送地图点击事件（用于隐藏弹窗）
    emit("device-popup-hide");

    emit("map-click", {
        longitude: e.lnglat.lng,
        latitude: e.lnglat.lat,
    });
};

/**
 * 地图加载完成事件处理
 */
const handleMapComplete = () => {
    const facilityCount = facilityMarkers.value.length;
    const stationCount = stationMarkers.value.length;

    // 如果标记数量为0，尝试重新加载数据
    if (facilityCount === 0 && stationCount === 0) {
        setTimeout(() => {
            if (props.facilities.length > 0) {
                updateFacilityMarkers(props.facilities);
            }
            if (props.monitoringStations.length > 0) {
                updateStationMarkers(props.monitoringStations);
            }
        }, 500);
    }
};

/**
 * 处理地图缩放变化事件
 */
const handleZoomChange = () => {
    const newZoom = map.value.getZoom();
    currentZoom.value = newZoom;

    // 立即直接更新，无需防抖
    updateAllMarkersSize();
};

/**
 * 处理地图移动结束事件
 */
const handleMapMoveEnd = () => {
    // 地图移动时更新弹窗位置
    emit("device-popup-hide");
};

/**
 * 处理地图缩放结束事件
 */
const handleMapZoomEnd = () => {
    // 地图缩放时更新弹窗位置
    emit("device-popup-hide");
};

/**
 * 根据缩放级别和图标类型计算合适的尺寸
 * @param {number} zoomLevel - 地图缩放级别
 * @param {string} iconType - 图标类型 'facility' | 'station'
 * @returns {number} 计算得到的图标尺寸
 */
const calculateIconSize = (zoomLevel, iconType) => {
    // 减小基础尺寸，让图标更适中
    const baseSize = 16;
    const baseZoom = 8; // 基准缩放级别

    // 减小缩放因子：每级缩放级别1.2倍变化，避免图标过大
    const zoomFactor = Math.pow(1.3, zoomLevel - baseZoom);
    const calculatedSize = Math.round(baseSize * zoomFactor);

    // 减小尺寸范围：最小10px，最大32px，保持合适的视觉效果
    return Math.max(10, Math.min(40, calculatedSize));
};

/**
 * 批量更新所有图标尺寸
 */
const updateAllMarkersSize = () => {
    // 直接批量更新，去除过度的动画和延迟
    updateMarkersSize('facility');
    updateMarkersSize('station');
};

/**
 * 通用标记尺寸更新函数
 */
const updateMarkersSize = (type) => {
    const newSize = calculateIconSize(currentZoom.value, type);
    const markersArray = type === 'facility' ? facilityMarkers : stationMarkers;

    markersArray.value.forEach((marker) => {
        const extData = marker.getExtData();
        if (extData && extData.data) {
            const item = extData.data;
            const markerId = extData.markerId || '';
            const iconConfig = getItemIconConfig(item);
            iconConfig.type = type;

            // 获取名称的通用逻辑
            const name = type === 'facility' ? item.name : (item.stationName || item.name);
            const newContent = createIconHtml(iconConfig, newSize, name, markerId);

            // 直接同步更新，避免requestAnimationFrame导致的延迟
            marker.setContent(newContent);
            marker.setOffset(new AMap.Size(-newSize / 2, -newSize / 2));
            // 高德地图Marker通过重新设置选项来更新zIndex
            if (marker.setOptions) {
                marker.setOptions({ zIndex: 100 });
            }
        }
    });
};

/**
 * 更新设施标记尺寸 - 使用通用函数
 */
const updateFacilityMarkersSize = () => {
    updateMarkersSize('facility');
};

/**
 * 创建管线多段线
 */
const createPipelinePolylines = (pipelines) => {
    if (!map.value || !Array.isArray(pipelines)) return;

    // 清除现有管线
    clearPipelinePolylines();

    pipelines.forEach((pipeline) => {
        if (!pipeline.coordinates || !Array.isArray(pipeline.coordinates)) return;

        const config = getPipelineTypeConfig(pipeline);

        // 创建多段线
        const polyline = new AMap.Polyline({
            path: pipeline.coordinates.map(coord => [coord.lng, coord.lat]),
            strokeColor: config.color,
            strokeWeight: config.width,
            strokeStyle: config.style,
            strokeOpacity: 0.8,
            zIndex: 50,
            extData: {
                type: 'pipeline',
                data: pipeline,
                pipelineType: pipeline.type
            }
        });

        // 添加点击事件
        polyline.on('click', (e) => {
            handlePipelineClick(pipeline, e.lnglat);
        });

        // 添加到地图
        map.value.add(polyline);
        pipelinePolylines.value.push(polyline);
    });
};

/**
 * 清除管线多段线
 */
const clearPipelinePolylines = () => {
    if (pipelinePolylines.value.length > 0) {
        map.value.remove(pipelinePolylines.value);
        pipelinePolylines.value = [];
    }
};

/**
 * 处理管线点击事件
 */
const handlePipelineClick = (pipeline, lnglat) => {
    console.log('Pipeline clicked:', pipeline);
    // 这里可以添加管线点击的具体处理逻辑
    // 比如显示管线信息弹窗等
};

/**
 * 创建预警站点标记
 */
const createWarningMarkers = (warnings) => {
    if (!map.value || !Array.isArray(warnings)) return;

    // 清除现有预警标记
    clearWarningMarkers();

    warnings.forEach((warning) => {
        if (!warning.longitude || !warning.latitude) return;

        const config = getWarningIconConfig(warning);

        const iconSize = calculateIconSize(currentZoom.value, 'warning');
        const markerId = `warning_${warning.id || Math.random()}`;

        const iconHtml = createWarningIconHtml(config, iconSize, warning.name || '预警点', markerId);

        const marker = new AMap.Marker({
            position: [warning.longitude, warning.latitude],
            content: iconHtml,
            offset: new AMap.Size(-iconSize / 2, -iconSize / 2),
            zIndex: 150,
            extData: {
                type: 'warning',
                data: warning,
                markerId: markerId,
                level: warning.level
            }
        });

        // 添加点击事件
        marker.on('click', (e) => {
            handleWarningClick(warning, e.target.getPosition());
        });

        map.value.add(marker);
        warningMarkers.value.push(marker);
    });
};

/**
 * 清除预警站点标记
 */
const clearWarningMarkers = () => {
    if (warningMarkers.value.length > 0) {
        map.value.remove(warningMarkers.value);
        warningMarkers.value = [];
    }
};

/**
 * 处理预警站点点击事件
 */
const handleWarningClick = (warning, position) => {
    console.log('Warning station clicked:', warning);
    // 发送预警点击事件给父组件
    emit('station-click', warning, {
        x: position.lng,
        y: position.lat
    });
};

/**
 * 创建预警图标HTML - 使用内联样式确保尺寸正确应用
 */
const createWarningIconHtml = (iconConfig, size, name, markerId) => {
    // 计算相关数值
    const borderWidth = 2;
    const fontSize = Math.max(Math.round(size * 0.5), 12);

    // 构建CSS类名
    const cssClasses = [
        'amap-marker',
        'amap-marker--warning',
        iconConfig.level > 1 ? 'amap-marker--warning-urgent' : '',
    ].filter(Boolean).join(' ');

    // 动态样式通过CSS自定义属性传递
    const dynamicStyle = [
        `--marker-size: ${size}px`,
        `--marker-font-size: ${fontSize}px`,
        `--marker-border-width: ${borderWidth}px`,
        `--marker-border-color: ${iconConfig.color}`,
        `--marker-bg-color: ${iconConfig.bgColor}`,
        `--marker-color: ${iconConfig.color}`
    ].join('; ');

    return `
        <div class="${cssClasses}" 
             data-marker-id="${markerId}" 
             data-marker-type="warning"
             title="${name}"
             style="${dynamicStyle}">
            <div class="amap-marker__icon">
                ${iconConfig.symbol}
            </div>
        </div>
    `;
};

/**
 * 更新监测站标记尺寸 - 使用通用函数
 */
const updateStationMarkersSize = () => {
    updateMarkersSize('station');
};

/**
 * 更新地图样式
 */
const updateMapStyle = (style) => {
    if (!map.value || !satelliteLayer.value) return;

    if (style === 'satellite') {
        // 显示卫星图层
        map.value.add(satelliteLayer.value);
    } else {
        // 隐藏卫星图层，显示标准地图
        map.value.remove(satelliteLayer.value);
    }
};

/**
 * 切换路网图层
 */
const toggleRoadNetLayer = (show) => {
    if (!roadNetLayer.value || !map.value) return;

    if (show) {
        map.value.add(roadNetLayer.value);
    } else {
        map.value.remove(roadNetLayer.value);
    }
};

/**
 * 切换路况图层
 */
const toggleTrafficLayer = (show) => {
    if (!trafficLayer.value || !map.value) return;

    if (show) {
        map.value.add(trafficLayer.value);
    } else {
        map.value.remove(trafficLayer.value);
    }
};

/**
 * 暴露给父组件的方法
 */
defineExpose({
    // 设置地图中心点
    setCenter: (center) => {
        if (map.value) {
            map.value.setCenter(center);
        }
    },
    // 设置地图缩放级别
    setZoom: (zoom) => {
        if (map.value) {
            map.value.setZoom(zoom);
        }
    },
    // 适应显示所有标记
    fitView: () => {
        if (map.value) {
            map.value.setFitView();
        }
    },
    // 获取地图实例
    getMapInstance: () => map.value,
    // 更新地图样式
    updateMapStyle: (style) => updateMapStyle(style),
    // 切换路网图层
    toggleRoadNet: (show) => toggleRoadNetLayer(show),
    // 切换路况图层
    toggleTraffic: (show) => toggleTrafficLayer(show),
    // 管线相关方法
    createPipelinePolylines: createPipelinePolylines,
    clearPipelinePolylines: clearPipelinePolylines,
    // 预警站点相关方法
    createWarningMarkers: createWarningMarkers,
    clearWarningMarkers: clearWarningMarkers,
    // 坐标转换方法
    lngLatToContainer: (lnglat) => {
        if (map.value) {
            return map.value.lngLatToContainer(lnglat);
        }
        return { x: 0, y: 0 };
    }
});
</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;

.amap-container {
    background: var(--bg-primary);
    position: relative;
    width: 100%;
    height: 100%;

    &__map {
        image-rendering: optimizeSpeed;
        image-rendering: crisp-edges;
        width: 100%;
        height: 100%;
    }

    &__loading {
        background: var(--bg-primary);
        padding: var(--spacing-large);
        border-radius: var(--border-radius-large);
        box-shadow: var(--shadow-card);
        z-index: var(--z-index-dropdown);
        opacity: var(--opacity-intense);
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        display: flex;
        align-items: center;
        justify-content: center;
        text-align: center;

        &-icon {
            font-size: var(--font-size-extra-large);
            color: var(--primary-color);
            margin-right: var(--spacing-small);
            display: inline-block;
            animation: amap-spin 1s linear infinite;
        }

        &-text {
            color: var(--text-secondary);
            font-size: var(--font-size-base);
            font-weight: var(--font-weight-medium);
        }
    }
}

/* 地图标记基础样式 */
:deep(.amap-marker) {
    box-sizing: border-box;
    position: relative;
    transform-origin: center center;
    will-change: transform, box-shadow;
    cursor: pointer;
    user-select: none;
    pointer-events: auto !important;
    /* 强制确保可以接收点击事件 */
    z-index: var(--z-index-map-marker);
}

:deep(.amap-marker__icon) {
    width: var(--marker-size);
    height: var(--marker-size);
    font-size: var(--marker-font-size);
    border-width: var(--marker-border-width);
    border-color: var(--marker-border-color);
    background-color: var(--marker-bg-color);
    color: var(--marker-color);
    opacity: var(--opacity-heavy);
    border-radius: var(--border-radius-round);
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: var(--font-weight-medium);
    line-height: 1;
    position: relative;
    border-style: solid;
    box-sizing: border-box;
}

// 悬停状态 - 重叠图标可视性优化
:deep(.amap-marker:hover) {
    z-index: var(--z-index-map-marker-hover);
}

// 激活状态已移除动画效果





// 工具提示
:deep(.amap-marker__tooltip) {
    position: absolute;
    bottom: calc(var(--spacing-base) + var(--spacing-mini));
    left: 50%;
    transform: translateX(-50%);
    background: var(--black-transparent-heavy);
    color: var(--text-on-dark);
    padding: var(--spacing-mini) var(--spacing-small);
    border-radius: var(--border-radius-base);
    font-size: var(--font-size-small);
    white-space: nowrap;
    display: none;
    z-index: var(--z-index-tooltip);
    pointer-events: none;
}

// 响应式适配
@include respond-to(lg) {
    .amap-container {
        &__loading {
            padding: var(--spacing-extra-large);
        }
    }
}

@include respond-to(md) {
    .amap-container {
        &__loading {
            padding: var(--spacing-medium);

            &-icon {
                font-size: var(--font-size-large);
            }
        }
    }
}

@include respond-to(sm) {
    .amap-container {
        &__loading {
            padding: var(--spacing-base);
            border-radius: var(--border-radius-base);

            &-icon {
                font-size: var(--font-size-base);
                margin-right: var(--spacing-mini);
            }

            &-text {
                font-size: var(--font-size-small);
            }
        }
    }
}

@include respond-to(xs) {
    .amap-container {
        &__loading {
            padding: var(--spacing-small);
            margin: 0 var(--spacing-base);
        }
    }
}
</style>