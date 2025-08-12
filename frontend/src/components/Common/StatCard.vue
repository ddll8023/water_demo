<!-- 统计卡片组件 - 用于显示统计数据和指标 -->
<template>
  <div class="stat-card" :class="cardClass" @click="handleClick">
    <div class="card-content">
      <!-- 卡片头部 -->
      <div class="card-header" v-if="showHeader">
        <div class="header-left">
          <div class="card-icon" v-if="icon || $slots.icon" :style="{ backgroundColor: iconBgColor || undefined }">
            <slot name="icon">
              <i class="fa" :class="[icon, iconClass]" :style="{
                fontSize: iconSize + 'px',
                color: iconColor || undefined
              }"></i>
            </slot>
          </div>
          <div class="card-title">
            <h4 class="title-text">{{ title }}</h4>
            <p class="title-desc" v-if="description">{{ description }}</p>
          </div>
        </div>
        <div class="header-right" v-if="$slots.extra">
          <slot name="extra" />
        </div>
      </div>

      <!-- 卡片主体 -->
      <div class="card-body">
        <!-- 主要数值 -->
        <div class="main-value">
          <span class="value-number" :class="valueClass">
            <CountUp v-if="animate && typeof value === 'number'" :end-val="value" :duration="animateDuration"
              :options="countUpOptions" />
            <span v-else>{{ formattedValue }}</span>
          </span>
          <span class="value-unit" v-if="unit">{{ unit }}</span>
        </div>

        <!-- 副标题 -->
        <div class="sub-title" v-if="subtitle">
          {{ subtitle }}
        </div>

        <!-- 趋势指示器 -->
        <div class="trend-indicator" v-if="showTrend && (trend || change)">
          <i class="fa trend-icon" :class="[trendIcon, trendIconClass]"></i>
          <span class="trend-text" :class="trendTextClass">
            {{ change || trendText || (typeof trend === 'number' ? Math.abs(trend) + '%' : '') }}
          </span>
        </div>

        <!-- 进度条 -->
        <div class="progress-section" v-if="showProgress">
          <el-progress :percentage="progressPercentage" :stroke-width="progressStrokeWidth"
            :show-text="showProgressText" :color="progressColor" :status="progressStatus" />
          <div class="progress-info" v-if="progressInfo">
            <span class="progress-current">{{ progressCurrent }}</span>
            <span class="progress-total">/ {{ progressTotal }}</span>
          </div>
        </div>

        <!-- 额外内容 -->
        <div class="extra-content" v-if="$slots.content">
          <slot name="content" :value="value" :trend="trend" />
        </div>
      </div>

      <!-- 卡片底部 -->
      <div class="card-footer" v-if="$slots.footer || footerText">
        <slot name="footer">
          <div class="footer-text">{{ footerText }}</div>
        </slot>
      </div>
    </div>

    <!-- 加载状态 -->
    <div class="card-loading" v-if="loading">
      <i class="fa fa-spinner fa-spin loading-icon"></i>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'

/**
 * ------------------------------------------------
 * 组件定义
 * ------------------------------------------------
 */

// 模拟CountUp组件（实际项目中可以安装vue-countup-v3）
const CountUp = {
  props: ['endVal', 'duration', 'options'],
  setup(props) {
    const displayValue = ref(0)

    // 简单的数字动画实现
    const animate = () => {
      const start = 0
      const end = props.endVal
      const duration = props.duration || 2000
      const startTime = Date.now()

      const update = () => {
        const elapsed = Date.now() - startTime
        const progress = Math.min(elapsed / duration, 1)
        displayValue.value = Math.floor(start + (end - start) * progress)

        if (progress < 1) {
          requestAnimationFrame(update)
        }
      }

      requestAnimationFrame(update)
    }

    animate()

    return () => displayValue.value
  }
}

/**
 * ------------------------------------------------
 * Props定义
 * ------------------------------------------------
 */
const props = defineProps({
  // 卡片标题
  title: {
    type: String,
    required: true
  },
  // 卡片描述
  description: {
    type: String,
    default: ''
  },
  // 主要数值
  value: {
    type: [Number, String],
    required: true
  },
  // 数值单位
  unit: {
    type: String,
    default: ''
  },
  // 副标题
  subtitle: {
    type: String,
    default: ''
  },
  // 图标
  icon: {
    type: [String, Object],
    default: null
  },
  // 图标大小
  iconSize: {
    type: [String, Number],
    default: 24
  },
  // 卡片类型
  type: {
    type: String,
    default: 'default',
    validator: (value) => ['default', 'primary', 'success', 'warning', 'danger', 'info'].includes(value)
  },
  // 兼容Business版本的color属性
  color: {
    type: String,
    default: ''
  },
  // 卡片大小
  size: {
    type: String,
    default: 'default',
    validator: (value) => ['small', 'default', 'large'].includes(value)
  },
  // 是否显示头部
  showHeader: {
    type: Boolean,
    default: true
  },
  // 是否显示趋势
  showTrend: {
    type: Boolean,
    default: false
  },
  // 趋势类型 (兼容Business版本的数字类型)
  trend: {
    type: [String, Number],
    default: 'up'
  },

  // 趋势文本
  trendText: {
    type: String,
    default: ''
  },
  // 是否显示进度条
  showProgress: {
    type: Boolean,
    default: false
  },
  // 进度百分比
  progressPercentage: {
    type: Number,
    default: 0
  },
  // 进度条宽度
  progressStrokeWidth: {
    type: Number,
    default: 4
  },
  // 是否显示进度文字
  showProgressText: {
    type: Boolean,
    default: false
  },
  // 进度条颜色
  progressColor: {
    type: [String, Array, Function],
    default: ''
  },
  // 进度状态
  progressStatus: {
    type: String,
    default: ''
  },
  // 进度信息
  progressInfo: {
    type: Boolean,
    default: false
  },
  // 当前进度值
  progressCurrent: {
    type: [Number, String],
    default: 0
  },
  // 总进度值
  progressTotal: {
    type: [Number, String],
    default: 100
  },
  // 底部文本
  footerText: {
    type: String,
    default: ''
  },
  // 是否加载中
  loading: {
    type: Boolean,
    default: false
  },
  // 是否可点击
  clickable: {
    type: Boolean,
    default: false
  },
  // 是否启用数字动画
  animate: {
    type: Boolean,
    default: false
  },
  // 动画持续时间
  animateDuration: {
    type: Number,
    default: 2000
  },
  // CountUp配置选项
  countUpOptions: {
    type: Object,
    default: () => ({})
  },
  // 兼容Business版本的图标属性
  iconColor: {
    type: String,
    default: ''
  },
  iconBgColor: {
    type: String,
    default: ''
  },
  // 兼容Business版本的change属性
  change: {
    type: String,
    default: ''
  },
  // 兼容Business版本的precision属性
  precision: {
    type: Number,
    default: 0
  }
})

/**
 * ------------------------------------------------
 * Emits定义
 * ------------------------------------------------
 */
const emit = defineEmits(['click'])

/**
 * ------------------------------------------------
 * 计算属性
 * ------------------------------------------------
 */
// 卡片类名计算属性
const cardClass = computed(() => {
  // 优先使用color属性，如果没有则使用type属性
  const cardType = props.color || props.type
  return [
    `stat-card--${cardType}`,
    `stat-card--${props.size}`,
    {
      'stat-card--clickable': props.clickable,
      'stat-card--loading': props.loading
    }
  ]
})

// 图标类名计算属性
const iconClass = computed(() => {
  const cardType = props.color || props.type
  return `icon--${cardType}`
})

// 数值类名计算属性
const valueClass = computed(() => {
  const cardType = props.color || props.type
  return `value--${cardType}`
})

// 格式化数值计算属性
const formattedValue = computed(() => {
  if (typeof props.value === 'number') {
    // 格式化数字，添加千分位分隔符，支持precision属性
    return props.value.toLocaleString('zh-CN', {
      minimumFractionDigits: props.precision,
      maximumFractionDigits: props.precision
    })
  }
  return props.value
})

// 趋势图标计算属性
const trendIcon = computed(() => {
  // 兼容Business版本的数字类型trend
  if (typeof props.trend === 'number') {
    if (props.trend > 0) return 'fa-arrow-up'
    if (props.trend < 0) return 'fa-arrow-down'
    return 'fa-minus'
  }

  switch (props.trend) {
    case 'up':
      return 'fa-arrow-up'
    case 'down':
      return 'fa-arrow-down'
    case 'flat':
    case 'stable':
      return 'fa-minus'
    default:
      return 'fa-line-chart'
  }
})

// 标准化趋势类型计算属性
const normalizedTrendType = computed(() => {
  if (typeof props.trend === 'number') {
    return props.trend > 0 ? 'up' : props.trend < 0 ? 'down' : 'flat'
  }
  return props.trend
})

// 趋势图标类名计算属性
const trendIconClass = computed(() => {
  return [
    'trend-icon',
    `trend-icon--${normalizedTrendType.value}`
  ]
})

// 趋势文本类名计算属性
const trendTextClass = computed(() => {
  return [
    'trend-text',
    `trend-text--${normalizedTrendType.value}`
  ]
})

/**
 * ------------------------------------------------
 * 事件处理方法
 * ------------------------------------------------
 */
// 卡片点击处理方法
const handleClick = () => {
  if (props.clickable && !props.loading) {
    emit('click')
  }
}
</script>

<style scoped lang="scss">
/**
 * ------------------------------------------------
 * 卡片基础样式
 * ------------------------------------------------
 */
.stat-card {
  position: relative;
  background: var(--el-bg-color);
  border: 1px solid var(--el-border-color-light);
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;

  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }

  &--clickable {
    cursor: pointer;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
    }
  }

  &--loading {
    pointer-events: none;
  }

  /**
   * ------------------------------------------------
   * 卡片类型样式
   * ------------------------------------------------
   */
  &--primary {
    border-color: var(--el-color-primary-light-7);

    .card-icon .icon--primary {
      color: var(--el-color-primary);
    }

    .value--primary {
      color: var(--el-color-primary);
    }
  }

  &--success {
    border-color: var(--el-color-success-light-7);

    .card-icon .icon--success {
      color: var(--el-color-success);
    }

    .value--success {
      color: var(--el-color-success);
    }
  }

  &--warning {
    border-color: var(--el-color-warning-light-7);

    .card-icon .icon--warning {
      color: var(--el-color-warning);
    }

    .value--warning {
      color: var(--el-color-warning);
    }
  }

  &--danger {
    border-color: var(--el-color-danger-light-7);

    .card-icon .icon--danger {
      color: var(--el-color-danger);
    }

    .value--danger {
      color: var(--el-color-danger);
    }
  }

  &--info {
    border-color: var(--el-color-info-light-7);

    .card-icon .icon--info {
      color: var(--el-color-info);
    }

    .value--info {
      color: var(--el-color-info);
    }
  }

  /**
   * ------------------------------------------------
   * 卡片大小样式
   * ------------------------------------------------
   */
  &--small {
    .card-content {
      padding: 16px;
    }

    .main-value .value-number {
      font-size: 24px;
    }
  }

  &--large {
    .card-content {
      padding: 32px;
    }

    .main-value .value-number {
      font-size: 36px;
    }
  }

  /**
   * ------------------------------------------------
   * 卡片内容区域样式
   * ------------------------------------------------
   */
  .card-content {
    padding: 24px;
    position: relative;
    z-index: 1;
  }

  /**
   * ------------------------------------------------
   * 卡片头部样式
   * ------------------------------------------------
   */
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 16px;

    .header-left {
      display: flex;
      align-items: center;
      gap: 12px;
    }

    .card-icon {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 48px;
      height: 48px;
      border-radius: 8px;
      background: var(--el-bg-color-page);
    }

    .card-title {
      .title-text {
        margin: 0;
        font-size: 16px;
        font-weight: 500;
        color: var(--el-text-color-primary);
        line-height: 1.4;
      }

      .title-desc {
        margin: 4px 0 0 0;
        font-size: 12px;
        color: var(--el-text-color-secondary);
        line-height: 1.4;
      }
    }
  }

  /**
   * ------------------------------------------------
   * 卡片主体样式
   * ------------------------------------------------
   */
  .card-body {
    .main-value {
      display: flex;
      align-items: baseline;
      gap: 4px;
      margin-bottom: 8px;

      .value-number {
        font-size: 32px;
        font-weight: 600;
        color: var(--el-text-color-primary);
        line-height: 1;
      }

      .value-unit {
        font-size: 14px;
        color: var(--el-text-color-secondary);
      }
    }

    .sub-title {
      font-size: 14px;
      color: var(--el-text-color-secondary);
      margin-bottom: 12px;
    }

    .trend-indicator {
      display: flex;
      align-items: center;
      gap: 4px;
      margin-bottom: 12px;

      .trend-icon {
        font-size: 14px;

        &--up {
          color: var(--el-color-success);
        }

        &--down {
          color: var(--el-color-danger);
        }

        &--flat {
          color: var(--el-text-color-secondary);
        }
      }

      .trend-text {
        font-size: 12px;

        &--up {
          color: var(--el-color-success);
        }

        &--down {
          color: var(--el-color-danger);
        }

        &--flat {
          color: var(--el-text-color-secondary);
        }
      }
    }

    .progress-section {
      margin-bottom: 12px;

      .progress-info {
        display: flex;
        justify-content: space-between;
        margin-top: 8px;
        font-size: 12px;
        color: var(--el-text-color-secondary);
      }
    }

    .extra-content {
      margin-top: 12px;
    }
  }

  /**
   * ------------------------------------------------
   * 卡片底部样式
   * ------------------------------------------------
   */
  .card-footer {
    padding-top: 12px;
    border-top: 1px solid var(--el-border-color-lighter);

    .footer-text {
      font-size: 12px;
      color: var(--el-text-color-secondary);
    }
  }

  /**
   * ------------------------------------------------
   * 加载状态样式
   * ------------------------------------------------
   */
  .card-loading {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(255, 255, 255, 0.8);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 2;

    .loading-icon {
      font-size: 24px;
      color: var(--el-color-primary);
      animation: rotate 1s linear infinite;
    }
  }
}

/**
 * ------------------------------------------------
 * 动画效果
 * ------------------------------------------------
 */
@keyframes rotate {
  from {
    transform: rotate(0deg);
  }

  to {
    transform: rotate(360deg);
  }
}
</style>
