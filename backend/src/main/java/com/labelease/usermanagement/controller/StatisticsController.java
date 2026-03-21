package com.labelease.usermanagement.controller;

import com.labelease.usermanagement.common.Result;
import com.labelease.usermanagement.mapper.OrderMapper;
import com.labelease.usermanagement.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据统计控制器 - 仪表盘数据接口
 */
@Tag(name = "数据统计", description = "仪表盘统计数据接口")
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final UserMapper userMapper;
    private final OrderMapper orderMapper;

    @Operation(summary = "获取仪表盘统计数据")
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboardStatistics() {
        Map<String, Object> data = new HashMap<>();

        Long totalUsers = userMapper.selectCount(null);
        data.put("totalUsers", totalUsers);

        Long monthNewUsers = userMapper.countNewUsersThisMonth();
        data.put("monthNewUsers", monthNewUsers);

        Long totalOrders = orderMapper.selectCount(null);
        data.put("totalOrders", totalOrders);

        BigDecimal totalAmount = orderMapper.sumAllAmount();
        data.put("totalAmount", totalAmount != null ? totalAmount : BigDecimal.ZERO);

        return Result.success(data);
    }

    @Operation(summary = "获取最近7天趋势数据")
    @GetMapping("/trend")
    public Result<List<Map<String, Object>>> getTrendData() {
        List<Map<String, Object>> trendList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            String dateStr = date.format(formatter);

            Long newUsers = userMapper.countNewUsersByDate(date);
            Long newOrders = orderMapper.countNewOrdersByDate(date);

            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", dateStr);
            dayData.put("newUsers", newUsers);
            dayData.put("newOrders", newOrders);

            trendList.add(dayData);
        }

        return Result.success(trendList);
    }
}
