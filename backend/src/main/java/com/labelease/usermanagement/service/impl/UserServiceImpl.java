package com.labelease.usermanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.labelease.usermanagement.entity.User;
import com.labelease.usermanagement.mapper.UserMapper;
import com.labelease.usermanagement.service.OrderService;
import com.labelease.usermanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 用户服务实现
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final OrderService orderService;

    @Override
    public Page<User> pageUsers(int current, int size, String keyword) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(User::getUsername, keyword)
                   .or().like(User::getRealName, keyword)
                   .or().like(User::getEmail, keyword)
                   .or().like(User::getPhone, keyword);
        }
        wrapper.orderByDesc(User::getCreateTime);
        return page(new Page<>(current, size), wrapper);
    }

    @Override
    public User getByUsername(String username) {
        return baseMapper.selectByUsername(username);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeUserWithOrders(Long id) {
        orderService.removeByUserId(id);
        return removeById(id);
    }
}
