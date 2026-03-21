package com.labelease.usermanagement.controller;

import com.labelease.usermanagement.common.Result;
import com.labelease.usermanagement.mapper.OrderMapper;
import com.labelease.usermanagement.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计数据控制器
 */
@Tag(name = "统计管理", description = "仪表盘统计数据接口")
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final UserMapper userMapper;
    private final OrderMapper orderMapper;

    @Operation(summary = "获取仪表盘统计数据")
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboardStats() {
        Map<String, Object> result = new HashMap<>();

        // 用户总数
        long userCount = userMapper.selectCount(null);
        result.put("totalUsers", userCount);

        // 本月新增用户数
        LocalDateTime monthStart = LocalDateTime.of(LocalDate.now().withDayOfMonth(1), LocalTime.MIN);
        long newUsersThisMonth = userMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.labelease.usermanagement.entity.User>()
                        .ge(com.labelease.usermanagement.entity.User::getCreateTime, monthStart)
        );
        result.put("newUsersThisMonth", newUsersThisMonth);

        // 订单总数
        long orderCount = orderMapper.selectCount(null);
        result.put("totalOrders", orderCount);

        // 所有订单总金额
        BigDecimal totalAmount = orderMapper.selectList(null).stream()
                .map(com.labelease.usermanagement.entity.Order::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        result.put("totalAmount", totalAmount);

        return Result.success(result);
    }

    @Operation(summary = "获取最近7天趋势数据")
    @GetMapping("/trend")
    public Result<List<Map<String, Object>>> getTrendData() {
        List<Map<String, Object>> result = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

            // 当天新增用户数
            long newUsers = userMapper.selectCount(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.labelease.usermanagement.entity.User>()
                            .ge(com.labelease.usermanagement.entity.User::getCreateTime, startOfDay)
                            .le(com.labelease.usermanagement.entity.User::getCreateTime, endOfDay)
            );

            // 当天新增订单数
            long newOrders = orderMapper.selectCount(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.labelease.usermanagement.entity.Order>()
                            .ge(com.labelease.usermanagement.entity.Order::getCreateTime, startOfDay)
                            .le(com.labelease.usermanagement.entity.Order::getCreateTime, endOfDay)
            );

            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", date.format(formatter));
            dayData.put("newUsers", newUsers);
            dayData.put("newOrders", newOrders);
            result.add(dayData);
        }

        return Result.success(result);
    }
}