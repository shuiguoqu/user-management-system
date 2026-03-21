package com.labelease.usermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 趋势数据 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrendDataDTO {

    /** 日期 (格式: yyyy-MM-dd) */
    private String date;

    /** 当天新增用户数 */
    private Integer newUsers;

    /** 当天新增订单数 */
    private Integer newOrders;
}
