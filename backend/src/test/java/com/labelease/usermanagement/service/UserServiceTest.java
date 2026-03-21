package com.labelease.usermanagement.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.labelease.usermanagement.entity.User;
import com.labelease.usermanagement.mapper.UserMapper;
import com.labelease.usermanagement.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UserService 单元测试
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testSaveUser_Success() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setRealName("测试用户");
        user.setEmail("test@example.com");
        user.setPhone("13800138000");
        user.setStatus(1);

        when(userMapper.insert(any(User.class))).thenReturn(1);

        boolean result = userService.save(user);

        assertTrue(result);
        verify(userMapper, times(1)).insert(any(User.class));
    }

    @Test
    void testSaveUser_DuplicateUsername() {
        User user = new User();
        user.setUsername("existinguser");
        user.setPassword("password");
        user.setRealName("现有用户");
        user.setStatus(1);

        when(userMapper.insert(any(User.class))).thenThrow(new DuplicateKeyException("用户名重复"));

        assertThrows(DuplicateKeyException.class, () -> userService.save(user));
        verify(userMapper, times(1)).insert(any(User.class));
    }

    @Test
    void testUpdateUser_Success() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("testuser");
        existingUser.setRealName("原姓名");
        existingUser.setStatus(1);
        existingUser.setCreateTime(LocalDateTime.now());

        User updateUser = new User();
        updateUser.setId(1L);
        updateUser.setRealName("新姓名");
        updateUser.setEmail("new@example.com");

        when(userMapper.updateById(any(User.class))).thenReturn(1);

        boolean result = userService.updateById(updateUser);

        assertTrue(result);
        verify(userMapper, times(1)).updateById(any(User.class));
    }

    @Test
    void testUpdateUser_NotExists() {
        User updateUser = new User();
        updateUser.setId(999L);
        updateUser.setRealName("不存在的用户");

        when(userMapper.updateById(any(User.class))).thenReturn(0);

        boolean result = userService.updateById(updateUser);

        assertFalse(result);
        verify(userMapper, times(1)).updateById(any(User.class));
    }

    @Test
    void testDeleteUser_Success() {
        Long userId = 1L;

        when(userMapper.deleteById(userId)).thenReturn(1);

        boolean result = userService.removeById(userId);

        assertTrue(result);
        verify(userMapper, times(1)).deleteById(userId);
    }

    @Test
    void testBatchDeleteUsers_Success() {
        List<Long> userIds = Arrays.asList(1L, 2L, 3L);

        when(userMapper.deleteBatchIds(userIds)).thenReturn(3);

        boolean result = userService.removeByIds(userIds);

        assertTrue(result);
        verify(userMapper, times(1)).deleteBatchIds(userIds);
    }

    @Test
    void testPageUsers_WithoutKeyword() {
        int current = 1;
        int size = 10;
        Page<User> expectedPage = new Page<>(current, size);
        expectedPage.setRecords(Arrays.asList(new User(), new User()));
        expectedPage.setTotal(2);

        when(userMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(expectedPage);

        Page<User> result = userService.pageUsers(current, size, null);

        assertNotNull(result);
        assertEquals(2, result.getRecords().size());
        assertEquals(2, result.getTotal());
        verify(userMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void testPageUsers_WithKeyword() {
        int current = 1;
        int size = 10;
        String keyword = "test";
        Page<User> expectedPage = new Page<>(current, size);
        expectedPage.setRecords(Arrays.asList(new User()));
        expectedPage.setTotal(1);

        when(userMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(expectedPage);

        Page<User> result = userService.pageUsers(current, size, keyword);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        assertEquals(1, result.getTotal());
        verify(userMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void testGetByUsername_Found() {
        String username = "testuser";
        User expectedUser = new User();
        expectedUser.setId(1L);
        expectedUser.setUsername(username);
        expectedUser.setRealName("测试用户");

        when(userMapper.selectByUsername(username)).thenReturn(expectedUser);

        User result = userService.getByUsername(username);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        verify(userMapper, times(1)).selectByUsername(username);
    }

    @Test
    void testGetByUsername_NotFound() {
        String username = "nonexistent";

        when(userMapper.selectByUsername(username)).thenReturn(null);

        User result = userService.getByUsername(username);

        assertNull(result);
        verify(userMapper, times(1)).selectByUsername(username);
    }
}
