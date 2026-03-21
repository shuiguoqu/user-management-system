package com.labelease.usermanagement.controller;

import com.labelease.usermanagement.common.JwtUtil;
import com.labelease.usermanagement.common.Result;
import com.labelease.usermanagement.entity.User;
import com.labelease.usermanagement.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("认证控制器单元测试")
class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    private User testUser;
    private static final String TEST_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("$2a$10$encodedPasswordHash");
        testUser.setRealName("测试用户");
        testUser.setStatus(1);
    }

    @Test
    @DisplayName("测试登录成功 - 应该返回JWT token")
    void testLogin_Success() {
        // given
        Map<String, String> credentials = Map.of(
                "username", "testuser",
                "password", "password123"
        );

        when(userService.getByUsername("testuser")).thenReturn(testUser);
        when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken("testuser")).thenReturn(TEST_TOKEN);

        // when
        Result<?> result = authController.login(credentials);

        // then
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("登录成功", result.getMessage());

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertNotNull(data);
        assertEquals(TEST_TOKEN, data.get("token"));
        assertEquals("testuser", data.get("username"));
        assertEquals("测试用户", data.get("realName"));

        verify(userService, times(1)).getByUsername("testuser");
        verify(passwordEncoder, times(1)).matches("password123", testUser.getPassword());
        verify(jwtUtil, times(1)).generateToken("testuser");
    }

    @Test
    @DisplayName("测试登录失败 - 用户名错误")
    void testLogin_WrongUsername() {
        // given
        Map<String, String> credentials = Map.of(
                "username", "wronguser",
                "password", "password123"
        );

        when(userService.getByUsername("wronguser")).thenReturn(null);

        // when
        Result<?> result = authController.login(credentials);

        // then
        assertNotNull(result);
        assertEquals(401, result.getCode());
        assertEquals("用户名或密码错误", result.getMessage());
        assertNull(result.getData());

        verify(userService, times(1)).getByUsername("wronguser");
        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    @DisplayName("测试登录失败 - 密码错误")
    void testLogin_WrongPassword() {
        // given
        Map<String, String> credentials = Map.of(
                "username", "testuser",
                "password", "wrongpassword"
        );

        when(userService.getByUsername("testuser")).thenReturn(testUser);
        when(passwordEncoder.matches("wrongpassword", testUser.getPassword())).thenReturn(false);

        // when
        Result<?> result = authController.login(credentials);

        // then
        assertNotNull(result);
        assertEquals(401, result.getCode());
        assertEquals("用户名或密码错误", result.getMessage());
        assertNull(result.getData());

        verify(userService, times(1)).getByUsername("testuser");
        verify(passwordEncoder, times(1)).matches("wrongpassword", testUser.getPassword());
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    @DisplayName("测试登录失败 - 用户被禁用")
    void testLogin_UserDisabled() {
        // given
        testUser.setStatus(0);
        Map<String, String> credentials = Map.of(
                "username", "testuser",
                "password", "password123"
        );

        when(userService.getByUsername("testuser")).thenReturn(testUser);
        when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(true);

        // when
        Result<?> result = authController.login(credentials);

        // then
        assertNotNull(result);
        assertEquals(403, result.getCode());
        assertEquals("账户已被禁用", result.getMessage());
        assertNull(result.getData());

        verify(userService, times(1)).getByUsername("testuser");
        verify(passwordEncoder, times(1)).matches("password123", testUser.getPassword());
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    @DisplayName("测试登录失败 - 用户名为空")
    void testLogin_NullUsername() {
        // given
        Map<String, String> credentials = Map.of(
                "password", "password123"
        );

        // when
        Result<?> result = authController.login(credentials);

        // then
        assertNotNull(result);
        assertEquals(400, result.getCode());
        assertEquals("用户名和密码不能为空", result.getMessage());

        verify(userService, never()).getByUsername(any());
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    @DisplayName("测试登录失败 - 密码为空")
    void testLogin_NullPassword() {
        // given
        Map<String, String> credentials = Map.of(
                "username", "testuser"
        );

        // when
        Result<?> result = authController.login(credentials);

        // then
        assertNotNull(result);
        assertEquals(400, result.getCode());
        assertEquals("用户名和密码不能为空", result.getMessage());

        verify(userService, never()).getByUsername(any());
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    @DisplayName("测试登录失败 - 用户名和密码都为空")
    void testLogin_NullCredentials() {
        // given
        Map<String, String> credentials = Map.of();

        // when
        Result<?> result = authController.login(credentials);

        // then
        assertNotNull(result);
        assertEquals(400, result.getCode());
        assertEquals("用户名和密码不能为空", result.getMessage());

        verify(userService, never()).getByUsername(any());
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    @DisplayName("测试健康检查 - 应该返回服务正常")
    void testHealth() {
        // when
        Result<String> result = authController.health();

        // then
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("success", result.getMessage());
        assertEquals("服务运行正常", result.getData());
    }
}
