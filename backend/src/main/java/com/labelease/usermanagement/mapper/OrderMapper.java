package com.labelease.usermanagement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.labelease.usermanagement.entity.Order;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 订单数据访问层
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /** 根据用户ID查询该用户的所有订单 */
    @Select("SELECT * FROM t_order WHERE user_id = #{userId} AND deleted = 0 ORDER BY create_time DESC")
    List<Order> selectByUserId(@Param("userId") Long userId);

    /** 根据用户ID删除该用户的所有订单 */
    @Delete("DELETE FROM t_order WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Long userId);
}
