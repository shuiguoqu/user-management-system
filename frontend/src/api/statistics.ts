import request from './request'

/**
 * 统计数据 API 接口
 */

// 获取仪表盘统计数据
export const getDashboardStats = () => {
  return request({
    url: '/statistics/dashboard',
    method: 'get'
  })
}

// 获取最近7天趋势数据
export const getTrendData = () => {
  return request({
    url: '/statistics/trend',
    method: 'get'
  })
}