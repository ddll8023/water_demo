<template>
  <div class="introduction-page">
    <!-- 页面标题区域 -->
    <PageHeader title="工程简介" icon="fa-info-circle" description="鄂北地区水资源配置工程项目介绍" />

    <!-- 主体内容区域 -->
    <div class="main-content">
      <el-row :gutter="20">
        <!-- 视频展示区域 -->
        <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
          <CustomCard :title="videoInfo.title" :padding="'normal'" :bordered="true" :shadow="false">
            <div class="video-container">
              <video ref="videoPlayer" :src="videoInfo.url" :poster="videoInfo.poster" controls preload="metadata"
                class="video-player" @loadstart="onVideoLoadStart" @loadeddata="onVideoLoaded" @error="onVideoError">
                您的浏览器不支持视频播放
              </video>
            </div>
            <p class="video-description">{{ videoInfo.description }}</p>
          </CustomCard>
        </el-col>

        <!-- 文字介绍区域 -->
        <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
          <div class="text-section">
            <!-- 工程概况 -->
            <div class="card-wrapper">
              <CustomCard title="工程概况" :padding="'normal'" :bordered="true" :shadow="false">
                <p>{{ projectInfo.overview }}</p>
              </CustomCard>
            </div>

            <!-- 工程规模 -->
            <div class="card-wrapper">
              <CustomCard title="工程规模" :padding="'normal'" :bordered="true" :shadow="false">
                <el-row :gutter="16">
                  <el-col :span="8" v-for="stat in projectStats" :key="stat.key">
                    <CustomCard :padding="'small'" :hoverable="false" :bordered="true" :shadow="false">
                      <div class="stat-card">
                        <div class="stat-card__value">{{ stat.value }}<span class="stat-card__unit">{{ stat.unit
                        }}</span>
                        </div>
                        <div class="stat-card__label">{{ stat.label }}</div>
                      </div>
                    </CustomCard>
                  </el-col>
                </el-row>
              </CustomCard>
            </div>

            <!-- 建设意义 -->
            <div class="card-wrapper">
              <CustomCard title="建设意义" :padding="'normal'" :bordered="true" :shadow="false">
                <ul class="significance-list">
                  <li v-for="item in projectSignificance" :key="item">{{ item }}</li>
                </ul>
              </CustomCard>
            </div>

            <!-- 工程特点 -->
            <div class="card-wrapper">
              <CustomCard title="工程特点" :padding="'normal'" :bordered="true" :shadow="false">
                <el-row :gutter="12">
                  <el-col :span="12" v-for="feature in projectFeatures" :key="feature.key">
                    <CustomCard :padding="'small'" :bordered="false" :shadow="false">
                      <div class="feature-content">
                        <div class="feature-icon">{{ feature.icon }}</div>
                        <div class="feature-text">{{ feature.text }}</div>
                      </div>
                    </CustomCard>
                  </el-col>
                </el-row>
              </CustomCard>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import CustomCard from '@/components/Common/CustomCard.vue'
import PageHeader from '@/components/Common/PageHeader.vue'

// 响应式数据
const videoPlayer = ref(null)

// 视频信息
const videoInfo = reactive({
  title: '工程建设纪实',
  description: '全面展示工程建设历程，记录重要建设节点和技术突破',
  url: '/videos/project-overview.mp4',
  poster: '/images/video-poster.jpg'
})

// 项目信息
const projectInfo = reactive({
  overview: '自鄂北总干渠分水竖井后，新建泵站进水闸及提水泵站，经泵站提水后，由管道分水入两河口水库库尾河道，自流至两河口水库，对水库充蓄，在两河口水库右坝头山体顶新建一座水厂，水厂采用浮船取水，水库水经取水口水泵加压至水厂，先流入反应池经投药混凝后再经过沉淀、过滤、消毒流入清水池，清水池出水通过管道自流送至用户，管网按枝状布置，铺设水厂至吴家湾村 268m DN600 PE输配水干管，干管在吴家湾村分为 2支，一支向兴隆村及石桥村方向供水，供水范围为兴隆村、高庙村、红石岗村、小河沟村、九里岗村、双河村、黄家畈村、夹子沟村、塔儿湾居委会、龙头湾村、石桥村，另一支向真武山村及新东村供水，供水范围为槐东村、万店镇居委会、泉水寺村、落天坡村、真武山村、粉铺村、新中村、新东村；真武山村、高庙村、兴隆村、徐家畈村西湾支管尾部、泉水寺村梯子坡支管尾部由于线路较长水头不足增设加压泵站，泉水寺~真武山村沿线 2台，其他各 1台，共计 5台'
})

// 项目统计数据
const projectStats = reactive([
  { key: 'development1', value: '暂未', label: '数据统计', unit: '开发' },
  { key: 'development2', value: '暂未', label: '规模计算', unit: '开发' },
  { key: 'development3', value: '暂未', label: '指标分析', unit: '开发' }
])

// 建设意义
const projectSignificance = reactive([
  '解决鄂北地区长期缺水问题',
  '改善区域生态环境',
  '完善国家水网体系',
  '提升防洪减灾能力'
])

// 工程特点
const projectFeatures = reactive([
  { key: 'feature1', icon: '🚧', text: '暂未开发' },
  { key: 'feature2', icon: '🚧', text: '暂未开发' },
  { key: 'feature3', icon: '🚧', text: '暂未开发' },
  { key: 'feature4', icon: '🚧', text: '暂未开发' }
])

// 系统信息
const systemInfo = reactive({
  version: 'v2.1.0',
  onlineUsers: 28,
  serverTime: '2024-01-15 14:30:25'
})

// 视频事件处理
const onVideoLoadStart = () => {
  // 视频开始加载
}

const onVideoLoaded = () => {
  // 视频加载完成
}

const onVideoError = (error) => {
  console.error('视频加载失败:', error)
  ElMessage.error('视频加载失败，请检查网络连接')
}

// 更新服务器时间
const updateServerTime = () => {
  const now = new Date()
  systemInfo.serverTime = now.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

onMounted(() => {
  // 每秒更新服务器时间
  setInterval(updateServerTime, 1000)
  updateServerTime()
})
</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;

.introduction-page {
  display: flex;
  flex-direction: column;
}



.main-content {
  flex: 1;
  padding: var(--spacing-base) var(--spacing-large);
  background: var(--bg-primary);
  border-radius: var(--border-radius-large);
  box-shadow: var(--shadow-card);
}

.video-container {
  background: #000;
  border-radius: var(--border-radius-large);
  overflow: hidden;
  margin-bottom: var(--spacing-small);

  .video-player {
    width: 100%;
    height: 320px;
    display: block;
    object-fit: cover;
  }
}

.video-description {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
  line-height: var(--line-height-large);
  margin: 0;
}

.significance-list {
  list-style: none;
  padding: 0;
  margin: 0;

  li {
    font-size: var(--font-size-base);
    color: var(--text-secondary);
    line-height: var(--line-height-large);
    margin-bottom: var(--spacing-small);
    position: relative;
    padding-left: var(--spacing-base);

    &::before {
      content: '•';
      color: var(--primary-color);
      position: absolute;
      left: 0;
    }
  }
}

.text-section {
  .card-wrapper {
    margin-bottom: var(--spacing-base);

    &:last-child {
      margin-bottom: 0;
    }
  }
}

.feature-content {
  @include flex-center-y;

  .feature-icon {
    font-size: var(--font-size-large);
    margin-right: var(--spacing-sm);
  }

  .feature-text {
    font-size: var(--font-size-base);
    color: var(--text-secondary);
  }
}

.stat-card {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: var(--spacing-xs);

  &__value {
    font-size: var(--font-size-large);
    font-weight: 700;
    color: var(--text-primary);

    .stat-card__unit {
      margin-left: var(--spacing-mini);
      font-size: var(--font-size-extra-small);
      color: var(--text-disabled);
      font-weight: var(--font-weight-medium);
    }
  }

  &__label {
    font-size: var(--font-size-extra-small);
    color: var(--text-secondary);
  }
}
</style>
