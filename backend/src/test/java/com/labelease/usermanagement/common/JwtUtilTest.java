package com.labelease.usermanagement.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtUtil 单元测试")
class JwtUtilTest {

    private JwtUtil jwtUtil;

    private static final String TEST_SECRET = "thisIsAVeryLongSecretKeyForJwtTokenGenerationAndValidation123456789";
    private static final long TEST_EXPIRATION = 3600000L;
    private static final String TEST_USERNAME = "testuser";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expiration", TEST_EXPIRATION);
    }

    @Nested
    @DisplayName("Token 生成测试")
    class GenerateTokenTests {

        @Test
        @DisplayName("生成 Token - 成功返回非空字符串")
        void generateToken_Success() {
            String token = jwtUtil.generateToken(TEST_USERNAME);

            assertNotNull(token);
            assertFalse(token.isEmpty());
            assertTrue(token.split("\\.").length == 3);
        }

        @Test
        @DisplayName("生成 Token - 不同用户名生成不同 Token")
        void generateToken_DifferentUsers_DifferentTokens() {
            String token1 = jwtUtil.generateToken("user1");
            String token2 = jwtUtil.generateToken("user2");

            assertNotNull(token1);
            assertNotNull(token2);
            assertNotEquals(token1, token2);
        }

        @Test
        @DisplayName("生成 Token - 相同用户名多次生成不同 Token（时间戳不同）")
        void generateToken_SameUser_DifferentTokens() throws InterruptedException {
            String token1 = jwtUtil.generateToken(TEST_USERNAME);
            Thread.sleep(1000);
            String token2 = jwtUtil.generateToken(TEST_USERNAME);

            assertNotNull(token1);
            assertNotNull(token2);
            assertNotEquals(token1, token2);
        }

        @Test
        @DisplayName("生成 Token - 包含正确的用户名")
        void generateToken_ContainsCorrectUsername() {
            String token = jwtUtil.generateToken(TEST_USERNAME);

            String extractedUsername = jwtUtil.getUsernameFromToken(token);

            assertEquals(TEST_USERNAME, extractedUsername);
        }

        @Test
        @DisplayName("生成 Token - 特殊字符用户名")
        void generateToken_SpecialCharacters() {
            String specialUsername = "user@domain.com";

            String token = jwtUtil.generateToken(specialUsername);
            String extracted = jwtUtil.getUsernameFromToken(token);

            assertEquals(specialUsername, extracted);
        }

        @Test
        @DisplayName("生成 Token - 中文用户名")
        void generateToken_ChineseUsername() {
            String chineseUsername = "测试用户";

            String token = jwtUtil.generateToken(chineseUsername);
            String extracted = jwtUtil.getUsernameFromToken(token);

            assertEquals(chineseUsername, extracted);
        }
    }

    @Nested
    @DisplayName("Token 解析测试")
    class ParseTokenTests {

        @Test
        @DisplayName("解析 Token - 成功获取用户名")
        void getUsernameFromToken_Success() {
            String token = jwtUtil.generateToken(TEST_USERNAME);

            String username = jwtUtil.getUsernameFromToken(token);

            assertEquals(TEST_USERNAME, username);
        }

        @Test
        @DisplayName("解析 Token - 空Token抛出异常")
        void getUsernameFromToken_EmptyToken_ThrowsException() {
            assertThrows(Exception.class, () -> {
                jwtUtil.getUsernameFromToken("");
            });
        }

        @Test
        @DisplayName("解析 Token - null Token抛出异常")
        void getUsernameFromToken_NullToken_ThrowsException() {
            assertThrows(Exception.class, () -> {
                jwtUtil.getUsernameFromToken(null);
            });
        }

        @Test
        @DisplayName("解析 Token - 格式错误的Token抛出异常")
        void getUsernameFromToken_MalformedToken_ThrowsException() {
            String malformedToken = "invalid.token.format";

            assertThrows(Exception.class, () -> {
                jwtUtil.getUsernameFromToken(malformedToken);
            });
        }

        @Test
        @DisplayName("解析 Token - 被篡改的Token抛出异常")
        void getUsernameFromToken_TamperedToken_ThrowsException() {
            String token = jwtUtil.generateToken(TEST_USERNAME);
            String tamperedToken = token + "tampered";

            assertThrows(Exception.class, () -> {
                jwtUtil.getUsernameFromToken(tamperedToken);
            });
        }
    }

    @Nested
    @DisplayName("Token 验证测试")
    class ValidateTokenTests {

        @Test
        @DisplayName("验证 Token - 有效Token返回true")
        void validateToken_ValidToken_ReturnsTrue() {
            String token = jwtUtil.generateToken(TEST_USERNAME);

            boolean isValid = jwtUtil.validateToken(token);

            assertTrue(isValid);
        }

        @Test
        @DisplayName("验证 Token - 空Token返回false")
        void validateToken_EmptyToken_ReturnsFalse() {
            boolean isValid = jwtUtil.validateToken("");

            assertFalse(isValid);
        }

        @Test
        @DisplayName("验证 Token - null Token返回false")
        void validateToken_NullToken_ReturnsFalse() {
            boolean isValid = jwtUtil.validateToken(null);

            assertFalse(isValid);
        }

        @Test
        @DisplayName("验证 Token - 格式错误的Token返回false")
        void validateToken_MalformedToken_ReturnsFalse() {
            boolean isValid = jwtUtil.validateToken("invalid.token.format");

            assertFalse(isValid);
        }

        @Test
        @DisplayName("验证 Token - 被篡改的Token返回false")
        void validateToken_TamperedToken_ReturnsFalse() {
            String token = jwtUtil.generateToken(TEST_USERNAME);
            String tamperedToken = token.substring(0, token.length() - 5) + "xxxxx";

            boolean isValid = jwtUtil.validateToken(tamperedToken);

            assertFalse(isValid);
        }

        @Test
        @DisplayName("验证 Token - 使用不同密钥签名的Token返回false")
        void validateToken_DifferentSecret_ReturnsFalse() {
            String token = jwtUtil.generateToken(TEST_USERNAME);

            JwtUtil anotherJwtUtil = new JwtUtil();
            ReflectionTestUtils.setField(anotherJwtUtil, "secret", "anotherSecretKeyForTestingDifferentSignature12345678");
            ReflectionTestUtils.setField(anotherJwtUtil, "expiration", TEST_EXPIRATION);

            boolean isValid = anotherJwtUtil.validateToken(token);

            assertFalse(isValid);
        }
    }

    @Nested
    @DisplayName("Token 生成与解析一致性测试")
    class ConsistencyTests {

        @Test
        @DisplayName("一致性 - 生成后立即解析用户名一致")
        void consistency_ImmediateParse_Success() {
            String token = jwtUtil.generateToken(TEST_USERNAME);

            String extractedUsername = jwtUtil.getUsernameFromToken(token);

            assertEquals(TEST_USERNAME, extractedUsername);
        }

        @Test
        @DisplayName("一致性 - 生成后立即验证有效")
        void consistency_ImmediateValidate_Success() {
            String token = jwtUtil.generateToken(TEST_USERNAME);

            boolean isValid = jwtUtil.validateToken(token);

            assertTrue(isValid);
        }

        @Test
        @DisplayName("一致性 - 多次解析结果一致")
        void consistency_MultipleParse_SameResult() {
            String token = jwtUtil.generateToken(TEST_USERNAME);

            String username1 = jwtUtil.getUsernameFromToken(token);
            String username2 = jwtUtil.getUsernameFromToken(token);
            String username3 = jwtUtil.getUsernameFromToken(token);

            assertEquals(username1, username2);
            assertEquals(username2, username3);
            assertEquals(TEST_USERNAME, username1);
        }
    }

    @Nested
    @DisplayName("Token 过期测试")
    class ExpirationTests {

        @Test
        @DisplayName("过期测试 - 设置短过期时间后Token过期")
        void expiration_TokenExpires() throws InterruptedException {
            JwtUtil shortExpirationJwtUtil = new JwtUtil();
            ReflectionTestUtils.setField(shortExpirationJwtUtil, "secret", TEST_SECRET);
            ReflectionTestUtils.setField(shortExpirationJwtUtil, "expiration", 1000L);

            String token = shortExpirationJwtUtil.generateToken(TEST_USERNAME);

            assertTrue(shortExpirationJwtUtil.validateToken(token));

            Thread.sleep(1500);

            assertFalse(shortExpirationJwtUtil.validateToken(token));
        }

        @Test
        @DisplayName("过期测试 - 正常过期时间内Token有效")
        void expiration_TokenValidWithinExpiration() {
            String token = jwtUtil.generateToken(TEST_USERNAME);

            boolean isValid = jwtUtil.validateToken(token);

            assertTrue(isValid);
        }
    }

    @Nested
    @DisplayName("边界条件测试")
    class EdgeCaseTests {

        @Test
        @DisplayName("边界条件 - 空字符串用户名")
        void edgeCase_EmptyUsername() {
            String token = jwtUtil.generateToken("");

            String username = jwtUtil.getUsernameFromToken(token);

            assertEquals("", username);
        }

        @Test
        @DisplayName("边界条件 - 超长用户名")
        void edgeCase_VeryLongUsername() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 1000; i++) {
                sb.append("a");
            }
            String longUsername = sb.toString();

            String token = jwtUtil.generateToken(longUsername);
            String extracted = jwtUtil.getUsernameFromToken(token);

            assertEquals(longUsername, extracted);
        }

        @Test
        @DisplayName("边界条件 - Token格式验证（三段式）")
        void edgeCase_TokenFormat() {
            String token = jwtUtil.generateToken(TEST_USERNAME);

            String[] parts = token.split("\\.");

            assertEquals(3, parts.length);
            assertTrue(parts[0].length() > 0);
            assertTrue(parts[1].length() > 0);
            assertTrue(parts[2].length() > 0);
        }
    }
}
