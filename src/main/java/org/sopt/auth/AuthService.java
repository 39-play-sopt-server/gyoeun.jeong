package org.sopt.auth;

import org.sopt.auth.exception.DuplicateEmailException;
import org.sopt.auth.exception.InvalidCredentialsException;
import org.sopt.auth.exception.InvalidRefreshTokenException;
import org.sopt.auth.domain.RefreshToken;
import org.sopt.auth.dto.TokenPair;
import org.sopt.auth.jwt.JwtProvider;
import org.sopt.auth.repository.RefreshTokenRepository;
import io.jsonwebtoken.JwtException;
import org.sopt.member.domain.Member;
import org.sopt.member.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final long refreshTokenExpiration;

    public AuthService(
            MemberRepository memberRepository,
            PasswordEncoder passwordEncoder,
            JwtProvider jwtProvider,
            RefreshTokenRepository refreshTokenRepository,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration
    ) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    @Transactional
    public Long signup(String name, String email, String password) {
        if (memberRepository.existsByEmail(email)) {
            throw new DuplicateEmailException();
        }

        Member member = new Member(name, email, passwordEncoder.encode(password));
        return memberRepository.save(member).getId();
    }

    @Transactional
    public TokenPair login(String email, String password) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        member = memberRepository.findActiveForUpdate(member.getId())
                .orElseThrow(InvalidCredentialsException::new);

        if (member.isDeleted() || !passwordEncoder.matches(password, member.getPassword())) {
            throw new InvalidCredentialsException();
        }

        refreshTokenRepository.deleteByExpiresAtBefore(Instant.now());
        return issueTokens(member.getId(), UUID.randomUUID().toString());
    }

    @Transactional
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
        return issueTokens(memberId, sessionId);
    }

    @Transactional
    public void logout(Long memberId, String sessionId) {
        refreshTokenRepository.findSessionForUpdate(sessionId, memberId)
                .ifPresent(refreshTokenRepository::delete);
    }

    @Transactional
    public void withdraw(Long memberId) {
        Member member = memberRepository.findActiveForUpdate(memberId)
                .orElseThrow(InvalidCredentialsException::new);
        refreshTokenRepository.deleteByMemberId(memberId);
        member.withdraw();
    }

    private TokenPair issueTokens(Long memberId, String sessionId) {
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
