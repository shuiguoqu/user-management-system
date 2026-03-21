package com.labelease.usermanagement.controller;

import com.labelease.usermanagement.common.JwtUtil;
import com.labelease.usermanagement.common.Result;
import com.labelease.usermanagement.entity.User;
import com.labelease.usermanagement.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * AuthController 单元测试 - 登录逻辑测试
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    @Test
    void testLogin_Success() {
        String username = "testuser";
        String password = "password";
        String encodedPassword = "$2a$10$encodedPasswordHash";
        String mockToken = "mock.jwt.token";

        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", username);
        credentials.put("password", password);

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername(username);
        mockUser.setPassword(encodedPassword);
        mockUser.setRealName("测试用户");
        mockUser.setStatus(1);

        when(userService.getByUsername(username)).thenReturn(mockUser);
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(jwtUtil.generateToken(username)).thenReturn(mockToken);

        Result<?> result = authController.login(credentials);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("登录成功", result.getMessage());
        assertNotNull(result.getData());

        verify(userService, times(1)).getByUsername(username);
        verify(passwordEncoder, times(1)).matches(password, encodedPassword);
        verify(jwtUtil, times(1)).generateToken(username);
    }

    @Test
    void testLogin_UsernameNotFound() {
        String username = "nonexistent";
        String password = "password";

        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", username);
        credentials.put("password", password);

        when(userService.getByUsername(username)).thenReturn(null);

        Result<?> result = authController.login(credentials);

        assertNotNull(result);
        assertEquals(401, result.getCode());
        assertEquals("用户名或密码错误", result.getMessage());

        verify(userService, times(1)).getByUsername(username);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    void testLogin_WrongPassword() {
        String username = "testuser";
        String password = "wrongpassword";
        String encodedPassword = "$2a$10$encodedPasswordHash";

        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", username);
        credentials.put("password", password);

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername(username);
        mockUser.setPassword(encodedPassword);
        mockUser.setStatus(1);

        when(userService.getByUsername(username)).thenReturn(mockUser);
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

        Result<?> result = authController.login(credentials);

        assertNotNull(result);
        assertEquals(401, result.getCode());
        assertEquals("用户名或密码错误", result.getMessage());

        verify(userService, times(1)).getByUsername(username);
        verify(passwordEncoder, times(1)).matches(password, encodedPassword);
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    void testLogin_UserDisabled() {
        String username = "testuser";
        String password = "password";
        String encodedPassword = "$2a$10$encodedPasswordHash";

        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", username);
        credentials.put("password", password);

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername(username);
        mockUser.setPassword(encodedPassword);
        mockUser.setStatus(0);

        when(userService.getByUsername(username)).thenReturn(mockUser);
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);

        Result<?> result = authController.login(credentials);

        assertNotNull(result);
        assertEquals(403, result.getCode());
        assertEquals("账户已被禁用", result.getMessage());

        verify(userService, times(1)).getByUsername(username);
        verify(passwordEncoder, times(1)).matches(password, encodedPassword);
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    void testLogin_EmptyCredentials() {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", null);
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
    void testLogin_EmptyUsername() {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "");
        credentials.put("password", "password");

        Result<?> result = authController.login(credentials);

        assertNotNull(result);
        assertEquals(400, result.getCode());
        assertEquals("用户名和密码不能为空", result.getMessage());

        verify(userService, never()).getByUsername(anyString());
    }

    @Test
    void testLogin_ReturnsJwtToken() {
        String username = "testuser";
        String password = "password";
        String encodedPassword = "$2a$10$encodedPasswordHash";
        String expectedToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciJ9.mockSignature";

        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", username);
        credentials.put("password", password);

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername(username);
        mockUser.setPassword(encodedPassword);
        mockUser.setRealName("测试用户");
        mockUser.setStatus(1);

        when(userService.getByUsername(username)).thenReturn(mockUser);
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(jwtUtil.generateToken(username)).thenReturn(expectedToken);

        Result<?> result = authController.login(credentials);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertTrue(result.getData() instanceof Map);

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertEquals(expectedToken, data.get("token"));
        assertEquals(username, data.get("username"));
        assertEquals("测试用户", data.get("realName"));
    }
}
