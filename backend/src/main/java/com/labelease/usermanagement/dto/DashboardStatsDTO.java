package com.labelease.usermanagement.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 仪表盘统计数据 DTO
 */
@Data
public class DashboardStatsDTO {

    /** 用户总数 */
    private Long totalUsers;

    /** 本月新增用户数 */
    private Long newUsersThisMonth;

    /** 订单总数 */
    private Long totalOrders;

    /** 所有订单总金额 */
    private BigDecimal totalAmount;
}
