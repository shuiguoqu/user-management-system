package com.labelease.usermanagement.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JwtUtil 单元测试
 */
@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    private String testSecret = "thisIsASecretKeyThatIsLongEnoughForTestingPurposes12345";
    private long testExpiration = 3600000;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtil, "secret", testSecret);
        ReflectionTestUtils.setField(jwtUtil, "expiration", testExpiration);
    }

    @Test
    void testGenerateToken() {
        String username = "testuser";

        String token = jwtUtil.generateToken(username);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testGetUsernameFromToken() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        String extractedUsername = jwtUtil.getUsernameFromToken(token);

        assertEquals(username, extractedUsername);
    }

    @Test
    void testValidateToken_ValidToken() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        boolean isValid = jwtUtil.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    void testValidateToken_InvalidToken() {
        String invalidToken = "invalid.token.here";

        boolean isValid = jwtUtil.validateToken(invalidToken);

        assertFalse(isValid);
    }

    @Test
    void testTokenConsistency() {
        String username = "testuser";
        String token1 = jwtUtil.generateToken(username);
        String extractedUsername = jwtUtil.getUsernameFromToken(token1);

        assertEquals(username, extractedUsername);
        assertTrue(jwtUtil.validateToken(token1));
    }

    @Test
    void testDifferentUsersHaveDifferentTokens() {
        String user1 = "user1";
        String user2 = "user2";

        String token1 = jwtUtil.generateToken(user1);
        String token2 = jwtUtil.generateToken(user2);

        assertNotEquals(token1, token2);
        assertEquals(user1, jwtUtil.getUsernameFromToken(token1));
        assertEquals(user2, jwtUtil.getUsernameFromToken(token2));
    }
}
