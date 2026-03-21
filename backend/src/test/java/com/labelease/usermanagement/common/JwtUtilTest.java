package com.labelease.usermanagement.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JWT工具类单元测试")
class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    private static final String SECRET = "mySecretKeyForJwtTokenGeneration12345678901234567890";
    private static final long EXPIRATION = 86400000; // 24小时

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtil, "secret", SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expiration", EXPIRATION);
    }

    @Test
    @DisplayName("测试生成JWT token - 应该成功生成")
    void testGenerateToken_Success() {
        // given
        String username = "testuser";

        // when
        String token = jwtUtil.generateToken(username);

        // then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains("."));
    }

    @Test
    @DisplayName("测试从token解析用户名 - 应该正确解析")
    void testGetUsernameFromToken_Success() {
        // given
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        // when
        String extractedUsername = jwtUtil.getUsernameFromToken(token);

        // then
        assertEquals(username, extractedUsername);
    }

    @Test
    @DisplayName("测试验证有效的token - 应该返回true")
    void testValidateToken_Valid() {
        // given
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        // when
        boolean isValid = jwtUtil.validateToken(token);

        // then
        assertTrue(isValid);
    }

    @Test
    @DisplayName("测试验证无效的token - 应该返回false")
    void testValidateToken_Invalid() {
        // given
        String invalidToken = "invalid.token.here";

        // when
        boolean isValid = jwtUtil.validateToken(invalidToken);

        // then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("测试验证空token - 应该返回false")
    void testValidateToken_Empty() {
        // given
        String emptyToken = "";

        // when
        boolean isValid = jwtUtil.validateToken(emptyToken);

        // then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("测试验证null token - 应该返回false")
    void testValidateToken_Null() {
        // when
        boolean isValid = jwtUtil.validateToken(null);

        // then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("测试token生成和解析一致性 - 不同用户名")
    void testTokenConsistency_DifferentUsers() {
        // given
        String username1 = "user1";
        String username2 = "user2";

        // when
        String token1 = jwtUtil.generateToken(username1);
        String token2 = jwtUtil.generateToken(username2);

        String extractedUsername1 = jwtUtil.getUsernameFromToken(token1);
        String extractedUsername2 = jwtUtil.getUsernameFromToken(token2);

        // then
        assertEquals(username1, extractedUsername1);
        assertEquals(username2, extractedUsername2);
        assertNotEquals(token1, token2);
    }

    @Test
    @DisplayName("测试token生成和解析一致性 - 相同用户名生成不同token")
    void testTokenConsistency_SameUserDifferentTokens() {
        // given
        String username = "testuser";

        // when - 相同用户名生成两次token
        String token1 = jwtUtil.generateToken(username);
        String token2 = jwtUtil.generateToken(username);

        // then - 两个token应该不同（因为有时间戳）
        assertNotEquals(token1, token2);

        // 但都能正确解析出用户名
        assertEquals(username, jwtUtil.getUsernameFromToken(token1));
        assertEquals(username, jwtUtil.getUsernameFromToken(token2));
    }

    @Test
    @DisplayName("测试token验证 - 篡改token应该验证失败")
    void testValidateToken_Tampered() {
        // given
        String username = "testuser";
        String token = jwtUtil.generateToken(username);
        String tamperedToken = token.substring(0, token.length() - 5) + "XXXXX";

        // when
        boolean isValid = jwtUtil.validateToken(tamperedToken);

        // then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("测试解析篡改的token - 应该抛出异常")
    void testGetUsernameFromToken_Tampered() {
        // given
        String username = "testuser";
        String token = jwtUtil.generateToken(username);
        String tamperedToken = token.substring(0, token.length() - 5) + "XXXXX";

        // when & then
        assertThrows(Exception.class, () -> jwtUtil.getUsernameFromToken(tamperedToken));
    }

    @Test
    @DisplayName("测试特殊字符用户名的token生成和解析")
    void testTokenWithSpecialCharacters() {
        // given
        String username = "user@example.com";

        // when
        String token = jwtUtil.generateToken(username);
        String extractedUsername = jwtUtil.getUsernameFromToken(token);

        // then
        assertEquals(username, extractedUsername);
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    @DisplayName("测试中文用户名的token生成和解析")
    void testTokenWithChineseCharacters() {
        // given
        String username = "测试用户123";

        // when
        String token = jwtUtil.generateToken(username);
        String extractedUsername = jwtUtil.getUsernameFromToken(token);

        // then
        assertEquals(username, extractedUsername);
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    @DisplayName("测试长用户名的token生成和解析")
    void testTokenWithLongUsername() {
        // given
        String username = "a".repeat(100);

        // when
        String token = jwtUtil.generateToken(username);
        String extractedUsername = jwtUtil.getUsernameFromToken(token);

        // then
        assertEquals(username, extractedUsername);
        assertTrue(jwtUtil.validateToken(token));
    }
}
