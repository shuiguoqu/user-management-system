package com.labelease.usermanagement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.labelease.usermanagement.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 订单数据访问层
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /** 根据用户ID查询该用户的所有订单 */
    @Select("SELECT * FROM t_order WHERE user_id = #{userId} AND deleted = 0 ORDER BY create_time DESC")
    List<Order> selectByUserId(@Param("userId") Long userId);

    /** 统计所有订单总金额 */
    @Select("SELECT COALESCE(SUM(amount), 0) FROM t_order WHERE deleted = 0")
    BigDecimal sumAllAmount();

    /** 统计指定日期新增订单数 */
    @Select("SELECT COUNT(*) FROM t_order WHERE deleted = 0 AND DATE(create_time) = #{date}")
    Long countNewOrdersByDate(@Param("date") LocalDate date);
}
