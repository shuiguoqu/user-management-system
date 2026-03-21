import request from './request'

export interface DashboardStats {
  totalUsers: number
  newUsersThisMonth: number
  totalOrders: number
  totalAmount: number
}

export interface TrendData {
  date: string
  newUsers: number
  newOrders: number
}

export function getDashboardStats() {
  return request.get<DashboardStats>('/statistics/dashboard')
}

export function getTrendData() {
  return request.get<TrendData[]>('/statistics/trend')
}
