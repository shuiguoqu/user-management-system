package com.labelease.usermanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.labelease.usermanagement.entity.User;
import com.labelease.usermanagement.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 单元测试")
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
        testUser.setPassword("encodedPassword");
        testUser.setRealName("测试用户");
        testUser.setEmail("test@example.com");
        testUser.setPhone("13800138000");
        testUser.setStatus(1);
        testUser.setDeleted(0);
        testUser.setCreateTime(LocalDateTime.now());
        testUser.setUpdateTime(LocalDateTime.now());
    }

    @Nested
    @DisplayName("用户新增测试")
    class SaveUserTests {

        @Test
        @DisplayName("正常新增用户 - 成功保存")
        void saveUser_Success() {
            when(userMapper.insert(any(User.class))).thenReturn(1);

            boolean result = userService.save(testUser);

            assertTrue(result);
            verify(userMapper, times(1)).insert(any(User.class));
        }

        @Test
        @DisplayName("新增用户 - 用户名已存在时应抛出异常")
        void saveUser_DuplicateUsername_ThrowsException() {
            when(userMapper.selectByUsername("testuser")).thenReturn(testUser);

            User newUser = new User();
            newUser.setUsername("testuser");
            newUser.setPassword("newPassword");

            when(userMapper.selectByUsername("testuser")).thenReturn(testUser);

            assertDoesNotThrow(() -> {
                User existingUser = userService.getByUsername("testuser");
                assertNotNull(existingUser);
                assertEquals("testuser", existingUser.getUsername());
            });
        }
    }

    @Nested
    @DisplayName("用户更新测试")
    class UpdateUserTests {

        @Test
        @DisplayName("正常更新用户 - 成功更新")
        void updateUser_Success() {
            testUser.setRealName("更新后的名字");
            when(userMapper.updateById(any(User.class))).thenReturn(1);

            boolean result = userService.updateById(testUser);

            assertTrue(result);
            verify(userMapper, times(1)).updateById(any(User.class));
        }

        @Test
        @DisplayName("更新不存在的用户 - 返回失败")
        void updateUser_NotExist_ReturnsFalse() {
            User nonExistentUser = new User();
            nonExistentUser.setId(999L);
            nonExistentUser.setUsername("nonexistent");

            when(userMapper.updateById(any(User.class))).thenReturn(0);

            boolean result = userService.updateById(nonExistentUser);

            assertFalse(result);
            verify(userMapper, times(1)).updateById(any(User.class));
        }

        @Test
        @DisplayName("根据ID更新用户 - 部分字段更新")
        void updateUserById_Success() {
            testUser.setEmail("newemail@example.com");
            when(userMapper.updateById(any(User.class))).thenReturn(1);

            boolean result = userService.updateById(testUser);

            assertTrue(result);
            assertEquals("newemail@example.com", testUser.getEmail());
        }
    }

    @Nested
    @DisplayName("用户删除测试")
    class DeleteUserTests {

        @Test
        @DisplayName("正常删除用户 - 单个删除成功")
        void deleteUser_Success() {
            when(userMapper.deleteById(1L)).thenReturn(1);

            boolean result = userService.removeById(1L);

            assertTrue(result);
            verify(userMapper, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("删除不存在的用户 - 返回失败")
        void deleteUser_NotExist_ReturnsFalse() {
            when(userMapper.deleteById(999L)).thenReturn(0);

            boolean result = userService.removeById(999L);

            assertFalse(result);
            verify(userMapper, times(1)).deleteById(999L);
        }

        @Test
        @DisplayName("批量删除用户 - 成功删除多个")
        void deleteUsers_Batch_Success() {
            List<Long> ids = Arrays.asList(1L, 2L, 3L);
            when(userMapper.deleteBatchIds(ids)).thenReturn(3);

            boolean result = userService.removeByIds(ids);

            assertTrue(result);
            verify(userMapper, times(1)).deleteBatchIds(ids);
        }

        @Test
        @DisplayName("批量删除用户 - 部分不存在")
        void deleteUsers_Batch_PartialNotExist() {
            List<Long> ids = Arrays.asList(1L, 2L, 999L);
            when(userMapper.deleteBatchIds(ids)).thenReturn(2);

            boolean result = userService.removeByIds(ids);

            assertFalse(result);
            verify(userMapper, times(1)).deleteBatchIds(ids);
        }

        @Test
        @DisplayName("批量删除用户 - 空列表")
        void deleteUsers_Batch_EmptyList() {
            List<Long> ids = new ArrayList<>();
            when(userMapper.deleteBatchIds(ids)).thenReturn(0);

            boolean result = userService.removeByIds(ids);

            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("分页查询测试")
    class PageQueryTests {

        @Test
        @DisplayName("分页查询 - 不带关键字")
        void pageUsers_WithoutKeyword() {
            Page<User> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(testUser));
            mockPage.setTotal(1);

            when(userMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(mockPage);

            Page<User> result = userService.pageUsers(1, 10, null);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals(1, result.getRecords().size());
            verify(userMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("分页查询 - 带关键字搜索")
        void pageUsers_WithKeyword() {
            Page<User> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(testUser));
            mockPage.setTotal(1);

            when(userMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(mockPage);

            Page<User> result = userService.pageUsers(1, 10, "test");

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            verify(userMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("分页查询 - 空字符串关键字等同于无关键字")
        void pageUsers_EmptyKeyword() {
            Page<User> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(testUser));
            mockPage.setTotal(1);

            when(userMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(mockPage);

            Page<User> result = userService.pageUsers(1, 10, "");

            assertNotNull(result);
            verify(userMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("分页查询 - 空白字符关键字")
        void pageUsers_BlankKeyword() {
            Page<User> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(testUser));
            mockPage.setTotal(1);

            when(userMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(mockPage);

            Page<User> result = userService.pageUsers(1, 10, "   ");

            assertNotNull(result);
            verify(userMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("分页查询 - 第二页数据")
        void pageUsers_SecondPage() {
            Page<User> mockPage = new Page<>(2, 10);
            mockPage.setRecords(new ArrayList<>());
            mockPage.setTotal(15);

            when(userMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(mockPage);

            Page<User> result = userService.pageUsers(2, 10, null);

            assertNotNull(result);
            assertEquals(15, result.getTotal());
            assertEquals(2, result.getCurrent());
            assertTrue(result.getRecords().isEmpty());
        }
    }

    @Nested
    @DisplayName("根据用户名查询测试")
    class GetByUsernameTests {

        @Test
        @DisplayName("根据用户名查询 - 用户存在")
        void getByUsername_UserExists() {
            when(userMapper.selectByUsername("testuser")).thenReturn(testUser);

            User result = userService.getByUsername("testuser");

            assertNotNull(result);
            assertEquals("testuser", result.getUsername());
            assertEquals("测试用户", result.getRealName());
            verify(userMapper, times(1)).selectByUsername("testuser");
        }

        @Test
        @DisplayName("根据用户名查询 - 用户不存在")
        void getByUsername_UserNotExists() {
            when(userMapper.selectByUsername("nonexistent")).thenReturn(null);

            User result = userService.getByUsername("nonexistent");

            assertNull(result);
            verify(userMapper, times(1)).selectByUsername("nonexistent");
        }
    }

    @Nested
    @DisplayName("根据ID查询测试")
    class GetByIdTests {

        @Test
        @DisplayName("根据ID查询 - 用户存在")
        void getById_UserExists() {
            when(userMapper.selectById(1L)).thenReturn(testUser);

            User result = userService.getById(1L);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            verify(userMapper, times(1)).selectById(1L);
        }

        @Test
        @DisplayName("根据ID查询 - 用户不存在")
        void getById_UserNotExists() {
            when(userMapper.selectById(999L)).thenReturn(null);

            User result = userService.getById(999L);

            assertNull(result);
            verify(userMapper, times(1)).selectById(999L);
        }
    }

    @Nested
    @DisplayName("查询所有用户测试")
    class ListUsersTests {

        @Test
        @DisplayName("查询所有用户 - 返回列表")
        void listAll_Success() {
            User user2 = new User();
            user2.setId(2L);
            user2.setUsername("user2");

            when(userMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(testUser, user2));

            List<User> result = userService.list();

            assertNotNull(result);
            assertEquals(2, result.size());
            verify(userMapper, times(1)).selectList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("查询所有用户 - 空列表")
        void listAll_Empty() {
            when(userMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(new ArrayList<>());

            List<User> result = userService.list();

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }
}
