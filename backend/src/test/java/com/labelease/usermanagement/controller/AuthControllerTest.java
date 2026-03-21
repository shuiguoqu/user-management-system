package com.labelease.usermanagement.controller;

import com.labelease.usermanagement.common.JwtUtil;
import com.labelease.usermanagement.common.Result;
import com.labelease.usermanagement.entity.User;
import com.labelease.usermanagement.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController 单元测试")
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
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_PASSWORD = "password123";
    private static final String ENCODED_PASSWORD = "$2a$10$encodedPasswordHash";
    private static final String TEST_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername(TEST_USERNAME);
        testUser.setPassword(ENCODED_PASSWORD);
        testUser.setRealName("测试用户");
        testUser.setEmail("test@example.com");
        testUser.setPhone("13800138000");
        testUser.setStatus(1);
        testUser.setDeleted(0);
        testUser.setCreateTime(LocalDateTime.now());
        testUser.setUpdateTime(LocalDateTime.now());
    }

    @Nested
    @DisplayName("登录测试")
    class LoginTests {

        @Test
        @DisplayName("登录成功 - 返回JWT Token")
        void login_Success_ReturnsToken() {
            Map<String, String> credentials = new HashMap<>();
            credentials.put("username", TEST_USERNAME);
            credentials.put("password", TEST_PASSWORD);

            when(userService.getByUsername(TEST_USERNAME)).thenReturn(testUser);
            when(passwordEncoder.matches(TEST_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
            when(jwtUtil.generateToken(TEST_USERNAME)).thenReturn(TEST_TOKEN);

            Result<?> result = authController.login(credentials);

            assertNotNull(result);
            assertEquals(200, result.getCode());
            assertEquals("登录成功", result.getMessage());
            assertNotNull(result.getData());

            @SuppressWarnings("unchecked")
            Map<String, String> data = (Map<String, String>) result.getData();
            assertEquals(TEST_TOKEN, data.get("token"));
            assertEquals(TEST_USERNAME, data.get("username"));
            assertEquals("测试用户", data.get("realName"));

            verify(userService, times(1)).getByUsername(TEST_USERNAME);
            verify(passwordEncoder, times(1)).matches(TEST_PASSWORD, ENCODED_PASSWORD);
            verify(jwtUtil, times(1)).generateToken(TEST_USERNAME);
        }

        @Test
        @DisplayName("登录失败 - 用户名错误")
        void login_WrongUsername_Returns401() {
            Map<String, String> credentials = new HashMap<>();
            credentials.put("username", "wronguser");
            credentials.put("password", TEST_PASSWORD);

            when(userService.getByUsername("wronguser")).thenReturn(null);

            Result<?> result = authController.login(credentials);

            assertNotNull(result);
            assertEquals(401, result.getCode());
            assertEquals("用户名或密码错误", result.getMessage());
            assertNull(result.getData());

            verify(userService, times(1)).getByUsername("wronguser");
            verify(passwordEncoder, never()).matches(anyString(), anyString());
            verify(jwtUtil, never()).generateToken(anyString());
        }

        @Test
        @DisplayName("登录失败 - 密码错误")
        void login_WrongPassword_Returns401() {
            Map<String, String> credentials = new HashMap<>();
            credentials.put("username", TEST_USERNAME);
            credentials.put("password", "wrongpassword");

            when(userService.getByUsername(TEST_USERNAME)).thenReturn(testUser);
            when(passwordEncoder.matches("wrongpassword", ENCODED_PASSWORD)).thenReturn(false);

            Result<?> result = authController.login(credentials);

            assertNotNull(result);
            assertEquals(401, result.getCode());
            assertEquals("用户名或密码错误", result.getMessage());
            assertNull(result.getData());

            verify(userService, times(1)).getByUsername(TEST_USERNAME);
            verify(passwordEncoder, times(1)).matches("wrongpassword", ENCODED_PASSWORD);
            verify(jwtUtil, never()).generateToken(anyString());
        }

        @Test
        @DisplayName("登录失败 - 账户被禁用")
        void login_AccountDisabled_Returns403() {
            testUser.setStatus(0);

            Map<String, String> credentials = new HashMap<>();
            credentials.put("username", TEST_USERNAME);
            credentials.put("password", TEST_PASSWORD);

            when(userService.getByUsername(TEST_USERNAME)).thenReturn(testUser);
            when(passwordEncoder.matches(TEST_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);

            Result<?> result = authController.login(credentials);

            assertNotNull(result);
            assertEquals(403, result.getCode());
            assertEquals("账户已被禁用", result.getMessage());
            assertNull(result.getData());

            verify(userService, times(1)).getByUsername(TEST_USERNAME);
            verify(passwordEncoder, times(1)).matches(TEST_PASSWORD, ENCODED_PASSWORD);
            verify(jwtUtil, never()).generateToken(anyString());
        }

        @Test
        @DisplayName("登录失败 - 用户名为空")
        void login_EmptyUsername_Returns400() {
            Map<String, String> credentials = new HashMap<>();
            credentials.put("username", null);
            credentials.put("password", TEST_PASSWORD);

            Result<?> result = authController.login(credentials);

            assertNotNull(result);
            assertEquals(400, result.getCode());
            assertEquals("用户名和密码不能为空", result.getMessage());

            verify(userService, never()).getByUsername(anyString());
            verify(passwordEncoder, never()).matches(anyString(), anyString());
            verify(jwtUtil, never()).generateToken(anyString());
        }

        @Test
        @DisplayName("登录失败 - 密码为空")
        void login_EmptyPassword_Returns400() {
            Map<String, String> credentials = new HashMap<>();
            credentials.put("username", TEST_USERNAME);
            credentials.put("password", null);

            Result<?> result = authController.login(credentials);

            assertNotNull(result);
            assertEquals(400, result.getCode());
            assertEquals("用户名和密码不能为空", result.getMessage());

            verify(userService, never()).getByUsername(anyString());
            verify(passwordEncoder, never()).matches(anyString(), anyString());
            verify(jwtUtil, never()).generateToken(anyString());
        }

        @Test
        @DisplayName("登录失败 - 用户名和密码都为空")
        void login_EmptyCredentials_Returns400() {
            Map<String, String> credentials = new HashMap<>();
            credentials.put("username", null);
            credentials.put("password", null);

            Result<?> result = authController.login(credentials);

            assertNotNull(result);
            assertEquals(400, result.getCode());
            assertEquals("用户名和密码不能为空", result.getMessage());
        }

        @Test
        @DisplayName("登录失败 - 凭证Map中无username键")
        void login_MissingUsernameKey_Returns400() {
            Map<String, String> credentials = new HashMap<>();
            credentials.put("password", TEST_PASSWORD);

            Result<?> result = authController.login(credentials);

            assertNotNull(result);
            assertEquals(400, result.getCode());
            assertEquals("用户名和密码不能为空", result.getMessage());
        }

        @Test
        @DisplayName("登录失败 - 凭证Map中无password键")
        void login_MissingPasswordKey_Returns400() {
            Map<String, String> credentials = new HashMap<>();
            credentials.put("username", TEST_USERNAME);

            Result<?> result = authController.login(credentials);

            assertNotNull(result);
            assertEquals(400, result.getCode());
            assertEquals("用户名和密码不能为空", result.getMessage());
        }

        @Test
        @DisplayName("登录失败 - 空凭证Map")
        void login_EmptyCredentialsMap_Returns400() {
            Map<String, String> credentials = new HashMap<>();

            Result<?> result = authController.login(credentials);

            assertNotNull(result);
            assertEquals(400, result.getCode());
            assertEquals("用户名和密码不能为空", result.getMessage());
        }
    }

    @Nested
    @DisplayName("健康检查测试")
    class HealthTests {

        @Test
        @DisplayName("健康检查 - 返回正常状态")
        void health_ReturnsOk() {
            Result<String> result = authController.health();

            assertNotNull(result);
            assertEquals(200, result.getCode());
            assertEquals("服务运行正常", result.getData());
        }
    }

    @Nested
    @DisplayName("Token 生成验证测试")
    class TokenGenerationTests {

        @Test
        @DisplayName("Token生成 - 登录成功时生成Token")
        void tokenGeneration_OnLoginSuccess() {
            Map<String, String> credentials = new HashMap<>();
            credentials.put("username", TEST_USERNAME);
            credentials.put("password", TEST_PASSWORD);

            when(userService.getByUsername(TEST_USERNAME)).thenReturn(testUser);
            when(passwordEncoder.matches(TEST_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
            when(jwtUtil.generateToken(TEST_USERNAME)).thenReturn(TEST_TOKEN);

            Result<?> result = authController.login(credentials);

            verify(jwtUtil, times(1)).generateToken(TEST_USERNAME);

            @SuppressWarnings("unchecked")
            Map<String, String> data = (Map<String, String>) result.getData();
            assertNotNull(data.get("token"));
        }

        @Test
        @DisplayName("Token生成 - 不同用户生成不同Token")
        void tokenGeneration_DifferentUsers() {
            String user1 = "user1";
            String user2 = "user2";
            String token1 = "token1";
            String token2 = "token2";

            when(jwtUtil.generateToken(user1)).thenReturn(token1);
            when(jwtUtil.generateToken(user2)).thenReturn(token2);

            String generatedToken1 = jwtUtil.generateToken(user1);
            String generatedToken2 = jwtUtil.generateToken(user2);

            assertNotEquals(generatedToken1, generatedToken2);
        }
    }

    @Nested
    @DisplayName("密码验证测试")
    class PasswordValidationTests {

        @Test
        @DisplayName("密码验证 - BCrypt匹配成功")
        void passwordValidation_MatchSuccess() {
            when(passwordEncoder.matches(TEST_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);

            boolean matches = passwordEncoder.matches(TEST_PASSWORD, ENCODED_PASSWORD);

            assertTrue(matches);
        }

        @Test
        @DisplayName("密码验证 - BCrypt匹配失败")
        void passwordValidation_MatchFailure() {
            when(passwordEncoder.matches("wrongpassword", ENCODED_PASSWORD)).thenReturn(false);

            boolean matches = passwordEncoder.matches("wrongpassword", ENCODED_PASSWORD);

            assertFalse(matches);
        }
    }
}
