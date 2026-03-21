<template>
  <div class="dashboard-container">
    <div class="page-header">
      <h1>数据仪表盘</h1>
      <p>统揽全局数据，实时掌握业务动态</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <el-card class="stats-card" shadow="hover">
        <div class="card-content">
          <div class="card-icon user-icon">
            <el-icon><User /></el-icon>
          </div>
          <div class="card-info">
            <div class="card-value">{{ formatNumber(dashboardStats.totalUsers) }}</div>
            <div class="card-label">用户总数</div>
          </div>
        </div>
      </el-card>

      <el-card class="stats-card" shadow="hover">
        <div class="card-content">
          <div class="card-icon trend-icon">
            <el-icon><TrendCharts /></el-icon>
          </div>
          <div class="card-info">
            <div class="card-value">{{ formatNumber(dashboardStats.newUsersThisMonth) }}</div>
            <div class="card-label">本月新增</div>
          </div>
        </div>
      </el-card>

      <el-card class="stats-card" shadow="hover">
        <div class="card-content">
          <div class="card-icon order-icon">
            <el-icon><Document /></el-icon>
          </div>
          <div class="card-info">
            <div class="card-value">{{ formatNumber(dashboardStats.totalOrders) }}</div>
            <div class="card-label">订单总数</div>
          </div>
        </div>
      </el-card>

      <el-card class="stats-card" shadow="hover">
        <div class="card-content">
          <div class="card-icon money-icon">
            <el-icon><Money /></el-icon>
          </div>
          <div class="card-info">
            <div class="card-value">¥{{ formatAmount(dashboardStats.totalAmount) }}</div>
            <div class="card-label">总金额</div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 趋势图 -->
    <el-card class="chart-card" shadow="hover">
      <template #header>
        <div class="chart-header">
          <span>最近7天趋势</span>
          <div class="chart-legend">
            <span class="legend-item">
              <span class="legend-dot user-dot"></span>
              新增用户
            </span>
            <span class="legend-item">
              <span class="legend-dot order-dot"></span>
              新增订单
            </span>
          </div>
        </div>
      </template>
      <div ref="chartRef" class="chart-container"></div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { getDashboardStats, getTrendData } from '@/api/statistics'
import {
  User,
  TrendCharts,
  Document,
  Money
} from '@element-plus/icons-vue'

const chartRef = ref<HTMLElement | null>(null)
let chartInstance: echarts.ECharts | null = null

const dashboardStats = ref({
  totalUsers: 0,
  newUsersThisMonth: 0,
  totalOrders: 0,
  totalAmount: 0
})

// 格式化数字
const formatNumber = (num: number) => {
  return num.toLocaleString()
}

// 格式化金额
const formatAmount = (amount: number) => {
  return Number(amount).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

// 初始化图表
const initChart = (trendData: any[]) => {
  if (!chartRef.value) return

  chartInstance = echarts.init(chartRef.value)

  const dates = trendData.map(item => item.date.replace(/^\d{4}-/, '')) // 只显示月-日
  const newUsers = trendData.map(item => item.newUsers)
  const newOrders = trendData.map(item => item.newOrders)

  const option: echarts.EChartsOption = {
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#e5e7eb',
      borderWidth: 1,
      textStyle: {
        color: '#333'
      },
      formatter: (params: any) => {
        let result = `<div style="font-weight: bold; margin-bottom: 8px;">${params[0].axisValue}</div>`
        params.forEach((item: any) => {
          const marker = `<span style="display:inline-block;margin-right:8px;border-radius:50%;width:10px;height:10px;background-color:${item.color};"></span>`
          result += `<div style="margin: 4px 0;">${marker}${item.seriesName}: <strong>${item.value}</strong></div>`
        })
        return result
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
      boundaryGap: false,
      data: dates,
      axisLine: {
        lineStyle: {
          color: '#e5e7eb'
        }
      },
      axisLabel: {
        color: '#6b7280',
        fontSize: 12
      }
    },
    yAxis: {
      type: 'value',
      splitLine: {
        lineStyle: {
          color: '#f3f4f6',
          type: 'dashed'
        }
      },
      axisLine: {
        show: false
      },
      axisTick: {
        show: false
      },
      axisLabel: {
        color: '#6b7280',
        fontSize: 12
      }
    },
    series: [
      {
        name: '新增用户',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        showSymbol: true,
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
            { offset: 1, color: 'rgba(64, 158, 255, 0.02)' }
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
        showSymbol: true,
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
            { offset: 1, color: 'rgba(103, 194, 58, 0.02)' }
          ])
        },
        data: newOrders
      }
    ]
  }

  chartInstance.setOption(option)
}

// 获取数据
const fetchData = async () => {
  try {
    // 获取仪表盘统计
    const statsRes: any = await getDashboardStats()
    dashboardStats.value = statsRes.data

    // 获取趋势数据
    const trendRes: any = await getTrendData()
    initChart(trendRes.data)
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 窗口大小变化时调整图表
const handleResize = () => {
  chartInstance?.resize()
}

onMounted(() => {
  fetchData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
})
</script>

<style scoped>
.dashboard-container {
  padding: var(--spacing-lg);
  max-width: 1400px;
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
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-lg);
}

.stats-card {
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.card-content {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.card-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fff;
}

.user-icon {
  background: linear-gradient(135deg, #409eff, #66b1ff);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
}

.trend-icon {
  background: linear-gradient(135deg, #e6a23c, #f0c78a);
  box-shadow: 0 4px 12px rgba(230, 162, 60, 0.4);
}

.order-icon {
  background: linear-gradient(135deg, #67c23a, #85ce61);
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.4);
}

.money-icon {
  background: linear-gradient(135deg, #f56c6c, #f89898);
  box-shadow: 0 4px 12px rgba(245, 108, 108, 0.4);
}

.card-info {
  flex: 1;
}

.card-value {
  font-size: 32px;
  font-weight: 700;
  color: var(--text-primary);
  line-height: 1.2;
  margin-bottom: 4px;
}

.card-label {
  font-size: 14px;
  color: var(--text-secondary);
}

.chart-card {
  border-radius: var(--radius-lg);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  color: var(--text-primary);
}

.chart-legend {
  display: flex;
  gap: 20px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--text-secondary);
  font-weight: normal;
}

.legend-dot {
  display: inline-block;
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
  height: 400px;
  margin-top: var(--spacing-md);
}

@media (max-width: 1200px) {
  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .stats-cards {
    grid-template-columns: 1fr;
  }
}
</style>