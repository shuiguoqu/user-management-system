package com.labelease.usermanagement.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.labelease.usermanagement.entity.User;
import com.labelease.usermanagement.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("用户服务单元测试")
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setRealName("测试用户");
        testUser.setEmail("test@example.com");
        testUser.setPhone("13800138000");
        testUser.setStatus(1);
    }

    @Test
    @DisplayName("测试正常新增用户 - 应该成功保存")
    void testAddUser_Success() {
        // given
        when(userMapper.insert(any(User.class))).thenReturn(1);

        // when
        boolean result = userService.save(testUser);

        // then
        assertTrue(result);
        verify(userMapper, times(1)).insert(testUser);
    }

    @Test
    @DisplayName("测试新增用户失败 - 应该返回false")
    void testAddUser_Failure() {
        // given
        when(userMapper.insert(any(User.class))).thenReturn(0);

        // when
        boolean result = userService.save(testUser);

        // then
        assertFalse(result);
        verify(userMapper, times(1)).insert(testUser);
    }

    @Test
    @DisplayName("测试根据用户名查询用户 - 应该返回用户")
    void testGetByUsername_Success() {
        // given
        when(userMapper.selectByUsername("testuser")).thenReturn(testUser);

        // when
        User result = userService.getByUsername("testuser");

        // then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("测试用户", result.getRealName());
        verify(userMapper, times(1)).selectByUsername("testuser");
    }

    @Test
    @DisplayName("测试根据用户名查询不存在的用户 - 应该返回null")
    void testGetByUsername_NotFound() {
        // given
        when(userMapper.selectByUsername("nonexistent")).thenReturn(null);

        // when
        User result = userService.getByUsername("nonexistent");

        // then
        assertNull(result);
        verify(userMapper, times(1)).selectByUsername("nonexistent");
    }

    @Test
    @DisplayName("测试正常更新用户 - 应该成功更新")
    void testUpdateUser_Success() {
        // given
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // when
        boolean result = userService.updateById(testUser);

        // then
        assertTrue(result);
        verify(userMapper, times(1)).updateById(testUser);
    }

    @Test
    @DisplayName("测试更新不存在的用户 - 应该返回false")
    void testUpdateUser_NotFound() {
        // given
        when(userMapper.updateById(any(User.class))).thenReturn(0);

        // when
        boolean result = userService.updateById(testUser);

        // then
        assertFalse(result);
        verify(userMapper, times(1)).updateById(testUser);
    }

    @Test
    @DisplayName("测试正常删除用户 - 应该成功删除")
    void testDeleteUser_Success() {
        // given
        when(userMapper.deleteById(1L)).thenReturn(1);

        // when
        boolean result = userService.removeById(1L);

        // then
        assertTrue(result);
        verify(userMapper, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("测试删除不存在的用户 - 应该返回false")
    void testDeleteUser_NotFound() {
        // given
        when(userMapper.deleteById(999L)).thenReturn(0);

        // when
        boolean result = userService.removeById(999L);

        // then
        assertFalse(result);
        verify(userMapper, times(1)).deleteById(999L);
    }

    @Test
    @DisplayName("测试批量删除用户 - 应该成功删除多个用户")
    void testBatchDeleteUsers_Success() {
        // given
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        when(userMapper.deleteBatchIds(ids)).thenReturn(3);

        // when
        boolean result = userService.removeByIds(ids);

        // then
        assertTrue(result);
        verify(userMapper, times(1)).deleteBatchIds(ids);
    }

    @Test
    @DisplayName("测试批量删除部分用户 - 应该返回true（只要删除成功即可）")
    void testBatchDeleteUsers_Partial() {
        // given
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        when(userMapper.deleteBatchIds(ids)).thenReturn(2);

        // when
        boolean result = userService.removeByIds(ids);

        // then
        assertTrue(result);
        verify(userMapper, times(1)).deleteBatchIds(ids);
    }

    @Test
    @DisplayName("测试分页查询用户 - 不带关键字搜索")
    void testPageUsers_WithoutKeyword() {
        // given
        Page<User> page = new Page<>(1, 10);
        List<User> userList = Arrays.asList(testUser);
        page.setRecords(userList);
        page.setTotal(1);

        when(userMapper.selectPage(any(Page.class), any())).thenReturn(page);

        // when
        Page<User> result = userService.pageUsers(1, 10, null);

        // then
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
        verify(userMapper, times(1)).selectPage(any(Page.class), any());
    }

    @Test
    @DisplayName("测试分页查询用户 - 带关键字搜索")
    void testPageUsers_WithKeyword() {
        // given
        Page<User> page = new Page<>(1, 10);
        List<User> userList = Arrays.asList(testUser);
        page.setRecords(userList);
        page.setTotal(1);

        when(userMapper.selectPage(any(Page.class), any())).thenReturn(page);

        // when
        Page<User> result = userService.pageUsers(1, 10, "test");

        // then
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        verify(userMapper, times(1)).selectPage(any(Page.class), any());
    }

    @Test
    @DisplayName("测试分页查询用户 - 空结果")
    void testPageUsers_EmptyResult() {
        // given
        Page<User> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList());
        page.setTotal(0);

        when(userMapper.selectPage(any(Page.class), any())).thenReturn(page);

        // when
        Page<User> result = userService.pageUsers(1, 10, "nonexistent");

        // then
        assertNotNull(result);
        assertEquals(0, result.getTotal());
        assertTrue(result.getRecords().isEmpty());
        verify(userMapper, times(1)).selectPage(any(Page.class), any());
    }

    @Test
    @DisplayName("测试根据ID查询用户 - 应该返回用户")
    void testGetById_Success() {
        // given
        when(userMapper.selectById(1L)).thenReturn(testUser);

        // when
        User result = userService.getById(1L);

        // then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        verify(userMapper, times(1)).selectById(1L);
    }

    @Test
    @DisplayName("测试根据ID查询不存在的用户 - 应该返回null")
    void testGetById_NotFound() {
        // given
        when(userMapper.selectById(999L)).thenReturn(null);

        // when
        User result = userService.getById(999L);

        // then
        assertNull(result);
        verify(userMapper, times(1)).selectById(999L);
    }

    @Test
    @DisplayName("测试查询所有用户列表")
    void testListAllUsers() {
        // given
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        List<User> userList = Arrays.asList(testUser, user2);

        when(userMapper.selectList(any())).thenReturn(userList);

        // when
        List<User> result = userService.list();

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userMapper, times(1)).selectList(any());
    }
}
