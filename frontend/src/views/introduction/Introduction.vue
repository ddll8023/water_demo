<template>
  <div class="introduction-page">
    <!-- 页面标题区域 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">工程简介</h1>
        <p class="page-subtitle">鄂北地区水资源配置工程项目介绍</p>
      </div>
    </div>

    <!-- 主体内容区域 -->
    <div class="main-content">
      <el-row :gutter="20">
        <!-- 视频展示区域 -->
        <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
          <div class="video-section">
            <div class="video-container">
              <video ref="videoPlayer" :src="videoInfo.url" :poster="videoInfo.poster" controls preload="metadata"
                class="video-player" @loadstart="onVideoLoadStart" @loadeddata="onVideoLoaded" @error="onVideoError">
                您的浏览器不支持视频播放
              </video>
            </div>

            <div class="video-info">
              <h3>{{ videoInfo.title }}</h3>
              <p>{{ videoInfo.description }}</p>
            </div>
          </div>
        </el-col>

        <!-- 文字介绍区域 -->
        <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
          <div class="text-section">
            <!-- 工程概况 -->
            <div class="section-block">
              <h3>工程概况</h3>
              <p>{{ projectInfo.overview }}</p>
            </div>

            <!-- 工程规模 -->
            <div class="section-block">
              <h3>工程规模</h3>
              <el-row :gutter="16" class="stats-row">
                <el-col :span="8" v-for="stat in projectStats" :key="stat.key">
                  <StatCard :title="stat.label" :value="stat.value" :unit="stat.unit" type="primary" size="small"
                    :show-header="false" />
                </el-col>
              </el-row>
            </div>

            <!-- 建设意义 -->
            <div class="section-block">
              <h3>建设意义</h3>
              <ul class="significance-list">
                <li v-for="item in projectSignificance" :key="item">{{ item }}</li>
              </ul>
            </div>

            <!-- 工程特点 -->
            <div class="section-block">
              <h3>工程特点</h3>
              <el-row :gutter="12" class="features-row">
                <el-col :span="12" v-for="feature in projectFeatures" :key="feature.key">
                  <div class="feature-card">
                    <div class="feature-icon">{{ feature.icon }}</div>
                    <div class="feature-text">{{ feature.text }}</div>
                  </div>
                </el-col>
              </el-row>
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
import StatCard from '@/components/Common/StatCard.vue'

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
.introduction-page {
  display: flex;
  flex-direction: column;
}

.page-header {
  padding: 16px 20px;
  background: white;
  border-radius: 8px;
  margin-bottom: 16px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

  .header-content {
    .page-title {
      font-size: 24px;
      font-weight: 600;
      color: #303133;
      margin: 0 0 8px 0;
    }

    .page-subtitle {
      font-size: 14px;
      color: #909399;
      margin: 0;
    }
  }
}

.main-content {
  flex: 1;
  padding: 16px 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.video-section {
  .video-container {
    position: relative;
    width: 100%;
    background: #f5f7fa;
    border-radius: 8px;
    overflow: hidden;
    margin-bottom: 16px;

    .video-player {
      width: 100%;
      height: auto;
      min-height: 300px;
      object-fit: cover;
    }
  }

  .video-info {
    h3 {
      font-size: 18px;
      font-weight: 600;
      color: #303133;
      margin: 0 0 8px 0;
    }

    p {
      font-size: 14px;
      color: #606266;
      line-height: 1.6;
      margin: 0;
    }
  }
}

.text-section {
  .section-block {
    margin-bottom: 20px;

    h3 {
      font-size: 18px;
      font-weight: 600;
      color: #303133;
      margin: 0 0 16px 0;
      border-left: 4px solid #409eff;
      padding-left: 12px;
    }

    p {
      font-size: 14px;
      color: #606266;
      line-height: 1.8;
      margin: 0;
    }
  }

  .stats-row {
    // StatCard组件已替换自定义样式
  }

  .significance-list {
    list-style: none;
    padding: 0;
    margin: 0;

    li {
      font-size: 14px;
      color: #606266;
      line-height: 1.8;
      margin-bottom: 8px;
      position: relative;
      padding-left: 16px;

      &::before {
        content: '•';
        color: #409eff;
        position: absolute;
        left: 0;
      }
    }
  }

  .features-row {
    .feature-card {
      display: flex;
      align-items: center;
      padding: 12px;
      background: #f8f9fa;
      border-radius: 8px;
      border: 1px solid #e4e7ed;
      margin-bottom: 8px;

      .feature-icon {
        font-size: 20px;
        margin-right: 8px;
      }

      .feature-text {
        font-size: 14px;
        color: #303133;
        font-weight: 500;
      }
    }
  }
}

.status-bar {
  display: flex;
  justify-content: center;
  gap: 40px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
  margin-top: 16px;
  font-size: 14px;
  color: #909399;
}

// 响应式设计
@media (max-width: 768px) {

  .main-content {
    padding: 16px;
  }

  .status-bar {
    flex-direction: column;
    gap: 8px;
    text-align: center;
  }

  .stats-row {
    // 响应式布局已由StatCard组件处理
  }
}
</style>
