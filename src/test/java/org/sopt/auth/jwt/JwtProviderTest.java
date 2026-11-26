package org.sopt.auth.jwt;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtProviderTest {

    private static final String SECRET = Base64.getEncoder().encodeToString(new byte[32]);
    private final JwtProvider provider = new JwtProvider(SECRET, 1800000, 604800000);

    @Test
    void tokensContainMemberIdAndCannotBeUsedAsAnotherType() {
        String access = provider.createAccessToken(1L, "session-1");
        String refresh = provider.createRefreshToken(1L);

        assertEquals(1L, provider.getMemberId(access));
        assertEquals(1L, provider.getRefreshTokenMemberId(refresh));
        assertThrows(IllegalArgumentException.class, () -> provider.getMemberId(refresh));
        assertThrows(IllegalArgumentException.class, () -> provider.getRefreshTokenMemberId(access));
    }

    @Test
    void tokensAreUniqueAndExpiredTokensAreRejected() {
        assertNotEquals(provider.createRefreshToken(1L), provider.createRefreshToken(1L));

        JwtProvider expiredProvider = new JwtProvider(SECRET, -60000, -60000);
        String expired = expiredProvider.createAccessToken(1L, "session-1");
        assertThrows(JwtException.class, () -> provider.getMemberId(expired));
    }
}
