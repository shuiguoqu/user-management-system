package com.labelease.usermanagement.service.impl;

import com.labelease.usermanagement.dto.DashboardStatsDTO;
import com.labelease.usermanagement.dto.TrendDataDTO;
import com.labelease.usermanagement.mapper.OrderMapper;
import com.labelease.usermanagement.mapper.UserMapper;
import com.labelease.usermanagement.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 统计服务实现
 */
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final UserMapper userMapper;
    private final OrderMapper orderMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public DashboardStatsDTO getDashboardStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();

        stats.setTotalUsers(userMapper.countTotalUsers());

        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        stats.setNewUsersThisMonth(userMapper.countNewUsersAfter(monthStart));

        stats.setTotalOrders(orderMapper.countTotalOrders());

        stats.setTotalAmount(orderMapper.sumTotalAmount());

        return stats;
    }

    @Override
    public List<TrendDataDTO> getTrendData() {
        List<TrendDataDTO> trendList = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime dateTime = date.atTime(LocalTime.MIN);

            Integer newUsers = userMapper.countNewUsersByDate(dateTime);
            Integer newOrders = orderMapper.countNewOrdersByDate(dateTime);

            trendList.add(new TrendDataDTO(
                    date.format(DATE_FORMATTER),
                    newUsers != null ? newUsers : 0,
                    newOrders != null ? newOrders : 0
            ));
        }

        return trendList;
    }
}
