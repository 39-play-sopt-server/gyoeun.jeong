package org.sopt.auth;

import org.junit.jupiter.api.Test;
import org.sopt.auth.domain.RefreshToken;
import org.sopt.auth.dto.TokenPair;
import org.sopt.auth.exception.InvalidRefreshTokenException;
import org.sopt.auth.jwt.JwtProvider;
import org.sopt.auth.repository.RefreshTokenRepository;
import org.sopt.member.domain.Member;
import org.sopt.member.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RefreshTokenTest {

    private final MemberRepository members = mock(MemberRepository.class);
    private final RefreshTokenRepository tokens = mock(RefreshTokenRepository.class);
    private final JwtProvider jwt = new JwtProvider(
            Base64.getEncoder().encodeToString(new byte[32]), 1800000, 604800000);
    private final AuthService service = new AuthService(members, mock(PasswordEncoder.class), jwt, tokens, 604800000);

    @Test
    void reissueRotatesStoredRefreshToken() {
        String oldToken = jwt.createRefreshToken(1L);
        RefreshToken saved = new RefreshToken("hash", 1L, Instant.now().plusSeconds(60));
        when(tokens.findByHashForUpdate(any())).thenReturn(Optional.of(saved), Optional.empty());
        when(members.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(
                new Member("이름", "user@example.com", "password")));

        TokenPair replacement = service.reissue(oldToken);

        assertEquals(1L, jwt.getMemberId(replacement.accessToken()));
        assertNotEquals(oldToken, replacement.refreshToken());
        verify(tokens).delete(saved);
        verify(tokens).save(any(RefreshToken.class));
        assertThrows(InvalidRefreshTokenException.class, () -> service.reissue(oldToken));
    }

    @Test
    void reissueRejectsMissingOrWrongTypeToken() {
        assertThrows(InvalidRefreshTokenException.class, () -> service.reissue(null));
        assertThrows(InvalidRefreshTokenException.class, () -> service.reissue(jwt.createAccessToken(1L)));
        verifyNoInteractions(tokens);
    }

    @Test
    void reissueRejectsWithdrawnMember() {
        String token = jwt.createRefreshToken(1L);
        when(tokens.findByHashForUpdate(any())).thenReturn(Optional.of(
                new RefreshToken("hash", 1L, Instant.now().plusSeconds(60))));
        when(members.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.empty());

        assertThrows(InvalidRefreshTokenException.class, () -> service.reissue(token));
        verify(tokens, never()).save(any());
    }
}
