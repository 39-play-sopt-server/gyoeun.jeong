package org.sopt.auth;

import org.junit.jupiter.api.Test;
import org.sopt.auth.exception.DuplicateEmailException;
import org.sopt.auth.exception.InvalidCredentialsException;
import org.sopt.common.exception.GlobalExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.access.AccessDeniedException;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthResponseTest {

    @Test
    void missingTokenReturnsUnauthorizedApiResponse() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        new ApiAuthenticationEntryPoint(new ObjectMapper()).commence(
                new MockHttpServletRequest(),
                response,
                new InsufficientAuthenticationException("missing token")
        );

        var body = new ObjectMapper().readTree(response.getContentAsString());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
        assertEquals("application/json", response.getContentType().split(";")[0]);
        assertFalse(body.get("success").booleanValue());
        assertEquals("인증이 필요하거나 토큰이 유효하지 않습니다.", body.get("message").asString());
        assertTrue(body.get("data").isNull());
    }

    @Test
    void authServiceErrorsUseApiResponse() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        var duplicate = handler.handleDuplicateEmail(new DuplicateEmailException());
        assertEquals(HttpStatus.CONFLICT, duplicate.getStatusCode());
        assertFalse(duplicate.getBody().success());
        assertEquals("이미 가입된 이메일입니다.", duplicate.getBody().message());

        var invalid = handler.handleInvalidCredentials(new InvalidCredentialsException());
        assertEquals(HttpStatus.UNAUTHORIZED, invalid.getStatusCode());
        assertFalse(invalid.getBody().success());
        assertEquals("이메일 또는 비밀번호가 올바르지 않습니다.", invalid.getBody().message());
    }

    @Test
    void deniedAccessReturnsForbiddenApiResponse() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        new ApiAccessDeniedHandler(new ObjectMapper()).handle(new MockHttpServletRequest(), response,
                new AccessDeniedException("denied"));
        var body = new ObjectMapper().readTree(response.getContentAsString());
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
        assertFalse(body.get("success").booleanValue());
        assertEquals("접근 권한이 없습니다.", body.get("message").asString());
    }
}
