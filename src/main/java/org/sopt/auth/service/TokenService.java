package org.sopt.auth.service;

import io.jsonwebtoken.JwtException;
import org.sopt.auth.domain.RefreshToken;
import org.sopt.auth.dto.TokenPair;
import org.sopt.auth.exception.InvalidRefreshTokenException;
import org.sopt.auth.jwt.JwtProvider;
import org.sopt.auth.repository.RefreshTokenRepository;
import org.sopt.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

@Service
@Transactional(propagation = Propagation.MANDATORY)
public class TokenService {
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;
    private final long refreshTokenExpiration;

    public TokenService(JwtProvider jwtProvider, RefreshTokenRepository refreshTokenRepository,
                        MemberRepository memberRepository,
                        @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration) {
        this.jwtProvider = jwtProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.memberRepository = memberRepository;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public TokenPair issueForLogin(Long memberId) {
        refreshTokenRepository.deleteByExpiresAtBefore(Instant.now());
        return issue(memberId, UUID.randomUUID().toString());
    }

    public TokenPair reissue(String token) {
        Long memberId;
        try {
            memberId = jwtProvider.getRefreshTokenMemberId(token);
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidRefreshTokenException();
        }

        memberRepository.findActiveForUpdate(memberId).orElseThrow(InvalidRefreshTokenException::new);
        RefreshToken saved = refreshTokenRepository.findByHashForUpdate(hash(token))
                .orElseThrow(InvalidRefreshTokenException::new);
        if (!memberId.equals(saved.getMemberId())) {
            throw new InvalidRefreshTokenException();
        }

        refreshTokenRepository.delete(saved);
        refreshTokenRepository.flush();
        String sessionId = saved.getSessionId() == null ? UUID.randomUUID().toString() : saved.getSessionId();
        return issue(memberId, sessionId);
    }

    public void logout(Long memberId, String sessionId) {
        refreshTokenRepository.findSessionForUpdate(sessionId, memberId)
                .ifPresent(refreshTokenRepository::delete);
    }

    public void revokeAll(Long memberId) {
        refreshTokenRepository.deleteByMemberId(memberId);
    }

    private TokenPair issue(Long memberId, String sessionId) {
        String accessToken = jwtProvider.createAccessToken(memberId, sessionId);
        String refreshToken = jwtProvider.createRefreshToken(memberId);
        refreshTokenRepository.save(new RefreshToken(
                hash(refreshToken), memberId, sessionId, Instant.now().plusMillis(refreshTokenExpiration)));
        return new TokenPair(accessToken, refreshToken);
    }

    private String hash(String token) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256을 사용할 수 없습니다.", e);
        }
    }
}
