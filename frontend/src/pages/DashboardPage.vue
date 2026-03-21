<template>
  <div class="page-container">
    <div class="page-header">
      <h1>数据仪表盘</h1>
      <p>系统运营数据概览</p>
    </div>

    <div class="stats-cards">
      <el-card
        class="stat-card"
        shadow="hover"
      >
        <div class="stat-content">
          <div class="stat-icon user-icon">
            <el-icon :size="32">
              <User />
            </el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">
              {{ stats.totalUsers }}
            </div>
            <div class="stat-label">
              用户总数
            </div>
          </div>
        </div>
      </el-card>

      <el-card
        class="stat-card"
        shadow="hover"
      >
        <div class="stat-content">
          <div class="stat-icon new-user-icon">
            <el-icon :size="32">
              <UserFilled />
            </el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">
              {{ stats.newUsersThisMonth }}
            </div>
            <div class="stat-label">
              本月新增用户
            </div>
          </div>
        </div>
      </el-card>

      <el-card
        class="stat-card"
        shadow="hover"
      >
        <div class="stat-content">
          <div class="stat-icon order-icon">
            <el-icon :size="32">
              <List />
            </el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">
              {{ stats.totalOrders }}
            </div>
            <div class="stat-label">
              订单总数
            </div>
          </div>
        </div>
      </el-card>

      <el-card
        class="stat-card"
        shadow="hover"
      >
        <div class="stat-content">
          <div class="stat-icon amount-icon">
            <el-icon :size="32">
              <Money />
            </el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">
              {{ formatAmount(stats.totalAmount) }}
            </div>
            <div class="stat-label">
              订单总金额
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <el-card
      class="chart-card"
      shadow="hover"
    >
      <template #header>
        <div class="chart-header">
          <span class="chart-title">最近7天趋势</span>
          <div class="chart-legend">
            <span class="legend-item">
              <span class="legend-dot user-dot" />
              新增用户
            </span>
            <span class="legend-item">
              <span class="legend-dot order-dot" />
              新增订单
            </span>
          </div>
        </div>
      </template>
      <div
        ref="chartRef"
        class="chart-container"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { User, UserFilled, List, Money } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getDashboardStats, getTrendData, type DashboardStats, type TrendData } from '@/api/statistics'

const chartRef = ref<HTMLElement | null>(null)
let chartInstance: echarts.ECharts | null = null

const stats = ref<DashboardStats>({
  totalUsers: 0,
  newUsersThisMonth: 0,
  totalOrders: 0,
  totalAmount: 0
})

const trendData = ref<TrendData[]>([])

const formatAmount = (amount: number) => {
  if (amount >= 10000) {
    return (amount / 10000).toFixed(2) + '万'
  }
  return amount.toFixed(2)
}

const fetchDashboardStats = async () => {
  try {
    const res: any = await getDashboardStats()
    stats.value = res.data
  } catch {
    // error handled in interceptor
  }
}

const fetchTrendData = async () => {
  try {
    const res: any = await getTrendData()
    trendData.value = res.data
    renderChart()
  } catch {
    // error handled in interceptor
  }
}

const renderChart = () => {
  if (!chartRef.value || !trendData.value.length) return

  if (chartInstance) {
    chartInstance.dispose()
  }

  chartInstance = echarts.init(chartRef.value)

  const dates = trendData.value.map(item => item.date.slice(5))
  const newUsers = trendData.value.map(item => item.newUsers)
  const newOrders = trendData.value.map(item => item.newOrders)

  const option: echarts.EChartsOption = {
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#e4e7ed',
      borderWidth: 1,
      textStyle: {
        color: '#303133'
      },
      axisPointer: {
        type: 'cross',
        crossStyle: {
          color: '#999'
        }
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dates,
      axisLine: {
        lineStyle: {
          color: '#dcdfe6'
        }
      },
      axisLabel: {
        color: '#606266'
      }
    },
    yAxis: {
      type: 'value',
      minInterval: 1,
      axisLine: {
        show: false
      },
      axisTick: {
        show: false
      },
      axisLabel: {
        color: '#909399'
      },
      splitLine: {
        lineStyle: {
          color: '#ebeef5',
          type: 'dashed'
        }
      }
    },
    series: [
      {
        name: '新增用户',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        lineStyle: {
          width: 3,
          color: '#409eff'
        },
        itemStyle: {
          color: '#409eff',
          borderWidth: 2,
          borderColor: '#fff'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
          ])
        },
        data: newUsers
      },
      {
        name: '新增订单',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        lineStyle: {
          width: 3,
          color: '#67c23a'
        },
        itemStyle: {
          color: '#67c23a',
          borderWidth: 2,
          borderColor: '#fff'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(103, 194, 58, 0.3)' },
            { offset: 1, color: 'rgba(103, 194, 58, 0.05)' }
          ])
        },
        data: newOrders
      }
    ]
  }

  chartInstance.setOption(option)
}

const handleResize = () => {
  chartInstance?.resize()
}

onMounted(() => {
  fetchDashboardStats()
  fetchTrendData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
})
</script>

<style scoped>
.page-container {
  padding: var(--spacing-lg);
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: var(--spacing-lg);
}

.page-header h1 {
  font-size: 28px;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.page-header p {
  font-size: 14px;
  color: var(--text-secondary);
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
}

@media (max-width: 1024px) {
  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .stats-cards {
    grid-template-columns: 1fr;
  }
}

.stat-card {
  border-radius: 8px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.user-icon {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  color: #fff;
}

.new-user-icon {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
  color: #fff;
}

.order-icon {
  background: linear-gradient(135deg, #e6a23c 0%, #ebb563 100%);
  color: #fff;
}

.amount-icon {
  background: linear-gradient(135deg, #f56c6c 0%, #f78989 100%);
  color: #fff;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: var(--text-primary);
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: var(--text-secondary);
  margin-top: 4px;
}

.chart-card {
  border-radius: 8px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-title {
  font-size: 16px;
  font-weight: 500;
  color: var(--text-primary);
}

.chart-legend {
  display: flex;
  gap: var(--spacing-md);
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--text-secondary);
}

.legend-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.user-dot {
  background-color: #409eff;
}

.order-dot {
  background-color: #67c23a;
}

.chart-container {
  width: 100%;
  height: 350px;
}
</style>
