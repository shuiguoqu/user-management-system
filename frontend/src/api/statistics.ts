import request from './request'

/**
 * 获取仪表盘统计数据
 */
export const getDashboardStatistics = () => {
  return request({
    url: '/statistics/dashboard',
    method: 'get'
  })
}

/**
 * 获取最近7天趋势数据
 */
export const getTrendData = () => {
  return request({
    url: '/statistics/trend',
    method: 'get'
  })
}
