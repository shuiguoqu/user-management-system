package com.labelease.usermanagement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.labelease.usermanagement.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

/**
 * 用户数据访问层
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /** 根据用户名查询（含密码，用于登录） */
    @Select("SELECT * FROM t_user WHERE username = #{username} AND deleted = 0")
    User selectByUsername(@Param("username") String username);

    /** 统计用户总数 */
    @Select("SELECT COUNT(*) FROM t_user WHERE deleted = 0")
    Long countTotalUsers();

    /** 统计指定时间之后新增的用户数 */
    @Select("SELECT COUNT(*) FROM t_user WHERE deleted = 0 AND create_time >= #{startTime}")
    Long countNewUsersAfter(@Param("startTime") LocalDateTime startTime);

    /** 统计指定日期的新增用户数 */
    @Select("SELECT COUNT(*) FROM t_user WHERE deleted = 0 AND DATE(create_time) = DATE(#{date)")
    Integer countNewUsersByDate(@Param("date") LocalDateTime date);
}
