package org.sopt.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration
    ) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String createAccessToken(Long memberId) {
        return createToken(memberId, "access", accessTokenExpiration);
    }

    public String createRefreshToken(Long memberId) {
        return createToken(memberId, "refresh", refreshTokenExpiration);
    }

    private String createToken(Long memberId, String type, long expiration) {
        Date now = new Date();

        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .id(UUID.randomUUID().toString())
                .claim("type", type)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiration))
                .signWith(secretKey)
                .compact();
    }

    public Long getMemberId(String token) {
        return getMemberId(token, "access");
    }

    public Long getRefreshTokenMemberId(String token) {
        return getMemberId(token, "refresh");
    }

    private Long getMemberId(String token, String expectedType) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        if (!expectedType.equals(claims.get("type", String.class))) {
            throw new IllegalArgumentException("토큰 종류가 올바르지 않습니다.");
        }
        return Long.valueOf(claims.getSubject());
    }
}
