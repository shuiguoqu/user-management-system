package com.labelease.usermanagement.service;

import com.labelease.usermanagement.dto.DashboardStatsDTO;
import com.labelease.usermanagement.dto.TrendDataDTO;

import java.util.List;

/**
 * 统计服务接口
 */
public interface StatisticsService {

    /**
     * 获取仪表盘统计数据
     */
    DashboardStatsDTO getDashboardStats();

    /**
     * 获取最近7天趋势数据
     */
    List<TrendDataDTO> getTrendData();
}
