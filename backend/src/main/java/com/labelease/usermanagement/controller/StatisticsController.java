package com.labelease.usermanagement.controller;

import com.labelease.usermanagement.common.Result;
import com.labelease.usermanagement.dto.DashboardStatsDTO;
import com.labelease.usermanagement.dto.TrendDataDTO;
import com.labelease.usermanagement.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 统计数据控制器 - 仪表盘数据接口
 */
@Tag(name = "统计数据", description = "仪表盘统计与趋势数据")
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Operation(summary = "获取仪表盘统计数据")
    @GetMapping("/dashboard")
    public Result<DashboardStatsDTO> getDashboardStats() {
        return Result.success(statisticsService.getDashboardStats());
    }

    @Operation(summary = "获取最近7天趋势数据")
    @GetMapping("/trend")
    public Result<List<TrendDataDTO>> getTrendData() {
        return Result.success(statisticsService.getTrendData());
    }
}
