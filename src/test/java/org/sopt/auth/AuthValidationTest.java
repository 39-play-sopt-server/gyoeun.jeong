package org.sopt.auth;

import org.junit.jupiter.api.Test;
import org.sopt.auth.exception.InvalidCredentialsException;
import org.sopt.auth.exception.InvalidSignupException;
import org.sopt.auth.jwt.JwtProvider;
import org.sopt.auth.repository.RefreshTokenRepository;
import org.sopt.auth.service.AuthService;
import org.sopt.auth.service.TokenService;
import org.sopt.common.exception.GlobalExceptionHandler;
import org.sopt.member.repository.MemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthValidationTest {
    private final MemberRepository members = mock(MemberRepository.class);
    private final AuthService service = new AuthService(members, mock(PasswordEncoder.class),
            new TokenService(new JwtProvider(Base64.getEncoder().encodeToString(new byte[32]), 1800000, 604800000),
                    mock(RefreshTokenRepository.class), members, 604800000));

    @Test
    void rejectsInvalidSignupBeforeAccessingDatabase() {
        assertThrows(InvalidSignupException.class, () -> service.signup("  ", "a@b.com", "password"));
        assertThrows(InvalidSignupException.class, () -> service.signup("a".repeat(51), "a@b.com", "password"));
        assertThrows(InvalidSignupException.class, () -> service.signup("이름", null, "password"));
        assertThrows(InvalidSignupException.class, () -> service.signup("이름", "a".repeat(256), "password"));
        assertThrows(InvalidSignupException.class, () -> service.signup("이름", "a@b.com", " "));
        assertThrows(InvalidSignupException.class, () -> service.signup("이름", "a@b.com", "가".repeat(25)));
        verifyNoInteractions(members);
    }

    @Test
    void rejectsMissingLoginCredentials() {
        assertThrows(InvalidCredentialsException.class, () -> service.login(null, "password"));
        assertThrows(InvalidCredentialsException.class, () -> service.login("a@b.com", " "));
        verifyNoInteractions(members);
    }

    @Test
    void invalidSignupReturnsBadRequestResponse() {
        InvalidSignupException exception = assertThrows(InvalidSignupException.class,
                () -> service.signup("", "a@b.com", "password"));
        var response = new GlobalExceptionHandler().handleInvalidSignup(exception);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().success());
    }
}
