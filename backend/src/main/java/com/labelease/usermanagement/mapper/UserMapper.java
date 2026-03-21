package com.labelease.usermanagement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.labelease.usermanagement.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

/**
 * 用户数据访问层
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /** 根据用户名查询（含密码，用于登录） */
    @Select("SELECT * FROM t_user WHERE username = #{username} AND deleted = 0")
    User selectByUsername(@Param("username") String username);

    /** 统计本月新增用户数 */
    @Select("SELECT COUNT(*) FROM t_user WHERE deleted = 0 AND DATE_FORMAT(create_time, '%Y-%m') = DATE_FORMAT(NOW(), '%Y-%m')")
    Long countNewUsersThisMonth();

    /** 统计指定日期新增用户数 */
    @Select("SELECT COUNT(*) FROM t_user WHERE deleted = 0 AND DATE(create_time) = #{date}")
    Long countNewUsersByDate(@Param("date") LocalDate date);
}
