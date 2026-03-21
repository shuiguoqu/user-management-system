<template>
  <div class="page-container">
    <div class="page-header">
      <h1>数据仪表盘</h1>
      <p>实时监控系统运营数据</p>
    </div>

    <!-- 统计卡片 -->
    <el-row
      :gutter="20"
      class="stat-cards"
    >
      <el-col
        :xs="24"
        :sm="12"
        :lg="6"
      >
        <el-card
          class="stat-card"
          shadow="hover"
        >
          <div class="stat-icon user-icon">
            <el-icon><User /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-label">
              用户总数
            </div>
            <div class="stat-value">
              {{ statistics.totalUsers }}
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col
        :xs="24"
        :sm="12"
        :lg="6"
      >
        <el-card
          class="stat-card"
          shadow="hover"
        >
          <div class="stat-icon month-icon">
            <el-icon><Calendar /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-label">
              本月新增
            </div>
            <div class="stat-value">
              {{ statistics.monthNewUsers }}
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col
        :xs="24"
        :sm="12"
        :lg="6"
      >
        <el-card
          class="stat-card"
          shadow="hover"
        >
          <div class="stat-icon order-icon">
            <el-icon><ShoppingCart /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-label">
              订单总数
            </div>
            <div class="stat-value">
              {{ statistics.totalOrders }}
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col
        :xs="24"
        :sm="12"
        :lg="6"
      >
        <el-card
          class="stat-card"
          shadow="hover"
        >
          <div class="stat-icon amount-icon">
            <el-icon><Money /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-label">
              总金额
            </div>
            <div class="stat-value">
              ¥{{ formatAmount(statistics.totalAmount) }}
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 趋势图表 -->
    <el-card
      class="chart-card"
      shadow="hover"
    >
      <template #header>
        <div class="chart-header">
          <span>最近7天趋势</span>
          <el-radio-group
            v-model="chartType"
            size="small"
          >
            <el-radio-button label="both">
              全部
            </el-radio-button>
            <el-radio-button label="users">
              用户
            </el-radio-button>
            <el-radio-button label="orders">
              订单
            </el-radio-button>
          </el-radio-group>
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
import { ref, reactive, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { User, Calendar, ShoppingCart, Money } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getDashboardStatistics, getTrendData } from '@/api/statistics'

const chartRef = ref<HTMLElement>()
let chartInstance: echarts.ECharts | null = null
const chartType = ref('both')

const statistics = reactive({
  totalUsers: 0,
  monthNewUsers: 0,
  totalOrders: 0,
  totalAmount: 0
})

const trendData = ref<any[]>([])

const formatAmount = (amount: number) => {
  return amount?.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 }) || '0.00'
}

const fetchStatistics = async () => {
  try {
    const res: any = await getDashboardStatistics()
    Object.assign(statistics, res.data)
  } catch {
    // 错误已在拦截器中处理
  }
}

const fetchTrendData = async () => {
  try {
    const res: any = await getTrendData()
    trendData.value = res.data
    updateChart()
  } catch {
    // 错误已在拦截器中处理
  }
}

const initChart = () => {
  if (!chartRef.value) return
  chartInstance = echarts.init(chartRef.value)
  updateChart()
}

const updateChart = () => {
  if (!chartInstance) return

  const dates = trendData.value.map(item => item.date)
  const newUsersData = trendData.value.map(item => item.newUsers)
  const newOrdersData = trendData.value.map(item => item.newOrders)

  const series: echarts.SeriesOption[] = []

  if (chartType.value === 'both' || chartType.value === 'users') {
    series.push({
      name: '新增用户',
      type: 'line',
      data: newUsersData,
      smooth: true,
      symbol: 'circle',
      symbolSize: 8,
      lineStyle: {
        width: 3,
        color: '#409EFF'
      },
      itemStyle: {
        color: '#409EFF'
      },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
          { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
        ])
      }
    })
  }

  if (chartType.value === 'both' || chartType.value === 'orders') {
    series.push({
      name: '新增订单',
      type: 'line',
      data: newOrdersData,
      smooth: true,
      symbol: 'circle',
      symbolSize: 8,
      lineStyle: {
        width: 3,
        color: '#67C23A'
      },
      itemStyle: {
        color: '#67C23A'
      },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(103, 194, 58, 0.3)' },
          { offset: 1, color: 'rgba(103, 194, 58, 0.05)' }
        ])
      }
    })
  }

  const option: echarts.EChartsOption = {
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#e4e7ed',
      borderWidth: 1,
      textStyle: {
        color: '#606266'
      },
      axisPointer: {
        type: 'line',
        lineStyle: {
          color: '#409EFF',
          width: 1,
          type: 'dashed'
        }
      }
    },
    legend: {
      data: chartType.value === 'both' ? ['新增用户', '新增订单'] : 
            chartType.value === 'users' ? ['新增用户'] : ['新增订单'],
      bottom: 0,
      itemGap: 30,
      textStyle: {
        color: '#606266'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      top: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates,
      axisLine: {
        lineStyle: {
          color: '#dcdfe6'
        }
      },
      axisLabel: {
        color: '#606266',
        formatter: (value: string) => {
          const date = new Date(value)
          return `${date.getMonth() + 1}/${date.getDate()}`
        }
      }
    },
    yAxis: {
      type: 'value',
      axisLine: {
        show: false
      },
      axisTick: {
        show: false
      },
      splitLine: {
        lineStyle: {
          color: '#ebeef5',
          type: 'dashed'
        }
      },
      axisLabel: {
        color: '#606266'
      }
    },
    series
  }

  chartInstance.setOption(option, true)
}

const handleResize = () => {
  chartInstance?.resize()
}

watch(chartType, () => {
  updateChart()
})

onMounted(() => {
  fetchStatistics()
  fetchTrendData()
  nextTick(() => {
    initChart()
  })
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

.stat-cards {
  margin-bottom: var(--spacing-lg);
}

.stat-card {
  display: flex;
  align-items: center;
  padding: var(--spacing-md);
  margin-bottom: var(--spacing-md);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: var(--spacing-md);
}

.stat-icon .el-icon {
  font-size: 28px;
  color: #fff;
}

.user-icon {
  background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%);
}

.month-icon {
  background: linear-gradient(135deg, #E6A23C 0%, #f0c78a 100%);
}

.order-icon {
  background: linear-gradient(135deg, #67C23A 0%, #95d475 100%);
}

.amount-icon {
  background: linear-gradient(135deg, #F56C6C 0%, #f89898 100%);
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 4px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: var(--text-primary);
}

.chart-card {
  margin-bottom: var(--spacing-md);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-header span {
  font-size: 16px;
  font-weight: 500;
  color: var(--text-primary);
}

.chart-container {
  height: 400px;
}

@media (max-width: 768px) {
  .stat-card {
    margin-bottom: var(--spacing-sm);
  }
  
  .chart-container {
    height: 300px;
  }
}
</style>
