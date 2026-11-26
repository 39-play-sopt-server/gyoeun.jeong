package org.sopt.auth.jwt;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.sopt.auth.repository.RefreshTokenRepository;
import org.sopt.member.domain.Member;
import org.sopt.member.repository.MemberRepository;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {
    private final JwtProvider jwt = new JwtProvider(Base64.getEncoder().encodeToString(new byte[32]), 1800000, 604800000);
    private final RefreshTokenRepository sessions = mock(RefreshTokenRepository.class);
    private final MemberRepository members = mock(MemberRepository.class);
    private final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwt, sessions, members);

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void acceptsOnlyActiveSessionOfActiveMember() throws Exception {
        when(sessions.existsBySessionIdAndMemberId("session-1", 1L)).thenReturn(true, false);
        when(members.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(new Member("이름", "a@b.com", "pw")));
        String token = jwt.createAccessToken(1L, "session-1");

        assertEquals(1L, authenticate(token));
        SecurityContextHolder.clearContext();
        assertNull(authenticate(token));
    }

    @Test
    void propagatesDownstreamExceptionWithoutCallingChainAgain() {
        String token = jwt.createAccessToken(1L, "session-1");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        AtomicInteger calls = new AtomicInteger();

        assertThrows(IllegalArgumentException.class, () -> filter.doFilter(
                request, new MockHttpServletResponse(), (req, res) -> {
                    calls.incrementAndGet();
                    throw new IllegalArgumentException("downstream failure");
                }));
        assertEquals(1, calls.get());
    }

    private Object authenticate(String token) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        AtomicReference<Object> principal = new AtomicReference<>();
        filter.doFilter(request, new MockHttpServletResponse(), (req, res) -> {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) principal.set(authentication.getPrincipal());
        });
        return principal.get();
    }
}
